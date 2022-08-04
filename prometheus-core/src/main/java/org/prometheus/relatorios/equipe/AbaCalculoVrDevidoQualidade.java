package org.prometheus.relatorios.equipe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioEquipe;
import org.prometheus.utils.XSSFUtils;
import org.springframework.stereotype.Component;

@Component
public class AbaCalculoVrDevidoQualidade extends Relatorio {

	private static final int LAYOUT_COLUMNS_COUNT = 14;

	public XSSFCell gerar(XSSFWorkbook workBook, String coEquipe, List<FaturamentoDTO> faturamentos,
			Map<Long, InsumosFaturamentoDTO> insumos) throws Exception {

		carregarFontes(workBook);

		carregarEstilos(workBook);

		XSSFSheet sheet = workBook.createSheet(RelatorioEquipe.ABA_CALCULO_VALOR_DEVIDO);

		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);

		XSSFRow row = addNewRow(sheet);

		int pos = 0;

		addCell(row, pos++, "Faturamento", ALIGN_CENTER_GREY_25_PERCENT_BOLD, 50);
		addCell(row, pos++, "Qtd. Dias Fat.", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Qtd. de Membros", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Valor Equipe", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Extras", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Ausências", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "FNS", ALIGN_CENTER_GREY_25_PERCENT_BOLD, 30);
		addCell(row, pos++, "Valor Previsto", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

		List<XSSFCell> listaValoresPrevistos = new ArrayList<XSSFCell>();

		XSSFCell cellVrPrevisto = null;
		XSSFCell cellVrEquipe = null;
		XSSFCell cellVrAusencias = null;
		XSSFCell cellVrExtras = null;
		XSSFCell cellVrFns = null;

		for (FaturamentoDTO faturamento : faturamentos) {

			InsumosFaturamentoDTO insumo = insumos.get(faturamento.getNuFatura());

			if (insumo != null) {

				row = addNewRow(sheet);

				pos = 0;

				addCell(row, pos++, faturamento.getNuFatura() + " - " + faturamento.getDeFatura());
				addCell(row, pos++, faturamento.getQtDiasFaturamento());
				setCellFormula(sheet, row, XSSFUtils.getFullReference(insumo.getQtEfetivaTotal()), pos++);

				cellVrEquipe = setCellFormula(sheet, row, //
						XSSFUtils.getFullReference(insumo.getVrEquipe()), pos++);

				cellVrExtras = setCellFormula(sheet, row, //
						String.format("'%s'!%s", //
								insumo.getVrExtras().getSheet().getSheetName(), //
								insumo.getVrExtras().getReference()), //
						pos++);

				cellVrAusencias = setCellFormula(sheet, row, //
						String.format("'%s'!%s", //
								insumo.getVrAusencias().getSheet().getSheetName(), //
								insumo.getVrAusencias().getReference()), //
						pos++);

				if (insumo.getVrFns() != null && !"STRING".equalsIgnoreCase(insumo.getVrFns().getCellType().name())) {
					cellVrFns = setCellFormula(sheet, row, //
							String.format("'%s'!%s", //
									insumo.getVrFns().getSheet().getSheetName(), //
									insumo.getVrFns().getReference()), //
							pos++);
				} else {
					cellVrFns = addCell(row, pos++, BigDecimal.ONE);
				}

				cellVrPrevisto = setCellFormula(sheet, row, //
						String.format("(%s+%s-%s)*%s", //
								cellVrEquipe.getReference(), //
								cellVrExtras.getReference(), //
								cellVrAusencias.getReference(), //
								cellVrFns.getReference()), //
						pos++);

				listaValoresPrevistos.add(cellVrPrevisto);

			}
		}

		row = addNewRow(sheet);
		pos = 6;
		addCell(row, pos++, "TOTAL", ALIGN_CENTER_GREY_40_PERCENT_BOLD);
		XSSFCell result = setCellFormula(sheet, row, //
				String.format("sum(%s:%s)", //
						listaValoresPrevistos.get(0).getReference(), //
						listaValoresPrevistos.get(listaValoresPrevistos.size() - 1).getReference()), //
				pos++);

		return result;
	}

}
