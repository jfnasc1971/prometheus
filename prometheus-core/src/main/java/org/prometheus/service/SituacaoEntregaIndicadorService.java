package org.prometheus.service;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.prometheus.dto.SituacaoEntregaIndicadorDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

				dto.setNuContrato(rs.getLong("nu_contrato"));
				dto.setNuTipoGrupo(rs.getLong("nu_tipo_grupo"));
				dto.setNuSituacaoAtendimento(rs.getLong("nu_situacao_atendimento"));
				dto.setNoSituacaoAtendimento(rs.getString("no_situacao_atendimento"));
				dto.setNuTipoIndicador(rs.getLong("nu_tipo_indicador"));
				dto.setSgTipoIndicador(rs.getString("sg_tipo_indicador"));
				dto.setNoTipoIndicador(rs.getString("no_tipo_indicador"));

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
		sb.append("   gi.nu_contrato,");
		sb.append("   gi.nu_tipo_grupo,");
		sb.append("   gi.nu_situacao_atendimento,");
		sb.append("   sa.no_situacao_atendimento,");
		sb.append("   ti.nu_tipo_indicador,");
		sb.append("   ti.sg_tipo_indicador,");
		sb.append("   ti.no_tipo_indicador");
		sb.append(" from");
		sb.append("   gct.gcttb174_grupo_indicador_situacao gi");
		sb.append("   inner join gct.gcttb087_equipe e on e.nu_contrato = gi.nu_contrato");
		sb.append("   inner join gct.gcttb112_tipo_indicador ti on ti.nu_tipo_indicador = gi.nu_tipo_indicador");
		sb.append("   inner join gct.gcttb092_situacao_atendimento sa on sa.nu_situacao_atendimento = gi.nu_situacao_atendimento");
		sb.append(" where");
		sb.append("   1=1");
		sb.append("   and e.nu_equipe = ?");
		sb.append("   and gi.nu_contrato = e.nu_contrato ");
		sb.append("   and gi.nu_tipo_grupo = e.nu_tipo_grupo ");
		sb.append("   and upper(gi.no_situacao_atendimento) = upper('SERVICOS_CONTRATADOS')");
		sb.append(" order by");
		sb.append("   gi.nu_contrato,");
		sb.append("   gi.nu_tipo_grupo,");
		sb.append("   gi.nu_tipo_indicador,");
		sb.append("   gi.nu_situacao_atendimento");

		return sb.toString();
	}

}
