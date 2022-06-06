package org.prometheus.dto;

public class TermoDTO {

	private Long nuFaturaGerada;
	private String deFatura;
	private Long nuFaturaPaga;
	private String deFaturaPaga;
	private Long nuEquipe;
	private Long nuTermoEquipe;
	private Long nuTipoTermo;
	private String coTermo;
	private java.util.Date dhEmissaoTermo;
	private java.math.BigDecimal qtMembroEquipeVigente;
	private java.math.BigDecimal vrPrevistoEquipe;
	private java.math.BigDecimal diaServicoExtra;
	private java.math.BigDecimal vrAcrescimo;
	private java.math.BigDecimal qtDiaAusencia;
	private java.math.BigDecimal vrDecrescimo;
	private java.math.BigDecimal vrFinanceiroTermoOriginal;
	private java.math.BigDecimal vrFns;
	private java.math.BigDecimal vrFinanceiroTermo;
	private java.math.BigDecimal vrPrecoGrupo;

	public void setNuFaturaGerada(Long nuFaturaGerada) {
		this.nuFaturaGerada = nuFaturaGerada;
	}

	public Long getNuFaturaGerada() {
		return this.nuFaturaGerada;
	}

	public void setDeFatura(String deFatura) {
		this.deFatura = deFatura;
	}

	public String getDeFatura() {
		return this.deFatura;
	}

	public void setNuFaturaPaga(Long nuFaturaPaga) {
		this.nuFaturaPaga = nuFaturaPaga;
	}

	public Long getNuFaturaPaga() {
		return this.nuFaturaPaga;
	}

	public void setDeFaturaPaga(String deFaturaPaga) {
		this.deFaturaPaga = deFaturaPaga;
	}

	public String getDeFaturaPaga() {
		return this.deFaturaPaga;
	}

	public void setNuEquipe(Long nuEquipe) {
		this.nuEquipe = nuEquipe;
	}

	public Long getNuEquipe() {
		return this.nuEquipe;
	}

	public void setNuTermoEquipe(Long nuTermoEquipe) {
		this.nuTermoEquipe = nuTermoEquipe;
	}

	public Long getNuTermoEquipe() {
		return this.nuTermoEquipe;
	}

	public void setCoTermo(String coTermo) {
		this.coTermo = coTermo;
	}

	public String getCoTermo() {
		return this.coTermo;
	}

	public void setDhEmissaoTermo(java.util.Date dhEmissaoTermo) {
		this.dhEmissaoTermo = dhEmissaoTermo;
	}

	public java.util.Date getDhEmissaoTermo() {
		return this.dhEmissaoTermo;
	}

	public void setQtMembroEquipeVigente(java.math.BigDecimal qtMembroEquipeVigente) {
		this.qtMembroEquipeVigente = qtMembroEquipeVigente;
	}

	public java.math.BigDecimal getQtMembroEquipeVigente() {
		return this.qtMembroEquipeVigente;
	}

	public void setVrPrevistoEquipe(java.math.BigDecimal vrPrevistoEquipe) {
		this.vrPrevistoEquipe = vrPrevistoEquipe;
	}

	public java.math.BigDecimal getVrPrevistoEquipe() {
		return this.vrPrevistoEquipe;
	}

	public void setDiaServicoExtra(java.math.BigDecimal diaServicoExtra) {
		this.diaServicoExtra = diaServicoExtra;
	}

	public java.math.BigDecimal getDiaServicoExtra() {
		return this.diaServicoExtra;
	}

	public void setVrAcrescimo(java.math.BigDecimal vrAcrescimo) {
		this.vrAcrescimo = vrAcrescimo;
	}

	public java.math.BigDecimal getVrAcrescimo() {
		return this.vrAcrescimo;
	}

	public void setQtDiaAusencia(java.math.BigDecimal qtDiaAusencia) {
		this.qtDiaAusencia = qtDiaAusencia;
	}

	public java.math.BigDecimal getQtDiaAusencia() {
		return this.qtDiaAusencia;
	}

	public void setVrDecrescimo(java.math.BigDecimal vrDecrescimo) {
		this.vrDecrescimo = vrDecrescimo;
	}

	public java.math.BigDecimal getVrDecrescimo() {
		return this.vrDecrescimo;
	}

	public void setVrFinanceiroTermoOriginal(java.math.BigDecimal vrFinanceiroTermoOriginal) {
		this.vrFinanceiroTermoOriginal = vrFinanceiroTermoOriginal;
	}

	public java.math.BigDecimal getVrFinanceiroTermoOriginal() {
		return this.vrFinanceiroTermoOriginal;
	}

	public void setVrFns(java.math.BigDecimal vrFns) {
		this.vrFns = vrFns;
	}

	public java.math.BigDecimal getVrFns() {
		return this.vrFns;
	}

	public void setVrFinanceiroTermo(java.math.BigDecimal vrFinanceiroTermo) {
		this.vrFinanceiroTermo = vrFinanceiroTermo;
	}

	public java.math.BigDecimal getVrFinanceiroTermo() {
		return this.vrFinanceiroTermo;
	}

	/**
	 * @return the vrPrecoGrupo
	 */
	public java.math.BigDecimal getVrPrecoGrupo() {
		return vrPrecoGrupo;
	}

	/**
	 * @param vrPrecoGrupo the vrPrecoGrupo to set
	 */
	public void setVrPrecoGrupo(java.math.BigDecimal vrPrecoGrupo) {
		this.vrPrecoGrupo = vrPrecoGrupo;
	}

	/**
	 * @return the nuTipoTermo
	 */
	public Long getNuTipoTermo() {
		return nuTipoTermo;
	}

	/**
	 * @param nuTipoTermo the nuTipoTermo to set
	 */
	public void setNuTipoTermo(Long nuTipoTermo) {
		this.nuTipoTermo = nuTipoTermo;
	}

}
