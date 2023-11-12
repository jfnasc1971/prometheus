package org.prometheus.relatorios.equipe;

import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.TermoDTO;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioEquipe;
import org.prometheus.service.EquipeService;
import org.prometheus.service.TermoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaPagamentos extends Relatorio {

	private static final int LAYOUT_COLUMNS_COUNT = 14;

	@Autowired
	private TermoService termoService;

	@Autowired
	private EquipeService equipeService;

	public XSSFCell write(XSSFWorkbook workBook, String coEquipe) throws InvalidFormatException, IOException {

		EquipeDTO equipe = equipeService.findBy(coEquipe);

		carregarFontes(workBook);

		carregarEstilos(workBook);

		XSSFSheet sheet = workBook.createSheet(RelatorioEquipe.ABA_PAGAMENTOS);

		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);

		XSSFRow row = addNewRow(sheet);

		int pos = 0;

		addCell(row, pos++, "Faturamento", ALIGN_CENTER_GREY_25_PERCENT_BOLD, 50);
		addCell(row, pos++, "Termo", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Data Emissão", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Preço", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Qtd. Efetiva", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Vr. Previsto", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Qtd. Dias Extras", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "APEC", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Qtd. Dias Ausência", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "DPEC", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Vr. Previsto Termo", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "FNS", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Fatura Paga", ALIGN_CENTER_GREY_25_PERCENT_BOLD, 50);
		addCell(row, pos++, "Vr. Termo", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

		List<TermoDTO> termos = termoService.listar(equipe.getNuEquipe());

		XSSFCell primeiraFatura = null;
		XSSFCell ultimaFatura = null;

		for (TermoDTO termo : termos) {

			row = addNewRow(sheet);

			pos = 0;

			addCell(row, pos++, termo.getNuFaturaGerada() + " - " + termo.getDeFatura());
			addCell(row, pos++, termo.getCoTermo());
			addCell(row, pos++, termo.getDhEmissaoTermo());
			addCell(row, pos++, termo.getNuTipoTermo() == 11 ? termo.getVrPrecoGrupo() : null, STYLE_DECIMAL);
			addCell(row, pos++, termo.getNuTipoTermo() == 11 ? termo.getQtMembroEquipeVigente() : null, STYLE_DECIMAL);
			addCell(row, pos++, termo.getNuTipoTermo() == 11 ? termo.getVrPrevistoEquipe() : null, STYLE_DECIMAL);
			addCell(row, pos++, termo.getNuTipoTermo() == 11 ? termo.getDiaServicoExtra() : null, STYLE_DECIMAL);
			addCell(row, pos++, termo.getNuTipoTermo() == 11 ? termo.getVrAcrescimo() : null, STYLE_DECIMAL);
			addCell(row, pos++, termo.getNuTipoTermo() == 11 ? termo.getQtDiaAusencia() : null, STYLE_DECIMAL);
			addCell(row, pos++, termo.getNuTipoTermo() == 11 ? termo.getVrDecrescimo() : null, STYLE_DECIMAL);
			addCell(row, pos++, termo.getNuTipoTermo() == 11 ? termo.getVrFinanceiroTermoOriginal() : null, STYLE_DECIMAL);

			if (termo.getVrFns() == null) {
				addCell(row, pos++);
			} else {
				addCell(row, pos++, termo.getNuTipoTermo() == 11 ? termo.getVrFns() : null, STYLE_DECIMAL);
			}

			addCell(row, pos++, termo.getNuFaturaPaga() + " - " + termo.getDeFaturaPaga());

			if (primeiraFatura == null) {
				primeiraFatura = addCell(row, pos++, termo.getVrFinanceiroTermo(), STYLE_DECIMAL);
				ultimaFatura = primeiraFatura; 
			} else {
				ultimaFatura = addCell(row, pos++, termo.getVrFinanceiroTermo(), STYLE_DECIMAL);
			}

		}

		row = addNewRow(sheet);
		addCell(row, pos - 2, "TOTAL", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		XSSFCell result = addCell(row, pos - 1, "N/A");

		if (primeiraFatura != null) {
			setCellFormula(result,
					String.format("sum(%s:%s)", primeiraFatura.getReference(), ultimaFatura.getReference()), //
					STYLE_DECIMAL);
		}

		return result;

	}

}
