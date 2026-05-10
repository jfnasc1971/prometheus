package org.prometheus.relatorios.demanda;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioDemanda;
import org.prometheus.utils.XSSFSheetUtils;
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
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, coDemanda, CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet);
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, 1, "Vr. Devido Faturamento", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
		setCellFormula(sheet, row, getCellReference(RelatorioDemanda.ABA_DEMANDA, //
				cellValorDevido.getReference()), 2, CellStylesEnum.DECIMAL);

		row = addNewRow(sheet);
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, 1, "Vr. Pago Empenho", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
		setCellFormula(sheet, row, getCellReference(RelatorioDemanda.ABA_TERMOS, //
				cellValorPagamentos.getReference()), 2, CellStylesEnum.DECIMAL);
		
		row = addNewRow(sheet);
		XSSFCell cell = XSSFSheetUtils.addMergedRegion(sheet, row, 0, 1, "Valor Ajuste", CellStylesEnum.GREEN_ALIGN_RIGHT_BOLD);
		XSSFCell result = setCellFormula(sheet, row, "if(C2<>\"N/A\",C2-C3,\"0\")", 2, CellStylesEnum.DECIMAL);
		setCellComment(cell, "Corresponde à diferença entre o valor devido calculado e que foi pago.\n" //
				+ "Esse valor tende a 0(zero).");

		LOG.debug(coDemanda + ";" + result.getRawValue());
		
		return result;

	}

}
