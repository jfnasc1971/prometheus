package org.prometheus.dto;

public class VigenciaMembrosSprintDTO {

    private Long nuFatura;
    private Long nuEquipe;
    private Long nuCicloTrabalho;
    private java.util.Date dtInicioVigenciaMembros;
    private java.util.Date dtFinalVigenciaMembros;
    private java.math.BigDecimal qtMembrosEquipe;
    private java.math.BigDecimal qtDiasTrab;
    private java.math.BigDecimal qtDiasSprint;
    private java.math.BigDecimal qtEfetiva;

    public void setNuFatura(Long nuFatura){
        this.nuFatura = nuFatura;
    }

    public Long getNuFatura(){
        return this.nuFatura;
    }

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

    public void setDtInicioVigenciaMembros(java.util.Date dtInicioVigenciaMembros){
        this.dtInicioVigenciaMembros = dtInicioVigenciaMembros;
    }

    public java.util.Date getDtInicioVigenciaMembros(){
        return this.dtInicioVigenciaMembros;
    }

    public void setDtFinalVigenciaMembros(java.util.Date dtFinalVigenciaMembros){
        this.dtFinalVigenciaMembros = dtFinalVigenciaMembros;
    }

    public java.util.Date getDtFinalVigenciaMembros(){
        return this.dtFinalVigenciaMembros;
    }

    public void setQtMembrosEquipe(java.math.BigDecimal qtMembrosEquipe){
        this.qtMembrosEquipe = qtMembrosEquipe;
    }

    public java.math.BigDecimal getQtMembrosEquipe(){
        return this.qtMembrosEquipe;
    }

    public void setQtDiasTrab(java.math.BigDecimal qtDiasTrab){
        this.qtDiasTrab = qtDiasTrab;
    }

    public java.math.BigDecimal getQtDiasTrab(){
        return this.qtDiasTrab;
    }

    public void setQtDiasSprint(java.math.BigDecimal qtDiasSprint){
        this.qtDiasSprint = qtDiasSprint;
    }

    public java.math.BigDecimal getQtDiasSprint(){
        return this.qtDiasSprint;
    }

    public void setQtEfetiva(java.math.BigDecimal qtEfetiva){
        this.qtEfetiva = qtEfetiva;
    }

    public java.math.BigDecimal getQtEfetiva(){
        return this.qtEfetiva;
    }


}
