package org.prometheus.relatorios.equipe;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.AusenciaExtraFaturamentoDTO;
import org.prometheus.dto.DetalheFaturamentoDTO;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.IndicadorDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.service.AusenciaExtraFaturamentoService;
import org.prometheus.service.DetalheFaturamentoService;
import org.prometheus.service.EquipeService;
import org.prometheus.service.IndicadorService;
import org.prometheus.service.PrecosEquipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaMemoriaCalculo extends Relatorio {

    private static final Logger LOG = LogManager.getLogger(AbaMemoriaCalculo.class.getSimpleName());
    
	private static final int LAYOUT_COLUMNS_COUNT = 8;

	@Autowired
	private DetalheFaturamentoService detalheService;

	@Autowired
	private AusenciaExtraFaturamentoService ausenciaExtraService;

	@Autowired
	private IndicadorService indicadorService;

	@Autowired
	private PrecosEquipeService precoEquipeService;

	@Autowired
	private EquipeService equipeService;

	public void write(XSSFWorkbook workBook, String coEquipe, List<FaturamentoDTO> faturamentos)
			throws InvalidFormatException, IOException {

		EquipeDTO equipe = equipeService.findBy(coEquipe);

		carregarFontes(workBook);

		carregarEstilos(workBook);

		XSSFSheet sheet = workBook.createSheet("Memória de Cálculo");

		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);

		XSSFRow row = addNewRow(sheet);

		addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, coEquipe, //
		        CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		for (FaturamentoDTO faturamento : faturamentos) {

			LOG.info(String.format("Contrato: %s - Fatura: %s - %s", //
					equipe.getNuContrato(), faturamento.getNuFatura(), faturamento.getDeFatura()));

			row = addNewRow(sheet);

			addMergedRegion(
					sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, String.format("%s - %s - %s", faturamento.getNuFatura(),
							faturamento.getDeFatura(), faturamento.getDtFinalFatura()),
					CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

			List<DetalheFaturamentoDTO> detalhes = detalheService.listar(coEquipe, faturamento.getNuFatura());

			row = addNewRow(sheet);

			int pos = 0;

			addCell(row, pos++, "Dt. Início", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Dt. Final", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Preço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Qt. Membros", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Dias Trab.", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Dias Fat.", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Qt. Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Valor Previsto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

			int posInicioVgnciaMembros = sheet.getLastRowNum() + 2;
			int posFinalVgnciaMembros = sheet.getLastRowNum() + 1;

			for (DetalheFaturamentoDTO d : detalhes) {

				row = addNewRow(sheet);

				pos = 0;

				addCell(row, pos++, d.getDtInicioVigenciaMembros());
				addCell(row, pos++, d.getDtFinalVigenciaMembros());

				BigDecimal vrPrecoGrupo = precoEquipeService.getVrPrecoGrupoVigente(equipe.getNuEquipe(),
						faturamento.getNuFatura());
				addCell(row, pos++, vrPrecoGrupo);

				addCell(row, pos++, d.getQtMembros());
				addCell(row, pos++, d.getQtDiasTrab());
				addCell(row, pos++, d.getQtDiasFat());

				// =(E4/F4)*D4
				setCellFormula(sheet, row, "(E$1/F$1)*D$1", pos++, CellStylesEnum.DECIMAL5);

				// =G4*C4
				setCellFormula(sheet, row, "G$1*C$1", pos++);

				posFinalVgnciaMembros++;
			}

			row = addNewRow(sheet);
			pos = 5;

			// Qt. Efetiva
			addCell(row, pos++, "Qt. Efetiva", CellStylesEnum.NORMAL_ALIGN_RIGHT_BOLD);
			setCellFormula(sheet, row, "SUM(G$1:G$2)", pos++, posInicioVgnciaMembros, posFinalVgnciaMembros,
					CellStylesEnum.DECIMAL5);

			// Sub-Total
			setCellFormula(sheet, row, "SUM(H$1:H$2)", pos++, posInicioVgnciaMembros, posFinalVgnciaMembros);

			// extras
			AusenciaExtraFaturamentoDTO ausenciaExtraDTO = ausenciaExtraService.findBy(coEquipe,
					faturamento.getNuFatura());

			row = addNewRow(sheet);
			pos = 5;
			addCell(row, pos++, "APEC", CellStylesEnum.NORMAL_ALIGN_RIGHT_BOLD);
			if (ausenciaExtraDTO != null) {
				addCell(row, pos++, ausenciaExtraDTO.getQtDiaServicoExtra());
			} else {
				addCell(row, pos++, BigDecimal.ZERO);
			}
			addFormulaExtra(sheet, row, pos);

			// ausencias
			row = addNewRow(sheet);
			pos = 5;
			addCell(row, pos++, "DPEC", CellStylesEnum.NORMAL_ALIGN_RIGHT_BOLD);
			if (ausenciaExtraDTO != null) {
				addCell(row, pos++, ausenciaExtraDTO.getQtDiaAusencia());
			} else {
				addCell(row, pos++, BigDecimal.ZERO);
			}
			addFormulaAusencia(sheet, row, pos);

			//
			row = addNewRow(sheet);
			pos = 6;
			addCell(row, pos++, "Sub-Total", CellStylesEnum.NORMAL_ALIGN_RIGHT_BOLD);
			setCellFormula(sheet, row, //
					String.format("SUM(H%s+H%s-H%s)", sheet.getLastRowNum() - 2, sheet.getLastRowNum() - 1,
							sheet.getLastRowNum()),
					pos++);

			//
			// FNS
			//
			row = addNewRow(sheet);
			pos = 5;
			addCell(row, pos++, "Valor com FNS", CellStylesEnum.ORANGE_ALIGN_RIGHT_BOLD);
			IndicadorDTO indicador = indicadorService.findByFatura(equipe.getNuEquipe(), faturamento.getNuFatura(),
					"S");
			if (indicador != null) {
				addCell(row, pos++, indicador.getVrFaixaFns());
			} else {
				addCell(row, pos++, BigDecimal.ONE);
				setCellComment(row.getCell(pos - 1),
						"Indicador não encontrado. Pode não ter sido calculado ou ocorreu um erro.");
			}
			setCellFormula(sheet, row, "G$1*H$2", pos++, sheet.getLastRowNum() + 1, sheet.getLastRowNum());

			// blank line
			addNewRow(sheet);

		}

	}

	private void addFormulaAusencia(XSSFSheet sheet, XSSFRow row, int pos) {

		String m1 = String.valueOf(row.getRowNum() + 1);
		String m2 = String.valueOf((row.getRowNum() - 2));

		String formula = "G$1*(C$2/F$2)".replaceAll("\\$1", m1);
		formula = formula.replaceAll("\\$2", m2);

		evaluateFormula(sheet, row, pos, formula);
	}

	private void addFormulaExtra(XSSFSheet sheet, XSSFRow row, int pos) {

		String m1 = String.valueOf(row.getRowNum() + 1);
		String m2 = String.valueOf((row.getRowNum() - 1));

		String formula = "G$1*(C$2/F$2)".replaceAll("\\$1", m1);
		formula = formula.replaceAll("\\$2", m2);

		evaluateFormula(sheet, row, pos, formula);
	}

}
