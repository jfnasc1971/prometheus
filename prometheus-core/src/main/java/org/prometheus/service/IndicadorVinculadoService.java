package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.prometheus.dto.IndicadorVinculadoDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndicadorVinculadoService {

	@Autowired
	private DatabaseService databaseService;

	public List<IndicadorVinculadoDTO> listar(Long nuContrato, Long nuTipoGrupo, Date dtFinalFatura,
			Long nuTipoIndicador) {

		List<IndicadorVinculadoDTO> result = new ArrayList<IndicadorVinculadoDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setLong(1, nuTipoIndicador);
			pstmt.setLong(2, nuContrato);
			pstmt.setLong(3, nuTipoGrupo);
			pstmt.setDate(4, new java.sql.Date(dtFinalFatura.getTime()));

			rs = pstmt.executeQuery();

			while (rs.next()) {

				IndicadorVinculadoDTO dto = new IndicadorVinculadoDTO();

				dto.setNuEquipe(rs.getLong("nu_equipe"));
				dto.setCoEquipe(rs.getString("co_equipe"));
				dto.setNoTipoGrupo(rs.getString("no_tipo_grupo"));
				dto.setSgTipoIndicador(rs.getString("sg_tipo_indicador"));
				dto.setNoTipoIndicador(rs.getString("no_tipo_indicador"));
				dto.setDtReferencia(rs.getDate("dt_referencia"));
				dto.setVrCalculoIndicador(rs.getBigDecimal("vr_calculo_indicador"));
				dto.setVrFaixaFns(rs.getBigDecimal("vr_faixa_fns"));
				dto.setDhGeracaoIndicadorEquipe(rs.getTimestamp("dh_geracao_indicador_equipe"));
				dto.setNuAjuste(rs.getLong("nu_ajuste"));

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

		sb.append("select "); 
		sb.append("  e.nu_equipe, "); 
		sb.append("  e.co_equipe, "); 
		sb.append("  tg.no_tipo_grupo, "); 
		sb.append("  ti.sg_tipo_indicador, "); 
		sb.append("  ti.no_tipo_indicador, "); 
		sb.append("  vie.dt_referencia, "); 
		sb.append("  vie.vr_calculo_indicador, "); 
		sb.append("  vie.vr_faixa_fns, "); 
		sb.append("  vie.dh_geracao_indicador_equipe, "); 
		sb.append("  vie.nu_ajuste "); 
		sb.append("from "); 
		sb.append("  gct.gcttb173_grupo_indicador_vinculo giv "); 
		sb.append("  inner join gct.gctvw052_valor_indicador_equipe vie on ( "); 
		sb.append("    giv.nu_tipo_grupo_vinculado = vie.nu_tipo_grupo "); 
		sb.append("    and giv.nu_contrato_vinculado = vie.nu_contrato "); 
		sb.append("    and giv.nu_tipo_indicador_vinculado = vie.nu_tipo_indicador "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb087_equipe e on e.nu_equipe = vie.nu_equipe "); 
		sb.append("  inner join gct.gcttb010_contrato c on c.nu_contrato = vie.nu_contrato "); 
		sb.append("  inner join gct.gcttb001_area_interna ai on ai.nu_area_interna = e.nu_area_interna "); 
		sb.append("  inner join gct.gcttb032_tipo_grupo tg on tg.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("  inner join gct.gcttb102_fatura f on ( "); 
		sb.append("    f.nu_faturamento = c.nu_faturamento "); 
		sb.append("    and f.co_sitio = ai.co_sitio "); 
		sb.append("    and vie.dt_referencia = f.dt_final_fatura "); 
		sb.append("  ) "); 
		sb.append("  inner join gct.gcttb112_tipo_indicador ti on ( "); 
		sb.append("    ti.nu_tipo_indicador = giv.nu_tipo_indicador_vinculado "); 
		sb.append("  ) "); 
		sb.append("where "); 
		sb.append("  vie.vr_calculo_indicador is not null ");
		sb.append("  and giv.nu_tipo_indicador = ? "); 
		sb.append("  and giv.nu_contrato = ? "); 
		sb.append("  and giv.nu_tipo_grupo = ? "); 
		sb.append("  and f.dt_final_fatura = ? "); 
		sb.append("order by "); 
		sb.append("  e.co_equipe ");

		return sb.toString();
	}

}
