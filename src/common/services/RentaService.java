package common.services;

import common.constants.ApplicationConstants;
import common.dao.RentaDAO;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.exceptions.NoDataFoundException;
import common.model.Abono;
import common.model.DatosGenerales;
import common.model.DetalleRenta;
import common.model.EstadoEvento;
import common.model.Renta;
import common.model.Tipo;
import common.model.TipoAbono;
import common.model.Usuario;
import common.utilities.UtilityCommon;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentaService {
    
    private final RentaDAO rentaDao = RentaDAO.getInstance();
    private final TaskAlmacenUpdateService taskAlmacenUpdateService = TaskAlmacenUpdateService.getInstance();
    private final OrderStatusChangeService orderStatusChangeService = OrderStatusChangeService.getInstance();
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RentaService.class.getName());
    private final TaskDeliveryChoferUpdateService taskDeliveryChoferUpdateService = TaskDeliveryChoferUpdateService.getInstance();
    private final AbonosService abonosService = AbonosService.getInstance();

    private RentaService() {}

    private static RentaService SINGLE_INSTANCE = null;
    private final SystemService systemService = SystemService.getInstance();

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
    
    private String getFechaPedido () {
        
        Calendar fecha = Calendar.getInstance();
        String mes = Integer.toString(fecha.get(Calendar.MONTH) + 1);
        String dia = Integer.toString(fecha.get(Calendar.DATE));
        String auxMes = null, auxDia = null;
        String fechaCreation;

        if (mes.length() == 1) {
            auxMes = "0" + mes;
            fechaCreation = fecha.get(Calendar.DATE) + "/" + auxMes + "/" + fecha.get(Calendar.YEAR);

            if (dia.length() == 1) {
                auxDia = "0" + dia;
                fechaCreation = auxDia + "/" + auxMes + "/" + fecha.get(Calendar.YEAR);

            }

        } else {
            fechaCreation = fecha.get(Calendar.DATE) + "/" + (fecha.get(Calendar.MONTH) + 1) + "/" + fecha.get(Calendar.YEAR);
        }
        return fechaCreation;
    }
    
    private void validateRenta (final Renta renta) throws BusinessException {
                
        UtilityCommon.validateStatusAndTypeEvent(renta.getEstado(),renta.getTipo());
        
        StringBuilder stringBuilder = new StringBuilder();
        int count=0;
        
        if (renta.getDescuento() != null && (renta.getDescuento() < 0 || renta.getDescuento() > 100)) {
            stringBuilder.append(++count).append(". ");
            stringBuilder.append(". ERROR en PORCENTAJE DE DESCUENTO. Ingresa un número entre el 1 y el 100\n");
        }        
        if (!UtilityCommon.validateHour(renta.getHoraEntregaInicial())) {
            stringBuilder.append(++count).append(". ");
            stringBuilder.append("Ingresa hora entrega inicial válida.\n");
        }
        if (!UtilityCommon.validateHour(renta.getHoraEntregaFinal())) {
            stringBuilder.append(++count).append(". ");
            stringBuilder.append("Ingresa hora entrega final válida.\n");
        }
        if (!UtilityCommon.validateHour(renta.getHoraDevolucionInicial())) {
            stringBuilder.append(++count).append(". ");
            stringBuilder.append("Ingresa hora devolución inicial válida.\n");
        }
        if (!UtilityCommon.validateHour(renta.getHoraDevolucionFinal())) {
            stringBuilder.append(++count).append(". ");
            stringBuilder.append("Ingresa hora devolución final válida.\n");
        }
        if (renta.getFechaEntregaDate() == null) {
            stringBuilder.append(++count).append(". ");
            stringBuilder.append("Fecha de entrega es requerida.\n");
        }
        if (renta.getFechaDevolucionDate() == null) {
            stringBuilder.append(++count).append(". ");
            stringBuilder.append("Fecha de devolución es requerida.\n");
        }
        if (renta.getFechaEventoDate() == null) {
            stringBuilder.append("Fecha de evento es requerida.\n");
        }
        if (renta.getChofer() == null || renta.getChofer().getUsuarioId() == 0) {
            stringBuilder.append(++count).append(". ");
            stringBuilder.append("Chofer es requerido.\n");
        }
        if (renta.getDescripcion() == null || renta.getDescripcion().isEmpty()) {
            stringBuilder.append("Dirección es requerido.\n");
        }
        if (renta.getDescripcion() != null 
                && !renta.getDescripcion().isEmpty()
                && renta.getDescripcion().length() > 400) {
            stringBuilder.append("Dirección a rebasado los caracteres permitidos [400 caracteres].\n");
        }
        if (renta.getComentario()!= null 
                && !renta.getComentario().isEmpty()
                && renta.getComentario().length() > 500) {
            stringBuilder.append(++count).append(". ");
            stringBuilder.append("Comentario a rebasado los caracteres permitidos [500 caracteres].\n");
        }
        
        if (renta.getDetalleRenta() == null || renta.getDetalleRenta().size() <= 0) {
            stringBuilder.append(++count).append(". ");
            stringBuilder.append("Ingresa al menos un artículo a la renta.\n");
        }
        if (renta.getUsuario() == null) {
            stringBuilder.append(++count).append(". ");
            stringBuilder.append("Falta usuario que registro la renta.\n");
        }
        

        
        if (!stringBuilder.toString().isEmpty()) {
            throw new BusinessException(stringBuilder.toString());
        }
        
    }
    
    private void setupBeforeSaveOrUpdate (Renta renta) {
        
        renta.setHoraEntrega(renta.getHoraEntregaInicial()+" a "+renta.getHoraEntregaFinal());
        
        renta.setHoraDevolucion(renta.getHoraDevolucionInicial()+" a "+renta.getHoraDevolucionFinal());
        
        renta.setFechaEvento(
                new SimpleDateFormat(ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT).format(renta.getFechaEventoDate()));
        
        renta.setFechaEntrega(
                new SimpleDateFormat(ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT).format(renta.getFechaEntregaDate()));
        
        renta.setFechaDevolucion(
                new SimpleDateFormat(ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT).format(renta.getFechaDevolucionDate()));
        
        renta.setStock("1");
        
        renta.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        if(renta.getRentaId() <= 0) {
            // para obtener el folio
            DatosGenerales datosGenerales = systemService.getGeneralData();
            renta.setFolio(datosGenerales.getFolio() + 1);
            // nuevo pedido.
            renta.setFechaPedido(getFechaPedido());
            renta.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        

        
    }
    
    private void generateTasks (final Renta renta,
            List<String> listNotifications, 
            javax.swing.JTextArea txtAreaNotifications) {
        
        if (renta.getTipo().getTipoId().toString().equals(ApplicationConstants.TIPO_PEDIDO) 
                || renta.getTipo().getTipoId().toString().equals(ApplicationConstants.TIPO_FABRICACION)) {
            
            new Thread(() -> {
                String message;

                try {
                    message = taskAlmacenUpdateService.saveWhenIsNewEvent(Long.valueOf(renta.getRentaId()), 
                            renta.getFolio()+"", 
                            renta.getUsuario().getUsuarioId().toString()
                    );
                    
                    log.info(message);
                } catch (DataOriginException | NoDataFoundException e) {
                    message = e.getMessage();
                    log.error(message);
                }
                
                UtilityCommon.pushNotification(message, listNotifications, txtAreaNotifications);

                
            }).start();

            new Thread(() -> {
                String message;                

                try {

                    taskDeliveryChoferUpdateService.saveWhenIsNewEvent(
                            Long.valueOf(renta.getRentaId()), 
                            renta.getFolio()+"",renta.getChofer().getUsuarioId().toString(), 
                            renta.getUsuario().getUsuarioId().toString()
                    );
                    message = String.format("Tarea 'entrega chofer' generada. Folio: %s, chofer: %s",renta.getFolio(),renta.getChofer());

                    
                } catch (DataOriginException | NoDataFoundException e) {
                    message = e.getMessage();
                    log.error(message);
                }
                
                UtilityCommon.pushNotification(message, listNotifications, txtAreaNotifications);
                
            }).start();
        } else {
            String message = 
                    String.format("No se genero tarea para el folio: %s. Por que el folio "
                            + "es de tipo: [%s] ....NOTA: Para poder generar tarea"
                            + " debe de ser de tipo "
                            + "[%s] o [%s]",
                            renta.getFolio(),
                            renta.getTipo().getTipo(), 
                            ApplicationConstants.DS_TIPO_PEDIDO, 
                            ApplicationConstants.DS_TIPO_FABRICACION);
            
            UtilityCommon.pushNotification(message, listNotifications, txtAreaNotifications);
        }
    }
    
    public void saveOrUpdate (Renta renta, 
            List<String> listNotifications, 
            javax.swing.JTextArea txtAreaNotifications) throws BusinessException {
        
        validateRenta(renta);
        
        setupBeforeSaveOrUpdate(renta);
        
        rentaDao.saveOrUpdate(renta);
        
        // update folio
        systemService.updateFolio(renta.getFolio()+"");

        
        // guardar el detalle, una vez guardada la renta
        for (DetalleRenta detalleRenta : renta.getDetalleRenta()) {
            detalleRenta.setRentaId(renta.getRentaId());
        }
        saveDetalleRenta(renta.getDetalleRenta());
        
        // guardar abonos
        if (!renta.getAbonos().isEmpty()) {
            for (Abono abono : renta.getAbonos()) {
                abono.setUsuario(renta.getUsuario());
                abono.setRenta(renta);
                abono.setFecha(
                        new SimpleDateFormat(ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT).format(new Date())
                );
            }
        }
        abonosService.save(renta.getAbonos());
        
        generateTasks(renta,listNotifications,txtAreaNotifications);
        
        String messageSuccess = renta.getUsuario().getNombre()+ " registró un evento de tipo "+renta.getTipo().getTipo()
                +" con status ["+renta.getEstado().getDescripcion()+ "], FOLIO: ["+renta.getFolio()+"]";
            UtilityCommon.pushNotification(messageSuccess,listNotifications,txtAreaNotifications);
            log.info(messageSuccess);
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
    
    public void saveDetalleRenta (List<DetalleRenta> detalles) throws BusinessException{
        rentaDao.saveDetalleRenta(detalles);
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
