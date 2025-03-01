package common.model;

import java.util.Date;
import lombok.Data;

@Data
public class TaskAlmacenVO {
    
    private Long id;
    private Renta renta;
    private StatusAlmacenTaskCatalogVO statusAlmacenTaskCatalogVO;
    private AttendAlmacenTaskTypeCatalogVO attendAlmacenTaskTypeCatalogVO;
    private Date createdAt;
    private Date updatedAt;
    private Usuario userByCategory;
    private String fgActive;
    private Usuario user;
    
}
