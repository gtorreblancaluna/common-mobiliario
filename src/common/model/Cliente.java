package common.model;

import java.sql.Timestamp;
import lombok.Data;


@Data
public class Cliente {
    
    private Long id;
    private String nombre;
    private String apellidos;
    private String apodo;
    private String telMovil;
    private String telFijo;
    private String email;
    private String direccion;
    private String localidad;
    private String rfc;
    private String activo;
    private Timestamp birthday;
    private CatalogSocialMediaContactModel socialMedia;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public Cliente (Long id) {
        this.id = id;
    }
    
    public Cliente () {
    }
    
}
