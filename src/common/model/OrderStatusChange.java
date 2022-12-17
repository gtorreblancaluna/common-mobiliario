package common.model;

import java.util.Date;
import lombok.Data;

@Data
public class OrderStatusChange {
   
    private Long id;
    private Renta renta;
    private Usuario user;
    private EstadoEvento currentStatus;
    private EstadoEvento changeStatus;
    private Date createdAt;
    private Date updatedAt;
    private String fgActive;    
}
