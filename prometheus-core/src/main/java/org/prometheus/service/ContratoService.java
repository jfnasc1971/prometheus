package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.ContratoDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContratoService {

	@Autowired
	private DatabaseService databaseService;

	public List<ContratoDTO> listar() {
		List<ContratoDTO> result = new ArrayList<ContratoDTO>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());

			rs = pstmt.executeQuery();

			while (rs.next()) {

				ContratoDTO dto = new ContratoDTO();

				dto.setNuContrato(rs.getLong("nu_contrato"));
				dto.setNuFornecedor(rs.getLong("nu_fornecedor"));
				dto.setDtInicioCompromisso(rs.getDate("dt_inicio_compromisso"));
				dto.setDtFinalCompromisso(rs.getDate("dt_final_compromisso"));
				dto.setDtInicioFatura(rs.getDate("dt_inicio_fatura"));
				dto.setDtFinalFatura(rs.getDate("dt_final_fatura"));
				dto.setCoLicitacao(rs.getString("co_licitacao"));
				dto.setCoContrato(rs.getString("co_contrato"));
				dto.setVrGlobalContrato(rs.getBigDecimal("vr_global_contrato"));
				dto.setCoProcesso(rs.getString("co_processo"));
				dto.setCoCompromisso(rs.getString("co_compromisso"));
				dto.setDeContrato(rs.getString("de_contrato"));
				dto.setNuObjeto(rs.getLong("nu_objeto"));
				dto.setNoContrato(rs.getString("no_contrato"));
				dto.setPzGarantiaContrato(rs.getBigDecimal("pz_garantia_contrato"));
				dto.setPzVigenciaContrato(rs.getBigDecimal("pz_vigencia_contrato"));
				dto.setQtVolumeEstimado(rs.getBigDecimal("qt_volume_estimado"));
				dto.setDtAssinaturaContrato(rs.getDate("dt_assinatura_contrato"));
				dto.setDhCadastro(rs.getDate("dh_cadastro"));
				dto.setNuMtrcaCadastro(rs.getLong("nu_mtrca_cadastro"));
				dto.setPcPrazoValidacaoEntrega(rs.getBigDecimal("pc_prazo_validacao_entrega"));
				dto.setPzMininoValidacaoEntrega(rs.getBigDecimal("pz_minino_validacao_entrega"));
				dto.setNuFaturamento(rs.getLong("nu_faturamento"));
				dto.setNuCategoria(rs.getLong("nu_categoria"));
				dto.setNuCalculoVdpContrato(rs.getLong("nu_calculo_vdp_contrato"));
				dto.setNuCalculoVdpEntrega(rs.getLong("nu_calculo_vdp_entrega"));
				dto.setNuCalculoVdpOs(rs.getLong("nu_calculo_vdp_os"));
				dto.setNuAcaoHistorico(rs.getLong("nu_acao_historico"));
				dto.setNuMtrcaAcaoHstro(rs.getLong("nu_mtrca_acao_hstro"));
				dto.setIcTipoContratacao(rs.getString("ic_tipo_contratacao"));
				dto.setQtProfissionalContrato(rs.getBigDecimal("qt_profissional_contrato"));
				dto.setIcDistribuiProfissional(rs.getString("ic_distribui_profissional"));
				dto.setCoSispc(rs.getString("co_sispc"));

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

		sb.append(" select * from gct.gcttb010_contrato");

		return sb.toString();
	}

}
