package org.prometheus.service;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.prometheus.dto.VigenciaMembrosDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VigenciaMembrosService {

	@Autowired
	private DatabaseService databaseService;

	public List<VigenciaMembrosDTO> listar(Long nuFatura, Long nuEquipe) {
		List<VigenciaMembrosDTO> result = new ArrayList<VigenciaMembrosDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setLong(1, nuFatura);
			pstmt.setLong(2, nuEquipe);
			pstmt.setLong(3, nuFatura);
			pstmt.setLong(4, nuEquipe);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				VigenciaMembrosDTO dto = new VigenciaMembrosDTO();

				dto.setNuEquipe(rs.getLong("nu_equipe"));
				dto.setNuPerfil(rs.getLong("nu_perfil"));
				dto.setDePerfil(rs.getString("de_perfil"));
				dto.setDtInicioVigenciaMembros(rs.getDate("dt_inicio_vigencia_membros"));
				dto.setDtFinalVigenciaMembros(rs.getDate("dt_final_vigencia_membros"));
				dto.setQtMembrosEquipe(rs.getBigDecimal("qt_membros_equipe"));
				dto.setQtDiasTrabalhados(rs.getBigDecimal("qt_dias_trabalhados"));
				dto.setQtDiasFaturamento(rs.getBigDecimal("qt_dias_faturamento"));
				dto.setQtEfetiva(rs.getBigDecimal("qt_efetiva"));

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
		sb.append("  *, "); 
		sb.append("  (qt_dias_trabalhados / qt_dias_faturamento) * qt_membros_equipe qt_efetiva "); 
		sb.append("FROM "); 
		sb.append("  ( "); 
		sb.append("    SELECT "); 
		sb.append("      pp.nu_perfil, ");
		sb.append("      pp.de_perfil, ");
		sb.append("      e.nu_equipe, "); 
		sb.append("      q.dt_inicio_vigencia as  dt_inicio_vigencia_membros, "); 
		sb.append("      coalesce( "); 
		sb.append("        q.dt_final_vigencia, "); 
		sb.append("        c.dt_final_compromisso "); 
		sb.append("      ) as dt_final_vigencia_membros, "); 
		sb.append("      q.qt_membros as qt_membros_equipe, "); 
		sb.append("      gct.gctfn305_dias_entre_data( "); 
		sb.append("        CASE "); 
		sb.append("          WHEN q.dt_inicio_vigencia < f.dt_inicio_fatura THEN f.dt_inicio_fatura "); 
		sb.append("          ELSE q.dt_inicio_vigencia "); 
		sb.append("        END, "); 
		sb.append("        CASE "); 
		sb.append("          WHEN coalesce( "); 
		sb.append("            q.dt_final_vigencia, "); 
		sb.append("            c.dt_final_compromisso "); 
		sb.append("          ) > f.dt_final_fatura THEN f.dt_final_fatura "); 
		sb.append("          ELSE coalesce( "); 
		sb.append("            q.dt_final_vigencia, "); 
		sb.append("            c.dt_final_compromisso "); 
		sb.append("          ) "); 
		sb.append("        END, "); 
		sb.append("        e.co_sitio, "); 
		sb.append("        gc.ic_dias_calculo_pagamento "); 
		sb.append("      ) qt_dias_trabalhados, "); 
		sb.append("      gct.gctfn305_dias_entre_data( "); 
		sb.append("        f.dt_inicio_fatura, "); 
		sb.append("        f.dt_final_fatura, "); 
		sb.append("        e.co_sitio, "); 
		sb.append("        gc.ic_dias_calculo_pagamento "); 
		sb.append("      ) qt_dias_faturamento "); 
		sb.append("    FROM "); 
		sb.append("      gct.gcttb087_equipe e "); 
		sb.append("      INNER JOIN gct.gcttb010_contrato c ON c.nu_contrato = e.nu_contrato "); 
		sb.append("      INNER JOIN gct.gcttb033_grupo_contrato gc ON ( "); 
		sb.append("        gc.nu_contrato = e.nu_contrato "); 
		sb.append("        AND gc.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      ) "); 
		sb.append("      INNER JOIN gct.gcttb523_qntde_membro_equipe_perfil q ON q.nu_equipe = e.nu_equipe "); 
		sb.append("      inner join gct.gcttb521_perfil_profissional pp on pp.nu_perfil = q.nu_perfil "); 
		sb.append("      inner join gct.gcttb102_fatura f on f.nu_fatura = ? "); 
		sb.append("    WHERE "); 
		sb.append("      e.nu_equipe = ? "); 
		sb.append("      and q.dt_inicio_vigencia <= f.dt_final_fatura "); 
		sb.append("      and coalesce( "); 
		sb.append("        q.dt_final_vigencia, "); 
		sb.append("        c.dt_final_compromisso "); 
		sb.append("      ) >= f.dt_inicio_fatura "); 
		sb.append("  ) tb "); 
		sb.append("union "); 
		sb.append("SELECT "); 
		sb.append("  null::numeric nu_perfil, ");
		sb.append("  null::text de_perfil, "); 
		sb.append("  *, "); 
		sb.append("  (qt_dias_trabalhados / qt_dias_faturamento) * qt_membros_equipe qt_efetiva "); 
		sb.append("FROM "); 
		sb.append("  ( "); 
		sb.append("    SELECT "); 
		sb.append("      e.nu_equipe, "); 
		sb.append("      q.dt_inicio_vigencia_membros, "); 
		sb.append("      coalesce( "); 
		sb.append("        q.dt_final_vigencia_membros, "); 
		sb.append("        c.dt_final_compromisso "); 
		sb.append("      ) as dt_final_vigencia_membros, "); 
		sb.append("      q.qt_membros_equipe, "); 
		sb.append("      gct.gctfn305_dias_entre_data( "); 
		sb.append("        CASE "); 
		sb.append("          WHEN q.dt_inicio_vigencia_membros < f.dt_inicio_fatura THEN f.dt_inicio_fatura "); 
		sb.append("          ELSE q.dt_inicio_vigencia_membros "); 
		sb.append("        END, "); 
		sb.append("        CASE "); 
		sb.append("          WHEN coalesce( "); 
		sb.append("            q.dt_final_vigencia_membros, "); 
		sb.append("            c.dt_final_compromisso "); 
		sb.append("          ) > f.dt_final_fatura THEN f.dt_final_fatura "); 
		sb.append("          ELSE coalesce( "); 
		sb.append("            q.dt_final_vigencia_membros, "); 
		sb.append("            c.dt_final_compromisso "); 
		sb.append("          ) "); 
		sb.append("        END, "); 
		sb.append("        e.co_sitio, "); 
		sb.append("        gc.ic_dias_calculo_pagamento "); 
		sb.append("      ) qt_dias_trabalhados, "); 
		sb.append("      gct.gctfn305_dias_entre_data( "); 
		sb.append("        f.dt_inicio_fatura, "); 
		sb.append("        f.dt_final_fatura, "); 
		sb.append("        e.co_sitio, "); 
		sb.append("        gc.ic_dias_calculo_pagamento "); 
		sb.append("      ) qt_dias_faturamento "); 
		sb.append("    FROM "); 
		sb.append("      gct.gcttb087_equipe e "); 
		sb.append("      INNER JOIN gct.gcttb010_contrato c ON c.nu_contrato = e.nu_contrato "); 
		sb.append("      INNER JOIN gct.gcttb033_grupo_contrato gc ON ( "); 
		sb.append("        gc.nu_contrato = e.nu_contrato "); 
		sb.append("        AND gc.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      ) "); 
		sb.append("      INNER JOIN gct.gcttb097_qntde_membros_equipe q ON q.nu_equipe = e.nu_equipe "); 
		sb.append("      inner join gct.gcttb102_fatura f on f.nu_fatura = ? "); 
		sb.append("    WHERE "); 
		sb.append("      e.nu_equipe = ? "); 
		sb.append("      and q.dt_inicio_vigencia_membros <= f.dt_final_fatura "); 
		sb.append("      and coalesce( "); 
		sb.append("        q.dt_final_vigencia_membros, "); 
		sb.append("        c.dt_final_compromisso "); 
		sb.append("      ) >= f.dt_inicio_fatura "); 
		sb.append("  ) tb "); 
		sb.append("order by "); 
		sb.append("  de_perfil, dt_inicio_vigencia_membros ");

		return sb.toString();
	}

}
