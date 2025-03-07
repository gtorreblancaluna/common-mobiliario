package common.services;

import common.constants.ApplicationConstants;
import common.dao.RentaDAO;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.exceptions.NoDataFoundException;
import common.model.DetalleRenta;
import common.model.EstadoEvento;
import common.model.Renta;
import common.model.Tipo;
import common.model.TipoAbono;
import common.model.Usuario;
import common.utilities.UtilityCommon;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentaService {
    
    private final RentaDAO rentaDao = RentaDAO.getInstance();
    private final TaskAlmacenUpdateService taskAlmacenUpdateService = TaskAlmacenUpdateService.getInstance();
    private final OrderStatusChangeService orderStatusChangeService = OrderStatusChangeService.getInstance();
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RentaService.class.getName());
    private final TaskDeliveryChoferUpdateService taskDeliveryChoferUpdateService = TaskDeliveryChoferUpdateService.getInstance();

    private RentaService() {}

    private static RentaService SINGLE_INSTANCE = null;

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private static synchronized void createInstance() {
        if (SINGLE_INSTANCE == null) { 
            SINGLE_INSTANCE = new RentaService();
        }
    }
    
    public static RentaService getInstance() {
        if (SINGLE_INSTANCE == null){ 
            createInstance();
        }
        return SINGLE_INSTANCE;
    }
    
    public Renta getByFolio (Integer folio) throws DataOriginException, BusinessException {
        return rentaDao.getByFolio(folio);
    }
    
    public Renta getById (Integer id) throws DataOriginException, BusinessException {
        return rentaDao.getById(id);
    }
    
    public List<Renta> getByParameters (Map<String,Object> parameters) throws DataOriginException, BusinessException {
        return rentaDao.getByParameters(parameters);
    }
    
    public List<Renta> getByIds (List<String> ids) throws DataOriginException, BusinessException {
        return rentaDao.getByIds(ids);
    }
    
    public void updateChofer (List<String> idsRenta, Usuario user, Usuario choferToUpdate,
            List<String> listNotifications, javax.swing.JTextArea txArea) throws DataOriginException, BusinessException {
        
        for (String idRenta : idsRenta) {
            
            Map<String,Object> parameters = new HashMap<>();
            parameters.put("choferId",choferToUpdate.getUsuarioId());
            parameters.put("rentaId",idRenta);
            rentaDao.updateChofer(parameters);
        }
        
        new Thread(() -> {
            for (String idRenta : idsRenta) {

                String messageTaskDeliveryChoferUpdateService;
                try {
                    Renta renta = getById(Integer.parseInt(idRenta));
                    
                    taskDeliveryChoferUpdateService.saveWhenIsNewEvent(
                        Long.parseLong(idRenta),
                        renta.getFolio()+"",
                        choferToUpdate.getUsuarioId()+"",
                        user.getUsuarioId().toString()
                    );
                    messageTaskDeliveryChoferUpdateService = String.format("Tarea 'entrega chofer' generada, Folio: %s. chofer: %s",renta.getFolio(),choferToUpdate);
                } catch (Exception e) {
                    messageTaskDeliveryChoferUpdateService = e.getMessage();
                    log.error(messageTaskDeliveryChoferUpdateService);
                }

                UtilityCommon.pushNotification(messageTaskDeliveryChoferUpdateService,listNotifications,txArea);
            }
        }).start();
        
        
    }
    
    public void updateStatusFromApartadoToEnRenta (List<String> ids, Usuario user, 
            List<String> listNotifications, javax.swing.JTextArea txArea) throws DataOriginException, BusinessException {
        if (ids.isEmpty()) {
            throw new BusinessException("No se recibieron parametros");
        }
        if (ids.size() > 20) {
            throw new BusinessException("Limite de operaciones permitidas [20]");
        }
        List<Renta> rentas = getByIds(ids);
        
        if (rentas.isEmpty()) {
            throw new BusinessException("No se obtuvieron eventos");
        }
        
        List<String> message = new ArrayList<>();
        for (Renta renta : rentas) {
            if (!renta.getEstado().getEstadoId().toString().equals(ApplicationConstants.ESTADO_APARTADO)) {
                message.add(String.format("El folio %s tiene estado [%s], debe tener estado [%s]",renta.getFolio(),renta.getEstado().getDescripcion(),ApplicationConstants.DS_ESTADO_APARTADO));
            }
            if (!renta.getTipo().getTipoId().toString().equals(ApplicationConstants.TIPO_PEDIDO)) {
                message.add(String.format("El folio %s tiene tipo [%s], debe tener tipo [%s]",renta.getFolio(),renta.getTipo().getTipo(),ApplicationConstants.DS_TIPO_PEDIDO));
            }
        }
        
        if (!message.isEmpty()) {
            throw new BusinessException(String.join("\n", message));
        }
        
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("ids", ids);
        parameters.put("estadoIdInRent", ApplicationConstants.ESTADO_EN_RENTA);
        
        rentaDao.updateStatusFromApartadoToEnRenta(parameters);
        final EstadoEvento estadoEventoSelected = new EstadoEvento(Integer.parseInt(ApplicationConstants.ESTADO_EN_RENTA), ApplicationConstants.DS_ESTADO_EN_RENTA);
        final Tipo tipoSelected = new Tipo(Integer.parseInt(ApplicationConstants.TIPO_PEDIDO), ApplicationConstants.DS_TIPO_PEDIDO);
        
        new Thread(() -> {
            for (Renta renta : rentas) {
            
                String messageSaveWhenEventIsUpdated;
                try {
                    messageSaveWhenEventIsUpdated = taskAlmacenUpdateService
                        .saveWhenEventIsUpdated(estadoEventoSelected, tipoSelected, renta, false, false, user.getUsuarioId().toString());
                } catch (DataOriginException | NoDataFoundException e) {
                    log.error(e.getMessage(),e);
                    messageSaveWhenEventIsUpdated = e.getMessage();
                }
                UtilityCommon.pushNotification(messageSaveWhenEventIsUpdated,listNotifications,txArea);
            }
        }).start();
        
        new Thread(() -> {
            for (Renta renta : rentas) {
                String msg = String.format("Folio: %s, Usuario: %s,  Realizó el cambio de Estado [%s] a [%s]",
                    renta.getFolio()+"",
                    user.getNombre() + " " + user.getApellidos(),
                    renta.getEstado().getDescripcion(),
                    estadoEventoSelected.getDescripcion()
                );
                try {
                    orderStatusChangeService.insert(renta.getRentaId(), renta.getEstado().getEstadoId() , estadoEventoSelected.getEstadoId(),user.getUsuarioId());
                    log.info(msg);
                } catch (BusinessException | DataOriginException e) {
                    log.error(e.getMessage(),e);
                    msg = e.getMessage();
                }
                UtilityCommon.pushNotification(msg,listNotifications,txArea);
            }
        }).start();
        
        new Thread(() -> {
            for (Renta renta : rentas) {
                String messageTaskDeliveryChoferUpdateService;
                try {
                    taskDeliveryChoferUpdateService.saveWhenEventIsUpdated(
                            estadoEventoSelected, tipoSelected, renta, false, renta.getChofer().getUsuarioId().toString() ,false,
                            user.getUsuarioId().toString()
                    );
                    messageTaskDeliveryChoferUpdateService = String.format("Tarea 'entrega chofer' generada. Folio: %s, chofer: %s",renta.getFolio(),renta.getChofer());
                } catch (DataOriginException | NoDataFoundException e) {
                    messageTaskDeliveryChoferUpdateService = e.getMessage();
                    log.error(messageTaskDeliveryChoferUpdateService);
                }

                UtilityCommon.pushNotification(messageTaskDeliveryChoferUpdateService,listNotifications,txArea);
            }
        }).start();
        
        
    }
    
    public List<DetalleRenta> getDetailByRentId (String rentaId) throws DataOriginException{
        return rentaDao.getDetailByRentId(rentaId);
    }
    
    
    public List<TipoAbono> getTipoAbonos () throws DataOriginException{
        return rentaDao.getTipoAbonos();
    }
    
    public List<Renta> getEventsByNumbersOfWeeks (Integer numbersWeek, Integer userByCategoryId) throws DataOriginException, BusinessException {        
        
        if (numbersWeek == null || numbersWeek >= 3 || numbersWeek <= -3) {
            throw new BusinessException("Numero de semanas no permitidas");
        }
        
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("statusId", Arrays.asList(
                        ApplicationConstants.ESTADO_APARTADO,
                        ApplicationConstants.ESTADO_EN_RENTA
                    ));
        parameters.put("userByCategoryId", userByCategoryId);
        
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String initDeliveryDate;
        String endDeliveryDate;
        
        if (numbersWeek > 0) {
            initDeliveryDate = dateTimeFormatter.format(LocalDate.now().with(DayOfWeek.MONDAY));
            endDeliveryDate = dateTimeFormatter.format(LocalDate.now().plusWeeks(numbersWeek).with(DayOfWeek.MONDAY).minusDays(1));
        } else {
            initDeliveryDate = dateTimeFormatter.format(LocalDate.now().minusWeeks((numbersWeek*-1)).with(DayOfWeek.MONDAY));
            endDeliveryDate = dateTimeFormatter.format(LocalDate.now().minusWeeks(1).with(DayOfWeek.SUNDAY));
        }
        
        parameters.put("initDeliveryDate", initDeliveryDate);
        parameters.put("endDeliveryDate", endDeliveryDate);
        return rentaDao.getEventsBetweenDeliveryDate(parameters);
    }
    
}
