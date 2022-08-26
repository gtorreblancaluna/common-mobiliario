package common.utilities;

import common.constants.ApplicationConstants;
import common.model.Renta;
import java.awt.Component;
import java.awt.Dialog;
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


public abstract class UtilityCommon {
    
    public static void calcularTotalesPorRenta (Renta renta) {
    
        Float totalCalculo = 0F;

        if (renta.getDescuento() != null && renta.getDescuento() > 0) {
            renta.setCalculoDescuento((renta.getSubTotal() * (renta.getDescuento() / 100)));
        } else {
            renta.setCalculoDescuento(0F);
        }

        if (renta.getIva() != null && renta.getIva() > 0) {
            renta.setCalculoIVA(renta.getSubTotal() * (renta.getIva() / 100));
        } else {
            renta.setCalculoIVA(0F);
        }

        if(renta.getTotalFaltantes() > 0 && renta.getDepositoGarantia()>0){
                // el pedido tiene pago pendiente por faltante 
               // a dejado deposito en garantia
               renta.setTotalFaltantesPorCubrir(renta.getTotalFaltantes() - renta.getDepositoGarantia());

       }

        if (renta.getTotalAbonos() == null) {
            renta.setTotalAbonos(0F);
        }

        totalCalculo = (renta.getSubTotal() +
                        (renta.getEnvioRecoleccion() != null ? renta.getEnvioRecoleccion() : 0F) +
                        (renta.getDepositoGarantia() != null ? renta.getDepositoGarantia() : 0F) +
                        renta.getCalculoIVA()) - renta.getCalculoDescuento();

        renta.setTotalCalculo(totalCalculo);

        renta.setTotal( (totalCalculo - renta.getTotalAbonos()) + renta.getTotalFaltantes());
        if(renta.getTotal() <= 0){
            renta.setDescripcionCobranza(ApplicationConstants.COBRANZA_PAGADO);
        }else if(renta.getTotal() > 0 && renta.getTotalAbonos() == 0){
            renta.setDescripcionCobranza(ApplicationConstants.COBRANZA_NO_PAGADO);
        }else if (renta.getTotal() > 0 && renta.getTotalAbonos() > 0){
            renta.setDescripcionCobranza(ApplicationConstants.COBRANZA_PARCIAL_PAGADO);
        }

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
    
    public static Date getFromString (String date, String pattern) throws ParseException {
        return new SimpleDateFormat(pattern).parse(date);
    }
    
}
