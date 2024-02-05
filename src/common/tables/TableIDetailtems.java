package common.tables;

import common.constants.ApplicationConstants;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.Getter;

public class TableIDetailtems extends JTable {
    
    public TableIDetailtems() {       
        this.setFont(new Font( ApplicationConstants.ARIAL , Font.PLAIN, 12 ));
        format();
    }
    
    @Getter
    public enum Column {
        
        ID(0,"id",20,String.class, false),
        CODE(1,"Código",20,String.class, false),
        STOCK(2,"Stock",20,String.class, false),
        RENT(3,"En renta",20,String.class, false),
        MISSING(4,"Faltantes",20,String.class, false),
        REPAIR(5,"Reparación",20,String.class, false),
        WORK_ACCIDENT(6,"Accidente trabajo",20,String.class, false),
        RETURN(7,"Devolución",20,String.class, false),
        SHOPPING(8,"Compras",20,String.class, false),
        UTILS(9,"Utiles",20,String.class, false),
        CATEGORY(10,"Categoria",90,String.class, false),
        DESCRIPTION(11,"Descripción",100,String.class, false),
        COLOR(12,"Color",100,String.class, false),
        UPDATED_AT(13,"Actualizado",100,String.class, false);
        
        private final int number;
        private final int size;
        private final String name;
        private final Class clazzType;
        private final Boolean isEditable;

        private Column(int number, String name,int size, Class clazzType, Boolean isEditable) {
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
        
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        this.getActionMap().put("Enter", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            //do something on JTable enter pressed
        }
    });
        
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
        
        this.getColumnModel().getColumn(Column.ID.getNumber()).setMaxWidth(0);
        this.getColumnModel().getColumn(Column.ID.getNumber()).setMinWidth(0);
        this.getColumnModel().getColumn(Column.ID.getNumber()).setPreferredWidth(0);
        
        this.getColumnModel().getColumn(Column.CODE.getNumber()).setCellRenderer(center);
        this.getColumnModel().getColumn(Column.STOCK.getNumber()).setCellRenderer(center);
        this.getColumnModel().getColumn(Column.RENT.getNumber()).setCellRenderer(center);
        this.getColumnModel().getColumn(Column.MISSING.getNumber()).setCellRenderer(center);
        this.getColumnModel().getColumn(Column.REPAIR.getNumber()).setCellRenderer(center);
        this.getColumnModel().getColumn(Column.WORK_ACCIDENT.getNumber()).setCellRenderer(center);
        this.getColumnModel().getColumn(Column.RETURN.getNumber()).setCellRenderer(center);
        this.getColumnModel().getColumn(Column.SHOPPING.getNumber()).setCellRenderer(center);
        this.getColumnModel().getColumn(Column.UTILS.getNumber()).setCellRenderer(center);
              
        try {
            DefaultTableModel temp = (DefaultTableModel) this.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            
        }
        
    }
    
}
