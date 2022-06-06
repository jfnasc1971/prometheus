package org.prometheus.dto;

public class IndicadorDTO {

    private Long nuFatura;
    private String deFatura;
    private java.util.Date dtInicioFatura;
    private java.util.Date dtFinalFatura;
    private Long nuEquipe;
    private Long nuTipoGrupo;
    private Long nuTipoIndicador;
    private String noTipoIndicador;
    private String sgTipoIndicador;
    private String icAplicaIndicador;
    private String icApenasFaturamentoAtual;
    private java.math.BigDecimal vrCalculoIndicador;
    private java.math.BigDecimal vrPesoTipoIndicador;
    private java.math.BigDecimal vrFaixaFns;
    private java.util.Date dhGeracaoIndicadorEquipe;

    public void setNuFatura(Long nuFatura){
        this.nuFatura = nuFatura;
    }

    public Long getNuFatura(){
        return this.nuFatura;
    }

    public void setDeFatura(String deFatura){
        this.deFatura = deFatura;
    }

    public String getDeFatura(){
        return this.deFatura;
    }

    public void setDtInicioFatura(java.util.Date dtInicioFatura){
        this.dtInicioFatura = dtInicioFatura;
    }

    public java.util.Date getDtInicioFatura(){
        return this.dtInicioFatura;
    }

    public void setDtFinalFatura(java.util.Date dtFinalFatura){
        this.dtFinalFatura = dtFinalFatura;
    }

    public java.util.Date getDtFinalFatura(){
        return this.dtFinalFatura;
    }

    public void setNuEquipe(Long nuEquipe){
        this.nuEquipe = nuEquipe;
    }

    public Long getNuEquipe(){
        return this.nuEquipe;
    }

    public void setNuTipoGrupo(Long nuTipoGrupo){
        this.nuTipoGrupo = nuTipoGrupo;
    }

    public Long getNuTipoGrupo(){
        return this.nuTipoGrupo;
    }

    public void setNuTipoIndicador(Long nuTipoIndicador){
        this.nuTipoIndicador = nuTipoIndicador;
    }

    public Long getNuTipoIndicador(){
        return this.nuTipoIndicador;
    }

    public void setNoTipoIndicador(String noTipoIndicador){
        this.noTipoIndicador = noTipoIndicador;
    }

    public String getNoTipoIndicador(){
        return this.noTipoIndicador;
    }

    public void setSgTipoIndicador(String sgTipoIndicador){
        this.sgTipoIndicador = sgTipoIndicador;
    }

    public String getSgTipoIndicador(){
        return this.sgTipoIndicador;
    }

    public void setIcAplicaIndicador(String icAplicaIndicador){
        this.icAplicaIndicador = icAplicaIndicador;
    }

    public String getIcAplicaIndicador(){
        return this.icAplicaIndicador;
    }

    public void setIcApenasFaturamentoAtual(String icApenasFaturamentoAtual){
        this.icApenasFaturamentoAtual = icApenasFaturamentoAtual;
    }

    public String getIcApenasFaturamentoAtual(){
        return this.icApenasFaturamentoAtual;
    }

    public void setVrCalculoIndicador(java.math.BigDecimal vrCalculoIndicador){
        this.vrCalculoIndicador = vrCalculoIndicador;
    }

    public java.math.BigDecimal getVrCalculoIndicador(){
        return this.vrCalculoIndicador;
    }

    public void setVrPesoTipoIndicador(java.math.BigDecimal vrPesoTipoIndicador){
        this.vrPesoTipoIndicador = vrPesoTipoIndicador;
    }

    public java.math.BigDecimal getVrPesoTipoIndicador(){
        return this.vrPesoTipoIndicador;
    }

    public void setVrFaixaFns(java.math.BigDecimal vrFaixaFns){
        this.vrFaixaFns = vrFaixaFns;
    }

    public java.math.BigDecimal getVrFaixaFns(){
        return this.vrFaixaFns;
    }

    public void setDhGeracaoIndicadorEquipe(java.util.Date dhGeracaoIndicadorEquipe){
        this.dhGeracaoIndicadorEquipe = dhGeracaoIndicadorEquipe;
    }

    public java.util.Date getDhGeracaoIndicadorEquipe(){
        return this.dhGeracaoIndicadorEquipe;
    }


}
