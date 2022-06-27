package org.prometheus.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.CicloTrabalhoDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CicloTrabalhoService {

	@Autowired
	private DatabaseService databaseService;

	public CicloTrabalhoDTO getDiasTrabalhados(Long nuFatura, Long nuEquipe, Long nuCiclo) {

		CicloTrabalhoDTO dto = null;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		StringBuilder sb = new StringBuilder();

		sb.append("  SELECT");
		sb.append("    nu_equipe,");
		sb.append("    nu_fatura,");
		sb.append("    nu_ciclo_trabalho,");
		sb.append("    qt_dias_sprint,");
		sb.append("    sum(qt_dias_trab) as qt_dias_trab,");
		sb.append("    sum((qt_dias_trab / qt_dias_sprint) * qt_membros_equipe) qt_efetiva");
		sb.append("  FROM");
		sb.append("    (");
		sb.append("      SELECT");
		sb.append("        f.nu_fatura,");
		sb.append("        ct.nu_equipe,");
		sb.append("        ct.nu_ciclo_trabalho,");
		sb.append("        q.dt_inicio_vigencia_membros,");
		sb.append("        q.dt_final_vigencia_membros,");
		sb.append("        q.qt_membros_equipe,");
		sb.append("        gct.gctfn305_dias_entre_data(");
		sb.append("          CASE");
		sb.append("            WHEN q.dt_inicio_vigencia_membros < cast(ct.dt_inicio_ciclo_trabalho as date)");
		sb.append("              THEN cast(ct.dt_inicio_ciclo_trabalho as date)");
		sb.append("            ELSE q.dt_inicio_vigencia_membros");
		sb.append("          END,");
		sb.append("          CASE");
		sb.append("            WHEN q.dt_final_vigencia_membros > cast(ct.dt_alvo_ciclo_trabalho as date)");
		sb.append("              THEN cast(ct.dt_alvo_ciclo_trabalho as date)");
		sb.append("            ELSE q.dt_final_vigencia_membros");
		sb.append("          END,");
		sb.append("          e.co_sitio,");
		sb.append("          gc.ic_dias_calculo_pagamento");
		sb.append("        ) qt_dias_trab,");
		sb.append("        gct.gctfn305_dias_entre_data(");
		sb.append("          ct.dt_inicio_ciclo_trabalho,");
		sb.append("          ct.dt_alvo_ciclo_trabalho,");
		sb.append("          e.co_sitio,");
		sb.append("          gc.ic_dias_calculo_pagamento");
		sb.append("        ) qt_dias_sprint");
		sb.append("      FROM");
		sb.append("        gct.gcttb087_equipe e");
		sb.append("        INNER JOIN gct.gcttb170_termo_equipe t ON t.nu_equipe = e.nu_equipe");
		sb.append("        INNER JOIN gct.gcttb102_fatura f ON f.nu_fatura = t.nu_fatura_gerada");
		sb.append("        INNER JOIN gct.gcttb010_contrato c ON c.nu_contrato = e.nu_contrato");
		sb.append("        INNER JOIN gct.gcttb033_grupo_contrato gc ON (");
		sb.append("          gc.nu_contrato = e.nu_contrato");
		sb.append("          AND gc.nu_tipo_grupo = e.nu_tipo_grupo");
		sb.append("        )");
		sb.append("        INNER JOIN gct.gcttb097_qntde_membros_equipe q ON q.nu_equipe = e.nu_equipe");
		sb.append("        INNER JOIN GCT.gcttb088_ciclo_trabalho ct ON ct.nu_equipe = e.nu_equipe");
		sb.append("        AND cast(ct.dt_alvo_ciclo_trabalho as date) BETWEEN f.dt_inicio_fatura");
		sb.append("        AND f.dt_final_fatura");
		sb.append("      WHERE");
		sb.append("        t.nu_tipo_termo = 11");
		sb.append("        AND q.dt_inicio_vigencia_membros <= f.dt_final_fatura");
		sb.append("        AND coalesce(");
		sb.append("          q.dt_final_vigencia_membros,");
		sb.append("          c.dt_final_compromisso");
		sb.append("        ) >= f.dt_inicio_fatura");
		sb.append("    ) tb");
		sb.append("  WHERE");
		sb.append("    nu_fatura = ?");
		sb.append("    AND nu_equipe = ?");
		sb.append("    and nu_ciclo_trabalho = ?");
		sb.append("  group by ");
		sb.append("    nu_equipe,");
		sb.append("    nu_fatura,");
		sb.append("    nu_ciclo_trabalho,");
		sb.append("    qt_dias_sprint");

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setLong(1, nuFatura);
			pstmt.setLong(2, nuEquipe);
			pstmt.setLong(3, nuCiclo);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new CicloTrabalhoDTO();

				dto.setNuEquipe(rs.getLong("nu_equipe"));
				dto.setNuFatura(rs.getLong("nu_fatura"));
				dto.setNuCicloTrabalho(rs.getLong("nu_ciclo_trabalho"));
				dto.setQtDiasSprint(rs.getBigDecimal("qt_dias_sprint"));
				dto.setQtDiasTrab(rs.getBigDecimal("qt_dias_trab"));
				dto.setQtEfetiva(rs.getBigDecimal("qt_efetiva"));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return dto;

	}

	public BigDecimal getTamanhoCiclo(Long nuEquipe, Long nuCiclo) {
		BigDecimal result = BigDecimal.ZERO;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT ");
		sb.append("   sum(coalesce(se.nu_tamanho_servico,cast(0 as numeric))) as nu_tamanho_servico ");
		sb.append(" FROM ");
		sb.append("   gct.gcttb087_equipe e ");
		sb.append("   INNER JOIN gct.gcttb010_contrato c ON c.nu_contrato = e.nu_contrato ");
		sb.append("   INNER JOIN gct.gcttb088_ciclo_trabalho ct ON ct.nu_equipe = e.nu_equipe ");
		sb.append("   inner join gct.gcttb086_solicitacao_equipe se on ( ");
		sb.append("           se.nu_equipe = e.nu_equipe ");
		sb.append("           and se.nu_ciclo_trabalho = ct.nu_ciclo_trabalho ");
		sb.append("         ) ");
		sb.append("   inner join gct.gcttb005_solicitacao s on s.nu_solicitacao = se.nu_solicitacao ");
		sb.append(" WHERE ");
		sb.append("   e.nu_equipe = ? ");
		sb.append("   and ct.nu_ciclo_trabalho = ? ");

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setLong(1, nuEquipe);
			pstmt.setLong(2, nuCiclo);

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

	public List<CicloTrabalhoDTO> listar(Long nuFatura, Long nuEquipe) {
		List<CicloTrabalhoDTO> result = new ArrayList<CicloTrabalhoDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setLong(1, nuFatura);
			pstmt.setLong(2, nuEquipe);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				CicloTrabalhoDTO dto = new CicloTrabalhoDTO();

				dto.setNuEquipe(rs.getLong("nu_equipe"));
				dto.setNuCicloTrabalho(rs.getLong("nu_ciclo_trabalho"));
				dto.setNuSituacaoCicloTrabalho(rs.getLong("nu_situacao_ciclo_trabalho"));
				dto.setDeCicloTrabalho(rs.getString("de_ciclo_trabalho"));
				dto.setDtInicioCicloTrabalho(rs.getTimestamp("dt_inicio_ciclo_trabalho"));
				dto.setDtAlvoCicloTrabalho(rs.getTimestamp("dt_alvo_ciclo_trabalho"));
				dto.setDtFinalCicloTrabalho(rs.getTimestamp("dt_final_ciclo_trabalho"));
				dto.setCoFalhaCicloTrabalho(rs.getString("co_falha_ciclo_trabalho"));
				dto.setNuAcaoHistorico(rs.getLong("nu_acao_historico"));
				dto.setNuMtrcaAcaoHstro(rs.getLong("nu_mtrca_acao_hstro"));
				dto.setVrQuestionarioAvaliacao(rs.getBigDecimal("vr_questionario_avaliacao"));
				dto.setVrNotaQualidadeProduto(rs.getBigDecimal("vr_nota_qualidade_produto"));
				dto.setVrTotalApuradoAvaliacao(rs.getBigDecimal("vr_total_apurado_avaliacao"));
				dto.setNuMtrcaAvaliacaoEquipe(rs.getLong("nu_mtrca_avaliacao_equipe"));
				dto.setDeJustificativaFalha(rs.getString("de_justificativa_falha"));
				dto.setDeJustificativaAlteracao(rs.getString("de_justificativa_alteracao"));
				dto.setQtDiasAusencia(rs.getBigDecimal("qt_dia_ausencia"));
				dto.setQtDiasExtra(rs.getBigDecimal("qt_dia_servico_extra"));

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
		sb.append("  distinct ct.*, ");
		sb.append("  coalesce( ");
		sb.append("    ( ");
		sb.append("      select ");
		sb.append("        sum(ase1.qt_dia_ausencia) :: numeric ");
		sb.append("      from ");
		sb.append("        gct.gcttb166_ausencia_servico_extra ase1 ");
		sb.append("      where ");
		sb.append("        ase1.nu_equipe = e.nu_equipe ");
		sb.append("        and ase1.nu_fatura <= f.nu_fatura ");
		sb.append("        and ase1.nu_ciclo_trabalho = ct.nu_ciclo_trabalho ");
		sb.append("        and ( ");
		sb.append("          ct.dt_inicio_ciclo_trabalho between f.dt_inicio_fatura and f.dt_final_fatura ");
		sb.append("          or ");
		sb.append("          ct.dt_alvo_ciclo_trabalho between f.dt_inicio_fatura and f.dt_final_fatura ");
		sb.append("        ) ");
		sb.append("    ), ");
		sb.append("    0 :: numeric ");
		sb.append("  ) qt_dia_ausencia, ");
		sb.append("  coalesce( ");
		sb.append("    ( ");
		sb.append("      select ");
		sb.append("        sum(ase1.qt_dia_servico_extra) :: numeric ");
		sb.append("      from ");
		sb.append("        gct.gcttb166_ausencia_servico_extra ase1 ");
		sb.append("      where ");
		sb.append("        ase1.nu_equipe = e.nu_equipe ");
		sb.append("        and ase1.nu_ciclo_trabalho = ct.nu_ciclo_trabalho ");
		sb.append("    ), ");
		sb.append("    0 :: numeric ");
		sb.append("  ) qt_dia_servico_extra ");
		sb.append("FROM ");
		sb.append("  gct.gcttb087_equipe e ");
		sb.append("  INNER JOIN gct.gcttb010_contrato c ON c.nu_contrato = e.nu_contrato ");
		sb.append("  INNER JOIN gct.gcttb102_fatura f ON f.nu_fatura = ? ");
		sb.append("  INNER JOIN gct.gcttb088_ciclo_trabalho ct ON ct.nu_equipe = e.nu_equipe ");
		sb.append("  inner join gct.gcttb086_solicitacao_equipe se on ( ");
		sb.append("    se.nu_equipe = ct.nu_equipe ");
		sb.append("    and se.nu_ciclo_trabalho = ct.nu_ciclo_trabalho ");
		sb.append("  ) ");
		sb.append("  inner join gct.gcttb055_servico_contrato sc on ( ");
		sb.append("    sc.nu_tipo_servico = se.nu_tipo_servico ");
		sb.append("    and e.nu_contrato = sc.nu_contrato ");
		sb.append("  ) ");
		sb.append("WHERE ");
		sb.append("  e.nu_equipe = ? ");
		sb.append("  AND cast(ct.dt_alvo_ciclo_trabalho AS date) BETWEEN f.dt_inicio_fatura AND f.dt_final_fatura ");
		sb.append("  AND ct.co_falha_ciclo_trabalho = 'NF' "); 
		sb.append("  AND ct.nu_situacao_ciclo_trabalho = 2 ");
		sb.append("  AND sc.ic_servico_compoe_calculo_fns = 'S' ");
		sb.append("ORDER BY ");
		sb.append("  ct.dt_alvo_ciclo_trabalho ");

		return sb.toString();
	}

}
