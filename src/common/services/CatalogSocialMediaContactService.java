package common.services;

import common.dao.CatalogSocialMediaContactDAO;
import common.exceptions.DataOriginException;
import common.model.CatalogSocialMediaContactModel;
import java.util.List;

public class CatalogSocialMediaContactService {
        
    private static CatalogSocialMediaContactService INSTANCE = null;
    private final CatalogSocialMediaContactDAO catalogSocialMediaContactDAO 
            = CatalogSocialMediaContactDAO.getInstance();

    // Private constructor suppresses 
    private CatalogSocialMediaContactService(){}

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private static synchronized void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new CatalogSocialMediaContactService();
        }
    }

    public static CatalogSocialMediaContactService getInstance() {
        if (INSTANCE == null){ 
            createInstance();
        }
        return INSTANCE;
    }
    
    public List<CatalogSocialMediaContactModel> getAll () throws DataOriginException {
        return catalogSocialMediaContactDAO.getAll();
    }
    
    public void saveOrUpdate (CatalogSocialMediaContactModel model) throws DataOriginException {
        catalogSocialMediaContactDAO.saveOrUpdate(model);
    }
}
