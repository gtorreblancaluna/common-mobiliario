package common.model;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class DesgloseAlmacenModel {
    
    private Long id;
    private Articulo itemInit;
    private Articulo itemRelation;
    private String comment;
    private Integer amount;
    private String fgActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
}
