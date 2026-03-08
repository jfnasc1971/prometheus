package org.prometheus.utils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.prometheus.enums.CellStylesEnum;

public class SheetUtils {

	public static final XSSFRow getRow(XSSFSheet sheet, int rownum) {
		XSSFRow row = sheet.getRow(rownum);
		if (row == null) {
			row = sheet.createRow(rownum);
		}
		return row;
	}

	public static final XSSFCell addCell(XSSFRow row, int pos, Long value) {
		XSSFCell cell1 = row.createCell(pos);
		cell1.setCellStyle(row.getSheet().getWorkbook().getCellStyleAt(CellStylesEnum.NUMBER.index));
		cell1.setCellValue(value == null ? 0L : value.doubleValue());
		return cell1;
	}

	public static final XSSFCell addCell(XSSFRow row, int pos, BigDecimal value) {
		XSSFCell cell1 = row.createCell(pos);
		cell1.setCellStyle(row.getSheet().getWorkbook().getCellStyleAt(CellStylesEnum.DECIMAL.index));
		cell1.setCellValue(value == null ? BigDecimal.ZERO.doubleValue() : value.doubleValue());
		return cell1;
	}

	public static final XSSFCell addCell(XSSFRow row, int pos, BigDecimal value, CellStylesEnum style) {
		XSSFCell cell1 = row.createCell(pos);
		cell1.setCellStyle(row.getSheet().getWorkbook().getCellStyleAt(style.index));
		if (value != null) {
			cell1.setCellValue(value == null ? BigDecimal.ZERO.doubleValue() : value.doubleValue());
		}
		return cell1;
	}

	public static final XSSFCell addCell(XSSFRow row, int pos, String value, CellStylesEnum style, int width) {
		XSSFCell cell1 = row.createCell(pos);
		cell1.setCellStyle(row.getSheet().getWorkbook().getCellStyleAt(style.index));
		cell1.setCellValue(value);
		setColumnWidth(row.getSheet(), pos, width);
		return cell1;
	}

	public static final XSSFCell addCell(XSSFRow row, int pos, String value, CellStylesEnum style) {
		XSSFCell cell1 = row.createCell(pos);
		cell1.setCellStyle(row.getSheet().getWorkbook().getCellStyleAt(style.index));
		cell1.setCellValue(value);
		cell1.getCellStyle().setWrapText(false);
		return cell1;
	}

	public static final XSSFCell addCell(XSSFRow row, int pos) {
		return addCell(row, pos, (String) null, CellStylesEnum.TEXT);
	}

	public static final XSSFCell addCell(XSSFRow row, int pos, String value) {
		return addCell(row, pos, value, CellStylesEnum.TEXT);
	}

	public static final XSSFCell addCell(XSSFRow row, int pos, Date value) {
		return addCell(row, pos, value, CellStylesEnum.DATE);
	}

	public static final XSSFCell addCell(XSSFRow row, int pos, Date value, CellStylesEnum style) {
		XSSFCell cell1 = row.createCell(pos);
		cell1.setCellStyle(row.getSheet().getWorkbook().getCellStyleAt(style.index));
		cell1.setCellValue(value);
		return cell1;
	}

	public static final XSSFCell addMergedRegion(XSSFSheet sheet, XSSFRow row, int firstCol, int lastCol, Object value,
			CellStylesEnum style) {

		CreationHelper createHelper = sheet.getWorkbook().getCreationHelper();

		CellRangeAddress cellRangeAddress = new CellRangeAddress(row.getRowNum(), row.getRowNum(), firstCol, lastCol);
		sheet.addMergedRegion(cellRangeAddress);

		XSSFCell cell1 = row.createCell(firstCol);

		CellStyle cellStyle = sheet.getWorkbook().getCellStyleAt(style.index);

		if (value instanceof Number) {
			cell1.setCellValue(value == null ? null : ((Number) value).doubleValue());
		} else if (value instanceof Date) {
			cell1.setCellValue(value == null ? null : (Date) value);
			cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
		} else {
			cell1.setCellValue(value == null ? "" : value.toString());
		}

		if (style.hasBorders) {
			RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, sheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress, sheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
			RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, sheet);

			RegionUtil.setTopBorderColor(IndexedColors.GREY_40_PERCENT.index, cellRangeAddress, sheet);
			RegionUtil.setRightBorderColor(IndexedColors.GREY_40_PERCENT.index, cellRangeAddress, sheet);
			RegionUtil.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index, cellRangeAddress, sheet);
			RegionUtil.setLeftBorderColor(IndexedColors.GREY_40_PERCENT.index, cellRangeAddress, sheet);
		}

		row.setHeight((short) 300);

		cell1.setCellStyle(cellStyle);

		return cell1;
	}

	public static final void setColumnWidth(XSSFSheet sheet, int colNum, int width) {
		sheet.setColumnWidth(colNum, (int) sheet.getDefaultColumnWidth() * width * 8);
	}

	public static final void setAllColumnsWidth(XSSFSheet sheet, int colsCount, int width) {
		for (int i = 0; i < colsCount; i++) {
			sheet.setColumnWidth(i, (int) sheet.getDefaultColumnWidth() * width * 8);
		}
	}

	public static final XSSFCell setCellFormula(XSSFCell cell, String formula) {
		formula = formula.replaceAll("\\$1", String.valueOf(cell.getRow().getRowNum() + 1));
		return evaluateFormula(cell, formula, null);
	}

	public static final XSSFCell setCellFormula(XSSFCell cell, String formula, CellStylesEnum style) {
		formula = formula.replaceAll("\\$1", String.valueOf(cell.getRow().getRowNum() + 1));
		return evaluateFormula(cell, formula, style);
	}

	public static final XSSFCell setCellFormula(XSSFSheet sheet, XSSFRow row, String formula, int pos,
			CellStylesEnum style) {
		formula = formula.replaceAll("\\$1", String.valueOf(row.getRowNum() + 1));
		return evaluateFormula(sheet, row, pos, formula, style);
	}

	public static final XSSFCell setCellFormula(XSSFSheet sheet, XSSFRow row, String formula,
			List<XSSFCell> listaValoresPrevistos, int pos) {

		if (Utils.isEmpty(listaValoresPrevistos)) {
			return row.createCell(pos);
		}

		formula = String.format(formula, //
				listaValoresPrevistos.get(0).getReference(), //
				listaValoresPrevistos.get(listaValoresPrevistos.size() - 1).getReference());

		return setCellFormula(sheet, row, formula, pos, null);
	}

	public static final XSSFCell setCellFormula(XSSFSheet sheet, XSSFRow row, String formula, int pos) {
		return setCellFormula(sheet, row, formula, pos, null);
	}

	public static final void setCellFormula(XSSFSheet sheet, XSSFRow row, String formula, int pos, int posInicio,
			int posFinal, CellStylesEnum style) {
		formula = formula.replaceAll("\\$1", String.valueOf(posInicio));
		formula = formula.replaceAll("\\$2", String.valueOf(posFinal));
		evaluateFormula(sheet, row, pos, formula, style);
	}

	public static final void setCellFormula(XSSFSheet sheet, XSSFRow row, String formula, int pos, int posInicio, int posFinal) {
		setCellFormula(sheet, row, formula, pos, posInicio, posFinal, null);
	}

	public static final XSSFCell evaluateFormula(XSSFSheet sheet, XSSFRow row, int pos, String formula,
			CellStylesEnum style) {

		XSSFCell c0 = row.createCell(pos);

		FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

		c0.setCellFormula(formula);
		evaluator.evaluateFormulaCell(c0);

		if (style != null) {
			XSSFCellStyle cellStyle = row.getSheet().getWorkbook().getCellStyleAt(style.index);
			if (cellStyle != null) {
				c0.setCellStyle(row.getSheet().getWorkbook().getCellStyleAt(style.index));
			}
		}

		return c0;

	}

	public static final XSSFCell evaluateFormula(XSSFCell cell, String formula, CellStylesEnum style) {

		FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();

		try {
			cell.setCellFormula(formula);
			evaluator.evaluateFormulaCell(cell);

			if (style != null) {
				cell.setCellStyle(cell.getSheet().getWorkbook().getCellStyleAt(style.index));
			}
		} catch (FormulaParseException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		return cell;
	}
}
