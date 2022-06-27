package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.SolicitacaoDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SolicitacaoService {

	@Autowired
	private DatabaseService databaseService;

	public List<SolicitacaoDTO> listar(Long nuEquipe, Long nuCicloTrabalho) {
		List<SolicitacaoDTO> result = new ArrayList<SolicitacaoDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setLong(1, nuEquipe);
			pstmt.setLong(2, nuCicloTrabalho);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				SolicitacaoDTO dto = new SolicitacaoDTO();

				dto.setNuEquipe(rs.getLong("nu_equipe"));
				dto.setNuCicloTrabalho(rs.getLong("nu_ciclo_trabalho"));
				dto.setCoSolicitacao(rs.getString("co_solicitacao"));
				dto.setDeTipoSolicitacao(rs.getString("de_tipo_solicitacao"));
				dto.setNoTipoServico(rs.getString("no_tipo_servico"));
				dto.setDhPrevista(rs.getTimestamp("dh_prevista"));
				dto.setDhEfetiva(rs.getTimestamp("dh_efetiva"));
				dto.setDhResolucao(rs.getTimestamp("dh_resolucao"));
				dto.setNuTamanhoServico(rs.getBigDecimal("nu_tamanho_servico"));

				result.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return result;
	}

	public String getDML() {

		StringBuilder sb = new StringBuilder();

		sb.append("select "); 
		sb.append("  e.nu_equipe, "); 
		sb.append("  cct.nu_ciclo_trabalho, "); 
		sb.append("  s.co_solicitacao, "); 
		sb.append("  s.de_tipo_solicitacao, "); 
		sb.append("  ts.no_tipo_servico, "); 
		sb.append("  s.dh_prevista, "); 
		sb.append("  s.dh_efetiva, "); 
		sb.append("  s.dh_resolucao, "); 
		sb.append("  se.nu_tamanho_servico "); 
		sb.append("from "); 
		sb.append("  gct.gcttb087_equipe e "); 
		sb.append("  inner join gct.gcttb088_ciclo_trabalho cct on cct.nu_equipe = e.nu_equipe "); 
		sb.append("  inner join gct.gcttb086_solicitacao_equipe se on ( "); 
		sb.append("    se.nu_ciclo_trabalho = cct.nu_ciclo_trabalho "); 
		sb.append("    and se.nu_equipe = e.nu_equipe "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb005_solicitacao s on s.nu_solicitacao = se.nu_solicitacao "); 
		sb.append("  inner join gct.gcttb015_tipo_servico ts on ts.nu_tipo_servico = se.nu_tipo_servico "); 
		sb.append("  inner join gct.gcttb055_servico_contrato sc on ( "); 
		sb.append("    sc.nu_tipo_servico = se.nu_tipo_servico "); 
		sb.append("    and e.nu_contrato = sc.nu_contrato "); 
		sb.append("  ) "); 
		sb.append("where "); 
		sb.append("  e.nu_equipe = ? "); 
		sb.append("  and cct.nu_ciclo_trabalho = ? "); 
		sb.append("  and cct.nu_situacao_ciclo_trabalho = 2 "); 
		sb.append("  and cct.co_falha_ciclo_trabalho = 'NF' "); 
		sb.append("  and sc.ic_servico_compoe_calculo_fns = 'S' ");

		return sb.toString();
	}

}
