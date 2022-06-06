SELECT
  tae.nu_equipe,
  tae.nu_ajuste,
  tae.nu_termo_equipe,
  teo.co_termo,
  teo.dh_emissao_termo,
  fteo.de_fatura,
  fteo.dt_final_fatura,
  teo.vr_financeiro_termo,
  tae.vr_ajuste_anterior,
  tag.vr_financeiro_termo as vr_financeiro_gerado
FROM
  gct.gcttb087_equipe e
  INNER JOIN gct.gcttb186_termo_ajuste_equipe tae ON tae.nu_equipe = e.nu_equipe
  INNER JOIN gct.gcttb170_termo_equipe teo ON teo.nu_termo_equipe = tae.nu_termo_equipe_original
  INNER JOIN gct.gcttb170_termo_equipe tag ON tag.nu_termo_equipe = tae.nu_termo_ajuste_gerado
  INNER JOIN gct.gcttb102_fatura fteo ON fteo.nu_fatura = teo.nu_fatura_gerada
WHERE
  e.nu_equipe = 240
  AND tae.nu_termo_equipe = 5749
ORDER BY
  fteo.dt_final_fatura
LIMIT
  1;