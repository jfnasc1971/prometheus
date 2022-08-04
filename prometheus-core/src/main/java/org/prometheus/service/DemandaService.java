package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.prometheus.dto.DemandaDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DemandaService {

	@Autowired
	private DatabaseService databaseService;

	public DemandaDTO listar(String coDemanda) {

		DemandaDTO dto = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setString(1, coDemanda);

			rs = pstmt.executeQuery();

			if (rs.next()) {

				dto = new DemandaDTO();

				dto.setNuSistema(rs.getLong("nu_sistema"));
				dto.setSgSistema(rs.getString("sg_sistema"));
				dto.setNoSistema(rs.getString("no_sistema"));
				dto.setNuDemanda(rs.getLong("nu_demanda"));
				dto.setCoDemanda(rs.getString("co_demanda"));
				dto.setNuAreaContratante(rs.getLong("nu_area_contratante"));
				dto.setNoSiglaContratante(rs.getString("no_sigla_contratante"));
				dto.setNoAreaContratante(rs.getString("no_area_contratante"));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return dto;
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
		sb.append("   ai.no_area_interna no_area_contratante");
		sb.append(" from");
		sb.append("   gct.gcttb021_demanda d");
		sb.append("   inner join gct.gcttb039_sistema si on si.nu_sistema = d.nu_sistema");
		sb.append("   inner join gct.gcttb001_area_interna ai on ai.nu_area_interna = d.nu_area_contratante");
		sb.append(" where");
		sb.append("   d.co_demanda = ?");

		return sb.toString();
	}

}
