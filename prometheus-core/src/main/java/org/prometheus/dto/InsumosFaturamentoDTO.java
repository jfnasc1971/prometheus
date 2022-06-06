package org.prometheus.dto;

import java.math.BigDecimal;

import org.apache.poi.xssf.usermodel.XSSFCell;

public class InsumosFaturamentoDTO {

	private Long nuFatura; // Faturamento
	private XSSFCell qtDiasFaturamento; // Qtd. Dias Fat.
	private BigDecimal vrPreco; // Preço
	private XSSFCell qtEfetiva; // Qtd. Efetiva
	private XSSFCell qtDiasExtra; // Qtd. Dias Extras
	private XSSFCell qtDiasAusencia; // Qtd. Dias Ausências
	private XSSFCell vrPrevistoTermo; // Vr. Previsto Termo
	private XSSFCell vrFns; // FNS
	private boolean considerarFNS;

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

	
}
