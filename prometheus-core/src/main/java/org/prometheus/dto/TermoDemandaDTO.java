package org.prometheus.dto;

public class TermoDemandaDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private Long nuDemanda;
    private String coDemanda;
    private Long nuOrdemServico;
    private String coOrdemServico;
    private Long nuTermo;
    private String coTermo;
    private java.util.Date dhEmissaoTermo;
    private java.util.Date dtFinalFaturaPaga;
    private Long nuFaturaPaga;
    private String deFaturaPaga;
    private java.util.Date dtFinalFaturaGerada;
    private Long nuFaturaGerada;
    private String deFaturaGerada;
    private java.math.BigDecimal vrFinanceiroTermo;

    public void setNuDemanda(Long nuDemanda){
        this.nuDemanda = nuDemanda;
    }

    public Long getNuDemanda(){
        return this.nuDemanda;
    }

    public void setCoDemanda(String coDemanda){
        this.coDemanda = coDemanda;
    }

    public String getCoDemanda(){
        return this.coDemanda;
    }

    public void setNuOrdemServico(Long nuOrdemServico){
        this.nuOrdemServico = nuOrdemServico;
    }

    public Long getNuOrdemServico(){
        return this.nuOrdemServico;
    }

    public void setCoOrdemServico(String coOrdemServico){
        this.coOrdemServico = coOrdemServico;
    }

    public String getCoOrdemServico(){
        return this.coOrdemServico;
    }

    public void setNuTermo(Long nuTermo){
        this.nuTermo = nuTermo;
    }

    public Long getNuTermo(){
        return this.nuTermo;
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

    public void setDtFinalFaturaPaga(java.util.Date dtFinalFaturaPaga){
        this.dtFinalFaturaPaga = dtFinalFaturaPaga;
    }

    public java.util.Date getDtFinalFaturaPaga(){
        return this.dtFinalFaturaPaga;
    }

    public void setNuFaturaPaga(Long nuFaturaPaga){
        this.nuFaturaPaga = nuFaturaPaga;
    }

    public Long getNuFaturaPaga(){
        return this.nuFaturaPaga;
    }

    public void setDeFaturaPaga(String deFaturaPaga){
        this.deFaturaPaga = deFaturaPaga;
    }

    public String getDeFaturaPaga(){
        return this.deFaturaPaga;
    }

    public void setDtFinalFaturaGerada(java.util.Date dtFinalFaturaGerada){
        this.dtFinalFaturaGerada = dtFinalFaturaGerada;
    }

    public java.util.Date getDtFinalFaturaGerada(){
        return this.dtFinalFaturaGerada;
    }

    public void setNuFaturaGerada(Long nuFaturaGerada){
        this.nuFaturaGerada = nuFaturaGerada;
    }

    public Long getNuFaturaGerada(){
        return this.nuFaturaGerada;
    }

    public void setDeFaturaGerada(String deFaturaGerada){
        this.deFaturaGerada = deFaturaGerada;
    }

    public String getDeFaturaGerada(){
        return this.deFaturaGerada;
    }

    public void setVrFinanceiroTermo(java.math.BigDecimal vrFinanceiroTermo){
        this.vrFinanceiroTermo = vrFinanceiroTermo;
    }

    public java.math.BigDecimal getVrFinanceiroTermo(){
        return this.vrFinanceiroTermo;
    }

}
