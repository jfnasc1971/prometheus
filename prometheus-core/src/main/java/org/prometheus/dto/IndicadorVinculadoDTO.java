package org.prometheus.dto;

public class IndicadorVinculadoDTO {

	private Long nuEquipe;
	private String coEquipe;
	private String noTipoGrupo;
	private String sgTipoIndicador;
	private String noTipoIndicador;
	private java.util.Date dtReferencia;
	private java.math.BigDecimal vrCalculoIndicador;
	private java.math.BigDecimal vrFaixaFns;
	private java.util.Date dhGeracaoIndicadorEquipe;
	private Long nuAjuste;

	public void setNuEquipe(Long nuEquipe) {
		this.nuEquipe = nuEquipe;
	}

	public Long getNuEquipe() {
		return this.nuEquipe;
	}

	public void setCoEquipe(String coEquipe) {
		this.coEquipe = coEquipe;
	}

	public String getCoEquipe() {
		return this.coEquipe;
	}

	public void setNoTipoGrupo(String noTipoGrupo) {
		this.noTipoGrupo = noTipoGrupo;
	}

	public String getNoTipoGrupo() {
		return this.noTipoGrupo;
	}

	public void setSgTipoIndicador(String sgTipoIndicador) {
		this.sgTipoIndicador = sgTipoIndicador;
	}

	public String getSgTipoIndicador() {
		return this.sgTipoIndicador;
	}

	public void setNoTipoIndicador(String noTipoIndicador) {
		this.noTipoIndicador = noTipoIndicador;
	}

	public String getNoTipoIndicador() {
		return this.noTipoIndicador;
	}

	public void setDtReferencia(java.util.Date dtReferencia) {
		this.dtReferencia = dtReferencia;
	}

	public java.util.Date getDtReferencia() {
		return this.dtReferencia;
	}

	public void setVrCalculoIndicador(java.math.BigDecimal vrCalculoIndicador) {
		this.vrCalculoIndicador = vrCalculoIndicador;
	}

	public java.math.BigDecimal getVrCalculoIndicador() {
		return this.vrCalculoIndicador;
	}

	public void setVrFaixaFns(java.math.BigDecimal vrFaixaFns) {
		this.vrFaixaFns = vrFaixaFns;
	}

	public java.math.BigDecimal getVrFaixaFns() {
		return this.vrFaixaFns;
	}

	public void setDhGeracaoIndicadorEquipe(java.util.Date dhGeracaoIndicadorEquipe) {
		this.dhGeracaoIndicadorEquipe = dhGeracaoIndicadorEquipe;
	}

	public java.util.Date getDhGeracaoIndicadorEquipe() {
		return this.dhGeracaoIndicadorEquipe;
	}

	public void setNuAjuste(Long nuAjuste) {
		this.nuAjuste = nuAjuste;
	}

	public Long getNuAjuste() {
		return this.nuAjuste;
	}

}
