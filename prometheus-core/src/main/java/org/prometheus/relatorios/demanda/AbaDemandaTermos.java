package org.prometheus.relatorios.demanda;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.DemandaDTO;
import org.prometheus.dto.TermoDemandaDTO;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.service.DemandaService;
import org.prometheus.service.TermoDemandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AbaDemandaTermos extends Relatorio {

    private static final int LAYOUT_COLUMNS_COUNT = 8;

    @Autowired
    private DemandaService demandaService;

    @Autowired
    private TermoDemandaService termoDemandaService;

    public XSSFCell write(XSSFWorkbook workBook, String coDemanda) throws Exception {

        XSSFCell result = null;

        DemandaDTO demanda = demandaService.findByDemanda(coDemanda);

        carregarFontes(workBook);
        carregarEstilos(workBook);

        XSSFSheet sheet = workBook.createSheet("Termos");

        sheet.setZoom(100);
        sheet.setDisplayGridlines(false);

        setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT + 1, 90);

        XSSFRow row = null;

        row = addNewRow(sheet);
        addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT, demanda.getCoDemanda(), ALIGN_CENTER_GREY_25_PERCENT_BOLD);

        row = addNewRow(sheet);
        // Termo Dt. Emissão Dt. Fatura Paga Fatura paga Dt. Fatura Gerada Fatura Gerada
        // Valor Financeiro
        addCell(row, 0, "Demanda", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
        addCell(row, 1, "OS", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
        addCell(row, 2, "Termo  ", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
        addCell(row, 3, "Dt. Emissão    ", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
        addCell(row, 4, "Dt. Fatura Paga    ", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
        addCell(row, 5, "Fatura Paga    ", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
        addCell(row, 6, "Dt. Fatura Gerada  ", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
        addCell(row, 7, "Fatura Gerada  ", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
        addCell(row, 8, "Valor Financeiro", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

        List<TermoDemandaDTO> termos = termoDemandaService.listar(demanda.getCoDemanda());

        int posPrimeiroTermo = row.getRowNum() + 2;
        int posUltimoTermo = posPrimeiroTermo;

        for (TermoDemandaDTO termo : termos) {
            row = addNewRow(sheet);
            addCell(row, 0, termo.getCoDemanda(), ALIGN_CENTER);
            addCell(row, 1, termo.getCoOrdemServico(), ALIGN_CENTER);
            addCell(row, 2, termo.getCoTermo(), ALIGN_CENTER);
            addCell(row, 3, termo.getDhEmissaoTermo(), STYLE_DATE_TIME);
            addCell(row, 4, termo.getDtFinalFaturaPaga());
            addCell(row, 5, termo.getDeFaturaPaga(), ALIGN_CENTER);
            addCell(row, 6, termo.getDtFinalFaturaGerada());
            addCell(row, 7, termo.getDeFaturaGerada(), ALIGN_CENTER);
            addCell(row, 8, termo.getVrFinanceiroTermo(), STYLE_DECIMAL);

            posUltimoTermo++;
        }

        row = addNewRow(sheet);
        addCell(row, 7, "TOTAL", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
        result = setCellFormula(sheet, row, //
                String.format("SUM(I%s:I%s)", posPrimeiroTermo, posUltimoTermo - 1), //
                8, STYLE_DECIMAL);

        return result;
    }
}
