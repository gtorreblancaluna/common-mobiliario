package common.model;

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
}
