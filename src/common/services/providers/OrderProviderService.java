package common.services.providers;

import common.constants.ApplicationConstants;
import common.dao.providers.OrderProviderDAO;
import common.dao.providers.ProvidersPaymentsDAO;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.exceptions.NoDataFoundException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import common.model.providers.DetalleOrdenProveedor;
import common.model.providers.OrdenProveedor;
import common.model.providers.DetailOrderProviderType;
import common.model.providers.queryresult.DetailOrderSupplierQueryResult;
import common.model.providers.ParameterOrderProvider;
import common.model.providers.Proveedor;


public class OrderProviderService {
    
    // singlenton instance
    private static final OrderProviderService SINGLE_INSTANCE = new OrderProviderService();
    
    private OrderProviderService(){}
    
    public static OrderProviderService getInstance() {
      return SINGLE_INSTANCE;
    }
    
    private final OrderProviderDAO orderProviderDAO = OrderProviderDAO.getInstance();
    private final ProvidersPaymentsDAO providersPaymentsDAO = ProvidersPaymentsDAO.getInstance();
    
    public List<Proveedor> getAllProvidersGroupByOrderId(Long orderId)throws BusinessException{
        try{
            return orderProviderDAO.getAllProvidersGroupByOrderId(orderId);
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
        
    }
    
    public List<DetailOrderSupplierQueryResult> getDetailOrderSupplierCustomize(ParameterOrderProvider parameter)throws BusinessException{
        try {
            List<DetailOrderSupplierQueryResult> detailOrderSupplierCustomizes =
                    orderProviderDAO.getDetailOrderSupplierCustomize(parameter);
            
            detailOrderSupplierCustomizes.stream().parallel()
                    .forEach(t -> t.setTotal(t.getAmount()*t.getPrice()));
            
            if (detailOrderSupplierCustomizes.isEmpty()) {
                throw new NoDataFoundException(ApplicationConstants.NO_DATA_FOUND_EXCEPTION);
            }
            
            return detailOrderSupplierCustomizes;
            
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e.getCause());
        }
    
    }
    
    public String changeStatusDetailOrderById(Long id)throws BusinessException{
        
        String currentStatus;
        
        try{
            DetalleOrdenProveedor detalle 
                    = orderProviderDAO.getDetailOrderById(id);
            
            String statusToChange = null;
            
            if(detalle == null || detalle.getId() == null){
                throw new BusinessException("Error inesperado, no se obtuvo el detalle orden");
            }else{
                if(detalle.getStatus().equals(ApplicationConstants.STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED)){
                    statusToChange = ApplicationConstants.STATUS_ORDER_DETAIL_PROVIDER_PENDING;
                    currentStatus = ApplicationConstants.DS_STATUS_ORDER_DETAIL_PROVIDER_PENDING;
                }else{
                    currentStatus = ApplicationConstants.DS_STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED;
                    statusToChange = ApplicationConstants.STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED;
                }
                
                orderProviderDAO.changeStatusDetailOrderById(id,statusToChange);
            }
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
        
        return currentStatus;
    }
    
    public void saveOrder(OrdenProveedor orden)throws BusinessException{
        try{
            orderProviderDAO.saveOrder(orden);
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
    public void updateOrder(OrdenProveedor orden)throws BusinessException{
        try{
            orderProviderDAO.updateOrder(orden);
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
        
    }
    
    public void updateDetailOrderProvider(
            Long detalleOrdenProveedorId, 
            Float cantidad, 
            Float precio, 
            String comentario, 
            Long detailOrderProviderType, 
            String fgActivo, Long proveedorId)throws BusinessException{
        
        DetalleOrdenProveedor detail = new DetalleOrdenProveedor();
        
        if (detailOrderProviderType != null) {
            DetailOrderProviderType type = 
                    new DetailOrderProviderType(detailOrderProviderType);
            detail.setDetailOrderProviderType(type);
        }
        
        detail.setId(detalleOrdenProveedorId);
        detail.setCantidad(cantidad);
        detail.setPrecio(precio);
        detail.setComentario(comentario);
        detail.setActualizado(new Timestamp(System.currentTimeMillis()));
        detail.setStatus("1");
        detail.setFgActivo(fgActivo);
        detail.setProveedor(new Proveedor(proveedorId));
        
        try{
            orderProviderDAO.updateDetailOrderProvider(detail);
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
        
    }
    
    public OrdenProveedor getOrderById(Long id)throws BusinessException{
        
        OrdenProveedor ordenProveedor;
        try{
            ordenProveedor =  orderProviderDAO.getOrderById(id);
            
            if(ordenProveedor != null){
                ordenProveedor.setPagosProveedor(
                        providersPaymentsDAO.getAllProviderPaymentsByOrderId(ordenProveedor.getId()));
            }
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
        
        return ordenProveedor;
    }
    
    public List<OrdenProveedor> getOrdersByRentaId(Integer rentaId)throws BusinessException{
        try{
            return orderProviderDAO.getOrdersByRentaId(rentaId);
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
    public List<OrdenProveedor> getOrdersByParameters(ParameterOrderProvider parameter)throws BusinessException{

        try{
            List<OrdenProveedor> list = orderProviderDAO.getOrdersByParameters(parameter);
            if (list.isEmpty()) {
                throw new NoDataFoundException(ApplicationConstants.NO_DATA_FOUND_EXCEPTION);
            }
            return list;
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
     
    }
    
    public List<DetalleOrdenProveedor> getDetailProviderByOrderId(Long orderId)throws BusinessException{
        try{
            return orderProviderDAO.getDetailOrderByOrderId(orderId);
        }catch(DataOriginException e){
            throw new BusinessException(e.getMessage());
        }
    }
    
    public List<DetalleOrdenProveedor> getDetailProvider(Map<String,Object> map)throws BusinessException{
        try{
         return orderProviderDAO.getDetailProvider(map);
        }catch(DataOriginException e){
            throw new BusinessException(e.getMessage());
        }
    }
    
    public List<DetailOrderProviderType> getTypesOrderDetailProvider ()throws DataOriginException{
        
         return orderProviderDAO.getTypesOrderDetailProvider();
        
    }
    
    public DetalleOrdenProveedor getDetalleOrdenProveedorById (Long id)throws DataOriginException{
        
         return orderProviderDAO.getDetalleOrdenProveedorById(id);
        
    }
    
    public void deleteDetailOrdenProveedorById (Long id) throws DataOriginException{
        orderProviderDAO.deleteDetailOrdenProveedorById(id);
    }
    
    
}
