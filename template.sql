  select
    c.no_contrato,
    c.nu_contrato,
    d.co_demanda,
    tg.no_tipo_grupo,
    m.no_modulo,
    s.sg_sistema,
    s.no_sistema,
    tsd.de_situacao_demanda,
    d.nu_demanda,
    m.nu_modulo,
    s.nu_sistema,
    fc.ic_ordem_servico_acionamento, 
    fc.ic_quantidade, 
    fc.ic_valor_os_informado, 
    fc.ic_calculo_quantidade_os, 
    fc.ic_quantidade_os 
  from
    gct.gcttb023_ordem_servico os
    inner join gct.gcttb010_contrato c on c.nu_contrato = os.nu_contrato 
    inner join gct.gcttb032_tipo_grupo tg on tg.nu_tipo_grupo = os.nu_tipo_grupo 
    inner join gct.gcttb021_demanda d on d.nu_demanda=os.nu_demanda
    inner join gct.gcttb039_sistema s on s.nu_sistema = d.nu_sistema
    inner join gct.gcttb040_modulo m on m.nu_modulo = d.nu_modulo
    inner join gct.gcttb008_tipo_situacao_demanda tsd on (
      tsd.nu_situacao_demanda = d.nu_situacao_demanda
    )
    inner join gct.gcttb013_forma_contrato fc on (
      fc.nu_contrato = os.nu_contrato 
      and fc.nu_tipo_grupo = os.nu_tipo_grupo 
      and fc.nu_tipo_servico = os.nu_tipo_servico 
      and fc.nu_tipo_forma = os.nu_tipo_forma  
    )
  where
    d.co_demanda = 'SP202400196'
    and os.nu_contrato = 649
    and os.nu_stco_ordem_servico not in (6) 