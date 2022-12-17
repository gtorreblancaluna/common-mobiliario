package common.model;

import lombok.Data;

@Data
public class Tipo {
    private Integer tipoId;
    private String tipo;

    public Tipo(int tipoId, String tipo) {
        this.tipoId = tipoId;
        this.tipo = tipo;
    }
    
    public Tipo(int tipoId) {
        this.tipoId = tipoId;
    }

    public Tipo() {
    }

    @Override
    public String toString() {
        return tipo;
    }
    
}
