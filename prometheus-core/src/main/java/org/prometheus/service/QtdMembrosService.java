package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.prometheus.dto.QtdMembrosDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QtdMembrosService {

	@Autowired
	private DatabaseService databaseService;

	public QtdMembrosDTO findBy(Long nuEquipe, Long nuFatura) {

		QtdMembrosDTO dto = null;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setLong(1, nuFatura);
			pstmt.setLong(2, nuEquipe);

			rs = pstmt.executeQuery();

			if (rs.next()) {

				dto = new QtdMembrosDTO();

				dto.setNoPerfil(rs.getString("no_perfil"));
				dto.setNuContrato(rs.getLong("nu_contrato"));
				dto.setNuEquipe(rs.getLong("nu_equipe"));
				dto.setNuFatura(rs.getLong("nu_fatura"));
				dto.setQtMembrosEfetiva(rs.getBigDecimal("qt_membros_efetiva"));

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

		sb.append("SELECT "); 
		sb.append("  no_perfil, "); 
		sb.append("  nu_contrato, "); 
		sb.append("  nu_fatura, "); 
		sb.append("  nu_equipe, "); 
		sb.append("  sum( "); 
		sb.append("    ( "); 
		sb.append("      gct.gctfn305_dias_entre_data( "); 
		sb.append("        dt_inicio_vigencia_membros, "); 
		sb.append("        dt_final_vigencia_membros, "); 
		sb.append("        co_sitio, "); 
		sb.append("        ic_dias_calculo_pagamento "); 
		sb.append("      ) / gct.gctfn305_dias_entre_data( "); 
		sb.append("        dt_inicio_fatura, "); 
		sb.append("        dt_final_fatura, "); 
		sb.append("        co_sitio, "); 
		sb.append("        ic_dias_calculo_pagamento "); 
		sb.append("      ) * qt_membros_equipe "); 
		sb.append("    ) "); 
		sb.append("  ) AS qt_membros_efetiva "); 
		sb.append("FROM "); 
		sb.append("  ( "); 
		sb.append("    SELECT "); 
		sb.append("      'N/A'::text AS no_perfil, "); 
		sb.append("      0 :: numeric AS nu_perfil, "); 
		sb.append("      e.nu_contrato, "); 
		sb.append("      e.nu_equipe, "); 
		sb.append("      f.nu_fatura, "); 
		sb.append("      f.dt_inicio_fatura, "); 
		sb.append("      f.dt_final_fatura, "); 
		sb.append("      e.co_sitio, "); 
		sb.append("      CASE "); 
		sb.append("        WHEN a.dt_inicio_vigencia_membros < f.dt_inicio_fatura THEN f.dt_inicio_fatura "); 
		sb.append("        ELSE a.dt_inicio_vigencia_membros "); 
		sb.append("      END dt_inicio_vigencia_membros, "); 
		sb.append("      CASE "); 
		sb.append("        WHEN coalesce(a.dt_final_vigencia_membros, f.dt_final_fatura) > f.dt_final_fatura THEN f.dt_final_fatura "); 
		sb.append("        ELSE coalesce(a.dt_final_vigencia_membros, f.dt_final_fatura) "); 
		sb.append("      END dt_final_vigencia_membros, "); 
		sb.append("      gc.ic_dias_calculo_pagamento, "); 
		sb.append("      a.qt_membros_equipe "); 
		sb.append("    FROM "); 
		sb.append("      gct.gcttb097_qntde_membros_equipe a "); 
		sb.append("      INNER JOIN gct.gcttb087_equipe e ON e.nu_equipe = a.nu_equipe "); 
		sb.append("      INNER JOIN gct.gcttb010_contrato c ON c.nu_contrato = e.nu_contrato "); 
		sb.append("      INNER JOIN gct.gcttb033_grupo_contrato gc ON ( "); 
		sb.append("        gc.nu_contrato = e.nu_contrato "); 
		sb.append("        AND gc.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      ) ");
		sb.append("      inner join gct.gcttb102_fatura f on f.nu_fatura = ? ");
		sb.append("  ) tb "); 
		sb.append("WHERE "); 
		sb.append("  1 = 1 "); 
		sb.append("  AND dt_inicio_vigencia_membros <= dt_final_fatura "); 
		sb.append("  AND dt_final_vigencia_membros >= dt_inicio_fatura "); 
		sb.append("  AND nu_equipe = ? "); 
		sb.append("group by "); 
		sb.append("  no_perfil, "); 
		sb.append("  nu_contrato, "); 
		sb.append("  nu_fatura, "); 
		sb.append("  nu_equipe ");

		return sb.toString();
	}

}
