package common.utilities;

import static common.utilities.UtilityCommon.selectAllCheckboxInTable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.AbstractButton;
import javax.swing.JTable;

public class ItemListenerHeaderCheckbox implements ItemListener{
    
    private final int column;
    private final JTable table;

    public ItemListenerHeaderCheckbox(final int column, final JTable table) {
        this.column = column;
        this.table = table;
    }
    
    
    
    @Override
      public void itemStateChanged(ItemEvent e)
      {
         Object source = e.getSource();
         if (source instanceof AbstractButton == false)
         {
            return;
         }
         boolean checked = e.getStateChange() == ItemEvent.SELECTED;
         selectAllCheckboxInTable(table,column,checked);
         table.getRowSorter().modelStructureChanged();
      }
}
