package common.services;

import common.constants.ApplicationConstants;
import common.constants.PropertyConstant;
import common.dao.TaskAlmacenUpdateDAO;
import common.exceptions.DataOriginException;
import common.exceptions.NoDataFoundException;
import common.model.AttendAlmacenTaskTypeCatalogVO;
import common.model.EstadoEvento;
import common.model.Renta;
import common.model.StatusAlmacenTaskCatalogVO;
import common.model.StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog;
import common.model.TaskAlmacenVO;
import common.model.TaskCatalogVO;
import common.model.Tipo;
import common.model.Usuario;
import common.utilities.PropertySystemUtil;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

public class TaskAlmacenUpdateService {
    
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TaskAlmacenUpdateService.class.getName());
    
    private TaskAlmacenUpdateService () {}
    
    private static final TaskAlmacenUpdateService SINGLE_INSTANCE = null;
    private final UserService userService = UserService.getInstance();
    private final TaskAlmacenUpdateDAO taskAlmacenUpdateDAO = TaskAlmacenUpdateDAO.getInstance();
    private static final Logger LOGGER = Logger.getLogger(TaskAlmacenUpdateService.class.getName());
    private final TaskUtilityValidateUpdateService taskUtilityValidateUpdateService = TaskUtilityValidateUpdateService.getInstance();
    
    public static TaskAlmacenUpdateService getInstance(){
        
        if (SINGLE_INSTANCE == null) {
            return new TaskAlmacenUpdateService();
        }
        return SINGLE_INSTANCE;
    }
    
    public String saveWhenEventIsUpdated (
            final EstadoEvento eventStatusChange, 
            final Tipo eventTypeChange,
            final Renta currentRenta,
            final Boolean updateItems,
            final Boolean generalDataUpdated,
            final String userId
    )  throws NoDataFoundException, DataOriginException {
        
        validateIfIsActiveGenerateTask();        
        TaskCatalogVO taskCatalogVO = taskUtilityValidateUpdateService.validateAndBuild(
                eventStatusChange,
                eventTypeChange,
                currentRenta,
                updateItems,
                generalDataUpdated
        );
        taskCatalogVO.setUserId(userId);
        taskCatalogVO.setEventFolio(String.valueOf(currentRenta.getFolio()));
        return save (taskCatalogVO); 
    }
    
    private String save (TaskCatalogVO taskCatalogVO) throws NoDataFoundException, DataOriginException {
        List<Usuario> usersInCategories =
                userService.getUsersInCategoriesAlmacenAndEvent(Integer.parseInt(taskCatalogVO.getRentaId()));
        
        if (usersInCategories == null || usersInCategories.isEmpty()) {
            String message = "No se generó tarea de almacén, por que no se obtuvieron usuarios por categoria";
            LOGGER.info(message);
            throw new NoDataFoundException(message);
        }
        
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        stringBuilder
                .append("[")
                .append(taskCatalogVO.getStatusAlmacenTaskCatalog().getDescription())
                .append(". # ")
                .append(taskCatalogVO.getEventFolio())
                .append("]")
                .append("\n");
        for (Usuario userByCategory : usersInCategories) {
            
            TaskAlmacenVO taskAlmacenVO = new TaskAlmacenVO();
            // renta
            Renta renta = new Renta();
            renta.setRentaId(Integer.parseInt(taskCatalogVO.getRentaId()));
            taskAlmacenVO.setRenta(renta);

            //status
            StatusAlmacenTaskCatalogVO statusAlmacenTaskCatalogVO = new StatusAlmacenTaskCatalogVO();
            statusAlmacenTaskCatalogVO.setId(taskCatalogVO.getStatusAlmacenTaskCatalog().getId());
            taskAlmacenVO.setStatusAlmacenTaskCatalogVO(statusAlmacenTaskCatalogVO);
            
            //user
            Usuario user = new Usuario();
            user.setUsuarioId(Integer.parseInt(taskCatalogVO.getUserId()));
            taskAlmacenVO.setUser(user);

            taskAlmacenVO.setUserByCategory(userByCategory);
            
            // attend
            AttendAlmacenTaskTypeCatalogVO attendAlmacenTaskTypeCatalogVO = new AttendAlmacenTaskTypeCatalogVO();
            attendAlmacenTaskTypeCatalogVO.setId(
                    Long.parseLong(AttendAlmacenTaskTypeCatalogVO.AttendAlmacenTaskTypeCatalog.UN_ATTENDED.getId().toString())
            );
            taskAlmacenVO.setAttendAlmacenTaskTypeCatalogVO(attendAlmacenTaskTypeCatalogVO);
            

            taskAlmacenVO.setCreatedAt(new Date());
            taskAlmacenVO.setUpdatedAt(new Date());
            
            stringBuilder.append("[")
                    .append(++count)
                    .append("]. ")
                    .append("Tarea almacen generada. para el usuario: ")
                    .append(userByCategory.getNombre()).append(" ").append(userByCategory.getApellidos())
                    ;
            stringBuilder.append("\n");
            
            taskAlmacenVO.setFgActive("1");
            taskAlmacenUpdateDAO.save(taskAlmacenVO);
            LOGGER.info(String.format("Se ha generado tarea almacen para el evento id: %s, user by category id: %s",taskCatalogVO.getRentaId(),userByCategory.getUsuarioId()));
            
        }
        
        return stringBuilder.toString();
    }
    
    public String saveWhenIsNewEvent (Long rentaId, String folio, String userId
            ) throws NoDataFoundException, DataOriginException{
               
        validateIfIsActiveGenerateTask();
        TaskCatalogVO taskCatalogVO = new TaskCatalogVO();
        taskCatalogVO.setRentaId(rentaId+"");
        taskCatalogVO.setStatusAlmacenTaskCatalog(StatusAlmacenTaskCatalog.NEW_FOLIO);
        taskCatalogVO.setEventFolio(folio);
        taskCatalogVO.setUserId(userId);
        return save(taskCatalogVO);
    }
    
    private void validateIfIsActiveGenerateTask () throws DataOriginException{
        try {
            if (!Boolean.parseBoolean(PropertySystemUtil.get(PropertyConstant.GENERATE_TASK_ALMACEN))) {
                throw new DataOriginException(ApplicationConstants.MESSAGE_GENERATE_TASK_ALMACEN_NO_ACTIVE);
            }
        } catch (IOException e) {
            log.error(e);
            throw new DataOriginException(e.getMessage(),e);
        }
    }
    
}
