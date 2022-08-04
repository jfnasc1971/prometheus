package org.prometheus.relatorios.equipe;

import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.CicloTrabalhoDTO;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.service.CicloTrabalhoService;
import org.prometheus.service.EquipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaSprints extends Relatorio {

	private static final int LAYOUT_COLUMNS_COUNT = 8;

	@Autowired
	private CicloTrabalhoService cicloTrabalhoService;

	@Autowired
	private EquipeService equipeService;

	public void write(XSSFWorkbook wb, String coEquipe, List<FaturamentoDTO> faturamentos)
			throws InvalidFormatException, IOException {

		EquipeDTO equipe = equipeService.findBy(coEquipe);

		XSSFSheet sheet = wb.createSheet("Sprints");
		
		carregarFontes(wb);
		carregarEstilos(wb);

		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);

		XSSFRow row = addNewRow(sheet);

		for (FaturamentoDTO faturamento : faturamentos) {

			row = addNewRow(sheet);
			addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1,
					faturamento.getNuFatura() + " - " + faturamento.getDeFatura(), //
					ALIGN_CENTER_GREY_25_PERCENT_BOLD);

			List<CicloTrabalhoDTO> ciclos = cicloTrabalhoService.listar(faturamento.getNuFatura(),
					equipe.getNuEquipe());

			if (ciclos == null || ciclos.isEmpty()) {
				row = addNewRow(sheet);
				addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, "Nenhum ciclo encontrado.", //
						ALIGN_LEFT);
			}

			addNewRow(sheet);

			if (ciclos != null && !ciclos.isEmpty()) {
				row = addNewRow(sheet);
				addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, "Ciclos", //
						ALIGN_CENTER_WHITE_BOLD);

				int pos = 0;

				row = addNewRow(sheet);

				addCell(row, pos++, "Ciclo", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
				addCell(row, pos++, "Situação", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
				addCell(row, pos++, "Data Início", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
				addCell(row, pos++, "Data Alvo", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
				addCell(row, pos++, "Falha", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
				addCell(row, pos++, "Vr. Quest.", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
				addCell(row, pos++, "Nota Produto", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
				addCell(row, pos++, "Total Avaliação", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

				for (CicloTrabalhoDTO ciclo : ciclos) {
					pos = 0;
					row = addNewRow(sheet);
					addCell(row, pos++, ciclo.getNuCicloTrabalho());
					addCell(row, pos++, ciclo.getNuSituacaoCicloTrabalho());
					addCell(row, pos++, ciclo.getDtInicioCicloTrabalho());
					addCell(row, pos++, ciclo.getDtAlvoCicloTrabalho());
					addCell(row, pos++, ciclo.getCoFalhaCicloTrabalho());
					addCell(row, pos++, ciclo.getVrQuestionarioAvaliacao());
					addCell(row, pos++, ciclo.getVrNotaQualidadeProduto());
					addCell(row, pos++, ciclo.getVrTotalApuradoAvaliacao());

				}

				addNewRow(sheet);
				
				row = addNewRow(sheet);
				addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, "Solicitações", ALIGN_CENTER_WHITE_BOLD);

				addNewRow(sheet);

			}


		}
	}

}
