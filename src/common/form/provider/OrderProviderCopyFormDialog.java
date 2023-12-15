package common.form.provider;

import common.constants.ApplicationConstants;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.exceptions.NoDataFoundException;
import common.model.Renta;
import common.model.providers.OrdenProveedor;
import common.model.providers.OrderProviderCopyParameter;
import common.model.providers.ParameterOrderProvider;
import common.services.RentaService;
import common.services.providers.OrderProviderService;
import common.tables.TableViewOrdersProviders;
import common.utilities.UtilityCommon;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

public class OrderProviderCopyFormDialog extends javax.swing.JDialog {
    
    private final OrderProviderCopyParameter orderProviderCopyParameter;
    private final TableViewOrdersProviders tableViewOrdersProviders;
    private static final DecimalFormat decimalFormat = 
            new DecimalFormat( ApplicationConstants.DECIMAL_FORMAT_SHORT );
    private final OrderProviderService orderService;
    private final Boolean successfulChangesDetected;
    private List<OrdenProveedor> initialOrders;
    private String folioToCopy;
    private final RentaService rentaService;

    public OrderProviderCopyFormDialog(java.awt.Frame parent, boolean modal, 
            OrderProviderCopyParameter orderProviderCopyParameter) {
        super(parent, modal);
        initComponents();
        this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        this.setTitle("Copiar ordenes de proveedor.");
        this.orderProviderCopyParameter = orderProviderCopyParameter;
        tableViewOrdersProviders = new TableViewOrdersProviders();
        initialOrders = new ArrayList<>();
        orderService = OrderProviderService.getInstance();
        rentaService = RentaService.getInstance();
        UtilityCommon.addJtableToPane(940, 940, panelTable, tableViewOrdersProviders);
        this.txtFolio.requestFocus();
        getOrders();
        addEscapeListener();
        successfulChangesDetected = false;
        addKeyListener();
        this.btnSave.setEnabled(false);
        lblInfo.setText("");
        
    }
    
    private void addKeyListener () {
        txtFolio.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                
                if(evt.getKeyCode() == KeyEvent.VK_ENTER){
                    getByFolio();
                } else {
                    txtFolio.setText(
                            UtilityCommon.onlyNumbers(txtFolio.getText())
                    );
                }
            }
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if(evt.getKeyCode() == KeyEvent.VK_ENTER){
                    getByFolio();
                } else {
                    txtFolio.setText(
                            UtilityCommon.onlyNumbers(txtFolio.getText())
                    );
                }
            }
        });
    }
    
    public Boolean showDialog () {
        setVisible(true);
        return successfulChangesDetected;
    }
    
    // close dialog when esc is pressed.
    private void addEscapeListener() {
        ActionListener escListener = (ActionEvent e) -> {
            setVisible(false);
            dispose();
        };

        this.getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

    }
    
    private void copyOrders () {
        
        try {
            
            for(OrdenProveedor orden : initialOrders){                
                orden.setStatus(ApplicationConstants.STATUS_ORDER_PROVIDER_CANCELLED);
                orderService.updateOrder(orden);                
            }
            
            for(OrdenProveedor orden : initialOrders){
                orden.setStatus(ApplicationConstants.STATUS_ORDER_PROVIDER_ORDER);
                orden.setRenta(
                        new Renta(Integer.parseInt(folioToCopy)));
                orderService.saveOrder(orden);                
            }
            
            ParameterOrderProvider parameter = new ParameterOrderProvider();
            parameter.setLimit(10000);
            parameter.setFolioRenta(Integer.parseInt(folioToCopy));
            List<OrdenProveedor> list = orderService.getOrdersByParameters(parameter);
            tableViewOrdersProviders.format();
            fillTable(list);
            this.btnSave.setEnabled(false);
            lblInfo.setText("Se copiaron las ordenes con exito.");
            
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            this.btnSave.setEnabled(false);
        }
        
    }
    
    private void getByFolio () {        
        
        if (txtFolio.getText().trim().equals(orderProviderCopyParameter.getCurrentFolio())) {
            JOptionPane.showMessageDialog(this, "El folio es el mismo al actual, ingresa un folio diferente para continuar.", 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            this.btnSave.setEnabled(false);
            return;
        }
        
        try {
            
            Renta renta = 
                    rentaService.getByFolio(Integer.parseInt(txtFolio.getText().trim()));
            
            if (renta == null) {
                lblInfo.setText("No se encontro el folio.");
                this.btnSave.setEnabled(false);
                return;
            }
            
            ParameterOrderProvider parameter = new ParameterOrderProvider();
            parameter.setLimit(10000);
            parameter.setFolioRenta(Integer.parseInt(txtFolio.getText().trim()));
            List<OrdenProveedor> list = orderService.getOrdersByParameters(parameter);
            tableViewOrdersProviders.format();
            fillTable(initialOrders);
            fillTable(list);
            this.btnSave.setEnabled(true);
            this.folioToCopy = txtFolio.getText().trim();
            lblInfo.setText("Se obtubieron las ordenes al proveedor con éxito para el folio: "+folioToCopy);
        } catch (NumberFormatException numberFormatException) {
            JOptionPane.showMessageDialog(this, "Ingresa un numero valido", 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            this.btnSave.setEnabled(false);
            lblInfo.setText("Ops ocurrio un error.");
        } catch (NoDataFoundException noDataFoundException) {
            this.btnSave.setEnabled(true);
            lblInfo.setText("No se obtuvieron ordenes al proveedor para el folio: "+txtFolio.getText().trim());
        } catch (DataOriginException | BusinessException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            this.btnSave.setEnabled(false);
            lblInfo.setText("Ops ocurrio un error.");
        }
    }
    
    private void getOrders () {
        try {
            ParameterOrderProvider parameter = new ParameterOrderProvider();
            parameter.setLimit(10000);
            parameter.setOrders(this.orderProviderCopyParameter.getOrders());
            initialOrders = orderService.getOrdersByParameters(parameter);
            tableViewOrdersProviders.format();
            fillTable(initialOrders);
        } catch (BusinessException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void fillTable (List<OrdenProveedor> orders) {
       
       DefaultTableModel tableModel = (DefaultTableModel) tableViewOrdersProviders.getModel();
       for(OrdenProveedor orden : orders){      

            Object fila[] = {
                false,
                orden.getId(),
                orden.getRenta().getFolio(),
                orden.getUsuario().getNombre()+" "+orden.getUsuario().getApellidos(),
                orden.getProveedor().getNombre()+" "+orden.getProveedor().getApellidos(),
                orden.getStatusDescription(),
                orden.getCreado(),
                orden.getActualizado(),
                orden.getComentario(),
                orden.getRenta().getRentaId(),             
                decimalFormat.format(orden.getTotal()),
                orden.getAbonos() > 0 ? decimalFormat.format(orden.getAbonos()) : "",
                decimalFormat.format((orden.getTotal() - orden.getAbonos())),
                orden.getRenta().getFechaEvento()
              };
              tableModel.addRow(fila);

       }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        panelTable = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        txtFolio = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnSearchByFolio = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout panelTableLayout = new javax.swing.GroupLayout(panelTable);
        panelTable.setLayout(panelTableLayout);
        panelTableLayout.setHorizontalGroup(
            panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelTableLayout.setVerticalGroup(
            panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 341, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        txtFolio.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Mandar ordenes al folio:");

        btnSearchByFolio.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnSearchByFolio.setText("Buscar folio");
        btnSearchByFolio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchByFolioActionPerformed(evt);
            }
        });

        btnSave.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnSave.setText("Guardar");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnCancel.setText("Cancelar");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        lblInfo.setText("jLabel2");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFolio, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearchByFolio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave)
                .addContainerGap(522, Short.MAX_VALUE))
            .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFolio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearchByFolio)
                    .addComponent(btnCancel)
                    .addComponent(btnSave))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInfo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSearchByFolioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchByFolioActionPerformed
        getByFolio();
    }//GEN-LAST:event_btnSearchByFolioActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        copyOrders();
    }//GEN-LAST:event_btnSaveActionPerformed

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
            java.util.logging.Logger.getLogger(OrderProviderCopyFormDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OrderProviderCopyFormDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OrderProviderCopyFormDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OrderProviderCopyFormDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                OrderProviderCopyFormDialog dialog = new OrderProviderCopyFormDialog(new javax.swing.JFrame(), true, null);
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
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearchByFolio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JPanel panelTable;
    private javax.swing.JTextField txtFolio;
    // End of variables declaration//GEN-END:variables
}
