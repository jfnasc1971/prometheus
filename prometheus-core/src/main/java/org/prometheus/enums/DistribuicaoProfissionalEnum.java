package org.prometheus.enums;

public enum DistribuicaoProfissionalEnum {
    GLOBAL("G", "Distribuição Global"),//
    PERFIL("D", "Distribuição por Perfil");

    public String SIGLA;
    public String DESCRICAO;

    private DistribuicaoProfissionalEnum(String sigla, String descricao) {
        this.SIGLA = sigla;
        this.DESCRICAO = descricao;
    }

}
