select
  gi.nu_contrato,
  gi.nu_tipo_grupo,
  gi.nu_situacao_atendimento,
  sa.no_situacao_atendimento,
  ti.nu_tipo_indicador,
  ti.sg_tipo_indicador,
  ti.no_tipo_indicador
from
  gct.gcttb174_grupo_indicador_situacao gi
  inner join gct.gcttb087_equipe e on e.nu_contrato = gi.nu_contrato
  inner join gct.gcttb112_tipo_indicador ti on ti.nu_tipo_indicador = gi.nu_tipo_indicador
  inner join gct.gcttb092_situacao_atendimento sa on sa.nu_situacao_atendimento = gi.nu_situacao_atendimento
where
  1=1
  and e.nu_equipe = 144
  and gi.nu_contrato = e.nu_contrato 
  and gi.nu_tipo_grupo = e.nu_tipo_grupo 
  and upper(gi.no_situacao_atendimento) = upper('SERVICOS_CONTRATADOS')
order by
  gi.nu_contrato,
  gi.nu_tipo_grupo,
  gi.nu_tipo_indicador,
  gi.nu_situacao_atendimento;