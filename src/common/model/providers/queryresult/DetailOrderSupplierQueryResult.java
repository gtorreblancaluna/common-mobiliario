
package common.model.providers.queryresult;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class DetailOrderSupplierQueryResult {
    
    private Long orderSupplierId;
    private Long orderSupplierDetailId;
    private Long rentaId;
    private String folio;
    private String product;
    private Float amount;
    private Float price;
    private Float total;
    private String eventDate;
    private String user;
    private String supplier;
    private String orderComment;
    private String detailComment;
    private String orderDetailType;
    private Timestamp creado;
    private Float pagosProveedor;
    
}
