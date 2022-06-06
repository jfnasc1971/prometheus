SELECT
  c.nu_contrato,
  c.no_contrato,
  e.nu_equipe,
  e.co_equipe,
  e.dt_inicio_atendimento,
  e.dt_final_atendimento,
  tg.nu_tipo_grupo,
  tg.no_tipo_grupo,
  f.nu_fatura,
  f.de_fatura,
  e.co_sitio,
  t.co_termo,
  t.dh_emissao_termo,
  t.vr_previsto_equipe,
  t.vr_acrescimo,
  t.vr_decrescimo,
  t.vr_financeiro_termo_original,
  vie.vr_faixa_fns,
  t.vr_financeiro_termo
FROM
  gct.gcttb087_equipe e
  INNER JOIN gct.gcttb010_contrato c ON c.nu_contrato = e.nu_contrato
  INNER JOIN gct.gcttb032_tipo_grupo tg ON tg.nu_tipo_grupo = e.nu_tipo_grupo
  INNER JOIN gct.gcttb170_termo_equipe t ON t.nu_equipe = e.nu_equipe
  INNER JOIN gct.gcttb102_fatura f ON f.nu_fatura = t.nu_fatura_paga
  INNER JOIN gct.gcttb115_grupo_indicador gi ON (
    gi.nu_contrato = e.nu_contrato
    AND gi.nu_tipo_grupo = e.nu_tipo_grupo
    AND gi.ic_aplica_indicador = 'S'
  )
  LEFT JOIN gct.gcttb172_valor_indicador_equipe vie ON (
    vie.nu_contrato = gi.nu_contrato
    AND vie.nu_tipo_grupo = gi.nu_tipo_grupo
    AND vie.nu_tipo_indicador = gi.nu_tipo_indicador
    AND vie.nu_fatura = t.nu_fatura_gerada
    AND vie.nu_equipe = e.nu_equipe
  )
WHERE
  c.no_contrato ilike '%9520%'
  AND t.nu_tipo_termo IN (11)
ORDER BY
  f.dt_final_fatura,
  tg.no_tipo_grupo,
  e.co_equipe