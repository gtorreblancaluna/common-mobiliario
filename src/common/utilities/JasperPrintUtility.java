package common.utilities;

import common.constants.ApplicationConstants;
import static common.constants.ApplicationConstants.COUNTRY;
import static common.constants.ApplicationConstants.DATE_FORMAT_LARGE;
import static common.constants.ApplicationConstants.LANGUAJE;
import static common.constants.ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT;
import static common.constants.ApplicationConstants.UTILITY_CLASS;
import common.exceptions.BusinessException;
import common.model.DatosGenerales;
import common.model.Renta;
import common.model.Usuario;
import java.awt.Desktop;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.log4j.Logger;


public class JasperPrintUtility {
    
    private JasperPrintUtility() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
    
    private static final Logger log = Logger.getLogger(JasperPrintUtility.class.getName());    
    
    public static void generatePDFOrderProvider (String orderId, DatosGenerales datosGenerales, final String pathLocation) {
             
        JasperPrint jasperPrint;
        
        try {
            String archivo = pathLocation+ApplicationConstants.RUTA_REPORTE_ORDEN_PROVEEDOR;
            if (archivo.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No se encuentra el Archivo jasper");
                return;
            }
            JasperReport masterReport = (JasperReport) JRLoader.loadObjectFromFile(archivo);
            
            Map<String,Object> parametros = new HashMap<>();
            parametros.put("ID_ORDEN",orderId);
            parametros.put("NOMBRE_EMPRESA",datosGenerales.getCompanyName());
            parametros.put("DIRECCION_EMPRESA",datosGenerales.getAddress1());
            parametros.put("TELEFONOS_EMPRESA",datosGenerales.getAddress2());
            parametros.put("EMAIL_EMPRESA",datosGenerales.getAddress3() != null ? datosGenerales.getAddress3() : "");
            //guardamos el parámetro
            parametros.put(ApplicationConstants.URL_IMAGEN_PARAMETER_JASPER_KEY,
                    pathLocation+ApplicationConstants.LOGO_EMPRESA );
           
            jasperPrint = JasperFillManager.fillReport(masterReport, parametros, ConnectionDB.getInstance().getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint, pathLocation+ApplicationConstants.NOMBRE_REPORTE_ORDEN_PROVEEDOR);
            File file2 = new File(pathLocation+ApplicationConstants.NOMBRE_REPORTE_ORDEN_PROVEEDOR);
            Desktop.getDesktop().open(file2);
            
        } catch (Exception e) {
            log.error(e);
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        
    }
    
    
    public static void openPDFReportDeliveryChofer (final Renta renta, final String pathLocation) throws BusinessException {
        
        try {
            
            String telsCustomer = getCustomerTels(renta);
            String reportName = pathLocation+ApplicationConstants.NOMBRE_REPORTE_ENTREGAS+"-"+renta.getFolio()+".pdf";
            JasperReport masterReport = (JasperReport) JRLoader.loadObjectFromFile(pathLocation+ApplicationConstants.RUTA_REPORTE_ENTREGAS);
            
            final SimpleDateFormat formatter = 
                new SimpleDateFormat(SIMPLE_DATE_FORMAT_SHORT, new Locale ( LANGUAJE , COUNTRY ));
            final SimpleDateFormat simpleDateFormat = 
                new SimpleDateFormat(DATE_FORMAT_LARGE, new Locale ( LANGUAJE , COUNTRY ));
            
            Map<String,Object> parameters = new HashMap<>();
            
            parameters.put("RENTA_ID",String.valueOf(renta.getRentaId()));
            parameters.put("NOMBRE_CLIENTE",renta.getCliente().getNombre()+" "+renta.getCliente().getApellidos());
            parameters.put("FECHA_EVENTO",
                    simpleDateFormat.format(formatter.parse(renta.getFechaEvento())));
            parameters.put("FECHA_ENTREGA",
                    simpleDateFormat.format(formatter.parse(renta.getFechaEntrega())));
            parameters.put("FECHA_RECOLECCION",
                    simpleDateFormat.format(formatter.parse(renta.getFechaDevolucion())));
            parameters.put("DESCRIPCION_EVENTO",renta.getDescripcion());
            parameters.put("FOLIO",String.valueOf(renta.getFolio()));
            parameters.put("TELEFONOS_CLIENTE",telsCustomer);
            parameters.put("COMENTARIO_EVENTO", renta.getComentario() != null ? renta.getComentario() : "");
            parameters.put("HORA_ENTREGA", renta.getHoraEntrega());
            parameters.put("HORA_RECOLECCION", renta.getHoraDevolucion());
            parameters.put("TIPO_EVENTO", renta.getTipo().getTipo());
            parameters.put("NOMBRE_USUARIO", renta.getUsuario().getNombre()+" "+renta.getUsuario().getApellidos());
            parameters.put("NOMBRE_CHOFER", renta.getChofer().getNombre()+" "+renta.getChofer().getApellidos());
            
            parameters.put(ApplicationConstants.URL_IMAGEN_PARAMETER_JASPER_KEY,
                    pathLocation+ApplicationConstants.LOGO_EMPRESA );
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(masterReport, parameters,
                    ConnectionDB.getInstance().getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint, reportName);

            Desktop.getDesktop().open(
                    new File(reportName)
            );            

        } catch (Exception e) {
            log.error(e);
            throw new BusinessException(e.getMessage(), e);
        }
    
    }
    
    private static String getCustomerTels (final Renta renta) {
        List<String> customerTels = new ArrayList<>();
        String telsCustomer = "";
            
        if (renta.getCliente().getTelMovil() != null && !renta.getCliente().getTelMovil().isEmpty()) {
            customerTels.add(renta.getCliente().getTelMovil());
        }
        if (renta.getCliente().getTelFijo() != null && !renta.getCliente().getTelFijo().isEmpty()) {
            customerTels.add(renta.getCliente().getTelFijo());
        }
        if (!customerTels.isEmpty()){
            telsCustomer = String.join(",", customerTels);
        }
        
        return telsCustomer;
    }
    
    public static void openPDFReportByCategories (final List<Usuario> users, 
            final Renta renta, final String pathLocation) throws BusinessException {
        
        final SimpleDateFormat formatter = 
            new SimpleDateFormat(SIMPLE_DATE_FORMAT_SHORT, new Locale ( LANGUAJE , COUNTRY ));
        final SimpleDateFormat simpleDateFormat = 
            new SimpleDateFormat(DATE_FORMAT_LARGE, new Locale ( LANGUAJE , COUNTRY ));
        
        try {          
            
            JasperReport masterReport = (JasperReport) JRLoader.loadObjectFromFile(pathLocation+ApplicationConstants.RUTA_REPORTE_CATEGORIAS);            
            String telsCustomer = getCustomerTels(renta);
            
            for (Usuario user : users) {
                String reportName = 
                        pathLocation+ApplicationConstants.NOMBRE_REPORTE_CATEGORIAS_SIN_EXT+"-"+renta.getFolio()+"-"+user.getNombre()+".pdf";
                Map<String,Object> parameters = new HashMap<>();
                parameters.put(ApplicationConstants.URL_IMAGEN_PARAMETER_JASPER_KEY,
                        pathLocation+ApplicationConstants.LOGO_EMPRESA );
                parameters.put("RENTA_ID",String.valueOf(renta.getRentaId()));
                parameters.put("USUARIO_ID",user.getUsuarioId());
                parameters.put("NOMBRE_CLIENTE",renta.getCliente().getNombre()+" "+renta.getCliente().getApellidos());
                parameters.put("FECHA_EVENTO",
                        simpleDateFormat.format(formatter.parse(renta.getFechaEvento())));
                parameters.put("FECHA_ENTREGA",
                        simpleDateFormat.format(formatter.parse(renta.getFechaEntrega())));
                parameters.put("FECHA_RECOLECCION",
                        simpleDateFormat.format(formatter.parse(renta.getFechaDevolucion())));
                parameters.put("DESCRIPCION_EVENTO",renta.getDescripcion());
                parameters.put("FOLIO",String.valueOf(renta.getFolio()));
                parameters.put("TELEFONOS_CLIENTE",telsCustomer);
                parameters.put("NOMBRE_ENCARGADO_AREA",user.getNombre()+" "+user.getApellidos());
                parameters.put("SUBREPORT_DIR", pathLocation+"/");
                parameters.put("COMENTARIO_EVENTO", renta.getComentario() != null ? renta.getComentario() : "");

                JasperPrint jasperPrint = JasperFillManager.fillReport(masterReport, parameters,
                        ConnectionDB.getInstance().getConnection());

                JasperExportManager.exportReportToPdfFile(jasperPrint, reportName);

                Desktop.getDesktop().open(
                        new File(reportName)
                );  
            
            }          

        } catch (Exception e) {
            log.error(e);
            throw new BusinessException(e.getMessage(),e);
        }
    }
    
}
