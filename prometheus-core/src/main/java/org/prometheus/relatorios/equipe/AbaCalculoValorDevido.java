package org.prometheus.relatorios.equipe;

import java.io.IOException;
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
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaCalculoValorDevido extends Relatorio {

	private static final int LAYOUT_COLUMNS_COUNT = 14;

	public XSSFCell write(XSSFWorkbook workBook, String coEquipe, List<FaturamentoDTO> faturamentos,
			Map<Long, InsumosFaturamentoDTO> insumos) throws InvalidFormatException, IOException {

		carregarFontes(workBook);

		carregarEstilos(workBook);

		XSSFSheet sheet = workBook.createSheet(RelatorioEquipe.ABA_CALCULO_VALOR_DEVIDO);

		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);

		XSSFRow row = addNewRow(sheet);

		int pos = 0;

		addCell(row, pos++, "Faturamento", ALIGN_CENTER_GREY_25_PERCENT_BOLD, 50);
		addCell(row, pos++, "Qtd. Dias Fat.", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Preço", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Qtd. Efetiva", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Valor Equipe", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Qtd. Dias Extras", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "APEC", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Qtd. Dias Ausências", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "DPEC", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Vr. Previsto Termo", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "FNS", ALIGN_CENTER_GREY_25_PERCENT_BOLD, 30);
		addCell(row, pos++, "Vr. Financeiro", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

		XSSFCell primeiraFatura = null;
		XSSFCell ultimaFatura = null;

		for (FaturamentoDTO faturamento : faturamentos) {

			InsumosFaturamentoDTO insumo = insumos.get(faturamento.getNuFatura());

			if (insumo != null) {

				row = addNewRow(sheet);

				pos = 0;

				addCell(row, pos++, faturamento.getNuFatura() + " - " + faturamento.getDeFatura());
				addCell(row, pos++, faturamento.getQtDiasFaturamento());
				addCell(row, pos++, insumo.getVrPreco());

				if (insumo.getQtEfetiva() != null) {
					setCellFormula(sheet, row, getCellReference(RelatorioEquipe.ABA_CALCULOS, //
							insumo.getQtEfetiva().getReference()), pos++, STYLE_DECIMAL);
				}

				setCellFormula(sheet, row, String.format("C%s*D%s", row.getRowNum() + 1, row.getRowNum() + 1), //
						pos++, STYLE_DECIMAL);

				setCellFormula(sheet, row, getCellReference(RelatorioEquipe.ABA_CALCULOS, //
						insumo.getQtDiasExtra().getReference()), pos++, STYLE_DECIMAL);

				// =(C20/B20)*F20
				setCellFormula(sheet, row,
						String.format("(C%s/B%s)*F%s", row.getRowNum() + 1, row.getRowNum() + 1, row.getRowNum() + 1), //
						pos++, STYLE_DECIMAL);

				setCellFormula(sheet, row, getCellReference(RelatorioEquipe.ABA_CALCULOS, //
						insumo.getQtDiasAusencia().getReference()), pos++, STYLE_DECIMAL);

				setCellFormula(sheet, row,
						String.format("(C%s/B%s)*H%s", row.getRowNum() + 1, row.getRowNum() + 1, row.getRowNum() + 1), //
						pos++, STYLE_DECIMAL);

				// E20+(G20-I20)
				setCellFormula(sheet, row,
						String.format("E%s+(G%s-I%s)", row.getRowNum() + 1, row.getRowNum() + 1, row.getRowNum() + 1), //
						pos++, STYLE_DECIMAL);

				if (insumo.getVrFns() != null && !"N/A".equalsIgnoreCase(insumo.getVrFns().toString())) {
					setCellFormula(sheet, row,
							getCellReference(RelatorioEquipe.ABA_CALCULOS, insumo.getVrFns().getReference()), pos++,
							STYLE_DECIMAL);
				} else {
					addCell(row, pos++, "N/A", ALIGN_CENTER);
				}

				// J20*K20
				if (primeiraFatura == null) {
					primeiraFatura = setCellFormula(sheet, row,
							"IF(K%s=\"N/A\",J%s,J%s*K%s)".replaceAll("%s", String.valueOf(row.getRowNum() + 1)), //
							pos++, STYLE_DECIMAL);

				} else {
					ultimaFatura = setCellFormula(sheet, row,
							"IF(K%s=\"N/A\",J%s,J%s*K%s)".replaceAll("%s", String.valueOf(row.getRowNum() + 1)), //
							pos++, STYLE_DECIMAL);

				}
			}
		}

		if (ultimaFatura == null) {
			ultimaFatura = primeiraFatura;
		}

		row = addNewRow(sheet);
		addCell(row, pos - 2, "TOTAL", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		XSSFCell result = setCellFormula(sheet, row,
				String.format("sum(%s:%s)", primeiraFatura.getReference(), ultimaFatura.getReference()), //
				pos - 1, STYLE_DECIMAL);

		return result;

	}

}
