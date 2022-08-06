package common.model;

public class StatusAlmacenTaskCatalogVO {
        
    private Long id;
    private String description;
    private String fgActive;

    public static enum StatusAlmacenTaskCatalog {
        
        NEW_FOLIO(1L,"NUEVO FOLIO"),
        UPDATE_STATUS_FOLIO(2L,"CAMBIO ESTADO FOLIO"),
        UPDATE_TYPE_FOLIO(3L,"CAMBIO TIPO FOLIO"),
        UPDATE_TYPE_AND_STATUS_FOLIO(4L,"CAMBIO TIPO Y ESTADO FOLIO"),
        UPDATE_ITEMS_FOLIO(5L,"CAMBIO ARTICULOS FOLIO");
        
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
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFgActive() {
        return fgActive;
    }

    public void setFgActive(String fgActive) {
        this.fgActive = fgActive;
    }

    @Override
    public String toString() {
        return description;
    }
    
    
}
