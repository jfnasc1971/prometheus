package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.prometheus.dto.QuantidadePrecoMilheirosDTO;

import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;

@Component
public class QuantidadePrecoMilheirosService {


    @Autowired
    private DatabaseService databaseService;


	public List<QuantidadePrecoMilheirosDTO> listar() { 
		List<QuantidadePrecoMilheirosDTO> result = new ArrayList<QuantidadePrecoMilheirosDTO>(); 
 
		Connection conn = null; 
		PreparedStatement pstmt = null; 
		ResultSet rs = null; 
 
		try { 
 
			conn = databaseService.getDataSource().getConnection(); 
 
			pstmt = conn.prepareStatement(getDML()); 
 
			rs = pstmt.executeQuery(); 
 
			while (rs.next()) { 
 
				QuantidadePrecoMilheirosDTO dto = new QuantidadePrecoMilheirosDTO(); 
 
dto.setNuTipoPreco(rs.getLong("nu_tipo_preco"));
dto.setNoTipoPreco(rs.getString("no_tipo_preco"));
dto.setVrPrecoGrupo(rs.getBigDecimal("vr_preco_grupo"));
dto.setQtSolicitacao(rs.getBigDecimal("qt_solicitacao"));
 
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
    sb.append("   tp.nu_tipo_preco,");
    sb.append("   tp.no_tipo_preco,");
    sb.append("   p.vr_preco_grupo,");
    sb.append("   q.qt_solicitacao");
    sb.append(" from");
    sb.append("   gct.gcttb244_quantidade_contratacao q");
    sb.append("   inner join gct.gcttb087_equipe e on e.nu_equipe = q.nu_equipe");
    sb.append("   inner join gct.gcttb102_fatura f on f.nu_fatura = q.nu_fatura");
    sb.append("   inner join gct.gcttb053_tipo_preco tp on tp.nu_tipo_preco = q.nu_tipo_preco");
    sb.append("   inner join gct.gcttb014_unidade_medida um on um.nu_unidade_medida = q.nu_unidade_medida");
    sb.append("   inner join gct.gcttb165_preco_contrato_equipe p on (");
    sb.append("     p.nu_contrato = e.nu_contrato");
    sb.append("     and p.nu_tipo_grupo = e.nu_tipo_grupo");
    sb.append("     and p.nu_tipo_preco = q.nu_tipo_preco");
    sb.append("     and f.dt_final_fatura between p.dt_inicio_vigencia");
    sb.append("     and p.dt_final_vigencia");
    sb.append("   )");
    sb.append(" where");
    sb.append("   e.nu_equipe = 2832");
    sb.append("   and q.nu_fatura = 5661;");

	  return sb.toString();
	}

}
