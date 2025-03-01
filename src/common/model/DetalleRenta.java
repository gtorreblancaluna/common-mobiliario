package common.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DetalleRenta {
    private int detalleRentaId;
    private int rentaId;
    private float cantidad;
    private Articulo articulo;
    private float precioUnitario;
    private String comentario;
    private String seDesconto;
    private float porcentajeDescuento;
    
}
