package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.prometheus.dto.TipoQuantidadeDemandaDTO;

import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;

@Component
public class TipoQuantidadeDemandaService {

	@Autowired
	private DatabaseService databaseService;

	public List<TipoQuantidadeDemandaDTO> listar(String coDemanda) {
		List<TipoQuantidadeDemandaDTO> result = new ArrayList<TipoQuantidadeDemandaDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());
			pstmt.setString(1, coDemanda);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				TipoQuantidadeDemandaDTO dto = new TipoQuantidadeDemandaDTO();

				dto.setNuTipoQuantidade(rs.getLong("nu_tipo_quantidade"));
				dto.setNoTipoQuantidade(rs.getString("no_tipo_quantidade"));
				dto.setVrAjusteFinanceiro(rs.getBigDecimal("vr_ajuste_financeiro"));

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
		sb.append("   distinct tq.nu_tipo_quantidade,");
		sb.append("   tq.no_tipo_quantidade,");
		sb.append("   tq.vr_ajuste_financeiro");
		sb.append(" from");
		sb.append("   gct.gcttb021_demanda d");
		sb.append("   inner join gct.gcttb023_ordem_servico os on os.nu_demanda = d.nu_demanda");
		sb.append("   inner join gct.gcttb051_quantidade_ordem_serv qos on qos.nu_ordem_servico = os.nu_ordem_servico");
		sb.append("   inner join gct.gcttb049_tipo_quantidade tq on tq.nu_tipo_quantidade = qos.nu_tipo_quantidade");
		sb.append(" where");
		sb.append("   d.co_demanda = ?");

		return sb.toString();
	}

}
