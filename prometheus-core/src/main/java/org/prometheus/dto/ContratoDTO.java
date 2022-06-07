package org.prometheus.dto;

import java.io.Serializable;

public class ContratoDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long nuContrato;
	private Long nuFornecedor;
	private java.util.Date dtInicioCompromisso;
	private java.util.Date dtFinalCompromisso;
	private java.util.Date dtInicioFatura;
	private java.util.Date dtFinalFatura;
	private String coLicitacao;
	private String coContrato;
	private java.math.BigDecimal vrGlobalContrato;
	private String coProcesso;
	private String coCompromisso;
	private String deContrato;
	private Long nuObjeto;
	private String noContrato;
	private java.math.BigDecimal pzGarantiaContrato;
	private java.math.BigDecimal pzVigenciaContrato;
	private java.math.BigDecimal qtVolumeEstimado;
	private java.util.Date dtAssinaturaContrato;
	private java.util.Date dhCadastro;
	private Long nuMtrcaCadastro;
	private java.math.BigDecimal pcPrazoValidacaoEntrega;
	private java.math.BigDecimal pzMininoValidacaoEntrega;
	private Long nuFaturamento;
	private Long nuCategoria;
	private Long nuCalculoVdpContrato;
	private Long nuCalculoVdpEntrega;
	private Long nuCalculoVdpOs;
	private Long nuAcaoHistorico;
	private Long nuMtrcaAcaoHstro;
	private String icTipoContratacao;
	private java.math.BigDecimal qtProfissionalContrato;
	private String icDistribuiProfissional;
	private String coSispc;

	public void setNuContrato(Long nuContrato) {
		this.nuContrato = nuContrato;
	}

	public Long getNuContrato() {
		return this.nuContrato;
	}

	public void setNuFornecedor(Long nuFornecedor) {
		this.nuFornecedor = nuFornecedor;
	}

	public Long getNuFornecedor() {
		return this.nuFornecedor;
	}

	public void setDtInicioCompromisso(java.util.Date dtInicioCompromisso) {
		this.dtInicioCompromisso = dtInicioCompromisso;
	}

	public java.util.Date getDtInicioCompromisso() {
		return this.dtInicioCompromisso;
	}

	public void setDtFinalCompromisso(java.util.Date dtFinalCompromisso) {
		this.dtFinalCompromisso = dtFinalCompromisso;
	}

	public java.util.Date getDtFinalCompromisso() {
		return this.dtFinalCompromisso;
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

	public void setCoLicitacao(String coLicitacao) {
		this.coLicitacao = coLicitacao;
	}

	public String getCoLicitacao() {
		return this.coLicitacao;
	}

	public void setCoContrato(String coContrato) {
		this.coContrato = coContrato;
	}

	public String getCoContrato() {
		return this.coContrato;
	}

	public void setVrGlobalContrato(java.math.BigDecimal vrGlobalContrato) {
		this.vrGlobalContrato = vrGlobalContrato;
	}

	public java.math.BigDecimal getVrGlobalContrato() {
		return this.vrGlobalContrato;
	}

	public void setCoProcesso(String coProcesso) {
		this.coProcesso = coProcesso;
	}

	public String getCoProcesso() {
		return this.coProcesso;
	}

	public void setCoCompromisso(String coCompromisso) {
		this.coCompromisso = coCompromisso;
	}

	public String getCoCompromisso() {
		return this.coCompromisso;
	}

	public void setDeContrato(String deContrato) {
		this.deContrato = deContrato;
	}

	public String getDeContrato() {
		return this.deContrato;
	}

	public void setNuObjeto(Long nuObjeto) {
		this.nuObjeto = nuObjeto;
	}

	public Long getNuObjeto() {
		return this.nuObjeto;
	}

	public void setNoContrato(String noContrato) {
		this.noContrato = noContrato;
	}

	public String getNoContrato() {
		return this.noContrato;
	}

	public void setPzGarantiaContrato(java.math.BigDecimal pzGarantiaContrato) {
		this.pzGarantiaContrato = pzGarantiaContrato;
	}

	public java.math.BigDecimal getPzGarantiaContrato() {
		return this.pzGarantiaContrato;
	}

	public void setPzVigenciaContrato(java.math.BigDecimal pzVigenciaContrato) {
		this.pzVigenciaContrato = pzVigenciaContrato;
	}

	public java.math.BigDecimal getPzVigenciaContrato() {
		return this.pzVigenciaContrato;
	}

	public void setQtVolumeEstimado(java.math.BigDecimal qtVolumeEstimado) {
		this.qtVolumeEstimado = qtVolumeEstimado;
	}

	public java.math.BigDecimal getQtVolumeEstimado() {
		return this.qtVolumeEstimado;
	}

	public void setDtAssinaturaContrato(java.util.Date dtAssinaturaContrato) {
		this.dtAssinaturaContrato = dtAssinaturaContrato;
	}

	public java.util.Date getDtAssinaturaContrato() {
		return this.dtAssinaturaContrato;
	}

	public void setDhCadastro(java.util.Date dhCadastro) {
		this.dhCadastro = dhCadastro;
	}

	public java.util.Date getDhCadastro() {
		return this.dhCadastro;
	}

	public void setNuMtrcaCadastro(Long nuMtrcaCadastro) {
		this.nuMtrcaCadastro = nuMtrcaCadastro;
	}

	public Long getNuMtrcaCadastro() {
		return this.nuMtrcaCadastro;
	}

	public void setPcPrazoValidacaoEntrega(java.math.BigDecimal pcPrazoValidacaoEntrega) {
		this.pcPrazoValidacaoEntrega = pcPrazoValidacaoEntrega;
	}

	public java.math.BigDecimal getPcPrazoValidacaoEntrega() {
		return this.pcPrazoValidacaoEntrega;
	}

	public void setPzMininoValidacaoEntrega(java.math.BigDecimal pzMininoValidacaoEntrega) {
		this.pzMininoValidacaoEntrega = pzMininoValidacaoEntrega;
	}

	public java.math.BigDecimal getPzMininoValidacaoEntrega() {
		return this.pzMininoValidacaoEntrega;
	}

	public void setNuFaturamento(Long nuFaturamento) {
		this.nuFaturamento = nuFaturamento;
	}

	public Long getNuFaturamento() {
		return this.nuFaturamento;
	}

	public void setNuCategoria(Long nuCategoria) {
		this.nuCategoria = nuCategoria;
	}

	public Long getNuCategoria() {
		return this.nuCategoria;
	}

	public void setNuCalculoVdpContrato(Long nuCalculoVdpContrato) {
		this.nuCalculoVdpContrato = nuCalculoVdpContrato;
	}

	public Long getNuCalculoVdpContrato() {
		return this.nuCalculoVdpContrato;
	}

	public void setNuCalculoVdpEntrega(Long nuCalculoVdpEntrega) {
		this.nuCalculoVdpEntrega = nuCalculoVdpEntrega;
	}

	public Long getNuCalculoVdpEntrega() {
		return this.nuCalculoVdpEntrega;
	}

	public void setNuCalculoVdpOs(Long nuCalculoVdpOs) {
		this.nuCalculoVdpOs = nuCalculoVdpOs;
	}

	public Long getNuCalculoVdpOs() {
		return this.nuCalculoVdpOs;
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

	public void setIcTipoContratacao(String icTipoContratacao) {
		this.icTipoContratacao = icTipoContratacao;
	}

	public String getIcTipoContratacao() {
		return this.icTipoContratacao;
	}

	public void setQtProfissionalContrato(java.math.BigDecimal qtProfissionalContrato) {
		this.qtProfissionalContrato = qtProfissionalContrato;
	}

	public java.math.BigDecimal getQtProfissionalContrato() {
		return this.qtProfissionalContrato;
	}

	public void setIcDistribuiProfissional(String icDistribuiProfissional) {
		this.icDistribuiProfissional = icDistribuiProfissional;
	}

	public String getIcDistribuiProfissional() {
		return this.icDistribuiProfissional;
	}

	public void setCoSispc(String coSispc) {
		this.coSispc = coSispc;
	}

	public String getCoSispc() {
		return this.coSispc;
	}

}
