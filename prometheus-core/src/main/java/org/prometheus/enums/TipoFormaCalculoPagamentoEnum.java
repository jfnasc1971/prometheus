package org.prometheus.enums;

public enum TipoFormaCalculoPagamentoEnum {

    DISTRIBUICAO_GLOBAL(1L,"Distribuição Global"), //
    DISTRIBUICAO_POR_PERFIL(2L,"Distribuição por Perfil"), //
    MILHEIROS(3L,"Milheiros"),
    ESPECIALIZADOS(4L,"Contratos Especializados");

    public Long TIPO;

    private TipoFormaCalculoPagamentoEnum(Long TIPO, String DESCRICAO) {
        this.TIPO = TIPO;
    }

}

