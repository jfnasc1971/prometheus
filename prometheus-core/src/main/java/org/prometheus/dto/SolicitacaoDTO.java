package org.prometheus.dto;

import java.math.BigDecimal;

public class SolicitacaoDTO {

    private Long nuEquipe;
    private Long nuCicloTrabalho;
    private String coSolicitacao;
    private String deTipoSolicitacao;
    private String noTipoServico;
    private java.util.Date dhPrevista;
    private java.util.Date dhEfetiva;
    private java.util.Date dhResolucao;
    private BigDecimal nuTamanhoServico;

    public void setNuEquipe(Long nuEquipe){
        this.nuEquipe = nuEquipe;
    }

    public Long getNuEquipe(){
        return this.nuEquipe;
    }

    public void setNuCicloTrabalho(Long nuCicloTrabalho){
        this.nuCicloTrabalho = nuCicloTrabalho;
    }

    public Long getNuCicloTrabalho(){
        return this.nuCicloTrabalho;
    }

    public void setCoSolicitacao(String coSolicitacao){
        this.coSolicitacao = coSolicitacao;
    }

    public String getCoSolicitacao(){
        return this.coSolicitacao;
    }

    public void setDeTipoSolicitacao(String deTipoSolicitacao){
        this.deTipoSolicitacao = deTipoSolicitacao;
    }

    public String getDeTipoSolicitacao(){
        return this.deTipoSolicitacao;
    }

    public void setNoTipoServico(String noTipoServico){
        this.noTipoServico = noTipoServico;
    }

    public String getNoTipoServico(){
        return this.noTipoServico;
    }

    public void setDhPrevista(java.util.Date dhPrevista){
        this.dhPrevista = dhPrevista;
    }

    public java.util.Date getDhPrevista(){
        return this.dhPrevista;
    }

    public void setDhEfetiva(java.util.Date dhEfetiva){
        this.dhEfetiva = dhEfetiva;
    }

    public java.util.Date getDhEfetiva(){
        return this.dhEfetiva;
    }

    public void setDhResolucao(java.util.Date dhResolucao){
        this.dhResolucao = dhResolucao;
    }

    public java.util.Date getDhResolucao(){
        return this.dhResolucao;
    }

    public void setNuTamanhoServico(BigDecimal nuTamanhoServico){
        this.nuTamanhoServico = nuTamanhoServico;
    }

    public BigDecimal getNuTamanhoServico(){
        return this.nuTamanhoServico;
    }


}
