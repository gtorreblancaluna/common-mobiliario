package common.tables;

import common.constants.ApplicationConstants;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class TableSaldoProveedores extends JTable {

    public TableSaldoProveedores() {
        
        this.setFont(new Font( ApplicationConstants.ARIAL , Font.PLAIN, 12 ));
        format();
        
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    
    
    public void format () {
        
        setModel(
                new DefaultTableModel(Column.getColumnNames(), 0){
                @Override
                public Class getColumnClass(int column) {
                    return Column.values()[column].getClazzType();
                }

                @Override
                public boolean isCellEditable (int row, int column) {
                    return Column.values()[column].getIsEditable();
                }
        });
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(this.getModel()); 
        this.setRowSorter(ordenarTabla);
        
        
        for (Column column : Column.values()) {
            this.getColumnModel()
                    .getColumn(column.getNumber())
                    .setPreferredWidth(column.getSize());
        }
        
        try {
            DefaultTableModel temp = (DefaultTableModel) this.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        
        
        this.getColumnModel().getColumn(Column.ID.getNumber()).setMaxWidth(0);
        this.getColumnModel().getColumn(Column.ID.getNumber()).setMinWidth(0);
        this.getColumnModel().getColumn(Column.ID.getNumber()).setPreferredWidth(0);
        
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setMaxWidth(0);
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setMinWidth(0);
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setPreferredWidth(0);
        
        this.getColumnModel().getColumn(Column.COUNT_ITEMS.getNumber()).setCellRenderer(centrar);
        this.getColumnModel().getColumn(Column.COUNT_PAYMENTS.getNumber()).setCellRenderer(centrar);
                
        this.getColumnModel().getColumn(Column.SUB_TOTAL.getNumber()).setCellRenderer(right);
        this.getColumnModel().getColumn(Column.PAYMENTS.getNumber()).setCellRenderer(right);
        this.getColumnModel().getColumn(Column.BALANCE.getNumber()).setCellRenderer(right);
    }
    
    
    public enum Column{
        
       ID(0,"id",10,String.class, false),
       RENTA_ID(1,"renta_id",10,String.class, false),
       ORDER_ID(2,"No. Orden",10,String.class, false),
       FOLIO(3,"Folio",10,String.class, false),
       PROVEEDOR_NAME(4,"Proveedor",140,String.class, false),
       COUNT_ITEMS(5,"Número de articulos",40,String.class, false),
       COUNT_PAYMENTS(6,"Número de pagos",40,String.class, false),
       SUB_TOTAL(7,"Sub total",40,String.class, false),
       PAYMENTS(8,"Pagos",40,String.class, false),
       BALANCE(9,"Total",40,String.class, false);
       
       private final int number;
       private final String description;
       private final int size;
       private final Class clazzType;
       private final Boolean isEditable;

        Column(int number, String description, int size, Class clazzType, Boolean isEditable) {
            this.number = number;
            this.description = description;
            this.size = size;
            this.clazzType = clazzType;
            this.isEditable = isEditable;
        }
        public int getSize () {
            return this.size;
        }
        public int getNumber () {
            return this.number;
        }
        
        public String getDescription () {
            return this.description;
        }
        
        public Class getClazzType() {
            return clazzType;
        }

        public Boolean getIsEditable() {
            return isEditable;
        }
        
        public static String[] getColumnNames () {
            List<String> columnNames = new ArrayList<>();
            for (Column column : Column.values()) {
                columnNames.add(column.getDescription());
            }
            return columnNames.toArray(new String[0]);
        }
        
    }
    
    
}
