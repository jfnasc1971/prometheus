package org.prometheus.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.prometheus.dto.EntregaDemandaDTO;
import org.prometheus.dto.OrdemServicoDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;

@Component
public class EntregaDemandaService {

	@Autowired
	private DatabaseService databaseService;

	@Autowired
	private OrdemServicoService ordemServicoService;

	public Long getQtDiasAtraso(EntregaDemandaDTO entrega) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDMLQtDiasAtraso());
			pstmt.setLong(1, entrega.getNuEntrega());

			rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getLong("qt_dias_atraso");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return 0L;
	}
	
	public BigDecimal getVrPreco(EntregaDemandaDTO entrega) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDMLPreco());
			pstmt.setLong(1, entrega.getNuEntrega());

			rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getBigDecimal("vr_preco");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return BigDecimal.ZERO;
	}
	
	public List<EntregaDemandaDTO> listar(OrdemServicoDTO os) {
		List<EntregaDemandaDTO> result = new ArrayList<EntregaDemandaDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setLong(1, os.getNuOrdemServico());

			rs = pstmt.executeQuery();

			while (rs.next()) {

				EntregaDemandaDTO dto = new EntregaDemandaDTO();

				dto.setCoDemanda(rs.getString("co_demanda"));
				dto.setNuDemanda(rs.getLong("nu_demanda"));
				dto.setNuOrdemServico(rs.getLong("nu_ordem_servico"));
				dto.setNuEntrega(rs.getLong("nu_entrega"));
				dto.setCoEntrega(rs.getString("co_entrega"));
				dto.setPcEntrega(rs.getBigDecimal("pc_entrega"));
				dto.setPcExecutado(rs.getBigDecimal("pc_executado"));
				dto.setDhPrevistaInicioAtendimento(rs.getTimestamp("dh_prevista_inicio_atendimento"));
				dto.setDhPrevistaEntrega(rs.getTimestamp("dh_prevista_entrega"));
				dto.setQtDiasPendencia(rs.getLong("qt_dias_pendencia"));
				dto.setDhPrevistaEntregaPendencia(rs.getTimestamp("dh_prevista_entrega_pendencia"));
				dto.setDhEfetivaEntrega(rs.getTimestamp("dh_efetiva_entrega"));
				dto.setVrPrevistoEntrega(rs.getBigDecimal("vr_previsto_entrega"));

				dto.setVrPreco(getVrPreco(dto));
				dto.setVrMensuracaoEfetiva(ordemServicoService.getVrMensuracaoEfetiva(dto.getNuOrdemServico()));
				dto.setQtDiasPendencia(getQtDiasAtraso(dto));
				
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

		sb.append(" select");
		sb.append("   d.co_demanda,");
		sb.append("   d.nu_demanda,");
		sb.append("   os.nu_ordem_servico,");
		sb.append("   e.nu_entrega,");
		sb.append("   e.co_entrega,");
		sb.append("   e.pc_entrega / cast(100 as numeric) as pc_entrega,");
		sb.append("   e.pc_executado / cast(100 as numeric) as pc_executado,");
		sb.append("   e.dh_prevista_inicio_atendimento,");
		sb.append("   e.dh_prevista_entrega,");
		sb.append("   coalesce(pe.qt_dias_pendencia,0) qt_dias_pendencia,");
		sb.append("   e.dh_prevista_entrega + ");
		sb.append("     (coalesce(pe.qt_dias_pendencia,0) * interval '1 day') dh_prevista_entrega_pendencia,");
		sb.append("   e.dh_efetiva_entrega,");
		sb.append("   e.vr_previsto_entrega");
		sb.append(" from");
		sb.append("   gct.gcttb021_demanda d");
		sb.append("   inner join gct.gcttb023_ordem_servico os on os.nu_demanda = d.nu_demanda");
		sb.append("   inner join gct.gcttb026_entrega e on e.nu_ordem_servico = os.nu_ordem_servico");
		sb.append("   left join (");
		sb.append("     select");
		sb.append("       nu_entrega,");
		sb.append("       floor(sum(extract(epoch from ");
		sb.append("         (ts_final_pendencia - ts_inicio_pendencia)) / 86400)) qt_dias_pendencia");
		sb.append("     from");
		sb.append("       gct.gcttb058_pendencia_entrega pe");
		sb.append("     where");
		sb.append("       1 = 1");
		sb.append("       and pe.ts_final_pendencia is not null");
		sb.append("       and pe.ic_pendencia_procede = 'S'");
		sb.append("     group by");
		sb.append("       pe.nu_entrega");
		sb.append("   ) pe on pe.nu_entrega = e.nu_entrega");
		sb.append(" where");
		sb.append("   e.nu_ordem_servico = ?");
		sb.append(" order by");
		sb.append("   e.co_entrega;");

		return sb.toString();
	}

	public String getDMLPreco() {

		StringBuilder sb = new StringBuilder();

		sb.append("select "); 
		sb.append("  coalesce(pf.vr_preco,0) vr_preco "); 
		sb.append("from "); 
		sb.append("  gct.gcttb021_demanda d "); 
		sb.append("  inner join gct.gcttb023_ordem_servico os on os.nu_demanda = d.nu_demanda "); 
		sb.append("  inner join gct.gcttb026_entrega e on e.nu_ordem_servico = os.nu_ordem_servico "); 
		sb.append("  inner join gct.gcttb054_preco_forma pf on ( "); 
		sb.append("    pf.nu_contrato = os.nu_contrato "); 
		sb.append("    and pf.nu_tipo_grupo = os.nu_tipo_grupo "); 
		sb.append("    and pf.nu_tipo_servico = os.nu_tipo_servico "); 
		sb.append("    and pf.nu_tipo_forma = os.nu_tipo_forma "); 
		sb.append("  ) "); 
		sb.append("  left join ( "); 
		sb.append("    select "); 
		sb.append("      nu_entrega, "); 
		sb.append("      floor(sum(extract(epoch from (ts_final_pendencia - ts_inicio_pendencia)) / 86400)) qt_dias_pendencia "); 
		sb.append("    from "); 
		sb.append("      gct.gcttb058_pendencia_entrega pe "); 
		sb.append("    where "); 
		sb.append("      1 = 1 "); 
		sb.append("      and pe.ts_final_pendencia is not null "); 
		sb.append("      and pe.ic_pendencia_procede = 'S' "); 
		sb.append("    group by "); 
		sb.append("      pe.nu_entrega "); 
		sb.append("  ) pe on pe.nu_entrega = e.nu_entrega "); 
		sb.append("where "); 
		sb.append("  e.nu_entrega = ? "); 
		sb.append("  and (e.dh_prevista_entrega + coalesce(qt_dias_pendencia,0) * interval '1 day') "); 
		sb.append("        between pf.dt_inicio_vigencia and pf.dt_final_vigencia "); 
		sb.append("order by "); 
		sb.append("  e.co_entrega; ");

		return sb.toString();
	}

	public String getDMLQtDiasAtraso() {

		StringBuilder sb = new StringBuilder();

		sb.append("select "); 
		sb.append("  e.dh_prevista_entrega, e.dh_efetiva_entrega, "); 
		sb.append("  floor( "); 
		sb.append("    (extract(epoch from e.dh_efetiva_entrega) - "); 
		sb.append("       extract(epoch from (coalesce(pe.qt_dias_pendencia,0) * ");
		sb.append("         interval '1 day' + e.dh_prevista_entrega)) "); 
		sb.append("    ) / 84400 "); 
		sb.append("  ) qt_dias_atraso "); 
		sb.append("from "); 
		sb.append("  gct.gcttb021_demanda d "); 
		sb.append("  inner join gct.gcttb023_ordem_servico os on os.nu_demanda = d.nu_demanda "); 
		sb.append("  inner join gct.gcttb026_entrega e on e.nu_ordem_servico = os.nu_ordem_servico "); 
		sb.append("  left join ( "); 
		sb.append("    select "); 
		sb.append("      nu_entrega, "); 
		sb.append("      floor(sum(extract(epoch from (ts_final_pendencia - ts_inicio_pendencia)) ");
		sb.append("         / 86400)) qt_dias_pendencia "); 
		sb.append("    from "); 
		sb.append("      gct.gcttb058_pendencia_entrega pe "); 
		sb.append("    where "); 
		sb.append("      1 = 1 "); 
		sb.append("      and pe.ts_final_pendencia is not null "); 
		sb.append("      and pe.ic_pendencia_procede = 'S' "); 
		sb.append("    group by "); 
		sb.append("      pe.nu_entrega "); 
		sb.append("  ) pe on pe.nu_entrega = e.nu_entrega "); 
		sb.append("where "); 
		sb.append("  e.nu_entrega = ? "); 
		sb.append("  and e.dh_efetiva_entrega is not null "); 
		sb.append("  and e.dh_efetiva_entrega > (coalesce(pe.qt_dias_pendencia,0) * ");
		sb.append("        interval '1 day' + e.dh_prevista_entrega) ");

		return sb.toString();
	}

}
