package org.prometheus.utils;

import java.util.Date;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.prometheus.enums.CellStylesEnum;

public class XSSFSheetUtils {

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

    public static final XSSFCell addMergedRegion(XSSFSheet sheet, XSSFRow row, int firstCol, int lastCol, Object value) {
        return addMergedRegion(sheet, row, firstCol, lastCol, value, CellStylesEnum.NORMAL_ALIGN_LEFT);
    }


}
