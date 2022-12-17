package common.model;

import lombok.Data;

@Data
public class TipoAbono {
    
    private int tipoAbonoId;
    private String descripcion;
    private char fgActivo;
    private String fechaRegistro;
    private Cuenta cuenta;

    public TipoAbono() {
    }

    public TipoAbono(int tipoAbonoId, String descripcion) {
        this.tipoAbonoId = tipoAbonoId;
        this.descripcion = descripcion;
    }
    

    @Override
    public String toString() {
        return descripcion;
    }
    
}
