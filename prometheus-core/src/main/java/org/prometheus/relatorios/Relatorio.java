package org.prometheus.relatorios;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.utils.Utils;

public abstract class Relatorio {
    protected Map<String, Integer> fontes = new HashMap<>();
    protected Map<String, XSSFCellStyle> estilos = new HashMap<>();

    public static final String ALIGN_CENTER = "ALIGN_CENTER";
    public static final String ALIGN_CENTER_BLUE_BOLD = "ALIGN_CENTER_BLUE_BOLD";
    public static final String ALIGN_CENTER_BOLD = "ALIGN_CENTER_BOLD";
    public static final String ALIGN_CENTER_GREEN_BOLD = "ALIGN_CENTER_GREEN_BOLD";
    public static final String ALIGN_CENTER_GREY_25_PERCENT_BOLD = "ALIGN_CENTER_GREY_25_PERCENT_BOLD";
    public static final String ALIGN_CENTER_GREY_40_PERCENT_BOLD = "ALIGN_CENTER_GREY_40_PERCENT_BOLD";
    public static final String ALIGN_CENTER_ORANGE_BOLD = "ALIGN_CENTER_ORANGE_BOLD";
    public static final String ALIGN_CENTER_RED_BOLD = "ALIGN_CENTER_RED_BOLD";
    public static final String ALIGN_CENTER_WHITE = "ALIGN_CENTER_WHITE";
    public static final String ALIGN_CENTER_WHITE_BOLD = "ALIGN_CENTER_WHITE_BOLD";

    public static final String ALIGN_LEFT = "ALIGN_LEFT";
    public static final String ALIGN_LEFT_GREY_25_PERCENT_BOLD = "ALIGN_LEFT_GREY_25_PERCENT_BOLD";

    public static final String ALIGN_RIGHT_BOLD = "ALIGN_RIGHT_BOLD";
    public static final String ALIGN_RIGHT_BLUE_BOLD = "ALIGN_RIGHT_BLUE_BOLD";
    public static final String ALIGN_RIGHT_GREEN_BOLD = "ALIGN_RIGHT_GREEN_BOLD";
    public static final String ALIGN_RIGHT_GREY_25_PERCENT_BOLD = "ALIGN_RIGHT_GREY_25_PERCENT_BOLD";
    public static final String ALIGN_RIGHT_ORANGE_BOLD = "ALIGN_RIGHT_ORANGE_BOLD";
    public static final String ALIGN_RIGHT_RED_BOLD = "ALIGN_RIGHT_RED_BOLD";

    public static final short FONT_SIZE = (short) 9;
    public static final String FONT_FAMILY = "Arial";
    public static final String FONTE_BOLD = "FONTE_BOLD";
    public static final String FONTE_NORMAL = "FONTE_NORMAL";
    public static final String FONTE_WHITE_BOLD = "FONTE_WHITE_BOLD";
    public static final String FONTE_RED_BOLD = "FONTE_RED_BOLD";

    public static final String STYLE_DATE = "STYLE_DATE";
    public static final String STYLE_DATE_TIME = "STYLE_DATE_TIME";
    public static final String STYLE_DECIMAL = "STYLE_DECIMAL";
    public static final String STYLE_DECIMAL_5DIGITS = "STYLE_DECIMAL_5DIGITS";
    public static final String STYLE_NUMBER = "STYLE_NUMBER";
    public static final String STYLE_TEXT = "STYLE_TEXT";
    public static final String STYLE_REDBOLD_TEXT = "STYLE_REDBOLD_TEXT";
    public static final String STYLE_PERCENT = "STYLE_PERCENT";

    public static final String BOLD = "_BOLD";

    protected void setColumnWidth(XSSFSheet sheet, int colNum, int width) {
        sheet.setColumnWidth(colNum, (int) sheet.getColumnWidthInPixels(colNum) * width);
    }

    protected void setAllColumnsWidth(XSSFSheet sheet, int colsCount, int width) {
        for (int i = 0; i < colsCount; i++) {
            sheet.setColumnWidth(i, (int) sheet.getColumnWidthInPixels(i) * width);
        }
    }

    protected final void setCellComment(XSSFCell c0, String text) {
        CreationHelper factory = c0.getSheet().getWorkbook().getCreationHelper();

        XSSFDrawing drawing = c0.getSheet().createDrawingPatriarch();

        ClientAnchor anchor = factory.createClientAnchor();
        anchor.setCol1(c0.getColumnIndex());
        anchor.setCol2(c0.getColumnIndex() + 5);
        anchor.setRow1(c0.getRow().getRowNum());
        anchor.setRow2(c0.getRow().getRowNum() + 3);

        Comment comment = drawing.createCellComment(anchor);

        RichTextString str = factory.createRichTextString(text);
        comment.setString(str);
        comment.setAuthor("SIGCT - Fábrica de Software");

        c0.setCellComment(comment);
    }

    protected final XSSFRow getRow(XSSFSheet sheet, int rownum) {
        XSSFRow row = sheet.getRow(rownum);
        if (row == null) {
            row = sheet.createRow(rownum);
        }
        return row;
    }

    protected final void addBlank(XSSFSheet sheet) {
        sheet.createRow(sheet.getLastRowNum() + 1);
    }

    protected final XSSFRow addNewRow(XSSFSheet sheet) {
        return sheet.createRow(sheet.getLastRowNum() + 1);
    }

    protected final XSSFRow addNewRow(XSSFSheet sheet, int rownum) {
        if (sheet.getRow(rownum) != null) {
            return sheet.getRow(rownum);
        }
        return sheet.createRow(sheet.getLastRowNum() + 1);
    }

    protected final void addMergedRegion(XSSFSheet sheet, XSSFRow row, int firstCol, int lastCol, Object value) {
        addMergedRegion(sheet, row, firstCol, lastCol, value, ALIGN_LEFT, true);
    }

    protected final void addMergedRegion2(XSSFSheet sheet, int firstRow, int lastRow, int colNum, Object value) {

        CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow, colNum, colNum);
        sheet.addMergedRegion(cellRangeAddress);

        XSSFRow row = getRow(sheet, firstRow);

        XSSFCell cell1 = row.createCell(colNum);

        XSSFCellStyle s20 = sheet.getWorkbook().createCellStyle();
        s20.setAlignment(HorizontalAlignment.CENTER_SELECTION);
        s20.setVerticalAlignment(VerticalAlignment.CENTER);
        s20.setRotation((short) 90);

        cell1.setCellStyle(s20);

        if (value instanceof Number) {
            cell1.setCellValue(value == null ? null : ((Number) value).doubleValue());
        } else {
            cell1.setCellValue(value == null ? "" : value.toString());
        }

        RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, sheet);

        RegionUtil.setTopBorderColor(IndexedColors.BLACK.index, cellRangeAddress, sheet);
        RegionUtil.setRightBorderColor(IndexedColors.BLACK.index, cellRangeAddress, sheet);
        RegionUtil.setBottomBorderColor(IndexedColors.BLACK.index, cellRangeAddress, sheet);
        RegionUtil.setLeftBorderColor(IndexedColors.BLACK.index, cellRangeAddress, sheet);

    }

    protected final XSSFCell addMergedRegion(XSSFSheet sheet, XSSFRow row, int firstCol, int lastCol, Object value,
            String style) {
        return addMergedRegion(sheet, row, firstCol, lastCol, value, style, true);
    }

    protected final XSSFCell addMergedRegion(XSSFSheet sheet, XSSFRow row, int firstCol, int lastCol, Object value,
            String style, boolean hasBorders) {

        CreationHelper createHelper = sheet.getWorkbook().getCreationHelper();

        CellRangeAddress cellRangeAddress = new CellRangeAddress(row.getRowNum(), row.getRowNum(), firstCol, lastCol);
        sheet.addMergedRegion(cellRangeAddress);

        XSSFCell cell1 = row.createCell(firstCol);

        CellStyle cellStyle = estilos.get(style);

        if (value instanceof Number) {
            cell1.setCellValue(value == null ? null : ((Number) value).doubleValue());
        } else if (value instanceof Date) {
            cell1.setCellValue(value == null ? null : (Date) value);
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
        } else {
            cell1.setCellValue(value == null ? "" : value.toString());
        }

        cell1.setCellStyle(cellStyle);

        if (hasBorders) {
            RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, sheet);
            RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
            RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, sheet);

            RegionUtil.setTopBorderColor(IndexedColors.BLACK.index, cellRangeAddress, sheet);
            RegionUtil.setRightBorderColor(IndexedColors.BLACK.index, cellRangeAddress, sheet);
            RegionUtil.setBottomBorderColor(IndexedColors.BLACK.index, cellRangeAddress, sheet);
            RegionUtil.setLeftBorderColor(IndexedColors.BLACK.index, cellRangeAddress, sheet);
        }

        return cell1;
    }

    private void setFillForeground(XSSFCellStyle s0, short color) {
        s0.setFillForegroundColor(color);
        s0.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    private void setAlignLeft(final XSSFCellStyle s0) {
        s0.setAlignment(HorizontalAlignment.LEFT);
        s0.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    private void setAlignRight(XSSFCellStyle s0) {
        s0.setAlignment(HorizontalAlignment.RIGHT);
        s0.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    private void setAlignCenter(XSSFCellStyle s0) {
        s0.setAlignment(HorizontalAlignment.CENTER);
        s0.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    protected final void addBorders(XSSFCellStyle style) {

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        style.setTopBorderColor(IndexedColors.GREY_80_PERCENT.index);
        style.setRightBorderColor(IndexedColors.GREY_80_PERCENT.index);
        style.setBottomBorderColor(IndexedColors.GREY_80_PERCENT.index);
        style.setLeftBorderColor(IndexedColors.GREY_80_PERCENT.index);

    }

    protected final XSSFCell addCell(XSSFRow row, int pos, BigDecimal value) {
        XSSFCell cell1 = row.createCell(pos);
        cell1.setCellStyle(estilos.get(STYLE_NUMBER));
        cell1.setCellValue(value == null ? BigDecimal.ZERO.doubleValue() : value.doubleValue());
        return cell1;
    }

    protected final XSSFCell addCell(XSSFRow row, int pos, BigDecimal value, String style) {
        XSSFCell cell1 = row.createCell(pos);
        XSSFCellStyle s0 = estilos.get(style);
        if (s0 == null) {
            s0 = estilos.get(STYLE_DECIMAL);
        }

        if (value != null) {
            cell1.setCellValue(value == null ? BigDecimal.ZERO.doubleValue() : value.doubleValue());
        }

        cell1.setCellStyle(s0);

        return cell1;
    }

    protected final XSSFCell addCell(XSSFRow row, int pos, String value, String style, int width) {
        XSSFCell cell1 = row.createCell(pos);
        cell1.setCellStyle(estilos.get(style));
        cell1.setCellValue(value);
        setColumnWidth(row.getSheet(), pos, width);
        return cell1;
    }

    protected final XSSFCell addCell(XSSFRow row, int pos, String value, String style) {
        XSSFCell cell1 = row.createCell(pos);
        cell1.setCellStyle(estilos.get(style));
        cell1.setCellValue(value);
        return cell1;
    }

    protected final XSSFCell addCell(XSSFRow row, int pos) {
        return addCell(row, pos, (String) null, STYLE_TEXT);
    }

    protected final XSSFCell addCell(XSSFRow row, int pos, String value) {
        return addCell(row, pos, value, STYLE_TEXT);
    }

    protected final XSSFCell addCell(XSSFRow row, int pos, Date value) {
        return addCell(row, pos, value, STYLE_DATE);
    }

    protected final XSSFCell addCell(XSSFRow row, int pos, Date value, String style) {
        XSSFCell cell1 = row.createCell(pos);
        XSSFCellStyle s0 = estilos.get(style);
        cell1.setCellStyle(s0);
        cell1.setCellValue(value);
        return cell1;
    }

    protected final void evaluateFormula(XSSFSheet sheet, XSSFRow row, int pos, String formula) {
        evaluateFormula(sheet, row, pos, formula, null);
    }

    protected final XSSFCell evaluateFormula(XSSFSheet sheet, XSSFRow row, int pos, String formula, String style) {

        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        XSSFCell c0 = row.createCell(pos);
        c0.setCellFormula(formula);

        if (style == null) {
            c0.setCellStyle(estilos.get(STYLE_DECIMAL));
        } else {
            c0.setCellStyle(estilos.get(style));
        }

        evaluator.evaluateFormulaCell(c0);

        return c0;

    }

    protected final XSSFCell evaluateFormula(XSSFCell cell, String formula, String style) {

        FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();

        cell.setCellFormula(formula);

        if (style == null) {
            cell.setCellStyle(estilos.get(STYLE_DECIMAL));
        } else {
            cell.setCellStyle(estilos.get(style));
        }

        evaluator.evaluateFormulaCell(cell);

        return cell;
    }

    protected final void carregarFontes(XSSFWorkbook workBook) {

        XSSFFont normal = workBook.createFont();
        normal.setFontHeightInPoints(FONT_SIZE);
        normal.setFontName(FONT_FAMILY);
        normal.setColor(IndexedColors.BLACK.getIndex());
        fontes.put(FONTE_NORMAL, normal.getIndexAsInt());

        XSSFFont bold = workBook.createFont();
        bold.setFontHeightInPoints(FONT_SIZE);
        bold.setFontName(FONT_FAMILY);
        bold.setColor(IndexedColors.BLACK.getIndex());
        bold.setBold(true);
        fontes.put(FONTE_BOLD, bold.getIndexAsInt());

        XSSFFont whiteBold = workBook.createFont();
        whiteBold.setFontHeightInPoints(FONT_SIZE);
        whiteBold.setFontName(FONT_FAMILY);
        whiteBold.setColor(IndexedColors.WHITE.getIndex());
        whiteBold.setBold(true);
        fontes.put(FONTE_WHITE_BOLD, whiteBold.getIndexAsInt());

        XSSFFont redBold = workBook.createFont();
        redBold.setFontHeightInPoints(FONT_SIZE);
        whiteBold.setFontName(FONT_FAMILY);
        redBold.setColor(IndexedColors.RED.getIndex());
        redBold.setBold(true);
        fontes.put(FONTE_RED_BOLD, redBold.getIndexAsInt());
    }

    protected final void carregarEstilos(XSSFWorkbook wb) {

        CreationHelper createHelper = wb.getCreationHelper();

        XSSFCellStyle s1 = wb.createCellStyle();
        setAlignCenter(s1);
        s1.setFont(wb.getFontAt(fontes.get(FONTE_NORMAL)));
        addBorders(s1);
        estilos.put(ALIGN_CENTER, s1);

        XSSFCellStyle s2 = wb.createCellStyle();
        setAlignCenter(s2);
        s2.setFont(wb.getFontAt(fontes.get(FONTE_BOLD)));
        addBorders(s2);
        estilos.put(ALIGN_CENTER_BOLD, s2);

        XSSFCellStyle s3 = wb.createCellStyle();
        s3.setWrapText(true);
        setAlignCenter(s3);
        setFillForeground(s3, IndexedColors.GREY_25_PERCENT.index);
        s3.setFont(wb.getFontAt(fontes.get(FONTE_BOLD)));
        addBorders(s3);
        estilos.put(ALIGN_CENTER_GREY_25_PERCENT_BOLD, s3);

        XSSFCellStyle s4 = wb.createCellStyle();
        setAlignLeft(s4);
        setFillForeground(s4, IndexedColors.GREY_25_PERCENT.index);
        s4.setFont(wb.getFontAt(fontes.get(FONTE_BOLD)));
        addBorders(s4);
        estilos.put(ALIGN_RIGHT_GREY_25_PERCENT_BOLD, s4);

        XSSFCellStyle s5 = wb.createCellStyle();
        setAlignRight(s5);
        setFillForeground(s5, IndexedColors.ORANGE.index);
        s5.setFont(wb.getFontAt(fontes.get(FONTE_WHITE_BOLD)));
        addBorders(s5);
        estilos.put(ALIGN_RIGHT_ORANGE_BOLD, s5);

        XSSFCellStyle s7 = wb.createCellStyle();
        setAlignRight(s7);
        setFillForeground(s7, IndexedColors.LIGHT_BLUE.index);
        s7.setFont(wb.getFontAt(fontes.get(FONTE_WHITE_BOLD)));
        addBorders(s7);
        estilos.put(ALIGN_RIGHT_BLUE_BOLD, s7);

        final XSSFCellStyle s8 = wb.createCellStyle();
        setAlignCenter(s8);
        setFillForeground(s8, IndexedColors.GREY_40_PERCENT.index);
        s8.setFont(wb.getFontAt(fontes.get(FONTE_WHITE_BOLD)));
        addBorders(s8);
        estilos.put(ALIGN_CENTER_GREY_40_PERCENT_BOLD, s8);

        XSSFCellStyle s9 = wb.createCellStyle();
        setAlignCenter(s9);
        setFillForeground(s9, IndexedColors.RED.index);
        s9.setFont(wb.getFontAt(fontes.get(FONTE_WHITE_BOLD)));
        addBorders(s9);
        estilos.put(ALIGN_CENTER_RED_BOLD, s9);

        XSSFCellStyle s10 = wb.createCellStyle();
        setAlignCenter(s10);
        setFillForeground(s10, IndexedColors.GREEN.index);
        s10.setFont(wb.getFontAt(fontes.get(FONTE_WHITE_BOLD)));
        addBorders(s10);
        estilos.put(ALIGN_CENTER_GREEN_BOLD, s10);

        XSSFCellStyle s11 = wb.createCellStyle();
        setAlignCenter(s11);
        setFillForeground(s11, IndexedColors.WHITE.index);
        s11.setFont(wb.getFontAt(fontes.get(FONTE_BOLD)));
        estilos.put(ALIGN_CENTER_WHITE_BOLD, s11);

        XSSFCellStyle s12 = wb.createCellStyle();
        s12.setFont(wb.getFontAt(fontes.get(FONTE_NORMAL)));
        s12.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
        setAlignCenter(s12);
        addBorders(s12);
        estilos.put(STYLE_DATE, s12);

        XSSFCellStyle s13 = wb.createCellStyle();
        s13.setFont(wb.getFontAt(fontes.get(FONTE_NORMAL)));
        s13.setDataFormat(
                createHelper.createDataFormat().getFormat("###,###,###,###,##0.00;[Red]-###,###,###,###,##0.00"));
        setAlignRight(s13);
        addBorders(s13);
        estilos.put(STYLE_DECIMAL, s13);

        final XSSFCellStyle s14 = wb.createCellStyle();
        addBorders(s14);
        setAlignLeft(s14);
        s14.setFont(wb.getFontAt(fontes.get(FONTE_NORMAL)));
        estilos.put(STYLE_TEXT, s14);

        XSSFCellStyle s15 = wb.createCellStyle();
        s15.setFont(wb.getFontAt(fontes.get(FONTE_NORMAL)));
        s15.setDataFormat(createHelper.createDataFormat().getFormat("##############0"));
        s15.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorders(s15);
        estilos.put(STYLE_NUMBER, s15);

        XSSFCellStyle s16 = wb.createCellStyle();
        s16.setFont(wb.getFontAt(fontes.get(FONTE_NORMAL)));
        s16.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));
        setAlignCenter(s16);
        addBorders(s16);
        estilos.put(STYLE_DATE_TIME, s16);

        XSSFCellStyle s17 = wb.createCellStyle();
        setAlignCenter(s17);
        setFillForeground(s17, IndexedColors.LIGHT_BLUE.index);
        s17.setFont(wb.getFontAt(fontes.get(FONTE_WHITE_BOLD)));
        estilos.put(ALIGN_CENTER_BLUE_BOLD, s17);

        XSSFCellStyle s18 = wb.createCellStyle();
        setAlignCenter(s18);
        setFillForeground(s18, IndexedColors.ORANGE.index);
        s18.setFont(wb.getFontAt(fontes.get(FONTE_WHITE_BOLD)));
        estilos.put(ALIGN_CENTER_ORANGE_BOLD, s18);

        XSSFCellStyle s19 = wb.createCellStyle();
        setAlignRight(s19);
        setFillForeground(s19, IndexedColors.RED.index);
        s19.setFont(wb.getFontAt(fontes.get(FONTE_WHITE_BOLD)));
        addBorders(s19);
        estilos.put(ALIGN_RIGHT_RED_BOLD, s19);

        XSSFCellStyle s20 = wb.createCellStyle();
        setAlignRight(s20);
        setFillForeground(s20, IndexedColors.GREEN.index);
        s20.setFont(wb.getFontAt(fontes.get(FONTE_WHITE_BOLD)));
        estilos.put(ALIGN_RIGHT_GREEN_BOLD, s20);

        XSSFCellStyle s21 = wb.createCellStyle();
        setAlignLeft(s21);
        s21.setFont(wb.getFontAt(fontes.get(FONTE_NORMAL)));
        estilos.put(ALIGN_LEFT, s21);

        final XSSFCellStyle s22 = wb.createCellStyle();
        s22.setFont(wb.getFontAt(fontes.get(FONTE_BOLD)));
        setAlignLeft(s22);
        setFillForeground(s22, IndexedColors.GREY_25_PERCENT.index);
        addBorders(s22);
        estilos.put(ALIGN_LEFT_GREY_25_PERCENT_BOLD, s22);

        XSSFCellStyle s23 = wb.createCellStyle();
        s23.setFont(wb.getFontAt(fontes.get(FONTE_NORMAL)));
        s23.setDataFormat(createHelper.createDataFormat().getFormat("###,###,###,###,##0.00000"));
        addBorders(s23);
        estilos.put(STYLE_DECIMAL_5DIGITS, s23);

        XSSFCellStyle s24 = wb.createCellStyle();
        setAlignCenter(s24);
        setFillForeground(s24, IndexedColors.WHITE.index);
        s24.setFont(wb.getFontAt(fontes.get(FONTE_NORMAL)));
        estilos.put(ALIGN_CENTER_WHITE, s24);

        XSSFCellStyle s25 = wb.createCellStyle();
        setAlignRight(s25);
        s25.setFont(wb.getFontAt(fontes.get(FONTE_BOLD)));
        addBorders(s25);
        estilos.put(ALIGN_RIGHT_BOLD, s25);

        XSSFCellStyle s26 = wb.createCellStyle();
        s26.setFont(wb.getFontAt(fontes.get(FONTE_NORMAL)));
        s26.setDataFormat(
                createHelper.createDataFormat().getFormat("###,###,###,###,##0.00%;[Red]-###,###,###,###,##0.00%"));
        setAlignRight(s26);
        addBorders(s26);
        estilos.put(STYLE_PERCENT, s26);

    }

    protected XSSFCell setCellFormula(XSSFCell cell, String formula) {
        formula = formula.replaceAll("\\$1", String.valueOf(cell.getRow().getRowNum() + 1));
        return evaluateFormula(cell, formula, null);
    }

    protected XSSFCell setCellFormula(XSSFCell cell, String formula, String style) {
        formula = formula.replaceAll("\\$1", String.valueOf(cell.getRow().getRowNum() + 1));
        return evaluateFormula(cell, formula, style);
    }

    protected XSSFCell setCellFormula(XSSFSheet sheet, XSSFRow row, String formula, int pos, String style) {
        formula = formula.replaceAll("\\$1", String.valueOf(row.getRowNum() + 1));
        return evaluateFormula(sheet, row, pos, formula, style);
    }

    protected XSSFCell setCellFormula(XSSFSheet sheet, XSSFRow row, String formula, int pos) {
        return setCellFormula(sheet, row, formula, pos, null);
    }

    protected void setCellFormula(XSSFSheet sheet, XSSFRow row, String formula, int pos, int posInicio, int posFinal,
            String style) {
        formula = formula.replaceAll("\\$1", String.valueOf(posInicio));
        formula = formula.replaceAll("\\$2", String.valueOf(posFinal));
        evaluateFormula(sheet, row, pos, formula, style);
    }

    protected void setCellFormula(XSSFSheet sheet, XSSFRow row, String formula, int pos, int posInicio, int posFinal) {
        setCellFormula(sheet, row, formula, pos, posInicio, posFinal, null);
    }

    protected String getCellReference(String sheet, String reference) {
        if (Utils.isNotEmpty(sheet)) {
            return String.format("'%s'!%s", sheet, reference);
        }
        return reference;
    }

    protected void addIndicador(Map<Long, List<XSSFCell>> indicadores, Long nuFatura, XSSFCell cell) {
        if (indicadores != null) {
            if (indicadores.get(nuFatura) == null) {
                indicadores.put(nuFatura, new ArrayList<XSSFCell>());
            }
        }

        indicadores.get(nuFatura).add(cell);
    }

    protected String getMediaIndicador(Map<Long, List<XSSFCell>> indicadores, Long nuFatura, int qtFaturamentos) {

        List<Long> i = new ArrayList<>();

        for (Long key : indicadores.keySet()) {
            i.add(0, key);
            if (key.equals(nuFatura)) {
                break;
            }
        }

        List<Long> cells = new ArrayList<>();

        if (i.size() > 2) {
            cells.addAll(i.subList(0, 3));
        } else {
            cells.addAll(i);
        }

        StringBuilder sb = new StringBuilder();

        if (cells != null && !cells.isEmpty()) {
            for (Long j : cells) {
                for (XSSFCell c : indicadores.get(j)) {
                    try {
                        if (c != null && !"N/A".equalsIgnoreCase(c.toString())) {
                            sb.append(", " + c.getReference());
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }

        if (Utils.isNotEmpty(sb.toString())) {
            return sb.toString().substring(2);
        }

        return null;
    }

    protected List<XSSFCell> getIndicadoresMediaFNS(Map<Long, List<XSSFCell>> indicadores, Long nuFatura,
            int qtFaturamentos) {

        List<XSSFCell> result = new ArrayList<XSSFCell>();

        List<Long> i = new ArrayList<>();

        for (Long key : indicadores.keySet()) {
            i.add(0, key);
            if (key.equals(nuFatura)) {
                break;
            }
        }

        List<Long> cells = new ArrayList<>();

        if (i.size() > (qtFaturamentos - 1)) {
            cells.addAll(i.subList(0, qtFaturamentos));
        } else {
            cells.addAll(i);
        }

        if (Utils.isNotEmpty(cells)) {
            for (Long j : cells) {
                for (XSSFCell c : indicadores.get(j)) {
                    if (c != null && c.getReference() != null && !result.contains(c)) {
                        result.add(c);
                    }
                }
            }
        }

        return result;
    }

    protected XSSFCell getIndicador(Map<Long, List<XSSFCell>> indicadores, Long nuFatura) {
        indicadores.get(nuFatura);
        return null;
    }

    protected void setNoNull(XSSFRow row, int pos, BigDecimal value) {
        setNoNull(row, pos, value, null);
    }

    protected void setNoNull(XSSFRow row, int pos, BigDecimal value, String style) {
        if (value == null) {
            addCell(row, pos, "-", ALIGN_CENTER);
        } else {
            if (style != null) {
                addCell(row, pos, value, style);
            } else {
                addCell(row, pos, value);
            }
        }
    }
}
