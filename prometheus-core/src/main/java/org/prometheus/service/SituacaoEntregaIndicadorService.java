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

		sb.append(" select");
		sb.append("   ti.sg_tipo_indicador,");
		sb.append("   ti.no_tipo_indicador,");
		sb.append("   gi.no_situacao_atendimento no_agrupador,");
		sb.append("   sa.nu_situacao_atendimento,");
		sb.append("   sa.no_situacao_atendimento,");
		sb.append("   gi.nu_contrato,");
		sb.append("   gi.nu_tipo_grupo,");
		sb.append("   e.nu_equipe,");
		sb.append("   ti.nu_tipo_indicador");
		sb.append(" from");
		sb.append("   gct.gcttb174_grupo_indicador_situacao gi");
		sb.append("   inner join gct.gcttb087_equipe e on e.nu_contrato = gi.nu_contrato");
		sb.append("   and e.nu_tipo_grupo = gi.nu_tipo_grupo");
		sb.append("   inner join gct.gcttb112_tipo_indicador ti on ti.nu_tipo_indicador = gi.nu_tipo_indicador");
		sb.append("   inner join gct.gcttb092_situacao_atendimento sa ");
		sb.append("     on sa.nu_situacao_atendimento = gi.nu_situacao_atendimento");
		sb.append(" where");
		sb.append("   e.nu_equipe = ?");
		sb.append(" order by");
		sb.append("   ti.sg_tipo_indicador,");
		sb.append("   ti.no_tipo_indicador,");
		sb.append("   gi.no_situacao_atendimento,");
		sb.append("   sa.no_situacao_atendimento");

		return sb.toString();
	}

}
