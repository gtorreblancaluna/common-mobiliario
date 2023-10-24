package common.tables;

import common.constants.ApplicationConstants;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class TableViewOrdersProvidersDetail extends JTable {

    public TableViewOrdersProvidersDetail() {
        
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
        
        
        this.getColumnModel().getColumn(Column.ORDER_SUPPLIER_DETAIL_ID.getNumber()).setMaxWidth(0);
        this.getColumnModel().getColumn(Column.ORDER_SUPPLIER_DETAIL_ID.getNumber()).setMinWidth(0);
        this.getColumnModel().getColumn(Column.ORDER_SUPPLIER_DETAIL_ID.getNumber()).setPreferredWidth(0);
     
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setMaxWidth(0);
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setMinWidth(0);
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setPreferredWidth(0);
        
        this.getColumnModel().getColumn(Column.FOLIO.getNumber()).setCellRenderer(centrar);
        this.getColumnModel().getColumn(Column.AMOUNT.getNumber()).setCellRenderer(right);
        this.getColumnModel().getColumn(Column.PRICE.getNumber()).setCellRenderer(right);
        this.getColumnModel().getColumn(Column.IMPORT.getNumber()).setCellRenderer(right);
    }
    
    
    public enum Column{
        
       ORDER_SUPPLIER_ID(0,"No Orden",10,String.class, false),
       ORDER_SUPPLIER_DETAIL_ID(1,"detalle_orden_proveedor_id",10,String.class, false),
       RENTA_ID(2,"renta_id",10,String.class, false),
       FOLIO(3,"Folio",20,String.class, false),
       PRODUCT(4,"Articulo",60,String.class, false),
       AMOUNT(5,"Cantidad",40,String.class, false),
       PRICE(6,"Precio",40,String.class, false),
       IMPORT(7,"Importe",40,String.class, false),
       EVENT_DATE(8,"Fecha evento",60,String.class, false),
       USER(9,"Usuario",80,String.class, false),
       SUPPLIER(10,"Proveedor",80,String.class, false),
       ORDER_COMMENT(11,"Comentario orden",80,String.class, false),
       ORDER_DETAIL_TYPE(12,"Tipo",50,String.class, false),
       CREATED_AT(13,"Creado",60,String.class, false);
       
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
