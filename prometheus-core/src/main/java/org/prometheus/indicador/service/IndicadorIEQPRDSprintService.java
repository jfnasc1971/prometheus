package org.prometheus.indicador.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.prometheus.relatorios.RelatorioContext;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndicadorIEQPRDSprintService {

	@Autowired
	private DatabaseService databaseService;

	public BigDecimal recuperarProdutividadeEsperada(RelatorioContext context) {
		
		BigDecimal result = BigDecimal.ZERO;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			StringBuilder sb = new StringBuilder();

			sb.append("select "); 
			sb.append("  gi.vr_fator_ajuste "); 
			sb.append("from "); 
			sb.append("  gct.gcttb115_grupo_indicador gi "); 
			sb.append("where "); 
			sb.append("  gi.nu_contrato = ? "); 
			sb.append("  and gi.nu_tipo_grupo = ? "); 
			sb.append("  and gi.nu_tipo_indicador = ? ");
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setLong(1, context.getEquipe().getNuEquipe());
			pstmt.setLong(2, context.getEquipe().getNuTipoGrupo());
			pstmt.setLong(3, 46L);

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

	
	public BigDecimal contarDiasSprint(Long nuEquipe, Long nuCicloTrabalho) {
		
		BigDecimal result = BigDecimal.ZERO;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			StringBuilder sb = new StringBuilder();

			sb.append("SELECT "); 
			sb.append("  gct.gctfn305_dias_entre_data(cct.dt_inicio_ciclo_trabalho, cct.dt_alvo_ciclo_trabalho, "); 
			sb.append("    ai.co_sitio, gc.ic_dias_calculo_pagamento) "); 
			sb.append("FROM "); 
			sb.append("  gct.gcttb087_equipe e "); 
			sb.append("  inner join gct.gcttb088_ciclo_trabalho cct on cct.nu_equipe = e.nu_equipe "); 
			sb.append("  inner join gct.gcttb001_area_interna ai on ai.nu_area_interna = e.nu_area_interna "); 
			sb.append("  inner join gct.gcttb010_contrato c on c.nu_contrato = e.nu_contrato "); 
			sb.append("  inner join gct.gcttb033_grupo_contrato gc on ( "); 
			sb.append("    gc.nu_contrato = e.nu_contrato "); 
			sb.append("    and gc.nu_tipo_grupo = e.nu_tipo_grupo "); 
			sb.append("  ) "); 
			sb.append("where "); 
			sb.append("  e.nu_equipe = ? "); 
			sb.append("  and cct.nu_ciclo_trabalho = ? ");
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setLong(1, nuEquipe);
			pstmt.setLong(2, nuCicloTrabalho);

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

	public BigDecimal calcularTamanhoSprint(Long nuEquipe, Long nuCicloTrabalho) {
		
		BigDecimal result = BigDecimal.ZERO;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			StringBuilder sb = new StringBuilder();

			sb.append("SELECT "); 
			sb.append("  sum(se.nu_tamanho_servico) "); 
			sb.append("FROM "); 
			sb.append("  gct.gcttb088_ciclo_trabalho cct "); 
			sb.append("  inner join gct.gcttb087_equipe e on ( "); 
			sb.append("    e.nu_equipe = cct.nu_equipe "); 
			sb.append("  ) "); 
			sb.append("  inner join gct.gcttb086_solicitacao_equipe se on ( "); 
			sb.append("    se.nu_equipe = cct.nu_equipe "); 
			sb.append("    and se.nu_ciclo_trabalho = cct.nu_ciclo_trabalho "); 
			sb.append("  ) "); 
			sb.append("  inner join gct.gcttb005_solicitacao s on ( "); 
			sb.append("    s.nu_solicitacao = se.nu_solicitacao "); 
			sb.append("    and se.co_falha_solicitacao = 'NF' "); 
			sb.append("  ) "); 
			sb.append("  inner join gct.gcttb055_servico_contrato sc on ( "); 
			sb.append("    sc.nu_contrato = e.nu_contrato "); 
			sb.append("    and sc.nu_tipo_grupo = e.nu_tipo_grupo "); 
			sb.append("    and sc.nu_tipo_servico = se.nu_tipo_servico "); 
			sb.append("    and sc.ic_servico_compoe_calculo_fns = 'S' "); 
			sb.append("  ) "); 
			sb.append("where "); 
			sb.append("  cct.nu_equipe = ? "); 
			sb.append("  and cct.nu_ciclo_trabalho = ? ");
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setLong(1, nuEquipe);
			pstmt.setLong(2, nuCicloTrabalho);

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

	public BigDecimal contarMembrosSprint(Long nuEquipe, Long nuCicloTrabalho) {
		
		BigDecimal result = BigDecimal.ZERO;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			StringBuilder sb = new StringBuilder();

			sb.append("SELECT "); 
			sb.append("  sum(q.qt_membros) "); 
			sb.append("FROM "); 
			sb.append("  gct.gcttb088_ciclo_trabalho cct "); 
			sb.append("  inner join gct.gcttb087_equipe e on ( "); 
			sb.append("    e.nu_equipe = cct.nu_equipe "); 
			sb.append("  ) "); 
			sb.append("  inner join gct.gcttb523_qntde_membro_equipe_perfil q on q.nu_equipe = e.nu_equipe "); 
			sb.append("where "); 
			sb.append("  cct.nu_equipe = ? "); 
			sb.append("  and cct.nu_ciclo_trabalho = ? "); 
			sb.append("  and cast(q.dt_inicio_vigencia as date) <= cast(cct.dt_alvo_ciclo_trabalho as date) "); 
			sb.append("  and ( "); 
			sb.append("    cast(q.dt_final_vigencia as date) is null "); 
			sb.append("    or "); 
			sb.append("    cast(q.dt_final_vigencia as date) >= cast(cct.dt_alvo_ciclo_trabalho as date) "); 
			sb.append("  ) ");
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setLong(1, nuEquipe);
			pstmt.setLong(2, nuCicloTrabalho);

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

}
