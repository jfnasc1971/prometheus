package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.prometheus.dto.VigenciaMembrosEquipeDTO;

import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;

@Component
public class VigenciaMembrosEquipeService {


    @Autowired
    private DatabaseService databaseService;


	public List<VigenciaMembrosEquipeDTO> listar() { 
		List<VigenciaMembrosEquipeDTO> result = new ArrayList<VigenciaMembrosEquipeDTO>(); 
 
		Connection conn = null; 
		PreparedStatement pstmt = null; 
		ResultSet rs = null; 
 
		try { 
 
			conn = databaseService.getDataSource().getConnection(); 
 
			pstmt = conn.prepareStatement(getDML()); 
 
			rs = pstmt.executeQuery(); 
 
			while (rs.next()) { 
 
				VigenciaMembrosEquipeDTO dto = new VigenciaMembrosEquipeDTO(); 
 
dto.setCoMatricula(rs.getString("co_matricula"));
dto.setNoMembro(rs.getString("no_membro"));
dto.setNuPosicaoMembro(rs.getLong("nu_posicao_membro"));
dto.setDtInicioMembro(rs.getDate("dt_inicio_membro"));
dto.setDtFimMembro(rs.getDate("dt_fim_membro"));
 
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
    sb.append("   m.tp_matricula || m.nu_matricula co_matricula,");
    sb.append("   m.no_membro,");
    sb.append("   vme.nu_posicao_membro,");
    sb.append("   vme.dt_inicio_membro,");
    sb.append("   vme.dt_fim_membro");
    sb.append(" from");
    sb.append("   gct.gcttb087_equipe e");
    sb.append("   inner join gct.gcttb097_qntde_membros_equipe qme on qme.nu_equipe = e.nu_equipe");
    sb.append("   inner join gct.gcttb419_vigencia_membros_equipe vme on (");
    sb.append("     vme.nu_equipe = qme.nu_equipe");
    sb.append("     and vme.dt_inicio_membro = qme.dt_inicio_vigencia_membros");
    sb.append("   )");
    sb.append("   inner join gct.gcttb418_membros m on m.nu_membro = vme.nu_membro");
    sb.append(" where");
    sb.append("   e.co_equipe = 'SPE202300120'");
    sb.append(" order by");
    sb.append("   m.tp_matricula || m.nu_matricula,");
    sb.append("   vme.dt_inicio_membro");

	  return sb.toString();
	}

}
