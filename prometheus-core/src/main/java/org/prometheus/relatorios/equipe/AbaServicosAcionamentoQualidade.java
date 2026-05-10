package org.prometheus.relatorios.equipe;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.EntregaDTO;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioEquipe;
import org.prometheus.service.EntregaServiceQualidade;
import org.prometheus.service.EquipeService;
import org.prometheus.utils.Utils;
import org.prometheus.utils.XSSFSheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaServicosAcionamentoQualidade extends Relatorio {

	private static final Logger LOG = LogManager.getLogger(AbaServicosAcionamentoQualidade.class.getSimpleName());
	
	private static final int LAYOUT_COLUMNS_COUNT = 12;

	@Autowired
	private EquipeService equipeService;

	@Autowired
	private EntregaServiceQualidade entregaService;

	Map<Long, List<XSSFCell>> listaIEQQL = null;
	Map<Long, List<XSSFCell>> listaIEQQPZ = null;
	Map<Long, List<XSSFCell>> listaIEQQET = null;

	Map<Long, List<XSSFCell>> listaMediaIEQQL = null;
	Map<Long, List<XSSFCell>> listaMediaIEQQPZ = null;
	Map<Long, List<XSSFCell>> listaMediaIEQQET = null;

	public Map<Long, InsumosFaturamentoDTO> write(XSSFWorkbook wb, String coEquipe, List<FaturamentoDTO> faturamentos)
			throws InvalidFormatException, IOException {

		Map<Long, InsumosFaturamentoDTO> result = new LinkedHashMap<Long, InsumosFaturamentoDTO>();

		EquipeDTO equipe = equipeService.findBy(coEquipe);

		XSSFSheet sheet = wb.createSheet(RelatorioEquipe.ABA_SERVICOS);

		carregarFontes(wb);
		carregarEstilos(wb);

		sheet.setZoom(90);
		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 80);

		XSSFRow row = null;

		// faturamentos
		for (FaturamentoDTO faturamento : faturamentos) {

		    InsumosFaturamentoDTO insumo = new InsumosFaturamentoDTO();
		    
		    insumo.setNuFatura(faturamento.getNuFatura());
		    insumo.setNuEquipe(equipe.getNuEquipe());
		    
			LOG.info(String.format("Contrato: %s Equipe: %s Fatura: %s - %s", //
					equipe.getNuContrato(), equipe.getNuEquipe() + " - " + equipe.getCoEquipe(), //
					faturamento.getNuFatura(), faturamento.getDeFatura()));

			row = addNewRow(sheet);
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1,
					"Fatura: " + faturamento.getNuFatura() + " - " + faturamento.getDeFatura(), //
					 CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);

			addBlank(sheet);

			listarServicosEntregues(equipe, sheet, faturamento);
			listarServicosDevolvidos(equipe, sheet, faturamento);
			listarServicosAtrasados(equipe, sheet, faturamento);
			listarServicosEstoque(equipe, sheet, faturamento);
			listarServicosContratados(equipe, sheet, faturamento);

			addBlank(sheet);
			
			result.put(faturamento.getNuFatura(), insumo);

		} // faturamentos

		return result;
	}

	private XSSFCell listarServicosAtrasados(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

		List<EntregaDTO> servicosEntreguesComAtraso = entregaService
				.listarServicosEntreguesComAtraso(equipe.getNuEquipe(), faturamento.getNuFatura());

		XSSFRow row = addNewRow(sheet);
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, "Serviços Atrasados", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet);
		int pos = 0;
		addCell(row, pos++, "Solicitação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos + 2, "Resumo", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		pos = pos + 3;
		addCell(row, pos++, "Situação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Data Prevista", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Qt. Dias Pend.", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Data c/ Pendências", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Data Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Compõe FNS", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Falha Caixa", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

		if (Utils.isEmpty(servicosEntreguesComAtraso)) {
			row = addNewRow(sheet);
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, "Nenhuma entrega registrada.");
		}

		for (EntregaDTO e : servicosEntreguesComAtraso) {
			row = addNewRow(sheet);
			pos = 0;
			addCell(row, pos++, e.getCoSolicitacao());
			XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos + 2, e.getNoSolicitacao());
			pos = pos + 3;
			addCell(row, pos++, e.getDeSituacaoSolicitacao(), CellStylesEnum.NORMAL_ALIGN_CENTER);
			addCell(row, pos++, e.getDhPrevista());
			addCell(row, pos++, e.getQtDiasPendencia());
			addCell(row, pos++, e.getDhPrevistaPendencias());
			addCell(row, pos++, e.getDhEfetiva());
			addCell(row, pos++, e.getIcServicoCompoeCalculoFns(), CellStylesEnum.NORMAL_ALIGN_CENTER);
			addCell(row, pos++, e.getCoFalhaSolicitacao(), CellStylesEnum.NORMAL_ALIGN_CENTER);
		}

		row = addNewRow(sheet);
		addCell(row, 0, "Quantidade", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

		addBlank(sheet);

		XSSFCell cellQtServicosEntreguesComAtraso = addCell(row, 1, BigDecimal.valueOf(servicosEntreguesComAtraso.size()));
		return cellQtServicosEntreguesComAtraso;
	}

	private XSSFCell listarServicosEstoque(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {
		List<EntregaDTO> servicosEntreguesEstoque = entregaService.listarServicosEntreguesEstoque(equipe.getNuEquipe(),
				faturamento.getNuFatura());

		XSSFRow row = addNewRow(sheet);
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, "Estoque de Serviços", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet);
		int pos = 0;
		addCell(row, pos++, "Solicitação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos + 2, "Resumo", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		pos = pos + 3;
		addCell(row, pos++, "Situação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Data Prevista", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Qt. Dias Pend.", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Data c/ Pendências", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Data Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Compõe FNS", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Falha Caixa", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

		if (Utils.isEmpty(servicosEntreguesEstoque)) {
			row = addNewRow(sheet);
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, "Nenhuma entrega registrada.");
		}

		for (EntregaDTO e : servicosEntreguesEstoque) {
			row = addNewRow(sheet);
			pos = 0;
			addCell(row, pos++, e.getCoSolicitacao());
			XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos + 2, e.getNoSolicitacao());
			pos = pos + 3;
			addCell(row, pos++, e.getDeSituacaoSolicitacao(), CellStylesEnum.NORMAL_ALIGN_CENTER);
			addCell(row, pos++, e.getDhPrevista());
			addCell(row, pos++, e.getQtDiasPendencia());
			addCell(row, pos++, e.getDhPrevistaPendencias());
			addCell(row, pos++, e.getDhEfetiva());
			addCell(row, pos++, e.getIcServicoCompoeCalculoFns(), CellStylesEnum.NORMAL_ALIGN_CENTER);
			addCell(row, pos++, e.getCoFalhaSolicitacao(), CellStylesEnum.NORMAL_ALIGN_CENTER);
		}

		row = addNewRow(sheet);
		addCell(row, 0, "Quantidade", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

		addBlank(sheet);

		XSSFCell cellQtServicosEntreguesEstoque = addCell(row, 1, BigDecimal.valueOf(servicosEntreguesEstoque.size()));
		return cellQtServicosEntreguesEstoque;
	}

	private XSSFCell listarServicosContratados(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

		List<EntregaDTO> servicosContratados = entregaService.listarServicosContratados(equipe.getNuEquipe(),
				faturamento.getNuFatura());

		XSSFRow row = addNewRow(sheet);
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, "Serviços Contratados", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet);
		int pos = 0;
		addCell(row, pos++, "Solicitação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos + 2, "Resumo", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		pos = pos + 3;
		addCell(row, pos++, "Situação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Data Prevista", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Qt. Dias Pend.", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Data c/ Pendências", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Data Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Compõe FNS", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Falha Caixa", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		
		if (Utils.isEmpty(servicosContratados)) {
			row = addNewRow(sheet);
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, "Nenhuma entrega registrada.");
		}

		for (EntregaDTO e : servicosContratados) {
			row = addNewRow(sheet);
			pos = 0;
			XSSFCell coSolicitacao = addCell(row, pos++, e.getCoSolicitacao());
			
			if("S".equalsIgnoreCase(e.getIcEntregaForaPeriodo())) {
				setCellComment(coSolicitacao, "Entregue fora do período.");	
			}
			
			XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos + 2, Utils.isEmpty(e.getNoSolicitacao()) ? e.getDeSolicitacao() : e.getNoSolicitacao());
			pos = pos + 3;
			addCell(row, pos++, e.getDeSituacaoSolicitacao(), CellStylesEnum.NORMAL_ALIGN_CENTER);
			addCell(row, pos++, e.getDhPrevista());
			addCell(row, pos++, e.getQtDiasPendencia());
			addCell(row, pos++, e.getDhPrevistaPendencias());
			addCell(row, pos++, e.getDhEfetiva());
			addCell(row, pos++, e.getIcServicoCompoeCalculoFns(), CellStylesEnum.NORMAL_ALIGN_CENTER);
			addCell(row, pos++, e.getCoFalhaSolicitacao(), CellStylesEnum.NORMAL_ALIGN_CENTER);
		}

		row = addNewRow(sheet);
		addCell(row, 0, "Quantidade", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

		addBlank(sheet);

		XSSFCell cellQtServicosContratados = addCell(row, 1, BigDecimal.valueOf(servicosContratados.size()));
		return cellQtServicosContratados;
	}

	private XSSFCell listarServicosDevolvidos(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

		List<EntregaDTO> servicosDevolvidos = entregaService.listarServicosDevolvidos(equipe.getNuEquipe(),
				faturamento.getNuFatura());

		XSSFRow row = addNewRow(sheet);
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, "Serviços Devolvidos", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet);
		int pos = 0;
		addCell(row, pos++, "Solicitação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos + 2, "Resumo", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		pos = pos + 3;
		addCell(row, pos++, "Situação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Data Prevista", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Qt. Dias Pend.", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Data c/ Pendências", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Data Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Compõe FNS", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Falha Caixa", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

		if (Utils.isEmpty(servicosDevolvidos)) {
			row = addNewRow(sheet);
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, "Nenhuma entrega registrada.");
		}

		for (EntregaDTO e : servicosDevolvidos) {
			row = addNewRow(sheet);
			pos = 0;
			addCell(row, pos++, e.getCoSolicitacao());
			XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos + 2, e.getNoSolicitacao());
			pos = pos + 3;
			addCell(row, pos++, e.getDeSituacaoSolicitacao(), CellStylesEnum.NORMAL_ALIGN_CENTER);
			addCell(row, pos++, e.getDhPrevista());
			addCell(row, pos++, e.getQtDiasPendencia());
			addCell(row, pos++, e.getDhPrevistaPendencias());
			addCell(row, pos++, e.getDhEfetiva());
			addCell(row, pos++, e.getIcServicoCompoeCalculoFns(), CellStylesEnum.NORMAL_ALIGN_CENTER);
			addCell(row, pos++, e.getCoFalhaSolicitacao(), CellStylesEnum.NORMAL_ALIGN_CENTER);
		}

		row = addNewRow(sheet);
		addCell(row, 0, "Quantidade", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

		addBlank(sheet);

		XSSFCell cellQtServicosDevolvidos = addCell(row, 1, BigDecimal.valueOf(servicosDevolvidos.size()));
		return cellQtServicosDevolvidos;
	}

	private XSSFCell listarServicosEntregues(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

		List<EntregaDTO> servicosEntregues = entregaService.listarServicosEntregues(equipe.getNuEquipe(),
				faturamento.getNuFatura());

		XSSFRow row = addNewRow(sheet);
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, "Serviços Entregues", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet);
		int pos = 0;
		addCell(row, pos++, "Solicitação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos + 2, "Resumo", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		pos = pos + 3;
		addCell(row, pos++, "Situação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Data Prevista", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Qt. Dias Pend.", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Data c/ Pendências", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Data Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Compõe FNS", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Falha Caixa", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

		if (Utils.isEmpty(servicosEntregues)) {
			row = addNewRow(sheet);
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, "Nenhuma entrega registrada.");
		}

		for (EntregaDTO e : servicosEntregues) {
			row = addNewRow(sheet);
			pos = 0;
			addCell(row, pos++, e.getCoSolicitacao());
			XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos + 2, e.getNoSolicitacao());
			pos = pos + 3;
			addCell(row, pos++, e.getDeSituacaoSolicitacao(), CellStylesEnum.NORMAL_ALIGN_CENTER);
			addCell(row, pos++, e.getDhPrevista());
			addCell(row, pos++, e.getQtDiasPendencia());
			addCell(row, pos++, e.getDhPrevistaPendencias());
			addCell(row, pos++, e.getDhEfetiva());
			addCell(row, pos++, e.getIcServicoCompoeCalculoFns(), CellStylesEnum.NORMAL_ALIGN_CENTER);
			addCell(row, pos++, e.getCoFalhaSolicitacao(), CellStylesEnum.NORMAL_ALIGN_CENTER);
		}

		row = addNewRow(sheet);
		addCell(row, 0, "Quantidade", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

		addBlank(sheet);

		XSSFCell cellQtServicosEntregues = addCell(row, 1, BigDecimal.valueOf(servicosEntregues.size()));
		return cellQtServicosEntregues;
	}

}
