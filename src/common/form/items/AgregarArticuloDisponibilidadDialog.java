package common.form.items;

import common.constants.ApplicationConstants;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import common.model.Articulo;
import common.utilities.UtilityCommon;
import java.awt.event.KeyEvent;

public class AgregarArticuloDisponibilidadDialog extends java.awt.Dialog {

    
    private List<Articulo> items = new ArrayList<>();
    private static final DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.00" );
    private String itemId;
    
   
    public AgregarArticuloDisponibilidadDialog(java.awt.Frame parent, boolean modal, List<Articulo> articulos) {
        super(parent, modal);
        initComponents();       
        txtSearch.requestFocus();        
        this.setLocationRelativeTo(null);
        this.setTitle("Buscar articulo ");
        formato_tabla();     
        this.items = articulos;
        fillTable(articulos);      
        

    }
    
    private void fillTable(List<Articulo> items) {
        DefaultTableModel tableModel = (DefaultTableModel) itemsTable.getModel();
        
        items.forEach(articulo -> {
            Object row[] = {                                          
                articulo.getArticuloId(),
                articulo.getCodigo(),
                articulo.getCategoria().getDescripcion(),
                articulo.getDescripcion(),
                articulo.getColor().getColor(),
                decimalFormat.format(articulo.getPrecioRenta()),
                decimalFormat.format(articulo.getStock())
              };
              tableModel.addRow(row);
        });
    }
    
    public String showDialog () {
        setVisible(true);
        return itemId;
    }
    
    private void formato_tabla() {
        
        Object[][] data = {{ApplicationConstants.BLANK_SPACE, 
            ApplicationConstants.BLANK_SPACE, 
            ApplicationConstants.BLANK_SPACE, 
            ApplicationConstants.BLANK_SPACE, 
            ApplicationConstants.BLANK_SPACE,
            ApplicationConstants.BLANK_SPACE}};
        String[] columnNames = {"id","Código", "Categoría", "Descripción", "Color", "P.Unitario", "Stock"};       
        DefaultTableModel TableModel = new DefaultTableModel(data, columnNames);
        itemsTable.setModel(TableModel);

        int[] anchos = {10,60, 120, 250, 100, 90, 90};

        for (int inn = 0; inn < itemsTable.getColumnCount(); inn++) {
            itemsTable.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            DefaultTableModel temp = (DefaultTableModel) itemsTable.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        
        itemsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        itemsTable.getColumnModel().getColumn(0).setMinWidth(0);
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(0);      
        itemsTable.getColumnModel().getColumn(2).setCellRenderer(centrar);
        itemsTable.getColumnModel().getColumn(3).setCellRenderer(centrar);
        itemsTable.getColumnModel().getColumn(4).setCellRenderer(right);
        itemsTable.getColumnModel().getColumn(5).setCellRenderer(right);

    }

     public void tabla_articulos_like() {
         
        formato_tabla();
        String textToSearch = UtilityCommon.removeAccents(txtSearch.getText().toLowerCase().trim());
        List<Articulo> itemsFiltered = items.stream()
                .filter(articulo -> Objects.nonNull(articulo))
                .filter(articulo -> Objects.nonNull(articulo.getCodigo()))
                .filter(articulo -> Objects.nonNull(articulo.getDescripcion()))
                .filter(articulo -> Objects.nonNull(articulo.getColor()))
                .filter(articulo -> (
                            UtilityCommon.removeAccents(
                                    articulo.getDescripcion().trim().toLowerCase() + " " + 
                                            articulo.getColor().getColor().trim().toLowerCase()
                            )).contains(textToSearch)
                            || UtilityCommon.removeAccents(articulo.getCodigo().trim().toLowerCase())
                                    .contains(textToSearch))
                .collect(Collectors.toList());
        
        fillTable(itemsFiltered);

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
        jScrollPane1 = new javax.swing.JScrollPane();
        itemsTable = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};

        setTitle("Colores");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        lblEncontrados1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblEncontrados1.setText("Buscar artículos por código, descripción o color.");

        txtSearch.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
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

        itemsTable.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        itemsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        itemsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                itemsTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(itemsTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEncontrados1, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblEncontrados1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel2, java.awt.BorderLayout.CENTER);

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
        if(evt.getKeyCode() == KeyEvent.VK_ENTER && itemsTable.getRowCount() > 0) {
            this.itemId = itemsTable.getValueAt(0, 0).toString();
            setVisible(false);
            this.dispose();
        } else {
           tabla_articulos_like();
        }
    }//GEN-LAST:event_txtSearchKeyReleased

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void itemsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_itemsTableMouseClicked
         if (evt.getClickCount() == 2) {
            String idArticulo = itemsTable.getValueAt(itemsTable.getSelectedRow(), 0).toString();
            if (idArticulo == null || idArticulo.equals("")) {
                JOptionPane.showMessageDialog(null, "Ocurrio un error inesperado, porfavor intentalo de nuevo, si el problema sigue, reinicia el sistema :P ", "Error", JOptionPane.INFORMATION_MESSAGE);
                Toolkit.getDefaultToolkit().beep();
             }else{
               this.itemId = idArticulo;
               setVisible(false);              
               this.dispose();
                 
            }
         }
    }//GEN-LAST:event_itemsTableMouseClicked


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
    private javax.swing.JTable itemsTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEncontrados1;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
