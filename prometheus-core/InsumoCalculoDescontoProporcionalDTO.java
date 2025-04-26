package org.prometheus.dto;

public class InsumoCalculoDescontoProporcionalDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private Long nuFerramentaSuporte;
    private String noFerramentaSuporte;
    private java.math.BigDecimal pcParticipacao;
    private java.math.BigDecimal qtMinutosFaturamento;
    private java.math.BigDecimal qtMinutosDisponivel;
    private java.math.BigDecimal qtMinutosIndisponivel;

    public void setNuFerramentaSuporte(Long nuFerramentaSuporte){
        this.nuFerramentaSuporte = nuFerramentaSuporte;
    }

    public Long getNuFerramentaSuporte(){
        return this.nuFerramentaSuporte;
    }

    public void setNoFerramentaSuporte(String noFerramentaSuporte){
        this.noFerramentaSuporte = noFerramentaSuporte;
    }

    public String getNoFerramentaSuporte(){
        return this.noFerramentaSuporte;
    }

    public void setPcParticipacao(java.math.BigDecimal pcParticipacao){
        this.pcParticipacao = pcParticipacao;
    }

    public java.math.BigDecimal getPcParticipacao(){
        return this.pcParticipacao;
    }

    public void setQtMinutosFaturamento(java.math.BigDecimal qtMinutosFaturamento){
        this.qtMinutosFaturamento = qtMinutosFaturamento;
    }

    public java.math.BigDecimal getQtMinutosFaturamento(){
        return this.qtMinutosFaturamento;
    }

    public void setQtMinutosDisponivel(java.math.BigDecimal qtMinutosDisponivel){
        this.qtMinutosDisponivel = qtMinutosDisponivel;
    }

    public java.math.BigDecimal getQtMinutosDisponivel(){
        return this.qtMinutosDisponivel;
    }

    public void setQtMinutosIndisponivel(java.math.BigDecimal qtMinutosIndisponivel){
        this.qtMinutosIndisponivel = qtMinutosIndisponivel;
    }

    public java.math.BigDecimal getQtMinutosIndisponivel(){
        return this.qtMinutosIndisponivel;
    }

}
