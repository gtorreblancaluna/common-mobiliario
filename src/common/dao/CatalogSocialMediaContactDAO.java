package common.dao;

import common.utilities.MyBatisConnectionFactory;
import common.exceptions.DataOriginException;
import common.model.CatalogSocialMediaContactModel;
import java.sql.Timestamp;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class CatalogSocialMediaContactDAO {
    
    private static CatalogSocialMediaContactDAO INSTANCE = null;
    private final SqlSessionFactory sqlSessionFactory;
    
    // Private constructor suppresses 
    private CatalogSocialMediaContactDAO(){
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new CatalogSocialMediaContactDAO();
        }
    }

    public static CatalogSocialMediaContactDAO getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    public List<CatalogSocialMediaContactModel> getAll () throws DataOriginException {
        SqlSession session = null;
        try{
            session = sqlSessionFactory.openSession();
            return (List<CatalogSocialMediaContactModel>) 
                    session.selectList("MapperCatalogSocialMediaContact.getAllCatalogSocialMediaContactModel");
        } catch(Exception e){           
            throw new DataOriginException(e.getMessage(),e);
        } finally {
             if (session != null)
                session.close();
        }
    }
    
    public void saveOrUpdate (CatalogSocialMediaContactModel model) throws DataOriginException {
        SqlSession session = null;
        try{
            session = sqlSessionFactory.openSession();
            if (model.getId() != null && model.getId() > 0) {
                model.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                session.update("MapperCatalogSocialMediaContact.updateCatalogSocialMedia",model);
            } else {
                model.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                model.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                session.insert("MapperCatalogSocialMediaContact.insertCatalogSocialMedia",model);
            }
            session.commit();
        } catch(Exception e){           
            throw new DataOriginException(e.getMessage(),e);
        } finally {
             if (session != null)
                session.close();
        }
    }
    
}
