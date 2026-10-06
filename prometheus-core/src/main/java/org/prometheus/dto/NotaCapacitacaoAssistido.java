package org.prometheus.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class NotaCapacitacaoAssistido implements Serializable{
	
	private static final long serialVersionUID = 1L;

	BigDecimal vrSomaAvaliacoes;
	BigDecimal qtAvaliadores;
	BigDecimal qtPerguntas;

	/**
	 * @return the vrSomaAvaliacoes
	 */
	public BigDecimal getVrSomaAvaliacoes() {
		return vrSomaAvaliacoes;
	}

	/**
	 * @param vrSomaAvaliacoes the vrSomaAvaliacoes to set
	 */
	public void setVrSomaAvaliacoes(BigDecimal vrSomaAvaliacoes) {
		this.vrSomaAvaliacoes = vrSomaAvaliacoes;
	}

	/**
	 * @return the qtAvaliadores
	 */
	public BigDecimal getQtAvaliadores() {
		return qtAvaliadores;
	}

	/**
	 * @param qtAvaliadores the qtAvaliadores to set
	 */
	public void setQtAvaliadores(BigDecimal qtAvaliadores) {
		this.qtAvaliadores = qtAvaliadores;
	}

	/**
	 * @return the qtPerguntas
	 */
	public BigDecimal getQtPerguntas() {
		return qtPerguntas;
	}

	/**
	 * @param qtPerguntas the qtPerguntas to set
	 */
	public void setQtPerguntas(BigDecimal qtPerguntas) {
		this.qtPerguntas = qtPerguntas;
	}

}
