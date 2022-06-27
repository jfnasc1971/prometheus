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
			pstmt.setString(2, coEquipe);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				QntdeMembrosEquipeDTO dto = new QntdeMembrosEquipeDTO();

				dto.setNuEquipe(rs.getLong("nu_equipe"));
				dto.setDtInicioVigenciaMembros(rs.getDate("dt_inicio_vigencia_membros"));
				dto.setDtFinalVigenciaMembros(rs.getDate("dt_final_vigencia_membros"));
				dto.setQtMembrosEquipe(rs.getBigDecimal("qt_membros_equipe"));
				dto.setDePerfil(rs.getString("de_perfil"));

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
		sb.append("  null :: text as de_perfil, "); 
		sb.append("  q.nu_equipe, "); 
		sb.append("  q.dt_inicio_vigencia_membros, "); 
		sb.append("  q.dt_final_vigencia_membros, "); 
		sb.append("  null :: numeric as nu_perfil, "); 
		sb.append("  q.qt_membros_equipe "); 
		sb.append("FROM "); 
		sb.append("  gct.gcttb087_equipe e "); 
		sb.append("  inner join gct.gcttb001_area_interna ai on ai.nu_area_interna = e.nu_area_interna "); 
		sb.append("  INNER JOIN gct.gcttb097_qntde_membros_equipe q ON q.nu_equipe = e.nu_equipe "); 
		sb.append("WHERE "); 
		sb.append("  e.co_equipe = ? "); 
		sb.append("union "); 
		sb.append("all "); 
		sb.append("SELECT "); 
		sb.append("  pp.de_perfil, "); 
		sb.append("  q.nu_equipe, "); 
		sb.append("  q.dt_inicio_vigencia as dt_inicio_vigencia_membros, "); 
		sb.append("  q.dt_final_vigencia as dt_final_vigencia_membros, "); 
		sb.append("  q.nu_perfil, "); 
		sb.append("  sum(q.qt_membros) as qt_membros_equipe "); 
		sb.append("FROM "); 
		sb.append("  gct.gcttb087_equipe e "); 
		sb.append("  INNER JOIN gct.gcttb523_qntde_membro_equipe_perfil q ON q.nu_equipe = e.nu_equipe "); 
		sb.append("  inner join gct.gcttb521_perfil_profissional pp on pp.nu_perfil = q.nu_perfil "); 
		sb.append("WHERE "); 
		sb.append("  e.co_equipe = ? "); 
		sb.append("group by "); 
		sb.append("  pp.de_perfil, "); 
		sb.append("  q.nu_equipe, "); 
		sb.append("  q.dt_inicio_vigencia, "); 
		sb.append("  q.dt_final_vigencia, "); 
		sb.append("  q.nu_perfil "); 
		sb.append("ORDER BY "); 
		sb.append("  de_perfil, "); 
		sb.append("  dt_inicio_vigencia_membros");

		return sb.toString();
	}

}
