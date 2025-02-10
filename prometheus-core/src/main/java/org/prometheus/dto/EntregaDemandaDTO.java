package org.prometheus.dto;

public class EntregaDemandaDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private Long nuOrdemServico;
    private String coOrdemServico;
    private Long nuEntrega;
    private String coEntrega;
    private java.util.Date dhPrevistaEntrega;
    private java.util.Date dhEfetivaEntrega;
    private String deSituacaoEntrega;
    private java.math.BigDecimal pcEntrega;
    private java.math.BigDecimal pcExecutado;
    private Long nuSituacaoEntrega;
    private java.math.BigDecimal vrPreco;

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

    public void setNuEntrega(Long nuEntrega){
        this.nuEntrega = nuEntrega;
    }

    public Long getNuEntrega(){
        return this.nuEntrega;
    }

    public void setCoEntrega(String coEntrega){
        this.coEntrega = coEntrega;
    }

    public String getCoEntrega(){
        return this.coEntrega;
    }

    public void setDhPrevistaEntrega(java.util.Date dhPrevistaEntrega){
        this.dhPrevistaEntrega = dhPrevistaEntrega;
    }

    public java.util.Date getDhPrevistaEntrega(){
        return this.dhPrevistaEntrega;
    }

    public void setDhEfetivaEntrega(java.util.Date dhEfetivaEntrega){
        this.dhEfetivaEntrega = dhEfetivaEntrega;
    }

    public java.util.Date getDhEfetivaEntrega(){
        return this.dhEfetivaEntrega;
    }

    public void setDeSituacaoEntrega(String deSituacaoEntrega){
        this.deSituacaoEntrega = deSituacaoEntrega;
    }

    public String getDeSituacaoEntrega(){
        return this.deSituacaoEntrega;
    }

    public void setPcEntrega(java.math.BigDecimal pcEntrega){
        this.pcEntrega = pcEntrega;
    }

    public java.math.BigDecimal getPcEntrega(){
        return this.pcEntrega;
    }

    public void setPcExecutado(java.math.BigDecimal pcExecutado){
        this.pcExecutado = pcExecutado;
    }

    public java.math.BigDecimal getPcExecutado(){
        return this.pcExecutado;
    }

    public void setNuSituacaoEntrega(Long nuSituacaoEntrega){
        this.nuSituacaoEntrega = nuSituacaoEntrega;
    }

    public Long getNuSituacaoEntrega(){
        return this.nuSituacaoEntrega;
    }

    public void setVrPreco(java.math.BigDecimal vrPreco){
        this.vrPreco = vrPreco;
    }

    public java.math.BigDecimal getVrPreco(){
        return this.vrPreco;
    }

}
