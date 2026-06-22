package org.prometheus.indicador.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndicadorISETSprintService {

	@Autowired
	private DatabaseService databaseService;

	public BigDecimal contarServicosEntregues(Long nuEquipe, Long nuCicloTrabalho) {
		
		BigDecimal result = BigDecimal.ZERO;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			StringBuilder sb = new StringBuilder();

			sb.append("SELECT "); 
			sb.append("  count(*) "); 
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
			sb.append("  inner join gct.gcttb191_servico_entregue a on ( "); 
			sb.append("    a.nu_situacao_atendimento = s.nu_situacao_solicitacao "); 
			sb.append("    and a.nu_contrato = e.nu_contrato "); 
			sb.append("    and a.nu_tipo_grupo = e.nu_tipo_grupo "); 
			sb.append("    and a.nu_tipo_indicador = ? "); 
			sb.append("  ) "); 
			sb.append("where "); 
			sb.append("  cct.nu_equipe = ? "); 
			sb.append("  and cct.nu_ciclo_trabalho = ? ");
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setLong(1, 34L);
			pstmt.setLong(2, nuEquipe);
			pstmt.setLong(3, nuCicloTrabalho);

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

	public BigDecimal contarServicosContratados(Long nuEquipe, Long nuCicloTrabalho) {
		
		BigDecimal result = BigDecimal.ZERO;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			StringBuilder sb = new StringBuilder();

			sb.append("SELECT "); 
			sb.append("  count(*) "); 
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
			sb.append("  inner join gct.gcttb192_servico_contratado a on ( "); 
			sb.append("    a.nu_situacao_atendimento = s.nu_situacao_solicitacao "); 
			sb.append("    and a.nu_contrato = e.nu_contrato "); 
			sb.append("    and a.nu_tipo_grupo = e.nu_tipo_grupo "); 
			sb.append("    and a.nu_tipo_indicador = ? "); 
			sb.append("  ) "); 
			sb.append("where "); 
			sb.append("  cct.nu_equipe = ? "); 
			sb.append("  and cct.nu_ciclo_trabalho = ? ");
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setLong(1, 34L);
			pstmt.setLong(2, nuEquipe);
			pstmt.setLong(3, nuCicloTrabalho);

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
