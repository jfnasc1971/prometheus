package org.prometheus.dto;

public class VigenciaMembrosDTO {

	private Long nuEquipe;
	private Long nuPerfil;
	private String dePerfil;
	private java.util.Date dtInicioVigenciaMembros;
	private java.util.Date dtFinalVigenciaMembros;
	private java.math.BigDecimal qtMembrosEquipe;
	private java.math.BigDecimal qtDiasTrabalhados;
	private java.math.BigDecimal qtDiasFaturamento;
	private java.math.BigDecimal qtEfetiva;

	public void setNuEquipe(Long nuEquipe) {
		this.nuEquipe = nuEquipe;
	}

	public Long getNuEquipe() {
		return this.nuEquipe;
	}

	public void setDtInicioVigenciaMembros(java.util.Date dtInicioVigenciaMembros) {
		this.dtInicioVigenciaMembros = dtInicioVigenciaMembros;
	}

	public java.util.Date getDtInicioVigenciaMembros() {
		return this.dtInicioVigenciaMembros;
	}

	public void setDtFinalVigenciaMembros(java.util.Date dtFinalVigenciaMembros) {
		this.dtFinalVigenciaMembros = dtFinalVigenciaMembros;
	}

	public java.util.Date getDtFinalVigenciaMembros() {
		return this.dtFinalVigenciaMembros;
	}

	public void setQtMembrosEquipe(java.math.BigDecimal qtMembrosEquipe) {
		this.qtMembrosEquipe = qtMembrosEquipe;
	}

	public java.math.BigDecimal getQtMembrosEquipe() {
		return this.qtMembrosEquipe;
	}

	public void setQtDiasTrabalhados(java.math.BigDecimal qtDiasTrabalhados) {
		this.qtDiasTrabalhados = qtDiasTrabalhados;
	}

	public java.math.BigDecimal getQtDiasTrabalhados() {
		return this.qtDiasTrabalhados;
	}

	public void setQtDiasFaturamento(java.math.BigDecimal qtDiasFaturamento) {
		this.qtDiasFaturamento = qtDiasFaturamento;
	}

	public java.math.BigDecimal getQtDiasFaturamento() {
		return this.qtDiasFaturamento;
	}

	public void setQtEfetiva(java.math.BigDecimal qtEfetiva) {
		this.qtEfetiva = qtEfetiva;
	}

	public java.math.BigDecimal getQtEfetiva() {
		return this.qtEfetiva;
	}

	/**
	 * @return the nuPerfil
	 */
	public Long getNuPerfil() {
		return nuPerfil;
	}

	/**
	 * @param nuPerfil the nuPerfil to set
	 */
	public void setNuPerfil(Long nuPerfil) {
		this.nuPerfil = nuPerfil;
	}

	/**
	 * @return the dePerfil
	 */
	public String getDePerfil() {
		return dePerfil;
	}

	/**
	 * @param dePerfil the dePerfil to set
	 */
	public void setDePerfil(String dePerfil) {
		this.dePerfil = dePerfil;
	}

}
