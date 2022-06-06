package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.DetalheFaturamentoDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DetalheFaturamentoService {

	@Autowired
	private DatabaseService databaseService;

	public List<DetalheFaturamentoDTO> listar(String coEquipe, Long nuFatura) {
		return listar(coEquipe, nuFatura, false);
	}

	public List<DetalheFaturamentoDTO> listar(String coEquipe, Long nuFatura, boolean isPrecoVigente) {
		List<DetalheFaturamentoDTO> result = new ArrayList<DetalheFaturamentoDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setString(1, coEquipe);
			pstmt.setLong(2, nuFatura);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				DetalheFaturamentoDTO dto = new DetalheFaturamentoDTO();

				dto.setNoPerfil(rs.getString("no_perfil"));
				dto.setDtInicioVigenciaMembros(rs.getDate("dt_inicio_vigencia_membros"));
				dto.setDtFinalVigenciaMembros(rs.getDate("dt_final_vigencia_membros"));
				dto.setQtDiasTrab(rs.getBigDecimal("qt_dias_trab"));
				dto.setQtDiasFat(rs.getBigDecimal("qt_dias_fat"));
				dto.setQtMembros(rs.getBigDecimal("qt_membros"));
				dto.setVrPrecoGrupo(rs.getBigDecimal("vr_preco_grupo"));

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
		sb.append("   cast('N/A' as varchar) as no_perfil, ");
		sb.append("   dt_inicio_vigencia_membros, ");
		sb.append("   dt_final_vigencia_membros, ");
		sb.append("   qt_dias_trab, ");
		sb.append("   qt_dias_fat, ");
		sb.append("   qt_membros, ");
		sb.append("   vr_preco_grupo ");
		sb.append(" FROM ");
		sb.append("   ( ");
		sb.append("     SELECT ");
		sb.append("       dt_inicio_fatura, ");
		sb.append("       dt_final_fatura, ");
		sb.append("       dt_inicio_vigencia_membros, ");
		sb.append("       dt_final_vigencia_membros, ");
		sb.append("       gct.gctfn305_dias_entre_data( ");
		sb.append("         dt_inicio_vigencia_membros, ");
		sb.append("         dt_final_vigencia_membros, ");
		sb.append("         co_sitio, ");
		sb.append("         ic_dias_calculo_pagamento ");
		sb.append("       ) AS qt_dias_trab, ");
		sb.append("       gct.gctfn305_dias_entre_data( ");
		sb.append("         dt_inicio_fatura, ");
		sb.append("         dt_final_fatura, ");
		sb.append("         co_sitio, ");
		sb.append("         ic_dias_calculo_pagamento ");
		sb.append("       ) AS qt_dias_fat, ");
		sb.append("       qt_membros_equipe AS qt_membros, ");
		sb.append("       vr_financeiro_termo_original, ");
		sb.append("       vr_financeiro_termo, ");
		sb.append("       vr_preco_grupo ");
		sb.append("     FROM ");
		sb.append("       ( ");
		sb.append("         SELECT ");
		sb.append("           c.nu_contrato, ");
		sb.append("           c.co_contrato, ");
		sb.append("           c.no_contrato, ");
		sb.append("           e.nu_equipe, ");
		sb.append("           e.co_equipe, ");
		sb.append("           t.nu_termo_equipe, ");
		sb.append("           t.co_termo, ");
		sb.append("           f.de_fatura, ");
		sb.append("           f.dt_inicio_fatura, ");
		sb.append("           f.dt_final_fatura, ");
		sb.append("           f.nu_fatura, ");
		sb.append("           CASE ");
		sb.append("             WHEN q.dt_inicio_vigencia_membros < f.dt_inicio_fatura THEN f.dt_inicio_fatura ");
		sb.append("             ELSE q.dt_inicio_vigencia_membros ");
		sb.append("           END AS dt_inicio_vigencia_membros, ");
		sb.append("           CASE ");
		sb.append("             WHEN coalesce( ");
		sb.append("               q.dt_final_vigencia_membros, ");
		sb.append("               coalesce(e.dt_final_atendimento,  c.dt_final_compromisso) ");
		sb.append("             ) > f.dt_final_fatura THEN f.dt_final_fatura ");
		sb.append("             ELSE q.dt_final_vigencia_membros ");
		sb.append("           END AS dt_final_vigencia_membros, ");
		sb.append("           q.qt_membros_equipe, ");
		sb.append("           e.co_sitio, ");
		sb.append("           gc.ic_dias_calculo_pagamento, ");
		sb.append("           t.vr_financeiro_termo_original, ");
		sb.append("           t.vr_financeiro_termo, ");
		sb.append("           t.vr_preco_grupo ");
		sb.append("         FROM ");
		sb.append("           gct.gcttb087_equipe e ");
		sb.append("           INNER JOIN gct.gcttb170_termo_equipe t ON t.nu_equipe = e.nu_equipe ");
		sb.append("           INNER JOIN gct.gcttb102_fatura f ON f.nu_fatura = t.nu_fatura_gerada ");
		sb.append("           INNER JOIN gct.gcttb010_contrato c ON c.nu_contrato = e.nu_contrato ");
		sb.append("           INNER JOIN gct.gcttb033_grupo_contrato gc ON ( ");
		sb.append("             gc.nu_contrato = e.nu_contrato ");
		sb.append("             AND gc.nu_tipo_grupo = e.nu_tipo_grupo ");
		sb.append("           ) ");
		sb.append("           INNER JOIN gct.gcttb097_qntde_membros_equipe q ON q.nu_equipe = e.nu_equipe ");
		sb.append("         WHERE ");
		sb.append("           e.co_equipe = ? ");
		sb.append("           AND t.nu_tipo_termo = 11 ");
		sb.append("           AND q.dt_inicio_vigencia_membros <= f.dt_final_fatura ");
		sb.append("           AND coalesce( ");
		sb.append("             q.dt_final_vigencia_membros, ");
		sb.append("             c.dt_final_compromisso ");
		sb.append("           ) >= f.dt_inicio_fatura ");
		sb.append("           AND f.nu_fatura = ? ");
		sb.append("       ) tb ");
		sb.append("   ) tb ");
		sb.append(" order by dt_inicio_vigencia_membros ");

		return sb.toString();
	}

}
