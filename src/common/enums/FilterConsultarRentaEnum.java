package common.enums;

public enum FilterConsultarRentaEnum {
    
    INIT_CREATED_DATE(0,"initCreatedDate"),
    END_CREATED_DATE(1,"endCreatedDate"),
    LIMIT(2,"limit"),
    TYPE(3,"type"),
    CUSTOMER(4,"customer"),
    INIT_DELIVERY_DATE(5,"initDeliveryDate"),
    END_DELIVERY_DATE(6,"endDeliveryDate"),
    INIT_EVENT_DATE(7,"initEventDate"),
    END_EVENT_DATE(8,"endEventDate"),
    STATUS_ID(9,"statusId"),
    DRIVER_ID(10,"driverId"),
    CURRENT_STATUS_ID(11,"currentStatusId"),
    CHANGE_STATUS_ID(12,"changeStatusId"),
    ORDER_STATUS_CHANGE_INIT_DATE(13,"orderStatusChangeInitDate"),
    ORDER_STATUS_CHANGE_END_DATE(14,"orderStatusChangeEndDate"),
    CURRENT_TYPE_ID(15,"currentTypeId"),
    CHANGE_TYPE_ID(16,"changeTypeId"),
    ORDER_TYPE_CHANGE_INIT_DATE(17,"orderTypeChangeInitDate"),
    ORDER_TYPE_CHANGE_END_DATE(18,"orderTypeChangeEndDate"),
    FOLIO(19,"folio");
    
    private FilterConsultarRentaEnum (int number, String name) {
        this.number = number;
        this.name = name;
    }
    
    private final int number;
    private final String name;
    
    public int getNumber () {
        return number;
    }
    public String getName () {
        return name;
    }
    
    public static FilterConsultarRentaEnum searchByKey(String key) {
        for (FilterConsultarRentaEnum filter : FilterConsultarRentaEnum.values()) {
            if (filter.getName().equals(key)) {
                return filter;
            }
        }
        return null;
    }
    
    
}
