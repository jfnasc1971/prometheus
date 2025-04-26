package org.prometheus.dto;

public class QuantidadePrecoMilheirosDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private Long nuTipoPreco;
    private String noTipoPreco;
    private java.math.BigDecimal vrPrecoGrupo;
    private java.math.BigDecimal qtSolicitacao;

    public void setNuTipoPreco(Long nuTipoPreco) {
        this.nuTipoPreco = nuTipoPreco;
    }

    public Long getNuTipoPreco() {
        return this.nuTipoPreco;
    }

    public void setNoTipoPreco(String noTipoPreco) {
        this.noTipoPreco = noTipoPreco;
    }

    public String getNoTipoPreco() {
        return this.noTipoPreco;
    }

    public void setVrPrecoGrupo(java.math.BigDecimal vrPrecoGrupo) {
        this.vrPrecoGrupo = vrPrecoGrupo;
    }

    public java.math.BigDecimal getVrPrecoGrupo() {
        return this.vrPrecoGrupo;
    }

    public void setQtSolicitacao(java.math.BigDecimal qtSolicitacao) {
        this.qtSolicitacao = qtSolicitacao;
    }

    public java.math.BigDecimal getQtSolicitacao() {
        return this.qtSolicitacao;
    }

}
