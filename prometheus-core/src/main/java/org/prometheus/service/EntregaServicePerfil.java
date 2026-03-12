package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.EntregaDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntregaServicePerfil {

	@Autowired
	private DatabaseService databaseService;

	public List<EntregaDTO> listarServicosContratados(Long nuEquipe, Long nuFatura) {
		return listar(nuEquipe, nuFatura, getDMLServicosContratados());
	}

	public List<EntregaDTO> listarServicosEntregues(Long nuEquipe, Long nuFatura) {
		return listar(nuEquipe, nuFatura, getDMLServicosEntregues());
	}

	public List<EntregaDTO> listarServicosEntreguesComAtraso(Long nuEquipe, Long nuFatura) {
		return listar(nuEquipe, nuFatura, getDMLServicosAtrasados());
	}

	public List<EntregaDTO> listarServicosDevolvidos(Long nuEquipe, Long nuFatura) {
		return listar(nuEquipe, nuFatura, getDMLServicosDevolvidos());
	}

	public List<EntregaDTO> listarServicosEntreguesNoPeriodo(Long nuEquipe, Long nuFatura) {
		return listar(nuEquipe, nuFatura, getDMLServicosEntreguesNoPeriodo());
	}

	private List<EntregaDTO> listar(Long nuEquipe, Long nuFatura, String sql) {
		List<EntregaDTO> result = new ArrayList<EntregaDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, nuFatura);
			pstmt.setLong(2, nuEquipe);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				EntregaDTO dto = new EntregaDTO();

				dto.setCoSolicitacao(rs.getString("co_solicitacao"));
				dto.setDeSituacaoSolicitacao(rs.getString("de_situacao_solicitacao"));
				dto.setNoSolicitacao(rs.getString("no_solicitacao"));
				dto.setDeSolicitacao(rs.getString("de_solicitacao"));
				dto.setDhPrevista(rs.getTimestamp("dh_prevista"));
				dto.setDhPrevistaPendencias(rs.getDate("dh_prevista_pendencias"));
				dto.setDhEfetiva(rs.getTimestamp("dh_efetiva"));
				dto.setIcServicoCompoeCalculoFns(rs.getString("ic_servico_compoe_calculo_fns"));
				dto.setCoFalhaSolicitacao(rs.getString("co_falha_solicitacao"));
				dto.setIcEntregaForaPeriodo(rs.getString("ic_entrega_fora_periodo"));
				dto.setQtDiasPendencia(rs.getBigDecimal("qt_dias_pendencia"));

				result.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return result;
	}

	private String getDMLServicosContratados() {

		StringBuilder sb = new StringBuilder();

		sb.append("select "); 
		sb.append("  distinct s.co_solicitacao, "); 
		sb.append("  s.de_situacao_solicitacao, "); 
		sb.append("  s.de_solicitacao, "); 
		sb.append("  s.no_solicitacao, "); 
		sb.append("  s.dh_prevista, "); 
		sb.append("  gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) dh_prevista_pendencias, "); 
		sb.append("  s.dh_efetiva, "); 
		sb.append("  sc.ic_servico_compoe_calculo_fns ic_servico_compoe_calculo_fns, "); 
		sb.append("  se.co_falha_solicitacao, "); 
		sb.append("  case when "); 
		sb.append("    coalesce(s.dh_efetiva,current_date)::date > f.dt_final_fatura::date "); 
		sb.append("  then 'S' else 'N' end ic_entrega_fora_periodo, "); 
		sb.append("  ( "); 
		sb.append("    select floor(sum(coalesce(qt_intervalo,0))/3600/24) "); 
		sb.append("      from gct.gcttb225_pendencia_solicitacao "); 
		sb.append("     where nu_solicitacao = se.nu_solicitacao "); 
		sb.append("  ) qt_dias_pendencia "); 
		sb.append("from "); 
		sb.append("  gct.gcttb087_equipe e "); 
		sb.append("  inner join gct.gcttb086_solicitacao_equipe se on e.nu_equipe = se.nu_equipe "); 
		sb.append("  inner join gct.gcttb005_solicitacao s on se.nu_solicitacao = s.nu_solicitacao "); 
		sb.append("  inner join gct.gcttb055_servico_contrato sc on ( "); 
		sb.append("    sc.nu_contrato = e.nu_contrato "); 
		sb.append("    and sc.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("    and sc.nu_tipo_servico = se.nu_tipo_servico "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = ? "); 
		sb.append("where "); 
		sb.append("  se.nu_equipe = ? "); 
		sb.append("  and ( "); 
		sb.append("    f.dt_inicio_fatura <= gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) "); 
		sb.append("    and f.dt_final_fatura >= gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) "); 
		sb.append(") "); 
		sb.append("  and sc.ic_servico_compoe_calculo_fns = 'S' "); 
		sb.append("  and (se.co_falha_solicitacao is null or se.co_falha_solicitacao = 'NF') "); 
		sb.append("  and exists ("); 
		sb.append("    select "); 
		sb.append("      se1.nu_situacao_atendimento "); 
		sb.append("    from "); 
		sb.append("      gct.gcttb192_servico_contratado se1 "); 
		sb.append("    where "); 
		sb.append("      se1.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      and se1.nu_contrato = e.nu_contrato ");
		sb.append("      and se1.nu_situacao_atendimento = s.nu_situacao_solicitacao ");
		sb.append("  ); ");

		return sb.toString();
	}

	private String getDMLServicosAtrasados() {

		StringBuilder sb = new StringBuilder();

		sb.append("select "); 
		sb.append("  distinct s.co_solicitacao, "); 
		sb.append("  s.de_situacao_solicitacao, "); 
		sb.append("  s.de_solicitacao, "); 
		sb.append("  s.no_solicitacao, "); 
		sb.append("  s.dh_prevista, "); 
		sb.append("  gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) dh_prevista_pendencias, "); 
		sb.append("  s.dh_efetiva, "); 
		sb.append("  sc.ic_servico_compoe_calculo_fns ic_servico_compoe_calculo_fns, "); 
		sb.append("  se.co_falha_solicitacao, "); 
		sb.append("  case when "); 
		sb.append("    coalesce(s.dh_efetiva,current_date)::date > f.dt_final_fatura::date "); 
		sb.append("  then 'S' else 'N' end ic_entrega_fora_periodo, "); 
		sb.append("  ( "); 
		sb.append("    select floor(sum(coalesce(qt_intervalo,0))/3600/24) "); 
		sb.append("      from gct.gcttb225_pendencia_solicitacao "); 
		sb.append("     where nu_solicitacao = se.nu_solicitacao "); 
		sb.append("  ) qt_dias_pendencia "); 
		sb.append("from "); 
		sb.append("  gct.gcttb087_equipe e "); 
		sb.append("  inner join gct.gcttb086_solicitacao_equipe se on e.nu_equipe = se.nu_equipe "); 
		sb.append("  inner join gct.gcttb005_solicitacao s on se.nu_solicitacao = s.nu_solicitacao "); 
		sb.append("  inner join gct.gcttb055_servico_contrato sc on ( "); 
		sb.append("    sc.nu_contrato = e.nu_contrato "); 
		sb.append("    and sc.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("    and sc.nu_tipo_servico = se.nu_tipo_servico "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = ? "); 
		sb.append("where "); 
		sb.append("  se.nu_equipe = ? "); 
		sb.append("  and s.dh_efetiva::date between f.dt_inicio_fatura and f.dt_final_fatura "); 
		sb.append("  and s.dh_efetiva::date > gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista))::date "); 
		sb.append("  and sc.ic_servico_compoe_calculo_fns = 'S' "); 
		sb.append("  and (se.co_falha_solicitacao is null or se.co_falha_solicitacao = 'NF') "); 
		
		return sb.toString();
	}

	private String getDMLServicosDevolvidos() {

		StringBuilder sb = new StringBuilder();

		sb.append("select "); 
		sb.append("  e.co_equipe, "); 
		sb.append("  f.nu_fatura, "); 
		sb.append("  s.co_solicitacao, "); 
		sb.append("  s.de_situacao_solicitacao, "); 
		sb.append("  s.no_solicitacao, "); 
		sb.append("  s.de_solicitacao, "); 
		sb.append("  f.dt_final_fatura, "); 
		sb.append("  s.dh_prevista, "); 
		sb.append("  gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) dh_prevista_pendencias, "); 
		sb.append("  s.dh_efetiva, "); 
		sb.append("  sc.ic_servico_compoe_calculo_fns, "); 
		sb.append("  se.co_falha_solicitacao, "); 
		sb.append("  case when "); 
		sb.append("    coalesce(s.dh_efetiva,current_date)::date > f.dt_final_fatura::date "); 
		sb.append("  then 'S' else 'N' end ic_entrega_fora_periodo, "); 
		sb.append("  ( "); 
		sb.append("    select floor(sum(coalesce(qt_intervalo,0))/3600/24) "); 
		sb.append("      from gct.gcttb225_pendencia_solicitacao "); 
		sb.append("     where nu_solicitacao = se.nu_solicitacao "); 
		sb.append("  ) qt_dias_pendencia "); 
		sb.append("from "); 
		sb.append("  gct.gcttb087_equipe e "); 
		sb.append("  inner join gct.gcttb086_solicitacao_equipe se on e.nu_equipe = se.nu_equipe "); 
		sb.append("  inner join gct.gcttb005_solicitacao s on se.nu_solicitacao = s.nu_solicitacao "); 
		sb.append("  inner join gct.gcttb055_servico_contrato sc on ( "); 
		sb.append("    sc.nu_contrato = e.nu_contrato "); 
		sb.append("    and sc.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("    and sc.nu_tipo_servico = se.nu_tipo_servico "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = ? "); 
		sb.append("  inner join gct.gcttb226_devolucao_solicitacao rs on ( "); 
		sb.append("    rs.nu_solicitacao = s.nu_solicitacao "); 
		sb.append("    and coalesce(rs.qt_devolucao_manual,0) <> 0 "); 
		sb.append("    and rs.nu_fatura = f.nu_fatura "); 
		sb.append("  ) "); 
		sb.append("where "); 
		sb.append("  se.nu_equipe = ? "); 
		sb.append("  and sc.ic_servico_compoe_calculo_fns = 'S' "); 
		sb.append("  and (se.co_falha_solicitacao is null or se.co_falha_solicitacao = 'NF') "); 
		sb.append("  and exists ( "); 
		sb.append("    select "); 
		sb.append("      se1.nu_situacao_atendimento "); 
		sb.append("    from "); 
		sb.append("      gct.gcttb194_servico_devolvido se1 "); 
		sb.append("    where "); 
		sb.append("      se1.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      and se1.nu_contrato = e.nu_contrato "); 
		sb.append("  ); "); 
		
		return sb.toString();
	}

	private String getDMLServicosEntregues() {

		StringBuilder sb = new StringBuilder();

		sb.append("select "); 
		sb.append("  distinct s.co_solicitacao, "); 
		sb.append("  s.de_situacao_solicitacao, "); 
		sb.append("  s.de_solicitacao, "); 
		sb.append("  s.no_solicitacao, "); 
		sb.append("  s.dh_prevista, "); 
		sb.append("  gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) dh_prevista_pendencias, "); 
		sb.append("  s.dh_efetiva, "); 
		sb.append("  sc.ic_servico_compoe_calculo_fns ic_servico_compoe_calculo_fns, "); 
		sb.append("  se.co_falha_solicitacao, "); 
		sb.append("  case when "); 
		sb.append("    coalesce(s.dh_efetiva,current_date)::date > f.dt_final_fatura::date "); 
		sb.append("  then 'S' else 'N' end ic_entrega_fora_periodo, "); 
		sb.append("  ( "); 
		sb.append("    select floor(sum(coalesce(qt_intervalo,0))/3600/24) "); 
		sb.append("      from gct.gcttb225_pendencia_solicitacao "); 
		sb.append("     where nu_solicitacao = se.nu_solicitacao "); 
		sb.append("  ) qt_dias_pendencia "); 
		sb.append("from "); 
		sb.append("  gct.gcttb087_equipe e "); 
		sb.append("  inner join gct.gcttb086_solicitacao_equipe se on e.nu_equipe = se.nu_equipe "); 
		sb.append("  inner join gct.gcttb005_solicitacao s on se.nu_solicitacao = s.nu_solicitacao "); 
		sb.append("  inner join gct.gcttb055_servico_contrato sc on ( "); 
		sb.append("    sc.nu_contrato = e.nu_contrato "); 
		sb.append("    and sc.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("    and sc.nu_tipo_servico = se.nu_tipo_servico "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = ? "); 
		sb.append("where "); 
		sb.append("  se.nu_equipe = ? "); 
		sb.append("  and gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) between f.dt_inicio_fatura and f.dt_final_fatura");
		sb.append("  and s.dh_efetiva is not null ");
		sb.append("  and sc.ic_servico_compoe_calculo_fns = 'S' "); 
		sb.append("  and (se.co_falha_solicitacao is null or se.co_falha_solicitacao = 'NF') "); 

		return sb.toString();
	}	
	
	private String getDMLServicosEntreguesNoPeriodo() {

		StringBuilder sb = new StringBuilder();

		sb.append("select "); 
		sb.append("  distinct s.co_solicitacao, "); 
		sb.append("  s.de_situacao_solicitacao, "); 
		sb.append("  s.de_solicitacao, "); 
		sb.append("  s.no_solicitacao, "); 
		sb.append("  s.dh_prevista, "); 
		sb.append("  gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) dh_prevista_pendencias, "); 
		sb.append("  s.dh_efetiva, "); 
		sb.append("  sc.ic_servico_compoe_calculo_fns, "); 
		sb.append("  se.co_falha_solicitacao, "); 
		sb.append("  case when "); 
		sb.append("    coalesce(s.dh_efetiva,current_date)::date > f.dt_final_fatura::date "); 
		sb.append("  then 'S' else 'N' end ic_entrega_fora_periodo, "); 
		sb.append("  ( "); 
		sb.append("    select floor(sum(coalesce(qt_intervalo,0))/3600/24) "); 
		sb.append("      from gct.gcttb225_pendencia_solicitacao "); 
		sb.append("     where nu_solicitacao = se.nu_solicitacao "); 
		sb.append("  ) qt_dias_pendencia "); 
		sb.append("from "); 
		sb.append("  gct.gcttb087_equipe e "); 
		sb.append("  inner join gct.gcttb086_solicitacao_equipe se on e.nu_equipe = se.nu_equipe "); 
		sb.append("  inner join gct.gcttb005_solicitacao s on se.nu_solicitacao = s.nu_solicitacao "); 
		sb.append("  inner join gct.gcttb055_servico_contrato sc on ( "); 
		sb.append("    sc.nu_contrato = e.nu_contrato "); 
		sb.append("    and sc.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("    and sc.nu_tipo_servico = se.nu_tipo_servico "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = ? "); 
		sb.append("where "); 
		sb.append("  se.nu_equipe = ? "); 
		sb.append("  and s.dh_efetiva::date between f.dt_inicio_fatura and f.dt_final_fatura "); 
		sb.append("  and sc.ic_servico_compoe_calculo_fns = 'S' "); 
		sb.append("  and (se.co_falha_solicitacao is null or se.co_falha_solicitacao = 'NF') "); 

		return sb.toString();
	}
}
