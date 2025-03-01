package common.model;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class Cuenta {
    
    private Integer id;
    private String descripcion;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String fgActivo;
    
}
