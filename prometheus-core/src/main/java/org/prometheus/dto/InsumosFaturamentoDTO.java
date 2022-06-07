package org.prometheus.dto;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.xssf.usermodel.XSSFCell;

public class InsumosFaturamentoDTO {

	private Long nuFatura; // Faturamento
	private XSSFCell qtDiasFaturamento; // Qtd. Dias Fat.
	private BigDecimal vrPreco; // Preço
	private Map<Long, BigDecimal> precosPerfil; // Lista de preços por perfil
	private Map<Long, BigDecimal> qtEfetivaPerfil; // Quantidade efetiva por perfil
	private XSSFCell qtEfetiva; // Qtd. Efetiva
	private XSSFCell qtDiasExtra; // Qtd. Dias Extras
	private XSSFCell qtDiasAusencia; // Qtd. Dias Ausências
	private XSSFCell vrPrevistoTermo; // Vr. Previsto Termo
	private XSSFCell vrFns; // FNS
	private boolean considerarFNS;
	private Long nuPerfil;

	/**
	 * @return the nuFatura
	 */
	public Long getNuFatura() {
		return nuFatura;
	}

	/**
	 * @param nuFatura the nuFatura to set
	 */
	public void setNuFatura(Long nuFatura) {
		this.nuFatura = nuFatura;
	}

	/**
	 * @return the qtDiasFaturamento
	 */
	public XSSFCell getQtDiasFaturamento() {
		return qtDiasFaturamento;
	}

	/**
	 * @param qtDiasFaturamento the qtDiasFaturamento to set
	 */
	public void setQtDiasFaturamento(XSSFCell qtDiasFaturamento) {
		this.qtDiasFaturamento = qtDiasFaturamento;
	}

	/**
	 * @return the vrPreco
	 */
	public BigDecimal getVrPreco() {
		return vrPreco;
	}

	/**
	 * @param vrPreco the vrPreco to set
	 */
	public void setVrPreco(BigDecimal vrPreco) {
		this.vrPreco = vrPreco;
	}

	/**
	 * @return the qtEfetiva
	 */
	public XSSFCell getQtEfetiva() {
		return qtEfetiva;
	}

	/**
	 * @param qtEfetiva the qtEfetiva to set
	 */
	public void setQtEfetiva(XSSFCell qtEfetiva) {
		this.qtEfetiva = qtEfetiva;
	}

	/**
	 * @return the qtDiasExtra
	 */
	public XSSFCell getQtDiasExtra() {
		return qtDiasExtra;
	}

	/**
	 * @param qtDiasExtra the qtDiasExtra to set
	 */
	public void setQtDiasExtra(XSSFCell qtDiasExtra) {
		this.qtDiasExtra = qtDiasExtra;
	}

	/**
	 * @return the qtDiasAusencia
	 */
	public XSSFCell getQtDiasAusencia() {
		return qtDiasAusencia;
	}

	/**
	 * @param qtDiasAusencia the qtDiasAusencia to set
	 */
	public void setQtDiasAusencia(XSSFCell qtDiasAusencia) {
		this.qtDiasAusencia = qtDiasAusencia;
	}

	/**
	 * @return the vrPrevistoTermo
	 */
	public XSSFCell getVrPrevistoTermo() {
		return vrPrevistoTermo;
	}

	/**
	 * @param vrPrevistoTermo the vrPrevistoTermo to set
	 */
	public void setVrPrevistoTermo(XSSFCell vrPrevistoTermo) {
		this.vrPrevistoTermo = vrPrevistoTermo;
	}

	/**
	 * @return the vrFns
	 */
	public XSSFCell getVrFns() {
		return vrFns;
	}

	/**
	 * @param vrFns the vrFns to set
	 */
	public void setVrFns(XSSFCell vrFns) {
		this.vrFns = vrFns;
	}

	/**
	 * @return the considerarFNS
	 */
	public boolean isConsiderarFNS() {
		return considerarFNS;
	}

	/**
	 * @param considerarFNS the considerarFNS to set
	 */
	public void setConsiderarFNS(boolean considerarFNS) {
		this.considerarFNS = considerarFNS;
	}

	/**
	 * @return the nuPerfil
	 */
	public Long getNuPerfil() {
		return nuPerfil;
	}

	/**
	 * @param nuPerfil the nuPerfil to set
	 */
	public void setNuPerfil(Long nuPerfil) {
		this.nuPerfil = nuPerfil;
	}

	/**
	 * @return the precosPerfil
	 */
	public Map<Long, BigDecimal> getPrecosPerfil() {
		if (precosPerfil == null) {
			precosPerfil = new HashedMap<Long, BigDecimal>();
		}
		return precosPerfil;
	}

	/**
	 * @param precosPerfil the precosPerfil to set
	 */
	public void setPrecosPerfil(Map<Long, BigDecimal> precosPerfil) {
		this.precosPerfil = precosPerfil;
	}

	public void addVrPrecoPerfil(Long nuPerfil, BigDecimal vrPreco) {
		getPrecosPerfil().put(nuPerfil, vrPreco);
	}

	public BigDecimal getVrPrecoPerfil(Long nuPerfil) {

		BigDecimal result = getPrecosPerfil().get(nuPerfil);

		if (result == null) {
			return BigDecimal.ZERO;
		}

		return result;
	}

	/**
	 * @return the qtEfetivaPerfil
	 */
	public Map<Long, BigDecimal> getQtEfetivaPerfil() {
		if(qtEfetivaPerfil == null) {
			qtEfetivaPerfil = new HashedMap<Long, BigDecimal>();
		}
		return qtEfetivaPerfil;
	}

	/**
	 * @param qtEfetivaPerfil the qtEfetivaPerfil to set
	 */
	public void setQtEfetivaPerfil(Map<Long, BigDecimal> qtEfetivaPerfil) {
		this.qtEfetivaPerfil = qtEfetivaPerfil;
	}
	
	public void addQtEfetivaPerfil(Long nuPerfil, BigDecimal cellQtEfetiva) {
		if (getQtEfetivaPerfil().get(nuPerfil) != null) {
			cellQtEfetiva = cellQtEfetiva.add(getQtEfetivaPerfil().get(nuPerfil));
		}
		getQtEfetivaPerfil().put(nuPerfil, cellQtEfetiva);
	}

	public BigDecimal getQtEfetivaPerfil(Long nuPerfil) {
		
		BigDecimal result = getQtEfetivaPerfil().get(nuPerfil); 
		
		if(result == null) {
			return BigDecimal.ZERO;
		}
		
		return result;
	}
	
}
