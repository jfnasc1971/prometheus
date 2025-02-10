package org.prometheus.relatorios.demanda;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.DemandaDTO;
import org.prometheus.dto.EntregaDemandaDTO;
import org.prometheus.dto.MensuracaoDTO;
import org.prometheus.dto.OrdemServicoDTO;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.service.DemandaService;
import org.prometheus.service.EntregaDemandaService;
import org.prometheus.service.MensuracaoService;
import org.prometheus.service.OrdemServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AbaDemanda extends Relatorio {

	private static final int LAYOUT_COLUMNS_COUNT = 10;

	@Autowired
	private DemandaService demandaService;

	@Autowired
	private OrdemServicoService ordemServicoService;

	@Autowired
	private EntregaDemandaService entregaDemandaService;

    @Autowired
    private MensuracaoService mensuracaoService;	
	
	public XSSFCell write(XSSFWorkbook workBook, String coDemanda) throws Exception {

	    XSSFCell result = null;
	    
		DemandaDTO demanda = demandaService.findByDemanda(coDemanda);

		carregarFontes(workBook);

		carregarEstilos(workBook);

		XSSFSheet sheet = workBook.createSheet("Demanda");

		sheet.setZoom(100);
		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT + 1, 80);

		XSSFRow row = null;

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT, demanda.getCoDemanda(), ALIGN_CENTER_GREY_25_PERCENT_BOLD);

		addBlank(sheet);

		row = addNewRow(sheet);
		//co_demanda    no_modulo       sg_sistema  no_sistema      de_situacao_demanda
		addCell(row, 0, "Demanda", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addMergedRegion(sheet, row, 1, 3, "Módulo", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, 4, "Sigla", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addMergedRegion(sheet, row, 5, 7, "Sistema", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, 8, "Situação", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

		row = addNewRow(sheet);
        //co_demanda    no_modulo       sg_sistema  no_sistema      de_situacao_demanda
        addCell(row, 0, demanda.getCoDemanda(), ALIGN_CENTER);
        addMergedRegion(sheet, row, 1, 3, demanda.getNoModulo(), ALIGN_CENTER);
        addCell(row, 4, demanda.getSgSistema(), ALIGN_CENTER);
        addMergedRegion(sheet, row, 5, 7, demanda.getNoSistema(), ALIGN_CENTER);
        addCell(row, 8, demanda.getDeSituacaoDemanda(), ALIGN_CENTER);
		
        addBlank(sheet);

        row = addNewRow(sheet);
        addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT, "Ordens de Serviço", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
        
		List<OrdemServicoDTO> ordensServico = ordemServicoService.listar(coDemanda);

        row = addNewRow(sheet);
        // co_ordem_servico no_tipo_grupo           no_tipo_servico         de_tipo_forma   vr_estmo_ordem_servico
        addCell(row, 0, "Código", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
        addMergedRegion(sheet, row, 1, 3, "Grupo", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
        addMergedRegion(sheet, row, 4, 6, "Serviço", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
        addMergedRegion(sheet, row, 7, 9, "Forma", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
        addCell(row, 10, "Valor previsto", ALIGN_CENTER_GREY_25_PERCENT_BOLD);  		
		
        int posPrimeiraOS = row.getRowNum() + 2;
        int posUltimaOS = posPrimeiraOS - 1;
        
		for (OrdemServicoDTO ordemServico : ordensServico) {

		    row = addNewRow(sheet);
	        // co_ordem_servico no_tipo_grupo           no_tipo_servico         de_tipo_forma   vr_estmo_ordem_servico
	        addCell(row, 0, ordemServico.getCoOrdemServico(), ALIGN_CENTER);
	        addMergedRegion(sheet, row, 1, 3, ordemServico.getNoTipoGrupo(), ALIGN_CENTER);
	        addMergedRegion(sheet, row, 4, 6, ordemServico.getNoTipoServico(), ALIGN_CENTER);
	        addMergedRegion(sheet, row, 7, 9, ordemServico.getDeTipoForma(), ALIGN_CENTER);
	        addCell(row, 10, ordemServico.getVrEstmoOrdemServico(), STYLE_DECIMAL);
	        posUltimaOS++;
		}
		
        row = addNewRow(sheet);
        addCell(row, 9, "TOTAL", ALIGN_CENTER_GREY_25_PERCENT_BOLD);		
        result = setCellFormula(sheet, row, //
                String.format("SUM(K%s:K%s)", posPrimeiraOS, posUltimaOS), //
                10, STYLE_DECIMAL);        
        
        addBlank(sheet);

        for (OrdemServicoDTO ordemServico : ordensServico) {
            
            row = addNewRow(sheet);
            addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT, "OS: " + //
                    ordemServico.getCoOrdemServico(), ALIGN_CENTER_LIGHT_BLUE_BOLD);
            
            row = addNewRow(sheet);
            // co_ordem_servico no_tipo_grupo           no_tipo_servico         de_tipo_forma   vr_estmo_ordem_servico
            addCell(row, 0, "Código", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addMergedRegion(sheet, row, 1, 3, "Grupo", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addMergedRegion(sheet, row, 4, 6, "Serviço", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addMergedRegion(sheet, row, 7, 9, "Forma", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 10, "Valor previsto", ALIGN_CENTER_GREY_25_PERCENT_BOLD);          
            
            row = addNewRow(sheet);
            // co_ordem_servico no_tipo_grupo           no_tipo_servico         de_tipo_forma   vr_estmo_ordem_servico
            addCell(row, 0, ordemServico.getCoOrdemServico(), ALIGN_CENTER);
            addMergedRegion(sheet, row, 1, 3, ordemServico.getNoTipoGrupo(), ALIGN_CENTER);
            addMergedRegion(sheet, row, 4, 6, ordemServico.getNoTipoServico(), ALIGN_CENTER);
            addMergedRegion(sheet, row, 7, 9, ordemServico.getDeTipoForma(), ALIGN_CENTER);
            addCell(row, 10, ordemServico.getVrEstmoOrdemServico(), STYLE_DECIMAL);
            
            //
            // MENSURACAO
            //
            addBlank(sheet);

            row = addNewRow(sheet);
            addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 4, "Mensuração", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            
            row = addNewRow(sheet);
            // OS   Solicitação Qt. PF  Fator Ajuste    Qt. PF Efetiva
            addCell(row, 0, "Código", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 1, "Solicitação", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addMergedRegion(sheet, row, 2, 3, "Tipo", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 4, "Qt. PF", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 5, "Fator Ajuste", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 6, "Qt. PF Efetiva", ALIGN_CENTER_GREY_25_PERCENT_BOLD);           
            
            List<MensuracaoDTO> mensuracoes = mensuracaoService.listar(ordemServico.getNuOrdemServico());
            
            for (MensuracaoDTO mensuracao : mensuracoes) {
                row = addNewRow(sheet);
                addCell(row, 0, mensuracao.getCoOrdemServico(), ALIGN_CENTER);
                addCell(row, 1, mensuracao.getCoSolicitacao(), ALIGN_CENTER);
                addMergedRegion(sheet, row, 2, 3, mensuracao.getNoTipoQuantidade());
                addCell(row, 4, mensuracao.getQtSolicitacao(), STYLE_DECIMAL);
                addCell(row, 5, mensuracao.getVrAjusteFinanceiro(), STYLE_DECIMAL);
                addCell(row, 6, mensuracao.getQtSolicitacaoAjustado(), STYLE_DECIMAL);           
            }

            //
            // ETREGAS
            //
            addBlank(sheet);

            row = addNewRow(sheet);
            addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT -1 , "Entregas", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

            row = addNewRow(sheet);
            // co_ordem_servico co_entrega  de_situacao_entrega dh_prevista_entrega dh_efetiva_entrega  
            // % Entrega   % Executado de_situacao_entrega vr_preco    qt_solicitacao  vr_financeiro_entrega
            addCell(row, 0, "OS", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 1, "Entrega", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 2, "Situação", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 3, "Data Prevista", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 4, "Data Efetiva", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 5, "% Entrega", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 6, "% Executado", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 7, "Preço", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 8, "QT. PF Ajustado", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 9, "Valor Previsto", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            
            List<EntregaDemandaDTO> entregas = entregaDemandaService.listar(ordemServico.getNuOrdemServico());
            
            int posPrimeiraEntrega = row.getRowNum() + 2;
            int posUltimaEntrega = posPrimeiraEntrega - 1;
            
            for (EntregaDemandaDTO entrega : entregas) {
                row = addNewRow(sheet);
                addCell(row, 0, entrega.getCoOrdemServico(), ALIGN_CENTER);
                addCell(row, 1, entrega.getCoEntrega(), ALIGN_CENTER);
                addCell(row, 2, entrega.getDeSituacaoEntrega(), ALIGN_CENTER);
                addCell(row, 3, entrega.getDhEfetivaEntrega(), STYLE_DATE_TIME);
                addCell(row, 4, entrega.getDhEfetivaEntrega(), STYLE_DATE_TIME);
                addCell(row, 5, entrega.getPcEntrega(), STYLE_PERCENT);
                addCell(row, 6, entrega.getPcExecutado(), STYLE_PERCENT);
                addCell(row, 7, entrega.getVrPreco(), STYLE_DECIMAL);
                addCell(row, 8, mensuracaoService.calcularQtTotalPFAjustado(ordemServico.getNuOrdemServico()), STYLE_DECIMAL);
                
                // =I17*H17*G17*F17
                setCellFormula(sheet, row, //
                        String.format("I#*H#*G#*F#").replaceAll("#", String.valueOf(row.getRowNum()+1)), //
                        9, STYLE_DECIMAL);
                
                posUltimaEntrega++;
            }
            
            row = addNewRow(sheet);
            setCellFormula(sheet, row, //
                    String.format("SUM(J%s:J%s)", posPrimeiraEntrega, posUltimaEntrega), //
                    9, STYLE_DECIMAL);
            
            addBlank(sheet);
        }
		
        return result;
	}
}
