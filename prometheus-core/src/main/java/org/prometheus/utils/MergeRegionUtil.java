package org.prometheus.utils;

import java.util.Date;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.prometheus.enums.CellStylesEnum;

public class MergeRegionUtil {

    public static final XSSFCell addMergedRegion(XSSFSheet sheet, XSSFRow row, int primeiraColuna, int qtColunas,  
            Object value, CellStylesEnum style) {
        return addMergedRegion(sheet, row, primeiraColuna, qtColunas, 1, value, style);
    }

    public static final XSSFCell addMergedRegion(XSSFSheet sheet, XSSFRow row, int primeiraColuna, int qtColunas, int qtLinhas, 
            Object value, CellStylesEnum style) {

        CellRangeAddress cellRangeAddress = new CellRangeAddress(row.getRowNum(), row.getRowNum() + (qtLinhas - 1), primeiraColuna, primeiraColuna + qtColunas);
        sheet.addMergedRegion(cellRangeAddress);

        XSSFCell cell1 = row.createCell(primeiraColuna);
        cell1.getSheet().getWorkbook().getCellStyleAt(style.index);

        if (value instanceof Number) {
            cell1.setCellValue(value == null ? null : ((Number) value).doubleValue());
        } else if (value instanceof Date) {
            cell1.setCellValue(value == null ? null : (Date) value);
        } else {
            cell1.setCellValue(value == null ? "" : value.toString());
        }

        RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, sheet);

        RegionUtil.setTopBorderColor(IndexedColors.GREY_40_PERCENT.index, cellRangeAddress, sheet);
        RegionUtil.setRightBorderColor(IndexedColors.GREY_40_PERCENT.index, cellRangeAddress, sheet);
        RegionUtil.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index, cellRangeAddress, sheet);
        RegionUtil.setLeftBorderColor(IndexedColors.GREY_40_PERCENT.index, cellRangeAddress, sheet);

        row.setHeight((short) 300);

        return cell1;
    }

}
