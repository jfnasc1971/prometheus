package org.prometheus.dto;

public class DemandaDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private String coContrato;
    private String noContrato;
    private Long nuContrato;
    private String coDemanda;
    private String noTipoGrupo;
    private String noModulo;
    private String sgSistema;
    private String noSistema;
    private String deSituacaoDemanda;
    private Long nuDemanda;
    private Long nuModulo;
    private Long nuSistema;
    private String icOrdemServicoAcionamento;
    private String icQuantidade;
    private String icValorOsInformado;
    private String icCalculoQuantidadeOs;
    private String icQuantidadeOs;

    public void setNoContrato(String noContrato) {
        this.noContrato = noContrato;
    }

    public String getNoContrato() {
        return this.noContrato;
    }

    public void setNuContrato(Long nuContrato) {
        this.nuContrato = nuContrato;
    }

    public Long getNuContrato() {
        return this.nuContrato;
    }

    public void setCoDemanda(String coDemanda) {
        this.coDemanda = coDemanda;
    }

    public String getCoDemanda() {
        return this.coDemanda;
    }

    public void setNoTipoGrupo(String noTipoGrupo) {
        this.noTipoGrupo = noTipoGrupo;
    }

    public String getNoTipoGrupo() {
        return this.noTipoGrupo;
    }

    public void setNoModulo(String noModulo) {
        this.noModulo = noModulo;
    }

    public String getNoModulo() {
        return this.noModulo;
    }

    public void setSgSistema(String sgSistema) {
        this.sgSistema = sgSistema;
    }

    public String getSgSistema() {
        return this.sgSistema;
    }

    public void setNoSistema(String noSistema) {
        this.noSistema = noSistema;
    }

    public String getNoSistema() {
        return this.noSistema;
    }

    public void setDeSituacaoDemanda(String deSituacaoDemanda) {
        this.deSituacaoDemanda = deSituacaoDemanda;
    }

    public String getDeSituacaoDemanda() {
        return this.deSituacaoDemanda;
    }

    public void setNuDemanda(Long nuDemanda) {
        this.nuDemanda = nuDemanda;
    }

    public Long getNuDemanda() {
        return this.nuDemanda;
    }

    public void setNuModulo(Long nuModulo) {
        this.nuModulo = nuModulo;
    }

    public Long getNuModulo() {
        return this.nuModulo;
    }

    public void setNuSistema(Long nuSistema) {
        this.nuSistema = nuSistema;
    }

    public Long getNuSistema() {
        return this.nuSistema;
    }

    public void setIcOrdemServicoAcionamento(String icOrdemServicoAcionamento) {
        this.icOrdemServicoAcionamento = icOrdemServicoAcionamento;
    }

    public String getIcOrdemServicoAcionamento() {
        return this.icOrdemServicoAcionamento;
    }

    public void setIcQuantidade(String icQuantidade) {
        this.icQuantidade = icQuantidade;
    }

    public String getIcQuantidade() {
        return this.icQuantidade;
    }

    public void setIcValorOsInformado(String icValorOsInformado) {
        this.icValorOsInformado = icValorOsInformado;
    }

    public String getIcValorOsInformado() {
        return this.icValorOsInformado;
    }

    public void setIcCalculoQuantidadeOs(String icCalculoQuantidadeOs) {
        this.icCalculoQuantidadeOs = icCalculoQuantidadeOs;
    }

    public String getIcCalculoQuantidadeOs() {
        return this.icCalculoQuantidadeOs;
    }

    public void setIcQuantidadeOs(String icQuantidadeOs) {
        this.icQuantidadeOs = icQuantidadeOs;
    }

    public String getIcQuantidadeOs() {
        return this.icQuantidadeOs;
    }

    public String getCoContrato() {
        return coContrato;
    }

    public void setCoContrato(String coContrato) {
        this.coContrato = coContrato;
    }

}
