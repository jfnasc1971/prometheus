package org.prometheus.dto;

public class FaixaIndicadorDTO {

    private String sgTipoIndicador;
    private String noTipoIndicador;
    private Long nuTipoFaixa;
    private java.math.BigDecimal qtInicioFaixa;
    private java.math.BigDecimal qtFinalFaixa;
    private java.math.BigDecimal vrFatorFaixa;
    private Long nuAcaoHistorico;
    private Long nuMtrcaAcaoHstro;

    public void setSgTipoIndicador(String sgTipoIndicador){
        this.sgTipoIndicador = sgTipoIndicador;
    }

    public String getSgTipoIndicador(){
        return this.sgTipoIndicador;
    }

    public void setNoTipoIndicador(String noTipoIndicador){
        this.noTipoIndicador = noTipoIndicador;
    }

    public String getNoTipoIndicador(){
        return this.noTipoIndicador;
    }

    public void setNuTipoFaixa(Long nuTipoFaixa){
        this.nuTipoFaixa = nuTipoFaixa;
    }

    public Long getNuTipoFaixa(){
        return this.nuTipoFaixa;
    }

    public void setQtInicioFaixa(java.math.BigDecimal qtInicioFaixa){
        this.qtInicioFaixa = qtInicioFaixa;
    }

    public java.math.BigDecimal getQtInicioFaixa(){
        return this.qtInicioFaixa;
    }

    public void setQtFinalFaixa(java.math.BigDecimal qtFinalFaixa){
        this.qtFinalFaixa = qtFinalFaixa;
    }

    public java.math.BigDecimal getQtFinalFaixa(){
        return this.qtFinalFaixa;
    }

    public void setVrFatorFaixa(java.math.BigDecimal vrFatorFaixa){
        this.vrFatorFaixa = vrFatorFaixa;
    }

    public java.math.BigDecimal getVrFatorFaixa(){
        return this.vrFatorFaixa;
    }

    public void setNuAcaoHistorico(Long nuAcaoHistorico){
        this.nuAcaoHistorico = nuAcaoHistorico;
    }

    public Long getNuAcaoHistorico(){
        return this.nuAcaoHistorico;
    }

    public void setNuMtrcaAcaoHstro(Long nuMtrcaAcaoHstro){
        this.nuMtrcaAcaoHstro = nuMtrcaAcaoHstro;
    }

    public Long getNuMtrcaAcaoHstro(){
        return this.nuMtrcaAcaoHstro;
    }


}
