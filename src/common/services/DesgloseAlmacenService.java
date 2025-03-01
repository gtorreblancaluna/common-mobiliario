package common.services;

import common.dao.DesgloseAlmacenDAO;
import common.exceptions.DataOriginException;
import common.model.DesgloseAlmacenModel;
import java.util.List;

public class DesgloseAlmacenService {
        
    private static DesgloseAlmacenService instanceService = null;
    private final DesgloseAlmacenDAO desgloseAlmacenDao 
            = DesgloseAlmacenDAO.getInstance();

    // Private constructor suppresses 
    private DesgloseAlmacenService(){}

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private static synchronized  void createInstance() {
        if (instanceService == null) { 
            instanceService = new DesgloseAlmacenService();
        }
    }

    public static DesgloseAlmacenService getInstance() {
        if (instanceService == null){ 
            createInstance();
        }
        return instanceService;
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
