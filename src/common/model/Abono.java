package common.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Abono {
    
    private int abonoId;
    private int rentaId;
    private int usuarioId;
    private String fecha;
    private float abono;
    private String comentario;
    private TipoAbono tipoAbono;
    private String fechaPago;
    private Renta renta;
    private Usuario usuario;
    private Float totalAbonos;
}
