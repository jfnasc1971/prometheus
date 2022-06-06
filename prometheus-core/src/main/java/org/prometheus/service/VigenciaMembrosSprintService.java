package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.VigenciaMembrosSprintDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VigenciaMembrosSprintService {

	@Autowired
	private DatabaseService databaseService;

	public List<VigenciaMembrosSprintDTO> listar(Long nuFatura, Long nuEquipe, Long nuCiclo) {
		List<VigenciaMembrosSprintDTO> result = new ArrayList<VigenciaMembrosSprintDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setLong(1, nuEquipe);
			pstmt.setLong(2, nuCiclo);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				VigenciaMembrosSprintDTO dto = new VigenciaMembrosSprintDTO();

				dto.setNuEquipe(rs.getLong("nu_equipe"));
				dto.setNuCicloTrabalho(rs.getLong("nu_ciclo_trabalho"));
				dto.setDtInicioVigenciaMembros(rs.getDate("dt_inicio_vigencia_membros"));
				dto.setDtFinalVigenciaMembros(rs.getDate("dt_final_vigencia_membros"));
				dto.setQtMembrosEquipe(rs.getBigDecimal("qt_membros_equipe"));
				dto.setQtDiasTrab(rs.getBigDecimal("qt_dias_trab"));
				dto.setQtDiasSprint(rs.getBigDecimal("qt_dias_sprint"));
				dto.setQtEfetiva(rs.getBigDecimal("qt_efetiva"));

				result.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return result;
	}

	public String getDML() {

		StringBuilder sb = new StringBuilder();

		sb.append("SELECT "); 
		sb.append("  *, "); 
		sb.append("  (qt_dias_trab / qt_dias_sprint) * qt_membros_equipe qt_efetiva "); 
		sb.append("FROM "); 
		sb.append("  ( "); 
		sb.append("    SELECT "); 
		sb.append("      ct.nu_equipe, "); 
		sb.append("      ct.nu_ciclo_trabalho, "); 
		sb.append("      q.dt_inicio_vigencia_membros, "); 
		sb.append("      q.dt_final_vigencia_membros, "); 
		sb.append("      q.qt_membros_equipe, "); 
		sb.append("      gct.gctfn305_dias_entre_data( "); 
		sb.append("        CASE "); 
		sb.append("          WHEN q.dt_inicio_vigencia_membros < cast(ct.dt_inicio_ciclo_trabalho as date) THEN "); 
		sb.append("            cast(ct.dt_inicio_ciclo_trabalho as date) "); 
		sb.append("          ELSE "); 
		sb.append("            q.dt_inicio_vigencia_membros "); 
		sb.append("        END, "); 
		sb.append("        CASE "); 
		sb.append("          WHEN coalesce(q.dt_final_vigencia_membros, c.dt_final_compromisso) > cast(ct.dt_alvo_ciclo_trabalho as date) "); 
		sb.append("            THEN cast(ct.dt_alvo_ciclo_trabalho as date) "); 
		sb.append("          ELSE "); 
		sb.append("            coalesce(q.dt_final_vigencia_membros, c.dt_final_compromisso) "); 
		sb.append("        END, "); 
		sb.append("        e.co_sitio, "); 
		sb.append("        gc.ic_dias_calculo_pagamento "); 
		sb.append("      ) qt_dias_trab, "); 
		sb.append("      gct.gctfn305_dias_entre_data( "); 
		sb.append("        ct.dt_inicio_ciclo_trabalho::date, "); 
		sb.append("        ct.dt_alvo_ciclo_trabalho::date, "); 
		sb.append("        e.co_sitio, "); 
		sb.append("        gc.ic_dias_calculo_pagamento "); 
		sb.append("      ) qt_dias_sprint, "); 
		sb.append("      ct.dt_inicio_ciclo_trabalho "); 
		sb.append("    FROM "); 
		sb.append("      gct.gcttb087_equipe e "); 
		sb.append("      INNER JOIN gct.gcttb010_contrato c ON c.nu_contrato = e.nu_contrato "); 
		sb.append("      INNER JOIN gct.gcttb033_grupo_contrato gc ON ( "); 
		sb.append("        gc.nu_contrato = e.nu_contrato "); 
		sb.append("        AND gc.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      ) "); 
		sb.append("      INNER JOIN gct.gcttb097_qntde_membros_equipe q ON q.nu_equipe = e.nu_equipe "); 
		sb.append("      INNER JOIN GCT.gcttb088_ciclo_trabalho ct ON ( "); 
		sb.append("        ct.nu_equipe = e.nu_equipe "); 
		sb.append("        AND coalesce(q.dt_final_vigencia_membros, c.dt_final_compromisso) >= cast(ct.dt_inicio_ciclo_trabalho as date) "); 
		sb.append("        AND q.dt_inicio_vigencia_membros <= cast(ct.dt_alvo_ciclo_trabalho as date) "); 
		sb.append("      ) "); 
		sb.append("    WHERE "); 
		sb.append("      e.nu_equipe = ? "); 
		sb.append("      and ct.nu_ciclo_trabalho = ? "); 
		sb.append("  ) tb ");
		sb.append("where ");
		sb.append("  qt_dias_sprint > 0 ");
		sb.append("ORDER BY "); 
		sb.append("  dt_inicio_vigencia_membros; ");
		
		return sb.toString();
	}

}
