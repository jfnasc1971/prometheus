package org.prometheus.tests;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.enums.RGBColorsEnum;

public class ExcelCustomColorExample {
    public static void main(String[] args) throws IOException {
        // Cria uma nova pasta de trabalho (workbook) no formato XSSF (.xlsx)
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Cores Personalizadas");

        // Cria uma célula na linha 0 e coluna 0
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Célula com cor personalizada");

        // Cria um estilo de célula
        CellStyle cellStyle = workbook.createCellStyle();

        // Define a cor de preenchimento personalizada (RGB: vermelho = 255, verde = 0, azul = 0)
        XSSFColor customColor = RGBColorsEnum.BLUE_VIOLET.color();// new XSSFColor(rgb, null);

        // Aplica a cor personalizada ao estilo da célula
        ((XSSFCellStyle) cellStyle).setFillForegroundColor(customColor);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Aplica o estilo à célula
        cell.setCellStyle(cellStyle);

        // Ajusta o tamanho da coluna para melhor visualização
        sheet.autoSizeColumn(0);

        // Salva o arquivo Excel
        try (FileOutputStream fileOut = new FileOutputStream("CustomColorExample.xlsx")) {
            workbook.write(fileOut);
        }

        // Fecha a pasta de trabalho
        workbook.close();

        System.out.println("Arquivo Excel criado com sucesso!");
    }
}