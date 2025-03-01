package common.form.items;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import common.model.Articulo;
import common.tables.TableItems;
import common.utilities.UtilityCommon;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class AgregarArticuloDisponibilidadDialog extends javax.swing.JDialog {
    
    private List<Articulo> items = new ArrayList<>();
    private static final DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.00" );
    private List<Integer> itemsSelected = new ArrayList<>();
    private final TableItems tableItems;
    private static final String SELECT_ALL = "Seleccionar todo";
    private static final String UN_SELECT_ALL = "Quitar seleccion";
    
   
    public AgregarArticuloDisponibilidadDialog(java.awt.Frame parent, boolean modal, List<Articulo> articulos) {
        super(parent, modal);
        initComponents();        
        tableItems = new TableItems();        
        txtSearch.requestFocus();        
        this.setLocationRelativeTo(null);
        this.setTitle("Buscar articulo ");    
        this.items = articulos;
        fillTable(articulos);
        UtilityCommon.addJtableToPane(400, 600, this.tablePane, tableItems);
        UtilityCommon.addEscapeListener(this);
        btnSelectAll.setText(SELECT_ALL);
        lblInfo.setText("");
        
        tableItems.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if(evt.getKeyCode() == KeyEvent.VK_ENTER && tableItems.getRowCount() > 0
                        && tableItems.getSelectedRow() > 0) {
                    returnItemSelected();
                } else if(evt.getKeyCode() == KeyEvent.VK_SPACE && tableItems.getRowCount() > 0
                        && tableItems.getSelectedRow() > 0) {
                    checkWhenKeySpaceIsPressed();
                } else if (evt.getKeyCode() == KeyEvent.VK_UP 
                        && tableItems.getSelectedRow() == 0) {
                    txtSearch.requestFocus();
                    txtSearch.selectAll();
                }
                numbersOfRowsSelectedAndSetLblInfo();
            }
        });
        
        tableItems.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {                

                  numbersOfRowsSelectedAndSetLblInfo();
                if (evt.getClickCount() == 2) {                   
                    returnItemSelectedInTable();
                }
            }
        });

    }
    
    private void checkWhenKeySpaceIsPressed () {
        // select checkbok
        Boolean setValueTo = !Boolean.parseBoolean(
                String.valueOf(
                        tableItems.getValueAt(tableItems.getSelectedRow(), TableItems.Column.BOOLEAN.getNumber()))
        );
        tableItems.setValueAt(setValueTo, tableItems.getSelectedRow(),TableItems.Column.BOOLEAN.getNumber());
    }
    
    private void returnItemSelectedInTable () {        
        
        itemsSelected.add(Integer.valueOf(
                String.valueOf(tableItems.getValueAt(tableItems.getSelectedRow(),TableItems.Column.ID.getNumber()))));

        setVisible(false);
        this.dispose();
    }
    
    private void returnFirstItemInTable () {
        
        itemsSelected.add(Integer.valueOf(
                String.valueOf(tableItems.getValueAt(0,TableItems.Column.ID.getNumber()))));

        setVisible(false);
        this.dispose();
    }
    
    private void returnItemSelected () { 
        
        for (int i=0; i < tableItems.getRowCount(); i ++) {
            if (Boolean.parseBoolean(
                    String.valueOf(tableItems.getValueAt(i,TableItems.Column.BOOLEAN.getNumber())))) {
                        itemsSelected.add(Integer.valueOf(
                                String.valueOf(tableItems.getValueAt(i,TableItems.Column.ID.getNumber())))
                );
            }
        }
        
        if (itemsSelected.isEmpty()) {
            itemsSelected = null;
        }
        
        setVisible(false);
        this.dispose();
    }
    
    private void fillTable(List<Articulo> items) {
        
        tableItems.format();
        
        DefaultTableModel tableModel = (DefaultTableModel) tableItems.getModel();
        
        items.forEach(articulo -> {
            Object row[] = {  
                false,
                articulo.getArticuloId(),
                articulo.getCodigo(),
                articulo.getCategoria().getDescripcion(),
                articulo.getDescripcion(),
                articulo.getColor().getColor(),
                decimalFormat.format(articulo.getPrecioRenta()),
                decimalFormat.format(articulo.getStock()),
                articulo.getFechaIngreso(),
                articulo.getFechaUltimaModificacion()
              };
              tableModel.addRow(row);
        });
    }
    
    public List<Integer> showDialog () {
        setVisible(true);
        return itemsSelected;
    }
    


     public void tabla_articulos_like() {         
        
        List<Articulo> itemsFiltered = 
                UtilityCommon.applyFilterToItems(items,txtSearch.getText());
        
        fillTable(itemsFiltered);
        
        btnSelectAll.setText(SELECT_ALL);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lblEncontrados1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        tablePane = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        btnSelectAll = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();

        setTitle("Colores");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        lblEncontrados1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblEncontrados1.setText("Buscar artículos por código, descripción o color.");

        txtSearch.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchMouseClicked(evt);
            }
        });
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout tablePaneLayout = new javax.swing.GroupLayout(tablePane);
        tablePane.setLayout(tablePaneLayout);
        tablePaneLayout.setHorizontalGroup(
            tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        tablePaneLayout.setVerticalGroup(
            tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 442, Short.MAX_VALUE)
        );

        jButton1.setText("Agregar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnSelectAll.setText("Seleccionar todo");
        btnSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectAllActionPerformed(evt);
            }
        });

        lblInfo.setText("lblInfo");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tablePane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblEncontrados1, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSelectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblEncontrados1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(btnSelectAll)
                    .addComponent(lblInfo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablePane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        if(evt.getKeyCode() == KeyEvent.VK_ENTER && tableItems.getRowCount() > 0) {            
            returnFirstItemInTable();
        } else if (evt.getKeyCode() == KeyEvent.VK_DOWN && tableItems.getRowCount() > 0) {
            tableItems.requestFocus();
            tableItems.changeSelection(0,0,false, false);
        } else {
           tabla_articulos_like();
        }
    }//GEN-LAST:event_txtSearchKeyReleased

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void txtSearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchMouseClicked

    private void numbersOfRowsSelectedAndSetLblInfo () {
        int count = 0;
        
        for (int i=0; i < tableItems.getRowCount(); i ++) {
            if (Boolean.parseBoolean(
                    String.valueOf(tableItems.getValueAt(i,TableItems.Column.BOOLEAN.getNumber())))) {
                    count ++;
            }
        }
        if (count > 0) {
            lblInfo.setText("Artículos seleccionados: "+count);
        } else {
            lblInfo.setText("");
        }
        
    }
    
    private void btnSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectAllActionPerformed
        
        if (btnSelectAll.getText().equals(SELECT_ALL)) {
            UtilityCommon.selectAllCheckboxInTable(tableItems, 
                TableItems.Column.BOOLEAN.getNumber(), 
                true);
            btnSelectAll.setText(UN_SELECT_ALL);
        } else {
            UtilityCommon.selectAllCheckboxInTable(tableItems, 
                TableItems.Column.BOOLEAN.getNumber(), 
                false);
            btnSelectAll.setText(SELECT_ALL);
        }
        
        numbersOfRowsSelectedAndSetLblInfo();
        
    }//GEN-LAST:event_btnSelectAllActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        returnItemSelected();
    }//GEN-LAST:event_jButton1ActionPerformed


    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AgregarArticuloDisponibilidadDialog dialog = new AgregarArticuloDisponibilidadDialog(new java.awt.Frame(), true, new ArrayList<>());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSelectAll;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblEncontrados1;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JPanel tablePane;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
