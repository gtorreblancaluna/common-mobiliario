package common.model.providers;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import lombok.Data;

@Data
public class ParameterOrderProvider {
    
    private Integer orderId;
    private Integer limit;
    private Timestamp initDate;
    private Timestamp endDate;
    private String initEventDate;
    private String endEventDate;
    private String nameProvider;
    private String status;
    private Integer folioRenta;
    private List<String> orders;

    public void setInitDate(Timestamp initDate) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(initDate.getTime());
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 1);
        initDate.setTime(now.getTimeInMillis());
        this.initDate = initDate;
    }

    public void setEndDate(Timestamp endDate) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(endDate.getTime());
        now.set(Calendar.HOUR_OF_DAY, 23);
        now.set(Calendar.MINUTE, 23);
        now.set(Calendar.SECOND, 59);
        now.set(Calendar.MILLISECOND, 59);
        endDate.setTime(now.getTimeInMillis());
        this.endDate = endDate;
    }

    public void setStatus(String status) {
        String array[] = status.split("-");
        this.status = array[0].trim();
    }
    
    
    
}
