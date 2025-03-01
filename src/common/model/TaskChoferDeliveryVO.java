package common.model;

import java.util.Date;
import lombok.Data;

@Data
public class TaskChoferDeliveryVO {
    
    private Long id;
    private Renta renta;
    private StatusAlmacenTaskCatalogVO statusAlmacenTaskCatalogVO;
    private AttendAlmacenTaskTypeCatalogVO attendAlmacenTaskTypeCatalogVO;
    private Date createdAt;
    private Date updatedAt;
    private Usuario chofer;
    private String fgActive;
    private Boolean pendingToPayEvent;
    private Usuario user;   
    
}
