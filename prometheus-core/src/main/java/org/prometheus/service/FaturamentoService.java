package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FaturamentoService {

	@Autowired
	private DatabaseService databaseService;

	public List<FaturamentoDTO> listar(String coEquipe) {
		return listar(coEquipe, null);
	}

	public List<FaturamentoDTO> listar(String coEquipe, Date dtFinalFatura) {

		List<FaturamentoDTO> result = new ArrayList<FaturamentoDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML(dtFinalFatura));
			pstmt.setString(1, coEquipe);
			if (dtFinalFatura != null) {
				pstmt.setDate(2, new java.sql.Date(dtFinalFatura.getTime()));
			}

			rs = pstmt.executeQuery();

			while (rs.next()) {

				FaturamentoDTO dto = new FaturamentoDTO();

				dto.setNuFaturamento(rs.getLong("nu_faturamento"));
				dto.setNoFaturamento(rs.getString("no_faturamento"));
				dto.setNuFatura(rs.getLong("nu_fatura"));
				dto.setDeFatura(rs.getString("de_fatura"));
				dto.setDtInicioFatura(rs.getDate("dt_inicio_fatura"));
				dto.setDtFinalFatura(rs.getDate("dt_final_fatura"));
				dto.setDtConsiderarFns(rs.getDate("dt_considerar_fns"));
				dto.setIcConsiderarFns(rs.getString("ic_considerar_fns"));
				dto.setQtDiasFaturamento(rs.getBigDecimal("qt_dias_faturamento"));
				dto.setQtDiasServicoExtra(rs.getBigDecimal("qt_dias_servico_extra"));
				dto.setQtDiasAusencia(rs.getBigDecimal("qt_dias_ausencia"));

				result.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return result;
	}

	public String getDML(Date dtFinalFatura) {

		StringBuilder sb = new StringBuilder();

		sb.append("SELECT "); 
		sb.append("  fat.nu_faturamento, "); 
		sb.append("  fat.no_faturamento, "); 
		sb.append("  f.nu_fatura, "); 
		sb.append("  f.de_fatura, "); 
		sb.append("  f.dt_inicio_fatura, "); 
		sb.append("  f.dt_final_fatura, "); 
		sb.append("  gc.dt_considerar_fns, "); 
		sb.append("  case "); 
		sb.append("    when gc.dt_considerar_fns <= f.dt_final_fatura then 'S' "); 
		sb.append("    else 'N' "); 
		sb.append("  end ic_considerar_fns, "); 
		sb.append("  gct.gctfn305_dias_entre_data( "); 
		sb.append("    f.dt_inicio_fatura, "); 
		sb.append("    f.dt_final_fatura, "); 
		sb.append("    f.co_sitio, "); 
		sb.append("    gc.ic_dias_calculo_pagamento "); 
		sb.append("  ) qt_dias_faturamento, "); 
		sb.append("  coalesce( "); 
		sb.append("    ( "); 
		sb.append("      select "); 
		sb.append("        sum(ase.qt_dia_servico_extra) "); 
		sb.append("      from "); 
		sb.append("        gct.gcttb166_ausencia_servico_extra ase "); 
		sb.append("      where "); 
		sb.append("        e.nu_equipe = ase.nu_equipe "); 
		sb.append("        and ase.nu_fatura = f.nu_fatura "); 
		sb.append("    ), "); 
		sb.append("    0 :: numeric "); 
		sb.append("  ) as qt_dias_servico_extra, "); 
		sb.append("  coalesce( "); 
		sb.append("    ( "); 
		sb.append("      select "); 
		sb.append("        sum(ase.qt_dia_ausencia) "); 
		sb.append("      from "); 
		sb.append("        gct.gcttb166_ausencia_servico_extra ase "); 
		sb.append("      where "); 
		sb.append("        e.nu_equipe = ase.nu_equipe "); 
		sb.append("        and ase.nu_fatura = f.nu_fatura "); 
		sb.append("    ), "); 
		sb.append("    0 :: numeric "); 
		sb.append("  ) as qt_dias_ausencia "); 
		sb.append("from "); 
		sb.append("  gct.gcttb010_contrato c "); 
		sb.append("  inner join gct.gcttb087_equipe e on c.nu_contrato = e.nu_contrato "); 
		sb.append("  inner join gct.gcttb033_grupo_contrato gc on ( "); 
		sb.append("    gc.nu_contrato = e.nu_contrato "); 
		sb.append("    and gc.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("  ) "); 
		sb.append("  INNER JOIN gct.gcttb001_area_interna ai ON ai.nu_area_interna = e.nu_area_interna "); 
		sb.append("  inner join gct.gcttb170_termo_equipe t on ( "); 
		sb.append("    t.nu_equipe = e.nu_equipe "); 
		sb.append("    and t.nu_tipo_termo = 11 "); 
		sb.append("    and t.nu_situacao_termo in (1,3) ");
        sb.append("    and trunc(t.vr_financeiro_termo,2) <> 0.00 "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = t.nu_fatura_gerada "); 
		sb.append("  inner join gct.gcttb103_faturamento fat on fat.nu_faturamento = f.nu_faturamento "); 
		sb.append("where "); 
		sb.append("  e.co_equipe = ? ");
		//sb.append("  and f.nu_fatura = 5673 ");
		sb.append("order by "); 
		sb.append("  f.dt_final_fatura ");

		return sb.toString();
	}

}
