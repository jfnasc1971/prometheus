package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.FaixaIndicadorDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FaixaIndicadorService {

	@Autowired
	private DatabaseService databaseService;

	public List<FaixaIndicadorDTO> listar(Long nuEquipe) {
		List<FaixaIndicadorDTO> result = new ArrayList<FaixaIndicadorDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setLong(1, nuEquipe);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				FaixaIndicadorDTO dto = new FaixaIndicadorDTO();

				dto.setSgTipoIndicador(rs.getString("sg_tipo_indicador"));
				dto.setNoTipoIndicador(rs.getString("no_tipo_indicador"));
				dto.setNuTipoFaixa(rs.getLong("nu_tipo_faixa"));
				dto.setQtInicioFaixa(rs.getBigDecimal("qt_inicio_faixa"));
				dto.setQtFinalFaixa(rs.getBigDecimal("qt_final_faixa"));
				dto.setVrFatorFaixa(rs.getBigDecimal("vr_fator_faixa"));
				dto.setNuAcaoHistorico(rs.getLong("nu_acao_historico"));
				dto.setNuMtrcaAcaoHstro(rs.getLong("nu_mtrca_acao_hstro"));

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
		sb.append("   ti.sg_tipo_indicador,");
		sb.append("   ti.no_tipo_indicador,");
		sb.append("   tb057.nu_tipo_faixa,");
		sb.append("   tb057.qt_inicio_faixa,");
		sb.append("   tb057.qt_final_faixa,");
		sb.append("   tb057.vr_fator_faixa,");
		sb.append("   tb057.nu_acao_historico,");
		sb.append("   tb057.nu_mtrca_acao_hstro");
		sb.append(" from");
		sb.append("   gct.gcttb131_tipo_faixa_indicador tb131");
		sb.append("   inner join gct.gcttb057_valor_faixa tb057 on tb131.nu_tipo_faixa = tb057.nu_tipo_faixa");
		sb.append("   inner join gct.gcttb115_grupo_indicador tb115 on (");
		sb.append("     tb115.nu_contrato = tb131.nu_contrato");
		sb.append("     and tb115.nu_tipo_grupo = tb131.nu_tipo_grupo");
		sb.append("     and tb115.nu_tipo_indicador = tb131.nu_tipo_indicador");
		sb.append("   )");
		sb.append("   inner join gct.gcttb010_contrato c on c.nu_contrato = tb131.nu_contrato");
		sb.append("   inner join gct.gcttb087_equipe e on e.nu_contrato = c.nu_contrato");
		sb.append("   inner join gct.gcttb112_tipo_indicador ti on ti.nu_tipo_indicador = tb115.nu_tipo_indicador");
		sb.append(" where");
		sb.append("   e.nu_equipe = ?");
		sb.append("   and tb115.nu_tipo_grupo = e.nu_tipo_grupo");
		sb.append("   and tb115.ic_aplica_indicador = 'S'");
		sb.append(" order by tb057.qt_inicio_faixa");

		return sb.toString();
	}

}
