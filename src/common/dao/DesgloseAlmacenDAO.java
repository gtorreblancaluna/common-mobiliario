package common.dao;

import common.constants.ApplicationConstants;
import common.exceptions.DataOriginException;
import common.utilities.MyBatisConnectionFactory;
import common.model.DesgloseAlmacenModel;
import java.sql.Timestamp;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class DesgloseAlmacenDAO {
    
    private static DesgloseAlmacenDAO instanceDao = null;
    private final SqlSessionFactory sqlSessionFactory;
    private static final Logger log = Logger.getLogger(DesgloseAlmacenDAO.class.getName());
    
    // Private constructor suppresses 
    private DesgloseAlmacenDAO(){
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private static synchronized void createInstance() {
        if (instanceDao == null) { 
            instanceDao = new DesgloseAlmacenDAO();
        }
    }

    public static DesgloseAlmacenDAO getInstance() {
        if (instanceDao == null){ 
            createInstance();
        }
        return instanceDao;
    }
    
    @SuppressWarnings("unchecked")
    public DesgloseAlmacenModel getById(String id) throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            return (DesgloseAlmacenModel) 
                    session.selectOne("MapperDesgloseAlmacen.getDesgloseAlmacenById",id);
        }catch(Exception e){
            log.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            session.close();
        }
    }
    
    public void delete (List<String> ids) throws DataOriginException {
        SqlSession session = null;
        try{
            session = sqlSessionFactory.openSession();            
            session.update("MapperDesgloseAlmacen.deleteDesgloseAlmacenByIds",ids);            
            session.commit();
        } catch(Exception e){           
            throw new DataOriginException(e.getMessage(),e);
        } finally {
             if (session != null)
                session.close();
        }
    }
    
    public void saveOrUpdate (DesgloseAlmacenModel desgloseAlmacenModel) throws DataOriginException {
        SqlSession session = null;
        try{
            session = sqlSessionFactory.openSession();
            desgloseAlmacenModel.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            if (desgloseAlmacenModel.getId() != null && desgloseAlmacenModel.getId() > 0) {
                session.update("MapperDesgloseAlmacen.updateDesgloseAlmacen",desgloseAlmacenModel);
            } else {
                desgloseAlmacenModel.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                desgloseAlmacenModel.setFgActive(ApplicationConstants.FG_ACTIVE_TRUE);
                session.insert("MapperDesgloseAlmacen.insertDesgloseAlmacen",desgloseAlmacenModel);
            }
            session.commit();
        } catch(Exception e){           
            throw new DataOriginException(e.getMessage(),e);
        } finally {
             if (session != null)
                session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<DesgloseAlmacenModel> getItemsDesgloseAlmacenByInitItem(String itemInitId) throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            return (List<DesgloseAlmacenModel>) 
                    session.selectList("MapperDesgloseAlmacen.getItemsDesgloseAlmacenByInitItem",itemInitId);
        }catch(Exception e){
            log.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            session.close();
        }
    }
    

    
}
