package org.prometheus.dto;

public class TermoAjusteInsumoDTO {

    private Long nuEquipe;
    private Long nuAjuste;
    private Long nuTermoEquipe;
    private String coTermo;
    private java.util.Date dhEmissaoTermo;
    private String deFatura;
    private java.util.Date dtFinalFatura;
    private java.math.BigDecimal vrFinanceiroTermo;
    private java.math.BigDecimal vrAjusteAnterior;
    private java.math.BigDecimal vrFinanceiroGerado;

    public void setNuEquipe(Long nuEquipe){
        this.nuEquipe = nuEquipe;
    }

    public Long getNuEquipe(){
        return this.nuEquipe;
    }

    public void setNuAjuste(Long nuAjuste){
        this.nuAjuste = nuAjuste;
    }

    public Long getNuAjuste(){
        return this.nuAjuste;
    }

    public void setNuTermoEquipe(Long nuTermoEquipe){
        this.nuTermoEquipe = nuTermoEquipe;
    }

    public Long getNuTermoEquipe(){
        return this.nuTermoEquipe;
    }

    public void setCoTermo(String coTermo){
        this.coTermo = coTermo;
    }

    public String getCoTermo(){
        return this.coTermo;
    }

    public void setDhEmissaoTermo(java.util.Date dhEmissaoTermo){
        this.dhEmissaoTermo = dhEmissaoTermo;
    }

    public java.util.Date getDhEmissaoTermo(){
        return this.dhEmissaoTermo;
    }

    public void setDeFatura(String deFatura){
        this.deFatura = deFatura;
    }

    public String getDeFatura(){
        return this.deFatura;
    }

    public void setDtFinalFatura(java.util.Date dtFinalFatura){
        this.dtFinalFatura = dtFinalFatura;
    }

    public java.util.Date getDtFinalFatura(){
        return this.dtFinalFatura;
    }

    public void setVrFinanceiroTermo(java.math.BigDecimal vrFinanceiroTermo){
        this.vrFinanceiroTermo = vrFinanceiroTermo;
    }

    public java.math.BigDecimal getVrFinanceiroTermo(){
        return this.vrFinanceiroTermo;
    }

    public void setVrAjusteAnterior(java.math.BigDecimal vrAjusteAnterior){
        this.vrAjusteAnterior = vrAjusteAnterior;
    }

    public java.math.BigDecimal getVrAjusteAnterior(){
        return this.vrAjusteAnterior;
    }

    public void setVrFinanceiroGerado(java.math.BigDecimal vrFinanceiroGerado){
        this.vrFinanceiroGerado = vrFinanceiroGerado;
    }

    public java.math.BigDecimal getVrFinanceiroGerado(){
        return this.vrFinanceiroGerado;
    }


}
