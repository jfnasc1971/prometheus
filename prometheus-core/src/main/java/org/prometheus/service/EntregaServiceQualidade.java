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
public class EntregaServiceQualidade {

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

	public List<EntregaDTO> listarServicosEntreguesEstoque(Long nuEquipe, Long nuFatura) {
		return listar(nuEquipe, nuFatura, getDMLServicosEntreguesEstoque());
	}

	private List<EntregaDTO> listar(Long nuEquipe, Long nuFatura, String sql) {
		return listar(nuEquipe, nuFatura, sql, null);
	}

	private List<EntregaDTO> listar(Long nuEquipe, Long nuFatura, String sql, Long nuTipoIndicador) {
		List<EntregaDTO> result = new ArrayList<EntregaDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, nuFatura);
			pstmt.setLong(2, nuEquipe);
			if (nuTipoIndicador != null) {
				pstmt.setLong(3, nuTipoIndicador);
			}

			rs = pstmt.executeQuery();

			while (rs.next()) {

				EntregaDTO dto = new EntregaDTO();

				dto.setCoSolicitacao(rs.getString("co_solicitacao"));
				dto.setNoSolicitacao(rs.getString("no_solicitacao"));
				dto.setDhPrevista(rs.getTimestamp("dh_prevista"));
				dto.setDhPrevistaPendencias(rs.getDate("dh_prevista_pendencias"));
				dto.setDhEfetiva(rs.getTimestamp("dh_efetiva"));
				dto.setIcServicoCompoeCalculoFns(rs.getString("ic_servico_compoe_calculo_fns"));
				dto.setCoFalhaSolicitacao(rs.getString("co_falha_solicitacao"));

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
		sb.append("  s.co_solicitacao, ");
		sb.append("  s.de_solicitacao, ");
		sb.append("  s.dh_prevista, ");
		sb.append("  gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) dh_prevista_pendencias, ");
		sb.append("  s.dh_efetiva, ");
		sb.append("  sc.ic_servico_compoe_calculo_fns, ");
		sb.append("  se.co_falha_solicitacao ");
		sb.append("from ");
		sb.append("  gct.gcttb177_amostra_solicitacao_equipe se ");
		sb.append("  inner join gct.gcttb175_amostra_equipe e on ( ");
		sb.append("    e.nu_equipe = se.nu_equipe ");
		sb.append("    and e.nu_fatura_amostra = se.nu_fatura_amostra ");
		sb.append("  ) ");
		sb.append("  inner join gct.gcttb055_servico_contrato sc on ( ");
		sb.append("    sc.nu_tipo_servico = se.nu_tipo_servico ");
		sb.append("    and e.nu_contrato = sc.nu_contrato ");
		sb.append("    and e.nu_tipo_grupo = sc.nu_tipo_grupo ");
		sb.append("  ) ");
		sb.append("  inner join gct.gcttb178_amostra_solicitacao s on ( ");
		sb.append("    se.nu_solicitacao = s.nu_solicitacao ");
		sb.append("    and s.nu_fatura_amostra = se.nu_fatura_amostra ");
		sb.append("  ) ");
		sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = se.nu_fatura_amostra ");
		sb.append("where ");
		sb.append("  se.nu_fatura_amostra = ? ");
		sb.append("  and se.nu_equipe = ? ");
		sb.append("  and ( "); 
		sb.append("    gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, s.dh_prevista :: date) "); 
		sb.append("      between f.dt_inicio_fatura and f.dt_final_fatura "); 
		sb.append("  ) ");
		sb.append("  and se.ic_solicitacao_compoe_fns = 'S' ");
		sb.append("  and se.co_falha_solicitacao = 'NF' ");
		sb.append("  and s.nu_situacao_solicitacao in ( ");
		sb.append("    select ");
		sb.append("      se1.nu_situacao_atendimento ");
		sb.append("    from ");
		sb.append("      gct.gcttb192_servico_contratado se1 ");
		sb.append("    where ");
		sb.append("      se1.nu_tipo_grupo = e.nu_tipo_grupo ");
		sb.append("      and se1.nu_contrato = e.nu_contrato ");
		sb.append("  ) ");

		return sb.toString();
	}

	private String getDMLServicosEntregues() {

		StringBuilder sb = new StringBuilder();

		sb.append("select ");
		sb.append("  s.co_solicitacao, ");
		sb.append("  s.de_solicitacao, ");
		sb.append("  s.dh_prevista, ");
		sb.append("  gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, ");
		sb.append("    date(s.dh_prevista)) dh_prevista_pendencias, ");
		sb.append("  s.dh_efetiva, ");
		sb.append("  sc.ic_servico_compoe_calculo_fns, ");
		sb.append("  se.co_falha_solicitacao ");
		sb.append("from ");
		sb.append("  gct.gcttb177_amostra_solicitacao_equipe se ");
		sb.append("  inner join gct.gcttb175_amostra_equipe e on ( ");
		sb.append("    e.nu_equipe = se.nu_equipe ");
		sb.append("    and e.nu_fatura_amostra = se.nu_fatura_amostra ");
		sb.append("  ) ");
		sb.append("  inner join gct.gcttb055_servico_contrato sc on ( ");
		sb.append("    sc.nu_tipo_servico = se.nu_tipo_servico ");
		sb.append("    and e.nu_contrato = sc.nu_contrato ");
		sb.append("    and e.nu_tipo_grupo = sc.nu_tipo_grupo ");
		sb.append("  ) ");
		sb.append("  inner join gct.gcttb178_amostra_solicitacao s on ( ");
		sb.append("    se.nu_solicitacao = s.nu_solicitacao ");
		sb.append("    and s.nu_fatura_amostra = se.nu_fatura_amostra ");
		sb.append("  ) ");
		sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = se.nu_fatura_amostra ");
		sb.append("where ");
		sb.append("  se.nu_fatura_amostra = ? ");
		sb.append("  and se.nu_equipe = ? ");
		sb.append("  and s.dh_resolucao::date between f.dt_inicio_fatura and f.dt_final_fatura ");
		sb.append("  and se.ic_solicitacao_compoe_fns = 'S' ");
		sb.append("  and se.co_falha_solicitacao = 'NF' ");
		sb.append("  and s.nu_situacao_solicitacao in ( ");
		sb.append("    select ");
		sb.append("      se1.nu_situacao_atendimento ");
		sb.append("    from ");
		sb.append("      gct.gcttb191_servico_entregue se1 ");
		sb.append("    where ");
		sb.append("      se1.nu_tipo_grupo = e.nu_tipo_grupo ");
		sb.append("      and se1.nu_contrato = e.nu_contrato ");
		sb.append("  ); ");

		return sb.toString();
	}

	private String getDMLServicosAtrasados() {

		StringBuilder sb = new StringBuilder();

		sb.append("select "); 
		sb.append("  s.co_solicitacao, "); 
		sb.append("  s.de_solicitacao, "); 
		sb.append("  s.dh_prevista, "); 
		sb.append("  gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) dh_prevista_pendencias, "); 
		sb.append("  s.dh_efetiva, "); 
		sb.append("  sc.ic_servico_compoe_calculo_fns, "); 
		sb.append("  se.co_falha_solicitacao "); 
		sb.append("from "); 
		sb.append("  gct.gcttb177_amostra_solicitacao_equipe se "); 
		sb.append("  inner join gct.gcttb175_amostra_equipe e on ( "); 
		sb.append("    e.nu_equipe = se.nu_equipe "); 
		sb.append("    and e.nu_fatura_amostra = se.nu_fatura_amostra "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb055_servico_contrato sc on ( "); 
		sb.append("    sc.nu_tipo_servico = se.nu_tipo_servico "); 
		sb.append("    and e.nu_contrato = sc.nu_contrato "); 
		sb.append("    and e.nu_tipo_grupo = sc.nu_tipo_grupo "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb178_amostra_solicitacao s on ( "); 
		sb.append("    se.nu_solicitacao = s.nu_solicitacao "); 
		sb.append("    and s.nu_fatura_amostra = se.nu_fatura_amostra "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = se.nu_fatura_amostra "); 
		sb.append("where "); 
		sb.append("  se.nu_fatura_amostra = ? "); 
		sb.append("  and se.nu_equipe = ? ");
		sb.append("  and s.dh_resolucao :: date between f.dt_inicio_fatura and f.dt_final_fatura ");
		sb.append("  and se.ic_solicitacao_compoe_fns = 'S' "); 
		sb.append("  and se.co_falha_solicitacao = 'NF' "); 
		sb.append("  and s.dh_efetiva is not null "); 
		sb.append("  and s.dh_efetiva::date > gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, s.dh_prevista :: date)::date "); 
		sb.append("  and s.nu_situacao_solicitacao in ( "); 
		sb.append("    select "); 
		sb.append("      se1.nu_situacao_atendimento "); 
		sb.append("    from "); 
		sb.append("      gct.gcttb193_servico_atraso se1 "); 
		sb.append("    where "); 
		sb.append("      se1.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      and se1.nu_contrato = e.nu_contrato "); 
		sb.append("  ) ");
		
		return sb.toString();
	}

	private String getDMLServicosDevolvidos() {

		StringBuilder sb = new StringBuilder();

		sb.append("select "); 
		sb.append("  e.co_equipe, "); 
		sb.append("  se.nu_fatura_amostra, "); 
		sb.append("  s.co_solicitacao, "); 
		sb.append("  s.de_solicitacao, "); 
		sb.append("  f.dt_final_fatura, "); 
		sb.append("  s.dh_prevista, "); 
		sb.append("  gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) dh_prevista_pendencias, "); 
		sb.append("  s.dh_efetiva, "); 
		sb.append("  sc.ic_servico_compoe_calculo_fns, "); 
		sb.append("  se.co_falha_solicitacao "); 
		sb.append("from "); 
		sb.append("  gct.gcttb177_amostra_solicitacao_equipe se "); 
		sb.append("  inner join gct.gcttb175_amostra_equipe e on ( "); 
		sb.append("    e.nu_equipe = se.nu_equipe "); 
		sb.append("    and e.nu_fatura_amostra = se.nu_fatura_amostra "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb055_servico_contrato sc on ( "); 
		sb.append("    sc.nu_tipo_servico = se.nu_tipo_servico "); 
		sb.append("    and e.nu_contrato = sc.nu_contrato "); 
		sb.append("    and e.nu_tipo_grupo = sc.nu_tipo_grupo "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb178_amostra_solicitacao s on ( "); 
		sb.append("    se.nu_solicitacao = s.nu_solicitacao "); 
		sb.append("    and s.nu_fatura_amostra = se.nu_fatura_amostra "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = se.nu_fatura_amostra "); 
		sb.append("  inner join gct.gcttb226_devolucao_solicitacao rs on ( "); 
		sb.append("    rs.nu_solicitacao = s.nu_solicitacao "); 
		sb.append("  ) "); 
		sb.append("where "); 
		sb.append("  se.nu_fatura_amostra = ? "); 
		sb.append("  and se.nu_equipe = ? ");
		sb.append("  and s.dh_resolucao :: date between f.dt_inicio_fatura and f.dt_final_fatura ");
		sb.append("  and rs.dh_rejeicao between f.dt_inicio_fatura and f.dt_final_fatura "); 
		sb.append("  and se.ic_solicitacao_compoe_fns = 'S' "); 
		sb.append("  and se.co_falha_solicitacao = 'NF' "); 
		sb.append("  and s.nu_situacao_solicitacao in ( "); 
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

	private String getDMLServicosEntreguesEstoque() {

		StringBuilder sb = new StringBuilder();

		sb.append("select "); 
		sb.append("  s.co_solicitacao, "); 
		sb.append("  s.de_solicitacao, "); 
		sb.append("  s.dh_prevista, "); 
		sb.append("  gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) dh_prevista_pendencias, "); 
		sb.append("  s.dh_efetiva, "); 
		sb.append("  sc.ic_servico_compoe_calculo_fns, "); 
		sb.append("  se.co_falha_solicitacao "); 
		sb.append("from "); 
		sb.append("  gct.gcttb177_amostra_solicitacao_equipe se "); 
		sb.append("  inner join gct.gcttb175_amostra_equipe e on ( "); 
		sb.append("    e.nu_equipe = se.nu_equipe "); 
		sb.append("    and e.nu_fatura_amostra = se.nu_fatura_amostra "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb055_servico_contrato sc on ( "); 
		sb.append("    sc.nu_tipo_servico = se.nu_tipo_servico "); 
		sb.append("    and e.nu_contrato = sc.nu_contrato "); 
		sb.append("    and e.nu_tipo_grupo = sc.nu_tipo_grupo "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb178_amostra_solicitacao s on ( "); 
		sb.append("    se.nu_solicitacao = s.nu_solicitacao "); 
		sb.append("    and s.nu_fatura_amostra = se.nu_fatura_amostra "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = se.nu_fatura_amostra "); 
		sb.append("where "); 
		sb.append("  se.nu_fatura_amostra = ? "); 
		sb.append("  and se.nu_equipe = ? "); 
		sb.append("  and se.ic_solicitacao_compoe_fns = 'S' "); 
		sb.append("  and se.co_falha_solicitacao = 'NF' "); 
		sb.append("  and s.dh_efetiva::date between f.dt_inicio_fatura and f.dt_final_fatura ");
		sb.append("  and gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista))::date "); 
		sb.append("    between f.dt_inicio_fatura and f.dt_final_fatura ");
		sb.append("  and s.nu_situacao_solicitacao in ( "); 
		sb.append("    select "); 
		sb.append("      se1.nu_situacao_atendimento "); 
		sb.append("    from "); 
		sb.append("      gct.gcttb191_servico_entregue se1 "); 
		sb.append("    where "); 
		sb.append("      se1.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      and se1.nu_contrato = e.nu_contrato "); 
		sb.append("  ) ");

		return sb.toString();
	}
}
