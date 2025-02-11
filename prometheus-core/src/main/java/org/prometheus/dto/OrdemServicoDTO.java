package org.prometheus.dto;

public class OrdemServicoDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private Long nuOrdemServico;
    private String coOrdemServico;
    private String noTipoGrupo;
    private String noTipoServico;
    private String deTipoForma;
    private java.math.BigDecimal vrEstmoOrdemServico;
    private java.math.BigDecimal qtOrdemServico;
    private Long nuStcoOrdemServico;
    private String deStcoOrdemServico;

    public void setNuOrdemServico(Long nuOrdemServico) {
        this.nuOrdemServico = nuOrdemServico;
    }

    public Long getNuOrdemServico() {
        return this.nuOrdemServico;
    }

    public void setCoOrdemServico(String coOrdemServico) {
        this.coOrdemServico = coOrdemServico;
    }

    public String getCoOrdemServico() {
        return this.coOrdemServico;
    }

    public void setNoTipoGrupo(String noTipoGrupo) {
        this.noTipoGrupo = noTipoGrupo;
    }

    public String getNoTipoGrupo() {
        return this.noTipoGrupo;
    }

    public void setNoTipoServico(String noTipoServico) {
        this.noTipoServico = noTipoServico;
    }

    public String getNoTipoServico() {
        return this.noTipoServico;
    }

    public void setDeTipoForma(String deTipoForma) {
        this.deTipoForma = deTipoForma;
    }

    public String getDeTipoForma() {
        return this.deTipoForma;
    }

    public void setVrEstmoOrdemServico(java.math.BigDecimal vrEstmoOrdemServico) {
        this.vrEstmoOrdemServico = vrEstmoOrdemServico;
    }

    public java.math.BigDecimal getVrEstmoOrdemServico() {
        return this.vrEstmoOrdemServico;
    }

    public java.math.BigDecimal getQtOrdemServico() {
        return qtOrdemServico;
    }

    public void setQtOrdemServico(java.math.BigDecimal qtOrdemServico) {
        this.qtOrdemServico = qtOrdemServico;
    }

    public Long getNuStcoOrdemServico() {
        return nuStcoOrdemServico;
    }

    public void setNuStcoOrdemServico(Long nuStcoOrdemServico) {
        this.nuStcoOrdemServico = nuStcoOrdemServico;
    }

    public String getDeStcoOrdemServico() {
        return deStcoOrdemServico;
    }

    public void setDeStcoOrdemServico(String deStcoOrdemServico) {
        this.deStcoOrdemServico = deStcoOrdemServico;
    }

}
