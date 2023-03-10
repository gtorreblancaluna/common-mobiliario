package common.services;

import java.awt.Desktop;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class UtilityService {
    
    private UtilityService(){}
    
    private static final UtilityService SINGLE_INSTANCE = null;
    private final static Logger LOG = Logger.getLogger(UtilityService.class.getName());
    
    public static UtilityService getInstance(){
        
        if (SINGLE_INSTANCE == null) {
            return new UtilityService();
        }
        return SINGLE_INSTANCE;
    } 
    
    public void exportarExcel(JTable tableToExport){
        try {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.showSaveDialog(null);
            File fileToSave = jFileChooser.getSelectedFile();
            if (fileToSave != null) {
                fileToSave = new File(fileToSave.toString()+".xlsx");
                FileOutputStream out;
                try (Workbook wb = new XSSFWorkbook()) {
                    Sheet sheet = wb.createSheet();
                    Row rowCol = sheet.createRow(0);
                    for (int i = 0 ; i < tableToExport.getColumnCount(); i++) {
                        Cell cell = rowCol.createCell(i);
                        cell.setCellValue(tableToExport.getColumnName(i));
                    }   
                    for (int j = 0; j < tableToExport.getRowCount(); j++) {
                        Row row = sheet.createRow(j+1);
                        for (int k=0; k < tableToExport.getColumnCount();k++) {
                            Cell cell = row.createCell(k);
                            if (tableToExport.getValueAt(j, k) != null) {
                                cell.setCellValue(tableToExport.getValueAt(j, k).toString());
                            }
                        }
                    }   out = new FileOutputStream(new File(fileToSave.toString()));
                    wb.write(out);
                }
                out.close();
                openFile(fileToSave.toString());
            }
        } catch (HeadlessException | IOException e) {
            LOG.error(e);
            JOptionPane.showMessageDialog(null,e,"ERROR", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    private void openFile (String file) {
        try {
            File path = new File(file);
            Desktop.getDesktop().open(path);
        } catch (IOException e) {
            LOG.error(e.getMessage(),e);
            JOptionPane.showMessageDialog(null,e,"ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
