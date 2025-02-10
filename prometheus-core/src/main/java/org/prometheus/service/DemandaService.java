package org.prometheus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.prometheus.dto.DemandaDTO;
import org.prometheus.utils.DBUtil;
import org.prometheus.utils.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DemandaService {

    @Autowired
    private DatabaseService databaseService;

    public DemandaDTO findByDemanda(String coDemanda) {
        
        DemandaDTO dto = null;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = databaseService.getDataSource().getConnection();

            pstmt = conn.prepareStatement(getDML());
            pstmt.setString(1, coDemanda);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                dto = new DemandaDTO();

                dto.setCoDemanda(rs.getString("co_demanda"));
                dto.setNoTipoGrupo(rs.getString("no_tipo_grupo"));
                dto.setNoModulo(rs.getString("no_modulo"));
                dto.setSgSistema(rs.getString("sg_sistema"));
                dto.setNoSistema(rs.getString("no_sistema"));
                dto.setDeSituacaoDemanda(rs.getString("de_situacao_demanda"));
                dto.setNuDemanda(rs.getLong("nu_demanda"));
                dto.setNuModulo(rs.getLong("nu_modulo"));
                dto.setNuSistema(rs.getLong("nu_sistema"));
                dto.setIcOrdemServicoAcionamento(rs.getString("ic_ordem_servico_acionamento"));
                dto.setIcQuantidade(rs.getString("ic_quantidade"));
                dto.setIcValorOsInformado(rs.getString("ic_valor_os_informado"));
                dto.setIcCalculoQuantidadeOs(rs.getString("ic_calculo_quantidade_os"));
                dto.setIcQuantidadeOs(rs.getString("ic_quantidade_os"));
                
                return dto;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return dto;
    }
    
    public List<DemandaDTO> findByContrato(String coContrato) {
        
        List<DemandaDTO> result = new ArrayList<DemandaDTO>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = databaseService.getDataSource().getConnection();

            pstmt = conn.prepareStatement(getListarPorContratosDML());
            pstmt.setString(1, coContrato);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                DemandaDTO dto = new DemandaDTO();

                dto.setCoDemanda(rs.getString("co_demanda"));
                dto.setNoTipoGrupo(rs.getString("no_tipo_grupo"));
                dto.setNoModulo(rs.getString("no_modulo"));
                dto.setSgSistema(rs.getString("sg_sistema"));
                dto.setNoSistema(rs.getString("no_sistema"));
                dto.setDeSituacaoDemanda(rs.getString("de_situacao_demanda"));
                dto.setNuDemanda(rs.getLong("nu_demanda"));
                dto.setNuModulo(rs.getLong("nu_modulo"));
                dto.setNuSistema(rs.getLong("nu_sistema"));
                dto.setIcOrdemServicoAcionamento(rs.getString("ic_ordem_servico_acionamento"));
                dto.setIcQuantidade(rs.getString("ic_quantidade"));
                dto.setIcValorOsInformado(rs.getString("ic_valor_os_informado"));
                dto.setIcCalculoQuantidadeOs(rs.getString("ic_calculo_quantidade_os"));
                dto.setIcQuantidadeOs(rs.getString("ic_quantidade_os"));
                
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

        sb.append("  select");
        sb.append("    d.co_demanda,");
        sb.append("    tg.no_tipo_grupo,");
        sb.append("    m.no_modulo,");
        sb.append("    s.sg_sistema,");
        sb.append("    s.no_sistema,");
        sb.append("    tsd.de_situacao_demanda,");
        sb.append("    d.nu_demanda,");
        sb.append("    m.nu_modulo,");
        sb.append("    s.nu_sistema,");
        sb.append("    fc.ic_ordem_servico_acionamento, ");
        sb.append("    fc.ic_quantidade, ");
        sb.append("    fc.ic_valor_os_informado, ");
        sb.append("    fc.ic_calculo_quantidade_os, ");
        sb.append("    fc.ic_quantidade_os ");
        sb.append("  from");
        sb.append("    gct.gcttb023_ordem_servico os");
        sb.append("    inner join gct.gcttb032_tipo_grupo tg on tg.nu_tipo_grupo = os.nu_tipo_grupo ");
        sb.append("    inner join gct.gcttb021_demanda d on d.nu_demanda=os.nu_demanda");
        sb.append("    inner join gct.gcttb039_sistema s on s.nu_sistema = d.nu_sistema");
        sb.append("    inner join gct.gcttb040_modulo m on m.nu_modulo = d.nu_modulo");
        sb.append("    inner join gct.gcttb008_tipo_situacao_demanda tsd on (");
        sb.append("      tsd.nu_situacao_demanda = d.nu_situacao_demanda");
        sb.append("    )");
        sb.append("    inner join gct.gcttb013_forma_contrato fc on (");
        sb.append("      fc.nu_contrato = os.nu_contrato ");
        sb.append("      and fc.nu_tipo_grupo = os.nu_tipo_grupo ");
        sb.append("      and fc.nu_tipo_servico = os.nu_tipo_servico ");
        sb.append("      and fc.nu_tipo_forma = os.nu_tipo_forma  ");
        sb.append("    )");
        sb.append("  where");
        sb.append("    d.co_demanda = ?");
        sb.append("    and os.nu_contrato = 649");
        sb.append("    and os.nu_stco_ordem_servico not in (6) ");

        return sb.toString();
    }

    public String getListarPorContratosDML() {

        StringBuilder sb = new StringBuilder();

        sb.append("select distinct "); 
        sb.append("  d.co_demanda, ");
        sb.append("  tg.no_tipo_grupo,");        
        sb.append("  m.no_modulo, "); 
        sb.append("  s.sg_sistema, "); 
        sb.append("  s.no_sistema, "); 
        sb.append("  tsd.de_situacao_demanda, "); 
        sb.append("  d.nu_demanda, "); 
        sb.append("  m.nu_modulo, "); 
        sb.append("  s.nu_sistema, "); 
        sb.append("  fc.ic_ordem_servico_acionamento, "); 
        sb.append("  fc.ic_quantidade, "); 
        sb.append("  fc.ic_valor_os_informado, "); 
        sb.append("  fc.ic_calculo_quantidade_os, "); 
        sb.append("  fc.ic_quantidade_os "); 
        sb.append("from "); 
        sb.append("  gct.gcttb023_ordem_servico os "); 
        sb.append("  inner join gct.gcttb013_forma_contrato fc on ( "); 
        sb.append("    fc.nu_contrato = os.nu_contrato "); 
        sb.append("    and fc.nu_tipo_grupo = os.nu_tipo_grupo "); 
        sb.append("    and fc.nu_tipo_servico = os.nu_tipo_servico "); 
        sb.append("    and fc.nu_tipo_forma = os.nu_tipo_forma "); 
        sb.append("  ) "); 
        sb.append("  inner join gct.gcttb010_contrato c on c.nu_contrato = os.nu_contrato "); 
        sb.append("  inner join gct.gcttb021_demanda d on d.nu_demanda = os.nu_demanda "); 
        sb.append("  inner join gct.gcttb040_modulo m on m.nu_modulo = d.nu_modulo "); 
        sb.append("  inner join gct.gcttb039_sistema s on s.nu_sistema = d.nu_sistema "); 
        sb.append("  inner join gct.gcttb008_tipo_situacao_demanda tsd on ( "); 
        sb.append("    tsd.nu_situacao_demanda = d.nu_situacao_demanda "); 
        sb.append("  ) "); 
        sb.append("  inner join gct.gcttb032_tipo_grupo tg on tg.nu_tipo_grupo = os.nu_tipo_grupo "); 
        sb.append("  inner join gct.gcttb033_grupo_contrato gc on ( "); 
        sb.append("    gc.nu_contrato = os.nu_contrato "); 
        sb.append("    and gc.nu_tipo_grupo = os.nu_tipo_grupo "); 
        sb.append("  ) "); 
        sb.append("  inner join gct.gcttb015_tipo_servico ts on ts.nu_tipo_servico = os.nu_tipo_servico "); 
        sb.append("  inner join gct.gcttb016_tipo_forma tf on tf.nu_tipo_forma = os.nu_tipo_forma "); 
        sb.append("  inner join gct.gcttb026_entrega e on e.nu_ordem_servico = os.nu_ordem_servico "); 
        sb.append("  inner join gct.gcttb028_tipo_situacao_entrega tse on ( "); 
        sb.append("    tse.nu_situacao_entrega = e.nu_situacao_entrega "); 
        sb.append("    and e.nu_situacao_entrega = 9 "); 
        sb.append("  ) "); 
        sb.append("where "); 
        sb.append("  c.co_contrato = ? "); 
        //sb.append("  and d.co_demanda = 'SP202200308' "); 
        sb.append("  and tsd.de_situacao_demanda not in ('Cancelada') ");
        sb.append("  and os.nu_contrato = 649 ");
        sb.append("  and os.nu_stco_ordem_servico not in (6) ");
        sb.append("order by "); 
        sb.append("  tg.no_tipo_grupo, d.co_demanda ");

        return sb.toString();
    }
}
