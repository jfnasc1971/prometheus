package org.prometheus.dto;

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

}
