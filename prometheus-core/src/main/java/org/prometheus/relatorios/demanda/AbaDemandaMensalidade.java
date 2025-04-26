package org.prometheus.relatorios.demanda;

import java.math.BigDecimal;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.DemandaDTO;
import org.prometheus.dto.EntregaDemandaDTO;
import org.prometheus.dto.OrdemServicoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.service.DemandaService;
import org.prometheus.service.EntregaDemandaService;
import org.prometheus.service.MensuracaoService;
import org.prometheus.service.OrdemServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AbaDemandaMensalidade extends Relatorio {

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
		addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT, demanda.getCoDemanda(), CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

		addBlank(sheet);

		row = addNewRow(sheet);
		//co_demanda    no_modulo       sg_sistema  no_sistema      de_situacao_demanda
		addCell(row, 0, "Demanda", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addMergedRegion(sheet, row, 1, 3, "Módulo", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, 4, "Sigla", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addMergedRegion(sheet, row, 5, 7, "Sistema", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, 8, "Situação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet);
        //co_demanda    no_modulo       sg_sistema  no_sistema      de_situacao_demanda
        addCell(row, 0, demanda.getCoDemanda(), CellStylesEnum.NORMAL_ALIGN_CENTER);
        addMergedRegion(sheet, row, 1, 3, demanda.getNoModulo(), CellStylesEnum.NORMAL_ALIGN_CENTER);
        addCell(row, 4, demanda.getSgSistema(), CellStylesEnum.NORMAL_ALIGN_CENTER);
        addMergedRegion(sheet, row, 5, 7, demanda.getNoSistema(), CellStylesEnum.NORMAL_ALIGN_CENTER);
        addCell(row, 8, demanda.getDeSituacaoDemanda(), CellStylesEnum.NORMAL_ALIGN_CENTER);
		
        addBlank(sheet);

        row = addNewRow(sheet);
        addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT, "Ordens de Serviço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        
		List<OrdemServicoDTO> ordensServico = ordemServicoService.listar(coDemanda);

        row = addNewRow(sheet);
        // co_ordem_servico no_tipo_grupo           no_tipo_servico         de_tipo_forma   vr_estmo_ordem_servico
        addCell(row, 0, "Código", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addMergedRegion(sheet, row, 1, 3, "Grupo", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addMergedRegion(sheet, row, 4, 6, "Serviço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addMergedRegion(sheet, row, 7, 9, "Forma", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, 10, "Valor previsto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);  		
		
        int posPrimeiraOS = row.getRowNum() + 2;
        int posUltimaOS = posPrimeiraOS - 1;
        
		for (OrdemServicoDTO ordemServico : ordensServico) {

		    row = addNewRow(sheet);
	        // co_ordem_servico no_tipo_grupo           no_tipo_servico         de_tipo_forma   vr_estmo_ordem_servico
	        addCell(row, 0, ordemServico.getCoOrdemServico(), CellStylesEnum.NORMAL_ALIGN_CENTER);
	        addMergedRegion(sheet, row, 1, 3, ordemServico.getNoTipoGrupo(), CellStylesEnum.NORMAL_ALIGN_CENTER);
	        addMergedRegion(sheet, row, 4, 6, ordemServico.getNoTipoServico(), CellStylesEnum.NORMAL_ALIGN_CENTER);
	        addMergedRegion(sheet, row, 7, 9, ordemServico.getDeTipoForma(), CellStylesEnum.NORMAL_ALIGN_CENTER);
	        addCell(row, 10, ordemServico.getVrEstmoOrdemServico(), CellStylesEnum.DECIMAL);
	        posUltimaOS++;
		}
		
        row = addNewRow(sheet);
        addCell(row, 9, "TOTAL", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);		
        result = setCellFormula(sheet, row, //
                String.format("SUM(K%s:K%s)", posPrimeiraOS, posUltimaOS), //
                10, CellStylesEnum.DECIMAL);        
        
        addBlank(sheet);

        for (OrdemServicoDTO ordemServico : ordensServico) {
            
            row = addNewRow(sheet);
            addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT, "OS: " + //
                    ordemServico.getCoOrdemServico(), CellStylesEnum.LIGHT_BLUE_ALIGN_CENTER_BOLD);
            
            row = addNewRow(sheet);
            // co_ordem_servico no_tipo_grupo           no_tipo_servico         de_tipo_forma   vr_estmo_ordem_servico
            addCell(row, 0, "Código", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addMergedRegion(sheet, row, 1, 3, "Grupo", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addMergedRegion(sheet, row, 4, 6, "Serviço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addMergedRegion(sheet, row, 7, 9, "Forma", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 10, "Valor previsto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);          
            
            row = addNewRow(sheet);
            // co_ordem_servico no_tipo_grupo           no_tipo_servico         de_tipo_forma   vr_estmo_ordem_servico
            addCell(row, 0, ordemServico.getCoOrdemServico(), CellStylesEnum.NORMAL_ALIGN_CENTER);
            addMergedRegion(sheet, row, 1, 3, ordemServico.getNoTipoGrupo(), CellStylesEnum.NORMAL_ALIGN_CENTER);
            addMergedRegion(sheet, row, 4, 6, ordemServico.getNoTipoServico(), CellStylesEnum.NORMAL_ALIGN_CENTER);
            addMergedRegion(sheet, row, 7, 9, ordemServico.getDeTipoForma(), CellStylesEnum.NORMAL_ALIGN_CENTER);
            addCell(row, 10, ordemServico.getVrEstmoOrdemServico(), CellStylesEnum.DECIMAL);

            //
            // ETREGAS
            //
            addBlank(sheet);

            row = addNewRow(sheet);
            addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT -1 , "Entregas", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

            row = addNewRow(sheet);
            // co_ordem_servico co_entrega  de_situacao_entrega dh_prevista_entrega dh_efetiva_entrega  
            // % Entrega   % Executado de_situacao_entrega vr_preco    qt_solicitacao  vr_financeiro_entrega
            addCell(row, 0, "OS", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 1, "Entrega", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 2, "Situação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 3, "Data Prevista", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 4, "Data Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 5, "% Entrega", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 6, "% Executado", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 7, "Preço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 8, "Mensuração", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 9, "Valor Previsto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            
            List<EntregaDemandaDTO> entregas = entregaDemandaService.listar(ordemServico.getNuOrdemServico());
            
            int posPrimeiraEntrega = row.getRowNum() + 2;
            int posUltimaEntrega = posPrimeiraEntrega - 1;
            
            for (EntregaDemandaDTO entrega : entregas) {
                row = addNewRow(sheet);
                addCell(row, 0, entrega.getCoOrdemServico(), CellStylesEnum.NORMAL_ALIGN_CENTER);
                addCell(row, 1, entrega.getCoEntrega(), CellStylesEnum.NORMAL_ALIGN_CENTER);
                addCell(row, 2, entrega.getDeSituacaoEntrega(), CellStylesEnum.NORMAL_ALIGN_CENTER);
                addCell(row, 3, entrega.getDhEfetivaEntrega(), CellStylesEnum.DATE_TIME);
                addCell(row, 4, entrega.getDhEfetivaEntrega(), CellStylesEnum.DATE_TIME);
                addCell(row, 5, entrega.getPcEntrega(), CellStylesEnum.PERCENT);
                addCell(row, 6, entrega.getPcExecutado(), CellStylesEnum.PERCENT);
                addCell(row, 7, entrega.getVrPreco(), CellStylesEnum.DECIMAL);
                addCell(row, 8, BigDecimal.ONE, CellStylesEnum.DECIMAL);
                
                // =I17*H17*G17*F17
                setCellFormula(sheet, row, //
                        String.format("I#*H#*G#*F#").replaceAll("#", String.valueOf(row.getRowNum()+1)), //
                        9, CellStylesEnum.DECIMAL);
                
                posUltimaEntrega++;
            }
            
            row = addNewRow(sheet);
            setCellFormula(sheet, row, //
                    String.format("SUM(J%s:J%s)", posPrimeiraEntrega, posUltimaEntrega), //
                    9, CellStylesEnum.DECIMAL);
            
            addBlank(sheet);
        }
		
        return result;
	}
}
