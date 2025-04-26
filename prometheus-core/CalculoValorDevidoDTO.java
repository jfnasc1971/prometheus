package org.prometheus.dto;

public class CalculoValorDevidoDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private Long nuFatura;
    private java.util.Date dtInicioFatura;
    private java.util.Date dtFinalFatura;
    private Long nuPerfil;
    private String dePerfil;
    private String coSitio;
    private Long nuEquipe;
    private java.math.BigDecimal qtDiasFaturamento;
    private java.math.BigDecimal vrPrecoGrupo;
    private java.math.BigDecimal qtDiasExtra;
    private java.math.BigDecimal qtDiasAusencia;
    private java.math.BigDecimal qtMembrosEfetiva;

    public void setNuFatura(Long nuFatura){
        this.nuFatura = nuFatura;
    }

    public Long getNuFatura(){
        return this.nuFatura;
    }

    public void setDtInicioFatura(java.util.Date dtInicioFatura){
        this.dtInicioFatura = dtInicioFatura;
    }

    public java.util.Date getDtInicioFatura(){
        return this.dtInicioFatura;
    }

    public void setDtFinalFatura(java.util.Date dtFinalFatura){
        this.dtFinalFatura = dtFinalFatura;
    }

    public java.util.Date getDtFinalFatura(){
        return this.dtFinalFatura;
    }

    public void setNuPerfil(Long nuPerfil){
        this.nuPerfil = nuPerfil;
    }

    public Long getNuPerfil(){
        return this.nuPerfil;
    }

    public void setDePerfil(String dePerfil){
        this.dePerfil = dePerfil;
    }

    public String getDePerfil(){
        return this.dePerfil;
    }

    public void setCoSitio(String coSitio){
        this.coSitio = coSitio;
    }

    public String getCoSitio(){
        return this.coSitio;
    }

    public void setNuEquipe(Long nuEquipe){
        this.nuEquipe = nuEquipe;
    }

    public Long getNuEquipe(){
        return this.nuEquipe;
    }

    public void setQtDiasFaturamento(java.math.BigDecimal qtDiasFaturamento){
        this.qtDiasFaturamento = qtDiasFaturamento;
    }

    public java.math.BigDecimal getQtDiasFaturamento(){
        return this.qtDiasFaturamento;
    }

    public void setVrPrecoGrupo(java.math.BigDecimal vrPrecoGrupo){
        this.vrPrecoGrupo = vrPrecoGrupo;
    }

    public java.math.BigDecimal getVrPrecoGrupo(){
        return this.vrPrecoGrupo;
    }

    public void setQtDiasExtra(java.math.BigDecimal qtDiasExtra){
        this.qtDiasExtra = qtDiasExtra;
    }

    public java.math.BigDecimal getQtDiasExtra(){
        return this.qtDiasExtra;
    }

    public void setQtDiasAusencia(java.math.BigDecimal qtDiasAusencia){
        this.qtDiasAusencia = qtDiasAusencia;
    }

    public java.math.BigDecimal getQtDiasAusencia(){
        return this.qtDiasAusencia;
    }

    public void setQtMembrosEfetiva(java.math.BigDecimal qtMembrosEfetiva){
        this.qtMembrosEfetiva = qtMembrosEfetiva;
    }

    public java.math.BigDecimal getQtMembrosEfetiva(){
        return this.qtMembrosEfetiva;
    }

}
