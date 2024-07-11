package common.utilities;

import common.constants.ApplicationConstants;
import static common.constants.ApplicationConstants.LIMIT_GENERATE_PDF;
import static common.constants.ApplicationConstants.UTILITY_CLASS;
import common.constants.PropertyConstant;
import common.exceptions.BusinessException;
import common.model.Articulo;
import common.model.Renta;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.MessagingException;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;


public class UtilityCommon {
    
    public static String validateAndGetAbsolutePathFromjFileChooser
        (final javax.swing.JInternalFrame jInternalFrame)throws BusinessException {
        
        final String descriptionText = "Cargar imagen";
        String absolutePath=null;
        
        String[] imagesFilter = new String[]{"Image Files", "jpg","jpeg", "png", "gif", "tif"};
        FileNameExtensionFilter filter = 
                new FileNameExtensionFilter("Image Files", imagesFilter);
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(filter);
        int result = jFileChooser.showDialog(jInternalFrame,descriptionText);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = jFileChooser.getSelectedFile();         
                
                ImageInputStream imageInputStream = ImageIO.createImageInputStream(file);
                
                Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream);
                
                while (imageReaders.hasNext()) {
                    ImageReader reader = imageReaders.next();
                    
                    boolean formatCorrect = false;                    
                    
                    for (String filterImg : imagesFilter) {
                        if (reader.getFormatName().equalsIgnoreCase(filterImg)) {
                            formatCorrect = true;
                            break;
                        }
                    }
                    if (!formatCorrect) {
                        throw new BusinessException("La imagen tiene un formato incorrecto ["
                                +reader.getFormatName()+"]. Imagenes permitidas: jpg,jpeg,png,gif,tif");
                    }
                }
                
                absolutePath = file.getAbsolutePath();
                Path path = Paths.get(absolutePath);
                long bytes = Files.size(path);
                System.out.println(String.format("%,d kilobytes", bytes / 1024));
                if ( (bytes / 1024) >= 65 ) {
                    throw new BusinessException("La imagen que deseas cargar pesa ["+(bytes / 1024)+"] KB. Intenta cargar otra imagen con un peso menor a 65 kilobytes (KB)");
                }
            } catch (IOException io) {
                throw new BusinessException(io.getMessage(),io);
            }
        }
        
        return absolutePath;
    }
    
    public static void setMaximum (final javax.swing.JInternalFrame jInternalFrame,
            final PropertyConstant propertyConstant) {
        
        try {
            Boolean maxWin = 
                    Boolean.parseBoolean(PropertySystemUtil.get(propertyConstant).trim());
            log.info(">>> MAX WIN: "+maxWin);
            jInternalFrame.setMaximum(maxWin);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            JOptionPane.showMessageDialog(jInternalFrame,e, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE); 
        }
        
    }
    
    private UtilityCommon() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
    
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(UtilityCommon.class.getName());

    public static String getPathLocation()throws IOException,URISyntaxException{
   
        File file = new File(UtilityCommon.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getParentFile();
        
        return file+"";
    
    }
    
    public static void addEscapeListener(final javax.swing.JInternalFrame jInternalFrame) {
        ActionListener escListener = (ActionEvent e) -> {
            jInternalFrame.setVisible(false);
        };

        jInternalFrame.getRootPane().registerKeyboardAction(escListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

    }
    
    public static void addEscapeListener(final JDialog dialog) {
        ActionListener escListener = (ActionEvent e) -> {
            dialog.setVisible(false);
        };

        dialog.getRootPane().registerKeyboardAction(escListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

    }
    
    public static void pushNotification(final String notification,
            List<String> listNotifications, javax.swing.JTextArea txtAreaNotifications){
        StringBuilder messages = new StringBuilder();
        
        String date = simpleDateFormat.format(new Timestamp(System.currentTimeMillis()));
        listNotifications.add(date+" >> "+notification);
        listNotifications.stream().forEach(t -> {
            messages.append(t);
            messages.append("\n");
        });      
        txtAreaNotifications.setText(null);
        txtAreaNotifications.setText(messages+"");
    }
    
    public static List<Articulo> applyFilterToItems (List<Articulo> items, String text) {
        
        String textToSearch = "(.*)"+removeAccents(text).toLowerCase().trim()+"(.*)";
        
        return items.stream()
                    .filter(articulo -> Objects.nonNull(articulo))
                    .filter(articulo -> Objects.nonNull(articulo.getDescripcion()))
                    .filter(articulo -> Objects.nonNull(articulo.getColor()))
                    .filter(articulo -> (
                            removeAccents(
                                    articulo.getDescripcion().trim().toLowerCase() + " " + 
                                            articulo.getColor().getColor().trim().toLowerCase()
                            )).matches(textToSearch)
                            || removeAccents(articulo.getCodigo().trim().toLowerCase())
                                    .matches(textToSearch)
                            || removeAccents(articulo.getColor().getColor().trim().toLowerCase())
                                    .matches(textToSearch)
                            || removeAccents(articulo.getCategoria().getDescripcion().trim().toLowerCase())
                                    .matches(textToSearch)
                            
                    )
                    .collect(Collectors.toList());
    }
    
    public static String onlyNumbersAndPoint (String text) {
        return text.replaceAll("[^0-9.]", "");
    }
    public static String onlyNumbers (String text) {
        return text.replaceAll("[^0-9]", "");
    }
    
    // get value from first row selected.
    public static String getIdSelected (
            JTable table,Integer columBooleanNumber, Integer columValueNumber) {
       String value = null;
        
        for (int i = 0; i < table.getRowCount(); i++) {
            if (Boolean.parseBoolean(
                    table.getValueAt(i, columBooleanNumber).toString())) {
                value = table.getValueAt(i, columValueNumber).toString();
                break;
            }
        }
        return value;
   }
    
    public static List<String> getIdsSelected (
            JTable table,Integer columBooleanNumber, Integer columIDNumber) {
       List<String> ids = new ArrayList<>();
        
        for (int i = 0; i < table.getRowCount(); i++) {
            if (Boolean.parseBoolean(
                    table.getValueAt(i, columBooleanNumber).toString())) {
                ids.add(
                        table.getValueAt(i, columIDNumber).toString()
                );
            }
        }
        return ids;
   }
    
    public static void validateSelectCheckboxInTable(JTable table, Integer columNumber) throws BusinessException {
        
        int selectRows = 0;
        
        for (int i = 0; i < table.getRowCount(); i++) {
            if (Boolean.parseBoolean(table.getValueAt(i, columNumber).toString())) {
                selectRows++;
            }
        }
        
        if (selectRows > LIMIT_GENERATE_PDF) {
            throw new BusinessException ("Limite excedido de operaciones ["+ LIMIT_GENERATE_PDF +"]");
        }
        
        if (selectRows <= 0) {
            throw new BusinessException ("Marca el CHECKBOX de una o mas filas para continuar");
        }
    }
    
        public static void addJtableToPane (int sizeVertical, int sizeHorizontal, JPanel jPanel,JTable tableToAdd ) {
        
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(tableToAdd);
        
        javax.swing.GroupLayout tabPanelGeneralLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(tabPanelGeneralLayout);
        tabPanelGeneralLayout.setHorizontalGroup(
            tabPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPanelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, sizeVertical, Short.MAX_VALUE)
                .addContainerGap())
        );
        tabPanelGeneralLayout.setVerticalGroup(
            tabPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPanelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, sizeHorizontal, Short.MAX_VALUE))
        );
    }        
     
    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                log.error(e);
            }
        }).start();
    }
    
    public static LocalDate getLocalDateFromString (final String date, final String formatDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatDate);
        return LocalDate.parse(date, formatter);
    }
        
    public static String removeAccents(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

    }
    
    public static void selectCheckBoxWhenKeyPressedIsSpace (java.awt.event.KeyEvent evt, JTable table, int columnBoolean) {
        
        if(evt.getKeyCode() != 32) {
            return;
        }
        
        int rowSelect = table.getSelectedRow();
        
        if (rowSelect >= 0) {
            boolean isSelected = (Boolean) table.getValueAt(rowSelect, columnBoolean);
            table.setValueAt(!isSelected, rowSelect, columnBoolean);
        }
    }
    
    public static void calcularTotalesPorRenta (Renta renta) {
    
        if (renta.getDescuento() != null && renta.getDescuento() > 0) {
            renta.setCalculoDescuento((renta.getSubTotal() * (renta.getDescuento() / 100)));
        } else {
            renta.setCalculoDescuento(0F);
        }

        if(renta.getTotalFaltantes() > 0F && renta.getDepositoGarantia() > 0F){
                // el pedido tiene pago pendiente por faltante 
               // a dejado deposito en garantia
               renta.setTotalFaltantesPorCubrir(renta.getTotalFaltantes() - renta.getDepositoGarantia());

       }

        if (renta.getTotalAbonos() == null) {
            renta.setTotalAbonos(0F);
        }
        if (renta.getEnvioRecoleccion() == null) {
            renta.setEnvioRecoleccion(0F);
        }
        if (renta.getDepositoGarantia() == null) {
            renta.setDepositoGarantia(0F);
        }
        
        Float totalCalculoSinIVA = (renta.getSubTotal() + renta.getEnvioRecoleccion() + renta.getDepositoGarantia()) 
                - renta.getCalculoDescuento();

        
        if (renta.getIva() != null && renta.getIva() > 0) {
            renta.setCalculoIVA(totalCalculoSinIVA * (renta.getIva() / 100));
        } else {
            renta.setCalculoIVA(0F);
        }
        
        Float totalCalculoConIVA = (renta.getSubTotal() +
                        (renta.getEnvioRecoleccion() != null ? renta.getEnvioRecoleccion() : 0F) +
                        (renta.getDepositoGarantia() != null ? renta.getDepositoGarantia() : 0F) +
                        renta.getCalculoIVA()) - renta.getCalculoDescuento();
        

        renta.setTotalCalculo(totalCalculoConIVA);

        renta.setTotal( (totalCalculoConIVA - renta.getTotalAbonos()) + renta.getTotalFaltantes());
        if(renta.getTotal() <= 0){
            renta.setDescripcionCobranza(ApplicationConstants.COBRANZA_PAGADO);
        }else if(renta.getTotal() > 0 && renta.getTotalAbonos() == 0){
            renta.setDescripcionCobranza(ApplicationConstants.COBRANZA_NO_PAGADO);
        }else if (renta.getTotal() > 0 && renta.getTotalAbonos() > 0){
            renta.setDescripcionCobranza(ApplicationConstants.COBRANZA_PARCIAL_PAGADO);
        }
        renta.setMensajeFaltantes("");
        if(renta.getTotalFaltantes() > 0){
            // el pedido tiene pago pendiente por faltante
            if(renta.getDepositoGarantia()>0){
                // a dejado deposito en garantia
                float calculoDepositoMenosTotalFaltantes = renta.getDepositoGarantia() - renta.getTotalFaltantes();
                if(calculoDepositoMenosTotalFaltantes > 0)
                    renta.setMensajeFaltantes("Dep\u00F3sito en garant\u00EDa es: $ "+renta.getDepositoGarantia()+", concepto faltantes es: $ "+renta.getTotalFaltantes()+", cantidad a devolver al cliente: $ "+ (calculoDepositoMenosTotalFaltantes));
                else
                    renta.setMensajeFaltantes("Dep\u00F3sito en garant\u00EDa es: $ "+renta.getDepositoGarantia()+", concepto faltantes es: $ "+renta.getTotalFaltantes()+", resta: $ "+ (calculoDepositoMenosTotalFaltantes));
            }else{
                // se asgina el total                   
                renta.setMensajeFaltantes("Total a pagar por concepto de faltantes es: $ "+renta.getTotalFaltantes());
            }                

        }
 }
       
    public String conviertemoneda(String valor) {
        
        DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        simbolo.setGroupingSeparator(',');
        
        float entero = Float.parseFloat(valor);
        DecimalFormat formateador = new DecimalFormat("###,###.##", simbolo);
        String entero2 = formateador.format(entero);
        
        if (entero2.contains(".")) {
            entero2 = "$" + entero2;
            
        } else {
            entero2 = "$" + entero2 + ".00";
        }
        
        return entero2;
        
    }
    
     public static boolean verifyIfInternalFormIsOpen(Object obj, JDesktopPane desktopPane) {
        JInternalFrame[] activos = desktopPane.getAllFrames();
        boolean cerrado = true;
        int i = 0;
        while (i < activos.length && cerrado) {
            if (activos[i] == obj) {
                cerrado = false;
            }
            i++;
        }
        return cerrado;
    }
        
     public static String formatMoney(String valor) {
        
        DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        simbolo.setGroupingSeparator(',');
        
        float entero = Float.parseFloat(valor);
        DecimalFormat formateador = new DecimalFormat("###,###.##", simbolo);
        String entero2 = formateador.format(entero);
        
        if (entero2.contains(".")) {
            entero2 = "$" + entero2;
            
        } else {
            entero2 = "$" + entero2 + ".00";
        }
        
        return entero2;
        
    }
    
    public static JDialog showDialog(final String title, final String content, Component componentLocation){
        
        JDialog dialog = new JDialog(new JFrame());
        dialog.setModalityType(Dialog.ModalityType.MODELESS);
        dialog.setBounds(0,0,100, 100);
        dialog.setSize(300,100);
        dialog.add(new JLabel(content));
        dialog.setTitle(title);
        dialog.setLocationRelativeTo(componentLocation);
        dialog.setVisible(true);
        dialog.setModal(true);
            
        return dialog;
    }
    
    // obtiene la fecha del sistema con un caracter delimitador
    public static String getSystemDate(String delimiterCharacter) {
        String systemDate = null;
        Calendar fecha = Calendar.getInstance();
        String mes = Integer.toString(fecha.get(Calendar.MONTH) + 1);
        String dia = Integer.toString(fecha.get(Calendar.DATE));
        String auxMes = null, auxDia = null;
        

        if (mes.length() == 1) {
            auxMes = "0" + mes;
            systemDate = fecha.get(Calendar.DATE) + delimiterCharacter + auxMes + delimiterCharacter + fecha.get(Calendar.YEAR);

            if (dia.length() == 1) {
                auxDia = "0" + dia;
                systemDate = auxDia + delimiterCharacter + auxMes + delimiterCharacter + fecha.get(Calendar.YEAR);

            }

        } else {
            systemDate = fecha.get(Calendar.DATE) + delimiterCharacter + (fecha.get(Calendar.MONTH) + 1) + delimiterCharacter + fecha.get(Calendar.YEAR);
        }
        
        return systemDate;
    }
    
    public static String deleteCharacters(String s_cadena, String s_caracteres) {
        String nueva_cadena = "";
        Character caracter = null;
        boolean valido = true;

        /* Va recorriendo la cadena s_cadena y copia a la cadena que va a regresar,
         sólo los caracteres que no estén en la cadena s_caracteres */
        for (int i = 0; i < s_cadena.length(); i++) {
            valido = true;
            for (int j = 0; j < s_caracteres.length(); j++) {
                caracter = s_caracteres.charAt(j);

                if (s_cadena.charAt(i) == caracter) {
                    valido = false;
                    break;
                }
            }
            if (valido) {
                nueva_cadena += s_cadena.charAt(i);
            }
        }

        return nueva_cadena;
    }
  
    
     public static boolean validateAmount(String value) { 
        boolean valid = false;
        Float amount = null;
        
        if(value == null || value.isEmpty() || value.equals(""))
            return false;
        
         try {
             amount = Float.parseFloat(value);
         } catch (NumberFormatException e) {
             valid = false;
         }
         
         if(amount == null  || amount <= 0)
            valid = false;
        else
            valid = true;         
        
        return valid;
    }
     
    public static boolean validateComboBoxDataValue(String value){
                
        if(value == null 
                || value.isEmpty()
                || value.equals(ApplicationConstants.CMB_SELECCIONE))
            return false;
        else
            return true;
    
    }    
   
    public static void selectAllCheckboxInTable (JTable table, int column, boolean checked) {
       for (int i = 0 ; i < table.getRowCount() ; i++) {
            table.setValueAt(checked, i, column);
       }
    }
    
     public static void isEmail(String email) throws MessagingException{ //validar correo electronico
         
        Pattern pat;
        Matcher mat;
        pat = Pattern.compile("^([0-9a-zA-Z]([_.w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-w]*[0-9a-zA-Z].)+([a-zA-Z]{2,9}.)+[a-zA-Z]{2,3})$");
        
        if(email == null || email.equals("")){
            throw new MessagingException("Email vacio");
        }else{
            String[] array = email.split(";");
            for(String e : array){
                mat = pat.matcher(e);
                if(!mat.find()){
                    throw new MessagingException("Email no v\u00E1lido: "+e);
                }
            }
        }
        
    }
    
    public static Date getFromString (final String date, final String pattern) throws ParseException {
        return new SimpleDateFormat(pattern).parse(date);
    }
    
    public static String getStringFromDate (final Date date, final String format) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(format);  
        return dateFormat.format(date);
    }
    
}
