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
public class EntregaService {

	@Autowired
	private DatabaseService databaseService;

	public List<EntregaDTO> listarServicosContratados(Long nuEquipe, Long nuFatura, Long nuTipoIndicador) {
		return listar(nuEquipe, nuFatura, getDMLServicosContratados(), nuTipoIndicador);
	}

	public List<EntregaDTO> listarServicosEntregues(Long nuEquipe, Long nuFatura) {
		return listar(nuEquipe, nuFatura, getDMLServicosEntregues());
	}

	public List<EntregaDTO> listarServicosEntreguesComAtraso(Long nuEquipe, Long nuFatura) {
		return listar(nuEquipe, nuFatura, getDMLServicosEntreguesComAtraso());
	}

	public List<EntregaDTO> listarServicosDevolvidos(Long nuEquipe, Long nuFatura) {
		return listar(nuEquipe, nuFatura, getDMLServicosDevolvidos());
	}

	public List<EntregaDTO> listarServicosEstoque(Long nuEquipe, Long nuFatura) {
		return listar(nuEquipe, nuFatura, getDMLServicosEstoque());
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
				dto.setDeSituacaoSolicitacao(rs.getString("de_situacao_solicitacao"));
				dto.setNuSituacaoSolicitacao(rs.getBigDecimal("nu_situacao_solicitacao"));

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
		sb.append("  s.no_solicitacao, ");
		sb.append("  s.dh_prevista, ");
		sb.append("  gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, ");
		sb.append("    date(s.dh_prevista)) dh_prevista_pendencias, ");
		sb.append("  s.dh_efetiva, ");
		sb.append("  sc.ic_servico_compoe_calculo_fns, ");
		sb.append("  coalesce(se.co_falha_solicitacao, 'NF') as co_falha_solicitacao, ") ; 
		sb.append("  s.nu_situacao_solicitacao, "); 
		sb.append("  s.de_situacao_solicitacao "); 
		sb.append(" from");
		sb.append("     gct.gcttb086_solicitacao_equipe se");
		sb.append("     inner join gct.gcttb087_equipe e on e.nu_equipe = se.nu_equipe");
		sb.append("     inner join gct.gcttb001_area_interna ai on ai.nu_area_interna = e.nu_area_interna");
		sb.append("     inner join gct.gcttb010_contrato c on c.nu_contrato = e.nu_contrato");
		sb.append("     inner join gct.gcttb005_solicitacao s on (se.nu_solicitacao = s.nu_solicitacao)");
		sb.append("     inner join gct.gcttb055_servico_contrato sc on (");
		sb.append("         sc.nu_tipo_servico = se.nu_tipo_servico");
		sb.append("         and e.nu_tipo_grupo = sc.nu_tipo_grupo");
		sb.append("         and e.nu_contrato = sc.nu_contrato");
		sb.append("     )");
		sb.append("     inner join gct.gcttb102_fatura f on f.nu_fatura = ?");
		sb.append(" where");
		sb.append("     se.nu_equipe = ?");
		sb.append("     and sc.ic_servico_compoe_calculo_fns = 'S'");
		sb.append("     and coalesce(se.co_falha_solicitacao, 'NF') = 'NF' ");
		sb.append("     and (");
		sb.append("         f.dt_inicio_fatura <= ");
		sb.append("           gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista))");
		sb.append("         and");
		sb.append("         f.dt_final_fatura >= ");
		sb.append("           gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista))");
		sb.append("     )");
		sb.append("     and s.nu_situacao_solicitacao in (");
		sb.append("         select");
		sb.append("             nu_situacao_atendimento");
		sb.append("         from");
		sb.append("             gct.gcttb174_grupo_indicador_situacao");
		sb.append("         where");
		sb.append("             nu_contrato = e.nu_contrato");
		sb.append("             and nu_tipo_grupo = e.nu_tipo_grupo");
		sb.append("             and nu_tipo_indicador = ?");
		sb.append("             and upper(no_situacao_atendimento) = upper('SERVICOS_CONTRATADOS')");
		sb.append("     )");

		return sb.toString();
	}

	private String getDMLServicosEntregues() {

		StringBuilder sb = new StringBuilder();

		sb.append("select ");
		sb.append("  s.co_solicitacao, ");
		sb.append("  s.no_solicitacao, ");
		sb.append("  s.dh_prevista, ");
		sb.append("  gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, ");
		sb.append("    date(s.dh_prevista)) dh_prevista_pendencias, ");
		sb.append("  s.dh_efetiva, ");
		sb.append("  sc.ic_servico_compoe_calculo_fns, ");
		sb.append("  coalesce(se.co_falha_solicitacao, 'NF') as co_falha_solicitacao, ") ; 
		sb.append("  s.nu_situacao_solicitacao, "); 
		sb.append("  s.de_situacao_solicitacao "); 
		sb.append("from ");
		sb.append("  gct.gcttb086_solicitacao_equipe se ");
		sb.append("  inner join gct.gcttb087_equipe e on e.nu_equipe = se.nu_equipe ");
		sb.append("  inner join gct.gcttb001_area_interna ai on ai.nu_area_interna = e.nu_area_interna ");
		sb.append("  inner join gct.gcttb010_contrato c on c.nu_contrato = e.nu_contrato ");
		sb.append("  inner join gct.gcttb005_solicitacao s on (se.nu_solicitacao = s.nu_solicitacao) ");
		sb.append("  inner join gct.gcttb055_servico_contrato sc on ( ");
		sb.append("    sc.nu_tipo_servico = se.nu_tipo_servico ");
		sb.append("    and e.nu_contrato = sc.nu_contrato ");
		sb.append("    and e.nu_tipo_grupo = sc.nu_tipo_grupo ");
		sb.append("  ) ");
		sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = ? ");
		sb.append("where ");
		sb.append("  se.nu_equipe = ? ");
		sb.append("  and sc.ic_servico_compoe_calculo_fns = 'S' ");
		sb.append("  and coalesce(se.co_falha_solicitacao, 'NF') = 'NF' ");
		sb.append("  and s.dh_efetiva is not null ");
		sb.append("  and s.dh_efetiva::date between f.dt_inicio_fatura and f.dt_final_fatura ");

		return sb.toString();
	}

	private String getDMLServicosEntreguesComAtraso() {

		StringBuilder sb = new StringBuilder();

		sb.append("select ");
		sb.append("  s.co_solicitacao, ");
		sb.append("  s.no_solicitacao, ");
		sb.append("  s.dh_prevista, ");
		sb.append("  gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, ");
		sb.append("    date(s.dh_prevista)) dh_prevista_pendencias, ");
		sb.append("  s.dh_efetiva, ");
		sb.append("  sc.ic_servico_compoe_calculo_fns, ");
		sb.append("  coalesce(se.co_falha_solicitacao, 'NF') as co_falha_solicitacao, ") ; 
		sb.append("  s.nu_situacao_solicitacao, "); 
		sb.append("  s.de_situacao_solicitacao "); 
		sb.append(" from");
		sb.append("     gct.gcttb086_solicitacao_equipe se");
		sb.append("     inner join gct.gcttb087_equipe e on e.nu_equipe = se.nu_equipe");
		sb.append("     inner join gct.gcttb001_area_interna ai on ai.nu_area_interna = e.nu_area_interna");
		sb.append("     inner join gct.gcttb010_contrato c on c.nu_contrato = e.nu_contrato");
		sb.append("     inner join gct.gcttb005_solicitacao s on (se.nu_solicitacao = s.nu_solicitacao)");
		sb.append("     inner join gct.gcttb055_servico_contrato sc on (");
		sb.append("         sc.nu_tipo_servico = se.nu_tipo_servico");
		sb.append("         and e.nu_contrato = sc.nu_contrato");
		sb.append("         and e.nu_tipo_grupo = sc.nu_tipo_grupo ");
		sb.append("     )");
		sb.append("     inner join gct.gcttb102_fatura f on f.nu_faturamento = c.nu_faturamento");
		sb.append("     and f.co_sitio = ai.co_sitio");
		sb.append("     and f.nu_fatura = ?");
		sb.append(" where");
		sb.append("     se.nu_equipe = ?");
		sb.append("     and sc.ic_servico_compoe_calculo_fns = 'S'");
		sb.append("     and coalesce(se.co_falha_solicitacao, 'NF') = 'NF' ");
		sb.append("     and s.dh_efetiva is not null");
		sb.append("     and date(s.dh_efetiva) between f.dt_inicio_fatura and f.dt_final_fatura");
		sb.append("     and date(s.dh_efetiva) > gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista))");

		return sb.toString();
	}

	private String getDMLServicosDevolvidos() {

		StringBuilder sb = new StringBuilder();

		sb.append("select ");
		sb.append("  s.co_solicitacao, ");
		sb.append("  s.no_solicitacao, ");
		sb.append("  s.dh_prevista, ");
		sb.append("  gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, ");
		sb.append("    date(s.dh_prevista)) dh_prevista_pendencias, ");
		sb.append("  s.dh_efetiva, ");
		sb.append("  sc.ic_servico_compoe_calculo_fns, ");
		sb.append("  coalesce(se.co_falha_solicitacao, 'NF') as co_falha_solicitacao, ") ; 
		sb.append("  s.nu_situacao_solicitacao, "); 
		sb.append("  s.de_situacao_solicitacao "); 
		sb.append("from ");
		sb.append("  gct.gcttb086_solicitacao_equipe se ");
		sb.append("  inner join gct.gcttb087_equipe e on e.nu_equipe = se.nu_equipe ");
		sb.append("  inner join gct.gcttb001_area_interna ai on ai.nu_area_interna = e.nu_area_interna ");
		sb.append("  inner join gct.gcttb010_contrato c on c.nu_contrato = e.nu_contrato ");
        sb.append("     inner join gct.gcttb102_fatura f on f.nu_fatura = ?");
		sb.append("  inner join gct.gcttb005_solicitacao s on (se.nu_solicitacao = s.nu_solicitacao) ");
		sb.append("  inner join gct.gcttb055_servico_contrato sc on ( ");
		sb.append("    sc.nu_tipo_servico = se.nu_tipo_servico ");
		sb.append("    and e.nu_contrato = sc.nu_contrato ");
		sb.append("    and e.nu_tipo_grupo = sc.nu_tipo_grupo ");
		sb.append("  ) ");
		sb.append("  inner join gct.gcttb226_devolucao_solicitacao ds on ( ");
		sb.append("    ds.nu_solicitacao = s.nu_solicitacao ");
		sb.append("  ) ");
		sb.append("where ");
		sb.append("  se.nu_equipe = ? ");
		sb.append("  and sc.ic_servico_compoe_calculo_fns = 'S' ");
		sb.append("  and coalesce(se.co_falha_solicitacao, 'NF') = 'NF' ");
		sb.append("  and ( ");
		sb.append("    f.dt_inicio_fatura <= ds.dh_modificacao ");
		sb.append("    and f.dt_final_fatura >= ds.dh_modificacao ");
		sb.append("  ) ");

		return sb.toString();
	}

	private String getDMLServicosEstoque() {

		StringBuilder sb = new StringBuilder();

		sb.append("select ");
		sb.append("  s.co_solicitacao, ");
		sb.append("  s.no_solicitacao, ");
		sb.append("  s.dh_prevista, ");
		sb.append("  gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, ");
		sb.append("    date(s.dh_prevista)) dh_prevista_pendencias, ");
		sb.append("  s.dh_efetiva, ");
		sb.append("  sc.ic_servico_compoe_calculo_fns, ");
		sb.append("  coalesce(se.co_falha_solicitacao, 'NF') as co_falha_solicitacao, ") ; 
		sb.append("  s.nu_situacao_solicitacao, "); 
		sb.append("  s.de_situacao_solicitacao "); 
		sb.append("from ");
		sb.append("  gct.gcttb086_solicitacao_equipe se ");
		sb.append("  inner join gct.gcttb087_equipe e on e.nu_equipe = se.nu_equipe ");
		sb.append("  inner join gct.gcttb001_area_interna ai on ai.nu_area_interna = e.nu_area_interna ");
		sb.append("  inner join gct.gcttb010_contrato c on c.nu_contrato = e.nu_contrato ");
		sb.append("  inner join gct.gcttb005_solicitacao s on (se.nu_solicitacao = s.nu_solicitacao) ");
		sb.append("  inner join gct.gcttb055_servico_contrato sc on ( ");
		sb.append("    sc.nu_tipo_servico = se.nu_tipo_servico ");
		sb.append("    and e.nu_contrato = sc.nu_contrato ");
		sb.append("    and e.nu_tipo_grupo = sc.nu_tipo_grupo ");
		sb.append("  ) ");
        sb.append("     inner join gct.gcttb102_fatura f on f.nu_fatura = ?");
		sb.append("where ");
		sb.append("  se.nu_equipe = ? ");
		sb.append("  and sc.ic_servico_compoe_calculo_fns = 'S' ");
		sb.append("  and coalesce(se.co_falha_solicitacao, 'NF') = 'NF' ");
		sb.append("  and s.dh_efetiva is not null ");
		sb.append("  and ( ");
		sb.append("    f.dt_inicio_fatura <= gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) ");
		sb.append("    and f.dt_final_fatura >= gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) ");
		sb.append("  ) ");


		return sb.toString();
	}
}
