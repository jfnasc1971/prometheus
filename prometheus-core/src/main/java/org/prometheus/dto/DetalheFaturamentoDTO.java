package org.prometheus.dto;

public class DetalheFaturamentoDTO {

	private String noPerfil;
	private java.util.Date dtInicioVigenciaMembros;
	private java.util.Date dtFinalVigenciaMembros;
	private java.math.BigDecimal qtDiasTrab;
	private java.math.BigDecimal qtDiasFat;
	private java.math.BigDecimal qtMembros;
	private java.math.BigDecimal qtDiaAusencia;
	private java.math.BigDecimal qtDiaServicoExtra;
	private java.math.BigDecimal vrPrecoGrupo;

	public String getNoPerfil() {
		return noPerfil;
	}

	public void setNoPerfil(String noPerfil) {
		this.noPerfil = noPerfil;
	}

	public void setDtInicioVigenciaMembros(java.util.Date dtInicioVigenciaMembros) {
		this.dtInicioVigenciaMembros = dtInicioVigenciaMembros;
	}

	public java.util.Date getDtInicioVigenciaMembros() {
		return this.dtInicioVigenciaMembros;
	}

	public void setDtFinalVigenciaMembros(java.util.Date dtFinalVigenciaMembros) {
		this.dtFinalVigenciaMembros = dtFinalVigenciaMembros;
	}

	public java.util.Date getDtFinalVigenciaMembros() {
		return this.dtFinalVigenciaMembros;
	}

	public void setQtDiasTrab(java.math.BigDecimal qtDiasTrab) {
		this.qtDiasTrab = qtDiasTrab;
	}

	public java.math.BigDecimal getQtDiasTrab() {
		return this.qtDiasTrab;
	}

	public void setQtDiasFat(java.math.BigDecimal qtDiasFat) {
		this.qtDiasFat = qtDiasFat;
	}

	public java.math.BigDecimal getQtDiasFat() {
		return this.qtDiasFat;
	}

	public void setQtMembros(java.math.BigDecimal qtMembros) {
		this.qtMembros = qtMembros;
	}

	public java.math.BigDecimal getQtMembros() {
		return this.qtMembros;
	}

	public void setQtDiaAusencia(java.math.BigDecimal qtDiaAusencia) {
		this.qtDiaAusencia = qtDiaAusencia;
	}

	public java.math.BigDecimal getQtDiaAusencia() {
		return this.qtDiaAusencia;
	}

	public void setQtDiaServicoExtra(java.math.BigDecimal qtDiaServicoExtra) {
		this.qtDiaServicoExtra = qtDiaServicoExtra;
	}

	public java.math.BigDecimal getQtDiaServicoExtra() {
		return this.qtDiaServicoExtra;
	}

	public void setVrPrecoGrupo(java.math.BigDecimal vrPrecoGrupo) {
		this.vrPrecoGrupo = vrPrecoGrupo;
	}

	public java.math.BigDecimal getVrPrecoGrupo() {
		return this.vrPrecoGrupo;
	}

}
