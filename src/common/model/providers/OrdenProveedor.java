package common.model.providers;

import common.constants.ApplicationConstants;
import common.exceptions.BusinessException;
import java.sql.Timestamp;
import java.util.List;
import common.model.Renta;
import common.model.Usuario;
import lombok.Data;

@Data
public class OrdenProveedor {
    
    private Long id;
    private Renta renta;
    private Usuario usuario;
    private Proveedor proveedor;
    private String fgActivo;
    private String status;
    private String statusDescription;
    private Timestamp creado;
    private Timestamp actualizado;
    private List<DetalleOrdenProveedor> detalleOrdenProveedorList;
    private List<PagosProveedor> pagosProveedor;
    private String comentario;
    // estas variables son para calcular y mostrar en la vista
    private Float abonos;
    private Float total;

    
    public OrdenProveedor copy() {
        OrdenProveedor order = new OrdenProveedor();
        order.setId(this.id);
        order.setRenta(this.renta);
        order.setUsuario(this.usuario);
        order.setProveedor(this.proveedor);
        order.setFgActivo(this.fgActivo);
        order.setStatus(this.status);
        order.setStatusDescription(this.statusDescription);
        order.setCreado(this.creado);
        order.setActualizado(this.actualizado);
        order.setDetalleOrdenProveedorList(this.detalleOrdenProveedorList);
        order.setPagosProveedor(this.pagosProveedor);
        order.setComentario(this.comentario);
        order.setAbonos(this.abonos);
        order.setTotal(this.total);
	return order;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRenta(Renta renta) {
        this.renta = renta;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public void setFgActivo(String fgActivo) {
        this.fgActivo = fgActivo;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public void setCreado(Timestamp creado) {
        this.creado = creado;
    }

    public void setActualizado(Timestamp actualizado) {
        this.actualizado = actualizado;
    }

    public void setDetalleOrdenProveedorList(List<DetalleOrdenProveedor> detalleOrdenProveedorList) {
        this.detalleOrdenProveedorList = detalleOrdenProveedorList;
    }

    public void setPagosProveedor(List<PagosProveedor> pagosProveedor) {
        this.pagosProveedor = pagosProveedor;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public void setAbonos(Float abonos) {
        this.abonos = abonos;
    }

    public void setTotal(Float total) {
        this.total = total;
    }
    
    
    

    public void setStatus(String status) {
        
        switch(status){
            case ApplicationConstants.STATUS_ORDER_PROVIDER_ORDER:
                this.setStatusDescription(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_ORDER);
                break;
            case ApplicationConstants.STATUS_ORDER_PROVIDER_PENDING:
                this.setStatusDescription(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_PENDING);
                break;
            case ApplicationConstants.STATUS_ORDER_PROVIDER_CANCELLED:
                this.setStatusDescription(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_CANCELLED);
                break;
            case ApplicationConstants.STATUS_ORDER_PROVIDER_FINISH:
                this.setStatusDescription(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_FINISH);
                break;
        }
        
      this.status = status;
       
    }
    
    
    public String getStatusFromDescription(String description)throws BusinessException{
        String status=null;
        switch(description){
            case ApplicationConstants.DS_STATUS_ORDER_PROVIDER_ORDER:
                status = ApplicationConstants.STATUS_ORDER_PROVIDER_ORDER;
                break;
            case ApplicationConstants.DS_STATUS_ORDER_PROVIDER_PENDING:
                status = ApplicationConstants.STATUS_ORDER_PROVIDER_PENDING;
                break;
            case ApplicationConstants.DS_STATUS_ORDER_PROVIDER_CANCELLED:
                status = ApplicationConstants.STATUS_ORDER_PROVIDER_CANCELLED;
                break;
            case ApplicationConstants.DS_STATUS_ORDER_PROVIDER_FINISH:
                status = ApplicationConstants.STATUS_ORDER_PROVIDER_FINISH;
                break;
            default:
                throw new BusinessException("No econtramos el tipo de descripción para el estatus de orden proveedor >>> ["+description+"]");
                
        }
        
        return status;
    }
    
    
}
