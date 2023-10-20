package common.services;

import common.constants.ApplicationConstants;
import common.dao.ItemDAO;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import java.util.List;
import java.util.Map;
import common.model.Articulo;
import common.model.AvailabilityItemResult;
import common.model.CategoriaDTO;
import common.model.Color;

public class ItemService {

    private static final ItemService SINGLE_INSTANCE = null;
    private ItemService(){}
    public static ItemService getInstance() {
      if (SINGLE_INSTANCE == null) {
            return new ItemService();
        }
        return SINGLE_INSTANCE;
    }   
  
    private final ItemDAO itemDao = ItemDAO.getInstance();
    
    public List<AvailabilityItemResult> obtenerDisponibilidadRentaPorConsulta(Map<String, Object> parameters)throws BusinessException {
         
        try {
            List<AvailabilityItemResult> availabilityItemResults = itemDao.obtenerDisponibilidadRentaPorConsulta(parameters);
            for (AvailabilityItemResult availabilityItemResult : availabilityItemResults) {        
              availabilityItemResult.getItem().setUtiles(utilesCalculate(availabilityItemResult.getItem()));  
            }
            return availabilityItemResults;
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
                
    } 
    
    public List<Articulo> obtenerArticulosActivos() {
        List<Articulo> articulos = itemDao.obtenerArticulosActivos();
        for(Articulo articulo : articulos){    
                articulo.setUtiles(utilesCalculate(articulo));
            } // end for articulos
        return articulos;
    }
                         
    public Float utilesCalculate (Articulo articulo) {
    
        return (
                (articulo.getCantidad() != null ? articulo.getCantidad() : 0F)  -
                (articulo.getFaltantes() != null ? articulo.getFaltantes() : 0F) -
                (articulo.getReparacion() != null ? articulo.getReparacion() : 0F ) -
                (articulo.getRentados()!= null ? articulo.getRentados() : 0F ) -
                (articulo.getAccidenteTrabajo() != null ? articulo.getAccidenteTrabajo() : 0F )
            ) +
                    (articulo.getDevolucion() != null ? articulo.getDevolucion() : 0F ) + 
                    (articulo.getTotalCompras() != null ? articulo.getTotalCompras() : 0F  );
    }
    
    public List<Articulo> obtenerArticulosBusquedaInventario(Map<String,Object> map) throws DataOriginException {
        
        
       // neccesary for get total shop from provider
       map.put("statusOrderFinish", ApplicationConstants.STATUS_ORDER_PROVIDER_FINISH);
       map.put("statusOrder", ApplicationConstants.STATUS_ORDER_PROVIDER_ORDER);
       map.put("typeOrderDetail", ApplicationConstants.TYPE_DETAIL_ORDER_SHOPPING);
       
       // necessary to get total sum in rent
       map.put("estado_renta", ApplicationConstants.ESTADO_EN_RENTA);
       map.put("tipo_pedido", ApplicationConstants.TIPO_PEDIDO);
        
        List<Articulo> articulos = itemDao.obtenerArticulosBusquedaInventario(map);
        
        if(!articulos.isEmpty()){
            for(Articulo articulo : articulos){
                articulo.setUtiles(utilesCalculate(articulo));
                articulo.setTotalCompras( (articulo.getTotalCompras() != null ? articulo.getTotalCompras() : 0F));
                articulo.setDevolucion((articulo.getDevolucion() != null ? articulo.getDevolucion() : 0F));
                articulo.setCantidad((articulo.getCantidad() != null ? articulo.getCantidad() : 0F));
                articulo.setFaltantes((articulo.getFaltantes() != null ? articulo.getFaltantes() : 0F));
                articulo.setReparacion((articulo.getReparacion() != null ? articulo.getReparacion() : 0F));
                articulo.setRentados((articulo.getRentados() != null ? articulo.getRentados() : 0F));
                articulo.setAccidenteTrabajo((articulo.getAccidenteTrabajo() != null ? articulo.getAccidenteTrabajo() : 0F));
            } // end for articulos
        }
         
        return articulos;
    }
    
     public CategoriaDTO obtenerCategoriaPorDescripcion(String descripcion){
         return itemDao.obtenerCategoriaPorDescripcion(descripcion);
     }
     
     public Color obtenerColorPorDescripcion(String descripcion){
         return itemDao.obtenerColorPorDescripcion(descripcion);
     }
     
     public void insertarArticulo(Articulo articulo){
        itemDao.insertarArticulo(articulo);
     }
     
     public void actualizarArticulo(Articulo articulo){
        itemDao.actualizarArticulo(articulo);
     }
     
     public Articulo obtenerArticuloPorId(int id){
         return itemDao.obtenerArticuloPorId(id);
     }
     
     public List<Color> obtenerColores(){
         return itemDao.obtenerColores();
     }
    
}
