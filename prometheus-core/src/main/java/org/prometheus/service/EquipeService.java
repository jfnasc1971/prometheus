package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.EquipeDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EquipeService {

    @Autowired
    private DatabaseService databaseService;

    public List<EquipeDTO> findByContrato(String coContrato) {

        List<EquipeDTO> result = new ArrayList<EquipeDTO>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = databaseService.getDataSource().getConnection();

            pstmt = conn.prepareStatement(getDMLContrato());
            pstmt.setString(1, "%" + coContrato + "%");

            rs = pstmt.executeQuery();

            while (rs.next()) {

                EquipeDTO dto = new EquipeDTO();

                dto.setNuContrato(rs.getLong("nu_contrato"));
                dto.setNoContrato(rs.getString("no_contrato"));
                dto.setCoContrato(rs.getString("co_contrato"));
                dto.setNuTipoGrupo(rs.getLong("nu_tipo_grupo"));
                dto.setNoTipoGrupo(rs.getString("no_tipo_grupo"));
                dto.setNuEquipe(rs.getLong("nu_equipe"));
                dto.setCoEquipe(rs.getString("co_equipe"));
                dto.setCoSitio(rs.getString("co_sitio"));
                dto.setDtInicioAtendimento(rs.getTimestamp("dt_inicio_atendimento"));
                dto.setDtFinalAtendimento(rs.getTimestamp("dt_final_atendimento"));
                dto.setNoSituacaoEquipe(rs.getString("no_situacao_equipe"));
                dto.setIcDiasCalculoPagamento(rs.getString("ic_dias_calculo_pagamento"));
                dto.setIcCalculoProRata(rs.getString("ic_calculo_pro_rata"));
                dto.setNoTipoAtendimento(rs.getString("no_tipo_atendimento"));
                dto.setIcTipoAtendimento(rs.getString("ic_tipo_atendimento"));
                dto.setIcPermiteServicoExtra(rs.getString("ic_permite_servico_extra"));
                dto.setIcPermiteRegistroAusencia(rs.getString("ic_permite_registro_ausencia"));
                dto.setDtConsiderarFns(rs.getDate("dt_considerar_fns"));
                dto.setIcDistribuiProfissional(rs.getString("ic_distribui_profissional"));
                
                result.add(dto);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return result;
    }    
    
    public EquipeDTO findBy(String coEquipe) {

        EquipeDTO dto = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = databaseService.getDataSource().getConnection();

            pstmt = conn.prepareStatement(getDMLEquipe());
            pstmt.setString(1, coEquipe);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                dto = new EquipeDTO();

                dto.setNuContrato(rs.getLong("nu_contrato"));
                dto.setNoContrato(rs.getString("no_contrato"));
                dto.setCoContrato(rs.getString("co_contrato"));
                dto.setNuTipoGrupo(rs.getLong("nu_tipo_grupo"));
                dto.setNoTipoGrupo(rs.getString("no_tipo_grupo"));
                dto.setNuEquipe(rs.getLong("nu_equipe"));
                dto.setCoEquipe(rs.getString("co_equipe"));
                dto.setCoSitio(rs.getString("co_sitio"));
                dto.setDtInicioAtendimento(rs.getTimestamp("dt_inicio_atendimento"));
                dto.setDtFinalAtendimento(rs.getTimestamp("dt_final_atendimento"));
                dto.setNoSituacaoEquipe(rs.getString("no_situacao_equipe"));
                dto.setIcDiasCalculoPagamento(rs.getString("ic_dias_calculo_pagamento"));
                dto.setIcCalculoProRata(rs.getString("ic_calculo_pro_rata"));
                dto.setNoTipoAtendimento(rs.getString("no_tipo_atendimento"));
                dto.setIcTipoAtendimento(rs.getString("ic_tipo_atendimento"));
                dto.setIcPermiteServicoExtra(rs.getString("ic_permite_servico_extra"));
                dto.setIcPermiteRegistroAusencia(rs.getString("ic_permite_registro_ausencia"));
                dto.setDtConsiderarFns(rs.getDate("dt_considerar_fns"));
                dto.setIcDistribuiProfissional(rs.getString("ic_distribui_profissional"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return dto;
    }

    public String getDMLEquipe() {

        StringBuilder sb = getDML();

        sb.append("WHERE ");
        sb.append("  e.co_equipe = ? ");

        return sb.toString();

    }

    public String getDMLContrato() {

        StringBuilder sb = getDML();

        sb.append("WHERE ");
        sb.append("  upper(c.co_contrato) ilike upper(?) ");
        sb.append("  and e.nu_tipo_grupo = 63 ");
        sb.append("ORDER BY ");
        sb.append("  e.co_equipe ");

        return sb.toString();

    }

    private StringBuilder getDML() {

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append("  c.nu_contrato, ");
        sb.append("  c.no_contrato, ");
        sb.append("  c.co_contrato, ");
        sb.append("  c.ic_distribui_profissional, ");
        sb.append("  tg.nu_tipo_grupo, ");
        sb.append("  tg.no_tipo_grupo, ");
        sb.append("  e.nu_equipe, ");
        sb.append("  e.co_equipe, ");
        sb.append("  e.co_sitio, ");
        sb.append("  e.dt_inicio_atendimento, ");
        sb.append("  e.dt_final_atendimento, ");
        sb.append("  tse.no_situacao_equipe, ");
        sb.append("  gc.ic_dias_calculo_pagamento, ");
        sb.append("  gc.ic_calculo_pro_rata, ");
        sb.append("  gc.ic_tipo_atendimento, ");
        sb.append("  CASE ");
        sb.append("    WHEN gc.ic_tipo_atendimento = 'S' THEN 'Sprint' ");
        sb.append("    ELSE 'Acionamento' ");
        sb.append("  END AS no_tipo_atendimento, ");
        sb.append("  gc.ic_permite_servico_extra, ");
        sb.append("  gc.ic_permite_registro_ausencia, ");
        sb.append("  gc.ic_calculo_pro_rata, ");
        sb.append("  gc.dt_considerar_fns ");
        sb.append("FROM ");
        sb.append("  gct.gcttb087_equipe e ");
        sb.append("  INNER JOIN gct.gcttb010_contrato c ON c.nu_contrato = e.nu_contrato ");
        sb.append("  INNER JOIN gct.gcttb032_tipo_grupo tg ON tg.nu_tipo_grupo = e.nu_tipo_grupo ");
        sb.append("  INNER JOIN gct.gcttb085_situacao_equipe tse ON tse.nu_situacao_equipe = e.nu_situacao_equipe ");
        sb.append("  INNER JOIN gct.gcttb033_grupo_contrato gc ON ( ");
        sb.append("    gc.nu_contrato = e.nu_contrato ");
        sb.append("    AND gc.nu_tipo_grupo = e.nu_tipo_grupo ");
        sb.append("  ) ");

        return sb;
    }

}
