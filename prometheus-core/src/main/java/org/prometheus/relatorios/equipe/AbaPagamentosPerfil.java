package org.prometheus.relatorios.equipe;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.dto.TermoDTO;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioEquipe;
import org.prometheus.service.EquipeService;
import org.prometheus.service.TermoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AbaPagamentosPerfil extends Relatorio {

	private static final int LAYOUT_COLUMNS_COUNT = 15;

	@Autowired
	private TermoService termoService;

	@Autowired
	private EquipeService equipeService;

	public XSSFCell gerar(XSSFWorkbook workBook, String coEquipe, Map<Long, InsumosFaturamentoDTO> insumos) throws Exception {

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
		addCell(row, pos++, "Valor Equipe", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Qtd. de Membros", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Extras", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Ausências", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "FNS", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Valor Previsto", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Fatura Paga", ALIGN_CENTER_GREY_25_PERCENT_BOLD, 50);
		addCell(row, pos++, "Valor Termo", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

		List<TermoDTO> termos = termoService.listar(equipe.getNuEquipe());

		XSSFCell primeiraFatura = null;
		XSSFCell ultimaFatura = null;

		for (TermoDTO termo : termos) {

			row = addNewRow(sheet);

			pos = 0;

			addCell(row, pos++, termo.getNuFaturaGerada() + " - " + termo.getDeFatura());
			addCell(row, pos++, termo.getCoTermo());
			addCell(row, pos++, termo.getDhEmissaoTermo());

			XSSFCell cellVrPrevisto = addCell(row, pos++,
					termo.getNuTipoTermo() == 11 ? termo.getVrPrevistoEquipe() : null, STYLE_DECIMAL);
			
			addCell(row, pos++, termo.getNuTipoTermo() == 11 ? termo.getQtMembroEquipeVigente() : null, STYLE_DECIMAL);
			
			XSSFCell cellVrAcrescimo = addCell(row, pos++,
					termo.getNuTipoTermo() == 11 ? termo.getVrAcrescimo() : null, STYLE_DECIMAL);
			
			XSSFCell cellVrDecrescimo = addCell(row, pos++,
					termo.getNuTipoTermo() == 11 ? termo.getVrDecrescimo() : null, STYLE_DECIMAL);

			// FNS
			XSSFCell cellVrFNS = null;
			
			if (termo.getVrFns() == null) {
				cellVrFNS = addCell(row, pos++, termo.getNuTipoTermo() == 11 ? BigDecimal.ONE : null, STYLE_DECIMAL);
			} else {
				cellVrFNS = addCell(row, pos++, termo.getNuTipoTermo() == 11 ? termo.getVrFns() : null, STYLE_DECIMAL);
			}
			
			// Valor Previsto
			if (termo.getNuTipoTermo() == 11) {
				setCellFormula(sheet, row, //
						String.format("(%s+%s-%s)*%s", //
								cellVrPrevisto.getReference(), //
								cellVrAcrescimo.getReference(),
								cellVrDecrescimo.getReference(), //
								cellVrFNS.getReference()), //
						pos++,STYLE_DECIMAL);
			}

			if (termo.getNuTipoTermo() == 12) {
				addCell(row, pos++);
			}
			
			addCell(row, pos++, termo.getNuFaturaPaga() + " - " + termo.getDeFaturaPaga());

			if (primeiraFatura == null) {
				primeiraFatura = addCell(row, pos++, termo.getVrFinanceiroTermo(),STYLE_DECIMAL);
				ultimaFatura = primeiraFatura;
			} else {
				ultimaFatura = addCell(row, pos++, termo.getVrFinanceiroTermo(),STYLE_DECIMAL);
			}

			InsumosFaturamentoDTO insumo = new InsumosFaturamentoDTO();
			insumo.setVrPago(ultimaFatura);
			insumo.setNuFatura(termo.getNuFaturaGerada());
			insumo.setNuEquipe(equipe.getNuEquipe());
			
			insumos.put(termo.getNuFaturaGerada(), insumo);
			
		}

		row = addNewRow(sheet);
		addCell(row, pos - 2, "TOTAL", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		XSSFCell result = addCell(row, pos - 1, "N/A");

		if (primeiraFatura != null) {
			setCellFormula(result,
					String.format("sum(%s:%s)", //
							primeiraFatura.getReference(), ultimaFatura.getReference()), //
					STYLE_DECIMAL);
		}

		return result;

	}

}
