package common.services;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.log4j.Logger;

public class UtilityService {
    private UtilityService(){}
    
    private static final UtilityService SINGLE_INSTANCE = null;
    private int contaFila;
    private final static Logger LOG = Logger.getLogger(UtilityService.class.getName());
    
    public static UtilityService getInstance(){
        
        if (SINGLE_INSTANCE == null) {
            return new UtilityService();
        }
        return SINGLE_INSTANCE;
    } 
    
       
    public void exportarExcel(JTable tabla){
          // TODO add your handling code here:this.workbook = new HSSFWorkbook();
        contaFila = 1;
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
          
        int guardarArchivo = 0;       
        // obtenemos los encabezados de la tabla
        List<String> encabezados = new ArrayList<>();
        for(int i=0;i<tabla.getColumnCount();i++)
            encabezados.add(tabla.getColumnName(i));        
        System.out.println("ENCABEZADOS OBTENIDOS: "+encabezados);
        
        if(tabla.getRowCount() <= 0){
          Toolkit.getDefaultToolkit().beep();
          JOptionPane.showMessageDialog(null, "ERROR: Tabla esta vacia ", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        crearEsqueleto(encabezados,workbook,sheet);
            try {
                JFileChooser chooser = new JFileChooser();
//                FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivo Excel", new String[]{"xsl"});
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivo Excel", new String[]{"csv"});
                chooser.setFileFilter(filter);
                guardarArchivo = chooser.showSaveDialog(null);
                if (guardarArchivo == 0) {
                    int contador = 0;
                    while (contador < tabla.getRowCount()) {
                        List<String> fila = new ArrayList<>();
                        for(int col=0;col<encabezados.size();col++)
                            fila.add(tabla.getValueAt(contador, col)+"");
                        
                        generarFilaXLS(encabezados,fila, chooser.getSelectedFile().getPath() + ".xls",workbook,sheet);
                        contador++;
                    }                    
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Proceso Finalizado. Se ha escrito el archivo en la ruta:\n" + chooser.getSelectedFile().getAbsolutePath() + ".xsl", "Generación de reporte", 1);
                }

            } catch (Exception ex) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "No se pudo generar el reporte en Excel.\n CODIGO DE ERROR: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }       
    
    }
    
    private void generarFilaXLS(List<String> encabezados,List<String> fila,String pathXLS,HSSFWorkbook workbook,HSSFSheet sheet){
    
        try {
            for(int i=0;i<=encabezados.size();i++)
                sheet.autoSizeColumn(i);
        
            agregarFila(fila,workbook,sheet);

        } catch (Exception fos) {
            JOptionPane.showMessageDialog(null, "Error: " + fos.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(new File(pathXLS));
                workbook.write(fos);
            } catch (IOException e) {
                LOG.error(e.getMessage(),e);
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        LOG.error(e.getMessage(),e);
                        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
    
    private void crearEsqueleto(List<String> encabezados,HSSFWorkbook workbook,HSSFSheet sheet) {
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setFontName("Impact");
        style.setFont(font);
        HSSFRow row2 = sheet.createRow(1);
        HSSFCell cell = row2.createCell(0);
        cell.setCellStyle(style);
        HSSFCell celltot = row2.createCell(1);
        celltot.setCellStyle(style);

        crearTitulosConceptos(encabezados,workbook,sheet);
    }
    
     private void crearTitulosConceptos(List<String> encabezados,HSSFWorkbook workbook,HSSFSheet sheet) {
        HSSFCellStyle styleTituloConcepto = workbook.createCellStyle();
        styleTituloConcepto.setWrapText(true);
        styleTituloConcepto.setFillForegroundColor(new HSSFColor.WHITE().getIndex());
        org.apache.poi.ss.usermodel.Font hlink_font = workbook.createFont();
        hlink_font.setColor(IndexedColors.WHITE.getIndex());
        styleTituloConcepto.setFont(hlink_font);
        styleTituloConcepto.setFont(hlink_font);
        HSSFFont fontTituloConcepto = workbook.createFont();
        fontTituloConcepto.setFontName("Arial");
        styleTituloConcepto.setFont(fontTituloConcepto);
        styleTituloConcepto.setWrapText(true);
        HSSFRow row14 = sheet.createRow(0);
        
        int contador = 0;
        for(String encabezado : encabezados){
            HSSFCell cell = row14.createCell(contador);
            cell.setCellValue(new HSSFRichTextString(encabezado));
            cell.setCellStyle(styleTituloConcepto);
            contador++;
        }


    }
     
      private void agregarFila(List<String> fila,HSSFWorkbook workbook,HSSFSheet sheet) {
        
        HSSFCellStyle styleNumero = workbook.createCellStyle();
        HSSFCellStyle styleCentrado = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();

        HSSFRow row = sheet.createRow(contaFila);
        font.setFontName("Arial");

        styleCentrado.setFont(font);
        styleCentrado.setWrapText(true);
        styleNumero.setFont(font);
        styleNumero.setWrapText(true);
        
        int contador = 0;
        for(String f : fila){
            HSSFCell cell = row.createCell(contador);
            if(f.startsWith("$"))
               f = f.replace("$", "").replace(",", "");
            
            try {            
                Float numero = Float.parseFloat(f);   
                cell = row.createCell(contador);
                cell.setCellValue(numero);
                cell.setCellStyle(styleNumero);
            } catch (NumberFormatException e) {
                // este no es un numero
                cell = row.createCell(contador);   
                if(f.equals("null")){
                    cell.setCellValue("");
                }else{
                    cell.setCellValue(f);
                }
                cell.setCellStyle(styleCentrado);
            }
            contador ++ ;
        }       

        contaFila++;

    }
    
}
