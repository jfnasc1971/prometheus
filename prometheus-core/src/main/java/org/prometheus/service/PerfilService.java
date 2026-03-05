package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.PerfilDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PerfilService {

    @Autowired
    private DatabaseService databaseService;

    public List<PerfilDTO> listar(Long nuFatura, Long nuEquipe) {
        List<PerfilDTO> result = new ArrayList<PerfilDTO>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = databaseService.getDataSource().getConnection();

            pstmt = conn.prepareStatement(getDMLConsultaPorFaturaEquipe());

            pstmt.setLong(1, nuFatura);
            pstmt.setLong(2, nuEquipe);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                PerfilDTO dto = new PerfilDTO();

                dto.setNuPerfil(rs.getLong("nu_perfil"));
                dto.setDePerfil(rs.getString("de_perfil"));

                result.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return result;
    }

    public List<PerfilDTO> listar(EquipeDTO equipe) {
        List<PerfilDTO> result = new ArrayList<PerfilDTO>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = databaseService.getDataSource().getConnection();

            pstmt = conn.prepareStatement(getDML());

            pstmt.setLong(1, equipe.getNuEquipe());

            rs = pstmt.executeQuery();

            while (rs.next()) {

                PerfilDTO dto = new PerfilDTO();

                dto.setNuPerfil(rs.getLong("nu_perfil"));
                dto.setDePerfil(rs.getString("de_perfil"));

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

        sb.append(" select distinct "); 
        sb.append("   pp.nu_perfil, "); 
        sb.append("   pp.de_perfil "); 
        sb.append(" from "); 
        sb.append("   gct.gcttb087_equipe e "); 
        sb.append("   inner join gct.gcttb523_qntde_membro_equipe_perfil q on q.nu_equipe = e.nu_equipe "); 
        sb.append("   inner join gct.gcttb521_perfil_profissional pp on pp.nu_perfil = q.nu_perfil "); 
        sb.append(" where "); 
        sb.append("   e.nu_equipe=? ");
        
        return sb.toString();
    }

    public String getDMLConsultaPorFaturaEquipe() {

        StringBuilder sb = new StringBuilder();

        sb.append("select ");
        sb.append("  distinct pp.nu_perfil, pp.de_perfil ");
        sb.append("from ");
        sb.append("  gct.gcttb087_equipe e ");
        sb.append("  inner join gct.gcttb102_fatura f on f.nu_fatura = ? ");
        sb.append("  inner join gct.gcttb523_qntde_membro_equipe_perfil q on ( ");
        sb.append("    q.nu_equipe = e.nu_equipe ");
        sb.append("    and q.dt_inicio_vigencia <= f.dt_final_fatura ");
        sb.append("    and coalesce(q.dt_final_vigencia,f.dt_final_fatura) >= f.dt_inicio_fatura ");
        sb.append("  ) ");
        sb.append("  inner join gct.gcttb521_perfil_profissional pp on pp.nu_perfil = q.nu_perfil ");
        sb.append("where ");
        sb.append("  e.nu_equipe = ? ");

        return sb.toString();
    }
}
