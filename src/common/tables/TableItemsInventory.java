package common.tables;

import common.constants.ApplicationConstants;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.Getter;

public class TableItemsInventory extends JTable {
    
    public static final int SIZE_IMAGE_IN_CELL = 27;
    
    public TableItemsInventory() {       
        this.setFont(new Font( ApplicationConstants.ARIAL , Font.PLAIN, 12 ));
        format();
    }
    
    @Getter
    public enum Column {
        
        ID(0,10,"id",String.class, false),
        IMAGE(1,SIZE_IMAGE_IN_CELL,"Img",ImageIcon.class, false),
        CODE(2,40,"Código",String.class, false),
        STOCK(3,20,"Stock",String.class, false),
        EN_RENTA(4,20,"En renta",String.class, false),
        FALTANTES(5,20,"Faltantes",String.class, false),
        REPARACION(6,20,"Reparacion",String.class, false),
        ACCIDENTE_TRABAJO(7,20,"Accidente trabajo",String.class, false),
        DEVOLUCION(8,20,"Devolucion",String.class, false),
        COMPRAS(9,20,"Compras",String.class, false),
        UTILES(10,20,"Utiles",String.class, false),       
        CATEGORY(11,90,"Categoría",String.class, false),
        DESCRIPTION(12,280,"Descripción",String.class, false),
        COLOR(13,90,"Color",String.class, false),       
        DATE(14,90,"Fecha",String.class, false),        
        PRECIO_COMPRA(15,40,"P.Compra",String.class, false),
        PRECIO_RENTA(16,40,"P.Rebta",String.class, false),        
        UPDATED_AT(17,40,"Actualizado",String.class, false);
        
        private final int number;
        private final int size;
        private final String name;
        private final Class clazzType;
        private final Boolean isEditable;

        private Column(int number, int size, String name, Class clazzType, Boolean isEditable) {
            this.number = number;
            this.size = size;
            this.name = name;
            this.clazzType = clazzType;
            this.isEditable = isEditable;
        }

        public int getNumber() {
            return number;
        }

        public int getSize() {
            return size;
        }

        public String getName() {
            return name;
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
                columnNames.add(column.getName());
            }
            return columnNames.toArray(new String[0]);
        }
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
        
        setRowHeight(SIZE_IMAGE_IN_CELL);
        
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(this.getModel()); 
        this.setRowSorter(ordenarTabla);
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        
        for (Column column : Column.values()) {
            this.getColumnModel()
                    .getColumn(column.getNumber())
                    .setPreferredWidth(column.getSize());
        }
        
        getColumnModel().getColumn(Column.ID.getNumber()).setMaxWidth(0);
        getColumnModel().getColumn(Column.ID.getNumber()).setMinWidth(0);
        getColumnModel().getColumn(Column.ID.getNumber()).setPreferredWidth(0); 
                        
        getColumnModel().getColumn(Column.STOCK.getNumber()).setCellRenderer(center);
        getColumnModel().getColumn(Column.EN_RENTA.getNumber()).setCellRenderer(center);
        getColumnModel().getColumn(Column.FALTANTES.getNumber()).setCellRenderer(center);
        
        getColumnModel().getColumn(Column.REPARACION.getNumber()).setCellRenderer(center);
        getColumnModel().getColumn(Column.ACCIDENTE_TRABAJO.getNumber()).setCellRenderer(center);
        getColumnModel().getColumn(Column.DEVOLUCION.getNumber()).setCellRenderer(center);
        getColumnModel().getColumn(Column.COMPRAS.getNumber()).setCellRenderer(center);
        getColumnModel().getColumn(Column.UTILES.getNumber()).setCellRenderer(center);
              
        try {
            DefaultTableModel temp = (DefaultTableModel) this.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            
        }
        
    }
    
}
