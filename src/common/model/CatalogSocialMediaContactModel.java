package common.model;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class CatalogSocialMediaContactModel {
    
    private Long id;
    private String description;
    private String fgActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public CatalogSocialMediaContactModel () {}
    
    public CatalogSocialMediaContactModel (Long id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
    
    
}
