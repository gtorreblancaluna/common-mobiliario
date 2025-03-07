package common.dao;

import common.constants.ApplicationConstants;
import common.exceptions.DataOriginException;
import java.util.List;
import common.model.Usuario;
import common.utilities.MyBatisConnectionFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class UserDAO {
    
    private final static Logger log = Logger.getLogger(UserDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
    private static UserDAO INSTANCE = null;
 
    private UserDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new UserDAO();
        }
    }
    
    public static UserDAO getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    @SuppressWarnings("unchecked")
    public Usuario getByPassword(String password) throws DataOriginException {
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            Usuario usuario = (Usuario) session.selectOne("MapperUsuarios.getByPassword",password);
            if(usuario != null)
                log.debug("usuario obtenido es: "+usuario.getNombre()+" "+usuario.getApellidos());
            else
                log.debug("usuario no econtrado para la contrasenia: "+password);
            return usuario;
        } catch(Exception e){           
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public Usuario getById(Integer id) throws DataOriginException {
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            return (Usuario) session.selectOne("MapperUsuarios.getById",id);
        } catch(Exception e){           
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Usuario> getChoferes() throws DataOriginException{
        SqlSession session = sqlSessionFactory.openSession();
        log.debug("session db: "+session.getConnection());
        try {
            return (List<Usuario>) session.selectList("MapperUsuarios.getChoferes",ApplicationConstants.PUESTO_CHOFER);
        }catch(Exception e){           
            log.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Usuario> getUsersInCategoriesAlmacenAndEvent(Integer eventId) throws DataOriginException{
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            return (List<Usuario>) session.selectList("MapperUsuarios.getUsersInCategoriesAlmacenAndEvent",eventId);
        }catch(Exception e){           
            log.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Usuario> getUsersInCategoriesAlmacen() throws DataOriginException{
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            return (List<Usuario>) session.selectList("MapperUsuarios.getUsersInCategoriesAlmacen");
        }catch(Exception e){           
            log.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Usuario> getUsersCategoriesAlmacenAndEventInDesgloseAlmacen(Integer eventId) throws DataOriginException{
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            return (List<Usuario>) session.selectList("MapperUsuarios.getUsersCategoriesAlmacenAndEventInDesgloseAlmacen",eventId);
        }catch(Exception e){           
            log.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }
     
    
}
