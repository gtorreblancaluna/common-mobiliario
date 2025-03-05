package common.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class Renta {
    
    private int rentaId;
    private int estadoId;
    private Cliente cliente;
    private Usuario usuario;
    private String fechaPedido;
    private String fechaEntrega;
    private String horaEntrega;
    private String fechaDevolucion;
    private String descripcion;
    private Float descuento;
    private float cantidadDescuento;
    private Float iva;
    private String comentario;
    private int usuarioChoferId;
    private int folio;
    private String stock;
    private Tipo tipo;
    private Float totalAbonos;
    private List<DetalleRenta> detalleRenta;
    private List<Abono> abonos;
    private String horaDevolucion;
    private String fechaEvento;
    private Float depositoGarantia;
    private Float envioRecoleccion;
    private Usuario chofer;
    private EstadoEvento estado;
    private String mostrarPreciosPdf;
    private String descripcionCobranza;
    private float subTotal;
    // nos servira para almacenar el total y mostrarlo en la consulta
    private float total;
    // almacena el total de faltantes
    private float totalFaltantes;
    // dato del faltante por cubrir
    private float totalFaltantesPorCubrir;
    
    private Float calculoDescuento;
    private Float calculoIVA;
    private Float totalCalculo;
    // este mensaje se muestra en el PDF
    private String mensajeFaltantes;
    // sumatoria del total de ordenes por proveedor
    private Float totalOrdersProvider;
    // Solo para validaciones
    private String horaEntregaInicial;
    private String horaEntregaFinal;
    private String horaDevolucionInicial;
    private String horaDevolucionFinal;
    // Solo para validaciones
    private Date fechaEntregaDate;
    // Solo para validaciones
    private Date fechaDevolucionDate;
    // Solo para validaciones
    private Date fechaEventoDate;
    
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Renta(int rentaId) {
        this.rentaId = rentaId;
    }

    public Renta() {
    }
    
    
    
}
