package org.prometheus.dto;

public class QtdMembrosDTO {

	private String noPerfil;
	private Long nuContrato;
	private Long nuEquipe;
	private Long nuFatura;
	private java.math.BigDecimal qtMembrosEfetiva;

	public void setNoPerfil(String noPerfil) {
		this.noPerfil = noPerfil;
	}

	public String getNoPerfil() {
		return this.noPerfil;
	}

	public void setNuContrato(Long nuContrato) {
		this.nuContrato = nuContrato;
	}

	public Long getNuContrato() {
		return this.nuContrato;
	}

	public void setNuEquipe(Long nuEquipe) {
		this.nuEquipe = nuEquipe;
	}

	public Long getNuEquipe() {
		return this.nuEquipe;
	}

	public void setNuFatura(Long nuFatura) {
		this.nuFatura = nuFatura;
	}

	public Long getNuFatura() {
		return this.nuFatura;
	}

	public void setQtMembrosEfetiva(java.math.BigDecimal qtMembrosEfetiva) {
		this.qtMembrosEfetiva = qtMembrosEfetiva;
	}

	public java.math.BigDecimal getQtMembrosEfetiva() {
		return this.qtMembrosEfetiva;
	}

}
