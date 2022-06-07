package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.PerfilDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PerfilService {

	@Autowired
	private DatabaseService databaseService;

	public List<PerfilDTO> listar(Long nuContrato) {
		List<PerfilDTO> result = new ArrayList<PerfilDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());

			pstmt.setLong(1, nuContrato);
			
			rs = pstmt.executeQuery();

			while (rs.next()) {

				PerfilDTO dto = new PerfilDTO();

				dto.setNuPerfil(rs.getLong("nu_perfil"));
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

		sb.append(" select distinct ");
		sb.append("   pp.nu_perfil,");
		sb.append("   pp.de_perfil");
		sb.append(" from");
		sb.append("   gct.gcttb522_dstrbco_profissional_perfil p");
		sb.append("   inner join gct.gcttb521_perfil_profissional pp on pp.nu_perfil = p.nu_perfil");
		sb.append(" where");
		sb.append("   nu_contrato = ?");

		return sb.toString();
	}

}
