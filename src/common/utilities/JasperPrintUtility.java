package common.utilities;

import common.constants.ApplicationConstants;
import static common.constants.ApplicationConstants.COUNTRY;
import static common.constants.ApplicationConstants.DATE_FORMAT_LARGE;
import static common.constants.ApplicationConstants.LANGUAJE;
import static common.constants.ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT;
import common.exceptions.BusinessException;
import common.model.Renta;
import java.awt.Desktop;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.log4j.Logger;


public abstract class JasperPrintUtility {
    
    private static final Logger log = Logger.getLogger(JasperPrintUtility.class.getName());
    private static final SimpleDateFormat formatter = 
            new SimpleDateFormat(SIMPLE_DATE_FORMAT_SHORT, new Locale ( LANGUAJE , COUNTRY ));
    private static final SimpleDateFormat simpleDateFormat = 
            new SimpleDateFormat(DATE_FORMAT_LARGE, new Locale ( LANGUAJE , COUNTRY ));
    
    public static void openPDFReportDeliveryChofer (final String rentaId,
            final String choferName, final String folio, 
            ConnectionDB connectionDB, final String pathLocation) throws BusinessException {
        
        try {
            connectionDB = ConnectionDB.getInstance();            
            String reportName = pathLocation+ApplicationConstants.NOMBRE_REPORTE_ENTREGAS+"-"+folio+".pdf";
            JasperReport masterReport = (JasperReport) JRLoader.loadObjectFromFile(pathLocation+ApplicationConstants.RUTA_REPORTE_ENTREGAS);
            
            Map<String,Object> parameters = new HashMap<>();
            parameters.put("id_renta", rentaId);
            parameters.put("chofer", choferName);
            parameters.put("URL_IMAGEN",pathLocation+ApplicationConstants.LOGO_EMPRESA );
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(masterReport, parameters, connectionDB.getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint, reportName);

            Desktop.getDesktop().open(
                    new File(reportName)
            );            

        } catch (Exception e) {
            log.error(e);
            throw new BusinessException(e.getMessage(), e);
        }
    
    }
    
    public static void openPDFReportByCategories (final String userId, final String userName, final Renta renta, ConnectionDB connectionDB,
            final String pathLocation) throws BusinessException {
        try {
            connectionDB = ConnectionDB.getInstance();            
            String reportName = pathLocation+ApplicationConstants.NOMBRE_REPORTE_CATEGORIAS_SIN_EXT+"-"+renta.getFolio()+".pdf";
            JasperReport masterReport = (JasperReport) JRLoader.loadObjectFromFile(pathLocation+ApplicationConstants.RUTA_REPORTE_CATEGORIAS);
            
            Map<String,Object> parameters = new HashMap<>();
            parameters.put("URL_IMAGEN",pathLocation+ApplicationConstants.LOGO_EMPRESA );
            parameters.put("RENTA_ID",String.valueOf(renta.getRentaId()));
            parameters.put("USUARIO_ID",userId);
            parameters.put("NOMBRE_CLIENTE",renta.getCliente().getNombre()+" "+renta.getCliente().getApellidos());
            parameters.put("FECHA_EVENTO",
                    simpleDateFormat.format(formatter.parse(renta.getFechaEvento())));
            parameters.put("FECHA_ENTREGA",
                    simpleDateFormat.format(formatter.parse(renta.getFechaEntrega())));
            parameters.put("FECHA_RECOLECCION",
                    simpleDateFormat.format(formatter.parse(renta.getFechaDevolucion())));
            parameters.put("DESCRIPCION_EVENTO",renta.getDescripcion());
            parameters.put("FOLIO",String.valueOf(renta.getFolio()));
            parameters.put("TELEFONOS_CLIENTE",
                    renta.getCliente().getTelMovil() != null ? renta.getCliente().getTelMovil() : "" +" - "+
                            renta.getCliente().getTelFijo() != null ? renta.getCliente().getTelFijo() : "");
            parameters.put("NOMBRE_ENCARGADO_AREA",userName);
            parameters.put("SUBREPORT_DIR", pathLocation+"/");
            parameters.put("COMENTARIO_EVENTO", renta.getComentario() != null ? renta.getComentario() : "");
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(masterReport, parameters, connectionDB.getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint, reportName);

            Desktop.getDesktop().open(
                    new File(reportName)
            );            

        } catch (Exception e) {
            log.error(e);
            throw new BusinessException(e.getMessage(),e);
        }
    }
    
}
