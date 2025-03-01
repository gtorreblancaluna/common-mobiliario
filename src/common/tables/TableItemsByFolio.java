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

public class TableItemsByFolio extends JTable {

    public TableItemsByFolio() {
       
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
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(this.dataModel); 
        this.setRowSorter(ordenarTabla);
        
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setMaxWidth(0);
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setMinWidth(0);
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setPreferredWidth(0);
     
        this.getColumnModel().getColumn(Column.AMOUNT.getNumber()).setCellRenderer(centrar);
        this.getColumnModel().getColumn(Column.PRICE.getNumber()).setCellRenderer(right);
        this.getColumnModel().getColumn(Column.SUBTOTAL.getNumber()).setCellRenderer(right);
        this.getColumnModel().getColumn(Column.DISCOUNT.getNumber()).setCellRenderer(right);
        
        
        this.getColumnModel().getColumn(Column.EVENT_DATE.getNumber()).setCellRenderer(centrar);
        this.getColumnModel().getColumn(Column.EVENT_STATUS.getNumber()).setCellRenderer(centrar);
        this.getColumnModel().getColumn(Column.EVENT_TYPE.getNumber()).setCellRenderer(centrar);
        this.getColumnModel().getColumn(Column.DELIVERY_DATE.getNumber()).setCellRenderer(centrar);
    }
    
    
    public enum Column{
        
       RENTA_ID(0,"renta_id",20,String.class, false), 
       FOLIO(1,"Folio",20,String.class, false),
       AMOUNT(2,"Cantidad",20,String.class, false),
       ITEM(3,"Artículo",80,String.class, false),
       PRICE(4,"Precio",40,String.class, false),
       DISCOUNT(5,"Descuento %",40,String.class, false),
       SUBTOTAL(6,"Importe",40,String.class, false),
       DELIVERY_DATE(7,"Fecha entrega",80,String.class, false),
       EVENT_DATE(8,"Fecha creación evento",80,String.class, false),
       EVENT_TYPE(9,"Tipo evento",80,String.class, false),
       EVENT_STATUS(10,"Estado evento",80,String.class, false);
       
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
