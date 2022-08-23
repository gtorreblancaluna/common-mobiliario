
package common.services;

import common.exceptions.DataOriginException;
import common.dao.UserDAO;
import java.util.List;
import common.model.Usuario;
import org.apache.log4j.Logger;

public class UserService {
    private final static Logger log = Logger.getLogger(UserService.class.getName());
    
    private final UserDAO usuariosDao;
    private static UserService INSTANCE = null;
   
    // Private constructor suppresses 
    private UserService(){
        usuariosDao = UserDAO.getInstance();
    }

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new UserService();
        }
    }

    public static UserService getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }

    public List<Usuario> getChoferes () throws DataOriginException {
        return usuariosDao.getChoferes();
    }
    
    public Usuario getByPassword(String psw) throws DataOriginException{
        return usuariosDao.getByPassword(psw);
    }
    
    public Usuario getById(Integer id) throws DataOriginException{
        return usuariosDao.getById(id);
    }
    
    public Boolean checkAlReadyPassword(String password) throws DataOriginException {
        Usuario usuario = usuariosDao.getByPassword(password);
        return usuario != null;
    }
    
    public List<Usuario> getUsersInCategoriesAlmacenAndEvent (Integer eventId) throws DataOriginException {
        return usuariosDao.getUsersInCategoriesAlmacenAndEvent(eventId);
    }
    
    public List<Usuario> getUsersInCategoriesAlmacen () throws DataOriginException {
        return usuariosDao.getUsersInCategoriesAlmacen();
    }
        
}
