package common.services.providers;

import common.dao.providers.ProvidersPaymentsDAO;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import java.util.List;
import common.model.providers.PagosProveedor;
import common.model.providers.PaymentProviderFilter;
import common.model.providers.queryresult.BalanceProviderQueryResult;

/**
 *
 * @author gerardo torreblanca luna
 */
public class ProvidersPaymentsService {
    
    
    private ProvidersPaymentsService(){}
    
    private static final ProvidersPaymentsService SINGLE_INSTANCE = new ProvidersPaymentsService();
    public static ProvidersPaymentsService getInstance(){
        return SINGLE_INSTANCE;
    }
    
    private final ProvidersPaymentsDAO providersPaymentsDAO = ProvidersPaymentsDAO.getInstance();
    
    public List<BalanceProviderQueryResult> getSaldosProveedor(PaymentProviderFilter filter)throws BusinessException{
        try{
            return providersPaymentsDAO.getSaldosProveedor(filter);

        }catch(DataOriginException e){
            throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
    public List<PagosProveedor> getByFilter(PaymentProviderFilter filter)throws BusinessException{
        try{
            return providersPaymentsDAO.getByFilter(filter);
        }catch(DataOriginException e){
            throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
    public PagosProveedor getById(Long id)throws BusinessException{
        try{
            return providersPaymentsDAO.getById(id);
        }catch(DataOriginException e){
            throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
    
    public List<PagosProveedor> getAllProviderPaymentsByOrderId(Long orderId)throws BusinessException{
        try{
            return providersPaymentsDAO.getAllProviderPaymentsByOrderId(orderId);
        }catch(DataOriginException e){
            throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
    public void addPayment(PagosProveedor pagosProveedor)throws BusinessException{
        try{
            providersPaymentsDAO.addPayment(pagosProveedor);
         }catch(DataOriginException e){
            throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
    public void delete(Long id)throws DataOriginException{
       providersPaymentsDAO.delete(id);
    }
    
}
