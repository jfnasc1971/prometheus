DROP VIEW IF EXISTS gct.gctvw900_qt_membros_efetiva_faturamento;
DROP VIEW IF EXISTS gct.gctvw901_qt_membros_efetiva;

--
--
--
CREATE VIEW gct.gctvw901_qt_membros_efetiva AS
SELECT
  nu_contrato,
  nu_equipe,
  nu_fatura,
  qt_dias_fat,
  qt_dias_trab,
  qt_membros_equipe,
  (qt_dias_trab / qt_dias_fat) * qt_membros_equipe AS qt_membros_efetiva
FROM
  (
    SELECT
      nu_contrato,
      nu_equipe,
      nu_fatura,
      gct.gctfn305_dias_entre_data(
        dt_inicio_fatura,
        dt_final_fatura,
        co_sitio,
        ic_dias_calculo_pagamento
      ) AS qt_dias_fat,
      gct.gctfn305_dias_entre_data(
        dt_inicio_vigencia_membros,
        dt_final_vigencia_membros,
        co_sitio,
        ic_dias_calculo_pagamento
      ) AS qt_dias_trab,
      qt_membros_equipe
    FROM
      (
        SELECT
          c.nu_contrato,
          e.nu_equipe,
          f.nu_fatura,
          f.dt_inicio_fatura,
          f.dt_final_fatura,
          e.co_sitio,
          gc.ic_dias_calculo_pagamento,
          CASE
            WHEN q.dt_inicio_vigencia_membros < f.dt_inicio_fatura THEN f.dt_inicio_fatura
            ELSE q.dt_inicio_vigencia_membros
          END AS dt_inicio_vigencia_membros,
          CASE
            WHEN coalesce(
              q.dt_final_vigencia_membros,
              c.dt_final_compromisso
            ) > f.dt_final_fatura THEN f.dt_final_fatura
            ELSE q.dt_final_vigencia_membros
          END AS dt_final_vigencia_membros,
          q.qt_membros_equipe
        FROM
          gct.gcttb087_equipe e
          INNER JOIN gct.gcttb170_termo_equipe t ON t.nu_equipe = e.nu_equipe
          INNER JOIN gct.gcttb102_fatura f ON f.nu_fatura = t.nu_fatura_gerada
          INNER JOIN gct.gcttb010_contrato c ON c.nu_contrato = e.nu_contrato
          INNER JOIN gct.gcttb033_grupo_contrato gc ON (
            gc.nu_contrato = e.nu_contrato
            AND gc.nu_tipo_grupo = e.nu_tipo_grupo
          )          
          INNER JOIN gct.gcttb097_qntde_membros_equipe q ON q.nu_equipe = e.nu_equipe 
        WHERE
          t.nu_tipo_termo = 11
          AND q.dt_inicio_vigencia_membros <= f.dt_final_fatura
          AND coalesce(
            q.dt_final_vigencia_membros,
            c.dt_final_compromisso
          ) >= f.dt_inicio_fatura
      ) tb
  ) tb2;

--
--
--
CREATE VIEW gct.gctvw900_qt_membros_efetiva_faturamento AS
SELECT
  nu_contrato,
  nu_equipe,
  nu_fatura,
  qt_dias_fat,
  qt_dias_trab,
  qt_membros_equipe,
  (qt_dias_trab / qt_dias_fat) * qt_membros_equipe AS qt_membros_efetiva
FROM
  (
    SELECT
      nu_contrato,
      nu_equipe,
      nu_fatura,
      gct.gctfn305_dias_entre_data(
        dt_inicio_fatura,
        dt_final_fatura,
        co_sitio,
        ic_dias_calculo_pagamento
      ) AS qt_dias_fat,
      gct.gctfn305_dias_entre_data(
        dt_inicio_vigencia_membros,
        dt_final_vigencia_membros,
        co_sitio,
        ic_dias_calculo_pagamento
      ) AS qt_dias_trab,
      qt_membros_equipe
    FROM
      (
        SELECT
          c.nu_contrato,
          e.nu_equipe,
          f.nu_fatura,
          f.dt_inicio_fatura,
          f.dt_final_fatura,
          e.co_sitio,
          gc.ic_dias_calculo_pagamento,
          CASE
            WHEN q.dt_inicio_vigencia_membros < f.dt_inicio_fatura THEN f.dt_inicio_fatura
            ELSE q.dt_inicio_vigencia_membros
          END AS dt_inicio_vigencia_membros,
          CASE
            WHEN coalesce(
              q.dt_final_vigencia_membros,
              c.dt_final_compromisso
            ) > f.dt_final_fatura THEN f.dt_final_fatura
            ELSE q.dt_final_vigencia_membros
          END AS dt_final_vigencia_membros,
          q.qt_membros_equipe
        FROM
          gct.gcttb087_equipe e
          INNER JOIN gct.gcttb170_termo_equipe t ON t.nu_equipe = e.nu_equipe
          INNER JOIN gct.gcttb102_fatura f ON f.nu_fatura = t.nu_fatura_gerada
          INNER JOIN gct.gcttb010_contrato c ON c.nu_contrato = e.nu_contrato
          INNER JOIN gct.gcttb033_grupo_contrato gc ON (
            gc.nu_contrato = e.nu_contrato
            AND gc.nu_tipo_grupo = e.nu_tipo_grupo
          )
          INNER JOIN gct.gcttb183_amostra_qntde_membros_equipe q ON (
            q.nu_equipe = e.nu_equipe
            AND q.nu_fatura_amostra = f.nu_fatura
          )
        WHERE
          t.nu_tipo_termo = 11
          AND q.dt_inicio_vigencia_membros <= f.dt_final_fatura
          AND coalesce(
            q.dt_final_vigencia_membros,
            c.dt_final_compromisso
          ) >= f.dt_inicio_fatura
      ) tb
  ) tb2;

SELECT
  *
FROM
  gct.gctvw901_qt_membros_efetiva
WHERE
  nu_equipe = 255
  AND nu_fatura = 2994;

SELECT
  *
FROM
  gct.gctvw900_qt_membros_efetiva_faturamento
WHERE
  nu_equipe = 255
  AND nu_fatura = 2994;