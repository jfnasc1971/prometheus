package org.prometheus.enums;

public enum SimNao {
    SIM("S", "Sim"),//
    NAO("N", "Não");

    public String SIGLA;
    public String DESCRICAO;

    private SimNao(String sigla, String descricao) {
        this.SIGLA = sigla;
        this.DESCRICAO = descricao;
    }

}
