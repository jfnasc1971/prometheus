package org.prometheus.service;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.prometheus.dto.TermoAjusteDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TermoAjusteService {

	@Autowired
	private DatabaseService databaseService;

	public List<TermoAjusteDTO> listar(String coEquipe) {
		List<TermoAjusteDTO> result = new ArrayList<TermoAjusteDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();
			
			pstmt = conn.prepareStatement(getDML());
			pstmt.setString(1, coEquipe);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				TermoAjusteDTO dto = new TermoAjusteDTO();

				dto.setNuEquipe(rs.getLong("nu_equipe"));
				dto.setNuTermoEquipe(rs.getLong("nu_termo_equipe"));
				dto.setCoTermo(rs.getString("co_termo"));
				dto.setDtFinalFatura(rs.getDate("dt_final_fatura"));
				dto.setDeFatura(rs.getString("de_fatura"));
				dto.setVrFinanceiroTermo(rs.getBigDecimal("vr_financeiro_termo"));

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

		sb.append(" SELECT");
		sb.append("   e.nu_equipe,");
		sb.append("   taj.nu_termo_equipe,");
		sb.append("   taj.co_termo,");
		sb.append("   f.dt_final_fatura,");
		sb.append("   f.de_fatura,");
		sb.append("   taj.vr_financeiro_termo");
		sb.append(" FROM");
		sb.append("   gct.gcttb087_equipe e");
		sb.append("   INNER JOIN gct.gcttb170_termo_equipe taj ON taj.nu_equipe = e.nu_equipe");
		sb.append("   INNER JOIN gct.gcttb102_fatura f ON f.nu_fatura = taj.nu_fatura_gerada");
		sb.append(" WHERE");
		sb.append("   e.co_equipe = ?");
		sb.append("   AND taj.nu_tipo_termo = 12");
		sb.append(" ORDER BY");
		sb.append("   taj.nu_termo_equipe");

		return sb.toString();
	}

}
