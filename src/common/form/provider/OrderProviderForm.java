package common.form.provider;

import common.constants.ApplicationConstants;
import common.utilities.UtilityCommon;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.services.UtilityService;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import common.model.Articulo;
import common.model.DatosGenerales;
import common.model.DetalleRenta;
import common.model.Renta;
import common.model.Usuario;
import common.model.providers.DetalleOrdenProveedor;
import common.model.providers.OrdenProveedor;
import common.model.providers.PagosProveedor;
import common.model.providers.Proveedor;
import common.model.providers.DetailOrderProviderType;
import common.services.RentaService;
import common.services.providers.OrderProviderService;
import common.services.providers.ProvidersPaymentsService;
import common.services.providers.ProvidersService;
import common.utilities.JasperPrintUtility;
import java.awt.Dimension;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;

public class OrderProviderForm extends javax.swing.JInternalFrame {
       
    private final RentaService rentaService;
    private final UtilityService utilityService = UtilityService.getInstance();
    private final OrderProviderService orderProviderService = OrderProviderService.getInstance();
    private final ProvidersService providersService = ProvidersService.getInstance();
    private final ProvidersPaymentsService providersPaymentsService = ProvidersPaymentsService.getInstance();
    public static String g_articuloId;
    public static String rentaId;
    public static String g_cantidadEnPedido;
    private static final DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.00" );
    protected OrdenProveedor ordenProveedor = null;
    private PaymentsProvidersDialog paymentsProvidersForm;    
    List<Proveedor> providers = new ArrayList<>();
    private final long PROVIDER_DEFAULT = 1L;
    private final String CREATED_AT_EMPTY = "";
    private final String UPDATED_AT_EMPTY = "";
    
    private final int DELAY_SECONDS = 7_000;
    
    /** Encabezados de la tabla ARTICULOS ORDEN PROVEEDOR */
    public final static int HD_ORDEN_PROVEEDOR_ID_ORDEN = 0;
    public final static int HD_ORDEN_PROVEEDOR_ID_ARTICULO = 1;
    public final static int HD_ORDEN_PROVEEDOR_DESCRIPCION_ARTICULO = 2;
    public final static int HD_ORDEN_PROVEEDOR_CANTIDAD = 3;
    public final static int HD_ORDEN_PROVEEDOR_PRECIO = 4;
    public final static int HD_ORDEN_PROVEEDOR_IMPORTE = 5;
    public final static int HD_ORDEN_PROVEEDOR_COMENTARIO = 6;
    public final static int HD_ORDEN_PROVEEDOR_TIPO_ORDEN_ID = 7;
    public final static int HD_ORDEN_PROVEEDOR_TIPO_ORDEN = 8;
    public final static int HD_ORDEN_PROVEEDOR_CREADO = 9;
    public final static int HD_ORDEN_PROVEEDOR_ACTUALIZADO = 10;
    public final static int HD_ORDEN_PROVEEDOR_STATUS = 11;
    public final static int HD_ORDEN_PROVEEDOR_PROVEEDOR_ID = 12;
    public final static int HD_ORDEN_PROVEEDOR_PROVEEDOR_NAME = 13;
   
    
    /** Encabezados de la tabla ARTICULOS */
    public final static int HD_ARTICULOS_ID_ARTICULO = 0;
    public final static int HD_ARTICULOS_CANTIDAD_PEDIDO = 1;
    public final static int HD_ARTICULOS_DESCRIPCION_ARTICULO = 2;
    public final static int HD_ARTICULOS_PRECIO_COBRAR = 3;
    
    public final static String UPDATE_ORDER = "update order";
    public final static String NEW_ORDER = "new order";
    private String orderId = "";
    private String folio = "";
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(OrderProviderForm.class.getName());
    private String idDetailOrderProviderToEdit = null;
    private Usuario globalUser;
    private DatosGenerales datosGenerales;
    private String messageReturn = null;
    private final String pathLocationFromInvoke;
    
   public OrderProviderForm(String folio, String orderId, String rentaId, Usuario globalUser, DatosGenerales datosGenerales,
           final String pathLocationFromInvoke, JDesktopPane jDesktopPane) {
        initComponents();
        
        // center jinternal frame
        Dimension desktopSize = jDesktopPane.getSize();
        Dimension jInternalFrameSize = this.getSize();
        this.setLocation((desktopSize.width - jInternalFrameSize.width)/2,
            (desktopSize.height- jInternalFrameSize.height)/2);
        
        lblInformacionInicial.setText(ApplicationConstants.EMPTY_STRING);
        this.datosGenerales = datosGenerales;
        this.globalUser = globalUser;
        this.orderId = orderId;
        this.folio = folio;
        this.rentaId = rentaId;
        rentaService = RentaService.getInstance();
        this.lblQuitarElemento.setText("");
        this.setTitle("Agregar orden al proveedor ");
        setResizable(true);
        resetCmbOrderStatus();
        formato_tabla_orden();
        formato_tabla_articulos();      
        llenar_tabla_articulos();
        resetInputBoxes();
        fillOrderTypeDetail();
        this.pathLocationFromInvoke = pathLocationFromInvoke;
        
        this.setClosable(true);
        txtSubTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtPagos.setHorizontalAlignment(JTextField.RIGHT);
        fillCmbProviders();
        
        setMaximizable(true);
        this.lblQuitarElemento.setText(ApplicationConstants.EMPTY_STRING);
        lblInfoExtra.setText(ApplicationConstants.EMPTY_STRING);

    }
   
    public String showDialog () {
        setVisible(true);
        return messageReturn;
    }
   
   
   public void reportPDF(String orderProviderId){
     
        if (orderProviderId == null || orderProviderId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se obtuvo la orden del proveedor");
            return;
        }
       
        try {
            JasperPrintUtility.generatePDFOrderProvider(orderId,datosGenerales, 
                    pathLocationFromInvoke);
            
       } catch (Exception e) {
           LOGGER.error(e);
           JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
       }
     
     }
    
   public void saveOrderProvider(){
       
       StringBuilder message = new StringBuilder();
       if(rentaId == null || rentaId.equals("")){
           message.append("No se obtuvo el folio de la renta, recarga la ventana nuevamente :(\n");
       }
       if(jTableOrderProvider.getRowCount() == 0){
           message.append("No existen árticulos para guardar.\n");
       }
       if(this.cmbStatusOrder.getSelectedItem().toString()
               .equals(ApplicationConstants.CMB_SELECCIONE)){
           message.append("Seleccione un status válido.\n");
       }
       if(dateDateInBodega.getDate() == null) {
           message.append("Fecha en bodega es requerido. P)\n");
       }
       
       if(!message.toString().isEmpty()){
           JOptionPane.showMessageDialog(this, message+"", 
                   ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
           return;
       }
       
      List<DetalleOrdenProveedor> list = new ArrayList<>();
      OrdenProveedor orden = new OrdenProveedor();
      
      Articulo articulo;
      DetalleOrdenProveedor detail;
      
      for (int i = 0; i < jTableOrderProvider.getRowCount(); i++) {
           detail = new DetalleOrdenProveedor();
           articulo = new Articulo();
           
           if(!jTableOrderProvider.getValueAt(i, 0).toString().equals("0")){
               // viene de actualizar lo ignoramos
               continue;
           }
           String articuloId = 
                   jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_ID_ARTICULO).toString();
           String cantidad = 
                   jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_CANTIDAD).toString();
           String precio = 
                   jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_PRECIO).toString();
           
           String comentario = 
                   jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_COMENTARIO).toString();
           
            String tipo = 
                   jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_TIPO_ORDEN).toString();
            
            String proveedorId = 
                   jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_PROVEEDOR_ID).toString();
            
           detail.setProveedor(new Proveedor(Long.parseLong(proveedorId)));
           
           articulo.setArticuloId(Integer.parseInt(articuloId));
           
           detail.setArticulo(articulo);
           detail.setCantidad(Float.parseFloat(UtilityCommon.deleteCharacters(cantidad,"$,")));
           detail.setPrecio(Float.parseFloat(UtilityCommon.deleteCharacters(precio,"$,")));
           
           detail.setDetailOrderProviderType(
                   new DetailOrderProviderType(Long.parseLong(jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_TIPO_ORDEN_ID).toString()))
           );
           detail.setComentario(comentario);
           
           list.add(detail);
           
      } // end for
      
      orden.setDetalleOrdenProveedorList(list);
      Renta renta = new Renta();
      renta.setRentaId(Integer.parseInt(rentaId));
      orden.setRenta(renta);
      orden.setUsuario(globalUser);
      Proveedor proveedor = new Proveedor();
      proveedor.setId(PROVIDER_DEFAULT);
      orden.setProveedor(proveedor);
      orden.setStatus(ApplicationConstants.STATUS_ORDER_PROVIDER_ORDER);
      orden.setComentario(txtCommentOrder.getText());
      if (dateDateInBodega.getDate() != null) {
          Timestamp dateInBodega = new Timestamp(dateDateInBodega.getDate().getTime());
          orden.setFechaEnBodega(dateInBodega);
      }
      
        
      try{
            if(orderId !=null && !orderId.isEmpty()){
                orden.setId(Long.parseLong(orderId));
                orden.setStatus(orden.getStatusFromDescription(cmbStatusOrder.getSelectedItem().toString()));
                orderProviderService.updateOrder(orden);
            }else{
                orderProviderService.saveOrder(orden);
            }
        }catch(BusinessException e){
              JOptionPane.showMessageDialog(this, 
                      e.getMessage()+"\n"+e.getCause(), 
                      ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }
      orderId = String.valueOf(orden.getId());
      llenar_tabla_articulos();
      lblInfoExtra.setText("Se guardo con éxito.");
      Toolkit.getDefaultToolkit().beep();
      
   }
    
    public void showProviders() {
        ViewProviderForm form = new ViewProviderForm(null,true);
        form.setVisible(true);
    }
    
     public void showPaymentsProvidersForm() {
         
        if(orderId ==null || orderId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Guarda antes de continuar", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
            
        paymentsProvidersForm = new PaymentsProvidersDialog(null,true,orderId,globalUser);
        
        if (paymentsProvidersForm.showDialog()) {
            this.total();
        }
        

    }
 
    
    public void resetInputBoxes(){
        this.txtPrecioCobrar.setEnabled(false);
        this.txtCantidad.setEnabled(false);
        this.txtComentario.setEnabled(false);
        this.txtArticulo.setEnabled(false);
        comboOrderType.setEnabled(false);
        
        this.txtPrecioCobrar.setText("");
        this.txtCantidad.setText("");
        this.txtComentario.setText("");
        this.txtArticulo.setText("");
        idDetailOrderProviderToEdit = null;
        btnAgregar.setText("Agregar");
    }
    
    public void enabledInputBoxes(){
        this.txtPrecioCobrar.setEnabled(true);
        this.txtCantidad.setEnabled(true);
        this.txtComentario.setEnabled(true);
        comboOrderType.setEnabled(true);
    }
    
    private void fillOrderTypeDetail () {
        
        try{
          List<DetailOrderProviderType> types = orderProviderService.getTypesOrderDetailProvider();
          comboOrderType.removeAllItems();
          comboOrderType.addItem(new DetailOrderProviderType(0L , ApplicationConstants.CMB_SELECCIONE)
          );
          types.stream().forEach(t -> {
            comboOrderType.addItem(t);
          });
        } catch(DataOriginException e){
          JOptionPane.showMessageDialog(this, e.getMessage()+"\n"+e, ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void llenar_tabla_articulos(){
        
                        
        if (orderId != null && !orderId.isEmpty()){
          try{
            ordenProveedor = orderProviderService.getOrderById(Long.parseLong(orderId));
          } catch(BusinessException e){
            JOptionPane.showMessageDialog(this, e.getMessage()+"\n"+e.getCause(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            return;
          }
          
          if(ordenProveedor == null){
              // que paso aqui???
              JOptionPane.showMessageDialog(this, "Ocurrio un error inesperado, porfavor recarga el sistema", "ATENCI\u00D3N", JOptionPane.WARNING_MESSAGE);            
              return;
          }else{
              this.lblInformacionInicial.setText("ORDEN: "+ordenProveedor.getId()+"  ");
          }
           
            
        }else{

            cmbStatusOrder.setEnabled(false);
            cmbStatusOrder.setSelectedItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_ORDER);

        }
        List<DetalleRenta> detail;
        try {
            detail = rentaService.getDetailByRentId(rentaId);
        } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.WARNING_MESSAGE);            
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        formato_tabla_articulos();
         DefaultTableModel tablaDetalle = (DefaultTableModel) tablaArticulos.getModel();
         this.lblInformacionInicial.setText(this.lblInformacionInicial.getText()+" FOLIO: "+folio);
          
            for(DetalleRenta detalle : detail){
                    Object fila[] = {                                          
                        detalle.getArticulo().getArticuloId()+"",   
                        detalle.getCantidad()+"",
                        detalle.getArticulo().getDescripcion()+" "+detalle.getArticulo().getColor().getColor(), 
                        detalle.getArticulo().getPrecioCompra()
                    };
                    tablaDetalle.addRow(fila);
            }
            
        if(ordenProveedor != null){
            cmbStatusOrder.setSelectedItem(ordenProveedor.getStatusDescription());
            txtCommentOrder.setText(ordenProveedor.getComentario());
            if (ordenProveedor.getFechaEnBodega() != null) {
                Date fechaEnBodegaDate = new Date(ordenProveedor.getFechaEnBodega().getTime());
                dateDateInBodega.setDate(fechaEnBodegaDate);
            }
            fillTableDetailOrderProvider (ordenProveedor.getDetalleOrdenProveedorList());
            
        }
        this.total();
         
    }
    
    private void fillTableDetailOrderProvider (List<DetalleOrdenProveedor> detail) {
        
        formato_tabla_orden();
        
        DefaultTableModel tabla = (DefaultTableModel) jTableOrderProvider.getModel();

         for(DetalleOrdenProveedor detalle : detail ){
            Object fila[] = {                                          
                    detalle.getId(),
                    detalle.getArticulo().getArticuloId(),
                    detalle.getArticulo().getDescripcion()+" "+detalle.getArticulo().getColor().getColor(),
                    detalle.getCantidad(),
                    detalle.getPrecio(),
                    decimalFormat.format(detalle.getCantidad()*detalle.getPrecio()),
                    detalle.getComentario(),
                    detalle.getDetailOrderProviderType().getId(),
                    detalle.getDetailOrderProviderType().getDescription(),
                    detalle.getCreado(),
                    detalle.getActualizado(),
                    detalle.getStatusDescription(),
                    detalle.getProveedor().getId(),
                    detalle.getProveedor().getNombre()+" "+ detalle.getProveedor().getApellidos()
                };
            tabla.addRow(fila);
         }
    }
    
    private void fillCmbProviders () {
        cmbProviders.removeAllItems();
        try {
            providers = providersService.getAll();
        } catch (BusinessException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Erro al obtener los proveedores de la base de datos"
                            + "\n"
                            +"Detalle del error:\n"+ ex.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.WARNING_MESSAGE);            
            Logger.getLogger(OrderProviderForm.class.getName()).log(Level.SEVERE, null, ex);
        }        
        cmbProviders.addItem(new Proveedor(0L,ApplicationConstants.CMB_SELECCIONE));
        for (Proveedor provider : providers) {
            cmbProviders.addItem(provider);
        }
    }
     
    public void resetCmbOrderStatus(){
        cmbStatusOrder.removeAllItems();
        cmbStatusOrder.addItem(ApplicationConstants.CMB_SELECCIONE);
        cmbStatusOrder.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_ORDER);
        cmbStatusOrder.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_PENDING);
        cmbStatusOrder.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_CANCELLED);
        cmbStatusOrder.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_FINISH);
    }
    
    @Deprecated
    public void finishOrder(){
        if(ordenProveedor == null){
            JOptionPane.showMessageDialog(this, "Requerimos de una orden existente para finalizar", "ATENCI\u00D3N", JOptionPane.WARNING_MESSAGE);            
            return;
        }
        
        if(!ordenProveedor.getStatus().equals(ApplicationConstants.STATUS_ORDER_PROVIDER_ORDER)){
            JOptionPane.showMessageDialog(this, "Para finalizar la orden se requiere status "+ApplicationConstants.DS_STATUS_ORDER_PROVIDER_ORDER, "ATENCI\u00D3N", JOptionPane.WARNING_MESSAGE);            
            return;
        }
        
        StringBuilder stringBuilder = new StringBuilder();
        int cont = 0;
        
                stringBuilder.append("Estos articulos se agregarán a compras\n");
                stringBuilder.append("¿Deseas continuar?\n");
                stringBuilder.append("\n");
        try{
            for (int i = 0; i < jTableOrderProvider.getRowCount(); i++) {
                String status = (String) jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_STATUS);
                String type = (String) jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_TIPO_ORDEN);
                    if(!status.equals(ApplicationConstants.DS_STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED)){
                       throw new BusinessException("El articulo ["+(String) jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_DESCRIPCION_ARTICULO)+
                               "] tiene status diferente a >>> "+ApplicationConstants.DS_STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED
                       );
                    }

                    if(type.equals(ApplicationConstants.DS_TYPE_DETAIL_ORDER_SHOPPING) &&
                            status.equals(ApplicationConstants.DS_STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED)){
                        stringBuilder.append(++cont);
                        stringBuilder.append(". ");
                        stringBuilder.append("Cantidad [");
                        stringBuilder.append((Float) jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_CANTIDAD));
                        stringBuilder.append("] ");
                        stringBuilder.append(
                                (String) jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_DESCRIPCION_ARTICULO));
                        stringBuilder.append("\n");
                    }
            }
        }catch(BusinessException e){
            JOptionPane.showMessageDialog(this, e.getMessage(), "ATENCI\u00D3N", JOptionPane.WARNING_MESSAGE);            
            return;
        }
        Integer optionWithoutItems= null;
        Integer finishOrder= null;
        
        if(cont <= 0){
            optionWithoutItems = 
                    JOptionPane.showOptionDialog(this, "No se encontraron articulos para agregar a COMPRAS, ¿Deseas continuar? " ,"Confirme", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
              
        }else{
            finishOrder =
                     JOptionPane.showOptionDialog(this,  stringBuilder.toString() ,"Confirme", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
        }
        
        if(optionWithoutItems != null 
                && optionWithoutItems == 0){
            // Eligio que si
           JOptionPane.showMessageDialog(this, "FINALIZANDO ORDEN...", "ATENCI\u00D3N", JOptionPane.WARNING_MESSAGE);            
        }else if(finishOrder != null && finishOrder == 0 ){
            JOptionPane.showMessageDialog(this, "FINALIZANDO ORDEN...", "ATENCI\u00D3N", JOptionPane.WARNING_MESSAGE);    
        }
    }
    
    private float getTotalPaymentsProvider () {
        
        if (ordenProveedor == null) {
            return 0f;
        }
        
        float totalPagos=0f;
        try {
            List<PagosProveedor> pagos = 
                    providersPaymentsService.getAllProviderPaymentsByOrderId(ordenProveedor.getId().longValue());
            for (PagosProveedor pago : pagos) {
                totalPagos += pago.getCantidad();
            }
        } catch (BusinessException businessException) {
        
        }
        return totalPagos;
    }
    
    public void total(){
        
        float cantidad=0f;
        float precio=0f;
        float subTotal=0f;
        float pagos=0f;
        
                
        for (int i = 0; i < jTableOrderProvider.getRowCount(); i++) {
           cantidad = Float.parseFloat(jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_CANTIDAD).toString());
           precio = Float.parseFloat(jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_PRECIO).toString());
           subTotal += (cantidad * precio);
        }
        
        pagos = getTotalPaymentsProvider();
        
        txtPagos.setText(decimalFormat.format(pagos));
        txtSubTotal.setText(decimalFormat.format(subTotal));
        txtTotal.setText(decimalFormat.format(subTotal - pagos));
    }
    
     public void formato_tabla_orden() {
        Object[][] data = {{"","","","","","","","","","","","","",""}};
        String[] columnNames = {
                        "Id_detalle_orden",
                        "Id articulo", 
                        "Articulo",
                        "Cantidad", 
                        "Precio u.",   
                        "Importe",
                        "Comentario",
                        "Tipo Orden ID",
                        "Tipo Orden",
                        "Creado",
                        "Actualizado",
                        "Status",
                        "idProveedor",
                        "Nombre proveedor"
                         };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        jTableOrderProvider.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        jTableOrderProvider.setRowSorter(ordenarTabla);

        int[] anchos = {20,20,80,40,40, 80,80,100,80,80,80,80,10,80};

        for (int inn = 0; inn < jTableOrderProvider.getColumnCount(); inn++) {
            jTableOrderProvider.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) jTableOrderProvider.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_ID_ORDEN).setMaxWidth(0);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_ID_ORDEN).setMinWidth(0);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_ID_ORDEN).setPreferredWidth(0);
        
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_TIPO_ORDEN_ID).setMaxWidth(0);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_TIPO_ORDEN_ID).setMinWidth(0);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_TIPO_ORDEN_ID).setPreferredWidth(0);
        
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_PROVEEDOR_ID).setMaxWidth(0);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_PROVEEDOR_ID).setMinWidth(0);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_PROVEEDOR_ID).setPreferredWidth(0);
        
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_ID_ARTICULO).setMaxWidth(1);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_ID_ARTICULO).setMinWidth(1);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_ID_ARTICULO).setPreferredWidth(1);
        
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_CANTIDAD).setCellRenderer(right);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_PRECIO).setCellRenderer(right);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_IMPORTE).setCellRenderer(right);
        
    }
     
     public void formato_tabla_articulos() {
        Object[][] data = {{"", "","",""}};
        String[] columnNames = {"id articulo","Cantidad", "Descripci"+ApplicationConstants.ACENTO_O_MINUSCULA+"n","Precio compra"};
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tablaArticulos.setModel(tableModel);
        
        // Instanciamos el TableRowSorter y lo añadimos al JTable
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tablaArticulos.setRowSorter(ordenarTabla);

        int[] anchos = {20, 40,120,40};

        for (int inn = 0; inn < tablaArticulos.getColumnCount(); inn++) {
            tablaArticulos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tablaArticulos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tablaArticulos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaArticulos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaArticulos.getColumnModel().getColumn(0).setPreferredWidth(0);
        tablaArticulos.getColumnModel().getColumn(1).setCellRenderer(centrar);
       

    }

    /**
     * Creates new form OrderProviderForm
     */
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel11 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        lblInformacionInicial = new javax.swing.JLabel();
        txtCommentOrder = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cmbStatusOrder = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        cmbProviders = new javax.swing.JComboBox<>();
        lbl_categoria = new javax.swing.JLabel();
        btnReloadCmbProviders = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        dateDateInBodega = new com.toedter.calendar.JDateChooser();
        lblInfoExtra = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtArticulo = new javax.swing.JTextField();
        txtPrecioCobrar = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtComentario = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnAgregar = new javax.swing.JButton();
        txtCantidad = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        comboOrderType = new javax.swing.JComboBox<>();
        jbtnPagosProveedor = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaArticulos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jPanel9 = new javax.swing.JPanel();
        txtTotal = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtSubTotal = new javax.swing.JTextField();
        txtPagos = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableOrderProvider = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        lblQuitarElemento = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();

        jLabel11.setText("jLabel11");

        lblInformacionInicial.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lblInformacionInicial.setText("lblInformacionInicial");

        txtCommentOrder.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Proveedor:");

        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Estatus orden:");

        cmbStatusOrder.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmbStatusOrder.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Comentario:");

        lbl_categoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/common/icons24/Add-icon.png"))); // NOI18N
        lbl_categoria.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lbl_categoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_categoriaMouseClicked(evt);
            }
        });
        lbl_categoria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lbl_categoriaKeyPressed(evt);
            }
        });

        btnReloadCmbProviders.setIcon(new javax.swing.ImageIcon(getClass().getResource("/common/icons24/refresh-24.png"))); // NOI18N
        btnReloadCmbProviders.setToolTipText("");
        btnReloadCmbProviders.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReloadCmbProviders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadCmbProvidersActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Fecha en bodega:");
        jLabel12.setToolTipText("Fecha en la que debe de estar en bodega");

        lblInfoExtra.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        lblInfoExtra.setForeground(new java.awt.Color(204, 0, 51));
        lblInfoExtra.setText("jLabel13");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblInformacionInicial, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnReloadCmbProviders, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(lbl_categoria, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(cmbProviders, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtCommentOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel10))
                .addGap(12, 12, 12)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(lblInfoExtra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cmbStatusOrder, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dateDateInBodega, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblInformacionInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblInfoExtra, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(dateDateInBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(txtCommentOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_categoria, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnReloadCmbProviders, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbProviders, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbStatusOrder, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Articulo:");

        txtArticulo.setEditable(false);
        txtArticulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txtPrecioCobrar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtPrecioCobrar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPrecioCobrarKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Costo:");

        txtComentario.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtComentario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtComentarioKeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Descripción");

        btnAgregar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnAgregar.setText("Agregar");
        btnAgregar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        txtCantidad.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCantidadKeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Cantidad:");

        comboOrderType.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        comboOrderType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboOrderTypeActionPerformed(evt);
            }
        });

        jbtnPagosProveedor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnPagosProveedor.setText("Pagos proveedor");
        jbtnPagosProveedor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnPagosProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPagosProveedorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(125, 125, 125)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecioCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtComentario, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboOrderType, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnPagosProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(btnAgregar)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecioCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtComentario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(comboOrderType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnPagosProveedor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAgregar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        tablaArticulos.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tablaArticulos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablaArticulos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tablaArticulos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaArticulosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaArticulos);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        txtTotal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtTotal.setEnabled(false);

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Sub total:");

        txtSubTotal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSubTotal.setEnabled(false);

        txtPagos.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtPagos.setEnabled(false);

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Total:");

        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Pagos:");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                        .addGroup(jPanel9Layout.createSequentialGroup()
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(12, 12, 12)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)))
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtSubTotal)
                    .addComponent(txtPagos)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSubTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPagos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTableOrderProvider.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTableOrderProvider.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableOrderProvider.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTableOrderProvider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableOrderProviderMouseClicked(evt);
            }
        });
        jTableOrderProvider.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableOrderProviderKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTableOrderProvider);

        jButton3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton3.setText("Editar");
        jButton3.setToolTipText("Elimina el elemento de la bd");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton2.setText("Cambiar status");
        jButton2.setToolTipText("Elimina el elemento de la bd");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton1.setText("(-) quitar elemento");
        jButton1.setToolTipText("Elimina el elemento de la bd");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lblQuitarElemento.setText("lblQuitarElemento");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblQuitarElemento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(129, 129, 129))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQuitarElemento))
                .addContainerGap())
        );

        jMenuBar1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jMenu2.setText("Archivo");
        jMenu2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenuItem3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem3.setText("Guardar");
        jMenuItem3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        jMenu1.setText("Exportar");
        jMenu1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jMenu1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenuItem2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem2.setText("Exportar tabla pedidos");
        jMenuItem2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem1.setText("Exportar tabla articulos");
        jMenuItem1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem6.setText("Generar PDF");
        jMenuItem6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtPrecioCobrarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrecioCobrarKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtPrecioCobrarKeyPressed

    private void txtComentarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtComentarioKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtComentarioKeyPressed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // TODO add your handling code here:

        this.agregar_articulo_a_orden();
    }//GEN-LAST:event_btnAgregarActionPerformed

    
    private void txtCantidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadKeyPressed

    private void tablaArticulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaArticulosMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {

            this.enabledInputBoxes();

            String artId = tablaArticulos.getValueAt(tablaArticulos.getSelectedRow(), HD_ARTICULOS_ID_ARTICULO).toString();
            String descripcion = tablaArticulos.getValueAt(tablaArticulos.getSelectedRow(), HD_ARTICULOS_DESCRIPCION_ARTICULO).toString();
            String precioCobrar = tablaArticulos.getValueAt(tablaArticulos.getSelectedRow(), HD_ARTICULOS_PRECIO_COBRAR).toString();
            this.g_cantidadEnPedido = tablaArticulos.getValueAt(tablaArticulos.getSelectedRow(), HD_ARTICULOS_CANTIDAD_PEDIDO).toString();
            if(this.g_cantidadEnPedido == null || this.g_cantidadEnPedido.equals(""))
            this.g_cantidadEnPedido = "0";
            this.g_articuloId = artId;
            this.txtArticulo.setText(descripcion);
            this.txtCantidad.requestFocus();
            this.txtPrecioCobrar.setText(precioCobrar);
            
            btnAgregar.setText("Agregar");
            idDetailOrderProviderToEdit = null;
            txtComentario.setText("");
            //comboOrderType.setSelectedIndex(0);

        }
    }//GEN-LAST:event_tablaArticulosMouseClicked

    private void showPagosProveedor () {
        String idDetailOrder = 
                    jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_ID_ORDEN).toString();
    }
    
    public void changeStatus(){
        
        if(JOptionPane.showOptionDialog(this, "\u00BFCambiar status? " ,"Confirme para continuar...", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si") != 0){
                    return;
        }else{
             String idDetailOrder = 
                    jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_ID_ORDEN).toString();

             String statusChange;
             try{
                statusChange = this.orderProviderService.changeStatusDetailOrderById(Long.parseLong(idDetailOrder));
                jTableOrderProvider.setValueAt(statusChange,jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_STATUS);
             }catch(BusinessException e){
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);            
                Toolkit.getDefaultToolkit().beep();
                return;
             }
        }
    }
    private void jTableOrderProviderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableOrderProviderMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
//            String comentario = jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_COMENTARIO).toString();
            String descripcionArticulo = jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_DESCRIPCION_ARTICULO).toString();
            this.lblQuitarElemento.setText(descripcionArticulo);
            
            if(descripcionArticulo!=null){
                changeStatus();
            }
        }
    }//GEN-LAST:event_jTableOrderProviderMouseClicked

    private void jTableOrderProviderKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableOrderProviderKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_jTableOrderProviderKeyPressed

     public void agregar_articulo_a_orden(){
        
        StringBuilder mensaje = new StringBuilder();
        int cont = 0;
        
        if(txtArticulo.getText().equals(""))
            mensaje.append(++cont + ". Debes elegir un articulo para agregar el faltante\n");

        float cantidad = 0f;
        float precio = 0f;
        DetailOrderProviderType type = (DetailOrderProviderType) this.comboOrderType.getSelectedItem();
        
        
        try {
            cantidad = Float.parseFloat(this.txtCantidad.getText());
           
            
        } catch (NumberFormatException e) {
            mensaje.append(++cont + ". Error al ingresar la cantidad\n");
        } catch (Exception e) {
            mensaje.append(++cont + ". Error al ingresar la cantidad\n");
        }
        
         try {
           
            precio = Float.parseFloat(this.txtPrecioCobrar.getText());
            
        } catch (NumberFormatException e) {
            mensaje.append(++cont + ". Error al ingresar el precio\n");
        } catch (Exception e) {
            mensaje.append(++cont + ". Error al ingresar el precio\n");
        }
        
        if(cantidad <= 0 ){
            mensaje.append(++cont + ". La cantidad debe ser mayor a cero\n");
        }
        
        
        if(type.getId().toString().equals("0")){
            mensaje.append(++cont + ". Seleccione tipo de orden\n");
        }
        
        
                
        Proveedor proveedor = (Proveedor) cmbProviders.getModel().getSelectedItem();
        if (proveedor.getId().equals(0L)) {
            mensaje.append(++cont + ". Seleccione un proveedor.\n");
        }
        
        
        if(!mensaje.toString().equals("")){
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, mensaje+"", "Error", JOptionPane.INFORMATION_MESSAGE);            
            return;
        }
        
        // verificamos si existe para lanzar una advertencia
        boolean existe = false;
        for (int i = 0; i < jTableOrderProvider.getRowCount(); i++) {            
            if (this.g_articuloId.equals(jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_ID_ARTICULO).toString() )
                    ) {
                existe = true;
                break;
            }
        }
        
        if (idDetailOrderProviderToEdit == null && existe){
            if(JOptionPane.showOptionDialog(this, "Ya existe ese articulo.  \u00BFContinuar? " ,"Confirme", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si") != 0)
                return;                
        }

        String comentario = this.txtComentario.getText();        
        
        if (idDetailOrderProviderToEdit != null) {
            // update detail order          
            try {
                orderProviderService.updateDetailOrderProvider(Long.parseLong(idDetailOrderProviderToEdit), cantidad, precio, comentario, type.getId(),"1",proveedor.getId());
                OrdenProveedor orderProvider = orderProviderService.getOrderById(Long.parseLong(orderId));
                formato_tabla_orden();
                fillTableDetailOrderProvider(orderProvider.getDetalleOrdenProveedorList());
                
            } catch (BusinessException e) {
                JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } else {
            String datos[] = {
                "0",
                g_articuloId,
                txtArticulo.getText(),
                cantidad+"",
                precio+"",
                decimalFormat.format(cantidad*precio),
                comentario,
                type.getId().toString(),
                type.getDescription(),
                "",
                CREATED_AT_EMPTY,
                UPDATED_AT_EMPTY,
                proveedor.getId().toString(),
                proveedor.getNombre()+ " " +proveedor.getApellidos()
            };
            
            DefaultTableModel tabla = (DefaultTableModel) jTableOrderProvider.getModel();
            tabla.addRow(datos);
        }

        
       this.resetInputBoxes();
       this.total();
       
    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if(globalUser.getAdministrador().equals("0")){
            JOptionPane.showMessageDialog(this, ApplicationConstants.MESSAGE_NOT_PERMISIONS_ADMIN, "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (jTableOrderProvider.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para continuar ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String id = jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_ID_ORDEN).toString();

        if(id == null || id.equals("")){
            JOptionPane.showMessageDialog(this, ApplicationConstants.MESSAGE_UNEXPECTED_ERROR, ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if(id.equals("0")){
            // entonces es primera vez solo eliminamos de la tabla
            DefaultTableModel temp = (DefaultTableModel) this.jTableOrderProvider.getModel();
            temp.removeRow(jTableOrderProvider.getSelectedRow());

        }else{
            if(JOptionPane.showOptionDialog(this, "Se eliminará de la base de datos,  \u00BFContinuar? " ,"Confirme eliminacion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si") != 0)
            return;

            String datos[] = {"0",id};
            try {
                this.orderProviderService.deleteDetailOrdenProveedorById(Long.parseLong(id));
                DefaultTableModel temp = (DefaultTableModel) this.jTableOrderProvider.getModel();
                temp.removeRow(jTableOrderProvider.getSelectedRow());
            } catch (Exception e) {
                LOGGER.error(String.format(" ocurrio un error al actualizar los datos [%s] ", e));
                JOptionPane.showMessageDialog(this, "Ocurrio un error actualizar la orden\n "+e, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        utilityService.exportarExcel(tablaArticulos);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        utilityService.exportarExcel(jTableOrderProvider);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        this.saveOrderProvider();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        if (jTableOrderProvider.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para continuar ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        this.changeStatus();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        try {
            reportPDF(orderId);
        } catch (Exception e) {
            LOGGER.error(e);
            System.out.println("Mensaje de Error:" + e.toString());
            JOptionPane.showMessageDialog(rootPane, "Error cargando el reporte maestro: " + e.getMessage() + "\n" + e);
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void comboOrderTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboOrderTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboOrderTypeActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        
        if (jTableOrderProvider.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para continuar ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        this.enabledInputBoxes();

        String artId = jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_ID_ARTICULO).toString();
        String descripcion = jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_DESCRIPCION_ARTICULO).toString();
        String precioCobrar = jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_PRECIO).toString();
        String providerId = jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_PROVEEDOR_ID).toString();
        String providerName = jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_PROVEEDOR_NAME).toString();
        
        this.g_cantidadEnPedido = jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_CANTIDAD).toString();
        if(this.g_cantidadEnPedido == null || this.g_cantidadEnPedido.equals("")){
            this.g_cantidadEnPedido = "0";
        }
        this.g_articuloId = artId;
        
        DetailOrderProviderType type = new DetailOrderProviderType(
                Long.parseLong(jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_TIPO_ORDEN_ID).toString()) ,
                jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_TIPO_ORDEN).toString()
        );
        
        comboOrderType.getModel().setSelectedItem(type);
        
        
        
        this.cmbProviders.getModel().setSelectedItem(new Proveedor(Long.parseLong(providerId),providerName));
        
        this.txtArticulo.setText(descripcion);
        this.txtCantidad.requestFocus();
        txtCantidad.setText(g_cantidadEnPedido);
        txtComentario.setText(jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_COMENTARIO).toString());
        this.txtPrecioCobrar.setText(precioCobrar);
        idDetailOrderProviderToEdit = jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_ID_ORDEN).toString();
        btnAgregar.setText("Actualizar");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jbtnPagosProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPagosProveedorActionPerformed
        showPaymentsProvidersForm();
    }//GEN-LAST:event_jbtnPagosProveedorActionPerformed

    private void lbl_categoriaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_categoriaMouseClicked
        showProviders();
    }//GEN-LAST:event_lbl_categoriaMouseClicked

    private void lbl_categoriaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lbl_categoriaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_categoriaKeyPressed

    private void btnReloadCmbProvidersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReloadCmbProvidersActionPerformed
        fillCmbProviders();
        btnReloadCmbProviders.setEnabled(false);
        UtilityCommon.setTimeout(() -> btnReloadCmbProviders.setEnabled(true), DELAY_SECONDS);

    }//GEN-LAST:event_btnReloadCmbProvidersActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnReloadCmbProviders;
    private javax.swing.JComboBox<Proveedor> cmbProviders;
    private javax.swing.JComboBox cmbStatusOrder;
    private javax.swing.JComboBox<common.model.providers.DetailOrderProviderType> comboOrderType;
    private com.toedter.calendar.JDateChooser dateDateInBodega;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableOrderProvider;
    private javax.swing.JButton jbtnPagosProveedor;
    private javax.swing.JLabel lblInfoExtra;
    private javax.swing.JLabel lblInformacionInicial;
    private javax.swing.JLabel lblQuitarElemento;
    private javax.swing.JLabel lbl_categoria;
    private javax.swing.JTable tablaArticulos;
    private javax.swing.JTextField txtArticulo;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtComentario;
    public static javax.swing.JTextField txtCommentOrder;
    public static javax.swing.JTextField txtPagos;
    private javax.swing.JTextField txtPrecioCobrar;
    public static javax.swing.JTextField txtSubTotal;
    public static javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
