package org.prometheus.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.PrecosEquipeDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrecosEquipeService {

	@Autowired
	private DatabaseService databaseService;

	public BigDecimal getVrPrecoGrupoVigente(Long nuEquipe, Long nuFatura) {

		BigDecimal result = BigDecimal.ZERO;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		StringBuilder sb = new StringBuilder();

		sb.append("SELECT "); 
		sb.append("  p.vr_preco_grupo "); 
		sb.append("FROM "); 
		sb.append("  gct.gcttb087_equipe e "); 
		sb.append("  INNER JOIN gct.gcttb010_contrato c on c.nu_contrato = e.nu_contrato "); 
		sb.append("  INNER JOIN gct.gcttb165_preco_contrato_equipe p ON ( "); 
		sb.append("    p.nu_contrato = e.nu_contrato "); 
		sb.append("    AND p.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("  ) "); 
		sb.append("  INNER JOIN gct.gcttb032_tipo_grupo tg ON tg.nu_tipo_grupo = p.nu_tipo_grupo "); 
		sb.append("WHERE "); 
		sb.append("  ( "); 
		sb.append("    ( "); 
		sb.append("      SELECT "); 
		sb.append("        dt_final_fatura "); 
		sb.append("      FROM "); 
		sb.append("        gct.gcttb102_fatura "); 
		sb.append("      WHERE "); 
		sb.append("        nu_fatura = ? "); 
		sb.append("    ) BETWEEN p.dt_inicio_vigencia AND coalesce(p.dt_final_vigencia, c.dt_final_compromisso) "); 
		sb.append("  ) "); 
		sb.append("  AND e.nu_equipe = ? ");

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setLong(1, nuFatura);
			pstmt.setLong(2, nuEquipe);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getBigDecimal(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return result;

	}

	public BigDecimal getVrPrecoGrupo(Long nuEquipe, Long nuFatura, Long nuPerfil) {

		BigDecimal result = BigDecimal.ZERO;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		StringBuilder sb = new StringBuilder();

		sb.append("select "); 
		sb.append("  p.vr_preco_grupo "); 
		sb.append("from "); 
		sb.append("  gct.gcttb087_equipe e "); 
		sb.append("  inner join gct.gcttb524_preco_cntrto_equipe_perfil p on ( "); 
		sb.append("    p.nu_contrato = e.nu_contrato "); 
		sb.append("    and p.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = ? "); 
		sb.append("where "); 
		sb.append("  e.nu_equipe = ? "); 
		sb.append("  and p.nu_perfil = ? "); 
		sb.append("  and f.dt_final_fatura "); 
		sb.append("    between p.dt_inicio_vigencia "); 
		sb.append("      and coalesce(p.dt_final_vigencia, f.dt_final_fatura) ");
		
		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setLong(1, nuFatura);
			pstmt.setLong(2, nuEquipe);
			pstmt.setLong(3, nuPerfil);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getBigDecimal(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return result;

	}

	public BigDecimal getVrPrecoGrupo(Long nuEquipe, Long nuFatura) {

		BigDecimal result = BigDecimal.ZERO;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT ");
		sb.append("   vr_preco_grupo ");
		sb.append(" FROM ");
		sb.append("   gct.gcttb170_termo_equipe t ");
		sb.append(" WHERE ");
		sb.append("   t.nu_tipo_termo = 11 ");
		sb.append("   and t.nu_equipe = ? ");
		sb.append("   and t.nu_fatura_gerada = ? ");

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setLong(1, nuEquipe);
			pstmt.setLong(2, nuFatura);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getBigDecimal(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return result;

	}

	public List<PrecosEquipeDTO> listar(String coEquipe) {

		List<PrecosEquipeDTO> result = new ArrayList<PrecosEquipeDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setString(1, coEquipe);
			pstmt.setString(2, coEquipe);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				PrecosEquipeDTO dto = new PrecosEquipeDTO();

				dto.setNuTipoGrupo(rs.getLong("nu_tipo_grupo"));
				dto.setNoTipoGrupo(rs.getString("no_tipo_grupo"));
				dto.setNuContrato(rs.getLong("nu_contrato"));
				dto.setNuTipoPreco(rs.getLong("nu_tipo_preco"));
				dto.setDtInicioVigencia(rs.getDate("dt_inicio_vigencia"));
				dto.setDtFinalVigencia(rs.getDate("dt_final_vigencia"));
				dto.setNuUnidadeMedida(rs.getLong("nu_unidade_medida"));
				dto.setVrPrecoGrupo(rs.getBigDecimal("vr_preco_grupo"));
				dto.setIcPrecoPadrao(rs.getString("ic_preco_padrao"));
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

		sb.append("SELECT "); 
		sb.append("  tg.nu_tipo_grupo, "); 
		sb.append("  tg.no_tipo_grupo, "); 
		sb.append("  p.nu_contrato, "); 
		sb.append("  p.nu_tipo_preco, "); 
		sb.append("  p.dt_inicio_vigencia, "); 
		sb.append("  p.dt_final_vigencia, "); 
		sb.append("  p.nu_unidade_medida, "); 
		sb.append("  p.vr_preco_grupo, "); 
		sb.append("  p.ic_preco_padrao, "); 
		sb.append("  'N/A'::text de_perfil "); 
		sb.append("FROM "); 
		sb.append("  gct.gcttb087_equipe e "); 
		sb.append("  INNER JOIN gct.gcttb165_preco_contrato_equipe p ON ( "); 
		sb.append("    p.nu_contrato = e.nu_contrato "); 
		sb.append("    AND p.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("  ) "); 
		sb.append("  INNER JOIN gct.gcttb032_tipo_grupo tg ON tg.nu_tipo_grupo = p.nu_tipo_grupo "); 
		sb.append("WHERE "); 
		sb.append("  co_equipe = ? "); 
		sb.append("union "); 
		sb.append("SELECT "); 
		sb.append("  tg.nu_tipo_grupo, "); 
		sb.append("  tg.no_tipo_grupo, "); 
		sb.append("  p.nu_contrato, "); 
		sb.append("  p.nu_tipo_preco, "); 
		sb.append("  p.dt_inicio_vigencia, "); 
		sb.append("  p.dt_final_vigencia, "); 
		sb.append("  p.nu_unidade_medida, "); 
		sb.append("  p.vr_preco_grupo, "); 
		sb.append("  p.ic_preco_padrao, "); 
		sb.append("  pp.de_perfil "); 
		sb.append("FROM "); 
		sb.append("  gct.gcttb087_equipe e "); 
		sb.append("  INNER JOIN gct.gcttb524_preco_cntrto_equipe_perfil p ON ( "); 
		sb.append("    p.nu_contrato = e.nu_contrato "); 
		sb.append("    AND p.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("  ) "); 
		sb.append("  INNER JOIN gct.gcttb032_tipo_grupo tg ON tg.nu_tipo_grupo = p.nu_tipo_grupo "); 
		sb.append("  inner join gct.gcttb521_perfil_profissional pp on pp.nu_perfil = p.nu_perfil "); 
		sb.append("WHERE "); 
		sb.append("  co_equipe = ? ");

		return sb.toString();
	}

}
