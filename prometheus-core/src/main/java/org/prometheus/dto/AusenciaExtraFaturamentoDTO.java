package org.prometheus.dto;

public class AusenciaExtraFaturamentoDTO {

    private Long nuAmostra;
    private String deFatura;
    private Long nuEquipe;
    private Long nuCicloTrabalho;
    private Long nuFaturaAmostra;
    private Long nuPerfil;
    private String dePerfil;
    private java.math.BigDecimal qtDiaAusencia;
    private java.math.BigDecimal qtDiaServicoExtra;
    private java.util.Date dhInclusaoInformacao;

    public void setNuAmostra(Long nuAmostra) {
        this.nuAmostra = nuAmostra;
    }

    public Long getNuAmostra() {
        return this.nuAmostra;
    }

    public void setNuEquipe(Long nuEquipe) {
        this.nuEquipe = nuEquipe;
    }

    public Long getNuEquipe() {
        return this.nuEquipe;
    }

    public void setNuFaturaAmostra(Long nuFaturaAmostra) {
        this.nuFaturaAmostra = nuFaturaAmostra;
    }

    public Long getNuFaturaAmostra() {
        return this.nuFaturaAmostra;
    }

    public void setNuPerfil(Long nuPerfil) {
        this.nuPerfil = nuPerfil;
    }

    public Long getNuPerfil() {
        return this.nuPerfil;
    }

    public void setDePerfil(String dePerfil) {
        this.dePerfil = dePerfil;
    }

    public String getDePerfil() {
        return this.dePerfil;
    }

    public void setQtDiaAusencia(java.math.BigDecimal qtDiaAusencia) {
        this.qtDiaAusencia = qtDiaAusencia;
    }

    public java.math.BigDecimal getQtDiaAusencia() {
        return this.qtDiaAusencia;
    }

    public void setQtDiaServicoExtra(java.math.BigDecimal qtDiaServicoExtra) {
        this.qtDiaServicoExtra = qtDiaServicoExtra;
    }

    public java.math.BigDecimal getQtDiaServicoExtra() {
        return this.qtDiaServicoExtra;
    }

    public String getDeFatura() {
        return deFatura;
    }

    public void setDeFatura(String deFatura) {
        this.deFatura = deFatura;
    }

    public Long getNuCicloTrabalho() {
        return nuCicloTrabalho;
    }

    public void setNuCicloTrabalho(Long nuCicloTrabalho) {
        this.nuCicloTrabalho = nuCicloTrabalho;
    }

    /**
     * @return the dhInclusaoInformacao
     */
    public java.util.Date getDhInclusaoInformacao() {
        return dhInclusaoInformacao;
    }

    /**
     * @param dhInclusaoInformacao the dhInclusaoInformacao to set
     */
    public void setDhInclusaoInformacao(java.util.Date dhInclusaoInformacao) {
        this.dhInclusaoInformacao = dhInclusaoInformacao;
    }

}
