package common.model;

import java.util.Date;

public class TaskChoferDeliveryVO {
    
    private Long id;
    private Renta renta;
    private StatusAlmacenTaskCatalogVO statusAlmacenTaskCatalogVO;
    private AttendAlmacenTaskTypeCatalogVO attendAlmacenTaskTypeCatalogVO;
    private Date createdAt;
    private Date updatedAt;
    private Usuario chofer;
    private String fgActive;
    private Boolean pendingToPayEvent;

    public Boolean getPendingToPayEvent() {
        return pendingToPayEvent;
    }

    public void setPendingToPayEvent(Boolean pendingToPayEvent) {
        this.pendingToPayEvent = pendingToPayEvent;
    }
    
    

    public String getFgActive() {
        return fgActive;
    }

    public void setFgActive(String fgActive) {
        this.fgActive = fgActive;
    }
    
    public Usuario getChofer() {
        return chofer;
    }

    public void setChofer(Usuario chofer) {
        this.chofer = chofer;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Renta getRenta() {
        return renta;
    }

    public void setRenta(Renta renta) {
        this.renta = renta;
    }

    public StatusAlmacenTaskCatalogVO getStatusAlmacenTaskCatalogVO() {
        return statusAlmacenTaskCatalogVO;
    }

    public void setStatusAlmacenTaskCatalogVO(StatusAlmacenTaskCatalogVO statusAlmacenTaskCatalogVO) {
        this.statusAlmacenTaskCatalogVO = statusAlmacenTaskCatalogVO;
    }

    public AttendAlmacenTaskTypeCatalogVO getAttendAlmacenTaskTypeCatalogVO() {
        return attendAlmacenTaskTypeCatalogVO;
    }

    public void setAttendAlmacenTaskTypeCatalogVO(AttendAlmacenTaskTypeCatalogVO attendAlmacenTaskTypeCatalogVO) {
        this.attendAlmacenTaskTypeCatalogVO = attendAlmacenTaskTypeCatalogVO;
    }
    
    
    
}
