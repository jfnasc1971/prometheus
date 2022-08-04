package org.prometheus.dto;

public class EntregaDemandaDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;

	private String coDemanda;
	private Long nuDemanda;
	private Long nuOrdemServico;
	private Long nuEntrega;
	private String coEntrega;
	private java.math.BigDecimal pcEntrega;
	private java.math.BigDecimal pcExecutado;
	private java.util.Date dhPrevistaInicioAtendimento;
	private java.util.Date dhPrevistaEntrega;
	private Long qtDiasPendencia;
	private java.util.Date dhPrevistaEntregaPendencia;
	private java.util.Date dhEfetivaEntrega;
	private java.math.BigDecimal vrPrevistoEntrega;

	public void setCoDemanda(String coDemanda) {
		this.coDemanda = coDemanda;
	}

	public String getCoDemanda() {
		return this.coDemanda;
	}

	public void setNuDemanda(Long nuDemanda) {
		this.nuDemanda = nuDemanda;
	}

	public Long getNuDemanda() {
		return this.nuDemanda;
	}

	public void setNuOrdemServico(Long nuOrdemServico) {
		this.nuOrdemServico = nuOrdemServico;
	}

	public Long getNuOrdemServico() {
		return this.nuOrdemServico;
	}

	public void setNuEntrega(Long nuEntrega) {
		this.nuEntrega = nuEntrega;
	}

	public Long getNuEntrega() {
		return this.nuEntrega;
	}

	public void setCoEntrega(String coEntrega) {
		this.coEntrega = coEntrega;
	}

	public String getCoEntrega() {
		return this.coEntrega;
	}

	public void setPcEntrega(java.math.BigDecimal pcEntrega) {
		this.pcEntrega = pcEntrega;
	}

	public java.math.BigDecimal getPcEntrega() {
		return this.pcEntrega;
	}

	public void setPcExecutado(java.math.BigDecimal pcExecutado) {
		this.pcExecutado = pcExecutado;
	}

	public java.math.BigDecimal getPcExecutado() {
		return this.pcExecutado;
	}

	public void setDhPrevistaInicioAtendimento(java.util.Date dhPrevistaInicioAtendimento) {
		this.dhPrevistaInicioAtendimento = dhPrevistaInicioAtendimento;
	}

	public java.util.Date getDhPrevistaInicioAtendimento() {
		return this.dhPrevistaInicioAtendimento;
	}

	public void setDhPrevistaEntrega(java.util.Date dhPrevistaEntrega) {
		this.dhPrevistaEntrega = dhPrevistaEntrega;
	}

	public java.util.Date getDhPrevistaEntrega() {
		return this.dhPrevistaEntrega;
	}

	public void setQtDiasPendencia(Long qtDiasPendencia) {
		this.qtDiasPendencia = qtDiasPendencia;
	}

	public Long getQtDiasPendencia() {
		return this.qtDiasPendencia;
	}

	public void setDhPrevistaEntregaPendencia(java.util.Date dhPrevistaEntregaPendencia) {
		this.dhPrevistaEntregaPendencia = dhPrevistaEntregaPendencia;
	}

	public java.util.Date getDhPrevistaEntregaPendencia() {
		return this.dhPrevistaEntregaPendencia;
	}

	public void setDhEfetivaEntrega(java.util.Date dhEfetivaEntrega) {
		this.dhEfetivaEntrega = dhEfetivaEntrega;
	}

	public java.util.Date getDhEfetivaEntrega() {
		return this.dhEfetivaEntrega;
	}

	public void setVrPrevistoEntrega(java.math.BigDecimal vrPrevistoEntrega) {
		this.vrPrevistoEntrega = vrPrevistoEntrega;
	}

	public java.math.BigDecimal getVrPrevistoEntrega() {
		return this.vrPrevistoEntrega;
	}

}
