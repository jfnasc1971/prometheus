package org.prometheus.dto;

public class EntregaDTO {

	private String coSolicitacao;
	private String deSolicitacao;
	private java.util.Date dhPrevista;
	private java.util.Date dhPrevistaPendencias;
	private java.util.Date dhEfetiva;
	private String icServicoCompoeCalculoFns;
	private String coFalhaSolicitacao;

	public void setCoSolicitacao(String coSolicitacao) {
		this.coSolicitacao = coSolicitacao;
	}

	public String getCoSolicitacao() {
		return this.coSolicitacao;
	}

	public void setDeSolicitacao(String deSolicitacao) {
		this.deSolicitacao = deSolicitacao;
	}

	public String getDeSolicitacao() {
		return this.deSolicitacao;
	}

	public void setDhPrevista(java.util.Date dhPrevista) {
		this.dhPrevista = dhPrevista;
	}

	public java.util.Date getDhPrevista() {
		return this.dhPrevista;
	}

	public void setDhPrevistaPendencias(java.util.Date dhPrevistaPendencias) {
		this.dhPrevistaPendencias = dhPrevistaPendencias;
	}

	public java.util.Date getDhPrevistaPendencias() {
		return this.dhPrevistaPendencias;
	}

	public void setDhEfetiva(java.util.Date dhEfetiva) {
		this.dhEfetiva = dhEfetiva;
	}

	public java.util.Date getDhEfetiva() {
		return this.dhEfetiva;
	}

	public void setIcServicoCompoeCalculoFns(String icServicoCompoeCalculoFns) {
		this.icServicoCompoeCalculoFns = icServicoCompoeCalculoFns;
	}

	public String getIcServicoCompoeCalculoFns() {
		return this.icServicoCompoeCalculoFns;
	}

	public void setCoFalhaSolicitacao(String coFalhaSolicitacao) {
		this.coFalhaSolicitacao = coFalhaSolicitacao;
	}

	public String getCoFalhaSolicitacao() {
		return this.coFalhaSolicitacao;
	}

}
