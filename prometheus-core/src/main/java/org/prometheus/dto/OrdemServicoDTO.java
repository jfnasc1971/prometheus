package org.prometheus.dto;

public class OrdemServicoDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;

	private Long nuOrdemServico;
	private String coOrdemServico;
	private Long nuTipoGrupo;
	private String noTipoGrupo;
	private Long nuTipoServico;
	private String noTipoServico;
	private Long nuTipoForma;
	private String deTipoForma;
	private java.math.BigDecimal vrEstmoOrdemServico;

	public void setCoOrdemServico(String coOrdemServico) {
		this.coOrdemServico = coOrdemServico;
	}

	public String getCoOrdemServico() {
		return this.coOrdemServico;
	}

	public void setNuTipoGrupo(Long nuTipoGrupo) {
		this.nuTipoGrupo = nuTipoGrupo;
	}

	public Long getNuTipoGrupo() {
		return this.nuTipoGrupo;
	}

	public void setNoTipoGrupo(String noTipoGrupo) {
		this.noTipoGrupo = noTipoGrupo;
	}

	public String getNoTipoGrupo() {
		return this.noTipoGrupo;
	}

	public void setNuTipoServico(Long nuTipoServico) {
		this.nuTipoServico = nuTipoServico;
	}

	public Long getNuTipoServico() {
		return this.nuTipoServico;
	}

	public void setNoTipoServico(String noTipoServico) {
		this.noTipoServico = noTipoServico;
	}

	public String getNoTipoServico() {
		return this.noTipoServico;
	}

	public void setNuTipoForma(Long nuTipoForma) {
		this.nuTipoForma = nuTipoForma;
	}

	public Long getNuTipoForma() {
		return this.nuTipoForma;
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

	/**
	 * @return the nuOrdemServico
	 */
	public Long getNuOrdemServico() {
		return nuOrdemServico;
	}

	/**
	 * @param nuOrdemServico the nuOrdemServico to set
	 */
	public void setNuOrdemServico(Long nuOrdemServico) {
		this.nuOrdemServico = nuOrdemServico;
	}

}
