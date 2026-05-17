package org.prometheus.relatorios;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.BorderStyle;
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
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.AppInfo;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.enums.FontWeightEnum;
import org.prometheus.enums.FontsEnum;
import org.prometheus.enums.ValueFormatsEnum;
import org.prometheus.utils.Utils;

public abstract class Relatorio {

    public static final String PATH = System.getProperty("user.dir") + File.separator + "Demonstrativos";
    //private static final String PATH = "/projetos/caixa/sigct/Trabalho/RTC23192487/Demonstrativos";    
    
    public static final Integer DEFAULT_COLUMNS_WIDTH = 60;
    
	private Map<Long, Map<String, XSSFCell>> listaIndicadores;
	private Map<Long, Map<String, XSSFCell>> listaMediaIndicadores;    
    
    protected void setColumnWidth(XSSFSheet sheet, int colNum, int width) {
        sheet.setColumnWidth(colNum, (int) sheet.getDefaultColumnWidth() * width * 8);
    }

    protected void setAllColumnsWidth(XSSFSheet sheet, int colsCount, int width) {
        for (int i = 0; i < colsCount; i++) {
            sheet.setColumnWidth(i, (int) sheet.getDefaultColumnWidth() * width * 8);
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

    protected final XSSFRow getLastRow(XSSFSheet sheet) {
        return sheet.getRow(sheet.getLastRowNum());
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

    protected final void addMergedRegion2(XSSFSheet sheet, int firstRow, int lastRow, int firstCol, Object value) {
        addMergedRegion2(sheet, firstRow, lastRow, firstCol, firstCol, value);
    }

    protected final void addMergedRegion2(XSSFSheet sheet, int firstRow, int lastRow, int firstCol, int lastCol,
            Object value) {

        CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegion(cellRangeAddress);

        XSSFRow row = getRow(sheet, firstRow);

        XSSFCell cell1 = row.createCell(firstCol);

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

        RegionUtil.setTopBorderColor(IndexedColors.GREY_40_PERCENT.index, cellRangeAddress, sheet);
        RegionUtil.setRightBorderColor(IndexedColors.GREY_40_PERCENT.index, cellRangeAddress, sheet);
        RegionUtil.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index, cellRangeAddress, sheet);
        RegionUtil.setLeftBorderColor(IndexedColors.GREY_40_PERCENT.index, cellRangeAddress, sheet);

    }

    private void setFillForeground(XSSFCellStyle s0, XSSFColor color) {
        s0.setFillForegroundColor(color);
        s0.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    private void setAlign(XSSFCellStyle s0, HorizontalAlignment hAlignment) {
        s0.setAlignment(hAlignment);
        s0.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    protected final void addBorders(XSSFCellStyle style) {

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        style.setTopBorderColor(IndexedColors.GREY_40_PERCENT.index);
        style.setRightBorderColor(IndexedColors.GREY_40_PERCENT.index);
        style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
        style.setLeftBorderColor(IndexedColors.GREY_40_PERCENT.index);

    }

    protected final XSSFCell addCell(XSSFRow row, int pos, Long value) {
        XSSFCell cell1 = row.createCell(pos);
        cell1.setCellStyle(row.getSheet().getWorkbook().getCellStyleAt(CellStylesEnum.NUMBER.index));
        cell1.setCellValue(value == null ? 0L : value.doubleValue());
        return cell1;
    }

    protected final XSSFCell addCell(XSSFRow row, int pos, BigDecimal value) {
        XSSFCell cell1 = row.createCell(pos);
        cell1.setCellStyle(row.getSheet().getWorkbook().getCellStyleAt(CellStylesEnum.DECIMAL.index));
        cell1.setCellValue(value == null ? BigDecimal.ZERO.doubleValue() : value.doubleValue());
        return cell1;
    }

    protected final XSSFCell addCell(XSSFRow row, int pos, BigDecimal value, CellStylesEnum style) {
        XSSFCell cell1 = row.createCell(pos);
        cell1.setCellStyle(row.getSheet().getWorkbook().getCellStyleAt(style.index));
        if (value != null) {
            cell1.setCellValue(value == null ? BigDecimal.ZERO.doubleValue() : value.doubleValue());
        }
        return cell1;
    }

    protected final XSSFCell addCell(XSSFRow row, int pos, String value, CellStylesEnum style, int width) {
        XSSFCell cell1 = row.createCell(pos);
        cell1.setCellStyle(row.getSheet().getWorkbook().getCellStyleAt(style.index));
        cell1.setCellValue(value);
        setColumnWidth(row.getSheet(), pos, width);
        return cell1;
    }

    protected final XSSFCell addCell(XSSFRow row, int pos, String value, CellStylesEnum style) {
        XSSFCell cell1 = row.createCell(pos);
        cell1.setCellStyle(row.getSheet().getWorkbook().getCellStyleAt(style.index));
        cell1.setCellValue(value);
        cell1.getCellStyle().setWrapText(false);
        return cell1;
    }

    protected final XSSFCell addCell(XSSFRow row, int pos) {
        return addCell(row, pos, (String) null, CellStylesEnum.TEXT);
    }

    protected final XSSFCell addCell(XSSFRow row, int pos, String value) {
        return addCell(row, pos, value, CellStylesEnum.TEXT);
    }

    protected final XSSFCell addCell(XSSFRow row, int pos, Date value) {
        return addCell(row, pos, value, CellStylesEnum.DATE);
    }

    protected final XSSFCell addCell(XSSFRow row, int pos, Date value, CellStylesEnum style) {
        XSSFCell cell1 = row.createCell(pos);
        cell1.setCellStyle(row.getSheet().getWorkbook().getCellStyleAt(style.index));
        cell1.setCellValue(value);
        return cell1;
    }

    protected final void evaluateFormula(XSSFSheet sheet, XSSFRow row, int pos, String formula) {
        evaluateFormula(sheet, row, pos, formula, null);
    }

    protected final XSSFCell evaluateFormula(XSSFSheet sheet, XSSFRow row, int pos, String formula,
            CellStylesEnum style) {

        XSSFCell c0 = row.createCell(pos);

        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        c0.setCellFormula(formula);
        evaluator.evaluateFormulaCell(c0);
        
        if (style != null) {
            XSSFCellStyle cellStyle = row.getSheet().getWorkbook().getCellStyleAt(style.index);
            if(cellStyle != null) {
                c0.setCellStyle(row.getSheet().getWorkbook().getCellStyleAt(style.index));
            }
        }

        return c0;

    }

    protected final XSSFCell evaluateFormula(XSSFCell cell, String formula, CellStylesEnum style) {

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

    protected final void carregarFontes(XSSFWorkbook workBook) {
        if (workBook.getStylesSource().getFonts().size() == 1) {
            for (FontsEnum f : FontsEnum.values()) {
                XSSFFont normal = workBook.createFont();
                normal.setFontHeightInPoints(f.size);
                normal.setFontName(f.fontFamily);
                normal.setColor(f.color);
                normal.setBold(f.fontWeight.equals(FontWeightEnum.BOLD));
            }
        }
    }

    protected final void carregarEstilos(XSSFWorkbook wb) {

        if (wb.getStylesSource().getNumCellStyles() == 1) {

            CreationHelper createHelper = wb.getCreationHelper();

            for (CellStylesEnum s : CellStylesEnum.values()) {
                XSSFCellStyle style = wb.createCellStyle();
                setAlign(style, s.hAlignment);
                setFillForeground(style, s.bgColor);
                style.setFont(wb.getFontAt(s.font));

                if (s.format.equals(ValueFormatsEnum.DECIMAL)) {
                    style.setDataFormat(createHelper.createDataFormat()
                            .getFormat("###,###,###,###,##0.00;[Red]-###,###,###,###,##0.00"));
                }

                else if (s.format.equals(ValueFormatsEnum.DECIMAL5)) {
                    style.setDataFormat(createHelper.createDataFormat()
                            .getFormat("###,###,###,###,##0.00000;[Red]-###,###,###,###,##0.00000"));
                }

                else if (s.format.equals(ValueFormatsEnum.DATE_TIME)) {
                    style.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));
                }

                else if (s.format.equals(ValueFormatsEnum.DATE)) {
                    style.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
                }

                else if (s.format.equals(ValueFormatsEnum.PERCENT)) {
                    style.setDataFormat(createHelper.createDataFormat()
                            .getFormat("###,###,###,###,##0.00%;[Red]-###,###,###,###,##0.00%"));
                }

                style.setBorderBottom(BorderStyle.NONE);
                style.setBorderTop(BorderStyle.NONE);
                style.setBorderRight(BorderStyle.NONE);
                style.setBorderLeft(BorderStyle.NONE);

                if (s.hasBorders) {
                    addBorders(style);
                }
            }
        }

    }

    protected XSSFCell setCellFormula(XSSFCell cell, String formula) {
        formula = formula.replaceAll("\\$1", String.valueOf(cell.getRow().getRowNum() + 1));
        return evaluateFormula(cell, formula, null);
    }

    protected XSSFCell setCellFormula(XSSFCell cell, String formula, CellStylesEnum style) {
        formula = formula.replaceAll("\\$1", String.valueOf(cell.getRow().getRowNum() + 1));
        return evaluateFormula(cell, formula, style);
    }

    protected XSSFCell setCellFormula(XSSFSheet sheet, XSSFRow row, String formula, int pos, CellStylesEnum style) {
        formula = formula.replaceAll("\\$1", String.valueOf(row.getRowNum() + 1));
        return evaluateFormula(sheet, row, pos, formula, style);
    }

    protected XSSFCell setCellFormula(XSSFSheet sheet, XSSFRow row, String formula,
            List<XSSFCell> listaValoresPrevistos, int pos) {

        if (Utils.isEmpty(listaValoresPrevistos)) {
            return row.createCell(pos);
        }

        formula = String.format(formula, //
                listaValoresPrevistos.get(0).getReference(), //
                listaValoresPrevistos.get(listaValoresPrevistos.size() - 1).getReference());

        return setCellFormula(sheet, row, formula, pos, null);
    }

    protected XSSFCell setCellFormula(XSSFSheet sheet, XSSFRow row, String formula, int pos) {
        return setCellFormula(sheet, row, formula, pos, null);
    }

    protected void setCellFormula(XSSFSheet sheet, XSSFRow row, String formula, int pos, int posInicio, int posFinal,
            CellStylesEnum style) {
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

    protected String getMediaIndicador(Map<Long, List<XSSFCell>> indicadores, Long nuFatura,
            IndicadorContratoDTO indicadorFNS) {

        if (indicadorFNS == null) {
            return null;
        }

        List<Long> i = new ArrayList<>();

        for (Long key : indicadores.keySet()) {
            i.add(0, key);
            if (key.equals(nuFatura)) {
                break;
            }
        }

        List<Long> cells = new ArrayList<>();

        if (i.size() > (indicadorFNS.getQtFaturamento() - 1)) {
            cells.addAll(i.subList(0, indicadorFNS.getQtFaturamento()));
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

    protected void setNoNull(XSSFRow row, int pos, Number value) {
        if (value != null) {
            setNoNull(row, pos, BigDecimal.valueOf(value.doubleValue()), null);
        } else {
            setNoNull(row, pos, (BigDecimal) null, null);
        }
    }

    protected void setNoNull(XSSFRow row, int pos, BigDecimal value) {
        setNoNull(row, pos, value, null);
    }

    protected void setNoNull(XSSFRow row, int pos, BigDecimal value, CellStylesEnum style) {
        if (value == null) {
            addCell(row, pos, "-", CellStylesEnum.NORMAL_ALIGN_CENTER);
        } else {
            if (style != null) {
                addCell(row, pos, value, style);
            } else {
                addCell(row, pos, value);
            }
        }
    }

    protected static void printVersion() {
        StringBuilder sb = new StringBuilder();

        sb.append(" ____                            _ \n");
        sb.append("|  _ \\ _ __ ___  _ __ ___   ___| |_ ___ _   _ ___ \n");
        sb.append("| |_) | '__/ _ \\| '_ ` _ \\ / _ \\ __/ _ \\ | | / __| \n");
        sb.append("|  __/| | | (_) | | | | | |  __/ ||  __/ |_| \\__ \\ \n");
        sb.append("|_|   |_|  \\___/|_| |_| |_|\\___|\\__\\___|\\__,_|___/ \n");
        System.out.println(sb.toString());
        System.out.println(AppInfo.getVersion());
        System.out.println("Da mitologia grega: um semideus que roubou o fogo do Olimpo ");
        System.out.println("para entregar aos homens e liberta-los do medo do desconhecido.");
        System.out.println("");
    }

}
