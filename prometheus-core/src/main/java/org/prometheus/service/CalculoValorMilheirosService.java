package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.prometheus.dto.CalculoValorDevidoMilheirosDTO;
import org.prometheus.dto.CategoriaServicoMilheirosDTO;
import org.prometheus.dto.InsumoCalculoDescontoProporcionalDTO;
import org.prometheus.dto.QuantidadePrecoMilheirosDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;

@Component
public class CalculoValorMilheirosService {

    @Autowired
    private DatabaseService databaseService;

    public List<InsumoCalculoDescontoProporcionalDTO> listar(Long nuFatura, Long nuEquipe) {

        List<InsumoCalculoDescontoProporcionalDTO> result = new ArrayList<InsumoCalculoDescontoProporcionalDTO>();

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

                InsumoCalculoDescontoProporcionalDTO dto = new InsumoCalculoDescontoProporcionalDTO();

                dto.setNuFerramentaSuporte(rs.getLong("nu_ferramenta_suporte"));
                dto.setNoFerramentaSuporte(rs.getString("no_ferramenta_suporte"));
                dto.setPcParticipacao(rs.getBigDecimal("pc_participacao"));
                dto.setQtMinutosFaturamento(rs.getBigDecimal("qt_minutos_faturamento"));
                dto.setQtMinutosDisponivel(rs.getBigDecimal("qt_minutos_disponivel"));
                dto.setQtMinutosIndisponivel(rs.getBigDecimal("qt_minutos_indisponivel"));

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
        sb.append("   fe.nu_ferramenta_suporte,");
        sb.append("   fe.no_ferramenta_suporte,");
        sb.append("   fse.pc_participacao,");
        sb.append("   fse.qt_minutos_faturamento,");
        sb.append("   fse.qt_minutos_disponivel,");
        sb.append("   fse.qt_minutos_indisponivel");
        sb.append(" from");
        sb.append("   gct.gcttb246_frmnta_sprte_equipe fse");
        sb.append("   inner join gct.gcttb243_ferramenta_suporte fe ");
        sb.append("     on fe.nu_ferramenta_suporte = fse.nu_ferramenta_suporte");
        sb.append(" where");
        sb.append("   fse.nu_fatura = ?");
        sb.append("   and fse.nu_equipe = ?");
        sb.append(" order by");
        sb.append("   fe.no_ferramenta_suporte;");

        return sb.toString();
    }

    public List<QuantidadePrecoMilheirosDTO> listarQuantidades(Long nuFatura, Long nuEquipe) {

        List<QuantidadePrecoMilheirosDTO> result = new ArrayList<QuantidadePrecoMilheirosDTO>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = databaseService.getDataSource().getConnection();

            pstmt = conn.prepareStatement(getDMLQuantidades());

            pstmt.setLong(1, nuFatura);
            pstmt.setLong(2, nuEquipe);

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
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return result;
    }

    public String getDMLQuantidades() {

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
        sb.append("   q.nu_fatura = ? ");
        sb.append("   and e.nu_equipe = ? ");

        return sb.toString();
    }

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
        sb.append("   e.nu_equipe = ?");
        sb.append(" order by ");
        sb.append("   cs.nu_tp_categoria_servico");

        return sb.toString();
    }
    
    public List<CalculoValorDevidoMilheirosDTO> listarValoresDevidos(Long nuFatura, Long nuEquipe) {
        List<CalculoValorDevidoMilheirosDTO> result = new ArrayList<CalculoValorDevidoMilheirosDTO>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = databaseService.getDataSource().getConnection();

            pstmt = conn.prepareStatement(getDMLValoresDevidos());
            
            pstmt.setLong(1, nuFatura);
            pstmt.setLong(2, nuEquipe);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                CalculoValorDevidoMilheirosDTO dto = new CalculoValorDevidoMilheirosDTO();

                dto.setNuFatura(rs.getLong("nu_fatura"));
                dto.setDeFatura(rs.getString("de_fatura"));
                dto.setNuTipoPreco(rs.getLong("nu_tipo_preco"));
                dto.setNoTipoPreco(rs.getString("no_tipo_preco"));
                dto.setVrPrecoGrupo(rs.getBigDecimal("vr_preco_grupo"));
                dto.setQtSolicitacao(rs.getBigDecimal("qt_solicitacao"));
                dto.setVrPrevisto(rs.getBigDecimal("vr_previsto"));

                result.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return result;
    }

    public String getDMLValoresDevidos() {

        StringBuilder sb = new StringBuilder();

        sb.append(" select");
        sb.append("   f.nu_fatura,");
        sb.append("   f.de_fatura,");
        sb.append("   tp.nu_tipo_preco,");
        sb.append("   tp.no_tipo_preco,");
        sb.append("   p.vr_preco_grupo,");
        sb.append("   q.qt_solicitacao,");
        sb.append("   p.vr_preco_grupo * q.qt_solicitacao vr_previsto");
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
        sb.append("     and f.dt_final_fatura between p.dt_inicio_vigencia and p.dt_final_vigencia");
        sb.append("   )");
        sb.append(" where");
        sb.append("   q.nu_fatura = ? ");
        sb.append("   and e.nu_equipe = ? ");

        return sb.toString();
    }    
}
