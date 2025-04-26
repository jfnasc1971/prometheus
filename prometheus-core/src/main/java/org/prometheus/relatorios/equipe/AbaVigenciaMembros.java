package org.prometheus.relatorios.equipe;

import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.QntdeMembrosEquipeDTO;
import org.prometheus.dto.VigenciaMembrosEquipeDTO;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.service.EquipeService;
import org.prometheus.service.QntdeMembrosEquipeService;
import org.prometheus.service.VigenciaMembrosEquipeService;
import org.prometheus.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaVigenciaMembros extends Relatorio {

	private static final int LAYOUT_COLUMNS_COUNT = 20;

	@Autowired
	private EquipeService equipeService;

	@Autowired
	private QntdeMembrosEquipeService qntdeMembrosEquipeService;

	@Autowired
	private VigenciaMembrosEquipeService vigenciaMembrosEquipeService;

	public void write(XSSFWorkbook workBook, String coEquipe, List<FaturamentoDTO> faturamentos)
			throws InvalidFormatException, IOException {

		EquipeDTO equipe = equipeService.findBy(coEquipe);

		carregarFontes(workBook);

		carregarEstilos(workBook);

		XSSFSheet sheet = workBook.createSheet("Vigências dos Membros");

		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 60);
		setColumnWidth(sheet, 0, 90);
		setColumnWidth(sheet, 1, 90);
		setColumnWidth(sheet, 5, 10);
		setColumnWidth(sheet, 7, 180);

		int pos = 0;
		int rownum = 0;

		XSSFRow row = addNewRow(sheet, rownum++);

		/**
		 * Vigências de Membros da Equipe
		 */
		List<QntdeMembrosEquipeDTO> membros = qntdeMembrosEquipeService.listar(coEquipe);

		pos = 0;

		addMergedRegion(sheet, row, 0, 4, "Total Membros por Vigência", ALIGN_CENTER_GREY_40_PERCENT_BOLD);

		row = addNewRow(sheet, rownum++);

		addMergedRegion(sheet, row, pos++, pos++, "Perfil do Profisional", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Dt. Início", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Dt. Final", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Qt. Membros", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

		for (QntdeMembrosEquipeDTO membro : membros) {
			pos = 0;
			row = addNewRow(sheet, rownum++);

			addMergedRegion(sheet, row, pos++, pos++, Utils.isEmpty(membro.getDePerfil()) ? "-":membro.getDePerfil());
			addCell(row, pos++, membro.getDtInicioVigenciaMembros());
			addCell(row, pos++, membro.getDtFinalVigenciaMembros());
			addCell(row, pos++, membro.getQtMembrosEquipe());

//			if (membro.getQtIntervalos() > 1) {
//				setCellComment(row.getCell(row.getLastCellNum() - 1),
//						"Essa vigência está incorreta. Verifique se há sobreposição de datas.");
//			}

		}
		
		// MEMBROS DA EQUIPE
		rownum = 0;

		row = addNewRow(sheet, rownum++);
		
		addMergedRegion(sheet, row, 6, 10, "Vigências de Membros da Equipe", ALIGN_CENTER_GREY_40_PERCENT_BOLD);
		
		List<VigenciaMembrosEquipeDTO> vigencias = vigenciaMembrosEquipeService.listar(equipe.getNuEquipe());

		row = addNewRow(sheet, rownum++);
		pos = 6;
		
		addCell(row, pos++, "Matrícula", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Nome", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Posição", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Dt. Início", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, pos++, "Dt. Fim", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		
		for (VigenciaMembrosEquipeDTO vigencia : vigencias) {
			
			row = addNewRow(sheet, rownum++);
			pos = 6;

			addCell(row, pos++, vigencia.getCoMatricula());
			addCell(row, pos++, Utils.isEmpty(vigencia.getNoMembro()) ? "-":vigencia.getNoMembro());
			addCell(row, pos++, vigencia.getNuPosicaoMembro());
			addCell(row, pos++, vigencia.getDtInicioMembro());
			addCell(row, pos++, vigencia.getDtFimMembro());

		}		
		
	}
}
