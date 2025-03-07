package common.model.providers;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class Proveedor {
    
    private Long id;
    private String nombre;
    private String apellidos;
    private String direccion;
    private String telefonos;
    private String email;
    private String fgActivo;
    private Timestamp creado;
    private Timestamp actualizado;

    public Proveedor() {
    }
    
    public Proveedor(Long id, String nombre) {
        this.nombre = nombre;
        this.apellidos = "";
        this.id = id;
    }

    public Proveedor(Long id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return nombre + " " + apellidos;
    }
    
}
