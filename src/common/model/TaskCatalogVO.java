package common.model;

import common.model.StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog;
import lombok.Data;

@Data
public class TaskCatalogVO {
    
    private String rentaId;
    private String eventFolio;
    private StatusAlmacenTaskCatalog statusAlmacenTaskCatalog;
    private String choferId;
    private String userId;
    
}
