package org.prometheus.dto;

public class CalculoValorDevidoMilheirosDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private Long nuFatura;
    private String deFatura;
    private Long nuTipoPreco;
    private String noTipoPreco;
    private java.math.BigDecimal vrPrecoGrupo;
    private java.math.BigDecimal qtSolicitacao;
    private java.math.BigDecimal vrPrevisto;

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

    public void setNuTipoPreco(Long nuTipoPreco){
        this.nuTipoPreco = nuTipoPreco;
    }

    public Long getNuTipoPreco(){
        return this.nuTipoPreco;
    }

    public void setNoTipoPreco(String noTipoPreco){
        this.noTipoPreco = noTipoPreco;
    }

    public String getNoTipoPreco(){
        return this.noTipoPreco;
    }

    public void setVrPrecoGrupo(java.math.BigDecimal vrPrecoGrupo){
        this.vrPrecoGrupo = vrPrecoGrupo;
    }

    public java.math.BigDecimal getVrPrecoGrupo(){
        return this.vrPrecoGrupo;
    }

    public void setQtSolicitacao(java.math.BigDecimal qtSolicitacao){
        this.qtSolicitacao = qtSolicitacao;
    }

    public java.math.BigDecimal getQtSolicitacao(){
        return this.qtSolicitacao;
    }

    public void setVrPrevisto(java.math.BigDecimal vrPrevisto){
        this.vrPrevisto = vrPrevisto;
    }

    public java.math.BigDecimal getVrPrevisto(){
        return this.vrPrevisto;
    }

}
