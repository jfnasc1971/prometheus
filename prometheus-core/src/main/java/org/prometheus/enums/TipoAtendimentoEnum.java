package org.prometheus.enums;

public enum TipoAtendimentoEnum {
    SPRINT("S", "Sprint"),//
    ACIONAMENTO("A", "Acionamento");

    public String SIGLA;
    public String DESCRICAO;

    private TipoAtendimentoEnum(String sigla, String descricao) {
        this.SIGLA = sigla;
        this.DESCRICAO = descricao;
    }

}
