package org.prometheus.relatorios.equipe;

import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaixaIndicadorDTO;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.dto.SituacaoEntregaIndicadorDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.service.EquipeService;
import org.prometheus.service.FaixaIndicadorService;
import org.prometheus.service.IndicadorService;
import org.prometheus.service.SituacaoEntregaIndicadorService;
import org.prometheus.utils.Utils;
import org.prometheus.utils.XSSFSheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaIndicadores extends Relatorio {

	private static final int LAYOUT_COLUMNS_COUNT = 20;

	@Autowired
	private EquipeService equipeService;

	@Autowired
	private SituacaoEntregaIndicadorService situacaoEntregaIndicadorService;

	@Autowired
	private IndicadorService indicadorService;

	@Autowired
	private FaixaIndicadorService faixaIndicadorService;

	public void write(XSSFWorkbook workBook, String coEquipe) throws InvalidFormatException, IOException {

		EquipeDTO equipe = equipeService.findBy(coEquipe);

		carregarFontes(workBook);

		carregarEstilos(workBook);

		XSSFSheet sheet = workBook.createSheet("Configuração dos Indicadores");

		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);
		setColumnWidth(sheet, 5, 10);

		/**
		 * Indicadores
		 */
		XSSFRow row = addNewRow(sheet);

		int pos = 0;

		List<IndicadorContratoDTO> indicadores = indicadorService.listarIndicadores(equipe.getCoEquipe());

		int rownum = 0;

		row = addNewRow(sheet, rownum++);
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, 4, "Indicadores", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		pos = 0;
		row = addNewRow(sheet, rownum++);
		addCell(row, pos++, "Indicador", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD, 70);
		addCell(row, pos++, "Peso", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Fator Ajuste", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Limite Máximo", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Qtd. Fat. Média", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

		for (IndicadorContratoDTO indicador : indicadores) {
			pos = 0;
			row = addNewRow(sheet, rownum++);

			addCell(row, pos++, indicador.getSgTipoIndicador() + " - " + indicador.getNoTipoIndicador());
			setNoNull(row, pos++, indicador.getVrPesoTipoIndicador());
			setNoNull(row, pos++, indicador.getVrFatorAjuste(), CellStylesEnum.DECIMAL5);
			setNoNull(row, pos++, indicador.getVrLimiteMaximoIndicador());
			setNoNull(row, pos++, indicador.getQtFaturamento());
		}

		/**
		 * Situações Consideradas para Serviços Contratados
		 */
		addNewRow(sheet, rownum++);
		row = addNewRow(sheet, rownum++);
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, "Situações Consideradas para Serviços Contratados",
				CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		List<SituacaoEntregaIndicadorDTO> situacoes = situacaoEntregaIndicadorService.listar(equipe.getNuEquipe());

		if (Utils.isEmpty(situacoes)) {
			row = addNewRow(sheet, rownum++);
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, "Nenhuma situação configurada.");
		}

//		if (Utils.isNotEmpty(situacoes)) {
//
//			row = addNewRow(sheet, rownum++);
//			addCell(row, 0, "Indicador", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
//			XSSFSheetUtils.addMergedRegion(sheet, row, 1, 2, "Situação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
//
//			for (SituacaoEntregaIndicadorDTO situacao : situacoes) {
//				row = addNewRow(sheet, rownum++);
//				addCell(row, 0, situacao.getSgTipoIndicador() + //
//						" - " + situacao.getNoTipoIndicador());
//				XSSFSheetUtils.addMergedRegion(sheet, row, 1, 2, situacao.getNuSituacaoAtendimento() + //
//						" - " + situacao.getNoSituacaoAtendimento());
//			}
//		}

		/**
		 * Valores Faixas Indicador
		 */
		rownum = 0;

		List<FaixaIndicadorDTO> faixas = faixaIndicadorService.listar(equipe.getNuEquipe());

		row = addNewRow(sheet, rownum++);
		XSSFSheetUtils.addMergedRegion(sheet, row, 6, 8, "Valores Faixas Indicador", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet, rownum++);
		pos = 6;
		addCell(row, pos++, "Vr. Inicial", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Vr. Final", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Valor", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

		for (FaixaIndicadorDTO faixa : faixas) {
			pos = 6;
			row = addNewRow(sheet, rownum++);

			addCell(row, pos++, faixa.getQtInicioFaixa(), CellStylesEnum.DECIMAL);
			addCell(row, pos++, faixa.getQtFinalFaixa(), CellStylesEnum.DECIMAL);
			addCell(row, pos++, faixa.getVrFatorFaixa(), CellStylesEnum.DECIMAL);
		}

		addNewRow(sheet, rownum++);

	}

}
