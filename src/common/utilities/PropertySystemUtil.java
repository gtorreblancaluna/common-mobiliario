package common.utilities;

import static common.constants.ApplicationConstants.UTILITY_CLASS;
import common.constants.PropertyConstant;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import org.apache.log4j.Logger;


public class PropertySystemUtil {
    
    private PropertySystemUtil () {
        throw new IllegalStateException(UTILITY_CLASS);
    }
    
    private static final Logger log = Logger.getLogger(PropertySystemUtil.class.getName());
    private static final String NAME_FILE = "/system.properties";
    private static Properties prop = null;
    
    public static String get(final PropertyConstant propertyConstant)throws IOException{
        String value;

        try (InputStream input = new FileInputStream(UtilityCommon.getPathLocation()+NAME_FILE)) {

            if(prop == null) {
                prop = new Properties();
            }
            // load a properties file
            prop.load(input);
            value = prop.getProperty(propertyConstant.getKey());
            if (value == null || value.isEmpty()){
                save(propertyConstant.getKey(), propertyConstant.getValue());
                value = propertyConstant.getValue();
            }            
        } catch (FileNotFoundException e) {
            log.error(e);
            buildFirstTimeFile();
            value = propertyConstant.getValue();
        } catch (URISyntaxException | IOException e) {
            log.error(e.getMessage(),e);
            throw new IOException(e);
        }
        log.info(">>> VALUE: "+value);
        return value;
    }
    
    public static void save(final String key, final String value)throws IOException{
        
       try (FileOutputStream fileOutputStream = new FileOutputStream(UtilityCommon.getPathLocation()+NAME_FILE)) {
            if(prop == null){
                 prop = new Properties();
            }
            // load a properties file
            // set the properties value
            prop.setProperty(key, value);      
            // save properties to project root folder
            prop.store(fileOutputStream, null);
       } catch (URISyntaxException | IOException e) {
          log.error(e);
          throw new IOException(e);
       }
    }
       

    public static void buildFirstTimeFile(){
        // estos parametros seran declarado y se construiran por primera vez, 
        // aqui se deben de registrar todos los valores que se van a utilizar durante el proyecto
        log.info(NAME_FILE+" created.");
        for (PropertyConstant prop : PropertyConstant.values()) {
            try{
                save(prop.getKey(),prop.getValue());            
            }catch(IOException e){
                log.error(e);
            }
        }        
    }
    
}
