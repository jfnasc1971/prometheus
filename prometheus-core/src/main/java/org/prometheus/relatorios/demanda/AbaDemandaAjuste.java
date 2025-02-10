package org.prometheus.relatorios.demanda;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioDemanda;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaDemandaAjuste extends Relatorio {

    private static final Logger LOG = LogManager.getLogger(AbaDemandaAjuste.class.getSimpleName());
    
	private static final int LAYOUT_COLUMNS_COUNT = 14;

	public XSSFCell write(XSSFWorkbook workBook, String coDemanda, XSSFCell cellValorDevido,
			XSSFCell cellValorPagamentos) throws InvalidFormatException, IOException {

		carregarFontes(workBook);

		carregarEstilos(workBook);

		XSSFSheet sheet = workBook.createSheet("Ajuste");

		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);

		XSSFRow row = null;

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 2, coDemanda, ALIGN_CENTER_GREY_40_PERCENT_BOLD);

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 1, "Vr. Devido Faturamento", ALIGN_LEFT_GREY_25_PERCENT_BOLD);
		setCellFormula(sheet, row, getCellReference(RelatorioDemanda.ABA_DEMANDA, //
				cellValorDevido.getReference()), 2, STYLE_DECIMAL);

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 1, "Vr. Pago Empenho", ALIGN_LEFT_GREY_25_PERCENT_BOLD);
		setCellFormula(sheet, row, getCellReference(RelatorioDemanda.ABA_TERMOS, //
				cellValorPagamentos.getReference()), 2, STYLE_DECIMAL);
		
		row = addNewRow(sheet);
		XSSFCell cell = addMergedRegion(sheet, row, 0, 1, "Valor Ajuste", ALIGN_RIGHT_GREEN_BOLD);
		XSSFCell result = setCellFormula(sheet, row, "if(C2<>\"N/A\",C2-C3,\"0\")", 2, STYLE_DECIMAL);
		setCellComment(cell, "Corresponde à diferença entre o valor devido calculado e que foi pago.\n" //
				+ "Esse valor tende a 0(zero).");

		LOG.debug(coDemanda + ";" + result.getRawValue());
		
		return result;

	}

}
