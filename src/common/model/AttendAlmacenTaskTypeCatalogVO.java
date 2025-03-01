package common.model;

import lombok.Data;


@Data
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

    @Override
    public String toString() {
        return description;
    }
    
    
}
