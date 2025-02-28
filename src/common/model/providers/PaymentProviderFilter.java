package common.model.providers;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentProviderFilter {
    
    private String orderId;
    private String providerId;
    private int limit;
    private String nameProvider;
    private Timestamp initDateEnBodega;
    private Timestamp endDateEnBodega;
    private Timestamp initDate;
    private Timestamp endDate;
    private Timestamp initEventDate;
    private Timestamp endEventDate;
    private String folioRenta;
}
