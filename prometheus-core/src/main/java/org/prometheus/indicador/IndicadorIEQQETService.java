package org.prometheus.indicador;

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
public class IndicadorIEQQETService {

	@Autowired
	private DatabaseService databaseService;

	public BigDecimal contarServicosEntregues(Long nuEquipe, Long nuFatura) {
		
		BigDecimal result = BigDecimal.ZERO;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			StringBuilder sb = new StringBuilder();
			
			sb.append("select "); 
			sb.append("  count(*) as qt_solicitacoes "); 
			sb.append("from "); 
			sb.append("  gct.gcttb086_solicitacao_equipe se "); 
			sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = ? "); 
			sb.append("  inner join gct.gcttb087_equipe e on e.nu_equipe = se.nu_equipe "); 
			sb.append("  inner join gct.gcttb178_amostra_solicitacao s on se.nu_solicitacao = s.nu_solicitacao "); 
			sb.append("  inner join gct.gcttb055_servico_contrato sc on ( "); 
			sb.append("    sc.nu_contrato = e.nu_contrato "); 
			sb.append("    and sc.nu_tipo_grupo = e.nu_tipo_grupo "); 
			sb.append("    and sc.nu_tipo_servico = se.nu_tipo_servico "); 
			sb.append("  ) "); 
			sb.append("where "); 
			sb.append("  se.nu_equipe = ? "); 
			sb.append("  and sc.ic_servico_compoe_calculo_fns = 'S' "); 
			sb.append("  and ( "); 
			sb.append("    se.co_falha_solicitacao is null "); 
			sb.append("    or se.co_falha_solicitacao = 'NF' "); 
			sb.append("  )"); 
			sb.append("  and gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) ");
			sb.append("    between f.dt_inicio_fatura and f.dt_final_fatura "); 
			sb.append("  and s.dh_efetiva is not null; ");			
			
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

	public BigDecimal contarServicosContratados(Long nuEquipe, Long nuFatura) {
		
		BigDecimal result = BigDecimal.ZERO;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			StringBuilder sb = new StringBuilder();
			
			sb.append("select "); 
			sb.append("  count(*) as qt_solicitacoes "); 
			sb.append("from "); 
			sb.append("  gct.gcttb086_solicitacao_equipe se "); 
			sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = ? "); 
			sb.append("  inner join gct.gcttb087_equipe e on e.nu_equipe = se.nu_equipe "); 
			sb.append("  inner join gct.gcttb178_amostra_solicitacao s on se.nu_solicitacao = s.nu_solicitacao "); 
			sb.append("  inner join gct.gcttb055_servico_contrato sc on ( "); 
			sb.append("    sc.nu_contrato = e.nu_contrato "); 
			sb.append("    and sc.nu_tipo_grupo = e.nu_tipo_grupo "); 
			sb.append("    and sc.nu_tipo_servico = se.nu_tipo_servico "); 
			sb.append("  ) "); 
			sb.append("where "); 
			sb.append("  se.nu_equipe = ? "); 
			sb.append("  and sc.ic_servico_compoe_calculo_fns = 'S' "); 
			sb.append("  and ( "); 
			sb.append("    se.co_falha_solicitacao is null "); 
			sb.append("    or se.co_falha_solicitacao = 'NF' "); 
			sb.append("  ) "); 
			sb.append("  and gct.gctfn316_calc_dt_prevista(s.nu_solicitacao, date(s.dh_prevista)) ");
			sb.append("    between f.dt_inicio_fatura and f.dt_final_fatura "); 
			sb.append("  and s.nu_situacao_solicitacao in ( "); 
			sb.append("    select "); 
			sb.append("      nu_situacao_atendimento "); 
			sb.append("    from "); 
			sb.append("      gct.gcttb192_servico_contratado "); 
			sb.append("    where "); 
			sb.append("      nu_contrato = e.nu_contrato "); 
			sb.append("      and nu_tipo_grupo = e.nu_tipo_grupo "); 
			sb.append("      and nu_tipo_indicador = 19 "); 
			sb.append("  ); ");			
			
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
}
