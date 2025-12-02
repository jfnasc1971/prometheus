package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.AusenciaExtraFaturamentoDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AusenciaExtraFaturamentoService {

    @Autowired
    private DatabaseService databaseService;

    public List<AusenciaExtraFaturamentoDTO> listarAusenciasExtrasPerfil(String coEquipe, Long nuFatura) {

        List<AusenciaExtraFaturamentoDTO> result = new ArrayList<AusenciaExtraFaturamentoDTO>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = databaseService.getDataSource().getConnection();

            pstmt = conn.prepareStatement(getDMLAusenciasExtrasPerfil());
            pstmt.setString(1, coEquipe);
            pstmt.setLong(2, nuFatura);
            pstmt.setString(3, coEquipe);
            pstmt.setLong(4, nuFatura);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                AusenciaExtraFaturamentoDTO dto = new AusenciaExtraFaturamentoDTO();

                dto.setNuPerfil(rs.getLong("nu_perfil"));
                dto.setDhInclusaoInformacao(rs.getDate("dh_inclusao_informacao"));
                dto.setDePerfil(rs.getString("de_perfil"));
                dto.setDeFatura(rs.getString("de_fatura"));
                dto.setQtDiaServicoExtra(rs.getBigDecimal("qt_dia_servico_extra"));
                dto.setQtDiaAusencia(rs.getBigDecimal("qt_dia_ausencia"));

                result.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return result;
    }

    public List<AusenciaExtraFaturamentoDTO> listar(String coEquipe) {
        List<AusenciaExtraFaturamentoDTO> result = new ArrayList<AusenciaExtraFaturamentoDTO>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = databaseService.getDataSource().getConnection();

            pstmt = conn.prepareStatement(getDML(false));
            pstmt.setString(1, coEquipe);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                AusenciaExtraFaturamentoDTO dto = new AusenciaExtraFaturamentoDTO();

                dto.setNuEquipe(rs.getLong("nu_equipe"));
                dto.setDeFatura(rs.getString("de_fatura"));
                if (rs.getBigDecimal("nu_ciclo_trabalho") != null) {
                    dto.setNuCicloTrabalho(rs.getLong("nu_ciclo_trabalho"));
                }
                dto.setQtDiaServicoExtra(rs.getBigDecimal("qt_dia_servico_extra"));
                dto.setQtDiaAusencia(rs.getBigDecimal("qt_dia_ausencia"));

                result.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return result;

    }

    public AusenciaExtraFaturamentoDTO findBy(Long nuEquipe, Long nuFatura, Long nuPerfil) {

        AusenciaExtraFaturamentoDTO dto = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = databaseService.getDataSource().getConnection();

            pstmt = conn.prepareStatement(getDMLPerfil());
            pstmt.setLong(1, nuEquipe);
            pstmt.setLong(2, nuFatura);
            pstmt.setLong(3, nuPerfil);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                dto = new AusenciaExtraFaturamentoDTO();

                dto.setNuEquipe(rs.getLong("nu_equipe"));
                dto.setQtDiaAusencia(rs.getBigDecimal("qt_dia_ausencia"));
                dto.setQtDiaServicoExtra(rs.getBigDecimal("qt_dia_servico_extra"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return dto;

    }

    public AusenciaExtraFaturamentoDTO findBy(String coEquipe, Long nuFatura) {

        AusenciaExtraFaturamentoDTO dto = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = databaseService.getDataSource().getConnection();

            pstmt = conn.prepareStatement(getDML(true));
            pstmt.setString(1, coEquipe);
            pstmt.setLong(2, nuFatura);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                dto = new AusenciaExtraFaturamentoDTO();

                dto.setNuEquipe(rs.getLong("nu_equipe"));
                dto.setQtDiaAusencia(rs.getBigDecimal("qt_dia_ausencia"));
                dto.setQtDiaServicoExtra(rs.getBigDecimal("qt_dia_servico_extra"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return dto;
    }

    public String getDML(boolean filtrarFatura) {

        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ");
        sb.append("   e.nu_equipe, ");
        sb.append("   f.de_fatura, ");
        sb.append("   a.nu_ciclo_trabalho, ");
        sb.append("   sum(coalesce(a.qt_dia_servico_extra, 0)) as qt_dia_servico_extra, ");
        sb.append("   sum(coalesce(a.qt_dia_ausencia, 0)) as qt_dia_ausencia ");
        sb.append(" FROM ");
        sb.append("   gct.gcttb087_equipe e ");
        sb.append("   INNER JOIN gct.gcttb170_termo_equipe t ON t.nu_equipe = e.nu_equipe ");
        sb.append("   INNER JOIN gct.gcttb102_fatura f ON f.nu_fatura = t.nu_fatura_gerada ");
        sb.append("   LEFT JOIN gct.gcttb166_ausencia_servico_extra a ON ( ");
        sb.append("     a.nu_equipe = e.nu_equipe ");
        sb.append("     AND a.nu_fatura = t.nu_fatura_gerada ");
        sb.append("   ) ");
        sb.append(" WHERE");
        sb.append("   t.nu_tipo_termo = 11");
        sb.append("   AND e.co_equipe = ?");
        if (filtrarFatura) {
            sb.append(" AND t.nu_fatura_gerada = ?");
        }
        sb.append(" group by ");
        sb.append("   e.nu_equipe, ");
        sb.append("   f.de_fatura, ");
        sb.append("   a.nu_ciclo_trabalho ");

        return sb.toString();
    }

    public String getDMLPerfil() {

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append("  a.nu_equipe, ");
        sb.append("  sum(coalesce(a.qt_dia_servico_extra, 0)) as qt_dia_servico_extra, ");
        sb.append("  sum(coalesce(a.qt_dia_ausencia, 0)) as qt_dia_ausencia ");
        sb.append("from ");
        sb.append("  gct.gcttb166_ausencia_servico_extra a ");
        sb.append("where ");
        sb.append("  a.nu_equipe = ? ");
        sb.append("  and a.nu_fatura = ? ");
        sb.append("  and coalesce(a.nu_perfil_ausencia, a.nu_perfil_servico_extra) = ? ");
        sb.append("group by ");
        sb.append("  a.nu_equipe ");

        return sb.toString();
    }

    public String getDMLAusenciasExtrasPerfil() {

        StringBuilder sb = new StringBuilder();

        sb.append("select "); 
        sb.append("  de_fatura, "); 
        sb.append("  dh_inclusao_informacao, "); 
        sb.append("  nu_perfil, "); 
        sb.append("  de_perfil, "); 
        sb.append("  coalesce(sum(qt_dia_ausencia),0) as qt_dia_ausencia, "); 
        sb.append("  coalesce(sum(qt_dia_servico_extra),0) as qt_dia_servico_extra "); 
        sb.append("from "); 
        sb.append("  ( "); 
        sb.append("    select "); 
        sb.append("      f.de_fatura, "); 
        sb.append("      a.dh_inclusao_informacao, "); 
        sb.append("      pp.nu_perfil, "); 
        sb.append("      pp.de_perfil, "); 
        sb.append("      a.qt_dia_ausencia, "); 
        sb.append("      a.qt_dia_servico_extra "); 
        sb.append("    from "); 
        sb.append("      gct.gcttb087_equipe e "); 
        sb.append("      inner join gct.gcttb166_ausencia_servico_extra a on a.nu_equipe = e.nu_equipe "); 
        sb.append("      inner join gct.gcttb102_fatura f on f.nu_fatura = a.nu_fatura "); 
        sb.append("      inner join gct.gcttb521_perfil_profissional pp on pp.nu_perfil = a.nu_perfil_servico_extra "); 
        sb.append("    where "); 
        sb.append("      e.co_equipe = ? "); 
        sb.append("      and f.nu_fatura = ? "); 
        sb.append("    union "); 
        sb.append("    select "); 
        sb.append("      f.de_fatura, "); 
        sb.append("      a.dh_inclusao_informacao, "); 
        sb.append("      pp.nu_perfil, "); 
        sb.append("      pp.de_perfil, "); 
        sb.append("      a.qt_dia_ausencia, "); 
        sb.append("      a.qt_dia_servico_extra "); 
        sb.append("    from "); 
        sb.append("      gct.gcttb087_equipe e "); 
        sb.append("      inner join gct.gcttb166_ausencia_servico_extra a on a.nu_equipe = e.nu_equipe "); 
        sb.append("      inner join gct.gcttb102_fatura f on f.nu_fatura = a.nu_fatura "); 
        sb.append("      inner join gct.gcttb521_perfil_profissional pp on pp.nu_perfil = a.nu_perfil_ausencia "); 
        sb.append("    where "); 
        sb.append("      e.co_equipe = ? "); 
        sb.append("      and f.nu_fatura = ? "); 
        sb.append("  ) tb "); 
        sb.append("group by "); 
        sb.append("  de_fatura, "); 
        sb.append("  dh_inclusao_informacao, "); 
        sb.append("  nu_perfil, "); 
        sb.append("  de_perfil "); 
        sb.append("order by "); 
        sb.append("  dh_inclusao_informacao, "); 
        sb.append("  de_perfil ");

        return sb.toString();
    }

}
