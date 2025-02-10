select d.co_demanda
from gct.gcttb023_ordem_servico os
inner join gct.gcttb021_demanda d on d.nu_demanda=os.nu_demanda
where os.nu_contrato=649
and os.nu_tipo_grupo=1
order by 1

--
--
--
select d.co_demanda, m.no_modulo, s.sg_sistema, s.no_sistema, tsd.de_situacao_demanda,
d.nu_demanda, m.nu_modulo, s.nu_sistema
from gct.gcttb021_demanda d
inner join gct.gcttb039_sistema s on s.nu_sistema=d.nu_sistema
inner join gct.gcttb040_modulo m on m.nu_modulo = d.nu_modulo
inner join gct.gcttb008_tipo_situacao_demanda tsd on tsd.nu_situacao_demanda=d.nu_situacao_demanda
where d.co_demanda='SP202400085';

--
--
--
select os.co_ordem_servico, tg.no_tipo_grupo, ts.no_tipo_servico, tf.de_tipo_forma, os.vr_estmo_ordem_servico
from gct.gcttb021_demanda d
inner join gct.gcttb023_ordem_servico os on os.nu_demanda=d.nu_demanda
inner join gct.gcttb032_tipo_grupo tg on tg.nu_tipo_grupo=os.nu_tipo_grupo
inner join gct.gcttb015_tipo_servico ts on ts.nu_tipo_servico=os.nu_tipo_servico
inner join gct.gcttb016_tipo_forma tf on tf.nu_tipo_forma=os.nu_tipo_forma
where d.co_demanda='SP202400085';

select d.co_demanda, os.co_ordem_servico, t.co_termo, f.dt_final_fatura, t.vr_financeiro_termo
from gct.gcttb021_demanda d
inner join gct.gcttb023_ordem_servico os on os.nu_demanda=d.nu_demanda
inner join gct.gcttb100_termo t on t.nu_ordem_servico=os.nu_ordem_servico
inner join gct.gcttb102_fatura f on f.nu_fatura=t.nu_fatura_paga
where d.co_demanda='SP202400085'
and t.nu_situacao_termo in(1,3)
order by d.co_demanda, os.co_ordem_servico, t.co_termo;




select
  os.nu_ordem_servico,
  os.co_ordem_servico,
  e.nu_entrega,
  e.co_entrega,
  e.de_situacao_entrega,
  e.dh_prevista_entrega,
  e.dh_efetiva_entrega,
  e.pc_entrega/100::numeric pc_entrega,
  e.pc_executado/100::numeric pc_executado,
  e.de_situacao_entrega,
  e.vr_preco,
  (
    select
      sum(qos.qt_solicitacao)
    from
      gct.gctvw901_quantidades qos
    where
      qos.nu_ordem_servico = d.nu_ordem_servico
  ) qt_solicitacao,
  (
    select
      sum(qos.qt_solicitacao * qos.vr_ajuste_financeiro)
    from
      gct.gctvw901_quantidades qos
    where
      qos.nu_ordem_servico = d.nu_ordem_servico
  ) qt_solicitacao_ajustado,
  (
    select
      sum(qos.qt_solicitacao * qos.vr_ajuste_financeiro) * (e.pc_entrega / 100) * (e.pc_executado / 100) * vr_preco
    from
      gct.gctvw901_quantidades qos
    where
      qos.nu_ordem_servico = d.nu_ordem_servico
  ) vr_financeiro_entrega
from
  gct.gctvw900_demandas d
  inner join gct.gctvw902_entregas e on e.nu_ordem_servico = d.nu_ordem_servico
  inner join gct.gcttb023_ordem_servico os on os.nu_ordem_servico=d.nu_ordem_servico
where
  os.nu_ordem_servico=692662;

sb.append("select "); 
sb.append("  sum(qos.qt_solicitacao * tq.vr_ajuste_financeiro) qt_solicitacao_ajustado "); 
sb.append("from "); 
sb.append("  gct.gcttb023_ordem_servico os "); 
sb.append("  inner join gct.gcttb051_quantidade_ordem_serv qos on qos.nu_ordem_servico = os.nu_ordem_servico "); 
sb.append("  inner join gct.gcttb049_tipo_quantidade tq on tq.nu_tipo_quantidade = qos.nu_tipo_quantidade "); 
sb.append("  inner join gct.gcttb005_solicitacao s on s.nu_solicitacao = qos.nu_solicitacao "); 
sb.append("where "); 
sb.append("  os.nu_ordem_servico = 692662; ");

  