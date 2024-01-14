package common.services;

import common.dao.DesgloseAlmacenDAO;
import common.exceptions.DataOriginException;
import common.model.DesgloseAlmacenModel;
import java.util.List;

public class DesgloseAlmacenService {
        
    private static DesgloseAlmacenService INSTANCE = null;
    private final DesgloseAlmacenDAO desgloseAlmacenDao 
            = DesgloseAlmacenDAO.getInstance();

    // Private constructor suppresses 
    private DesgloseAlmacenService(){}

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new DesgloseAlmacenService();
        }
    }

    public static DesgloseAlmacenService getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    public DesgloseAlmacenModel getById (String id) throws DataOriginException {
        return desgloseAlmacenDao.getById(id);
    }
    
    public void delete (List<String> ids) throws DataOriginException {
        desgloseAlmacenDao.delete(ids);
    }
    
    public void saveOrUpdate (DesgloseAlmacenModel desgloseAlmacenModel) throws DataOriginException {
        desgloseAlmacenDao.saveOrUpdate(desgloseAlmacenModel);
    }
    
    public List<DesgloseAlmacenModel> getItemsDesgloseAlmacenByInitItem(String itemInitId) throws DataOriginException {
        return desgloseAlmacenDao.getItemsDesgloseAlmacenByInitItem(itemInitId);
    }
}
