package org.prometheus.indicador.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.prometheus.indicador.ParametrosIndicador;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndicadorIEQRDASprintService {

	@Autowired
	private DatabaseService databaseService;

	public ParametrosIndicador recuperar(Long nuEquipe, Long nuFatura, Long nuCicloTrabalho) {

		ParametrosIndicador result = null;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			StringBuilder sb = new StringBuilder();

			sb.append("SELECT "); 
			sb.append("  a.nu_equipe, "); 
			sb.append("  a.nu_ciclo_trabalho, "); 
			sb.append("  sum(a.vr_avaliacao) as vr_avaliacoes, "); 
			sb.append("  count(a.nu_mtrca_avaliador) qt_profissionais, "); 
			sb.append("  a.qt_perguntas_respondidas, "); 
			sb.append("  gi.vr_fator_ajuste "); 
			sb.append("FROM "); 
			sb.append("  gct.gcttb199_avlco_atndmnto_equipe a "); 
			sb.append("  inner join gct.gcttb087_equipe e on e.nu_equipe = a.nu_equipe "); 
			sb.append("  inner join gct.gcttb115_grupo_indicador gi on ( "); 
			sb.append("    gi.nu_contrato = e.nu_contrato "); 
			sb.append("    and gi.nu_tipo_grupo = e.nu_tipo_grupo "); 
			sb.append("    and gi.nu_tipo_indicador = a.nu_tipo_indicador "); 
			sb.append("  ) "); 
			sb.append("where "); 
			sb.append("  a.nu_equipe = ? "); 
			sb.append("  and a.nu_tipo_indicador = ? "); 
			sb.append("  and a.nu_ciclo_trabalho = ? "); 
			sb.append("group by "); 
			sb.append("  a.nu_equipe, "); 
			sb.append("  a.nu_ciclo_trabalho, "); 
			sb.append("  a.qt_perguntas_respondidas, "); 
			sb.append("  gi.vr_fator_ajuste ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setLong(1, nuEquipe);
			pstmt.setLong(2, 31L);
			pstmt.setLong(3, nuCicloTrabalho);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = new ParametrosIndicador();
				result.setQtQuestoesRespondidas(rs.getBigDecimal("qt_perguntas_respondidas"));
				result.setVrFatorAjuste(rs.getBigDecimal("vr_fator_ajuste"));
				result.setVrAvaliacaoSprint(rs.getBigDecimal("vr_avaliacoes"));
				result.setQtProfissionaisAssistido(rs.getBigDecimal("qt_profissionais"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return result;
	}
}
