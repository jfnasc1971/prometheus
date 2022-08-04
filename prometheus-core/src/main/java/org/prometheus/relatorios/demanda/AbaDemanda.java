package org.prometheus.relatorios.demanda;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.DemandaDTO;
import org.prometheus.dto.EntregaDemandaDTO;
import org.prometheus.dto.OrdemServicoDTO;
import org.prometheus.dto.SolicitacaoDemandaDTO;
import org.prometheus.dto.TipoQuantidadeDemandaDTO;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.service.DemandaService;
import org.prometheus.service.EntregaDemandaService;
import org.prometheus.service.OrdemServicoService;
import org.prometheus.service.SolicitacaoDemandaService;
import org.prometheus.service.TipoQuantidadeDemandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AbaDemanda extends Relatorio {

	private static final int LAYOUT_COLUMNS_COUNT = 14;

	@Autowired
	private DemandaService demandaService;

	@Autowired
	private SolicitacaoDemandaService solicitacaoDemandaService;

	@Autowired
	private OrdemServicoService ordemServicoService;

	@Autowired
	private TipoQuantidadeDemandaService tipoQuantidadeDemandaService;

	@Autowired
	private EntregaDemandaService entregaDemandaService;

	public void write(XSSFWorkbook workBook, String coDemanda) throws Exception {

		DemandaDTO demanda = demandaService.listar(coDemanda);

		carregarFontes(workBook);

		carregarEstilos(workBook);

		XSSFSheet sheet = workBook.createSheet(coDemanda);

		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);

		XSSFRow row = null;

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT, demanda.getCoDemanda(), ALIGN_CENTER_GREY_40_PERCENT_BOLD);

		addBlank(sheet);

		row = addNewRow(sheet);
		addCell(row, 0, "Sistema", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, 1, demanda.getSgSistema());

		addBlank(sheet);

		List<SolicitacaoDemandaDTO> solicitacoes = solicitacaoDemandaService.listar(coDemanda);

		row = addNewRow(sheet);
		addCell(row, 0, "Código", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addMergedRegion(sheet, row, 1, 2, "Resumo", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

		for (SolicitacaoDemandaDTO solicitacao : solicitacoes) {
			row = addNewRow(sheet);
			addCell(row, 0, solicitacao.getCoSolicitacao());
			addMergedRegion(sheet, row, 1, 2, solicitacao.getNoSolicitacao());
		}

		addBlank(sheet);

		/**
		 * 
		 */
		List<OrdemServicoDTO> ordens = ordemServicoService.listar(coDemanda);

		row = addNewRow(sheet);
		addCell(row, 0, "OS", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addMergedRegion(sheet, row, 1, 2, "Grupo", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addMergedRegion(sheet, row, 3, 4, "Serviço", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addMergedRegion(sheet, row, 5, 7, "Forma Contratação", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, 8, "Valor Previsto", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

		for (OrdemServicoDTO os : ordens) {
			row = addNewRow(sheet);
			addCell(row, 0, os.getCoOrdemServico(), ALIGN_CENTER);
			addMergedRegion(sheet, row, 1, 2, os.getNoTipoGrupo(), ALIGN_CENTER);
			addMergedRegion(sheet, row, 3, 4, os.getNoTipoServico(), ALIGN_CENTER);
			addMergedRegion(sheet, row, 5, 7, os.getDeTipoForma(), ALIGN_CENTER);
			addCell(row, 8, os.getVrEstmoOrdemServico());

			addBlank(sheet);
			processarMensuracoes(sheet, demanda, os, solicitacoes);
			
			addBlank(sheet);
			processarEntregas(sheet, os);
			
		}

	}

	private void processarEntregas(XSSFSheet sheet, OrdemServicoDTO os) {

		XSSFRow row = addNewRow(sheet);
		row.setHeight((short) 600);

		int pos = 0;
		addCell(row, pos++, "Entrega", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "% Entrega", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "% Executado", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Dt. Início Atendimento", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Dt. Prevista", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Qt. Dias Pendência", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Dt. Prevista c/ Pendências", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Dt. Efetiva", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		
		List<EntregaDemandaDTO> entregas = entregaDemandaService.listar(os);
		
		for(EntregaDemandaDTO entrega:entregas) {
			
			row = addNewRow(sheet);

			pos = 0;
			addCell(row, pos++, entrega.getCoEntrega());
			addCell(row, pos++, entrega.getPcEntrega(), STYLE_PERCENT);
			addCell(row, pos++, entrega.getPcExecutado(), STYLE_PERCENT);
			addCell(row, pos++, entrega.getDhPrevistaInicioAtendimento(), STYLE_DATE_TIME);
			addCell(row, pos++, entrega.getDhPrevistaEntrega(), STYLE_DATE_TIME);
			addCell(row, pos++, entrega.getQtDiasPendencia());
			addCell(row, pos++, entrega.getDhPrevistaEntregaPendencia(), STYLE_DATE_TIME);
			addCell(row, pos++, entrega.getDhEfetivaEntrega(), STYLE_DATE_TIME);
			
		}
		
	}

	private void processarMensuracoes(XSSFSheet sheet, DemandaDTO demanda, OrdemServicoDTO os,
			List<SolicitacaoDemandaDTO> solicitacoes) {

		XSSFRow row = addNewRow(sheet);
		row.setHeight((short) 900);

		List<TipoQuantidadeDemandaDTO> tiposQuantidade = tipoQuantidadeDemandaService.listar(demanda.getCoDemanda());

		int pos = 0;
		addCell(row, pos++, "Solicitação", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Mensuração", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		for (TipoQuantidadeDemandaDTO tipo : tiposQuantidade) {
			addCell(row, pos++, tipo.getNoTipoQuantidade(), ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		}

		for (SolicitacaoDemandaDTO solicitacao : solicitacoes) {
			row = addNewRow(sheet);
			pos = 0;
			addCell(row, pos++, solicitacao.getCoSolicitacao());
			addCell(row, pos++, solicitacaoDemandaService.getMensuracao(demanda, os, solicitacao));
			for(TipoQuantidadeDemandaDTO tipo:tiposQuantidade) {
				addCell(row, pos++, solicitacaoDemandaService.getQuantidade(demanda, os, solicitacao, tipo));
			}
		}
	}

}
