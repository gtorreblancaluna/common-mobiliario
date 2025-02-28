
package common.model.providers.queryresult;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class BalanceProviderQueryResult {
    
    private String detailId;
    private String orderProviderId;
    private String rentaId;
    private String rentaFolio;
    private Timestamp detailDate;
    private String providerName;
    private String providerLastName;
    private Integer paymentCount;
    private Integer itemsCount;
    private Float importTotal;
    private Float paymentTotal;
    private Float balance;
    
}
