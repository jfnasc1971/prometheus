package org.prometheus.dto;

public class DemandaDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;

	private Long nuSistema;
	private String sgSistema;
	private String noSistema;
	private Long nuDemanda;
	private String coDemanda;
	private Long nuAreaContratante;
	private String noSiglaContratante;
	private String noAreaContratante;

	public void setNuSistema(Long nuSistema) {
		this.nuSistema = nuSistema;
	}

	public Long getNuSistema() {
		return this.nuSistema;
	}

	public void setSgSistema(String sgSistema) {
		this.sgSistema = sgSistema;
	}

	public String getSgSistema() {
		return this.sgSistema;
	}

	public void setNoSistema(String noSistema) {
		this.noSistema = noSistema;
	}

	public String getNoSistema() {
		return this.noSistema;
	}

	public void setNuDemanda(Long nuDemanda) {
		this.nuDemanda = nuDemanda;
	}

	public Long getNuDemanda() {
		return this.nuDemanda;
	}

	public void setCoDemanda(String coDemanda) {
		this.coDemanda = coDemanda;
	}

	public String getCoDemanda() {
		return this.coDemanda;
	}

	public void setNuAreaContratante(Long nuAreaContratante) {
		this.nuAreaContratante = nuAreaContratante;
	}

	public Long getNuAreaContratante() {
		return this.nuAreaContratante;
	}

	public void setNoSiglaContratante(String noSiglaContratante) {
		this.noSiglaContratante = noSiglaContratante;
	}

	public String getNoSiglaContratante() {
		return this.noSiglaContratante;
	}

	public void setNoAreaContratante(String noAreaContratante) {
		this.noAreaContratante = noAreaContratante;
	}

	public String getNoAreaContratante() {
		return this.noAreaContratante;
	}

}
