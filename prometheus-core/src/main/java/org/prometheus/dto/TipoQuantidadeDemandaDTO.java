package org.prometheus.dto;

public class TipoQuantidadeDemandaDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private Long nuTipoQuantidade;
    private String noTipoQuantidade;
    private java.math.BigDecimal vrAjusteFinanceiro;

    public void setNuTipoQuantidade(Long nuTipoQuantidade){
        this.nuTipoQuantidade = nuTipoQuantidade;
    }

    public Long getNuTipoQuantidade(){
        return this.nuTipoQuantidade;
    }

    public void setNoTipoQuantidade(String noTipoQuantidade){
        this.noTipoQuantidade = noTipoQuantidade;
    }

    public String getNoTipoQuantidade(){
        return this.noTipoQuantidade;
    }

    public void setVrAjusteFinanceiro(java.math.BigDecimal vrAjusteFinanceiro){
        this.vrAjusteFinanceiro = vrAjusteFinanceiro;
    }

    public java.math.BigDecimal getVrAjusteFinanceiro(){
        return this.vrAjusteFinanceiro;
    }

}
