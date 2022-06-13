/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common.model;

/**
 *
 * @author jerry
 * POJO para contener el tipo de evento para la renta
 * 2018.11.21
 */
public class Tipo {
    private Integer tipoId;
    private String tipo;

    public Tipo(int tipoId, String tipo) {
        this.tipoId = tipoId;
        this.tipo = tipo;
    }
    
    public Tipo(int tipoId) {
        this.tipoId = tipoId;
    }

    public Tipo() {
    }

    public Integer getTipoId() {
        return tipoId;
    }

    public void setTipoId(Integer tipoId) {
        this.tipoId = tipoId;
    }
    
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return tipo;
    }
    
    
    
    
}