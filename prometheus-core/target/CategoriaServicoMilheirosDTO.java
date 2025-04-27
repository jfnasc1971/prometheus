package org.prometheus.dto;

public class CategoriaServicoMilheirosDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private Long nuContrato;
    private Long nuTipoGrupo;
    private Long nuTpCategoriaServico;
    private String noTpCategoriaServico;
    private java.util.Date dtInicioVigencia;
    private java.util.Date dtFinalVigencia;
    private String icAplicaDesconto;
    private java.math.BigDecimal pcParticipacao;

    public void setNuContrato(Long nuContrato){
        this.nuContrato = nuContrato;
    }

    public Long getNuContrato(){
        return this.nuContrato;
    }

    public void setNuTipoGrupo(Long nuTipoGrupo){
        this.nuTipoGrupo = nuTipoGrupo;
    }

    public Long getNuTipoGrupo(){
        return this.nuTipoGrupo;
    }

    public void setNuTpCategoriaServico(Long nuTpCategoriaServico){
        this.nuTpCategoriaServico = nuTpCategoriaServico;
    }

    public Long getNuTpCategoriaServico(){
        return this.nuTpCategoriaServico;
    }

    public void setNoTpCategoriaServico(String noTpCategoriaServico){
        this.noTpCategoriaServico = noTpCategoriaServico;
    }

    public String getNoTpCategoriaServico(){
        return this.noTpCategoriaServico;
    }

    public void setDtInicioVigencia(java.util.Date dtInicioVigencia){
        this.dtInicioVigencia = dtInicioVigencia;
    }

    public java.util.Date getDtInicioVigencia(){
        return this.dtInicioVigencia;
    }

    public void setDtFinalVigencia(java.util.Date dtFinalVigencia){
        this.dtFinalVigencia = dtFinalVigencia;
    }

    public java.util.Date getDtFinalVigencia(){
        return this.dtFinalVigencia;
    }

    public void setIcAplicaDesconto(String icAplicaDesconto){
        this.icAplicaDesconto = icAplicaDesconto;
    }

    public String getIcAplicaDesconto(){
        return this.icAplicaDesconto;
    }

    public void setPcParticipacao(java.math.BigDecimal pcParticipacao){
        this.pcParticipacao = pcParticipacao;
    }

    public java.math.BigDecimal getPcParticipacao(){
        return this.pcParticipacao;
    }

}
