package org.prometheus.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.OrdemServicoDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrdemServicoService {

	@Autowired
	private DatabaseService databaseService;

	public BigDecimal getVrMensuracaoEfetiva(Long nuOrdemServico) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDMLMensuracaoEfetiva());
			pstmt.setLong(1, nuOrdemServico);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getBigDecimal("vr_mensuracao_efetiva");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return BigDecimal.ZERO;
	}	
	
	public List<OrdemServicoDTO> listar(String coDemanda) {
		List<OrdemServicoDTO> result = new ArrayList<OrdemServicoDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setString(1, coDemanda);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				OrdemServicoDTO dto = new OrdemServicoDTO();

				dto.setNuOrdemServico(rs.getLong("nu_ordem_servico"));
				dto.setCoOrdemServico(rs.getString("co_ordem_servico"));
				dto.setNuTipoGrupo(rs.getLong("nu_tipo_grupo"));
				dto.setNoTipoGrupo(rs.getString("no_tipo_grupo"));
				dto.setNuTipoServico(rs.getLong("nu_tipo_servico"));
				dto.setNoTipoServico(rs.getString("no_tipo_servico"));
				dto.setNuTipoForma(rs.getLong("nu_tipo_forma"));
				dto.setDeTipoForma(rs.getString("de_tipo_forma"));
				dto.setVrEstmoOrdemServico(rs.getBigDecimal("vr_estmo_ordem_servico"));

				result.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return result;
	}

	public String getDMLMensuracaoEfetiva() {

		StringBuilder sb = new StringBuilder();

		sb.append("select sum(tq.vr_ajuste_financeiro * qos.qt_solicitacao) vr_mensuracao_efetiva "); 
		sb.append("from gct.gcttb021_demanda d "); 
		sb.append("inner join gct.gcttb023_ordem_servico os on os.nu_demanda = d.nu_demanda "); 
		sb.append("inner join gct.gcttb051_quantidade_ordem_serv qos on qos.nu_ordem_servico = os.nu_ordem_servico "); 
		sb.append("inner join gct.gcttb049_tipo_quantidade tq on tq.nu_tipo_quantidade = qos.nu_tipo_quantidade "); 
		sb.append("where os.nu_ordem_servico = ? ");

		return sb.toString();
	}
	
	public String getDML() {

		StringBuilder sb = new StringBuilder();

		sb.append(" select");
		sb.append("   os.nu_ordem_servico,");
		sb.append("   os.co_ordem_servico,");
		sb.append("   tg.nu_tipo_grupo,");
		sb.append("   tg.no_tipo_grupo,");
		sb.append("   ts.nu_tipo_servico,");
		sb.append("   ts.no_tipo_servico,");
		sb.append("   tf.nu_tipo_forma,");
		sb.append("   tf.de_tipo_forma,");
		sb.append("   os.vr_estmo_ordem_servico");
		sb.append(" from");
		sb.append("   gct.gcttb021_demanda d");
		sb.append("   inner join gct.gcttb023_ordem_servico os on os.nu_demanda = d.nu_demanda");
		sb.append("   inner join gct.gcttb032_tipo_grupo tg on tg.nu_tipo_grupo = os.nu_tipo_grupo");
		sb.append("   inner join gct.gcttb015_tipo_servico ts on ts.nu_tipo_servico = os.nu_tipo_servico");
		sb.append("   inner join gct.gcttb016_tipo_forma tf on tf.nu_tipo_forma = os.nu_tipo_forma");
		sb.append(" where");
		sb.append("   d.co_demanda = ?");

		return sb.toString();
	}

}
