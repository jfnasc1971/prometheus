package org.prometheus.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.TermoDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TermoService {

	@Autowired
	private DatabaseService databaseService;

	public BigDecimal valorPago(Long nuFatura, Long nuEquipe) {
		
		BigDecimal result = BigDecimal.ZERO;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDMLValorPago());
			pstmt.setLong(1, nuFatura);
			pstmt.setLong(2, nuEquipe);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getBigDecimal("vr_financeiro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return result;
	}
	
	public List<TermoDTO> listar(Long nuEquipe) {
		List<TermoDTO> result = new ArrayList<TermoDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setLong(1, nuEquipe);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				TermoDTO dto = new TermoDTO();

				dto.setNuFaturaGerada(rs.getLong("nu_fatura_gerada"));
				dto.setDeFatura(rs.getString("de_fatura"));
				dto.setNuFaturaPaga(rs.getLong("nu_fatura_paga"));
				dto.setDeFaturaPaga(rs.getString("de_fatura_paga"));
				dto.setNuEquipe(rs.getLong("nu_equipe"));
				dto.setDtInicioAtendimento(rs.getTimestamp("dt_inicio_atendimento"));
				dto.setNuTermoEquipe(rs.getLong("nu_termo_equipe"));
				dto.setNuTipoTermo(rs.getLong("nu_tipo_termo"));
				dto.setCoTermo(rs.getString("co_termo"));
				dto.setDhEmissaoTermo(rs.getTimestamp("dh_emissao_termo"));
				dto.setQtMembroEquipeVigente(rs.getBigDecimal("qt_membro_equipe_vigente"));
				dto.setVrPrevistoEquipe(rs.getBigDecimal("vr_previsto_equipe"));
				dto.setDiaServicoExtra(rs.getBigDecimal("dia_servico_extra"));
				dto.setVrAcrescimo(rs.getBigDecimal("vr_acrescimo"));
				dto.setQtDiaAusencia(rs.getBigDecimal("qt_dia_ausencia"));
				dto.setVrDecrescimo(rs.getBigDecimal("vr_decrescimo"));
				dto.setVrFinanceiroTermoOriginal(rs.getBigDecimal("vr_financeiro_termo_original"));
				dto.setVrFns(rs.getBigDecimal("vr_fns"));
				dto.setVrFinanceiroTermo(rs.getBigDecimal("vr_financeiro_termo"));
				dto.setVrPrecoGrupo(rs.getBigDecimal("vr_preco_grupo"));

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

		sb.append(" select "); 
		sb.append("     t.nu_fatura_gerada, ");
		sb.append("     e.dt_inicio_atendimento, ");
		sb.append("     f.de_fatura, "); 
		sb.append("     t.nu_fatura_paga, "); 
		sb.append("     fp.de_fatura de_fatura_paga, "); 
		sb.append("     e.nu_equipe, "); 
		sb.append("     t.nu_tipo_termo, "); 
		sb.append("     t.nu_termo_equipe, "); 
		sb.append("     t.co_termo, "); 
		sb.append("     t.dh_emissao_termo, "); 
		sb.append("     t.qt_membro_equipe_vigente, "); 
		sb.append("     coalesce(t.vr_previsto_equipe, 0 :: numeric) as vr_previsto_equipe, "); 
		sb.append("     coalesce(t.qt_dia_servico_extra, 0 :: numeric) as dia_servico_extra, "); 
		sb.append("     coalesce(t.vr_acrescimo, 0 :: numeric) as vr_acrescimo, "); 
		sb.append("     coalesce(t.qt_dia_ausencia, 0 :: numeric) as qt_dia_ausencia, "); 
		sb.append("     coalesce(t.vr_decrescimo, 0 :: numeric) as vr_decrescimo, "); 
		sb.append("     t.vr_financeiro_termo_original, "); 
		sb.append("     vie.vr_faixa_fns as vr_fns, "); 
		sb.append("     t.vr_financeiro_termo, "); 
		sb.append("     t.vr_preco_grupo "); 
		sb.append(" from "); 
		sb.append("     gct.gcttb087_equipe e "); 
		sb.append("     inner join gct.gcttb170_termo_equipe t on t.nu_equipe = e.nu_equipe "); 
		sb.append("     inner join gct.gcttb102_fatura f on f.nu_fatura = t.nu_fatura_gerada "); 
		sb.append("     inner join gct.gcttb102_fatura fp on fp.nu_fatura = t.nu_fatura_paga "); 
		sb.append("     left join gct.gcttb172_valor_indicador_equipe vie on ( "); 
		sb.append("       vie.nu_equipe = e.nu_equipe "); 
		sb.append("       and vie.nu_fatura = t.nu_fatura_gerada "); 
		sb.append("       and vie.nu_tipo_indicador = 21 "); 
		sb.append("     ) "); 
		sb.append(" where "); 
		sb.append("     e.nu_equipe = ? "); 
		sb.append("     and t.nu_situacao_termo in (1, 3) "); 
		sb.append("     and t.nu_tipo_termo in (11, 12) "); 
		sb.append(" order by "); 
		sb.append("     f.dt_final_fatura ");

		return sb.toString();
	}

	public String getDMLValorPago() {

		StringBuilder sb = new StringBuilder();
		
		sb.append("select "); 
		sb.append("  coalesce(sum(t.vr_financeiro_termo),0)::numeric as vr_financeiro "); 
		sb.append("from "); 
		sb.append("  gct.gcttb170_termo_equipe t "); 
		sb.append("where "); 
		sb.append("  t.nu_fatura_paga = ? "); 
		sb.append("  and t.nu_equipe = ? "); 
		sb.append("  and t.nu_tipo_termo in (11, 12) "); 
		sb.append("  and t.nu_situacao_pagamento = 1 "); 
		sb.append("  and t.nu_situacao_termo = 1 ");
		
		return sb.toString();
	}
}
