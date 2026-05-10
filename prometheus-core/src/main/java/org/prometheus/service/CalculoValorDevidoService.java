package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.prometheus.dto.CalculoValorDevidoDTO;

import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;

@Component
public class CalculoValorDevidoService {

    @Autowired
    private DatabaseService databaseService;

    public List<CalculoValorDevidoDTO> listar(Long nuFatura, Long nuEquipe) {
        List<CalculoValorDevidoDTO> result = new ArrayList<CalculoValorDevidoDTO>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = databaseService.getDataSource().getConnection();

            pstmt = conn.prepareStatement(getDML());

            pstmt.setLong(1, nuFatura);
            pstmt.setLong(2, nuEquipe);
            
            rs = pstmt.executeQuery();

            while (rs.next()) {

                CalculoValorDevidoDTO dto = new CalculoValorDevidoDTO();

                dto.setNuFatura(rs.getLong("nu_fatura"));
                dto.setDtInicioFatura(rs.getDate("dt_inicio_fatura"));
                dto.setDtFinalFatura(rs.getDate("dt_final_fatura"));
                dto.setNuPerfil(rs.getLong("nu_perfil"));
                dto.setDePerfil(rs.getString("de_perfil"));
                dto.setCoSitio(rs.getString("co_sitio"));
                dto.setNuEquipe(rs.getLong("nu_equipe"));
                dto.setQtDiasFaturamento(rs.getBigDecimal("qt_dias_faturamento"));
                dto.setVrPrecoGrupo(rs.getBigDecimal("vr_preco_grupo"));
                dto.setQtDiasExtra(rs.getBigDecimal("qt_dias_extra"));
                dto.setQtDiasAusencia(rs.getBigDecimal("qt_dias_ausencia"));
                dto.setQtMembrosEfetiva(rs.getBigDecimal("qt_membros_efetiva"));

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

        sb.append("select "); 
        sb.append("  nu_fatura, "); 
        sb.append("  dt_inicio_fatura, "); 
        sb.append("  dt_final_fatura, "); 
        sb.append("  nu_perfil, "); 
        sb.append("  de_perfil, "); 
        sb.append("  co_sitio, "); 
        sb.append("  nu_equipe, "); 
        sb.append("  qt_dias_faturamento, "); 
        sb.append("  vr_preco_grupo, "); 
        sb.append("  coalesce( "); 
        sb.append("    ( "); 
        sb.append("      select "); 
        sb.append("        sum(qt_dia_servico_extra) "); 
        sb.append("      from "); 
        sb.append("        gct.gcttb166_ausencia_servico_extra a "); 
        sb.append("      where "); 
        sb.append("        a.nu_fatura = tb.nu_fatura "); 
        sb.append("        and a.nu_equipe = tb.nu_equipe "); 
        sb.append("        and a.nu_perfil_servico_extra = tb.nu_perfil "); 
        sb.append("    ), "); 
        sb.append("    0 "); 
        sb.append("  ) as qt_dias_extra, "); 
        sb.append("  coalesce( "); 
        sb.append("    ( "); 
        sb.append("      select "); 
        sb.append("        sum(qt_dia_ausencia) "); 
        sb.append("      from "); 
        sb.append("        gct.gcttb166_ausencia_servico_extra a "); 
        sb.append("      where "); 
        sb.append("        a.nu_fatura = tb.nu_fatura "); 
        sb.append("        and a.nu_equipe = tb.nu_equipe "); 
        sb.append("        and a.nu_perfil_ausencia = tb.nu_perfil "); 
        sb.append("    ), "); 
        sb.append("    0 "); 
        sb.append("  ) as qt_dias_ausencia, "); 
        sb.append("  sum(qt_membros_equipe * (qt_dias_trabalhados / qt_dias_faturamento) * coalesce(pc_alocacao_membro_efetiva,1)) qt_membros_efetiva "); 
        sb.append(" from ( "); 
        sb.append("    SELECT "); 
        sb.append("      f.nu_fatura, "); 
        sb.append("      f.dt_inicio_fatura, "); 
        sb.append("      f.dt_final_fatura, "); 
        sb.append("      pp.nu_perfil, "); 
        sb.append("      pp.de_perfil, "); 
        sb.append("      q.co_sitio, "); 
        sb.append("      e.nu_equipe, "); 
        sb.append("      q.pc_alocacao_membro_efetiva, "); 
        sb.append("      q.qt_membros as qt_membros_equipe, "); 
        sb.append("      gct.gctfn305_dias_entre_data( "); 
        sb.append("        CASE "); 
        sb.append("          WHEN q.dt_inicio_vigencia < f.dt_inicio_fatura THEN f.dt_inicio_fatura "); 
        sb.append("          ELSE q.dt_inicio_vigencia "); 
        sb.append("        END, "); 
        sb.append("        CASE "); 
        sb.append("          WHEN coalesce(q.dt_final_vigencia, f.dt_final_fatura) > f.dt_final_fatura THEN f.dt_final_fatura "); 
        sb.append("          ELSE coalesce(q.dt_final_vigencia, f.dt_final_fatura) "); 
        sb.append("        END, "); 
        sb.append("        ai.co_sitio, "); 
        sb.append("        gc.ic_dias_calculo_pagamento "); 
        sb.append("      ) as qt_dias_trabalhados, "); 
        sb.append("      gct.gctfn305_dias_entre_data( "); 
        sb.append("        f.dt_inicio_fatura, "); 
        sb.append("        f.dt_final_fatura, "); 
        sb.append("        e.co_sitio, "); 
        sb.append("        gc.ic_dias_calculo_pagamento "); 
        sb.append("      ) qt_dias_faturamento, "); 
        sb.append("      p.vr_preco_grupo "); 
        sb.append("    from "); 
        sb.append("      gct.gcttb523_qntde_membro_equipe_perfil q "); 
        sb.append("      inner join gct.gcttb521_perfil_profissional pp on pp.nu_perfil = q.nu_perfil "); 
        sb.append("      inner join gct.gcttb102_fatura f on f.nu_fatura = ? "); 
        sb.append("      inner join gct.gcttb087_equipe e on e.nu_equipe = q.nu_equipe "); 
        sb.append("      inner join gct.gcttb001_area_interna ai on ai.nu_area_interna = e.nu_area_interna "); 
        sb.append("      INNER JOIN gct.gcttb010_contrato c ON c.nu_contrato = e.nu_contrato "); 
        sb.append("      INNER JOIN gct.gcttb033_grupo_contrato gc ON ( "); 
        sb.append("        gc.nu_contrato = e.nu_contrato "); 
        sb.append("        AND gc.nu_tipo_grupo = e.nu_tipo_grupo "); 
        sb.append("      ) "); 
        sb.append("      inner join gct.gcttb524_preco_cntrto_equipe_perfil p on ( "); 
        sb.append("        p.nu_contrato = gc.nu_contrato "); 
        sb.append("        and p.nu_tipo_grupo = gc.nu_tipo_grupo "); 
        sb.append("        and p.nu_perfil = q.nu_perfil "); 
        sb.append("        and f.dt_final_fatura between p.dt_inicio_vigencia and p.dt_final_vigencia "); 
        sb.append("      ) "); 
        sb.append("    where "); 
        sb.append("      q.nu_equipe = ? "); 
        sb.append("      and q.dt_inicio_vigencia <= f.dt_final_fatura "); 
        sb.append("      and ( "); 
        sb.append("        q.dt_final_vigencia is null "); 
        sb.append("        or q.dt_final_vigencia >= f.dt_inicio_fatura "); 
        sb.append("      ) "); 
        sb.append("  ) tb "); 
        sb.append("group by "); 
        sb.append("  nu_fatura, "); 
        sb.append("  dt_inicio_fatura, "); 
        sb.append("  dt_final_fatura, "); 
        sb.append("  nu_perfil, "); 
        sb.append("  de_perfil, "); 
        sb.append("  co_sitio, "); 
        sb.append("  nu_equipe, "); 
        sb.append("  qt_dias_faturamento, "); 
        sb.append("  vr_preco_grupo "); 
        sb.append("order by "); 
        sb.append("  de_perfil ");

        return sb.toString();
    }

}
