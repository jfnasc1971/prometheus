package org.prometheus.dto;

public class SituacaoEntregaIndicadorDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;

	private String sgTipoIndicador;
	private String noTipoIndicador;
	private String noAgrupador;
	private Long nuSituacaoAtendimento;
	private String noSituacaoAtendimento;
	private Long nuContrato;
	private Long nuTipoGrupo;
	private Long nuEquipe;
	private Long nuTipoIndicador;

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

	public void setNoAgrupador(String noAgrupador) {
		this.noAgrupador = noAgrupador;
	}

	public String getNoAgrupador() {
		return this.noAgrupador;
	}

	public void setNoSituacaoAtendimento(String noSituacaoAtendimento) {
		this.noSituacaoAtendimento = noSituacaoAtendimento;
	}

	public String getNoSituacaoAtendimento() {
		return this.noSituacaoAtendimento;
	}

	public void setNuContrato(Long nuContrato) {
		this.nuContrato = nuContrato;
	}

	public Long getNuContrato() {
		return this.nuContrato;
	}

	public void setNuTipoGrupo(Long nuTipoGrupo) {
		this.nuTipoGrupo = nuTipoGrupo;
	}

	public Long getNuTipoGrupo() {
		return this.nuTipoGrupo;
	}

	public void setNuEquipe(Long nuEquipe) {
		this.nuEquipe = nuEquipe;
	}

	public Long getNuEquipe() {
		return this.nuEquipe;
	}

	public void setNuTipoIndicador(Long nuTipoIndicador) {
		this.nuTipoIndicador = nuTipoIndicador;
	}

	public Long getNuTipoIndicador() {
		return this.nuTipoIndicador;
	}

	/**
	 * @return the nuSituacaoAtendimento
	 */
	public Long getNuSituacaoAtendimento() {
		return nuSituacaoAtendimento;
	}

	/**
	 * @param nuSituacaoAtendimento the nuSituacaoAtendimento to set
	 */
	public void setNuSituacaoAtendimento(Long nuSituacaoAtendimento) {
		this.nuSituacaoAtendimento = nuSituacaoAtendimento;
	}

}
