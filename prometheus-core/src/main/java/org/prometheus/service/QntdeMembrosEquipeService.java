package org.prometheus.service;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.prometheus.dto.QntdeMembrosEquipeDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QntdeMembrosEquipeService {

	@Autowired
	private DatabaseService databaseService;

	public List<QntdeMembrosEquipeDTO> listar(String coEquipe) {

		List<QntdeMembrosEquipeDTO> result = new ArrayList<QntdeMembrosEquipeDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();
			
			pstmt = conn.prepareStatement(getDML());
			pstmt.setString(1, coEquipe);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				QntdeMembrosEquipeDTO dto = new QntdeMembrosEquipeDTO();

				dto.setNuEquipe(rs.getLong("nu_equipe"));
				dto.setDtInicioVigenciaMembros(rs.getDate("dt_inicio_vigencia_membros"));
				dto.setDtFinalVigenciaMembros(rs.getDate("dt_final_vigencia_membros"));
				dto.setQtMembrosEquipe(rs.getBigDecimal("qt_membros_equipe"));
				dto.setNuAcaoHistorico(rs.getLong("nu_acao_historico"));
				dto.setNuMtrcaAcaoHstro(rs.getLong("nu_mtrca_acao_hstro"));
				dto.setDhInclusaoInformacao(rs.getTimestamp("dh_inclusao_informacao"));
				dto.setDeJustificativaAlteracao(rs.getString("de_justificativa_alteracao"));
				dto.setQtIntervalos(rs.getLong("qt_intervalos"));

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

		sb.append("SELECT "); 
		sb.append("  q.*, "); 
		sb.append("  ( "); 
		sb.append("    SELECT "); 
		sb.append("      count(*) "); 
		sb.append("    FROM "); 
		sb.append("      gct.gcttb087_equipe e1 "); 
		sb.append("      INNER JOIN gct.gcttb097_qntde_membros_equipe q1 ON q1.nu_equipe = e1.nu_equipe "); 
		sb.append("    WHERE "); 
		sb.append("      e1.co_equipe = e.co_equipe "); 
		sb.append("      and ( "); 
		sb.append("        q.dt_inicio_vigencia_membros between q1.dt_inicio_vigencia_membros and q1.dt_final_vigencia_membros "); 
		sb.append("        or "); 
		sb.append("        q.dt_final_vigencia_membros between q1.dt_inicio_vigencia_membros and q1.dt_final_vigencia_membros "); 
		sb.append("      ) "); 
		sb.append("  ) qt_intervalos "); 
		sb.append("FROM "); 
		sb.append("  gct.gcttb087_equipe e "); 
		sb.append("  INNER JOIN gct.gcttb097_qntde_membros_equipe q ON q.nu_equipe = e.nu_equipe "); 
		sb.append("WHERE "); 
		sb.append("  e.co_equipe = ? "); 
		sb.append("ORDER BY "); 
		sb.append("  q.dt_inicio_vigencia_membros ");

		return sb.toString();
	}

}
