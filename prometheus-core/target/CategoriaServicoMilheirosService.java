package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.prometheus.dto.CategoriaServicoMilheirosDTO;

import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;

@Component
public class CategoriaServicoMilheirosService {

    @Autowired
    private DatabaseService databaseService;

    public List<CategoriaServicoMilheirosDTO> listarCategorias(Long nuEquipe) {
        List<CategoriaServicoMilheirosDTO> result = new ArrayList<CategoriaServicoMilheirosDTO>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = databaseService.getDataSource().getConnection();

            pstmt = conn.prepareStatement(getDMLCategorias());

            pstmt.setLong(1, nuEquipe);
            
            rs = pstmt.executeQuery();

            while (rs.next()) {

                CategoriaServicoMilheirosDTO dto = new CategoriaServicoMilheirosDTO();

                dto.setNuContrato(rs.getLong("nu_contrato"));
                dto.setNuTipoGrupo(rs.getLong("nu_tipo_grupo"));
                dto.setNuTpCategoriaServico(rs.getLong("nu_tp_categoria_servico"));
                dto.setNoTpCategoriaServico(rs.getString("no_tp_categoria_servico"));
                dto.setDtInicioVigencia(rs.getDate("dt_inicio_vigencia"));
                dto.setDtFinalVigencia(rs.getDate("dt_final_vigencia"));
                dto.setIcAplicaDesconto(rs.getString("ic_aplica_desconto"));
                dto.setPcParticipacao(rs.getBigDecimal("pc_participacao"));

                result.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return result;
    }

    public String getDMLCategorias() {

        StringBuilder sb = new StringBuilder();

        sb.append(" select");
        sb.append("   cs.nu_contrato,");
        sb.append("   cs.nu_tipo_grupo,");
        sb.append("   cs.nu_tp_categoria_servico,");
        sb.append("   c.no_tp_categoria_servico,");
        sb.append("   cs.dt_inicio_vigencia,");
        sb.append("   cs.dt_final_vigencia,");
        sb.append("   cs.ic_aplica_desconto,");
        sb.append("   cs.pc_participacao");
        sb.append(" from");
        sb.append("   gct.gcttb242_categoria_servico cs");
        sb.append("   inner join gct.gcttb087_equipe e ");
        sb.append("     on e.nu_contrato = cs.nu_contrato");
        sb.append("       and cs.nu_tipo_grupo = e.nu_tipo_grupo");
        sb.append("   inner join gct.gcttb241_tp_categoria_servico c ");
        sb.append("     on cs.nu_tp_categoria_servico = c.nu_tp_categoria_servico");
        sb.append(" where");
        sb.append("   e.nu_equipe = 2832");

        return sb.toString();
    }

}
