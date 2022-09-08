package common.model;

import java.util.Date;

public class TaskAlmacenVO {
    
    private Long id;
    private Renta renta;
    private StatusAlmacenTaskCatalogVO statusAlmacenTaskCatalogVO;
    private AttendAlmacenTaskTypeCatalogVO attendAlmacenTaskTypeCatalogVO;
    private Date createdAt;
    private Date updatedAt;
    private Usuario userByCategory;
    private String fgActive;
    private Usuario user;

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }
    
    

    public String getFgActive() {
        return fgActive;
    }

    public void setFgActive(String fgActive) {
        this.fgActive = fgActive;
    }

    public Usuario getUserByCategory() {
        return userByCategory;
    }

    public void setUserByCategory(Usuario userByCategory) {
        this.userByCategory = userByCategory;
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
