package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.prometheus.dto.MesuracaoDTO;

import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;

@Component
public class MesuracaoService {


    @Autowired
    private DatabaseService databaseService;


	public List<MesuracaoDTO> listar() { 
		List<MesuracaoDTO> result = new ArrayList<MesuracaoDTO>(); 
 
		Connection conn = null; 
		PreparedStatement pstmt = null; 
		ResultSet rs = null; 
 
		try { 
 
			conn = databaseService.getDataSource().getConnection(); 
 
			pstmt = conn.prepareStatement(getDML()); 
 
			rs = pstmt.executeQuery(); 
 
			while (rs.next()) { 
 
				MesuracaoDTO dto = new MesuracaoDTO(); 
 
dto.setNuOrdemServico(rs.getLong("nu_ordem_servico"));
dto.setCoOrdemServico(rs.getString("co_ordem_servico"));
dto.setCoSolicitacao(rs.getString("co_solicitacao"));
dto.setNuSolicitacao(rs.getLong("nu_solicitacao"));
dto.setNoTipoQuantidade(rs.getString("no_tipo_quantidade"));
dto.setQtSolicitacao(rs.getBigDecimal("qt_solicitacao"));
dto.setVrAjusteFinanceiro(rs.getBigDecimal("vr_ajuste_financeiro"));
dto.setQtSolicitacaoAjustado(rs.getBigDecimal("qt_solicitacao_ajustado"));
 
				result.add(dto); 
			} 
		} catch (SQLException e) { 
			e.printStackTrace(); 
		} 
		finally { 
			DBUtil.close(conn, pstmt, rs); 
		} 
 
		return result; 
	} 
	public String getDML() { 

	  StringBuilder sb = new StringBuilder();

    sb.append(" select");
    sb.append("   os.nu_ordem_servico,");
    sb.append("   os.co_ordem_servico,");
    sb.append("   s.co_solicitacao,");
    sb.append("   qos.nu_solicitacao,");
    sb.append("   tq.no_tipo_quantidade,");
    sb.append("   qos.qt_solicitacao,");
    sb.append("   tq.vr_ajuste_financeiro,");
    sb.append("   qos.qt_solicitacao * tq.vr_ajuste_financeiro qt_solicitacao_ajustado");
    sb.append(" from");
    sb.append("   gct.gcttb023_ordem_servico os");
    sb.append("   inner join gct.gcttb051_quantidade_ordem_serv qos on qos.nu_ordem_servico = os.nu_ordem_servico");
    sb.append("   inner join gct.gcttb049_tipo_quantidade tq on tq.nu_tipo_quantidade = qos.nu_tipo_quantidade");
    sb.append("   inner join gct.gcttb005_solicitacao s on s.nu_solicitacao = qos.nu_solicitacao");
    sb.append(" where");
    sb.append("   os.nu_ordem_servico = 692662");
    sb.append(" order by");
    sb.append("   os.co_ordem_servico,");
    sb.append("   s.co_solicitacao,");
    sb.append("   tq.nu_tipo_quantidade");

	  return sb.toString();
	}

}
