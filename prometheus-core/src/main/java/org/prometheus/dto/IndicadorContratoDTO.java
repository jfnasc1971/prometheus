package org.prometheus.dto;

public class IndicadorContratoDTO {

    private String sgTipoIndicador;
    private String noTipoIndicador;
    private java.math.BigDecimal vrPesoTipoIndicador;
    private java.math.BigDecimal vrFatorAjuste;
    private java.math.BigDecimal qtFaturamento;
    private java.math.BigDecimal vrLimiteMaximoIndicador;
    private String icApenasFaturamentoAtual;

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

    public void setVrPesoTipoIndicador(java.math.BigDecimal vrPesoTipoIndicador){
        this.vrPesoTipoIndicador = vrPesoTipoIndicador;
    }

    public java.math.BigDecimal getVrPesoTipoIndicador(){
        return this.vrPesoTipoIndicador;
    }

    public void setVrFatorAjuste(java.math.BigDecimal vrFatorAjuste){
        this.vrFatorAjuste = vrFatorAjuste;
    }

    public java.math.BigDecimal getVrFatorAjuste(){
        return this.vrFatorAjuste;
    }

    public void setQtFaturamento(java.math.BigDecimal qtFaturamento){
        this.qtFaturamento = qtFaturamento;
    }

    public java.math.BigDecimal getQtFaturamento(){
        return this.qtFaturamento;
    }

    public void setVrLimiteMaximoIndicador(java.math.BigDecimal vrLimiteMaximoIndicador){
        this.vrLimiteMaximoIndicador = vrLimiteMaximoIndicador;
    }

    public java.math.BigDecimal getVrLimiteMaximoIndicador(){
        return this.vrLimiteMaximoIndicador;
    }

    public void setIcApenasFaturamentoAtual(String icApenasFaturamentoAtual){
        this.icApenasFaturamentoAtual = icApenasFaturamentoAtual;
    }

    public String getIcApenasFaturamentoAtual(){
        return this.icApenasFaturamentoAtual;
    }


}
