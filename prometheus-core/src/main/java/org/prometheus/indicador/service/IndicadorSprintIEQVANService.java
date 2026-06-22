package org.prometheus.indicador.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.prometheus.indicador.ParametrosIndicador;
import org.prometheus.relatorios.RelatorioContext;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndicadorSprintIEQVANService {

	@Autowired
	private DatabaseService databaseService;

	public ParametrosIndicador recuperar(RelatorioContext context) {

		ParametrosIndicador result = null;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			StringBuilder sb = new StringBuilder();

			sb.append("select ");
			sb.append("  a.vr_avaliacao, ");
			sb.append("  a.qt_perguntas_respondidas, ");
			sb.append("  a.dh_avaliacao_equipe, ");
			sb.append("  gi.vr_fator_ajuste ");
			sb.append("from ");
			sb.append("  gct.gcttb088_ciclo_trabalho ct ");
			sb.append("  inner join gct.gcttb087_equipe e on e.nu_equipe = ct.nu_equipe ");
			sb.append("  inner join gct.gcttb086_solicitacao_equipe se on ( ");
			sb.append("    se.nu_equipe = ct.nu_equipe ");
			sb.append("    and se.nu_ciclo_trabalho = ct.nu_ciclo_trabalho ");
			sb.append("  ) ");
			sb.append("  inner join gct.gcttb055_servico_contrato sc on ( ");
			sb.append("    sc.nu_contrato = e.nu_contrato ");
			sb.append("    and sc.nu_tipo_grupo = e.nu_tipo_grupo ");
			sb.append("    and sc.nu_tipo_servico = se.nu_tipo_servico ");
			sb.append("    and sc.ic_servico_compoe_calculo_fns = 'S' ");
			sb.append("    and se.co_falha_solicitacao = 'NF' ");
			sb.append("  ) ");
			sb.append("  inner join gct.gcttb199_avlco_atndmnto_equipe a on ( ");
			sb.append("    a.nu_equipe = ct.nu_equipe ");
			sb.append("    and a.nu_ciclo_trabalho = ct.nu_ciclo_trabalho ");
			sb.append("  ) ");
			sb.append("  inner join gct.gcttb112_tipo_indicador ti on ti.nu_tipo_indicador = a.nu_tipo_indicador ");
			sb.append("  inner join gct.gcttb115_grupo_indicador gi on ( ");
			sb.append("    gi.nu_contrato = sc.nu_contrato ");
			sb.append("    and gi.nu_tipo_grupo = sc.nu_tipo_grupo ");
			sb.append("    and gi.nu_tipo_indicador = ti.nu_tipo_indicador ");
			sb.append("  ) ");
			sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = ? ");
			sb.append("where ");
			sb.append("  ct.nu_equipe = ? ");
			sb.append("  and ti.nu_tipo_indicador = ? ");
			sb.append("  and ct.dt_alvo_ciclo_trabalho :: date between f.dt_inicio_fatura and f.dt_final_fatura ");
			sb.append("  and ct.nu_ciclo_trabalho = ? ");
			sb.append("  and ct.co_falha_ciclo_trabalho in ('NF', 'SF') ");
			sb.append("order by ");
			sb.append("  a.dh_avaliacao_equipe desc ");
			sb.append("limit 1 ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setLong(1, context.getFaturamento().getNuFatura());
			pstmt.setLong(2, context.getEquipe().getNuEquipe());
			pstmt.setLong(3, 30L);
			pstmt.setLong(4, context.getCiclo().getNuCicloTrabalho());

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = new ParametrosIndicador();
				result.setQtQuestoesRespondidas(rs.getBigDecimal("qt_perguntas_respondidas"));
				result.setVrFatorAjuste(rs.getBigDecimal("vr_fator_ajuste"));
				result.setVrAvaliacaoSprint(rs.getBigDecimal("vr_avaliacao"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return result;
	}
}
