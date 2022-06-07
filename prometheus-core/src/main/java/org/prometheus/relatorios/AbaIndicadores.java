package org.prometheus.relatorios;

import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaixaIndicadorDTO;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.dto.SituacaoEntregaIndicadorDTO;
import org.prometheus.service.EquipeService;
import org.prometheus.service.FaixaIndicadorService;
import org.prometheus.service.IndicadorService;
import org.prometheus.service.SituacaoEntregaIndicadorService;
import org.prometheus.utils.Utils;
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

		List<IndicadorContratoDTO> indicadores = indicadorService.listarIndicadoresContrato(equipe.getCoEquipe());

		int rownum = 0;

		row = addNewRow(sheet, rownum++);
		addMergedRegion(sheet, row, 0, 4, "Indicadores", ALIGN_CENTER_GREY_40_PERCENT_BOLD);

		pos = 0;
		row = addNewRow(sheet, rownum++);
		addCell(row, pos++, "Indicador", ALIGN_CENTER_GREY_25_PERCENT_BOLD, 70);
		addCell(row, pos++, "Peso", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Fator Ajuste", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Limite Máximo", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Qtd. Fat. Média", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

		for (IndicadorContratoDTO indicador : indicadores) {
			pos = 0;
			row = addNewRow(sheet, rownum++);

			addCell(row, pos++, indicador.getSgTipoIndicador() + " - " + indicador.getNoTipoIndicador());
			setNoNull(row, pos++, indicador.getVrPesoTipoIndicador());
			setNoNull(row, pos++, indicador.getVrFatorAjuste(), STYLE_DECIMAL_5DIGITS);
			setNoNull(row, pos++, indicador.getVrLimiteMaximoIndicador());
			setNoNull(row, pos++, indicador.getQtFaturamento());
		}

		/**
		 * Situações Consideradas para Serviços Contratados
		 */
		addNewRow(sheet, rownum++);
		row = addNewRow(sheet, rownum++);
		addMergedRegion(sheet, row, 0, 2, "Situações Consideradas para Serviços Contratados",
				ALIGN_CENTER_GREY_40_PERCENT_BOLD);

		List<SituacaoEntregaIndicadorDTO> situacoes = situacaoEntregaIndicadorService.listar(equipe.getNuEquipe());

		if (Utils.isEmpty(situacoes)) {
			row = addNewRow(sheet, rownum++);
			addMergedRegion(sheet, row, 0, 2, "Nenhuma situação configurada.");
		}

		if (Utils.isNotEmpty(situacoes)) {

			row = addNewRow(sheet, rownum++);
			addCell(row, 0, "Indicador", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
			addMergedRegion(sheet, row, 1, 2, "Situação", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

			for (SituacaoEntregaIndicadorDTO situacao : situacoes) {
				row = addNewRow(sheet, rownum++);
				addCell(row, 0, situacao.getSgTipoIndicador() + //
						" - " + situacao.getNoTipoIndicador());
				addMergedRegion(sheet, row, 1, 2, situacao.getNuSituacaoAtendimento() + //
						" - " + situacao.getNoSituacaoAtendimento());
			}
		}

		/**
		 * Valores Faixas Indicador
		 */
		rownum = 0;

		List<FaixaIndicadorDTO> faixas = faixaIndicadorService.listar(equipe.getNuEquipe());

		row = addNewRow(sheet, rownum++);
		addMergedRegion(sheet, row, 6, 8, "Valores Faixas Indicador", ALIGN_CENTER_GREY_40_PERCENT_BOLD);

		row = addNewRow(sheet, rownum++);
		pos = 6;
		addCell(row, pos++, "Vr. Inicial", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Vr. Final", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Valor", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

		for (FaixaIndicadorDTO faixa : faixas) {
			pos = 6;
			row = addNewRow(sheet, rownum++);

			addCell(row, pos++, faixa.getQtInicioFaixa(), STYLE_DECIMAL);
			addCell(row, pos++, faixa.getQtFinalFaixa(), STYLE_DECIMAL);
			addCell(row, pos++, faixa.getVrFatorFaixa(), STYLE_DECIMAL);
		}

		addNewRow(sheet, rownum++);

	}

}
