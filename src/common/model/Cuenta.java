package common.model;

import java.sql.Timestamp;

public class Cuenta {
    
    private Integer id;
    private String descripcion;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String fgActivo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFgActivo() {
        return fgActivo;
    }

    public void setFgActivo(String fgActivo) {
        this.fgActivo = fgActivo;
    }

  

   
    
}