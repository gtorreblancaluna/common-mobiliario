package common.tables;

import common.constants.ApplicationConstants;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class TableViewOrdersProviders extends JTable {

    public TableViewOrdersProviders() {       
        this.setFont(new Font( ApplicationConstants.ARIAL , Font.PLAIN, 12 ));
        format();        
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
        
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setMaxWidth(0);
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setMinWidth(0);
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setPreferredWidth(0);
     
        this.getColumnModel().getColumn(Column.ORDER_NUM.getNumber()).setCellRenderer(centrar);
        this.getColumnModel().getColumn(Column.FOLIO_RENTA.getNumber()).setCellRenderer(centrar);
        this.getColumnModel().getColumn(Column.SUB_TOTAL.getNumber()).setCellRenderer(right);
        this.getColumnModel().getColumn(Column.PAYMENTS.getNumber()).setCellRenderer(right);
        this.getColumnModel().getColumn(Column.TOTAL.getNumber()).setCellRenderer(right);
    }    
    
    public enum Column{
       
       BOOLEAN(0,"",10,Boolean.class, true),
       ORDER_NUM(1,"No orden",20,String.class, false),
       FOLIO_RENTA(2,"Folio renta",20,String.class, false),
       USER(3,"Usuario",80,String.class, false),
       SUPPLIER(4,"Proveedor",40,String.class, false),
       STATUS(5,"Status",40,String.class, false),
       CREATED(6,"Creado",80,String.class, false),
       UPDATED(7,"Actualizado",90,String.class, false),
       COMMENT(8,"Comentario",100,String.class, false),
       RENTA_ID(9,"id_renta",20,String.class, false),
       SUB_TOTAL(10,"Subtotal",80,String.class, false),
       PAYMENTS(11,"Pagos",80,String.class, false),
       TOTAL(12,"Total",80,String.class, false),
       EVENT_DATE(13,"Fecha evento",80,String.class, false);
       
       private final int number;
       private final String description;
       private final int size;
       private final Class clazzType;
       private final Boolean isEditable;

        Column(int number, String description, int size,Class clazzType, Boolean isEditable) {
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
