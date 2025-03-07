package common.services;

import common.dao.OrderStatusChangeDAO;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import java.util.Date;
import common.model.EstadoEvento;
import common.model.OrderStatusChange;
import common.model.Renta;
import common.model.Usuario;
import org.apache.log4j.Logger;


public class OrderStatusChangeService {
    
    private static Logger log = Logger.getLogger(OrderStatusChangeService.class.getName());
    private final OrderStatusChangeDAO orderStatusChangeDAO;
    private static final OrderStatusChangeService SINGLE_INSTANCE = null;
    
    private OrderStatusChangeService () {
        orderStatusChangeDAO = OrderStatusChangeDAO.getInstance();
    }
    
    public static OrderStatusChangeService getInstance(){
        if (SINGLE_INSTANCE == null) {
            return new OrderStatusChangeService();
        }
        return SINGLE_INSTANCE;
    }
    
    public void insert (Integer rentaId, Integer currentStatusId, Integer changeStatusId, Integer userId) throws BusinessException,DataOriginException {
      
        OrderStatusChange orderStatusChange = new OrderStatusChange();

        orderStatusChange.setRenta(
                new Renta(rentaId)
        );
        orderStatusChange.setCurrentStatus(
                new EstadoEvento(currentStatusId)
        );
        orderStatusChange.setChangeStatus(
                new EstadoEvento(changeStatusId)
        );
        orderStatusChange.setUser(
                new Usuario(userId)
        );
        orderStatusChange.setCreatedAt(new Date());
        orderStatusChange.setUpdatedAt(new Date());
        orderStatusChange.setFgActive("1");
        orderStatusChangeDAO.save(orderStatusChange);
        
    }
}
