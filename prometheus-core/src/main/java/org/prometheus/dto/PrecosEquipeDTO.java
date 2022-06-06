package org.prometheus.dto;

public class PrecosEquipeDTO {

    private Long nuTipoGrupo;
    private String noTipoGrupo;
    private Long nuContrato;
    private Long nuTipoPreco;
    private java.util.Date dtInicioVigencia;
    private java.util.Date dtFinalVigencia;
    private Long nuUnidadeMedida;
    private java.math.BigDecimal vrPrecoGrupo;
    private String icPrecoPadrao;

    public void setNuTipoGrupo(Long nuTipoGrupo){
        this.nuTipoGrupo = nuTipoGrupo;
    }

    public Long getNuTipoGrupo(){
        return this.nuTipoGrupo;
    }

    public void setNoTipoGrupo(String noTipoGrupo){
        this.noTipoGrupo = noTipoGrupo;
    }

    public String getNoTipoGrupo(){
        return this.noTipoGrupo;
    }

    public void setNuContrato(Long nuContrato){
        this.nuContrato = nuContrato;
    }

    public Long getNuContrato(){
        return this.nuContrato;
    }

    public void setNuTipoPreco(Long nuTipoPreco){
        this.nuTipoPreco = nuTipoPreco;
    }

    public Long getNuTipoPreco(){
        return this.nuTipoPreco;
    }

    public void setDtInicioVigencia(java.util.Date dtInicioVigencia){
        this.dtInicioVigencia = dtInicioVigencia;
    }

    public java.util.Date getDtInicioVigencia(){
        return this.dtInicioVigencia;
    }

    public void setDtFinalVigencia(java.util.Date dtFinalVigencia){
        this.dtFinalVigencia = dtFinalVigencia;
    }

    public java.util.Date getDtFinalVigencia(){
        return this.dtFinalVigencia;
    }

    public void setNuUnidadeMedida(Long nuUnidadeMedida){
        this.nuUnidadeMedida = nuUnidadeMedida;
    }

    public Long getNuUnidadeMedida(){
        return this.nuUnidadeMedida;
    }

    public void setVrPrecoGrupo(java.math.BigDecimal vrPrecoGrupo){
        this.vrPrecoGrupo = vrPrecoGrupo;
    }

    public java.math.BigDecimal getVrPrecoGrupo(){
        return this.vrPrecoGrupo;
    }

    public void setIcPrecoPadrao(String icPrecoPadrao){
        this.icPrecoPadrao = icPrecoPadrao;
    }

    public String getIcPrecoPadrao(){
        return this.icPrecoPadrao;
    }


}
