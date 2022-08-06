package common.model;

public class AttendAlmacenTaskTypeCatalogVO {
    
    private Long id;
    private String description;
    private String fgActive;
    
    public static enum AttendAlmacenTaskTypeCatalog {
        
        UN_ATTENDED(1),
        ATTENDED(2);
            
        private AttendAlmacenTaskTypeCatalog (Integer id) {
            this.id = id;
        }
        
        private final Integer id;

        public Integer getId() {
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
