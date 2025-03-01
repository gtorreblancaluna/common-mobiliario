package common.model;

import lombok.Data;

@Data
public class Usuario {
    
    private Integer usuarioId;
    private String nombre;
    private String apellidos;
    private String telMovil;
    private String telFijo;
    private String direccion;
    private String administrador;
    private String nivel1;
    private String nivel2;
    private String contrasenia;
    private String activo;
    private Puesto puesto;

    public Usuario(Integer usuarioId, String nombre) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.apellidos = "";
    }
    
    public Usuario(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Usuario() {
    }

    @Override
    public String toString() {
        return nombre + " " + apellidos;
    }
}
