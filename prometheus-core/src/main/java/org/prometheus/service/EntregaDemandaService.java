package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.prometheus.dto.EntregaDemandaDTO;

import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;

@Component
public class EntregaDemandaService {

    @Autowired
    private DatabaseService databaseService;

    public List<EntregaDemandaDTO> listar(Long nuOrdemServico) {
        List<EntregaDemandaDTO> result = new ArrayList<EntregaDemandaDTO>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = databaseService.getDataSource().getConnection();

            pstmt = conn.prepareStatement(getDML());
            pstmt.setLong(1, nuOrdemServico);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                EntregaDemandaDTO dto = new EntregaDemandaDTO();

                dto.setNuOrdemServico(rs.getLong("nu_ordem_servico"));
                dto.setCoOrdemServico(rs.getString("co_ordem_servico"));
                dto.setNuEntrega(rs.getLong("nu_entrega"));
                dto.setCoEntrega(rs.getString("co_entrega"));
                dto.setDhPrevistaEntrega(rs.getTimestamp("dh_prevista_entrega"));
                dto.setDhPrevistaPendencias(rs.getTimestamp("dh_prevista_pendencias"));
                dto.setDhEfetivaEntrega(rs.getTimestamp("dh_efetiva_entrega"));
                dto.setDeSituacaoEntrega(rs.getString("de_situacao_entrega"));
                dto.setPcEntrega(rs.getBigDecimal("pc_entrega"));
                dto.setPcExecutado(rs.getBigDecimal("pc_executado"));
                dto.setNuSituacaoEntrega(rs.getLong("nu_situacao_entrega"));
                dto.setVrPreco(rs.getBigDecimal("vr_preco"));
                dto.setQtDiasPendencia(rs.getBigDecimal("qt_dias_pendencia"));

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
        sb.append("   os.nu_ordem_servico,");
        sb.append("   os.co_ordem_servico,");
        sb.append("   e.nu_entrega,");
        sb.append("   e.co_entrega,");
        sb.append("   e.dh_prevista_entrega,");
        sb.append("   e.dh_prevista_entrega + (gct.gctfn057_calcula_pendencia(e.nu_entrega) * interval '1 day') as dh_prevista_pendencias,");
        sb.append("   gct.gctfn057_calcula_pendencia(e.nu_entrega) qt_dias_pendencia,");
        sb.append("   e.dh_efetiva_entrega,");
        sb.append("   tse.de_situacao_entrega,");
        sb.append("   e.pc_entrega / 100 :: numeric pc_entrega,");
        sb.append("   e.pc_executado / 100 :: numeric pc_executado,");
        sb.append("   e.nu_situacao_entrega,");
        sb.append("   coalesce(pf.vr_preco,0) vr_preco");
        sb.append(" from");
        sb.append("   gct.gcttb023_ordem_servico os");
        sb.append("   inner join gct.gcttb021_demanda d on d.nu_demanda = os.nu_demanda");
        sb.append("   inner join gct.gcttb026_entrega e on e.nu_ordem_servico = os.nu_ordem_servico");
        sb.append("   inner join gct.gcttb028_tipo_situacao_entrega tse on tse.nu_situacao_entrega = e.nu_situacao_entrega");
        sb.append("   left join gct.gcttb054_preco_forma pf on (");
        sb.append("     pf.nu_contrato = os.nu_contrato");
        sb.append("     and pf.nu_tipo_grupo = os.nu_tipo_grupo");
        sb.append("     and pf.nu_tipo_servico = os.nu_tipo_servico");
        sb.append("     and pf.nu_tipo_forma = os.nu_tipo_forma");
        sb.append("     and e.dh_efetiva_entrega :: date between pf.dt_inicio_vigencia :: date");
        sb.append("     and pf.dt_final_vigencia :: date");
        sb.append("   )");
        sb.append(" where");
        //sb.append("   e.nu_situacao_entrega not in (12)");
        sb.append("   os.nu_ordem_servico = ?");
        sb.append(" order by");
        sb.append("   os.co_ordem_servico, e.co_entrega");

        return sb.toString();
    }

}
