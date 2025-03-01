package common.model;

import java.io.Serializable;
import java.sql.Timestamp;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Articulo implements Serializable{
    
    private Integer articuloId;
    private int categoriaId;
    private int usuarioId;
    private Color color;
    private Float cantidad;
    private String descripcion;
    private String fechaIngreso;
    private float precioCompra;
    private float precioRenta;
    private String activo;
    private float stock;
    private String codigo;
    private float enRenta;
    private CategoriaDTO categoria;    
    private Float rentados;
    private Float faltantes;
    private Float reparacion;
    private Float accidenteTrabajo;
    private Float devolucion;
    // dato para actualizar la ultima fecha de modificacion
    private Timestamp fechaUltimaModificacion;
    // estos datos solo son para realizar calculos en la vista
    private Float totalCompras;
    private Float totalShopProvider;
    private Float utiles;
    private byte[] image;
    
}
