package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.prometheus.dto.OrdemServicoDTO;

import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;

@Component
public class OrdemServicoService {

    @Autowired
    private DatabaseService databaseService;

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
                dto.setNoTipoGrupo(rs.getString("no_tipo_grupo"));
                dto.setNoTipoServico(rs.getString("no_tipo_servico"));
                dto.setDeTipoForma(rs.getString("de_tipo_forma"));
                dto.setVrEstmoOrdemServico(rs.getBigDecimal("vr_estmo_ordem_servico"));
                dto.setQtOrdemServico(rs.getBigDecimal("qt_ordem_servico"));
                dto.setNuStcoOrdemServico(rs.getLong("nu_stco_ordem_servico"));
                dto.setDeStcoOrdemServico(rs.getString("de_stco_ordem_servico"));

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

        sb.append(" select "); 
        sb.append("   os.nu_ordem_servico, "); 
        sb.append("   os.co_ordem_servico, "); 
        sb.append("   tos.nu_stco_ordem_servico, "); 
        sb.append("   tos.de_stco_ordem_servico, "); 
        sb.append("   tg.no_tipo_grupo, "); 
        sb.append("   ts.no_tipo_servico, "); 
        sb.append("   tf.de_tipo_forma, "); 
        sb.append("   os.vr_estmo_ordem_servico, "); 
        sb.append("   os.qt_ordem_servico "); 
        sb.append(" from");
        sb.append("   gct.gcttb021_demanda d");
        sb.append("   inner join gct.gcttb023_ordem_servico os on os.nu_demanda = d.nu_demanda");
        sb.append("   inner join gct.gcttb044_tipo_situacao_os tos on tos.nu_stco_ordem_servico = os.nu_stco_ordem_servico");
        sb.append("   inner join gct.gcttb032_tipo_grupo tg on tg.nu_tipo_grupo = os.nu_tipo_grupo");
        sb.append("   inner join gct.gcttb015_tipo_servico ts on ts.nu_tipo_servico = os.nu_tipo_servico");
        sb.append("   inner join gct.gcttb016_tipo_forma tf on tf.nu_tipo_forma = os.nu_tipo_forma");
        sb.append(" where");
        sb.append("   d.co_demanda = ?");
        sb.append("   and os.nu_contrato = 649");
        sb.append(" order by");
        sb.append("   os.co_ordem_servico");

        return sb.toString();
    }

}
