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
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.dto.VigenciaMembrosDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.indicador.Indicador;
import org.prometheus.indicador.IndicadorFactory;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioContext;
import org.prometheus.relatorios.RelatorioEquipe;
import org.prometheus.service.EquipeService;
import org.prometheus.service.IndicadorService;
import org.prometheus.service.PrecosEquipeService;
import org.prometheus.service.VigenciaMembrosService;
import org.prometheus.utils.DateUtil;
import org.prometheus.utils.Utils;
import org.prometheus.utils.XSSFSheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaCalculosAcionamento extends Relatorio {

	private static final Logger LOG = LogManager.getLogger(AbaCalculosAcionamento.class.getSimpleName());

	private static final int LAYOUT_COLUMNS_COUNT = 11;

	@Autowired
	private VigenciaMembrosService vigenciasMembrosService;

	@Autowired
	private EquipeService equipeService;

	@Autowired
	private PrecosEquipeService precoEquipeService;

	@Autowired
	private IndicadorService indicadorService;

	@Autowired
	private IndicadorFactory indicadorFactory;

	private RelatorioContext context;
	
	public Map<Long, InsumosFaturamentoDTO> write(XSSFWorkbook wb, String coEquipe, List<FaturamentoDTO> faturamentos)
			throws InvalidFormatException, IOException {

		Map<Long, InsumosFaturamentoDTO> result = new LinkedHashMap<Long, InsumosFaturamentoDTO>();

		EquipeDTO equipe = equipeService.findBy(coEquipe);

		XSSFSheet sheet = wb.createSheet(RelatorioEquipe.ABA_CALCULOS);

		context = new RelatorioContext(equipe, wb, sheet);

		carregarFontes(wb);
		carregarEstilos(wb);

		sheet.setZoom(90);
		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 75);

		XSSFRow row = null;

		// faturamentos
		for (FaturamentoDTO faturamento : faturamentos) {

			LOG.info(String.format("Memoria de Calculo - Contrato: %s Equipe: %s Fatura: %s - %s", //
					equipe.getNuContrato(), equipe.getNuEquipe() + " - " + equipe.getCoEquipe(), //
					faturamento.getNuFatura(), faturamento.getDeFatura()));

			context.setFaturamento(faturamento);

			InsumosFaturamentoDTO insumo = new InsumosFaturamentoDTO();

			List<VigenciaMembrosDTO> vigencias = vigenciasMembrosService.listar(faturamento.getNuFatura(),
					equipe.getNuEquipe());
			if (Utils.isEmpty(vigencias)) {
				continue;
			}

			insumo.setNuFatura(faturamento.getNuFatura());
			insumo.setConsiderarFNS("S".equalsIgnoreCase(faturamento.getIcConsiderarFns()));

			BigDecimal vrPrecoGrupo = precoEquipeService.getVrPrecoGrupoVigente(equipe.getNuEquipe(),
					faturamento.getNuFatura());
			insumo.setVrPreco(vrPrecoGrupo);

			row = addNewRow(sheet);
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1,
					"Fatura: " + faturamento.getNuFatura() + " - " + faturamento.getDeFatura(), //
					CellStylesEnum.LIGHT_BLUE_ALIGN_CENTER_BOLD);

			/**
			 * Vigência dos Membros do Faturamento
			 */
			addBlank(sheet);

			row = addNewRow(sheet);

			int posRowVigencias = row.getRowNum(); // preciso guardar essa posicao

			XSSFSheetUtils.addMergedRegion(sheet, row, 6, 10, "Vigência dos Membros do Faturamento", //
					CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

			XSSFCell cellQtEfetiva = null;

			if (Utils.isNotEmpty(vigencias)) {

				int pos = 6;

				row = addNewRow(sheet);
				addCell(row, pos++, "Dt. Início", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
				addCell(row, pos++, "Dt. Final", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
				addCell(row, pos++, "Qt. Membros", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
				addCell(row, pos++, "Qt. Dias Trab.", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
				addCell(row, pos++, "Qt. Efetiva", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

				XSSFCell cellInicioVigencias = null;
				XSSFCell cellFinalVigencias = null;

				for (VigenciaMembrosDTO vigencia : vigencias) {
					pos = 6;
					row = addNewRow(sheet);
					addCell(row, pos++, vigencia.getDtInicioVigenciaMembros());
					addCell(row, pos++, vigencia.getDtFinalVigenciaMembros());
					addCell(row, pos++, vigencia.getQtMembrosEquipe());
					addCell(row, pos++, vigencia.getQtDiasTrabalhados(), CellStylesEnum.DECIMAL);

					if (cellInicioVigencias == null) {
						cellInicioVigencias = addCell(row, pos++, vigencia.getQtEfetiva(), CellStylesEnum.DECIMAL5);
					} else {
						cellFinalVigencias = addCell(row, pos++, vigencia.getQtEfetiva(), CellStylesEnum.DECIMAL5);
					}
				}

				if (cellFinalVigencias == null) {
					cellFinalVigencias = cellInicioVigencias;
				}

				row = addNewRow(sheet);

				cellQtEfetiva = setCellFormula(sheet, row, String.format("sum(%s:%s)", //
						cellInicioVigencias.getReference(), cellFinalVigencias.getReference()), pos - 1,
						CellStylesEnum.DECIMAL5);

				insumo.setQtEfetiva(cellQtEfetiva);
			}

			/**
			 * Dias Trabalhados no Faturamento
			 */
			row = getRow(sheet, posRowVigencias++);
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, 4, "Dias Trabalhados no Faturamento",
					CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

			row = getRow(sheet, posRowVigencias++);
			addCell(row, 0, "Dt. Início", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
			addCell(row, 1, "Dt. Final", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
			addCell(row, 2, "Qt. Dias", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
			addCell(row, 3, "Qt. Dias Extra", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
			addCell(row, 4, "Qt. Dias Ausência", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

			row = getRow(sheet, posRowVigencias++);
			addCell(row, 0, faturamento.getDtInicioFatura());
			addCell(row, 1, faturamento.getDtFinalFatura());
			addCell(row, 2, faturamento.getQtDiasFaturamento(), CellStylesEnum.DECIMAL);
			insumo.setQtDiasExtra(addCell(row, 3, faturamento.getQtDiasServicoExtra(), CellStylesEnum.DECIMAL));
			insumo.setQtDiasAusencia(addCell(row, 4, faturamento.getQtDiasAusencia(), CellStylesEnum.DECIMAL));

			//
			context.nextRow(); // blank
			context.setRownum(cellQtEfetiva.getRowIndex());

			/**
			 * Indicadores
			 */
			if (!"S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {
				row = context.nextRow();
				XSSFSheetUtils.addMergedRegion(sheet, row, 0, 5,
						"Indicadores configurados para serem aplicados somente a partir de "
								+ DateUtil.format(faturamento.getDtConsiderarFns()),
						CellStylesEnum.NOTA_BOLD);
				context.nextRow();
			}

			if ("S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {

				//
				List<IndicadorContratoDTO> indicadores = indicadorService.listarIndicadoresContrato(coEquipe, false);

				for (IndicadorContratoDTO indicadorDTO : indicadores) {
					Indicador indicador = indicadorFactory.get(indicadorDTO, context);
					XSSFCell resultado = indicador.calcular();
					context.addIndicador(faturamento.getNuFatura(), indicadorDTO.getSgTipoIndicador(), resultado);
					context.nextRow(); // blank
				}

				context.nextRow(); // blank

				/**
				 * Média dos Indicadores
				 */
				context.setRownum(cellQtEfetiva.getRowIndex() + 1);

				indicadores = indicadorService.listarIndicadoresContrato(coEquipe, true);

				for (IndicadorContratoDTO indicadorDTO : indicadores) {
					Indicador indicador = indicadorFactory.get(indicadorDTO, context);
					XSSFCell resultado = indicador.calcular();
					insumo.setVrFns(resultado);
					context.addIndicador(faturamento.getNuFatura(), indicadorDTO.getSgTipoIndicador(), resultado);
					context.nextRow(); // blank
				}

				context.nextRow(); // blank

			}

			result.put(faturamento.getNuFatura(), insumo);

		} // faturamentos

		return result;
	}
}
