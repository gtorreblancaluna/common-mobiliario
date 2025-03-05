
package common.constants;

public class ApplicationConstants {
    
    private ApplicationConstants () {
        throw new IllegalStateException(UTILITY_CLASS);
    }
    
    public static final String PARAMETER_JASPER_DESCRIPCION_ABONOS = "DESCRIPCION_ABONOS";
    public static final String PARAMETER_JASPER_ABONOS = "abonos";
    public static final String PARAMETER_JASPER_DESCRIPCION_DESCUENTO = "DESCRIPCION_DESCUENTO";
    public static final String PARAMETER_JASPER_DESCUENTO = "descuento";
    public static final String PARAMETER_JASPER_DESCRIPCION_IVA = "DESCRIPCION_IVA";
    public static final String PARAMETER_JASPER_IVA = "iva";
    public static final String PARAMETER_JASPER_DESCRIPCION_TOTAL_FALTANTES = "DESCRIPCION_TOTAL_FALTANTES";
    public static final String PARAMETER_JASPER_TOTAL_FALTANTES = "total_faltantes";
    public static final String PARAMETER_JASPER_DESCRIPCION_ENVIO_RECOLECCION = "DESCRIPCION_ENVIO_RECOLECCION";
    public static final String PARAMETER_JASPER_ENVIO_RECOLECCION = "ENVIO_RECOLECCION";
    public static final String PARAMETER_JASPER_DESCRIPCION_DEPOSITO_GARANTIA = "DESCRIPCION_DEPOSITO_GARANTIA";
    public static final String PARAMETER_JASPER_DEPOSITO_GARANTIA = "DEPOSITO_GARANTIA";
    public static final String PARAMETER_JASPER_INFO_SUMMARY_FOLIO = "INFO_SUMMARY_FOLIO";
    public static final String PARAMETER_JASPER_FECHA_RECOLECCION = "FECHA_RECOLECCION";
    public static final String PARAMETER_JASPER_FECHA_EVENTO = "FECHA_EVENTO";
    public static final String PARAMETER_JASPER_TOTAL = "TOTAL";
    public static final String PARAMETER_JASPER_CHOFER = "chofer";
    public static final String PARAMETER_JASPER_MENSAJE_FALTANTES = "mensaje_faltantes";
    public static final String PARAMETER_JASPER_URL_SUB_REPORT_CONSULTA = "URL_SUB_REPORT_CONSULTA";    
    public static final String PARAMETER_JASPER_TELEFONOS_CLIENTE = "TELEFONOS_CLIENTE";
    public static final String PARAMETER_JASPER_FECHA_REGISTRO = "FECHA_REGISTRO";
    public static final String PARAMETER_JASPER_FECHA_ENTREGA = "FECHA_ENTREGA";
    public static final String PARAMETER_JASPER_NOMBRE_EMPRESA = "NOMBRE_EMPRESA";
    
    public static final String DECIMAL_FORMAT = "#,###,###,##0.00" ;
    
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String UTILITY_CLASS = "Utility class";
    public static final String URL_IMAGEN_PARAMETER_JASPER_KEY = "URL_IMAGEN";
    
    public static final String LANGUAJE = "es";
    public static final String COUNTRY = "MX";
    public static final String LANGUAGE_TAG = "es_MX";
    public static final String DATE_FORMAT_LARGE = "EEE dd 'de' MMMM 'del' yyyy";
    public static final String DATE_FORMAT_EX_LARGE = "EEEE dd 'de' MMMM 'del' yyyy";
    
    public static final String SIMPLE_DATE_FORMAT_SHORT = "dd/MM/yyyy";
    public static final String DECIMAL_FORMAT_SHORT = "#,###,###,##0.00";
    public static final String INTEGER_FORMAT = "#,###,###,##0";
    
    public static final String FG_ACTIVE_TRUE = "1";
    
    public static final String FG_ACTIVE_FALSE = "0";
    
    public static final String ARIAL = "Arial";
    
    public static final String EMPTY_STRING = "";
    public static final String ZERO_STRING = "0";
        
    public static final String MESSAGE_GENERATE_TASK_ALMACEN_NO_ACTIVE = 
            "Generar tareas a almacen esta desactivado. Puede activarlo en la ventana de Utilerias.";
    
    public static final String MESSAGE_GENERATE_TASK_CHOFER_NO_ACTIVE = 
            "Generar tareas a chofer esta desactivado. Puede activarlo en la ventana de Utilerias.";
    
    
    // Validations
    public static final int LIMIT_LENGHT_STRING_TO_FILTER = 1_000;
    public static final String LIMIT_LENGHT_EXCEDED = "Limite de caracteres excedido.";
    public static final int LIMIT_GENERATE_PDF = 20;
    public static final int UN_ATTEND_ALMACEN_TASK_TYPE_CATALOG = 1;
    public static final int ATTEND_ALMACEN_TASK_TYPE_CATALOG = 2;
    
    public static final String UN_ATTEND_ALMACEN_TASK_TYPE_CATALOG_DESCRIPTION = "SIN ATENDER";
    public static final String ATTEND_ALMACEN_TASK_TYPE_CATALOG_DESCRIPTION = "ATENDIDO";
    
    public static final String SELECT_A_ROW_TO_GENERATE_REPORT = "Selecciona una fila para generar el reporte...";
    public static final String SELECT_A_ROW_NECCESSARY = "Selecciona una fila para continuar...";
    public static final String ALREADY_AVAILABLE = "La ventana ya se encuentra disponible";
    public static final String ACTION_NOT_PERMITID = "Acción no permitida.";
    public static final String SECRET_KEY = "#yuosSDCG&6729.3";
    
    /** llaves para la tabla configuracion **/
    public static final String SYSTEM_EMAIL_COMPRAS = "email_compras";
    
    /** acentos unicode **/
    
    public static final String ACENTO_A_MINUSCULA = "\u00E1";
    public static final String ACENTO_A_MAYUSCULA = "\u00C1";
    public static final String ACENTO_E_MINUSCULA = "\u00E9";
    public static final String ACENTO_E_MAYUSCULA = "\u00C9";
    public static final String ACENTO_I_MINUSCULA = "\u00ED";
    public static final String ACENTO_I_MAYUSCULA = "\u00CD";
    public static final String ACENTO_O_MINUSCULA = "\u00F3";
    public static final String ACENTO_O_MAYUSCULA = "\u00D3";
    public static final String ACENTO_U_MINUSCULA = "\u00FA";
    public static final String ACENTO_U_MAYUSCULA = "\u00DA";
    
    /** 
     * Tip de orden en el detalle de orden
     */
    public static final String TYPE_DETAIL_ORDER_SHOPPING = "1";
    public static final String TYPE_DETAIL_ORDER_RENTAL = "2";
    
    public static final String DS_TYPE_DETAIL_ORDER_SHOPPING = "1 - compra";
    public static final String DS_TYPE_DETAIL_ORDER_RENTAL = "2 - renta";
    
    /** status detail provider order **/
    public static final String STATUS_ORDER_DETAIL_PROVIDER_PENDING = "1";
    public static final String STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED = "2";
    
    public static final String DS_STATUS_ORDER_DETAIL_PROVIDER_PENDING = "1 - Pendiente";
    public static final String DS_STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED = "2 - Recibido";
    
    /** status provider order **/
    public static final String STATUS_ORDER_PROVIDER_ORDER = "1";
    public static final String STATUS_ORDER_PROVIDER_PENDING = "2";
    public static final String STATUS_ORDER_PROVIDER_CANCELLED = "3";
    public static final String STATUS_ORDER_PROVIDER_FINISH = "4";
    
    public static final String DS_STATUS_ORDER_PROVIDER_ORDER = "1 - Orden";
    public static final String DS_STATUS_ORDER_PROVIDER_PENDING = "2 - Pendiente";
    public static final String DS_STATUS_ORDER_PROVIDER_CANCELLED = "3 - Cancelado";
    public static final String DS_STATUS_ORDER_PROVIDER_FINISH = "4 - Finalizado";
    
    /** Catalogo estados de renta */
    public static final String ESTADO_APARTADO = "1";
    public static final String ESTADO_EN_RENTA = "2";
    public static final String ESTADO_PENDIENTE = "3";
    public static final String ESTADO_CANCELADO = "4";
    public static final String ESTADO_FINALIZADO = "5";
    
    /** Descripcion estados de renta **/
    public static final String DS_ESTADO_APARTADO = "Apartado";
    public static final String DS_ESTADO_EN_RENTA = "En renta";
    public static final String DS_ESTADO_PENDIENTE = "Pendiente";
    public static final String DS_ESTADO_CANCELADO = "Cancelado";
    public static final String DS_ESTADO_FINALIZADO = "Finalizado";
    
    public static final int PUESTO_CHOFER = 1;
    public static final int PUESTO_REPARTIDOR = 2;
    public static final int PUESTO_ADMINISTRADOR = 3;
    public static final int PUESTO_MOSTRADOR = 4;
    
    /** Catalogo tipo de evento */
    public static final String TIPO_PEDIDO = "1";
    public static final String TIPO_COTIZACION = "2";
    public static final String TIPO_FABRICACION = "3";
    
    public static final String DS_TIPO_PEDIDO = "Renta";
    public static final String DS_TIPO_COTIZACION = "Cotización";
    public static final String DS_TIPO_FABRICACION = "Venta";
    
    public static final String LOGO_EMPRESA = "/logo_empresa.jpg";
    
    // Descripcion para la columna de cobranza en CONSULTAR RENTA
    
    public static final String COBRANZA_PAGADO = "Pagado";
    public static final String COBRANZA_PARCIAL_PAGADO = "Parcialmente pagado";
    public static final String COBRANZA_NO_PAGADO = "No pagado";
    
    /* Descripcion para mostrar en la tabla de faltantes */
    public static final String DS_FALTANTES_FALTANTE = "Faltante";
    public static final String DS_FALTANTES_DEVOLUCION = "Devoluci\u00F3n";
    public static final String DS_FALTANTES_REPARACION = "Reparaci\u00F3n";
    public static final String DS_FALTANTES_ACCIDENTE = "Accidente de trabajo";
    
    /* dato inicial para un combo box */
    public static final String CMB_SELECCIONE = "-seleccione-";
    
    /* mensajes para mostrar en los ventanas de avisos */
    public static final String MESSAGE_TITLE_CONFIRM_DELETE = "Eliminar";
    public static final String MESSAGE_SAVE_SUCCESSFUL = "Se ha registrado con \u00E9xito";
    public static final String MESSAGE_UPDATE_SUCCESSFUL = "Se actualiz\u00F3 con \u00E9xito";
    public static final String MESSAGE_DELETE_SUCCESSFUL = "Se elimin\u00F3 con \u00E9xito";
    public static final String MESSAGE_NOT_PARAMETER_RECEIVED = "No se recibi\u00F3 parametro";
    public static final String MESSAGE_NOT_PERMISIONS_ADMIN = "No cuentas con permisos de administrador";
    public static final String MESSAGE_MISSING_PARAMETERS = "Faltan parametros";
    public static final String MESSAGE_UNEXPECTED_ERROR = "Ocurri\u00F3 un error inesperado";
    public static final String MESSAGE_UNEXPECTED_ERROR_CONTACT_SUPPORT = "Ocurri\u00F3 un error inesperado, reinicia el sistema y si persiste el problea contacta a un soporte.";
    public static final String MESSAGE_TITLE_ERROR = "Error";
    public static final String NO_DATA_FOUND_EXCEPTION = "No se obtuvieron registros";
    
    /* mensaje generico */
    public static final String DS_MESSAGE_FAIL_LOGIN = "Contrase\u00F1a incorrecta o usuario no encontrado";
    public static final String TITLE_MESSAGE_FAIL_LOGIN = "Error al inciar sesion";
    
    public static final String MESSAGE_ACTION_DENIED = "Acción denegada, solo un usuario con perfil administrador, puede realizar este proceso";
    
    // ****************************************************************************************
    // nombres de reportes jasper
    public static final String RUTA_REPORTE_ENTREGAS = "/reporteEntrega.jasper";
    public static final String NOMBRE_REPORTE_ENTREGAS = "/reporteEntrega.pdf";
    public static final String NOMBRE_REPORTE_ENTREGAS_SIN_EXT = "/reporteEntrega";
    public static final String RUTA_REPORTE_CONSULTA = "/renta_consulta.jasper";
    public static final String RUTA_REPORTE_CONSULTA_IMAGENES = "/renta_consulta_imgs.jasper";
    public static final String NOMBRE_REPORTE_CONSULTA_IMAGENES = "/reporte_consulta_imgs.pdf";
    public static final String NOMBRE_REPORTE_CONSULTA = "/reporte_consulta.pdf";
    public static final String RUTA_REPORTE_CATEGORIAS = "/reporte_por_categorias.jasper";
    public static final String NOMBRE_REPORTE_CATEGORIAS = "/reporte_por_categorias.pdf";
    public static final String NOMBRE_REPORTE_CATEGORIAS_SIN_EXT = "/reporte_por_categorias";
    public static final String RUTA_REPORTE_NUEVO_PEDIDO = "/renta.jasper";
    public static final String NOMBRE_REPORTE_NUEVO_PEDIDO = "/reporte.pdf";
    public static final String RUTA_LOGO_EMPRESA = "/";
    public static final String URL_SUB_REPORT_CONSULTA = "/";
    public static final String RUTA_REPORTE_ORDEN_PROVEEDOR = "/reporte_proveedor.jasper";
    public static final String NOMBRE_REPORTE_ORDEN_PROVEEDOR = "/reporte_proveedor.pdf";
    public static final String JASPER_REPORT_COLLECTION_MATERIAL = "/reporteRecoleccion.jasper";
    
    public static final String NOMBRE_REPORTE_DETALLE_FOLIOS = "/reporte_detalle_folios.pdf";
    public static final String JASPER_REPORT_DETALLE_FOLIOS = "/detalleFolios.jasper";
    

}
