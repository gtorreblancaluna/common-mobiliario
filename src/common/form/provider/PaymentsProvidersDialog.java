
package common.form.provider;

import common.constants.ApplicationConstants;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.model.TipoAbono;
import common.model.Usuario;
import common.model.providers.DetalleOrdenProveedor;
import common.model.providers.OrdenProveedor;
import common.model.providers.PagosProveedor;
import common.model.providers.PaymentProviderFilter;
import common.model.providers.Proveedor;
import common.services.RentaService;
import common.services.UtilityService;
import common.services.providers.OrderProviderService;
import common.services.providers.ProvidersPaymentsService;
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


public class PaymentsProvidersDialog extends javax.swing.JDialog {


    private final UtilityService utilityService = UtilityService.getInstance();
    private final OrderProviderService orderService = OrderProviderService.getInstance();
    private final ProvidersPaymentsService paymentsService = ProvidersPaymentsService.getInstance();
    private final RentaService rentaService;
    private static final DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.00" );
    private OrdenProveedor ordenProveedorGlobal = new OrdenProveedor();
    private DetalleOrdenProveedor detalleOrdenProveedorGlobal = new DetalleOrdenProveedor();
    private Usuario globalUser;
    private boolean successfulChangesDetected = false;
    
    public static String g_idRenta=null;
    public static String g_idOrder=null;
    
    private static final int HEADER_ID = 0;
    private static final int HEADER_ID_ORDER_PROVIDER = 1;
    private static final int HEADER_USER = 2;
    private static final int HEADER_PAYMENT_TYPE = 3;
    private static final int HEADER_AMOUNT = 4;
    private static final int HEADER_COMMENT = 5;
    private static final int HEADER_CREATED = 6;
    private static final int HEADER_UPDATED = 7;
    private static final int HEADER_PROVEEDOR = 8;
    
    public PaymentsProvidersDialog(java.awt.Frame parent, boolean modal, String orderId, Usuario user) {
        super(parent, modal);
        globalUser = user;
        g_idOrder = orderId;
        initComponents();
        
         try{
            ordenProveedorGlobal = orderService.getOrderById(Long.parseLong(g_idOrder));            
        }catch(BusinessException e){
             JOptionPane.showMessageDialog(this, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);

        }
        
        rentaService = RentaService.getInstance();
        this.setTitle("Pagos al proveedor");
        this.setLocationRelativeTo(null);
        tableFormat();
        fillPaymentsType();
        getPayments();
        fillCmbProviders();
    }
    
    public Boolean showDialog () {
        setVisible(true);
        return successfulChangesDetected;
    }    
    
    private void fillCmbProviders () {
        
        cmbProviders.removeAllItems();
        try {
            List<Proveedor> providers = orderService.getAllProvidersGroupByOrderId(Long.parseLong(g_idOrder));
            
            cmbProviders.addItem(new Proveedor(0L,ApplicationConstants.CMB_SELECCIONE));
            
            for (Proveedor provider : providers) {
                cmbProviders.addItem(provider);
            }
        } catch (BusinessException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Erro al obtener los proveedores de la base de datos"
                            + "\n"
                            +"Detalle del error:\n"+ ex.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.WARNING_MESSAGE);            
            Logger.getLogger(PaymentsProvidersDialog.class.getName()).log(Level.SEVERE, null, ex);
        }        
      
    }
    
    private void fillTablePayments (List<PagosProveedor> pagos) {
        tableFormat();
        DefaultTableModel modelTable = (DefaultTableModel) tablePayments.getModel();

        for(PagosProveedor pago : pagos){

             Object fila[] = {                                          
                    pago.getId(),
                    pago.getOrdenProveedor().getId(),
                    pago.getUsuario().getNombre() +" "+pago.getUsuario().getApellidos(),
                    pago.getTipoAbono().getDescripcion(),
                    pago.getCantidad(),
                    pago.getComentario(),
                    pago.getCreado(),
                    pago.getActualizado(),
                    pago.getProveedor().getNombre() + " " + pago.getProveedor().getApellidos()
                };
                modelTable.addRow(fila);
        }

        this.lblInfoGeneral
                .setText("ORDEN ["+ordenProveedorGlobal.getId()+"]"
                        +", FOLIO [ "+ordenProveedorGlobal.getRenta().getFolio()+" ]"                               
                );
        this.total();
    }
    
    private void getPayments(){

         try{

             List<PagosProveedor> pagos = 
                     paymentsService.getAllProviderPaymentsByOrderId(Long.parseLong(g_idOrder));
             fillTablePayments(pagos);
             
         }catch(BusinessException e){
             JOptionPane.showMessageDialog(null, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);

         }        
      
    
    }
    
    public void total(){
        float total = 0f;
        for (int i = 0; i < tablePayments.getRowCount(); i++) {
           total += Float.parseFloat(tablePayments.getValueAt(i, HEADER_AMOUNT).toString());
        }
        
        this.lblTotal.setText("Total: "+decimalFormat.format(total));
    }
    
    public void fillPaymentsType() {
        
        try {

            List<TipoAbono> tiposAbonos = rentaService.getTipoAbonos();

            this.cmbTipoPago.removeAllItems();
            
            cmbTipoPago.addItem(new TipoAbono(0,ApplicationConstants.CMB_SELECCIONE));
            
            for(TipoAbono tipo : tiposAbonos){               
                   cmbTipoPago.addItem(tipo);
            }
            
        } catch(DataOriginException e){
            JOptionPane.showMessageDialog(null, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);

        }   
    }
    
    public void resetInputs(){
        txtCantidad.setText("");
        txtComentario.setText("");
        cmbTipoPago.setSelectedIndex(0);
    }
    
    private void deletePayment () {
        
        if (tablePayments.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String id = tablePayments.getValueAt(tablePayments.getSelectedRow(), HEADER_ID).toString();

        if(id == null || id.equals("")){
            JOptionPane.showMessageDialog(null, "Ocurrio un error, intenta de nuevo o reinicia la aplicacion ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if(JOptionPane.showOptionDialog(null, "Se eliminará de la base de datos,  \u00BFContinuar? " ,"Confirme eliminacion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si") != 0)
            return;
        
        try {
            paymentsService.delete(Long.parseLong(id));
            tableFormat();
            getPayments();
            successfulChangesDetected = true;
        } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    private void addPayment(){
        
        StringBuilder message = new StringBuilder();
        Float cantidad = 0f;
        try{
            cantidad = Float.parseFloat(txtCantidad.getText());
        }catch(NumberFormatException e){
            message.append("Solo se permiten números\n");
        }
        
        if(this.cmbTipoPago.getSelectedItem().equals(ApplicationConstants.CMB_SELECCIONE)){
            message.append("Selecciona el tipo de pago\n");
        }
        
        Proveedor proveedor = (Proveedor) cmbProviders.getModel().getSelectedItem();
        if (proveedor.getId().equals(0L)) {
            message.append("Seleccione un proveedor.\n");
        }
        
        if(!message.toString().equals("")){
            JOptionPane.showMessageDialog(this,message.toString(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }        
        
        PagosProveedor pagosProveedor = new PagosProveedor();
                       
        TipoAbono tipoAbono = (TipoAbono) this.cmbTipoPago.getModel().getSelectedItem();
        
         pagosProveedor.setTipoAbono(tipoAbono);
         pagosProveedor.setOrdenProveedor(ordenProveedorGlobal);
         pagosProveedor.setUsuario(globalUser);
         pagosProveedor.setCantidad(cantidad);
         pagosProveedor.setComentario(txtComentario.getText());
         pagosProveedor.setProveedor(proveedor);
         
         try{
            paymentsService.addPayment(pagosProveedor);
            tableFormat();
            getPayments();
            successfulChangesDetected = true;
         }catch(BusinessException e){
            JOptionPane.showMessageDialog(null, e.getMessage()+"\n"+e, ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            return;
         }
         
         resetInputs();
         
    }
   
   public void tableFormat() {
        Object[][] data = {{"","","","","","","","",""}};
        String[] columnNames = {                        
                        "Id",
                        "Id orden proveedor", 
                        "Usuario",
                        "Tipo abono", 
                        "Cantidad",                        
                        "Comentario",
                        "Creado",
                        "Actualizado",
                        "Proveedor"
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        this.tablePayments.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tablePayments.setRowSorter(ordenarTabla);

        int[] anchos = {20,20,80,40,40, 80,100,100,100};

        for (int inn = 0; inn < tablePayments.getColumnCount(); inn++) {
            tablePayments.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tablePayments.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        tablePayments.getColumnModel().getColumn(HEADER_ID).setMaxWidth(0);
        tablePayments.getColumnModel().getColumn(HEADER_ID).setMinWidth(0);
        tablePayments.getColumnModel().getColumn(HEADER_ID).setPreferredWidth(0);
        
        tablePayments.getColumnModel().getColumn(HEADER_ID_ORDER_PROVIDER).setMaxWidth(0);
        tablePayments.getColumnModel().getColumn(HEADER_ID_ORDER_PROVIDER).setMinWidth(0);
        tablePayments.getColumnModel().getColumn(HEADER_ID_ORDER_PROVIDER).setPreferredWidth(0);
     
        tablePayments.getColumnModel().getColumn(HEADER_USER).setCellRenderer(centrar);
        tablePayments.getColumnModel().getColumn(HEADER_AMOUNT).setCellRenderer(centrar);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtCantidad = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtComentario = new javax.swing.JTextField();
        btnAgregar = new javax.swing.JButton();
        cmbTipoPago = new javax.swing.JComboBox<>();
        jLabel47 = new javax.swing.JLabel();
        lblInfoGeneral = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        btnDelete = new javax.swing.JButton();
        cmbProviders = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btnFilterPaymentByProvider = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablePayments = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        txtCantidad.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Cantidad:");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Comentario:");

        txtComentario.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        btnAgregar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnAgregar.setText("(+) Agregar");
        btnAgregar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        cmbTipoPago.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmbTipoPago.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel47.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel47.setText("Tipo de pago:");

        lblInfoGeneral.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lblInfoGeneral.setText("lblInfoGeneral");

        lblTotal.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblTotal.setText("lblTotal");

        btnDelete.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnDelete.setText("(-) Eliminar");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Proveedor:");

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setText("?");
        jLabel1.setToolTipText("Información");
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        btnFilterPaymentByProvider.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnFilterPaymentByProvider.setText("Filtrar pagos por proveedor");
        btnFilterPaymentByProvider.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnFilterPaymentByProvider.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterPaymentByProviderActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFilterPaymentByProvider))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(txtComentario, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)))
                            .addComponent(lblInfoGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel47)))
                    .addComponent(cmbProviders, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblInfoGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel47)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtComentario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbProviders, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFilterPaymentByProvider, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        tablePayments.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablePayments);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // TODO add your handling code here:

        addPayment();
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deletePayment();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        JOptionPane.showMessageDialog(
                this, "Solo se muestran los proveedores relacionados con la orden "
                        + "["+ordenProveedorGlobal.getId()+"]", "Info", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jLabel1MouseClicked

    private void btnFilterPaymentByProviderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterPaymentByProviderActionPerformed
       
       try {
           
           Proveedor proveedor = (Proveedor) cmbProviders.getModel().getSelectedItem();
            if (proveedor.getId().equals(0L)) {
                JOptionPane.showMessageDialog(this, 
                        "Selecciona un proveedor.", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
                return;
            }
           
            PaymentProviderFilter filter = new PaymentProviderFilter();
            filter.setOrderId(ordenProveedorGlobal.getId().toString());
            filter.setLimit(10_000);
            filter.setProviderId(proveedor.getId().toString());
            List<PagosProveedor> payments = paymentsService.getByFilter(filter);
            fillTablePayments(payments);
       } catch (BusinessException be) {
           JOptionPane.showMessageDialog(this, be, ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
       }
    }//GEN-LAST:event_btnFilterPaymentByProviderActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PaymentsProvidersDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PaymentsProvidersDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PaymentsProvidersDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PaymentsProvidersDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PaymentsProvidersDialog dialog = new PaymentsProvidersDialog(new javax.swing.JFrame(), true,null,null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnFilterPaymentByProvider;
    private javax.swing.JComboBox<Proveedor> cmbProviders;
    private javax.swing.JComboBox<TipoAbono> cmbTipoPago;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblInfoGeneral;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tablePayments;
    public static javax.swing.JTextField txtCantidad;
    public static javax.swing.JTextField txtComentario;
    // End of variables declaration//GEN-END:variables
}
