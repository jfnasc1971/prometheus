package org.prometheus.relatorios.equipe;

import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.AusenciaExtraFaturamentoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.service.AusenciaExtraFaturamentoService;
import org.prometheus.utils.XSSFSheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaServicosExtraAusencias extends Relatorio {

    private static final int LAYOUT_COLUMNS_COUNT = 4;

    @Autowired
    private AusenciaExtraFaturamentoService ausenciaExtraService;

    public void write(XSSFWorkbook workBook, String coEquipe) throws InvalidFormatException, IOException {

        carregarFontes(workBook);

        carregarEstilos(workBook);

        XSSFSheet sheet = workBook.createSheet("Extras e Ausências");

        sheet.setDisplayGridlines(false);
        setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);

        XSSFRow row = addNewRow(sheet);

        XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, //
                "Serviços extra e ausências", //
                CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

        List<AusenciaExtraFaturamentoDTO> lista = ausenciaExtraService.listar(coEquipe);

        int pos = 0;
        row = addNewRow(sheet);
        addCell(row, pos++, "Fatura", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "Sprint", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "Qt. Dias Extra", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "Qt. Dias Ausência", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

        for (AusenciaExtraFaturamentoDTO a : lista) {

            pos = 0;

            row = addNewRow(sheet);
            addCell(row, pos++, a.getDeFatura());
            addCell(row, pos++, a.getNuCicloTrabalho() == null ? "" : a.getNuCicloTrabalho().toString(),
                    CellStylesEnum.NORMAL_ALIGN_CENTER);
            addCell(row, pos++, a.getQtDiaServicoExtra());
            addCell(row, pos++, a.getQtDiaAusencia());

        }

        // empty line
        addNewRow(sheet);
    }
}
