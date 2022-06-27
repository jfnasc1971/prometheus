package org.prometheus.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.dto.IndicadorDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndicadorService {

	@Autowired
	private DatabaseService databaseService;

	public BigDecimal getVrProdutividadeEsperada(Long nuEquipe) {

		BigDecimal result = BigDecimal.ZERO;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		StringBuilder sb = new StringBuilder();
		
		sb.append("select gi.vr_fator_ajuste "); 
		sb.append("  from gct.gcttb087_equipe e "); 
		sb.append(" inner join gct.gcttb010_contrato c on c.nu_contrato = e.nu_contrato "); 
		sb.append(" inner join gct.gcttb115_grupo_indicador gi on ( "); 
		sb.append("         gi.nu_contrato = e.nu_contrato "); 
		sb.append("         and gi.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("       ) "); 
		sb.append(" inner join gct.gcttb112_tipo_indicador ti on ti.nu_tipo_indicador = gi.nu_tipo_indicador "); 
		sb.append(" where ti.sg_tipo_indicador = 'IEQPR' "); 
		sb.append("   and e.nu_equipe = ? ");
		
		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setLong(1, nuEquipe);

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
	
	public List<IndicadorContratoDTO> listarIndicadoresContrato(String coEquipe) {
		List<IndicadorContratoDTO> result = new ArrayList<IndicadorContratoDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getIndicadoresContratoDML());
			pstmt.setString(1, coEquipe);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				IndicadorContratoDTO dto = new IndicadorContratoDTO();

				dto.setSgTipoIndicador(rs.getString("sg_tipo_indicador"));
				dto.setNoTipoIndicador(rs.getString("no_tipo_indicador"));
				dto.setVrPesoTipoIndicador(rs.getBigDecimal("vr_peso_tipo_indicador"));
				dto.setVrFatorAjuste(rs.getBigDecimal("vr_fator_ajuste"));
				dto.setQtFaturamento(rs.getBigDecimal("qt_faturamento"));
				dto.setVrLimiteMaximoIndicador(rs.getBigDecimal("vr_limite_maximo_indicador"));
				dto.setIcApenasFaturamentoAtual(rs.getString("ic_apenas_faturamento_atual"));

				result.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return result;
	}

	public List<IndicadorDTO> listar(Long nuEquipe, Long nuFatura) {
		List<IndicadorDTO> result = new ArrayList<IndicadorDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getIndicadoresDML());
			pstmt.setLong(1, nuEquipe);
			pstmt.setLong(2, nuFatura);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				IndicadorDTO dto = new IndicadorDTO();

				dto.setNuFatura(rs.getLong("nu_fatura"));
				dto.setDeFatura(rs.getString("de_fatura"));
				dto.setDtInicioFatura(rs.getDate("dt_inicio_fatura"));
				dto.setDtFinalFatura(rs.getDate("dt_final_fatura"));
				dto.setNuEquipe(rs.getLong("nu_equipe"));
				dto.setNuTipoGrupo(rs.getLong("nu_tipo_grupo"));
				dto.setNuTipoIndicador(rs.getLong("nu_tipo_indicador"));
				dto.setNoTipoIndicador(rs.getString("no_tipo_indicador"));
				dto.setSgTipoIndicador(rs.getString("sg_tipo_indicador"));
				dto.setIcAplicaIndicador(rs.getString("ic_aplica_indicador"));
				dto.setIcApenasFaturamentoAtual(rs.getString("ic_apenas_faturamento_atual"));
				dto.setVrCalculoIndicador(rs.getBigDecimal("vr_calculo_indicador"));
				dto.setVrPesoTipoIndicador(rs.getBigDecimal("vr_peso_tipo_indicador"));
				dto.setVrFaixaFns(rs.getBigDecimal("vr_faixa_fns"));
				dto.setDhGeracaoIndicadorEquipe(rs.getTimestamp("dh_geracao_indicador_equipe"));

				result.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return result;
	}

	public IndicadorDTO findByFatura(Long nuEquipe, Long nuFatura, String icAplicaIndicador) {

		IndicadorDTO result = null;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getIndicadoresDML());
			pstmt.setLong(1, nuEquipe);
			pstmt.setLong(2, nuFatura);
			pstmt.setString(3, icAplicaIndicador);

			rs = pstmt.executeQuery();

			if (rs.next()) {

				result = new IndicadorDTO();

				result.setNuFatura(rs.getLong("nu_fatura"));
				result.setDeFatura(rs.getString("de_fatura"));
				result.setDtInicioFatura(rs.getDate("dt_inicio_fatura"));
				result.setDtFinalFatura(rs.getDate("dt_final_fatura"));
				result.setNuEquipe(rs.getLong("nu_equipe"));
				result.setNuTipoGrupo(rs.getLong("nu_tipo_grupo"));
				result.setNuTipoIndicador(rs.getLong("nu_tipo_indicador"));
				result.setNoTipoIndicador(rs.getString("no_tipo_indicador"));
				result.setSgTipoIndicador(rs.getString("sg_tipo_indicador"));
				result.setIcAplicaIndicador(rs.getString("ic_aplica_indicador"));
				result.setIcApenasFaturamentoAtual(rs.getString("ic_apenas_faturamento_atual"));
				result.setVrCalculoIndicador(rs.getBigDecimal("vr_calculo_indicador"));
				result.setVrPesoTipoIndicador(rs.getBigDecimal("vr_peso_tipo_indicador"));
				result.setVrFaixaFns(rs.getBigDecimal("vr_faixa_fns"));
				result.setDhGeracaoIndicadorEquipe(rs.getTimestamp("dh_geracao_indicador_equipe"));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return result;

	}

	public String getIndicadoresDML() {

		StringBuilder sb = new StringBuilder();

		sb.append("SELECT ");
		sb.append("  f.nu_fatura, ");
		sb.append("  f.de_fatura, ");
		sb.append("  f.dt_inicio_fatura, ");
		sb.append("  f.dt_final_fatura, ");
		sb.append("  e.nu_equipe, ");
		sb.append("  e.nu_tipo_grupo, ");
		sb.append("  ti.nu_tipo_indicador, ");
		sb.append("  ti.no_tipo_indicador, ");
		sb.append("  ti.sg_tipo_indicador, ");
		sb.append("  gi.ic_aplica_indicador, ");
		sb.append("  gi.ic_apenas_faturamento_atual, ");
		sb.append("  vie.vr_calculo_indicador, ");
		sb.append("  vie.vr_peso_tipo_indicador, ");
		sb.append("  CASE ");
		sb.append("    WHEN gc.dt_considerar_fns > f.dt_final_fatura THEN cast(1 AS numeric) ");
		sb.append("    ELSE vie.vr_faixa_fns ");
		sb.append("  END vr_faixa_fns, ");
		sb.append("  vie.dh_geracao_indicador_equipe ");
		sb.append("FROM ");
		sb.append("  gct.gcttb087_equipe e ");
		sb.append("  INNER JOIN gct.gcttb170_termo_equipe t ON ( ");
		sb.append("    t.nu_equipe = e.nu_equipe ");
		sb.append("    AND t.nu_tipo_termo = 11 ");
		sb.append("  ) ");
		sb.append("  INNER JOIN gct.gcttb033_grupo_contrato gc ON ( ");
		sb.append("    gc.nu_contrato = e.nu_contrato ");
		sb.append("    AND gc.nu_tipo_grupo = e.nu_tipo_grupo ");
		sb.append("  ) ");
		sb.append("  INNER JOIN gct.gcttb115_grupo_indicador gi ON ( ");
		sb.append("    gi.nu_contrato = gc.nu_contrato ");
		sb.append("    AND gi.nu_tipo_grupo = gc.nu_tipo_grupo ");
		sb.append("  ) ");
		sb.append("  INNER JOIN gct.gcttb112_tipo_indicador ti ON ti.nu_tipo_indicador = gi.nu_tipo_indicador ");
		sb.append("  INNER JOIN gct.gcttb102_fatura f ON f.nu_fatura = t.nu_fatura_gerada ");
		sb.append("  INNER JOIN gct.gcttb172_valor_indicador_equipe vie ON ( ");
		sb.append("    vie.nu_equipe = e.nu_equipe ");
		sb.append("    AND vie.nu_tipo_indicador = gi.nu_tipo_indicador ");
		sb.append("    AND coalesce(vie.nu_fatura, vie.nu_fatura_ajuste) = f.nu_fatura ");
		sb.append("  ) ");
		sb.append("WHERE ");
		sb.append("  e.nu_equipe = ? ");
		sb.append("  AND t.nu_fatura_gerada = ? ");
		sb.append("  AND ic_aplica_indicador = ? ");
		sb.append("  AND vie.dh_geracao_indicador_equipe = ( ");
		sb.append("    SELECT ");
		sb.append("      max(dh_geracao_indicador_equipe) ");
		sb.append("    FROM ");
		sb.append("      gct.gcttb172_valor_indicador_equipe vie1 ");
		sb.append("    WHERE ");
		sb.append("      vie1.nu_equipe = vie.nu_equipe ");
		sb.append(
				"      AND coalesce(vie1.nu_fatura, vie1.nu_fatura_ajuste) = coalesce(vie.nu_fatura, vie.nu_fatura_ajuste) ");
		sb.append("      AND vie1.nu_tipo_indicador = vie.nu_tipo_indicador ");
		sb.append("  ) ");

		return sb.toString();
	}

	public String getIndicadoresContratoDML() {

		StringBuilder sb = new StringBuilder();

		sb.append("SELECT ");
		sb.append("  ti.sg_tipo_indicador, ");
		sb.append("  ti.no_tipo_indicador, ");
		sb.append("  gi.vr_peso_tipo_indicador, ");
		sb.append("  gi.vr_fator_ajuste, ");
		sb.append("  gi.qt_faturamento, ");
		sb.append("  gi.vr_limite_maximo_indicador, ");
		sb.append("  gi.ic_apenas_faturamento_atual ");
		sb.append("FROM ");
		sb.append("  gct.gcttb087_equipe e ");
		sb.append("  INNER JOIN gct.gcttb010_contrato c ON c.nu_contrato = e.nu_contrato ");
		sb.append("  INNER JOIN gct.gcttb033_grupo_contrato gc ON ( ");
		sb.append("    gc.nu_contrato = e.nu_contrato ");
		sb.append("    AND gc.nu_tipo_grupo = e.nu_tipo_grupo ");
		sb.append("  ) ");
		sb.append("  INNER JOIN gct.gcttb115_grupo_indicador gi ON ( ");
		sb.append("    gi.nu_contrato = gc.nu_contrato ");
		sb.append("    AND gi.nu_tipo_grupo = gc.nu_tipo_grupo ");
		sb.append("  ) ");
		sb.append("  INNER JOIN gct.gcttb112_tipo_indicador ti ON ti.nu_tipo_indicador = gi.nu_tipo_indicador ");
		sb.append("WHERE ");
		sb.append("  e.co_equipe = ? ");

		return sb.toString();
	}

}
