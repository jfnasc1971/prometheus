package org.prometheus.relatorios.equipe;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.CalculoValorDevidoDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioEquipe;
import org.prometheus.service.CalculoValorDevidoService;
import org.prometheus.utils.XSSFUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaCalculoValorDevidoPerfil extends Relatorio {

    private static final int LAYOUT_COLUMNS_COUNT = 14;
    
    @Autowired
    private CalculoValorDevidoService service;

    public XSSFCell write(XSSFWorkbook workBook, String coEquipe, List<FaturamentoDTO> faturamentos,
            Map<Long, InsumosFaturamentoDTO> insumos) throws InvalidFormatException, IOException {

        carregarFontes(workBook);

        carregarEstilos(workBook);

        XSSFSheet sheet = workBook.createSheet(RelatorioEquipe.ABA_CALCULO_VALOR_DEVIDO);

        sheet.setDisplayGridlines(false);

        setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);

        setColumnWidth(sheet, 1, 120);

        XSSFRow row = addNewRow(sheet);

        int pos = 0;

        addCell(row, pos++, "Faturamento", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD, 80);
        addCell(row, pos++, "Perfil", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "Dias Fat.", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD, 50);
        addCell(row, pos++, "Preço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "Qtd. Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "Valor Perfil", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "Qtd. Dias Extras", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "APEC", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "Qtd. Dias Ausências", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "DPEC", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "Vr. Previsto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "FNS", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD, 30);
        addCell(row, pos++, "Vr. Financeiro", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

        XSSFCell primeiraFatura = null;
        XSSFCell ultimaFatura = null;
        XSSFCell cellQtDiasFaturamento = null;
        XSSFCell cellVrPrecoPerfil = null;
        XSSFCell cellQtEfetivaPerfil = null;
        XSSFCell cellQtDiasExtraPerfil = null;
        XSSFCell cellQtDiasAusenciaPerfil = null;
        
        XSSFCell cellVrPrevistoPerfil = null;
        XSSFCell cellVrDiasExtraPerfil = null;
        XSSFCell cellVrDiasAusenciaPerfil = null;

        for (FaturamentoDTO faturamento : faturamentos) {

            InsumosFaturamentoDTO insumo = insumos.get(faturamento.getNuFatura());

            if (insumo != null) {
                
                List<CalculoValorDevidoDTO> valores = service.listar(insumo.getNuFatura(), insumo.getNuEquipe());

                for (CalculoValorDevidoDTO vigencia : valores) {
                    row = addNewRow(sheet);

                    pos = 0;

                    addCell(row, pos++, faturamento.getNuFatura() + " - " + faturamento.getDeFatura());
                    addCell(row, pos++, vigencia.getNuPerfil() + " - " + vigencia.getDePerfil());
                    
                    cellQtDiasFaturamento = addCell(row, pos++, faturamento.getQtDiasFaturamento());

                    cellVrPrecoPerfil = addCell(row, pos++, vigencia.getVrPrecoGrupo(), CellStylesEnum.DECIMAL);

                    cellQtEfetivaPerfil = addCell(row, pos++, vigencia.getQtMembrosEfetiva(), CellStylesEnum.DECIMAL);

                    cellVrPrevistoPerfil = setCellFormula(sheet, row, //
                            String.format("%s*%s", //
                                    cellVrPrecoPerfil.getReference(), //
                                    cellQtEfetivaPerfil.getReference()), //
                            pos++, //
                            CellStylesEnum.DECIMAL);

                    // EXTRAS
                    cellQtDiasExtraPerfil = addCell(row, pos++, vigencia.getQtDiasExtra(), CellStylesEnum.DECIMAL);

                    cellVrDiasExtraPerfil = setCellFormula(sheet, row, //
                            String.format("(%s/%s)*%s", //
                                    cellQtDiasExtraPerfil.getReference(), //
                                    cellQtDiasFaturamento.getReference(), //
                                    cellVrPrecoPerfil.getReference()), //
                            pos++, //
                            CellStylesEnum.DECIMAL);

                    // AUSENCIAS
                    cellQtDiasAusenciaPerfil = addCell(row, pos++, vigencia.getQtDiasAusencia(), CellStylesEnum.DECIMAL);

                    cellVrDiasAusenciaPerfil = setCellFormula(sheet, row, //
                            String.format("(%s/%s)*%s", //
                                    cellQtDiasAusenciaPerfil.getReference(), //
                                    cellQtDiasFaturamento.getReference(), //
                                    cellVrPrecoPerfil.getReference()), //
                            pos++, //
                            CellStylesEnum.DECIMAL);
                    
                    // VALOR PREVISTO
                    setCellFormula(sheet, row, //
                            String.format("%s+%s-%s", //
                                    cellVrPrevistoPerfil.getReference(), //
                                    cellVrDiasExtraPerfil.getReference(), //
                                    cellVrDiasAusenciaPerfil.getReference()), //
                            pos++, //
                            CellStylesEnum.DECIMAL);
                    
                    // FNS
                    if (!XSSFUtils.isNumericValue(insumo.getVrFns())) {
                        addCell(row, pos++, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
                    } else {
                    	addCell(row, pos++, BigDecimal.valueOf(insumo.getVrFns().getNumericCellValue()), CellStylesEnum.DECIMAL);
                    }

                    // J20*K20
                    if (primeiraFatura == null) {
                        primeiraFatura = setCellFormula(sheet, row,
                                "IF(L%s=\"N/A\",K%s,L%s*K%s)".replaceAll("%s", String.valueOf(row.getRowNum() + 1)), //
                                pos++, CellStylesEnum.DECIMAL);

                    } else {
                        ultimaFatura = setCellFormula(sheet, row,
                                "IF(L%s=\"N/A\",K%s,L%s*K%s)".replaceAll("%s", String.valueOf(row.getRowNum() + 1)), //
                                pos++, CellStylesEnum.DECIMAL);

                    }                    
                    
                }

            }

        }

        if (ultimaFatura == null) {
            ultimaFatura = primeiraFatura;
        }

        row = addNewRow(sheet);
        addCell(row, pos - 2, "TOTAL", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

        XSSFCell result = null;

        if (primeiraFatura != null) {
            result = setCellFormula(sheet, row,
                    String.format("sum(%s:%s)", primeiraFatura.getReference(), ultimaFatura.getReference()), //
                    pos - 1, CellStylesEnum.DECIMAL);
        } else {
            result = addCell(row, pos - 1, "N/A");
        }

        return result;

    }

}
