package org.prometheus.relatorios;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.dto.PerfilDTO;
import org.prometheus.service.EquipeService;
import org.prometheus.service.PerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaCalculoVrDevidoPerfil extends Relatorio {

	private static final int LAYOUT_COLUMNS_COUNT = 14;

	@Autowired
	private PerfilService perfilService;

	@Autowired
	private EquipeService equipeService;

	public XSSFCell write(XSSFWorkbook workBook, String coEquipe, List<FaturamentoDTO> faturamentos,
			Map<Long, InsumosFaturamentoDTO> insumos) throws InvalidFormatException, IOException {

		EquipeDTO equipe = equipeService.findBy(coEquipe);

		carregarFontes(workBook);

		carregarEstilos(workBook);

		XSSFSheet sheet = workBook.createSheet(Relatorio.ABA_CALCULO_VALOR_DEVIDO);

		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);

		XSSFRow row = addNewRow(sheet);

		int pos = 0;

		addCell(row, pos++, "Faturamento", ALIGN_CENTER_GREY_25_PERCENT_BOLD, 50);
		addCell(row, pos++, "Qtd. Dias Fat.", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Valor Equipe", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "APEC", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "DPEC", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Valor Previsto", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "FNS", ALIGN_CENTER_GREY_25_PERCENT_BOLD, 30);
		addCell(row, pos++, "Valor Financeiro", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

		List<XSSFCell> listaValoresFinanceiros = new ArrayList<XSSFCell>();
		XSSFCell cellVrPrevisto = null;
		XSSFCell cellVrFns = null;

		for (FaturamentoDTO faturamento : faturamentos) {

			InsumosFaturamentoDTO insumo = insumos.get(faturamento.getNuFatura());

			if (insumo != null) {

				row = addNewRow(sheet);

				pos = 0;

				List<PerfilDTO> perfis = perfilService.listar(equipe.getNuContrato());

				addCell(row, pos++, faturamento.getNuFatura() + " - " + faturamento.getDeFatura());
				addCell(row, pos++, faturamento.getQtDiasFaturamento());

				BigDecimal valorEquipe = BigDecimal.ZERO;

				for (PerfilDTO perfil : perfis) {

					System.out.println(faturamento.getNuFatura() + " " + perfil.getNuPerfil() + " "
							+ insumo.getVrPrecoPerfil(perfil.getNuPerfil()) + " "
							+ insumo.getQtEfetivaPerfil(perfil.getNuPerfil()));

					valorEquipe = valorEquipe.add(insumo.getVrPrecoPerfil(perfil.getNuPerfil())
							.multiply(insumo.getQtEfetivaPerfil(perfil.getNuPerfil())));
				}

				addCell(row, pos++, valorEquipe);

				setCellFormula(sheet, row, //
						String.format("'%s'!%s", //
								insumo.getVrAusencias().getSheet().getSheetName(), //
								insumo.getVrAusencias().getReference()), //
						pos++);

				setCellFormula(sheet, row, //
						String.format("'%s'!%s", //
								insumo.getVrExtras().getSheet().getSheetName(), //
								insumo.getVrExtras().getReference()), //
						pos++);

				cellVrPrevisto = setCellFormula(sheet, row, //
						String.format("'%s'!%s", //
								insumo.getVrPrevistoTermo().getSheet().getSheetName(), //
								insumo.getVrPrevistoTermo().getReference()), //
						pos++);

				if (insumo.getVrFns() != null) {
					cellVrFns = setCellFormula(sheet, row, //
							String.format("'%s'!%s", //
									insumo.getVrFns().getSheet().getSheetName(), //
									insumo.getVrFns().getReference()), //
							pos++);
				} else {
					cellVrFns = addCell(row, pos++, BigDecimal.ONE);
				}
				
				listaValoresFinanceiros.add(setCellFormula(sheet, row, //
						String.format("%s*%s", //
								cellVrPrevisto.getReference(), //
								cellVrFns.getReference()), //
						pos++));

			}
		}

		row = addNewRow(sheet);
		pos = 6;
		addCell(row, pos++, "TOTAL", ALIGN_CENTER_GREY_40_PERCENT_BOLD);
		XSSFCell result = setCellFormula(sheet, row, //
				String.format("sum(%s:%s)", //
						listaValoresFinanceiros.get(0).getReference(), //
						listaValoresFinanceiros.get(listaValoresFinanceiros.size() - 1).getReference()), //
				pos++);
		
		return result;

	}

}
