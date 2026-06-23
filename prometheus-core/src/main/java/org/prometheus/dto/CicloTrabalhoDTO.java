package org.prometheus.dto;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;

public class CicloTrabalhoDTO {

	private Long nuFatura;
	private java.math.BigDecimal qtDiasSprint;
	private java.math.BigDecimal qtDiasTrab;
	private java.math.BigDecimal qtEfetiva;
	private Long nuEquipe;
	private Long nuCicloTrabalho;
	private Long nuSituacaoCicloTrabalho;
	private String deCicloTrabalho;
	private java.util.Date dtInicioCicloTrabalho;
	private java.util.Date dtAlvoCicloTrabalho;
	private java.util.Date dtFinalCicloTrabalho;
	private String coFalhaCicloTrabalho;
	private Long nuAcaoHistorico;
	private Long nuMtrcaAcaoHstro;
	private java.math.BigDecimal vrQuestionarioAvaliacao;
	private java.math.BigDecimal vrNotaQualidadeProduto;
	private java.math.BigDecimal vrTotalApuradoAvaliacao;
	private Long nuMtrcaAvaliacaoEquipe;
	private String deJustificativaFalha;
	private String deJustificativaAlteracao;
	private java.math.BigDecimal qtDiasAusencia;
	private java.math.BigDecimal qtDiasExtra;
	private Map<String, XSSFCell> indicadores;

	public void setNuEquipe(Long nuEquipe) {
		this.nuEquipe = nuEquipe;
	}

	public Long getNuEquipe() {
		return this.nuEquipe;
	}

	public void setNuCicloTrabalho(Long nuCicloTrabalho) {
		this.nuCicloTrabalho = nuCicloTrabalho;
	}

	public Long getNuCicloTrabalho() {
		return this.nuCicloTrabalho;
	}

	public void setNuSituacaoCicloTrabalho(Long nuSituacaoCicloTrabalho) {
		this.nuSituacaoCicloTrabalho = nuSituacaoCicloTrabalho;
	}

	public Long getNuSituacaoCicloTrabalho() {
		return this.nuSituacaoCicloTrabalho;
	}

	public void setDeCicloTrabalho(String deCicloTrabalho) {
		this.deCicloTrabalho = deCicloTrabalho;
	}

	public String getDeCicloTrabalho() {
		return this.deCicloTrabalho;
	}

	public void setDtInicioCicloTrabalho(java.util.Date dtInicioCicloTrabalho) {
		this.dtInicioCicloTrabalho = dtInicioCicloTrabalho;
	}

	public java.util.Date getDtInicioCicloTrabalho() {
		return this.dtInicioCicloTrabalho;
	}

	public void setDtAlvoCicloTrabalho(java.util.Date dtAlvoCicloTrabalho) {
		this.dtAlvoCicloTrabalho = dtAlvoCicloTrabalho;
	}

	public java.util.Date getDtAlvoCicloTrabalho() {
		return this.dtAlvoCicloTrabalho;
	}

	public void setDtFinalCicloTrabalho(java.util.Date dtFinalCicloTrabalho) {
		this.dtFinalCicloTrabalho = dtFinalCicloTrabalho;
	}

	public java.util.Date getDtFinalCicloTrabalho() {
		return this.dtFinalCicloTrabalho;
	}

	public void setCoFalhaCicloTrabalho(String coFalhaCicloTrabalho) {
		this.coFalhaCicloTrabalho = coFalhaCicloTrabalho;
	}

	public String getCoFalhaCicloTrabalho() {
		return this.coFalhaCicloTrabalho;
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

	public void setVrQuestionarioAvaliacao(java.math.BigDecimal vrQuestionarioAvaliacao) {
		this.vrQuestionarioAvaliacao = vrQuestionarioAvaliacao;
	}

	public java.math.BigDecimal getVrQuestionarioAvaliacao() {
		return this.vrQuestionarioAvaliacao;
	}

	public void setVrNotaQualidadeProduto(java.math.BigDecimal vrNotaQualidadeProduto) {
		this.vrNotaQualidadeProduto = vrNotaQualidadeProduto;
	}

	public java.math.BigDecimal getVrNotaQualidadeProduto() {
		return this.vrNotaQualidadeProduto;
	}

	public void setVrTotalApuradoAvaliacao(java.math.BigDecimal vrTotalApuradoAvaliacao) {
		this.vrTotalApuradoAvaliacao = vrTotalApuradoAvaliacao;
	}

	public java.math.BigDecimal getVrTotalApuradoAvaliacao() {
		return this.vrTotalApuradoAvaliacao;
	}

	public void setNuMtrcaAvaliacaoEquipe(Long nuMtrcaAvaliacaoEquipe) {
		this.nuMtrcaAvaliacaoEquipe = nuMtrcaAvaliacaoEquipe;
	}

	public Long getNuMtrcaAvaliacaoEquipe() {
		return this.nuMtrcaAvaliacaoEquipe;
	}

	public void setDeJustificativaFalha(String deJustificativaFalha) {
		this.deJustificativaFalha = deJustificativaFalha;
	}

	public String getDeJustificativaFalha() {
		return this.deJustificativaFalha;
	}

	public void setDeJustificativaAlteracao(String deJustificativaAlteracao) {
		this.deJustificativaAlteracao = deJustificativaAlteracao;
	}

	public String getDeJustificativaAlteracao() {
		return this.deJustificativaAlteracao;
	}

	public Long getNuFatura() {
		return nuFatura;
	}

	public void setNuFatura(Long nuFatura) {
		this.nuFatura = nuFatura;
	}

	public java.math.BigDecimal getQtDiasSprint() {
		return qtDiasSprint;
	}

	public void setQtDiasSprint(java.math.BigDecimal qtDiasSprint) {
		this.qtDiasSprint = qtDiasSprint;
	}

	public java.math.BigDecimal getQtDiasTrab() {
		return qtDiasTrab;
	}

	public void setQtDiasTrab(java.math.BigDecimal qtDiasTrab) {
		this.qtDiasTrab = qtDiasTrab;
	}

	public java.math.BigDecimal getQtEfetiva() {
		return qtEfetiva;
	}

	public void setQtEfetiva(java.math.BigDecimal qtEfetiva) {
		this.qtEfetiva = qtEfetiva;
	}

	public java.math.BigDecimal getQtDiasAusencia() {
		return qtDiasAusencia;
	}

	public void setQtDiasAusencia(java.math.BigDecimal qtDiasAusencia) {
		this.qtDiasAusencia = qtDiasAusencia;
	}

	public java.math.BigDecimal getQtDiasExtra() {
		return qtDiasExtra;
	}

	public void setQtDiasExtra(java.math.BigDecimal qtDiasExtra) {
		this.qtDiasExtra = qtDiasExtra;
	}

	public String getNoCicloTrabalho() {
		String tmp = "0000" + nuCicloTrabalho;
		return "SPRINT: " + tmp.substring(tmp.length() - 4, tmp.length());
	}

	/**
	 * @return the indicadores
	 */
	public Map<String, XSSFCell> getIndicadores() {
		if (indicadores == null) {
			indicadores = new HashMap<String, XSSFCell>();
		}
		return indicadores;
	}

	/**
	 * @param indicadores the indicadores to set
	 */
	public void setIndicadores(Map<String, XSSFCell> indicadores) {
		this.indicadores = indicadores;
	}
	
	/**
	 * 
	 * @param sgIndicador
	 * @param vrIndicador
	 */
	public void addIndicador(String sgIndicador, XSSFCell vrIndicador) {
		getIndicadores().put(sgIndicador, vrIndicador);
	}
	
	/**
	 * 
	 * @param sgIndicador
	 * @return
	 */
	public XSSFCell getVrIndicador(String sgIndicador) {
		return getIndicadores().get(sgIndicador);
	}
}
