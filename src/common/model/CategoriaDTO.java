package common.model;

import lombok.Data;


@Data
public class CategoriaDTO {    

    private Integer categoriaId;
    private String descripcion;

    public CategoriaDTO() {
    }

    public CategoriaDTO(Integer categoriaId, String descripcion) {
        this.categoriaId = categoriaId;
        this.descripcion = descripcion;
    }

    public CategoriaDTO(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }    
    

    @Override
    public String toString() {
        return descripcion;
    }
    
}
