package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.prometheus.dto.SituacaoEntregaIndicadorDTO;

import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;

@Component
public class SituacaoEntregaIndicadorService {

	@Autowired
	private DatabaseService databaseService;

	public List<SituacaoEntregaIndicadorDTO> listar(Long nuEquipe) {

		List<SituacaoEntregaIndicadorDTO> result = new ArrayList<SituacaoEntregaIndicadorDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setLong(1, nuEquipe);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				SituacaoEntregaIndicadorDTO dto = new SituacaoEntregaIndicadorDTO();

				dto.setSgTipoIndicador(rs.getString("sg_tipo_indicador"));
				dto.setNoTipoIndicador(rs.getString("no_tipo_indicador"));
				dto.setNoAgrupador(rs.getString("no_agrupador"));
				dto.setNuSituacaoAtendimento(rs.getLong("nu_situacao_atendimento"));
				dto.setNoSituacaoAtendimento(rs.getString("no_situacao_atendimento"));
				dto.setNuContrato(rs.getLong("nu_contrato"));
				dto.setNuTipoGrupo(rs.getLong("nu_tipo_grupo"));
				dto.setNuEquipe(rs.getLong("nu_equipe"));
				dto.setNuTipoIndicador(rs.getLong("nu_tipo_indicador"));

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
		sb.append("  * "); 
		sb.append("from "); 
		sb.append("  ( "); 
		sb.append("    select "); 
		sb.append("      ti.sg_tipo_indicador, "); 
		sb.append("      ti.no_tipo_indicador, "); 
		sb.append("      '-' as no_agrupador, "); 
		sb.append("      sa.nu_situacao_atendimento, "); 
		sb.append("      sa.no_situacao_atendimento, "); 
		sb.append("      gis.nu_contrato, "); 
		sb.append("      gis.nu_tipo_grupo, "); 
		sb.append("      e.nu_equipe, "); 
		sb.append("      ti.nu_tipo_indicador "); 
		sb.append("    from "); 
		sb.append("      gct.gcttb174_grupo_indicador_situacao gis "); 
		sb.append("      inner join gct.gcttb112_tipo_indicador ti on ti.nu_tipo_indicador = gis.nu_tipo_indicador "); 
		sb.append("      inner join gct.gcttb092_situacao_atendimento sa on gis.nu_situacao_atendimento = sa.nu_situacao_atendimento "); 
		sb.append("      inner join gct.gcttb087_equipe e on ( "); 
		sb.append("        gis.nu_contrato = e.nu_contrato "); 
		sb.append("        and gis.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      ) "); 
		sb.append("    union "); 
		sb.append("    select "); 
		sb.append("      ti.sg_tipo_indicador, "); 
		sb.append("      ti.no_tipo_indicador, "); 
		sb.append("      'Serviços Entregues' :: text no_agrupador, "); 
		sb.append("      sa.nu_situacao_atendimento, "); 
		sb.append("      sa.no_situacao_atendimento, "); 
		sb.append("      e.nu_contrato, "); 
		sb.append("      e.nu_tipo_grupo, "); 
		sb.append("      e.nu_equipe, "); 
		sb.append("      ti.nu_tipo_indicador "); 
		sb.append("    from "); 
		sb.append("      gct.gcttb087_equipe e "); 
		sb.append("      inner join gct.gcttb033_grupo_contrato gc on gc.nu_contrato = e.nu_contrato "); 
		sb.append("      and gc.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      inner join gct.gcttb191_servico_entregue se on ( "); 
		sb.append("        se.nu_contrato = gc.nu_contrato "); 
		sb.append("        and se.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      ) "); 
		sb.append("      inner join gct.gcttb092_situacao_atendimento sa on se.nu_situacao_atendimento = sa.nu_situacao_atendimento "); 
		sb.append("      inner join gct.gcttb112_tipo_indicador ti on ti.nu_tipo_indicador = se.nu_tipo_indicador "); 
		sb.append("    union "); 
		sb.append("    select "); 
		sb.append("      ti.sg_tipo_indicador, "); 
		sb.append("      ti.no_tipo_indicador, "); 
		sb.append("      'Serviços Contratatados' :: text no_agrupador, "); 
		sb.append("      sa.nu_situacao_atendimento, "); 
		sb.append("      sa.no_situacao_atendimento, "); 
		sb.append("      e.nu_contrato, "); 
		sb.append("      e.nu_tipo_grupo, "); 
		sb.append("      e.nu_equipe, "); 
		sb.append("      ti.nu_tipo_indicador "); 
		sb.append("    from "); 
		sb.append("      gct.gcttb087_equipe e "); 
		sb.append("      inner join gct.gcttb033_grupo_contrato gc on gc.nu_contrato = e.nu_contrato "); 
		sb.append("      and gc.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      inner join gct.gcttb192_servico_contratado se on ( "); 
		sb.append("        se.nu_contrato = gc.nu_contrato "); 
		sb.append("        and se.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      ) "); 
		sb.append("      inner join gct.gcttb092_situacao_atendimento sa on se.nu_situacao_atendimento = sa.nu_situacao_atendimento "); 
		sb.append("      inner join gct.gcttb112_tipo_indicador ti on ti.nu_tipo_indicador = se.nu_tipo_indicador "); 
		sb.append("    union "); 
		sb.append("    select "); 
		sb.append("      ti.sg_tipo_indicador, "); 
		sb.append("      ti.no_tipo_indicador, "); 
		sb.append("      'Serviços Atrasados' :: text no_agrupador, "); 
		sb.append("      sa.nu_situacao_atendimento, "); 
		sb.append("      sa.no_situacao_atendimento, "); 
		sb.append("      e.nu_contrato, "); 
		sb.append("      e.nu_tipo_grupo, "); 
		sb.append("      e.nu_equipe, "); 
		sb.append("      ti.nu_tipo_indicador "); 
		sb.append("    from "); 
		sb.append("      gct.gcttb087_equipe e "); 
		sb.append("      inner join gct.gcttb033_grupo_contrato gc on gc.nu_contrato = e.nu_contrato "); 
		sb.append("      and gc.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      inner join gct.gcttb193_servico_atraso se on ( "); 
		sb.append("        se.nu_contrato = gc.nu_contrato "); 
		sb.append("        and se.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      ) "); 
		sb.append("      inner join gct.gcttb092_situacao_atendimento sa on se.nu_situacao_atendimento = sa.nu_situacao_atendimento "); 
		sb.append("      inner join gct.gcttb112_tipo_indicador ti on ti.nu_tipo_indicador = se.nu_tipo_indicador "); 
		sb.append("    union "); 
		sb.append("    select "); 
		sb.append("      ti.sg_tipo_indicador, "); 
		sb.append("      ti.no_tipo_indicador, "); 
		sb.append("      'Serviços Devolvidos' :: text no_agrupador, "); 
		sb.append("      sa.nu_situacao_atendimento, "); 
		sb.append("      sa.no_situacao_atendimento, "); 
		sb.append("      e.nu_contrato, "); 
		sb.append("      e.nu_tipo_grupo, "); 
		sb.append("      e.nu_equipe, "); 
		sb.append("      ti.nu_tipo_indicador "); 
		sb.append("    from "); 
		sb.append("      gct.gcttb087_equipe e "); 
		sb.append("      inner join gct.gcttb033_grupo_contrato gc on gc.nu_contrato = e.nu_contrato "); 
		sb.append("      and gc.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      inner join gct.gcttb194_servico_devolvido se on se.nu_contrato = gc.nu_contrato "); 
		sb.append("      and se.nu_tipo_grupo = e.nu_tipo_grupo "); 
		sb.append("      inner join gct.gcttb092_situacao_atendimento sa on se.nu_situacao_atendimento = sa.nu_situacao_atendimento "); 
		sb.append("      inner join gct.gcttb112_tipo_indicador ti on ti.nu_tipo_indicador = se.nu_tipo_indicador "); 
		sb.append("  ) tb "); 
		sb.append("where "); 
		sb.append("  nu_equipe = ? "); 
		sb.append("order by "); 
		sb.append("  no_tipo_indicador, "); 
		sb.append("  no_agrupador, ");
		sb.append("  no_situacao_atendimento ");

		return sb.toString();
	}

}
