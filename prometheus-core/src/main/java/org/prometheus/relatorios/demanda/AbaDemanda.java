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
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.service.DemandaService;
import org.prometheus.service.EntregaDemandaService;
import org.prometheus.service.MensuracaoService;
import org.prometheus.service.OrdemServicoService;
import org.prometheus.utils.XSSFSheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AbaDemanda extends Relatorio {

	private static final int LAYOUT_COLUMNS_COUNT = 11;

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

		sheet.setZoom(90);
		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT + 1, 80);

		XSSFRow row = null;

		row = addNewRow(sheet);
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT, demanda.getCoDemanda(), CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

        //
        // CONTRATO
        //
        addBlank(sheet);

        row = addNewRow(sheet);
        XSSFSheetUtils.addMergedRegion(sheet, row, 0, 4, "Contrato", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);        

        row = addNewRow(sheet);
        addCell(row, 0, demanda.getCoContrato(), CellStylesEnum.NORMAL_ALIGN_CENTER);
        XSSFSheetUtils.addMergedRegion(sheet, row, 1, 4, demanda.getNoContrato(), CellStylesEnum.NORMAL_ALIGN_CENTER);		

        //
        //
        //
        addBlank(sheet);
        
		row = addNewRow(sheet);
		//co_demanda    no_modulo       sg_sistema  no_sistema      de_situacao_demanda
		addCell(row, 0, "Demanda", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		XSSFSheetUtils.addMergedRegion(sheet, row, 1, 3, "Módulo", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, 4, "Sigla", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		XSSFSheetUtils.addMergedRegion(sheet, row, 5, 7, "Sistema", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, 8, "Situação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet);
        //co_demanda    no_modulo       sg_sistema  no_sistema      de_situacao_demanda
        addCell(row, 0, demanda.getCoDemanda(), CellStylesEnum.NORMAL_ALIGN_CENTER);
        XSSFSheetUtils.addMergedRegion(sheet, row, 1, 3, demanda.getNoModulo(), CellStylesEnum.NORMAL_ALIGN_CENTER);
        addCell(row, 4, demanda.getSgSistema(), CellStylesEnum.NORMAL_ALIGN_CENTER);
        XSSFSheetUtils.addMergedRegion(sheet, row, 5, 7, demanda.getNoSistema(), CellStylesEnum.NORMAL_ALIGN_CENTER);
        addCell(row, 8, demanda.getDeSituacaoDemanda(), CellStylesEnum.NORMAL_ALIGN_CENTER);
		
        //
        // ORDENS DE SERVICO
        //
        addBlank(sheet);
        
        row = addNewRow(sheet);
        XSSFSheetUtils.addMergedRegion(sheet, row, 0, 9, "Ordens de Serviço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        
		List<OrdemServicoDTO> ordensServico = ordemServicoService.listar(coDemanda);

        row = addNewRow(sheet);
        // co_ordem_servico no_tipo_grupo           no_tipo_servico         de_tipo_forma   vr_estmo_ordem_servico
        addCell(row, 0, "Código", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        XSSFSheetUtils.addMergedRegion(sheet, row, 1, 3, "Grupo", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        XSSFSheetUtils.addMergedRegion(sheet, row, 4, 5, "Serviço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        XSSFSheetUtils.addMergedRegion(sheet, row, 6, 7, "Forma", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, 8, "Situação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, 9, "Valor previsto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);  		
		
        XSSFCell primeiraOS = null;
        XSSFCell ultimaOS = null;
        
		for (OrdemServicoDTO ordemServico : ordensServico) {

		    row = addNewRow(sheet);
	        // co_ordem_servico no_tipo_grupo           no_tipo_servico         de_tipo_forma   vr_estmo_ordem_servico
	        addCell(row, 0, ordemServico.getCoOrdemServico(), CellStylesEnum.NORMAL_ALIGN_CENTER);
	        XSSFSheetUtils.addMergedRegion(sheet, row, 1, 3, ordemServico.getNoTipoGrupo(), CellStylesEnum.NORMAL_ALIGN_CENTER);
	        XSSFSheetUtils.addMergedRegion(sheet, row, 4, 5, ordemServico.getNoTipoServico(), CellStylesEnum.NORMAL_ALIGN_CENTER);
	        XSSFSheetUtils.addMergedRegion(sheet, row, 6, 7, ordemServico.getDeTipoForma(), CellStylesEnum.NORMAL_ALIGN_CENTER);
	        addCell(row, 8, ordemServico.getDeStcoOrdemServico(), CellStylesEnum.NORMAL_ALIGN_CENTER);
	        if (primeiraOS == null) {
	            primeiraOS = addCell(row, 9, ordemServico.getVrEstmoOrdemServico(), CellStylesEnum.DECIMAL);
	            ultimaOS = primeiraOS;
	        }
	        else{
	            ultimaOS = addCell(row, 9, ordemServico.getVrEstmoOrdemServico(), CellStylesEnum.DECIMAL);
	        }
	        addCell(row, 9, ordemServico.getVrEstmoOrdemServico(), CellStylesEnum.DECIMAL);
		}
		
        row = addNewRow(sheet);
        addCell(row, 8, "TOTAL", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);		
        result = setCellFormula(sheet, row, //
                String.format("SUM(%s:%s)", primeiraOS.getReference(), ultimaOS.getReference()), //
                9, CellStylesEnum.DECIMAL);        
        
        addBlank(sheet);

        for (OrdemServicoDTO ordemServico : ordensServico) {
            
            row = addNewRow(sheet);
            XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT, "OS: " + //
                    ordemServico.getCoOrdemServico(), CellStylesEnum.LIGHT_BLUE_ALIGN_CENTER_BOLD);
            
            row = addNewRow(sheet);
            // co_ordem_servico no_tipo_grupo           no_tipo_servico         de_tipo_forma   vr_estmo_ordem_servico
            addCell(row, 0, "Código", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            XSSFSheetUtils.addMergedRegion(sheet, row, 1, 3, "Grupo", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            XSSFSheetUtils.addMergedRegion(sheet, row, 4, 6, "Serviço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            XSSFSheetUtils.addMergedRegion(sheet, row, 7, 9, "Forma", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 10, "Situação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 11, "Valor previsto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);          
            
            row = addNewRow(sheet);
            // co_ordem_servico no_tipo_grupo           no_tipo_servico         de_tipo_forma   vr_estmo_ordem_servico
            addCell(row, 0, ordemServico.getCoOrdemServico(), CellStylesEnum.NORMAL_ALIGN_CENTER);
            XSSFSheetUtils.addMergedRegion(sheet, row, 1, 3, ordemServico.getNoTipoGrupo(), CellStylesEnum.NORMAL_ALIGN_CENTER);
            XSSFSheetUtils.addMergedRegion(sheet, row, 4, 6, ordemServico.getNoTipoServico(), CellStylesEnum.NORMAL_ALIGN_CENTER);
            XSSFSheetUtils.addMergedRegion(sheet, row, 7, 9, ordemServico.getDeTipoForma(), CellStylesEnum.NORMAL_ALIGN_CENTER);
            addCell(row, 10, ordemServico.getDeStcoOrdemServico(), CellStylesEnum.NORMAL_ALIGN_CENTER);
            addCell(row, 11, ordemServico.getVrEstmoOrdemServico(), CellStylesEnum.DECIMAL);
            
            //
            // MENSURACAO
            //
            addBlank(sheet);

            row = addNewRow(sheet);
            XSSFSheetUtils.addMergedRegion(sheet, row, 0, 6, "Mensuração", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            
            row = addNewRow(sheet);
            // OS   Solicitação Qt. PF  Fator Ajuste    Qt. PF Efetiva
            addCell(row, 0, "Código", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 1, "Solicitação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            XSSFSheetUtils.addMergedRegion(sheet, row, 2, 3, "Tipo", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 4, "Qt. PF", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 5, "Fator Ajuste", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 6, "Qt. PF Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);           
            
            List<MensuracaoDTO> mensuracoes = mensuracaoService.listar(ordemServico.getNuOrdemServico());
            
            for (MensuracaoDTO mensuracao : mensuracoes) {
                row = addNewRow(sheet);
                addCell(row, 0, mensuracao.getCoOrdemServico(), CellStylesEnum.NORMAL_ALIGN_CENTER);
                addCell(row, 1, mensuracao.getCoSolicitacao(), CellStylesEnum.NORMAL_ALIGN_CENTER);
                XSSFSheetUtils.addMergedRegion(sheet, row, 2, 3, mensuracao.getNoTipoQuantidade(), CellStylesEnum.NORMAL_ALIGN_CENTER);
                addCell(row, 4, mensuracao.getQtSolicitacao(), CellStylesEnum.DECIMAL);
                addCell(row, 5, mensuracao.getVrAjusteFinanceiro(), CellStylesEnum.DECIMAL);
                addCell(row, 6, mensuracao.getQtSolicitacaoAjustado(), CellStylesEnum.DECIMAL);           
            }

            //
            // ETREGAS
            //
            addBlank(sheet);

            row = addNewRow(sheet);
            XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT, "Entregas", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

            row = addNewRow(sheet);
            // co_ordem_servico co_entrega  de_situacao_entrega dh_prevista_entrega dh_efetiva_entrega  
            // % Entrega   % Executado de_situacao_entrega vr_preco    qt_solicitacao  vr_financeiro_entrega
            int pos = 0;
            addCell(row, pos++, "OS", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Entrega", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Data Prevista", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Qt. Dias Pend.", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Data Prevista c/ Pendências", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Situação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Data Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "% Entrega", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "% Executado", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Preço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "QT. PF Ajustado", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Valor Previsto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            
            List<EntregaDemandaDTO> entregas = entregaDemandaService.listar(ordemServico.getNuOrdemServico());
            
            XSSFCell primeiraEntrega = null;
            XSSFCell ultimaEntrega = null;
            
            for (EntregaDemandaDTO entrega : entregas) {
                
                pos = 0;
                row = addNewRow(sheet);
                addCell(row, pos++, entrega.getCoOrdemServico(), CellStylesEnum.NORMAL_ALIGN_CENTER);
                addCell(row, pos++, entrega.getCoEntrega(), CellStylesEnum.NORMAL_ALIGN_CENTER);
                addCell(row, pos++, entrega.getDhPrevistaEntrega(), CellStylesEnum.DATE_TIME);
                addCell(row, pos++, entrega.getQtDiasPendencia(), CellStylesEnum.NORMAL_ALIGN_CENTER);
                addCell(row, pos++, entrega.getDhPrevistaPendencias(), CellStylesEnum.DATE_TIME);
                addCell(row, pos++, entrega.getDeSituacaoEntrega(), CellStylesEnum.NORMAL_ALIGN_CENTER);
                addCell(row, pos++, entrega.getDhEfetivaEntrega(), CellStylesEnum.DATE_TIME);
                addCell(row, pos++, entrega.getPcEntrega(), CellStylesEnum.PERCENT);
                addCell(row, pos++, entrega.getPcExecutado(), CellStylesEnum.PERCENT);
                addCell(row, pos++, entrega.getVrPreco(), CellStylesEnum.DECIMAL);
                addCell(row, pos++, mensuracaoService.calcularQtTotalPFAjustado(ordemServico.getNuOrdemServico()), CellStylesEnum.DECIMAL);
                
                // =I17*H17*G17*F17
                if (primeiraEntrega == null) {
                    primeiraEntrega = setCellFormula(sheet, row, //
                            String.format("H#*I#*J#*K#").replaceAll("#", String.valueOf(row.getRowNum()+1)), //
                            pos++, CellStylesEnum.DECIMAL);
                    ultimaEntrega = primeiraEntrega;
                }
                else {
                    ultimaEntrega = setCellFormula(sheet, row, //
                            String.format("H#*I#*J#*K#").replaceAll("#", String.valueOf(row.getRowNum()+1)), //
                            pos++, CellStylesEnum.DECIMAL);
                }
            }
            
            row = addNewRow(sheet);
            if (primeiraEntrega != null) {
                setCellFormula(sheet, row, //
                        String.format("SUM(%s:%s)", primeiraEntrega.getReference(), ultimaEntrega.getReference()), //
                        pos-1, CellStylesEnum.DECIMAL);
            }
            
            addBlank(sheet);
        }
		
        return result;
	}
}
