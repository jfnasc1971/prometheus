package org.prometheus.relatorios.equipe;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.EntregaDTO;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.IndicadorVinculadoDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.dto.VigenciaMembrosDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioEquipe;
import org.prometheus.service.EntregaService;
import org.prometheus.service.EquipeService;
import org.prometheus.service.FaixaIndicadorService;
import org.prometheus.service.IndicadorVinculadoService;
import org.prometheus.service.PrecosEquipeService;
import org.prometheus.service.VigenciaMembrosService;
import org.prometheus.utils.Utils;
import org.prometheus.utils.XSSFSheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaCalculosG4 extends Relatorio {

    private static final Logger LOG = LogManager.getLogger(AbaCalculosG4.class.getSimpleName());

    private static final int LAYOUT_COLUMNS_COUNT = 11;

    @Autowired
    private VigenciaMembrosService vigenciasMembrosService;

    @Autowired
    private EquipeService equipeService;

    @Autowired
    private FaixaIndicadorService faixaIndicadorService;

    @Autowired
    private PrecosEquipeService precoEquipeService;

    @Autowired
    private EntregaService entregaService;

    @Autowired
    private IndicadorVinculadoService indicadorVinculadoService;

    Map<Long, List<XSSFCell>> listaIEQQL = null;
    Map<Long, List<XSSFCell>> listaIEQQPZ = null;
    Map<Long, List<XSSFCell>> listaIEQQET = null;
    Map<Long, List<XSSFCell>> listaIEQDES = null;

    public Map<Long, InsumosFaturamentoDTO> write(XSSFWorkbook wb, String coEquipe, List<FaturamentoDTO> faturamentos)
            throws InvalidFormatException, IOException {

        Map<Long, InsumosFaturamentoDTO> result = new LinkedHashMap<Long, InsumosFaturamentoDTO>();

        EquipeDTO equipe = equipeService.findBy(coEquipe);

        XSSFSheet sheet = wb.createSheet(RelatorioEquipe.ABA_CALCULOS);

        carregarFontes(wb);
        carregarEstilos(wb);

        sheet.setZoom(90);
        sheet.setDisplayGridlines(false);

        setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 75);

        XSSFRow row = null;

        listaIEQQL = new LinkedHashMap<Long, List<XSSFCell>>();
        listaIEQQPZ = new LinkedHashMap<Long, List<XSSFCell>>();
        listaIEQQET = new LinkedHashMap<Long, List<XSSFCell>>();
        listaIEQDES = new LinkedHashMap<Long, List<XSSFCell>>();

        int qtFaixas = faixaIndicadorService.listar(equipe.getNuEquipe()).size();

        // faturamentos
        for (FaturamentoDTO faturamento : faturamentos) {

            LOG.info(String.format("Memoria de Calculo - Contrato: %s Equipe: %s Fatura: %s - %s", //
                    equipe.getNuContrato(), equipe.getNuEquipe() + " - " + equipe.getCoEquipe(),
                    faturamento.getNuFatura(), faturamento.getDeFatura()));

            InsumosFaturamentoDTO insumo = new InsumosFaturamentoDTO();

            List<VigenciaMembrosDTO> vigencias = vigenciasMembrosService.listar(faturamento.getNuFatura(),
                    equipe.getNuEquipe());

            if (Utils.isEmpty(vigencias)) {
                continue;
            }

            insumo.setNuFatura(faturamento.getNuFatura());
            insumo.setConsiderarFNS("S".equalsIgnoreCase(faturamento.getIcConsiderarFns()));

            BigDecimal vrPrecoGrupo = precoEquipeService.getVrPrecoGrupoVigente(equipe.getNuEquipe(),
                    faturamento.getNuFatura());
            insumo.setVrPreco(vrPrecoGrupo);

            row = addNewRow(sheet);
            XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1,
                    faturamento.getNuFatura() + " - " + faturamento.getDeFatura(), //
                    CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

            /**
             * Vigência dos Membros do Faturamento
             */
            addBlank(sheet);

            row = addNewRow(sheet);

            int posRowVigencias = row.getRowNum(); // preciso guardar essa posicao

            XSSFSheetUtils.addMergedRegion(sheet, row, 6, 10, "Vigência dos Membros do Faturamento", //
                    CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

            int pos = 6;

            row = addNewRow(sheet);
            addCell(row, pos++, "Dt. Início", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Dt. Final", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Qt. Membros", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Qt. Dias Trab.", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Qt. Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

            XSSFCell cellInicioVigencias = null;
            XSSFCell cellFinalVigencias = null;

            for (VigenciaMembrosDTO vigencia : vigencias) {
                pos = 6;
                row = addNewRow(sheet);
                addCell(row, pos++, vigencia.getDtInicioVigenciaMembros());
                addCell(row, pos++, vigencia.getDtFinalVigenciaMembros());
                addCell(row, pos++, vigencia.getQtMembrosEquipe());
                addCell(row, pos++, vigencia.getQtDiasTrabalhados(), CellStylesEnum.DECIMAL);

                if (cellInicioVigencias == null) {
                    cellInicioVigencias = addCell(row, pos++, vigencia.getQtEfetiva(),
                            CellStylesEnum.DECIMAL5);
                } else {
                    cellFinalVigencias = addCell(row, pos++, vigencia.getQtEfetiva(),
                            CellStylesEnum.DECIMAL5);
                }
            }

            if (cellFinalVigencias == null) {
                cellFinalVigencias = cellInicioVigencias;
            }

            row = addNewRow(sheet);
            insumo.setQtEfetiva(setCellFormula(sheet, row, String.format("sum(%s:%s)", //
                    cellInicioVigencias.getReference(), cellFinalVigencias.getReference()), pos - 1,
                    CellStylesEnum.DECIMAL5));

            /**
             * Dias Trabalhados no Faturamento
             */
            row = getRow(sheet, posRowVigencias++);
            XSSFSheetUtils.addMergedRegion(sheet, row, 0, 4, "Dias Trabalhados no Faturamento", //
                    CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

            row = getRow(sheet, posRowVigencias++);
            addCell(row, 0, "Dt. Início", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 1, "Dt. Final", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 2, "Qt. Dias", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 3, "Qt. Dias Extra", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 4, "Qt. Dias Ausência", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

            row = getRow(sheet, posRowVigencias++);
            addCell(row, 0, faturamento.getDtInicioFatura());
            addCell(row, 1, faturamento.getDtFinalFatura());
            addCell(row, 2, faturamento.getQtDiasFaturamento(), CellStylesEnum.DECIMAL);
            insumo.setQtDiasExtra(addCell(row, 3, faturamento.getQtDiasServicoExtra(), CellStylesEnum.DECIMAL));
            insumo.setQtDiasAusencia(addCell(row, 4, faturamento.getQtDiasAusencia(), CellStylesEnum.DECIMAL));

            /**
             * Serviços Contratados
             */
            List<EntregaDTO> servicosContratados = entregaService.listarServicosContratadosIEQQET(equipe.getNuEquipe(),
                    faturamento.getNuFatura(), 19L);

            addBlank(sheet);
            row = addNewRow(sheet);
            XSSFCell cellTituloServicosContratados = XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, "Serviços Contratados", //
                    CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

            row = addNewRow(sheet);
            addCell(row, 0, "Solicitação", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
            addCell(row, 1, "Data Prevista", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
            addCell(row, 2, "Data Efetiva", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

            if (Utils.isEmpty(servicosContratados)) {
                row = addNewRow(sheet);
                XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, "Nenhuma entrega registrada.");
            }

            for (EntregaDTO e : servicosContratados) {
                row = addNewRow(sheet);
                addCell(row, 0, e.getCoSolicitacao());
                addCell(row, 1, e.getDhPrevistaPendencias());
                addCell(row, 2, e.getDhEfetiva());
            }

            row = addNewRow(sheet);
            addCell(row, 1, "Quantidade", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
            XSSFCell cellQtServicosContratados = addCell(row, 2, BigDecimal.valueOf(servicosContratados.size()));

            /**
             * Serviços Entregues
             */
            List<EntregaDTO> servicosEntregues = entregaService.listarServicosEntregues(equipe.getNuEquipe(),
                    faturamento.getNuFatura());

            addBlank(sheet);
            row = addNewRow(sheet);
            XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, "Serviços Entregues", //
                    CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);

            row = addNewRow(sheet);
            addCell(row, 0, "Solicitação", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
            addCell(row, 1, "Data Prevista", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
            addCell(row, 2, "Data Efetiva", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

            if (Utils.isEmpty(servicosEntregues)) {
                row = addNewRow(sheet);
                XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, "Nenhuma entrega registrada.");
            }

            for (EntregaDTO e : servicosEntregues) {
                row = addNewRow(sheet);
                addCell(row, 0, e.getCoSolicitacao());
                addCell(row, 1, e.getDhPrevistaPendencias());
                addCell(row, 2, e.getDhEfetiva());
            }

            row = addNewRow(sheet);
            addCell(row, 1, "Quantidade", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
            XSSFCell cellQtServicosEntregues = addCell(row, 2, BigDecimal.valueOf(servicosEntregues.size()));

            /**
             * Serviços Entregues/Estoque
             */
            List<EntregaDTO> servicosEntreguesEstoque = entregaService.listarServicosEstoque(equipe.getNuEquipe(),
                    faturamento.getNuFatura());

            addBlank(sheet);
            row = addNewRow(sheet);
            XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, "Serviços Entregues/Estoque", //
                    CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);

            row = addNewRow(sheet);
            addCell(row, 0, "Solicitação", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
            addCell(row, 1, "Data Prevista", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
            addCell(row, 2, "Data Efetiva", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

            if (Utils.isEmpty(servicosEntreguesEstoque)) {
                row = addNewRow(sheet);
                XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, "Nenhuma entrega registrada.");
            }

            for (EntregaDTO e : servicosEntreguesEstoque) {
                row = addNewRow(sheet);
                addCell(row, 0, e.getCoSolicitacao());
                addCell(row, 1, e.getDhPrevistaPendencias());
                addCell(row, 2, e.getDhEfetiva());
            }

            row = addNewRow(sheet);
            addCell(row, 1, "Quantidade", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
            XSSFCell cellQtServicosEntreguesEstoque = addCell(row, 2,
                    BigDecimal.valueOf(servicosEntreguesEstoque.size()));

            /**
             * Serviços Entregues com Atraso
             */
            List<EntregaDTO> servicosEntreguesComAtraso = entregaService
                    .listarServicosEntreguesComAtraso(equipe.getNuEquipe(), faturamento.getNuFatura());

            addBlank(sheet);
            row = addNewRow(sheet);
            XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, "Serviços Entregues com Atraso", //
                    CellStylesEnum.ORANGE_ALIGN_CENTER_BOLD);

            row = addNewRow(sheet);
            addCell(row, 0, "Solicitação", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
            addCell(row, 1, "Data Prevista", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
            addCell(row, 2, "Data Efetiva", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

            if (Utils.isEmpty(servicosEntreguesComAtraso)) {
                row = addNewRow(sheet);
                XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, "Nenhuma entrega registrada.");
            }

            for (EntregaDTO e : servicosEntreguesComAtraso) {
                row = addNewRow(sheet);
                addCell(row, 0, e.getCoSolicitacao());
                addCell(row, 1, e.getDhPrevistaPendencias());
                addCell(row, 2, e.getDhEfetiva());
            }

            row = addNewRow(sheet);
            addCell(row, 1, "Quantidade", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
            XSSFCell cellQtServicosEntreguesComAtraso = addCell(row, 2,
                    BigDecimal.valueOf(servicosEntreguesComAtraso.size()));

            /**
             * Serviços Devolvidos
             */
            List<EntregaDTO> servicosDevolvidos = entregaService.listarServicosDevolvidos(equipe.getNuEquipe(),
                    faturamento.getNuFatura());

            addBlank(sheet);
            row = addNewRow(sheet);
            XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, "Serviços Devolvidos", //
                    CellStylesEnum.RED_ALIGN_CENTER_BOLD);

            row = addNewRow(sheet);
            addCell(row, 0, "Solicitação", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
            addCell(row, 1, "Data Prevista", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
            addCell(row, 2, "Data Efetiva", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

            if (Utils.isEmpty(servicosDevolvidos)) {
                row = addNewRow(sheet);
                XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, "Nenhuma entrega registrada.");
            }

            for (EntregaDTO e : servicosDevolvidos) {
                row = addNewRow(sheet);
                addCell(row, 0, e.getCoSolicitacao());
                addCell(row, 1, e.getDhPrevistaPendencias());
                addCell(row, 2, e.getDhEfetiva());
            }

            row = addNewRow(sheet);
            addCell(row, 1, "Quantidade", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
            XSSFCell cellQtServicosDevolvidos = addCell(row, 2, BigDecimal.valueOf(servicosDevolvidos.size()));

            addBlank(sheet);

            //
            // Indicadores vinculados
            //
            if ("S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {

                List<IndicadorVinculadoDTO> indicadoresVinculados = indicadorVinculadoService
                        .listar(equipe.getNuContrato(), equipe.getNuTipoGrupo(), faturamento.getDtFinalFatura(), 20L);

                pos = cellTituloServicosContratados.getRowIndex();

                row = getRow(sheet, pos++);
                XSSFSheetUtils.addMergedRegion(sheet, row, 4, 10, "Equipes/Indicadores vinculados",
                        CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

                row = getRow(sheet, pos++);
                addCell(row, 4, "Equipe", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                XSSFSheetUtils.addMergedRegion(sheet, row, 5, 6, "Grupo", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                XSSFSheetUtils.addMergedRegion(sheet, row, 7, 8, "Indicador", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                addCell(row, 9, "Dt. Geração", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                addCell(row, 10, "Valor Calculado", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

                if (Utils.isEmpty(indicadoresVinculados)) {
                    row = getRow(sheet, pos++);
                    XSSFSheetUtils.addMergedRegion(sheet, row, 4, 10, "Nenhum registro encontrado.");
                }

                XSSFCell primeiroVinculado = null;
                XSSFCell ultimoVinculado = null;

                for (IndicadorVinculadoDTO i : indicadoresVinculados) {

                    row = getRow(sheet, pos++);
                    addCell(row, 4, i.getCoEquipe());
                    XSSFSheetUtils.addMergedRegion(sheet, row, 5, 6, i.getNoTipoGrupo(), CellStylesEnum.NORMAL_ALIGN_CENTER);
                    XSSFSheetUtils.addMergedRegion(sheet, row, 7, 8, i.getSgTipoIndicador() + " - " + i.getNoTipoIndicador(),
                            CellStylesEnum.NORMAL_ALIGN_CENTER);
                    addCell(row, 9, i.getDhGeracaoIndicadorEquipe(), CellStylesEnum.DATE_TIME);

                    if (primeiroVinculado == null) {
                        primeiroVinculado = addCell(row, 10, i.getVrCalculoIndicador(),
                                CellStylesEnum.DECIMAL5);
                        ultimoVinculado = primeiroVinculado;
                    } else {
                        ultimoVinculado = addCell(row, 10, i.getVrCalculoIndicador(),
                                CellStylesEnum.DECIMAL5);
                    }

                }

                //
                // INDICADORES
                //
                getRow(sheet, pos++); // blank

                row = getRow(sheet, pos++);
                XSSFSheetUtils.addMergedRegion(sheet, row, 4, 6, "Indicadores", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

                /**
                 * IEQQL - Qualidade do Serviço
                 */
                row = getRow(sheet, pos++);
                XSSFSheetUtils.addMergedRegion(sheet, row, 4, 5, "IEQQL - Qualidade do Serviço", CellStylesEnum.NORMAL_ALIGN_LEFT);

                XSSFCell cellFormulaIEQQL = addCell(row, 6, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

                if ("NUMERIC".equals(cellQtServicosEntregues.getCellType().name())
                        && cellQtServicosEntregues.getNumericCellValue() != 0) {
                    setCellFormula(cellFormulaIEQQL, //
                            String.format("%s/(%s+%s)", //
                                    cellQtServicosEntregues.getReference(), //
                                    cellQtServicosEntregues.getReference(), cellQtServicosDevolvidos.getReference()), //
                            CellStylesEnum.DECIMAL5);
                }

                addIndicador(listaIEQQL, faturamento.getNuFatura(), cellFormulaIEQQL);

                /**
                 * IEQQPZ - Prazo do Serviço
                 */
                row = getRow(sheet, pos++);
                XSSFSheetUtils.addMergedRegion(sheet, row, 4, 5, "IEQQPZ - Prazo do Serviço", CellStylesEnum.NORMAL_ALIGN_LEFT);

                XSSFCell cellFormulaIEQQPZ = addCell(row, 6, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

                if ("NUMERIC".equals(cellQtServicosEntregues.getCellType().name())
                        && cellQtServicosEntregues.getNumericCellValue() != 0) {
                    setCellFormula(cellFormulaIEQQPZ, //
                            String.format("1-(%s/%s)", cellQtServicosEntreguesComAtraso.getReference(),
                                    cellQtServicosEntregues.getReference()), //
                            CellStylesEnum.DECIMAL5);
                }

                addIndicador(listaIEQQPZ, faturamento.getNuFatura(), cellFormulaIEQQPZ);

                /**
                 * IEQQET - Estoque do Serviço
                 */
                row = getRow(sheet, pos++);
                XSSFSheetUtils.addMergedRegion(sheet, row, 4, 5, "IEQQET - Estoque do Serviço", CellStylesEnum.NORMAL_ALIGN_LEFT);

                XSSFCell cellFormulaIEQQET = addCell(row, 6, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

                if ("NUMERIC".equals(cellQtServicosContratados.getCellType().name())
                        && cellQtServicosContratados.getNumericCellValue() != 0) {
                    setCellFormula(cellFormulaIEQQET, //
                            String.format("%s/%s", cellQtServicosEntreguesEstoque.getReference(),
                                    cellQtServicosContratados.getReference()), //
                            CellStylesEnum.DECIMAL5);
                }

                addIndicador(listaIEQQET, faturamento.getNuFatura(), cellFormulaIEQQET);

                /**
                 * IEQDES - Desenvolvimento
                 */
                row = getRow(sheet, pos++);
                XSSFSheetUtils.addMergedRegion(sheet, row, 4, 5, "IEQDES - Desenvolvimento", CellStylesEnum.NORMAL_ALIGN_LEFT);

                XSSFCell cellFormulaIEQDES = addCell(row, 6, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
                if (primeiroVinculado != null && ultimoVinculado != null) {
                    setCellFormula(cellFormulaIEQDES, //
                            String.format("average(%s:%s)", primeiroVinculado.getReference(),
                                    ultimoVinculado.getReference()), //
                            CellStylesEnum.DECIMAL5);
                }

                addIndicador(listaIEQDES, faturamento.getNuFatura(), cellFormulaIEQDES);

                //
                // Desempenho / Valor FNS
                //
                getRow(sheet, pos++); // blank

                row = getRow(sheet, pos++);
                addCell(row, 4, "Desempenho", CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);
                addCell(row, 5, "Valor FNS", CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);

                row = getRow(sheet, pos++);

                XSSFCell cellDesempenho = addCell(row, 4, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
                XSSFCell cellValorFNS = addCell(row, 5, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

                getRow(sheet, pos++); // blank

                setCellFormulaDesempenho(cellFormulaIEQDES, cellDesempenho, faturamento.getNuFatura());
                setCellFormulaValorFNS(cellValorFNS, cellDesempenho, qtFaixas);
                insumo.setVrFns(cellValorFNS);

            }

            result.put(faturamento.getNuFatura(), insumo);

        } // faturamentos

        return result;
    }

    private void setCellFormulaDesempenho(XSSFCell cellFormulaIEQDES, XSSFCell cell, Long nuFatura) {

        StringBuilder s1 = new StringBuilder();
        StringBuilder s2 = new StringBuilder();

        String ieqql = parseFormulaDesempenho(listaIEQQL, nuFatura, "Equipe!J3", 3);
        if (ieqql != null) {
            s1.append("+" + ieqql.toString());
            s2.append("," + "Equipe!J3");
        }

        String ieqqpz = parseFormulaDesempenho(listaIEQQPZ, nuFatura, "Equipe!J4", 3);
        if (ieqqpz != null) {
            s1.append("+" + ieqqpz.toString());
            s2.append("," + "Equipe!J4");
        }

        String ieqqet = parseFormulaDesempenho(listaIEQQET, nuFatura, "Equipe!J5", 3);
        if (ieqqet != null) {
            s1.append("+" + ieqqet.toString());
            s2.append("," + "Equipe!J5");
        }

        String ieqdes = parseFormulaDesempenho(listaIEQDES, nuFatura, "Equipe!J6", 1);
        if (ieqdes != null) {
            s1.append("+" + ieqdes.toString());
            s2.append("," + "Equipe!J6");
        }

        if (s1.length() > 1) {
            setCellFormula(cell, //
                    String.format("(%s)/sum(%s)", s1.toString().substring(1), s2.toString().substring(1)), //
                    CellStylesEnum.DECIMAL5);
        }
    }

    private String parseFormulaDesempenho(Map<Long, List<XSSFCell>> lista, Long nuFatura, String cellPesoIndicador,
            int qtFaturas) {

        StringBuilder p1 = new StringBuilder();

        List<XSSFCell> indicadores = null;

        indicadores = getIndicadoresMediaFNS(lista, nuFatura, qtFaturas);

        for (XSSFCell c : indicadores) {
            try {
                if (c != null && !"N/A".equals(c.toString())) {
                    p1.append("," + c.getReference());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (p1.length() > 1) {
            return String.format("(average(%s)*%s)", p1.toString().substring(1), cellPesoIndicador);
        }

        return null;
    }

    private void setCellFormulaValorFNS(XSSFCell cellValorFNS, XSSFCell cellDesempenho, int qtFaixas) {

        if (!"N/A".equalsIgnoreCase(cellDesempenho.toString()) && qtFaixas > 0) {

            // essa posição fixa é o número da primeira linha das faixas
            // da seção "Valores Faixas Indicador" na aba equipe
            int pos = 15;

            StringBuilder formula = new StringBuilder();

            for (int i = 1; i <= qtFaixas; i++) {

                formula.append(String.format("+if(and(trunc(%s,2)>=Equipe!H%s,trunc(%s,2)<=Equipe!I%s),Equipe!J%s)", //
                        cellDesempenho.getReference(), pos + i, //
                        cellDesempenho.getReference(), pos + i, //
                        pos + i));
            }

            setCellFormula(cellValorFNS, formula.toString().substring(1), CellStylesEnum.DECIMAL);

        }

    }

}
