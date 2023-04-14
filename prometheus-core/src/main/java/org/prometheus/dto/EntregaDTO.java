package org.prometheus.dto;

public class EntregaDTO {

	private String coSolicitacao;
	private String noSolicitacao;
	private String deSolicitacao;
	private java.util.Date dhPrevista;
	private java.util.Date dhPrevistaPendencias;
	private java.util.Date dhEfetiva;
	private String icServicoCompoeCalculoFns;
	private String coFalhaSolicitacao;
	private String deSituacaoSolicitacao;
	private Long nuSituacaoSolicitacao;
	private String icEntregaForaPeriodo;

	public void setCoSolicitacao(String coSolicitacao) {
		this.coSolicitacao = coSolicitacao;
	}

	public String getCoSolicitacao() {
		return this.coSolicitacao;
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

	/**
	 * @return the deSituacaoSolicitacao
	 */
	public String getDeSituacaoSolicitacao() {
		return deSituacaoSolicitacao;
	}

	/**
	 * @param deSituacaoSolicitacao the deSituacaoSolicitacao to set
	 */
	public void setDeSituacaoSolicitacao(String deSituacaoSolicitacao) {
		this.deSituacaoSolicitacao = deSituacaoSolicitacao;
	}

	/**
	 * @return the nuSituacaoSolicitacao
	 */
	public Long getNuSituacaoSolicitacao() {
		return nuSituacaoSolicitacao;
	}

	/**
	 * @param nuSituacaoSolicitacao the nuSituacaoSolicitacao to set
	 */
	public void setNuSituacaoSolicitacao(Long nuSituacaoSolicitacao) {
		this.nuSituacaoSolicitacao = nuSituacaoSolicitacao;
	}

	/**
	 * @return the noSolicitacao
	 */
	public String getNoSolicitacao() {
		return noSolicitacao;
	}

	/**
	 * @param noSolicitacao the noSolicitacao to set
	 */
	public void setNoSolicitacao(String noSolicitacao) {
		this.noSolicitacao = noSolicitacao;
	}

	/**
	 * @return the icEntregaForaPeriodo
	 */
	public String getIcEntregaForaPeriodo() {
		return icEntregaForaPeriodo;
	}

	/**
	 * @param icEntregaForaPeriodo the icEntregaForaPeriodo to set
	 */
	public void setIcEntregaForaPeriodo(String icEntregaForaPeriodo) {
		this.icEntregaForaPeriodo = icEntregaForaPeriodo;
	}

	/**
	 * @return the deSolicitacao
	 */
	public String getDeSolicitacao() {
		return deSolicitacao;
	}

	/**
	 * @param deSolicitacao the deSolicitacao to set
	 */
	public void setDeSolicitacao(String deSolicitacao) {
		this.deSolicitacao = deSolicitacao;
	}

	
}
