package org.prometheus.enums;

/**
 * @author josen
 *
 */
public enum SimNaoEnum {

	SIM("S", "Sim"), // Sim
	NAO("N", "N\u00E3o");

	public String sigla;
	public String nome;

	private SimNaoEnum(String sigla, String nome) {
		this.sigla = sigla;
		this.nome = nome;
	}

	public static String getNome(String arg) {
		if (arg != null && arg.equalsIgnoreCase(SimNaoEnum.SIM.sigla)) {
			return SimNaoEnum.SIM.nome;
		}
		return SimNaoEnum.NAO.nome;
	}

	public static String getSigla(Object arg) {
		if (arg != null && arg.toString().equalsIgnoreCase(SimNaoEnum.SIM.sigla)) {
			return SimNaoEnum.SIM.sigla;
		}
		return SimNaoEnum.NAO.sigla;
	}
}
