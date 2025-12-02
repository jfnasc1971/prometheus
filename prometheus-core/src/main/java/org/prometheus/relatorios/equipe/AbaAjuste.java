package org.prometheus.relatorios.equipe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioEquipe;
import org.prometheus.utils.DateUtil;
import org.prometheus.utils.NumberUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaAjuste extends Relatorio {

	private static final Logger LOG = LogManager.getLogger(AbaAjuste.class.getSimpleName());

	private static final int LAYOUT_COLUMNS_COUNT = 14;

	public XSSFCell write(XSSFWorkbook workBook, EquipeDTO equipe, XSSFCell cellValorDevido,
			XSSFCell cellValorPagamentos) throws InvalidFormatException, IOException {

		XSSFSheet sheet = workBook.createSheet("Ajuste");

		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);

		XSSFRow row = null;

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 2, equipe.getCoEquipe(), CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 1, "Vr. Devido Faturamento", CellStylesEnum.LIGHT_GREY_ALIGN_LEFT);
		setCellFormula(sheet, row, getCellReference(RelatorioEquipe.ABA_CALCULO_VALOR_DEVIDO, //
				cellValorDevido.getReference()), 2, CellStylesEnum.DECIMAL);

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 1, "Vr. Pago Empenho", CellStylesEnum.LIGHT_GREY_ALIGN_LEFT);
		setCellFormula(sheet, row, getCellReference(RelatorioEquipe.ABA_PAGAMENTOS, //
				cellValorPagamentos.getReference()), 2, CellStylesEnum.DECIMAL);

		row = addNewRow(sheet);
		XSSFCell cell = addMergedRegion(sheet, row, 0, 1, "Valor Ajuste", CellStylesEnum.LIGHT_GREY_ALIGN_LEFT_BOLD);
		XSSFCell result = setCellFormula(sheet, row, "if(C2<>\"N/A\",C2-C3,\"0\")", 2, CellStylesEnum.DECIMAL);
		setCellComment(cell, "Corresponde à diferença entre o valor devido calculado e que foi pago.\n" //
				+ "Esse valor tende a 0(zero).");

		File file = (new File(PATH + File.separator + "consolidado.csv"));
		FileWriter fw = new FileWriter(file, true);
		LOG.info(file.getAbsolutePath());

		String vrAjuste = "N/A";
		try {
			vrAjuste = NumberUtils.format(Double.valueOf(result.getRawValue().toString()));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOG.info(equipe.getCoContrato() + ";" + equipe.getNoTipoGrupo() + ";" + //
				equipe.getNuEquipe() + ";" + equipe.getCoEquipe() + ";" + vrAjuste);

		fw.write(equipe.getCoContrato() + ";" + equipe.getNoTipoGrupo() + ";" + //
				equipe.getNuEquipe() + ";" + equipe.getCoEquipe() + ";" + vrAjuste + "\n");
		
		fw.flush();
		fw.close();

		return result;

	}

}
