package org.prometheus.dto;

public class QntdeMembrosEquipeDTO {

	private Long nuEquipe;
	private java.util.Date dtInicioVigenciaMembros;
	private java.util.Date dtFinalVigenciaMembros;
	private java.math.BigDecimal qtMembrosEquipe;
	private Long nuAcaoHistorico;
	private Long nuMtrcaAcaoHstro;
	private java.util.Date dhInclusaoInformacao;
	private String deJustificativaAlteracao;
	private Long qtIntervalos;
	private String dePerfil;
	private String noMatricula;

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

	public void setNuAcaoHistorico(Long nuAcaoHistorico) {
		this.nuAcaoHistorico = nuAcaoHistorico;
	}

	public Long getNuAcaoHistorico() {
		return this.nuAcaoHistorico;
	}

	public void setNuMtrcaAcaoHstro(Long nuMtrcaAcaoHstro) {
		this.nuMtrcaAcaoHstro = nuMtrcaAcaoHstro;
	}

	public Long getNuMtrcaAcaoHstro() {
		return this.nuMtrcaAcaoHstro;
	}

	public void setDhInclusaoInformacao(java.util.Date dhInclusaoInformacao) {
		this.dhInclusaoInformacao = dhInclusaoInformacao;
	}

	public java.util.Date getDhInclusaoInformacao() {
		return this.dhInclusaoInformacao;
	}

	public void setDeJustificativaAlteracao(String deJustificativaAlteracao) {
		this.deJustificativaAlteracao = deJustificativaAlteracao;
	}

	public String getDeJustificativaAlteracao() {
		return this.deJustificativaAlteracao;
	}

	/**
	 * @return the qtIntervalos
	 */
	public Long getQtIntervalos() {
		return qtIntervalos;
	}

	/**
	 * @param qtIntervalos the qtIntervalos to set
	 */
	public void setQtIntervalos(Long qtIntervalos) {
		this.qtIntervalos = qtIntervalos;
	}

	public String getDePerfil() {
		return dePerfil;
	}

	public void setDePerfil(String dePerfil) {
		this.dePerfil = dePerfil;
	}

	/**
	 * @return the noMatricula
	 */
	public String getNoMatricula() {
		return noMatricula;
	}

	/**
	 * @param noMatricula the noMatricula to set
	 */
	public void setNoMatricula(String noMatricula) {
		this.noMatricula = noMatricula;
	}

	@Override
	public String toString() {
		return "QntdeMembrosEquipeDTO [nuEquipe=" + nuEquipe + ", dtInicioVigenciaMembros=" + dtInicioVigenciaMembros
				+ ", dtFinalVigenciaMembros=" + dtFinalVigenciaMembros + ", qtMembrosEquipe=" + qtMembrosEquipe + "]";
	}

}
