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

public class TablePaymentsProvider extends JTable {

    public TablePaymentsProvider() {
        
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
        
        this.getColumnModel().getColumn(Column.PROVEEDOR_ID.getNumber()).setMaxWidth(0);
        this.getColumnModel().getColumn(Column.PROVEEDOR_ID.getNumber()).setMinWidth(0);
        this.getColumnModel().getColumn(Column.PROVEEDOR_ID.getNumber()).setPreferredWidth(0);
        
        this.getColumnModel().getColumn(Column.USER_ID.getNumber()).setMaxWidth(0);
        this.getColumnModel().getColumn(Column.USER_ID.getNumber()).setMinWidth(0);
        this.getColumnModel().getColumn(Column.USER_ID.getNumber()).setPreferredWidth(0);
        
        this.getColumnModel().getColumn(Column.AMOUNT.getNumber()).setCellRenderer(right);
    }
    
    
    public enum Column{
        
       ID(0,"id",10,String.class, false),
       RENTA_ID(1,"renta_id",10,String.class, false),
       ORDER_ID(2,"No. Orden",10,String.class, false),
       FOLIO(3,"Folio",10,String.class, false),
       USER_ID(4,"user_id",10,String.class, false),
       USER_NAME(5,"Usuario",20,String.class, false),
       PAYMENT_TYPE(6,"Tipo pago",60,String.class, false),
       AMOUNT(7,"Cantidad",40,String.class, false),
       DESCRIPTION(8,"Descripción",140,String.class, false),
       CREATED_AT(9,"Creado",60,String.class, false),
       UPDATED_AT(10,"Actualizado",60,String.class, false),
       PROVEEDOR_ID(11,"provider_id",40,String.class, false),
       PROVEEDOR_NAME(12,"Proveedor",160,String.class, false);
       
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
