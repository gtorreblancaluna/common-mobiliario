package common.model;

import lombok.Data;

@Data
public class EstadoEvento {
    
    private Integer estadoId;
    private String descripcion;

    public EstadoEvento(Integer estadoId) {
        this.estadoId = estadoId;
    }

    public EstadoEvento(Integer estadoId, String descripcion) {
        this.estadoId = estadoId;
        this.descripcion = descripcion;
    }

    public EstadoEvento() {
    }

    
    @Override
    public String toString() {
        return descripcion;
    }
    
    
}
