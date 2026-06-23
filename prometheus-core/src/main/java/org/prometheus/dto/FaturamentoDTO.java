package org.prometheus.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;

public class FaturamentoDTO {

	private Long nuFaturamento;
	private String noFaturamento;
	private Long nuFatura;
	private String deFatura;
	private java.util.Date dtInicioFatura;
	private java.util.Date dtFinalFatura;
	private java.util.Date dtConsiderarFns;
	private String icConsiderarFns;
	private java.math.BigDecimal qtDiasFaturamento;
	private java.math.BigDecimal qtDiasServicoExtra;
	private java.math.BigDecimal qtDiasAusencia;
	private Map<String, XSSFCell> indicadores;
	private List<CicloTrabalhoDTO> ciclosTrabalho;

	public void setNuFaturamento(Long nuFaturamento) {
		this.nuFaturamento = nuFaturamento;
	}

	public Long getNuFaturamento() {
		return this.nuFaturamento;
	}

	public void setNoFaturamento(String noFaturamento) {
		this.noFaturamento = noFaturamento;
	}

	public String getNoFaturamento() {
		return this.noFaturamento;
	}

	public void setNuFatura(Long nuFatura) {
		this.nuFatura = nuFatura;
	}

	public Long getNuFatura() {
		return this.nuFatura;
	}

	public void setDeFatura(String deFatura) {
		this.deFatura = deFatura;
	}

	public String getDeFatura() {
		return this.deFatura;
	}

	public void setDtInicioFatura(java.util.Date dtInicioFatura) {
		this.dtInicioFatura = dtInicioFatura;
	}

	public java.util.Date getDtInicioFatura() {
		return this.dtInicioFatura;
	}

	public void setDtFinalFatura(java.util.Date dtFinalFatura) {
		this.dtFinalFatura = dtFinalFatura;
	}

	public java.util.Date getDtFinalFatura() {
		return this.dtFinalFatura;
	}

	public void setDtConsiderarFns(java.util.Date dtConsiderarFns) {
		this.dtConsiderarFns = dtConsiderarFns;
	}

	public java.util.Date getDtConsiderarFns() {
		return this.dtConsiderarFns;
	}

	public void setIcConsiderarFns(String icConsiderarFns) {
		this.icConsiderarFns = icConsiderarFns;
	}

	public String getIcConsiderarFns() {
		return this.icConsiderarFns;
	}

	public void setQtDiasFaturamento(java.math.BigDecimal qtDiasFaturamento) {
		this.qtDiasFaturamento = qtDiasFaturamento;
	}

	public java.math.BigDecimal getQtDiasFaturamento() {
		return this.qtDiasFaturamento;
	}

	public void setQtDiasServicoExtra(java.math.BigDecimal qtDiasServicoExtra) {
		this.qtDiasServicoExtra = qtDiasServicoExtra;
	}

	public java.math.BigDecimal getQtDiasServicoExtra() {
		return this.qtDiasServicoExtra;
	}

	public void setQtDiasAusencia(java.math.BigDecimal qtDiasAusencia) {
		this.qtDiasAusencia = qtDiasAusencia;
	}

	public java.math.BigDecimal getQtDiasAusencia() {
		return this.qtDiasAusencia;
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
	
	public XSSFCell getVrIndicador(String sgIndicador) {
		return getIndicadores().get(sgIndicador);
	}
	
	/**
	 * @return the ciclosTrabalho
	 */
	public List<CicloTrabalhoDTO> getCiclosTrabalho() {
		if (ciclosTrabalho == null) {
			ciclosTrabalho = new ArrayList<CicloTrabalhoDTO>();
		}
		return ciclosTrabalho;
	}

	/**
	 * @param ciclosTrabalho the ciclosTrabalho to set
	 */
	public void setCiclosTrabalho(List<CicloTrabalhoDTO> ciclosTrabalho) {
		this.ciclosTrabalho = ciclosTrabalho;
	}
}
