package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.prometheus.dto.SolicitacaoDemandaDTO;

import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;

@Component
public class SolicitacaoDemandaService {

	@Autowired
	private DatabaseService databaseService;

	public List<SolicitacaoDemandaDTO> listar(String coDemanda) {
		List<SolicitacaoDemandaDTO> result = new ArrayList<SolicitacaoDemandaDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setString(1, coDemanda);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				SolicitacaoDemandaDTO dto = new SolicitacaoDemandaDTO();

				dto.setNuSistema(rs.getLong("nu_sistema"));
				dto.setSgSistema(rs.getString("sg_sistema"));
				dto.setNoSistema(rs.getString("no_sistema"));
				dto.setNuDemanda(rs.getLong("nu_demanda"));
				dto.setCoDemanda(rs.getString("co_demanda"));
				dto.setNuAreaContratante(rs.getLong("nu_area_contratante"));
				dto.setNoSiglaContratante(rs.getString("no_sigla_contratante"));
				dto.setNoAreaContratante(rs.getString("no_area_contratante"));
				dto.setNuSolicitacao(rs.getLong("nu_solicitacao"));
				dto.setCoSolicitacao(rs.getString("co_solicitacao"));
				dto.setNoSolicitacao(rs.getString("no_solicitacao"));
				dto.setDeSolicitacao(rs.getString("de_solicitacao"));
				dto.setDeSituacaoSolicitacao(rs.getString("de_situacao_solicitacao"));

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

		sb.append(" select");
		sb.append("   si.nu_sistema,");
		sb.append("   si.sg_sistema,");
		sb.append("   si.no_sistema,");
		sb.append("   d.nu_demanda,");
		sb.append("   d.co_demanda,");
		sb.append("   d.nu_area_contratante,");
		sb.append("   ai.no_sigla_area no_sigla_contratante,");
		sb.append("   ai.no_area_interna no_area_contratante,");
		sb.append("   s.nu_solicitacao,");
		sb.append("   s.co_solicitacao,");
		sb.append("   s.no_solicitacao,");
		sb.append("   s.de_solicitacao,");
		sb.append("   s.de_situacao_solicitacao");
		sb.append(" from");
		sb.append("   gct.gcttb021_demanda d");
		sb.append("   inner join gct.gcttb039_sistema si on si.nu_sistema = d.nu_sistema");
		sb.append("   inner join gct.gcttb001_area_interna ai on ai.nu_area_interna = d.nu_area_contratante");
		sb.append("   inner join gct.gcttb022_solicitacao_demanda sd on sd.nu_demanda = d.nu_demanda");
		sb.append("   inner join gct.gcttb005_solicitacao s on s.nu_solicitacao = sd.nu_solicitacao");
		sb.append(" where");
		sb.append("   d.co_demanda = ?");

		return sb.toString();
	}

}
