package common.dao.providers;

import common.exceptions.DataOriginException;
import common.utilities.MyBatisConnectionFactory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import common.model.providers.DetalleOrdenProveedor;
import common.model.providers.OrdenProveedor;
import common.model.providers.DetailOrderProviderType;
import common.model.providers.queryresult.DetailOrderSupplierQueryResult;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import common.model.providers.ParameterOrderProvider;
import common.model.providers.Proveedor;

public class OrderProviderDAO {
    
    private static final Logger log = Logger.getLogger(OrderProviderDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
    
    private OrderProviderDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
   
    private static final OrderProviderDAO SINGLE_INSTANCE = new OrderProviderDAO();
    public static OrderProviderDAO getInstance(){
        return SINGLE_INSTANCE;
    }
    
    public List<Proveedor> getAllProvidersGroupByOrderId(Long orderId)throws DataOriginException{
        SqlSession session = sqlSessionFactory.openSession();
        
        List<Proveedor> list = null;
        
         try{
            list = (List<Proveedor>) session.selectList("MapperPagosProveedores.getAllProvidersGroupByOrderId",orderId);
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
        return list;
    }
    
    public List<DetailOrderSupplierQueryResult> getDetailOrderSupplierCustomize(ParameterOrderProvider parameter)throws DataOriginException{
         
        SqlSession session = sqlSessionFactory.openSession();
        try{
            return session.selectList("MapperOrdenProveedor.getDetailOrderSupplierCustomize",parameter);
        }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
       
     }
    
     public List<OrdenProveedor> getOrdersByRentaId(Integer rentaId)throws DataOriginException{
         List<OrdenProveedor> list = new ArrayList<>();
         SqlSession session = sqlSessionFactory.openSession();
          try{
           list = (List<OrdenProveedor>) session.selectList("MapperOrdenProveedor.getOrderByRentaId",rentaId);   
           
            if(list != null && list.size()>0){
                for(OrdenProveedor ordenProveedor : list){
                     ordenProveedor.setDetalleOrdenProveedorList((List<DetalleOrdenProveedor>) 
                       session.selectList("MapperOrdenProveedor.getDetailOrderByOrderId",ordenProveedor.getId()));
                }
            }
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
                    
        return list;
         
     
     }
     
    public List<DetalleOrdenProveedor> getDetailOrderByOrderId(Long orderId)throws DataOriginException{

        SqlSession session = sqlSessionFactory.openSession();
        try{ 
            return (List<DetalleOrdenProveedor>) session.selectList("MapperOrdenProveedor.getDetailOrderByOrderId",orderId);    
        }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        }finally{
            session.close();
        }         
     
     }
     
      public OrdenProveedor getOrderById(Long id)throws DataOriginException{
         OrdenProveedor orden = new OrdenProveedor();
         SqlSession session = sqlSessionFactory.openSession();
          try{
           orden = (OrdenProveedor) session.selectOne("MapperOrdenProveedor.getOrderById",id);   
           
            if(orden != null ){
                     orden.setDetalleOrdenProveedorList((List<DetalleOrdenProveedor>) 
                       session.selectList("MapperOrdenProveedor.getDetailOrderByOrderId",orden.getId()));
                
            }
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
                    
        return orden;
         
     
     }
     
     public List<OrdenProveedor> getOrdersByParameters(ParameterOrderProvider parameter)throws DataOriginException{
         
        SqlSession session = sqlSessionFactory.openSession();
        try{
            return session.selectList("MapperOrdenProveedor.getOrdersByParameters",parameter);
        }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
       
     }
     
     public void updateOrder(OrdenProveedor orden)throws DataOriginException{
       
        orden.setActualizado(new Timestamp(System.currentTimeMillis()));
        
        SqlSession session = sqlSessionFactory.openSession();
        try{
           session.update("MapperOrdenProveedor.updateOrder",orden);     
           session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
        if (orden.getDetalleOrdenProveedorList() != null && 
                !orden.getDetalleOrdenProveedorList().isEmpty()) {
            for(DetalleOrdenProveedor detail : orden.getDetalleOrdenProveedorList()){
                detail.setCreado(new Timestamp(System.currentTimeMillis()));
                detail.setActualizado(new Timestamp(System.currentTimeMillis()));
                detail.setIdOrdenProveedor(orden.getId());
                saveOrderDetail(detail);
            }
        }
    }
     
     public void updateDetailOrderProvider(DetalleOrdenProveedor detail)throws DataOriginException{
        
        
        SqlSession session = null;
        try{
           session = sqlSessionFactory.openSession();
           session.update("MapperDetalleOrdenProveedor.updateDetailOrderProvider",detail);     
           session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            if (session != null)
                session.close();
        }
        
    }
    
    public void saveOrder(OrdenProveedor orden)throws DataOriginException{
        orden.setCreado(new Timestamp(System.currentTimeMillis()));
        orden.setActualizado(new Timestamp(System.currentTimeMillis()));
        
        SqlSession session = sqlSessionFactory.openSession();
        try{
           session.insert("MapperOrdenProveedor.saveOrder",orden);     
           session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
        if (orden.getDetalleOrdenProveedorList() != null && 
                !orden.getDetalleOrdenProveedorList().isEmpty()) {
            for(DetalleOrdenProveedor detail : orden.getDetalleOrdenProveedorList()){
                detail.setCreado(new Timestamp(System.currentTimeMillis()));
                detail.setActualizado(new Timestamp(System.currentTimeMillis()));
                detail.setIdOrdenProveedor(orden.getId());
                saveOrderDetail(detail);
            }
        }
    }
    
    public void saveOrderDetail(DetalleOrdenProveedor detail)throws DataOriginException{
       
        
        SqlSession session = sqlSessionFactory.openSession();
        try{
           session.insert("MapperOrdenProveedor.saveOrderDetail",detail);     
           session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
    }
    
    public DetalleOrdenProveedor getDetailOrderById(Long id)throws DataOriginException{
        DetalleOrdenProveedor detalle = new DetalleOrdenProveedor();
        SqlSession session = sqlSessionFactory.openSession();
        try{
           detalle = (DetalleOrdenProveedor) 
                   session.selectOne("MapperOrdenProveedor.getDetailOrderById",id);     
           
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
        return detalle;
    }
    
    public void changeStatusDetailOrderById(Long id,String statusToChange)throws DataOriginException{
        
        Map<String,Object> map = new HashMap<>();
        map.put("id", id);
        map.put("statusToChange", statusToChange);
        map.put("dateTimestamp", new Timestamp(System.currentTimeMillis()));
        
        SqlSession session = sqlSessionFactory.openSession();
        try{
            session.update("MapperOrdenProveedor.changeStatusDetailOrderById",map);     
            session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
    }
    
    public List<DetalleOrdenProveedor> getDetailProvider(Map<String,Object>  map)throws DataOriginException{
        
        List<DetalleOrdenProveedor> list;
        SqlSession session = sqlSessionFactory.openSession();
        try{
            list = session.selectList("MapperOrdenProveedor.getDetailProvider",map);     
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
        return list;
        
    }
    
    public List<DetailOrderProviderType> getTypesOrderDetailProvider()throws DataOriginException{
        
       
        SqlSession session = null;
        try{
            session = sqlSessionFactory.openSession();
            return session.selectList("MapperOrdenProveedor.getTypesOrderDetailProvider");     
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            if (session != null)
                session.close();
        }
        
    }
    
    public DetalleOrdenProveedor getDetalleOrdenProveedorById (final Long id)throws DataOriginException{       
       
        SqlSession session = null;
        try{
            session = sqlSessionFactory.openSession();
            return (DetalleOrdenProveedor) session.selectOne("MapperDetalleOrdenProveedor.getDetalleOrdenProveedorById",id);     
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            if (session != null)
                session.close();
        }
        
    }
    
    public void deleteDetailOrdenProveedorById(Long id)throws DataOriginException{
                
        SqlSession session = sqlSessionFactory.openSession();
        
        Map<String,Object> map = new HashMap<>();
        map.put("id", id);
        map.put("dateTimestamp", new Timestamp(System.currentTimeMillis()));
        
        try{
            session.update("MapperOrdenProveedor.deleteDetailOrdenProveedorById",map);     
            session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
    }
    
}