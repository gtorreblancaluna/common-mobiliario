package common.form.provider;

import common.constants.ApplicationConstants;
import common.constants.PropertyConstant;
import common.utilities.UtilityCommon;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.exceptions.NoDataFoundException;
import common.services.UtilityService;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import common.model.DatosGenerales;
import common.model.Renta;
import common.model.Usuario;
import common.model.providers.OrdenProveedor;
import common.model.providers.OrderProviderCopyParameter;
import common.model.providers.PagosProveedor;
import common.model.providers.queryresult.DetailOrderSupplierQueryResult;
import common.model.providers.ParameterOrderProvider;
import common.model.providers.PaymentProviderFilter;
import common.model.providers.queryresult.BalanceProviderQueryResult;
import common.services.RentaService;
import common.services.providers.OrderProviderService;
import common.services.providers.ProvidersPaymentsService;
import common.tables.TablePaymentsProvider;
import common.tables.TableSaldoProveedores;
import common.tables.TableViewOrdersProviders;
import common.tables.TableViewOrdersProvidersDetail;
import common.utilities.JasperPrintUtility;
import static common.utilities.UtilityCommon.validateLimitCharacters;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JDesktopPane;


public class ViewOrdersProviders extends javax.swing.JInternalFrame {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ViewOrdersProviders.class.getName());
    private final UtilityService utilityService = UtilityService.getInstance();
    private final OrderProviderService orderService = OrderProviderService.getInstance();
    private final ProvidersPaymentsService providersPaymentsService = ProvidersPaymentsService.getInstance();
    private static final DecimalFormat decimalFormat = UtilityCommon.getDecimalFormat();
    public static String g_idRenta=null;  
    private OrderProviderForm orderProviderForm = null;
    private int indexTabPanelActive = 0;
    private final TableViewOrdersProviders tableViewOrdersProviders;
    private final TableViewOrdersProvidersDetail tableViewOrdersProvidersDetail;
    private final TablePaymentsProvider tablePaymentsProvider;
    private final TableSaldoProveedores tableSaldoProveedores;
    private final RentaService rentaService = RentaService.getInstance();
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ViewOrdersProviders.class.getName());
    private Usuario globalUser;
    private DatosGenerales datosGenerales;
    private JDesktopPane indexDesktopPane1;
    private final String pathLocationFromInvoke;


    public ViewOrdersProviders(Usuario globalUser,DatosGenerales datosGenerales,JDesktopPane jDesktopPane1,
            final String pathLocationMain) {
        
        initComponents();
        
        // center jinternal frame
        Dimension desktopSize = jDesktopPane1.getSize();
        Dimension jInternalFrameSize = this.getSize();
        this.setLocation((desktopSize.width - jInternalFrameSize.width)/2,
            (desktopSize.height- jInternalFrameSize.height)/2);
        
        
        this.indexDesktopPane1 = jDesktopPane1;
        this.globalUser = globalUser;
        this.datosGenerales = datosGenerales;
        this.setTitle("Ordenes al proveedor");
        lblInfoGeneral.setText("");
        setResizable(true);
        setMaximizable(true);
        initComboBox();
        eventListener();
        tableViewOrdersProviders = new TableViewOrdersProviders();
        tableViewOrdersProvidersDetail = new TableViewOrdersProvidersDetail();
        tablePaymentsProvider = new TablePaymentsProvider();
        tableSaldoProveedores = new TableSaldoProveedores();
        pathLocationFromInvoke = pathLocationMain;
        UtilityCommon.addJtableToPane(937, 305, tabPanelGeneral, tableViewOrdersProviders);
        UtilityCommon.addJtableToPane(937, 305, tabPanelDetail, tableViewOrdersProvidersDetail);
        UtilityCommon.addJtableToPane(937, 305, tabPanelPaymentsProvider, tablePaymentsProvider);
        UtilityCommon.addJtableToPane(937, 305, panelSadoProveedores, tableSaldoProveedores);
        
        tableViewOrdersProvidersDetail.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    showPaymentsProvidersForm();
                }
            }
        });
        
        tableViewOrdersProviders.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    showPaymentsProvidersForm();
                }
            }
        });        
        
        // msximixst ventana
        UtilityCommon.setMaximum(this, PropertyConstant.MAX_WIN_CONSULTAR_PROVEEDORES);
        
    }

    
    private enum ColumnToGetValue {
        RENTA_ID,
        ORDER_ID,
        FOLIO,
        DETAIL_ORDER_ID,
    }
    
    private enum IndexTabPanel {
        
        TAB_PANEL_GENERAL(0),
        TAB_PANEL_DETAIL(1),
        TAB_PANEL_PAYMENT_DETAIL(2),
        TAB_PANEL_SALDO_PROVEEDORES(3);
        
        private final int index;
        
        IndexTabPanel (int index) {
            this.index = index;
        }
        
        public int getIndex () {
            return this.index;
        }
        
        public static IndexTabPanel getEnum (int value) {
            for (IndexTabPanel indexTabPanel : IndexTabPanel.values()) {
                if ( (indexTabPanel.index+"").equals(value+"")) {
                    return indexTabPanel;
                }
            }
            return TAB_PANEL_GENERAL;
        }
    }
    
    private void eventListener () {
        tabGeneral.addMouseListener(new MouseAdapter(){
        @Override
        public void mousePressed(MouseEvent e) {
            Component c = tabGeneral.getComponentAt(new Point(e.getX(), e.getY()));
                //TODO Find the right label and print it! :-)
                indexTabPanelActive = tabGeneral.getSelectedIndex();            
            }
        });
    }
    
     public void showProviders() {
        ViewProviderForm win = new ViewProviderForm(null, true);
        win.setVisible(true);
        win.setLocationRelativeTo(null);

    }
     
    private void addNewOrderProvider () {
        String folioID = JOptionPane.showInputDialog(this, "Folio de la renta para generar nueva orden.", "Nueva orden.", JOptionPane.INFORMATION_MESSAGE);
        if(folioID == null || folioID.isEmpty()){
             return;
        }
        System.out.println(folioID);

        try {
            Integer folioInt = Integer.parseInt(folioID);
            Renta renta = rentaService.getByFolio(folioInt);
            if (renta == null) {
                throw new BusinessException(String.format("Folio '%s' no encontrado en la base de datos.",folioInt));
            }
            OrderProviderForm form = 
                    new OrderProviderForm(renta.getFolio()+"", null, renta.getRentaId()+"",globalUser, datosGenerales,pathLocationFromInvoke,
                    this.indexDesktopPane1);
            
            indexDesktopPane1.add(form);
            form.show();
        } catch (NumberFormatException numberFormatException) {
            JOptionPane.showMessageDialog(this, "Introduce un numero valido", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        } catch (BusinessException | DataOriginException dataOriginException) {
            JOptionPane.showMessageDialog(this, dataOriginException.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }
   }
    
     public void mostrar_agregar_orden_proveedor(){
         
        String rentaId;
        String orderId;
        String folio;
        
       try {
            rentaId = getValueIdBySelectedRow(ColumnToGetValue.RENTA_ID);
            orderId = getValueIdBySelectedRow(ColumnToGetValue.ORDER_ID);
            folio = getValueIdBySelectedRow(ColumnToGetValue.FOLIO);
       } catch (BusinessException e) {
           LOGGER.error(e);
           JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
           return;
       }
        orderProviderForm = new OrderProviderForm(folio, orderId, rentaId,globalUser, datosGenerales,pathLocationFromInvoke,
        this.indexDesktopPane1);
        this.indexDesktopPane1.add(orderProviderForm);
        orderProviderForm.show();
        
    }

     public void reportPDF(){
         
       String orderId;
       try {
            orderId = getValueIdBySelectedRow(ColumnToGetValue.ORDER_ID);
                
            JasperPrintUtility.generatePDFOrderProvider(orderId,datosGenerales, pathLocationFromInvoke);
       } catch (Exception e) {
           LOGGER.error(e);
           JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
       }       
       
     
     }
   public void initComboBox(){
       
        this.cmbLimit.removeAllItems();
        this.cmbStatus.removeAllItems();
        cmbLimit.addItem("100");
        cmbLimit.addItem("500");
        cmbLimit.addItem("1000");
        cmbLimit.addItem("10000");
        cmbStatus.addItem(ApplicationConstants.CMB_SELECCIONE);
        cmbStatus.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_ORDER);
        cmbStatus.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_PENDING);
        cmbStatus.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_CANCELLED);     
   
   }
   
   
   public void showPaymentsProvidersForm() {
       String orderId;
       try {
            orderId = getValueIdBySelectedRow(ColumnToGetValue.ORDER_ID);
       } catch (BusinessException e) {
           LOGGER.error(e);
           JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
           return;
       }
       
        PaymentsProvidersDialog paymentsProvidersForm = new PaymentsProvidersDialog(null,true,orderId,globalUser);
        if (paymentsProvidersForm.showDialog()) {
           this.search();
        }
        
    }
   

   
   private ParameterOrderProvider getParameters () throws BusinessException{
     
     ParameterOrderProvider parameter = new ParameterOrderProvider();
     
     Integer folioRenta = null;
     Integer orderNumber = null;
     
     validateLimitCharacters(txtSearchFolioRenta);
     validateLimitCharacters(txtSearchOrderNumber);
     validateLimitCharacters(txtSearchByNameProvider);
     
     
     if (!txtSearchFolioRenta.getText().isEmpty()) {
        try{
            folioRenta = Integer.valueOf(txtSearchFolioRenta.getText().trim());
        }catch(NumberFormatException e){
            txtSearchFolioRenta.requestFocus();
            txtSearchFolioRenta.selectAll();
            throw new BusinessException("Ingresa un número valido para el folio de la renta.");
        }
     }
     
     if (!txtSearchOrderNumber.getText().isEmpty()) {
        try{
            orderNumber = Integer.valueOf(this.txtSearchOrderNumber.getText());
        }catch(NumberFormatException e){
            txtSearchOrderNumber.requestFocus();
            txtSearchOrderNumber.selectAll();
            throw new BusinessException("Ingresa un número valido para el número de orden.");
        }
     }
     
     parameter.setFolioRenta(folioRenta);
     parameter.setOrderId(orderNumber);

    if(!txtSearchByNameProvider.getText().isEmpty()){
        parameter.setNameProvider(this.txtSearchByNameProvider.getText().trim());
    }
    
    if(this.txtSearchInitialDateEnBodega.getDate() != null && this.txtSearchEndDateEnBodega.getDate() != null){
        parameter.setInitDateEnBodega(new Timestamp(txtSearchInitialDateEnBodega.getDate().getTime()));
        parameter.setEndDateEnBodega(new Timestamp(txtSearchEndDateEnBodega.getDate().getTime()));
    }
    if(this.txtSearchInitialDate.getDate() != null && this.txtSearchEndDate.getDate() != null){
        parameter.setInitDate(new Timestamp(txtSearchInitialDate.getDate().getTime()));
        parameter.setEndDate(new Timestamp(txtSearchEndDate.getDate().getTime()));
    }
    if(this.txtSearchInitialEventDate.getDate() != null && this.txtSearchEndEventDate.getDate() != null){
        try {
            String formatDate = "dd/MM/yyyy";
            parameter.setInitEventDate(UtilityCommon.getStringFromDate(txtSearchInitialEventDate.getDate(),formatDate));
            parameter.setEndEventDate(UtilityCommon.getStringFromDate(txtSearchEndEventDate.getDate(),formatDate));
        } catch (ParseException e) {
            throw new BusinessException("Error al obtener la fecha del evento.\n"+e.getMessage());
        }
    }
    if(!this.cmbStatus.getModel().getSelectedItem().equals(ApplicationConstants.CMB_SELECCIONE)){
        parameter.setStatus(this.cmbStatus.getSelectedItem().toString());
    }

     parameter.setLimit(Integer.parseInt(String.valueOf(this.cmbLimit.getSelectedItem())));
     return parameter;
   }
   
   private void fillTableTabPanelDetail () {
       
    try {
        
       ParameterOrderProvider parameter = getParameters();
       List<DetailOrderSupplierQueryResult> list;
       tableViewOrdersProvidersDetail.format();
       

            list = orderService.getDetailOrderSupplierCustomize(parameter);
            this.lblInfoGeneral.setText("Registros: "+list.size()+". Límite: "+
                this.cmbLimit.getSelectedItem().toString());
            
            DefaultTableModel tableModel = (DefaultTableModel) tableViewOrdersProvidersDetail.getModel();

       for(DetailOrderSupplierQueryResult detail : list){
            Object fila[] = {                                          
                detail.getOrderSupplierId(),
                detail.getOrderSupplierDetailId(),
                detail.getRentaId(),
                detail.getFolio(),
                detail.getProduct(),
                detail.getAmount(),
                detail.getPrice() <= 0 ? "" : decimalFormat.format(detail.getPrice()),
                detail.getTotal() <= 0 ? "" : decimalFormat.format(detail.getTotal()),
                detail.getEventDate(),
                detail.getUser(),
                detail.getSupplier(),
                detail.getDetailComment(),
                detail.getOrderDetailType(),
                detail.getCreado(),
                detail.getPagosProveedor()
              };
              tableModel.addRow(fila);

       }
        }catch(NoDataFoundException e){
            this.lblInfoGeneral.setText("No se han obtenido resultados :(");
        }catch(BusinessException e){
            LOGGER.error(e);
            JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }finally{
           Toolkit.getDefaultToolkit().beep();
        }
       
   }
   
   private void fillTableTabPanelGeneral () {
     
    try{
         
        ParameterOrderProvider parameter = getParameters();
        List<OrdenProveedor> list;
        tableViewOrdersProviders.format();
        list = orderService.getOrdersByParameters(parameter);

        this.lblInfoGeneral.setText("Registros: "+list.size()+". Límite: "+
                this.cmbLimit.getSelectedItem().toString());

       DefaultTableModel tableModel = (DefaultTableModel) tableViewOrdersProviders.getModel();

       for(OrdenProveedor orden : list){      

            Object fila[] = {
                false,
                orden.getId(),
                orden.getRenta().getFolio(),
                orden.getUsuario().getNombre()+" "+orden.getUsuario().getApellidos(),
                orden.getStatusDescription(),
                orden.getCreado(),
                orden.getActualizado(),
                orden.getComentario(),
                orden.getRenta().getRentaId(),             
                decimalFormat.format(orden.getTotal()),
                orden.getAbonos() > 0 ? decimalFormat.format(orden.getAbonos()) : "",
                decimalFormat.format((orden.getTotal() - orden.getAbonos())),
                orden.getRenta().getFechaEvento(),
                orden.getFechaEnBodega()
              };
              tableModel.addRow(fila);

       }
    }catch(NoDataFoundException e){
         this.lblInfoGeneral.setText("No se han obtenido resultados :(");
     }catch(BusinessException e){
        LOGGER.error(e);
        JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
     } finally {
        Toolkit.getDefaultToolkit().beep();
     }
     
   
   }
      
   
   private String getValueIdBySelectedRow (ColumnToGetValue columnToGetValue)throws BusinessException {
       
       JTable tableActive;
       int columnNumber=0;
       switch (IndexTabPanel.getEnum(indexTabPanelActive)) {
           case TAB_PANEL_GENERAL:
               tableActive = this.tableViewOrdersProviders;
               switch (columnToGetValue) {
                    case ORDER_ID:
                        columnNumber = TableViewOrdersProviders.Column.ORDER_NUM.getNumber();
                    break;
                    case RENTA_ID:
                        columnNumber = TableViewOrdersProviders.Column.RENTA_ID.getNumber();
                        break;
                    case FOLIO:
                        columnNumber = TableViewOrdersProviders.Column.FOLIO_RENTA.getNumber();
                        break;
                    case DETAIL_ORDER_ID:
                       throw new BusinessException(ApplicationConstants.ACTION_NOT_PERMITID);
                    default:
                        throw new AssertionError();
                }
               break;
           case TAB_PANEL_DETAIL:
               tableActive = this.tableViewOrdersProvidersDetail;
               switch (columnToGetValue) {
                    case ORDER_ID:
                        columnNumber = TableViewOrdersProvidersDetail.Column.ORDER_SUPPLIER_ID.getNumber();
                    break;
                    case RENTA_ID:
                        columnNumber = TableViewOrdersProvidersDetail.Column.RENTA_ID.getNumber();
                        break;
                    case FOLIO:
                        columnNumber = TableViewOrdersProvidersDetail.Column.FOLIO.getNumber();
                        break;
                    case DETAIL_ORDER_ID:
                        columnNumber = TableViewOrdersProvidersDetail.Column.ORDER_SUPPLIER_DETAIL_ID.getNumber();
                        break;
                    default:
                        throw new AssertionError();
                }
               break;
            case TAB_PANEL_PAYMENT_DETAIL:
                tableActive = this.tablePaymentsProvider;
                switch (columnToGetValue) {
                    case ORDER_ID:
                        columnNumber = TablePaymentsProvider.Column.ORDER_ID.getNumber();
                    break;
                    case RENTA_ID:
                        columnNumber = TablePaymentsProvider.Column.RENTA_ID.getNumber();
                        break;
                    case FOLIO:
                        columnNumber = TablePaymentsProvider.Column.FOLIO.getNumber();
                        break;
                    case DETAIL_ORDER_ID:
                        throw new BusinessException(ApplicationConstants.ACTION_NOT_PERMITID);
                    default:
                        throw new AssertionError();
                }
                break;
            case TAB_PANEL_SALDO_PROVEEDORES:
                tableActive = this.tableSaldoProveedores;
                switch (columnToGetValue) {
                    case ORDER_ID:
                        columnNumber = TableSaldoProveedores.Column.ORDER_ID.getNumber();
                    break;
                    case RENTA_ID:
                        columnNumber = TableSaldoProveedores.Column.RENTA_ID.getNumber();
                        break;
                    case FOLIO:
                        columnNumber = TableSaldoProveedores.Column.FOLIO.getNumber();
                        break;
                    case DETAIL_ORDER_ID:
                        throw new BusinessException(ApplicationConstants.ACTION_NOT_PERMITID);
                    default:
                        throw new AssertionError();
                }
                break;
           default:
               throw new BusinessException("Ops. error no definido. contacta a Gera..");
       }
       
       if (tableActive.getSelectedRow() == - 1) {
           throw new BusinessException(ApplicationConstants.SELECT_A_ROW_NECCESSARY);
       }
       
       return tableActive.getValueAt(tableActive.getSelectedRow(), columnNumber).toString(); 
   }
   private void fillSaldoProveedores () {

    try {
        
       ParameterOrderProvider parameter = getParameters();       
       PaymentProviderFilter paymentProviderFilter = getPaymentProviderFilter(parameter);
       
       tableSaldoProveedores.format();

           
            DefaultTableModel tableModel = (DefaultTableModel) tableSaldoProveedores.getModel();
            List<BalanceProviderQueryResult> balanceProvider = providersPaymentsService.getSaldosProveedor(paymentProviderFilter);
            
            for (BalanceProviderQueryResult balance : balanceProvider) {

                Object row[] = {
                    balance.getDetailId(),
                    balance.getRentaId(),
                    balance.getOrderProviderId(),
                    balance.getRentaFolio(),
                    balance.getProviderName()+ " " +balance.getProviderLastName(),
                    balance.getItemsCount(),
                    balance.getPaymentCount(),
                    decimalFormat.format(balance.getImportTotal()),
                    decimalFormat.format(balance.getPaymentTotal()),
                    decimalFormat.format(balance.getImportTotal() - balance.getPaymentTotal())
                  };
                tableModel.addRow(row);
            }
            
            lblInfoGeneral.setText("Registros obtenidos: "+balanceProvider.size());
       
       } catch (BusinessException be) {
           JOptionPane.showMessageDialog(this, be.getMessage(), 
                   ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
       }finally{
           Toolkit.getDefaultToolkit().beep();
       }
   }
   
   private PaymentProviderFilter getPaymentProviderFilter (ParameterOrderProvider parameter) {
       
       PaymentProviderFilter paymentProviderFilter =  new PaymentProviderFilter();
       paymentProviderFilter.setInitDateEnBodega(parameter.getInitDateEnBodega());
       paymentProviderFilter.setEndDateEnBodega(parameter.getEndDateEnBodega());
       paymentProviderFilter.setInitDate(parameter.getInitDate());
       paymentProviderFilter.setEndDate(parameter.getEndDate());
       paymentProviderFilter.setOrderId(parameter.getOrderId() != null ? parameter.getOrderId().toString() : null);
       paymentProviderFilter.setFolioRenta(parameter.getFolioRenta() != null ? parameter.getFolioRenta().toString() : null);
       paymentProviderFilter.setNameProvider(parameter.getNameProvider() != null ? parameter.getNameProvider().trim() : null);
       paymentProviderFilter.setLimit(parameter.getLimit());
       
       return paymentProviderFilter;
   }
   
   private void fillTablePaymentsProvider () {

    try {
        
       ParameterOrderProvider parameter = getParameters();
       
       PaymentProviderFilter paymentProviderFilter = getPaymentProviderFilter(parameter);
       

            tablePaymentsProvider.format();
            DefaultTableModel tableModel = (DefaultTableModel) tablePaymentsProvider.getModel();
            List<PagosProveedor> pagos = providersPaymentsService.getByFilter(paymentProviderFilter);
            float total = 0f;
            
            for (PagosProveedor pago : pagos) {
                total += pago.getCantidad();
                Object fila[] = {
                    pago.getId(),
                    pago.getRenta().getRentaId(),
                    pago.getOrdenProveedor().getId(),
                    pago.getRenta().getFolio(),
                    pago.getUsuario().getUsuarioId(),
                    pago.getUsuario().getNombre() + " " +pago.getUsuario().getApellidos(),
                    pago.getTipoAbono().getDescripcion(),
                    pago.getCantidad() != null ? decimalFormat.format(pago.getCantidad()) : "",
                    pago.getComentario(),
                    pago.getCreado(),
                    pago.getActualizado(),
                    pago.getProveedor().getId(),
                    pago.getProveedor().getNombre() + " "+pago.getProveedor().getApellidos()
                  };
                tableModel.addRow(fila);
            }
            
            lblInfoGeneral.setText("No. pagos: "+pagos.size()+" .. Total: $"+decimalFormat.format(total)
                    + " .. Filtros disponibles: [Nombre proveedor][Fecha creacion][Fecha evento][Folio][No. Orden]"
                    + " .. Limite de resultados: "+parameter.getLimit());
       
       } catch (BusinessException be) {
           JOptionPane.showMessageDialog(this, be.getMessage(), 
                   ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
       }finally{
           Toolkit.getDefaultToolkit().beep();
       }
       

       
       
   }
   
   private void exportToExcel () {
   
    switch (IndexTabPanel.getEnum(indexTabPanelActive)) {
           case TAB_PANEL_GENERAL:
               utilityService.exportarExcel(tableViewOrdersProviders);
               break;
           case TAB_PANEL_DETAIL:
               utilityService.exportarExcel(tableViewOrdersProvidersDetail);
               break;
           case TAB_PANEL_PAYMENT_DETAIL:
               utilityService.exportarExcel(tablePaymentsProvider);
               break;
            case TAB_PANEL_SALDO_PROVEEDORES:
               utilityService.exportarExcel(tableSaldoProveedores);
               break;
           default:
               throw new AssertionError();
       }
   
   }
   
   private void search () {
              
       switch (IndexTabPanel.getEnum(indexTabPanelActive)) {
           case TAB_PANEL_GENERAL:
               fillTableTabPanelGeneral();
               break;
           case TAB_PANEL_DETAIL:
               fillTableTabPanelDetail();
               break;
           case TAB_PANEL_PAYMENT_DETAIL:
               fillTablePaymentsProvider();
               break;
           case TAB_PANEL_SALDO_PROVEEDORES:
               fillSaldoProveedores();
               break;
           default:
               throw new AssertionError();
       }
   }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel9 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jMenuItem3 = new javax.swing.JMenuItem();
        panelGeneral = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        txtSearchFolioRenta = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        cmbLimit = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtSearchByNameProvider = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtSearchOrderNumber = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtSearchInitialDate = new com.toedter.calendar.JDateChooser();
        txtSearchEndDate = new com.toedter.calendar.JDateChooser();
        txtSearchInitialEventDate = new com.toedter.calendar.JDateChooser();
        txtSearchEndEventDate = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        txtSearchInitialDateEnBodega = new com.toedter.calendar.JDateChooser();
        txtSearchEndDateEnBodega = new com.toedter.calendar.JDateChooser();
        panelButtons = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jbtnSearch = new javax.swing.JButton();
        jbtnAddOrder = new javax.swing.JButton();
        jbtnBitacoraProveedor = new javax.swing.JButton();
        btnCopyOrders = new javax.swing.JButton();
        panelTabs = new javax.swing.JPanel();
        lblInfoGeneral = new javax.swing.JLabel();
        tabGeneral = new javax.swing.JTabbedPane();
        tabPanelGeneral = new javax.swing.JPanel();
        tabPanelDetail = new javax.swing.JPanel();
        tabPanelPaymentsProvider = new javax.swing.JPanel();
        panelSadoProveedores = new javax.swing.JPanel();

        jLabel9.setText("jLabel9");

        jMenuItem3.setText("jMenuItem3");

        setClosable(true);

        txtSearchFolioRenta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearchFolioRenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchFolioRentaKeyPressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Nombre proveedor:");

        cmbStatus.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Fecha de creación:");

        cmbLimit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmbLimit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Status orden:");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Limite de ressultados:");

        txtSearchByNameProvider.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearchByNameProvider.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchByNameProviderKeyPressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Folio:");

        txtSearchOrderNumber.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearchOrderNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchOrderNumberKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Número de orden:");

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Fecha del evento:");

        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Fecha en bodega:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(91, 91, 91)
                                .addComponent(jLabel4))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtSearchInitialEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearchEndEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtSearchByNameProvider, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(7, 7, 7)
                                .addComponent(txtSearchFolioRenta, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(11, 11, 11)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                                    .addComponent(txtSearchOrderNumber))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtSearchInitialDate, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearchEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtSearchInitialDateEnBodega, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearchEndDateEnBodega, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtSearchByNameProvider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtSearchFolioRenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtSearchOrderNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtSearchInitialDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSearchEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtSearchInitialDateEnBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSearchEndDateEnBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearchInitialEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtSearchEndEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jButton1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/common/icons24/beneficios-money-24.png"))); // NOI18N
        jButton1.setText("Pagos");
        jButton1.setToolTipText("Pagos");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/common/icons24/searching-24.png"))); // NOI18N
        jButton3.setText("Ver ordenes");
        jButton3.setToolTipText("Detalle");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/common/icons24/truck-24.png"))); // NOI18N
        jButton4.setText("Proveedores");
        jButton4.setToolTipText("Proveedores");
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/common/icons24/pdf-24.png"))); // NOI18N
        jButton5.setToolTipText("Exportar PDF");
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/common/icons24/excel-24.png"))); // NOI18N
        jButton2.setToolTipText("Exportar Excel");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jbtnSearch.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jbtnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/common/icons24/search-24.png"))); // NOI18N
        jbtnSearch.setText("Buscar");
        jbtnSearch.setToolTipText("Buscar");
        jbtnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSearchActionPerformed(evt);
            }
        });

        jbtnAddOrder.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnAddOrder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/common/icons24/agregar-24.png"))); // NOI18N
        jbtnAddOrder.setText("Nueva orden");
        jbtnAddOrder.setToolTipText("Agregar nueva orden");
        jbtnAddOrder.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnAddOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddOrderActionPerformed(evt);
            }
        });

        jbtnBitacoraProveedor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnBitacoraProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/common/icons24/inventario-24.png"))); // NOI18N
        jbtnBitacoraProveedor.setText("Bitacora");
        jbtnBitacoraProveedor.setToolTipText("Bitacora seguimiento proveedor");
        jbtnBitacoraProveedor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnBitacoraProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnBitacoraProveedorActionPerformed(evt);
            }
        });

        btnCopyOrders.setIcon(new javax.swing.ImageIcon(getClass().getResource("/common/icons24/copy-24.png"))); // NOI18N
        btnCopyOrders.setText("Copiar ordenes");
        btnCopyOrders.setToolTipText("Copiar ordenes a un nuevo folio");
        btnCopyOrders.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCopyOrders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyOrdersActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelButtonsLayout = new javax.swing.GroupLayout(panelButtons);
        panelButtons.setLayout(panelButtonsLayout);
        panelButtonsLayout.setHorizontalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnAddOrder)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnBitacoraProveedor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCopyOrders)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelButtonsLayout.setVerticalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jbtnSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jbtnAddOrder, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelButtonsLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnBitacoraProveedor, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCopyOrders, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        lblInfoGeneral.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblInfoGeneral.setText("lblInfoGeneral");

        tabGeneral.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabGeneral.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabGeneral.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabGeneralMouseClicked(evt);
            }
        });

        tabPanelGeneral.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabPanelGeneralMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout tabPanelGeneralLayout = new javax.swing.GroupLayout(tabPanelGeneral);
        tabPanelGeneral.setLayout(tabPanelGeneralLayout);
        tabPanelGeneralLayout.setHorizontalGroup(
            tabPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 963, Short.MAX_VALUE)
        );
        tabPanelGeneralLayout.setVerticalGroup(
            tabPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );

        tabGeneral.addTab("General", tabPanelGeneral);

        javax.swing.GroupLayout tabPanelDetailLayout = new javax.swing.GroupLayout(tabPanelDetail);
        tabPanelDetail.setLayout(tabPanelDetailLayout);
        tabPanelDetailLayout.setHorizontalGroup(
            tabPanelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 963, Short.MAX_VALUE)
        );
        tabPanelDetailLayout.setVerticalGroup(
            tabPanelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );

        tabGeneral.addTab("Detalle", tabPanelDetail);

        javax.swing.GroupLayout tabPanelPaymentsProviderLayout = new javax.swing.GroupLayout(tabPanelPaymentsProvider);
        tabPanelPaymentsProvider.setLayout(tabPanelPaymentsProviderLayout);
        tabPanelPaymentsProviderLayout.setHorizontalGroup(
            tabPanelPaymentsProviderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 963, Short.MAX_VALUE)
        );
        tabPanelPaymentsProviderLayout.setVerticalGroup(
            tabPanelPaymentsProviderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );

        tabGeneral.addTab("Pagos a proveedores", tabPanelPaymentsProvider);

        javax.swing.GroupLayout panelSadoProveedoresLayout = new javax.swing.GroupLayout(panelSadoProveedores);
        panelSadoProveedores.setLayout(panelSadoProveedoresLayout);
        panelSadoProveedoresLayout.setHorizontalGroup(
            panelSadoProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 963, Short.MAX_VALUE)
        );
        panelSadoProveedoresLayout.setVerticalGroup(
            panelSadoProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );

        tabGeneral.addTab("Saldos proveedores", panelSadoProveedores);

        javax.swing.GroupLayout panelTabsLayout = new javax.swing.GroupLayout(panelTabs);
        panelTabs.setLayout(panelTabsLayout);
        panelTabsLayout.setHorizontalGroup(
            panelTabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTabsLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(panelTabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblInfoGeneral, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabGeneral)))
        );
        panelTabsLayout.setVerticalGroup(
            panelTabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTabsLayout.createSequentialGroup()
                .addComponent(lblInfoGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabGeneral))
        );

        javax.swing.GroupLayout panelGeneralLayout = new javax.swing.GroupLayout(panelGeneral);
        panelGeneral.setLayout(panelGeneralLayout);
        panelGeneralLayout.setHorizontalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelTabs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelGeneralLayout.setVerticalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTabs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchInitialDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchInitialDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialDateMouseClicked

    private void txtSearchInitialDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchInitialDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialDateKeyPressed

    private void txtSearchEndDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchEndDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndDateMouseClicked

    private void txtSearchEndDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchEndDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndDateKeyPressed

    private void jbtnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSearchActionPerformed
        // TODO add your handling code here:
        this.search();
    }//GEN-LAST:event_jbtnSearchActionPerformed

    private void txtSearchFolioRentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchFolioRentaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER ) {
            this.search();
        } 
    }//GEN-LAST:event_txtSearchFolioRentaKeyPressed

    private void txtSearchOrderNumberKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchOrderNumberKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10 ) {
            this.search();
        } 
    }//GEN-LAST:event_txtSearchOrderNumberKeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       showPaymentsProvidersForm();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        exportToExcel();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        mostrar_agregar_orden_proveedor();        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        showProviders();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        reportPDF();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void txtSearchInitialEventDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchInitialEventDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialEventDateMouseClicked

    private void txtSearchInitialEventDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchInitialEventDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialEventDateKeyPressed

    private void txtSearchEndEventDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchEndEventDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndEventDateMouseClicked

    private void txtSearchEndEventDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchEndEventDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndEventDateKeyPressed

    private void tabGeneralMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabGeneralMouseClicked

    }//GEN-LAST:event_tabGeneralMouseClicked

    private void tabPanelGeneralMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabPanelGeneralMouseClicked

    }//GEN-LAST:event_tabPanelGeneralMouseClicked

    private void txtSearchByNameProviderKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchByNameProviderKeyPressed
         if (evt.getKeyCode() == 10 ) {
            this.search();
        } 
    }//GEN-LAST:event_txtSearchByNameProviderKeyPressed

    private void btnCopyOrdersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyOrdersActionPerformed

        List<String> orders = new ArrayList<>();
        for (int i = 0; i < tableViewOrdersProviders.getRowCount(); i++) {
            if (Boolean.parseBoolean(tableViewOrdersProviders.
                getValueAt(i, TableViewOrdersProviders.Column.BOOLEAN.getNumber()).toString())) {
            orders.add(
                tableViewOrdersProviders.getValueAt(i, TableViewOrdersProviders.Column.ORDER_NUM.getNumber()).toString()
            );
        }
        }

        if (orders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona una o mas ordenes al proveedor.");
            return;
        }

        OrderProviderCopyParameter orderProviderCopyParameter = new OrderProviderCopyParameter();
        orderProviderCopyParameter.setUsuarioId(globalUser.getUsuarioId());
        orderProviderCopyParameter.setOrders(orders);

        OrderProviderCopyFormDialog orderProviderCopyFormDialog =
        new OrderProviderCopyFormDialog(null, true, orderProviderCopyParameter);

        Boolean success = orderProviderCopyFormDialog.showDialog();

        if (Boolean.TRUE.equals(success)) {
            this.search();
        }

    }//GEN-LAST:event_btnCopyOrdersActionPerformed

    private void showBitacoraProveedores () {
        
        Frame frame = JOptionPane.getFrameForComponent(this);

        String rentaId;
        String folio;
       try {
            rentaId = getValueIdBySelectedRow(ColumnToGetValue.RENTA_ID);
            folio = getValueIdBySelectedRow(ColumnToGetValue.FOLIO);
       } catch (BusinessException e) {
           LOGGER.error(e);
           JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
           return;
       }

        ProviderStatusBitacoraDialog win =
        new ProviderStatusBitacoraDialog(frame,true,Long.parseLong(rentaId), folio,datosGenerales,globalUser);
        win.setLocationRelativeTo(this);
        win.setVisible(true);
   }
    
    private void jbtnBitacoraProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnBitacoraProveedorActionPerformed
        showBitacoraProveedores();
    }//GEN-LAST:event_jbtnBitacoraProveedorActionPerformed

    private void jbtnAddOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddOrderActionPerformed
        addNewOrderProvider();
    }//GEN-LAST:event_jbtnAddOrderActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCopyOrders;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbLimit;
    private javax.swing.JComboBox cmbStatus;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbtnAddOrder;
    private javax.swing.JButton jbtnBitacoraProveedor;
    private javax.swing.JButton jbtnSearch;
    private javax.swing.JLabel lblInfoGeneral;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JPanel panelSadoProveedores;
    private javax.swing.JPanel panelTabs;
    private javax.swing.JTabbedPane tabGeneral;
    private javax.swing.JPanel tabPanelDetail;
    private javax.swing.JPanel tabPanelGeneral;
    private javax.swing.JPanel tabPanelPaymentsProvider;
    private javax.swing.JTextField txtSearchByNameProvider;
    private com.toedter.calendar.JDateChooser txtSearchEndDate;
    private com.toedter.calendar.JDateChooser txtSearchEndDateEnBodega;
    private com.toedter.calendar.JDateChooser txtSearchEndEventDate;
    private javax.swing.JTextField txtSearchFolioRenta;
    private com.toedter.calendar.JDateChooser txtSearchInitialDate;
    private com.toedter.calendar.JDateChooser txtSearchInitialDateEnBodega;
    private com.toedter.calendar.JDateChooser txtSearchInitialEventDate;
    private javax.swing.JTextField txtSearchOrderNumber;
    // End of variables declaration//GEN-END:variables

    private void setLocationRelativeTo(Object object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
