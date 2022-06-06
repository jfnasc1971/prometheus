package org.prometheus.dto;

public class TermoAjusteDTO {

    private Long nuEquipe;
    private Long nuTermoEquipe;
    private String coTermo;
    private java.util.Date dtFinalFatura;
    private String deFatura;
    private java.math.BigDecimal vrFinanceiroTermo;

    public void setNuEquipe(Long nuEquipe){
        this.nuEquipe = nuEquipe;
    }

    public Long getNuEquipe(){
        return this.nuEquipe;
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

    public void setDtFinalFatura(java.util.Date dtFinalFatura){
        this.dtFinalFatura = dtFinalFatura;
    }

    public java.util.Date getDtFinalFatura(){
        return this.dtFinalFatura;
    }

    public void setDeFatura(String deFatura){
        this.deFatura = deFatura;
    }

    public String getDeFatura(){
        return this.deFatura;
    }

    public void setVrFinanceiroTermo(java.math.BigDecimal vrFinanceiroTermo){
        this.vrFinanceiroTermo = vrFinanceiroTermo;
    }

    public java.math.BigDecimal getVrFinanceiroTermo(){
        return this.vrFinanceiroTermo;
    }


}
