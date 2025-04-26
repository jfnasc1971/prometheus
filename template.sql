select
  f.nu_fatura,
  f.de_fatura,
  tp.nu_tipo_preco,
  tp.no_tipo_preco,
  p.vr_preco_grupo,
  q.qt_solicitacao,
  p.vr_preco_grupo * q.qt_solicitacao vr_previsto
from
  gct.gcttb244_quantidade_contratacao q
  inner join gct.gcttb087_equipe e on e.nu_equipe = q.nu_equipe
  inner join gct.gcttb102_fatura f on f.nu_fatura = q.nu_fatura
  inner join gct.gcttb053_tipo_preco tp on tp.nu_tipo_preco = q.nu_tipo_preco
  inner join gct.gcttb014_unidade_medida um on um.nu_unidade_medida = q.nu_unidade_medida
  inner join gct.gcttb165_preco_contrato_equipe p on (
    p.nu_contrato = e.nu_contrato
    and p.nu_tipo_grupo = e.nu_tipo_grupo
    and p.nu_tipo_preco = q.nu_tipo_preco
    and f.dt_final_fatura between p.dt_inicio_vigencia and p.dt_final_vigencia
  )
where
  e.nu_equipe = 2832
  and q.nu_fatura = 5661;


NETFLIX ANUNCIO INCLUSO R$ 20,90
PREMIERE HD ANUAL R$ 29,88
TELECINE ANUAL PROMO19 R$ 19,89
TELECINE ANUAL PROMO R$ 9,89