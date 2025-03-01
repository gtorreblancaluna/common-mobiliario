package common.model;

import lombok.Data;

@Data
public class StatusAlmacenTaskCatalogVO {
        
    private Long id;
    private String description;
    private String fgActive;

    public static enum StatusAlmacenTaskCatalog {
        
        NEW_FOLIO(1L,"NUEVO FOLIO"),
        UPDATE_STATUS_FOLIO(2L,"CAMBIO ESTADO FOLIO"),
        UPDATE_TYPE_FOLIO(3L,"CAMBIO TIPO FOLIO"),
        UPDATE_TYPE_AND_STATUS_FOLIO(4L,"CAMBIO TIPO Y ESTADO FOLIO"),
        UPDATE_ITEMS_FOLIO(5L,"CAMBIO ARTICULOS FOLIO"),
        GENERAL_DATA_UPDATED(6L,"CAMBIO DATOS GENERALES");
        
        private final Long id;
        private final String description;
        
        private StatusAlmacenTaskCatalog (Long id, String description) {
            this.id = id;
            this.description = description;
        }
        
        public String getDescription () {
            return description;
        }

        public Long getId() {
            return id;
        }
        
    }

    @Override
    public String toString() {
        return description;
    }
    
    
}
