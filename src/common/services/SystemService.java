package common.services;

import common.dao.SystemDAO;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.model.DatosGenerales;

public class SystemService {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SystemService.class.getName());
    
    private SystemService(){}
    
    private static final SystemService SINGLE_INSTANCE = null;
    
    public static SystemService getInstance(){
        
        if (SINGLE_INSTANCE == null) {
            return new SystemService();
        }
        return SINGLE_INSTANCE;
    } 
    
    private final SystemDAO systemDao = SystemDAO.getInstance();

    public String getDataConfigurationByKey(String key)throws BusinessException{
        String result;
        try{
            result = systemDao.getDataConfigurationByKey(key);
        }catch(DataOriginException e){
            throw new BusinessException(e.getMessage());
        }
        
        return result;
    }
    
    public DatosGenerales getGeneralData(){
        return systemDao.getGeneralData();
    }
    
    public void saveDatosGenerales(DatosGenerales datosGenerales){
        systemDao.saveDatosGenerales(datosGenerales);
    }
    
    public void updateFolio(String folio) throws BusinessException{
        systemDao.updateFolio(folio);
    }
    
    public void updateInfoPDFSummary(DatosGenerales datosGenerales) throws DataOriginException{
        systemDao.updateInfoPDFSummary(datosGenerales);
    }
    
    public void updateInfoPDFSummaryVenta(DatosGenerales datosGenerales) throws DataOriginException{
        systemDao.updateInfoPDFSummaryVenta(datosGenerales);
    }
    
    
}
