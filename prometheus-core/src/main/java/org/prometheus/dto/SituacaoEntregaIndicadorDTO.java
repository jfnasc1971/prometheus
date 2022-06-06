package org.prometheus.dto;

public class SituacaoEntregaIndicadorDTO {

	private Long nuContrato;
	private Long nuTipoGrupo;
	private Long nuSituacaoAtendimento;
	private String noSituacaoAtendimento;
	private Long nuTipoIndicador;
	private String sgTipoIndicador;
	private String noTipoIndicador;

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

	public void setNuSituacaoAtendimento(Long nuSituacaoAtendimento) {
		this.nuSituacaoAtendimento = nuSituacaoAtendimento;
	}

	public Long getNuSituacaoAtendimento() {
		return this.nuSituacaoAtendimento;
	}

	public void setNoSituacaoAtendimento(String noSituacaoAtendimento) {
		this.noSituacaoAtendimento = noSituacaoAtendimento;
	}

	public String getNoSituacaoAtendimento() {
		return this.noSituacaoAtendimento;
	}

	public void setNuTipoIndicador(Long nuTipoIndicador) {
		this.nuTipoIndicador = nuTipoIndicador;
	}

	public Long getNuTipoIndicador() {
		return this.nuTipoIndicador;
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

}
