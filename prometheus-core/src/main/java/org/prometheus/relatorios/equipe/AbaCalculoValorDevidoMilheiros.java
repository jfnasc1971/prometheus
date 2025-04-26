package org.prometheus.relatorios.equipe;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.CalculoValorDevidoMilheirosDTO;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioEquipe;
import org.prometheus.service.CalculoValorMilheirosService;
import org.prometheus.service.EquipeService;
import org.prometheus.utils.XSSFUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaCalculoValorDevidoMilheiros extends Relatorio {

    private static final int LAYOUT_COLUMNS_COUNT = 14;

    @Autowired
    private EquipeService equipeService;

    @Autowired
    private CalculoValorMilheirosService service;

    public XSSFCell write(XSSFWorkbook workBook, String coEquipe, List<FaturamentoDTO> faturamentos,
            Map<Long, InsumosFaturamentoDTO> insumos) throws InvalidFormatException, IOException {

        EquipeDTO equipe = equipeService.findBy(coEquipe);

        carregarFontes(workBook);

        carregarEstilos(workBook);

        XSSFSheet sheet = workBook.createSheet(RelatorioEquipe.ABA_CALCULO_VALOR_DEVIDO);

        sheet.setDisplayGridlines(false);

        setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);

        XSSFRow row = addNewRow(sheet);

        int pos = 0;

        addCell(row, pos++, "Faturamento", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD, 50);
        addCell(row, pos++, "Tipo Preço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "Preço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "Quantidade", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "Valor Previsto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "% Desconto Proporcional", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "Valor desconto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "FNS", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, pos++, "Valor Financeiro", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

        XSSFCell primeiraFatura = null;
        XSSFCell ultimaFatura = null;

        for (FaturamentoDTO faturamento : faturamentos) {

            InsumosFaturamentoDTO insumo = insumos.get(faturamento.getNuFatura());

            List<CalculoValorDevidoMilheirosDTO> valores = service.listarValoresDevidos(faturamento.getNuFatura(),
                    equipe.getNuEquipe());

            for (CalculoValorDevidoMilheirosDTO dto : valores) {
                row = addNewRow(sheet);

                pos = 0;

                addCell(row, pos++, dto.getNuFatura() + " - " + dto.getDeFatura());
                addCell(row, pos++, dto.getNoTipoPreco());
                addCell(row, pos++, dto.getVrPrecoGrupo(), CellStylesEnum.DECIMAL);
                addCell(row, pos++, dto.getQtSolicitacao(), CellStylesEnum.DECIMAL);

                XSSFCell vrPrevisto = addCell(row, pos++, dto.getVrPrevisto(), CellStylesEnum.DECIMAL);

                setCellFormula(addCell(row, pos++, BigDecimal.ZERO, CellStylesEnum.PERCENT), //
                        String.format("%s", XSSFUtils.getFullReference(insumo.getPcDescontoProporcional())));

                XSSFCell vrDescontoProporcional = setCellFormula(addCell(row, pos++, BigDecimal.ZERO, CellStylesEnum.DECIMAL), //
                        String.format("%s", XSSFUtils.getFullReference(insumo.getVrDescontoProporcional())));

                XSSFCell vrFns = setCellFormula(addCell(row, pos++, BigDecimal.ZERO, CellStylesEnum.DECIMAL), //
                        String.format("%s", XSSFUtils.getFullReference(insumo.getVrFns())));

                if (ultimaFatura == null) {
                    primeiraFatura = setCellFormula(addCell(row, pos++, BigDecimal.ZERO, CellStylesEnum.DECIMAL), //
                            String.format("(%s-%s)*%s", //
                                    vrPrevisto.getReference(), //
                                    vrDescontoProporcional.getReference(), //
                                    vrFns.getReference()));
                    ultimaFatura = primeiraFatura;
                } else {

                    ultimaFatura = setCellFormula(addCell(row, pos++, BigDecimal.ZERO, CellStylesEnum.DECIMAL), //
                            String.format("(%s-%s)*%s", //
                                    vrPrevisto.getReference(), //
                                    vrDescontoProporcional.getReference(), //
                                    vrFns.getReference()));
                }
            }

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
