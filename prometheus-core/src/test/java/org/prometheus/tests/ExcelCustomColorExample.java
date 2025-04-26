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

        int pos = 0;
        
        for (RGBColorsEnum color : RGBColorsEnum.values()) {
            System.out.println(color);

            // Cria uma célula na linha 0 e coluna 0
            Row row = sheet.createRow(pos++);
            // Cria um estilo de célula
            CellStyle cellStyle = workbook.createCellStyle();
            
            Cell cell = row.createCell(0);
            cell.setCellValue(color.toString());
            cell = row.createCell(1);
            
            // Define a cor de preenchimento personalizada (RGB: vermelho = 255, verde = 0, azul = 0)
            XSSFColor customColor = color.color();// new XSSFColor(rgb, null);

            // Aplica a cor personalizada ao estilo da célula
            ((XSSFCellStyle) cellStyle).setFillForegroundColor(customColor);
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Aplica o estilo à célula
            cell.setCellStyle(cellStyle);

        }
        

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