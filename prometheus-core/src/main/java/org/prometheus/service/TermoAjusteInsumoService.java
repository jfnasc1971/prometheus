package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.TermoAjusteInsumoDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TermoAjusteInsumoService {

	@Autowired
	private DatabaseService databaseService;

	public List<TermoAjusteInsumoDTO> listar(String coEquipe, Long nuTermo) {
		List<TermoAjusteInsumoDTO> result = new ArrayList<TermoAjusteInsumoDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();
			
			pstmt = conn.prepareStatement(getDML());
			pstmt.setString(1, coEquipe);
			pstmt.setLong(2, nuTermo);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				TermoAjusteInsumoDTO dto = new TermoAjusteInsumoDTO();

				dto.setNuEquipe(rs.getLong("nu_equipe"));
				dto.setNuAjuste(rs.getLong("nu_ajuste"));
				dto.setNuTermoEquipe(rs.getLong("nu_termo_equipe"));
				dto.setCoTermo(rs.getString("co_termo"));
				dto.setDhEmissaoTermo(rs.getTimestamp("dh_emissao_termo"));
				dto.setDeFatura(rs.getString("de_fatura"));
				dto.setDtFinalFatura(rs.getDate("dt_final_fatura"));
				dto.setVrFinanceiroTermo(rs.getBigDecimal("vr_financeiro_termo"));
				dto.setVrAjusteAnterior(rs.getBigDecimal("vr_ajuste_anterior"));
				dto.setVrFinanceiroGerado(rs.getBigDecimal("vr_financeiro_gerado"));

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
		sb.append("   tae.nu_equipe,");
		sb.append("   tae.nu_ajuste,");
		sb.append("   tae.nu_termo_equipe,");
		sb.append("   teo.co_termo,");
		sb.append("   teo.dh_emissao_termo,");
		sb.append("   fteo.de_fatura,");
		sb.append("   fteo.dt_final_fatura,");
		sb.append("   teo.vr_financeiro_termo,");
		sb.append("   tae.vr_ajuste_anterior,");
		sb.append("   tag.vr_financeiro_termo as vr_financeiro_gerado");
		sb.append(" FROM");
		sb.append("   gct.gcttb087_equipe e");
		sb.append("   INNER JOIN gct.gcttb186_termo_ajuste_equipe tae ON tae.nu_equipe = e.nu_equipe");
		sb.append("   INNER JOIN gct.gcttb170_termo_equipe teo ON teo.nu_termo_equipe = tae.nu_termo_equipe_original");
		sb.append("   INNER JOIN gct.gcttb170_termo_equipe tag ON tag.nu_termo_equipe = tae.nu_termo_ajuste_gerado");
		sb.append("   INNER JOIN gct.gcttb102_fatura fteo ON fteo.nu_fatura = teo.nu_fatura_gerada");
		sb.append(" WHERE");
		sb.append("   e.co_equipe = ?");
		sb.append("   AND tae.nu_termo_equipe = ?");
		sb.append(" ORDER BY");
		sb.append("   fteo.dt_final_fatura");

		return sb.toString();
	}

}
