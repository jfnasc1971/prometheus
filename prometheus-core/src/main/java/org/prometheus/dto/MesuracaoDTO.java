package org.prometheus.dto;

public class MesuracaoDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private Long nuOrdemServico;
    private String coOrdemServico;
    private String coSolicitacao;
    private Long nuSolicitacao;
    private String noTipoQuantidade;
    private java.math.BigDecimal qtSolicitacao;
    private java.math.BigDecimal vrAjusteFinanceiro;
    private java.math.BigDecimal qtSolicitacaoAjustado;

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

    public void setCoSolicitacao(String coSolicitacao){
        this.coSolicitacao = coSolicitacao;
    }

    public String getCoSolicitacao(){
        return this.coSolicitacao;
    }

    public void setNuSolicitacao(Long nuSolicitacao){
        this.nuSolicitacao = nuSolicitacao;
    }

    public Long getNuSolicitacao(){
        return this.nuSolicitacao;
    }

    public void setNoTipoQuantidade(String noTipoQuantidade){
        this.noTipoQuantidade = noTipoQuantidade;
    }

    public String getNoTipoQuantidade(){
        return this.noTipoQuantidade;
    }

    public void setQtSolicitacao(java.math.BigDecimal qtSolicitacao){
        this.qtSolicitacao = qtSolicitacao;
    }

    public java.math.BigDecimal getQtSolicitacao(){
        return this.qtSolicitacao;
    }

    public void setVrAjusteFinanceiro(java.math.BigDecimal vrAjusteFinanceiro){
        this.vrAjusteFinanceiro = vrAjusteFinanceiro;
    }

    public java.math.BigDecimal getVrAjusteFinanceiro(){
        return this.vrAjusteFinanceiro;
    }

    public void setQtSolicitacaoAjustado(java.math.BigDecimal qtSolicitacaoAjustado){
        this.qtSolicitacaoAjustado = qtSolicitacaoAjustado;
    }

    public java.math.BigDecimal getQtSolicitacaoAjustado(){
        return this.qtSolicitacaoAjustado;
    }

}
