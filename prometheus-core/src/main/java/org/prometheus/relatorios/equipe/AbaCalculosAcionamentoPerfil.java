package org.prometheus.relatorios.equipe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.AusenciaExtraFaturamentoDTO;
import org.prometheus.dto.EntregaDTO;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.dto.PerfilDTO;
import org.prometheus.dto.VigenciaMembrosDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioEquipe;
import org.prometheus.service.AusenciaExtraFaturamentoService;
import org.prometheus.service.EntregaServiceQualidade;
import org.prometheus.service.EquipeService;
import org.prometheus.service.FaixaIndicadorService;
import org.prometheus.service.IndicadorService;
import org.prometheus.service.PerfilService;
import org.prometheus.service.PrecosEquipeService;
import org.prometheus.service.VigenciaMembrosService;
import org.prometheus.utils.DateUtil;
import org.prometheus.utils.Utils;
import org.prometheus.utils.XSSFSheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AbaCalculosAcionamentoPerfil extends Relatorio {

    private static final Logger LOG = LogManager.getLogger(AbaCalculosAcionamentoPerfil.class.getSimpleName());

    private static final int LAYOUT_COLUMNS_COUNT = 13;

    @Autowired
    private AusenciaExtraFaturamentoService ausenciaExtraService;

    @Autowired
    private PerfilService perfilService;

    @Autowired
    private VigenciaMembrosService vigenciasMembrosService;

    @Autowired
    private EquipeService equipeService;

    @Autowired
    private FaixaIndicadorService faixaIndicadorService;

    @Autowired
    private PrecosEquipeService precoEquipeService;

    @Autowired
    private EntregaServiceQualidade entregaService;

    @Autowired
    private IndicadorService indicadorService;
    
    Map<Long, List<XSSFCell>> listaIEQQL = null;
    Map<Long, List<XSSFCell>> listaIEQQPZ = null;
    Map<Long, List<XSSFCell>> listaIEQQET = null;

    Map<Long, List<XSSFCell>> listaMediaIEQQL = null;
    Map<Long, List<XSSFCell>> listaMediaIEQQPZ = null;
    Map<Long, List<XSSFCell>> listaMediaIEQQET = null;

    public void gerar(XSSFWorkbook wb, String coEquipe, List<FaturamentoDTO> faturamentos,
            Map<Long, InsumosFaturamentoDTO> insumos) throws Exception {

        EquipeDTO equipe = equipeService.findBy(coEquipe);
        
        IndicadorContratoDTO indicadorFNS = indicadorService.getIndicadorContratoFNS(coEquipe);

        XSSFSheet sheet = wb.createSheet(RelatorioEquipe.ABA_CALCULOS);

        carregarFontes(wb);
        carregarEstilos(wb);

        sheet.setZoom(90);
        sheet.setDisplayGridlines(false);

        setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);
        setColumnWidth(sheet, 0, 55); // Coluna E
        setColumnWidth(sheet, 1, 55); // Coluna E
        setColumnWidth(sheet, 2, 55); // Coluna E
        setColumnWidth(sheet, 3, 5); // Coluna D
        setColumnWidth(sheet, 4, 75); // Coluna E

        XSSFRow row = null;

        listaIEQQL = new LinkedHashMap<Long, List<XSSFCell>>();
        listaIEQQPZ = new LinkedHashMap<Long, List<XSSFCell>>();
        listaIEQQET = new LinkedHashMap<Long, List<XSSFCell>>();
        listaMediaIEQQL = new LinkedHashMap<Long, List<XSSFCell>>();
        listaMediaIEQQPZ = new LinkedHashMap<Long, List<XSSFCell>>();
        listaMediaIEQQET = new LinkedHashMap<Long, List<XSSFCell>>();

        int qtFaixas = faixaIndicadorService.listar(equipe.getNuEquipe()).size();

        // faturamentos
        for (FaturamentoDTO faturamento : faturamentos) {

            LOG.info(String.format("Contrato: %s Equipe: %s Fatura: %s - %s", //
                    equipe.getNuContrato(), equipe.getNuEquipe() + " - " + equipe.getCoEquipe(),
                    faturamento.getNuFatura(), faturamento.getDeFatura()));

            int pos = 0;

            XSSFCell cellQtDiasFaturamento = null;
            List<XSSFCell> listaValorPrevisto = new ArrayList<XSSFCell>();
            List<XSSFCell> listaQtEfetiva = new ArrayList<XSSFCell>();

            InsumosFaturamentoDTO insumo = insumos.get(faturamento.getNuFatura());

            if (insumo == null) {
                continue;
            }

            insumo.setConsiderarFNS("S".equalsIgnoreCase(faturamento.getIcConsiderarFns()));

            row = addNewRow(sheet);
            XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1,
                    faturamento.getNuFatura() + " - " + faturamento.getDeFatura(), //
                    CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

            /**
             * Vigência dos Membros do Faturamento
             */
            addBlank(sheet);

            row = addNewRow(sheet);

            int rowNumSecao1 = row.getRowNum(); // preciso guardar essa posicao
            int rowNumSecao2 = row.getRowNum(); // preciso guardar essa posicao

            /**
             * Dias Trabalhados no Faturamento
             */
            row = getRow(sheet, rowNumSecao1++);
            XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, "Dias Trabalhados", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

            row = getRow(sheet, rowNumSecao1++);
            addCell(row, 0, "Dt. Início", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 1, "Dt. Final", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, 2, "Qt. Dias", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

            row = getRow(sheet, rowNumSecao1++);
            addCell(row, 0, faturamento.getDtInicioFatura());
            addCell(row, 1, faturamento.getDtFinalFatura());
            cellQtDiasFaturamento = addCell(row, 2, faturamento.getQtDiasFaturamento(), CellStylesEnum.DECIMAL);

            /**
             * Ausências/Extras
             */
            row = getRow(sheet, rowNumSecao2++);
            XSSFSheetUtils.addMergedRegion(sheet, row, 4, 10, "Ausências/Extras", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

            row = getRow(sheet, rowNumSecao2++);
            pos = 4;
            //addCell(row, pos++, "Perfis", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos++, "Perfis", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Qt. Ausências", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Qt. Extras", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Preço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Valor Ausências", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Valor Extras", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

            List<PerfilDTO> perfis = perfilService.listar(equipe.getNuEquipe());

            XSSFCell cellVrExtras = null;
            XSSFCell cellVrAusencias = null;
            List<XSSFCell> listaVrAusencias = new ArrayList<XSSFCell>();
            List<XSSFCell> listaVrExtras = new ArrayList<XSSFCell>();

            for (PerfilDTO perfil : perfis) {

                AusenciaExtraFaturamentoDTO ausenciaExtra = ausenciaExtraService.findBy(equipe.getNuEquipe(), //
                        faturamento.getNuFatura(), perfil.getNuPerfil());

                row = getRow(sheet, rowNumSecao2++);
                pos = 4;

                XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos++, perfil.getDePerfil());

                XSSFCell cellvrPrecoGrupo = null;
                XSSFCell cellQtExtras = null;
                XSSFCell cellQtAusencias = null;

                if (ausenciaExtra == null) {
                    cellQtAusencias = addCell(row, pos++, BigDecimal.ZERO);
                    cellQtExtras = addCell(row, pos++, BigDecimal.ZERO);
                } else {
                    cellQtAusencias = addCell(row, pos++, ausenciaExtra.getQtDiaAusencia());
                    cellQtExtras = addCell(row, pos++, ausenciaExtra.getQtDiaServicoExtra());
                }

                BigDecimal vrPrecoGrupo = precoEquipeService.getVrPrecoGrupo(equipe.getNuEquipe(), //
                        faturamento.getNuFatura(), perfil.getNuPerfil());

                cellvrPrecoGrupo = addCell(row, pos++, vrPrecoGrupo == null ? BigDecimal.ZERO : vrPrecoGrupo);

                listaVrAusencias.add(setCellFormula(sheet, row, //
                        String.format("(%s/%s)*%s", cellQtAusencias, cellQtDiasFaturamento, cellvrPrecoGrupo), //
                        pos++, CellStylesEnum.DECIMAL));

                listaVrExtras.add(setCellFormula(sheet, row, //
                        String.format("(%s/%s)*%s", cellQtExtras, cellQtDiasFaturamento, cellvrPrecoGrupo), //
                        pos++, CellStylesEnum.DECIMAL));
            }

            // blank
            row = getRow(sheet, rowNumSecao2++);

            /**
             * Vigência dos Membros do Faturamento
             */
            row = getRow(sheet, rowNumSecao2++);
            XSSFSheetUtils.addMergedRegion(sheet, row, 4, 12, "Vigência dos Membros do Faturamento", //
                    CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

            List<VigenciaMembrosDTO> vigencias = vigenciasMembrosService.listar(faturamento.getNuFatura(),
                    equipe.getNuEquipe());

            if (Utils.isNotEmpty(vigencias)) {

                pos = 4;

                row = addNewRow(sheet);
                XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos++, "Perfil", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                addCell(row, pos++, "Dt. Início", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                addCell(row, pos++, "Dt. Final", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                addCell(row, pos++, "Qt. Membros", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                addCell(row, pos++, "Qt. Dias Trab.", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                addCell(row, pos++, "Qt. Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                addCell(row, pos++, "Preço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                addCell(row, pos++, "Valor Previsto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

                for (VigenciaMembrosDTO vigencia : vigencias) {

                    BigDecimal vrPrecoGrupo = precoEquipeService.getVrPrecoGrupo(equipe.getNuEquipe(),
                            faturamento.getNuFatura(), vigencia.getNuPerfil());

                    insumo.addVrPrecoPerfil(vigencia.getNuPerfil(), vigencia.getDtInicioVigenciaMembros(), vrPrecoGrupo);

                    pos = 4;
                    row = addNewRow(sheet);

                    XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos++, vigencia.getDePerfil());
                    addCell(row, pos++, vigencia.getDtInicioVigenciaMembros());
                    addCell(row, pos++, vigencia.getDtFinalVigenciaMembros());
                    addCell(row, pos++, vigencia.getQtMembrosEquipe());
                    addCell(row, pos++, vigencia.getQtDiasTrabalhados(), CellStylesEnum.DECIMAL);
                    listaQtEfetiva.add(addCell(row, pos++, vigencia.getQtEfetiva(), CellStylesEnum.DECIMAL));
                    addCell(row, pos++, vrPrecoGrupo, CellStylesEnum.DECIMAL);

                    listaValorPrevisto.add(setCellFormula(sheet, row, //
                            String.format("%s*%s", row.getCell(row.getLastCellNum() - 2).getReference(), //
                                    row.getCell(row.getLastCellNum() - 1).getReference()),
                            pos++, CellStylesEnum.DECIMAL));

                    insumo.addQtEfetivaPerfil(vigencia.getNuPerfil(), vigencia.getDtInicioVigenciaMembros(), vigencia.getQtEfetiva());

                }

                row = addNewRow(sheet);

                pos = 9;
                addCell(row, pos++, "Qtd. Membros", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                insumo.setQtEfetivaTotal(setCellFormula(sheet, row, //
                        String.format("sum(%s:%s)", //
                                listaQtEfetiva.get(0).getReference(),
                                listaQtEfetiva.get(listaValorPrevisto.size() - 1).getReference()), //
                        pos++, CellStylesEnum.DECIMAL));

                pos = 11;
                addCell(row, pos++, "Valor Equipe", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                insumo.setVrEquipe(setCellFormula(sheet, row, //
                        String.format("sum(%s:%s)", //
                                listaValorPrevisto.get(0).getReference(),
                                listaValorPrevisto.get(listaValorPrevisto.size() - 1).getReference()), //
                        pos++, CellStylesEnum.DECIMAL));

                if (Utils.isNotEmpty(listaVrExtras)) {
                    pos = 11;
                    row = addNewRow(sheet);
                    addCell(row, pos++, "Extras", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                    cellVrExtras = setCellFormula(sheet, row, //
                            "sum(" + listaVrExtras.get(0).getReference() + ":" + //
                                    listaVrExtras.get(listaVrExtras.size() - 1).getReference() + ")",
                            pos++, CellStylesEnum.DECIMAL);
                    insumo.setVrExtras(cellVrExtras);
                }

                if (Utils.isNotEmpty(listaVrAusencias)) {
                    pos = 11;
                    row = addNewRow(sheet);
                    addCell(row, pos++, "Ausências", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                    cellVrAusencias = setCellFormula(sheet, row, //
                            "sum(" + listaVrAusencias.get(0).getReference() + ":" + //
                                    listaVrAusencias.get(listaVrAusencias.size() - 1).getReference() + ")",
                            pos++, CellStylesEnum.DECIMAL);
                    insumo.setVrAusencias(cellVrAusencias);
                }

                pos = 11;
                row = addNewRow(sheet);
                addCell(row, pos++, "Valor Previsto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                insumo.setVrPrevistoTermo(setCellFormula(sheet, row, //
                        "sum(" + listaValorPrevisto.get(0).getReference() + ":" + //
                                listaValorPrevisto.get(listaValorPrevisto.size() - 1).getReference() + ") + " + //
                                cellVrExtras.getReference() + " - " + //
                                cellVrAusencias.getReference(),
                        pos++, CellStylesEnum.DECIMAL));

                int rownum = listaValorPrevisto.get(listaValorPrevisto.size() - 1).getRowIndex() + 6;

                /**
                 * Indicadores
                 */
                if (!"S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {

                    row = getRow(sheet, rownum++);
                    XSSFSheetUtils.addMergedRegion(sheet, row, 0, 7,
                            "Grupo configurado para aplicar Indicadores somente a partir de "
                                    + DateUtil.format(faturamento.getDtConsiderarFns()),
                            CellStylesEnum.NOTA_BOLD);
                }

                if ("S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {

                    row = getRow(sheet, rownum);

                    XSSFSheetUtils.addMergedRegion(sheet, row, 0, 1, "Serviços",  CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);

                    XSSFCell cellQtServicosEntregues = contarServicosEntregues(equipe, sheet, faturamento);
                    XSSFCell cellQtServicosDevolvidos = contarServicosDevolvidos(equipe, sheet, faturamento);
                    XSSFCell cellQtServicosEntreguesComAtraso = contarServicosAtrasados(equipe, sheet, faturamento);
                    XSSFCell cellQtServicosEntreguesEstoque = contarServicosEntreguesEstoque(equipe, sheet, faturamento);
                    XSSFCell cellQtServicosContratados = contarServicosContratados(equipe, sheet, faturamento);

                    row = getRow(sheet, rownum++);
                    XSSFSheetUtils.addMergedRegion(sheet, row, 4, 6, "Indicadores", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

                    /**
                     * ICQL - Qualidade do Serviço
                     */
                    row = getRow(sheet, rownum++);
                    XSSFSheetUtils.addMergedRegion(sheet, row, 4, 5, "ICQL - Qualidade", CellStylesEnum.NORMAL_ALIGN_LEFT);

                    XSSFCell cellFormulaIEQQL = addCell(row, 6, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

                    if ("NUMERIC".equals(cellQtServicosEntregues.getCellType().name())
                            && cellQtServicosEntregues.getNumericCellValue() != 0) {
                        setCellFormula(cellFormulaIEQQL, //
                                String.format("1-(%s/%s)", //
                                        cellQtServicosDevolvidos.getReference(), //
                                        cellQtServicosEntregues.getReference()), //
                                CellStylesEnum.DECIMAL5);
                    }

                    addIndicador(listaIEQQL, faturamento.getNuFatura(), cellFormulaIEQQL);

                    /**
                     * IEQQPZ - Prazo do Serviço
                     */
                    row = getRow(sheet, rownum++);
                    XSSFSheetUtils.addMergedRegion(sheet, row, 4, 5, "ICPZ - Prazo", CellStylesEnum.NORMAL_ALIGN_LEFT);

                    XSSFCell cellFormulaIEQQPZ = addCell(row, 6, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

                    if ("NUMERIC".equals(cellQtServicosEntregues.getCellType().name())
                            && cellQtServicosEntregues.getNumericCellValue() != 0) {
                        setCellFormula(cellFormulaIEQQPZ, //
                                String.format("1-(%s/%s)", //
                                		cellQtServicosEntreguesComAtraso.getReference(),
                                        cellQtServicosEntregues.getReference()), //
                                CellStylesEnum.DECIMAL5);
                    }

                    addIndicador(listaIEQQPZ, faturamento.getNuFatura(), cellFormulaIEQQPZ);

                    /**
                     * ICET - Estoque
                     */
                    row = getRow(sheet, rownum++);
                    XSSFSheetUtils.addMergedRegion(sheet, row, 4, 5, "ICET - Estoque", CellStylesEnum.NORMAL_ALIGN_LEFT);
                    
                    XSSFCell cellFormulaIEQQET = addCell(row, 6, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

                    if ("NUMERIC".equals(cellQtServicosContratados.getCellType().name())
                            && cellQtServicosContratados.getNumericCellValue() != 0) {
                        setCellFormula(cellFormulaIEQQET, //
                                String.format("%s/%s", //
                                		cellQtServicosEntreguesEstoque.getReference(),
                                        cellQtServicosContratados.getReference()), //
                                CellStylesEnum.DECIMAL5);
                    }

                    addIndicador(listaIEQQET, faturamento.getNuFatura(), cellFormulaIEQQET);

                    /**
                     * Média dos Indicadores
                     */
                    rownum = listaValorPrevisto.get(listaValorPrevisto.size() - 1).getRowIndex() + 6;

                    row = getRow(sheet, rownum++);
                    XSSFSheetUtils.addMergedRegion(sheet, row, 8, 10, "Média dos Indicadores", CellStylesEnum.ORANGE_ALIGN_CENTER_BOLD);

                    row = getRow(sheet, rownum++);
                    XSSFSheetUtils.addMergedRegion(sheet, row, 8, 9, "ICQL - Qualidade");
                    cellFormulaIEQQL = addCell(row, 10, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
                    if (getMediaIndicador(listaIEQQL, faturamento.getNuFatura(), indicadorFNS) != null) {
                        setCellFormula(cellFormulaIEQQL, //
                                "average(" + getMediaIndicador(listaIEQQL, faturamento.getNuFatura(), indicadorFNS) + ")", //
                                CellStylesEnum.DECIMAL5);
                    }
                    addIndicador(listaMediaIEQQL, faturamento.getNuFatura(), cellFormulaIEQQL);

                    row = getRow(sheet, rownum++);
                    XSSFSheetUtils.addMergedRegion(sheet, row, 8, 9, "ICPZ - Prazo");
                    cellFormulaIEQQPZ = addCell(row, 10, "N/A", CellStylesEnum.DECIMAL5);
                    if (getMediaIndicador(listaIEQQPZ, faturamento.getNuFatura(), indicadorFNS) != null) {
                        setCellFormula(cellFormulaIEQQPZ, //
                                "average(" + getMediaIndicador(listaIEQQPZ, faturamento.getNuFatura(), indicadorFNS) + ")", //
                                CellStylesEnum.DECIMAL5);
                    }
                    addIndicador(listaMediaIEQQPZ, faturamento.getNuFatura(), cellFormulaIEQQPZ);

                    row = getRow(sheet, rownum++);
                    XSSFSheetUtils.addMergedRegion(sheet, row, 8, 9, "ICET - Estoque");
                    cellFormulaIEQQET = addCell(row, 10, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
                    if (getMediaIndicador(listaIEQQET, faturamento.getNuFatura(), indicadorFNS) != null) {
                        setCellFormula(cellFormulaIEQQET, //
                                "average(" + getMediaIndicador(listaIEQQET, faturamento.getNuFatura(), indicadorFNS) + ")", //
                                CellStylesEnum.DECIMAL5);
                    }
                    addIndicador(listaMediaIEQQET, faturamento.getNuFatura(), cellFormulaIEQQET);

                    /**
                     * Desempenho / Valor FNS
                     */
                    rownum = listaValorPrevisto.get(listaValorPrevisto.size() - 1).getRowIndex() + 12;

                    row = getRow(sheet, rownum++);
                    addCell(row, 8, "Desempenho",  CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);
                    addCell(row, 9, "Valor FNS",  CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);
                    addCell(row, 10, "Valor Financeiro",  CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);

                    row = getRow(sheet, rownum++);

                    XSSFCell cellDesempenho = addCell(row, 8, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
                    XSSFCell cellValorFNS = addCell(row, 9, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
                    XSSFCell cellValorFinanceiro = addCell(row, 10, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

                    //setCellFormulaDesempenho(cellDesempenho, faturamento.getNuFatura());
                    setCellFormulaValorFNS(cellValorFNS, cellDesempenho, qtFaixas);
                    setCellFormula(cellValorFinanceiro, //
                            String.format("%s*%s", //
                                    "STRING".equalsIgnoreCase(cellValorFNS.getCellType().name()) ? //
                                            BigDecimal.ONE : cellValorFNS.getReference(), //
                                    insumo.getVrPrevistoTermo().getReference()), //
                            CellStylesEnum.DECIMAL);

                    insumo.setVrFns(cellValorFNS);

                }

                row = addNewRow(sheet);

            }

        } // faturamentos
    }

    private XSSFCell contarServicosAtrasados(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

        List<EntregaDTO> servicosEntreguesComAtraso = entregaService
                .listarServicosEntreguesComAtraso(equipe.getNuEquipe(), faturamento.getNuFatura());

        XSSFRow row = addNewRow(sheet);
        addCell(row, 0, "Atrasados", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
        return addCell(row, 1, BigDecimal.valueOf(servicosEntreguesComAtraso.size()));
    }

    private XSSFCell contarServicosEntreguesEstoque(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

        List<EntregaDTO> servicosEntreguesEstoque = entregaService.listarServicosEntreguesEstoque(equipe.getNuEquipe(),
                faturamento.getNuFatura());

        XSSFRow row = addNewRow(sheet);
        addCell(row, 0, "Estoque", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
        return addCell(row, 1, BigDecimal.valueOf(servicosEntreguesEstoque.size()));
    }

    private XSSFCell contarServicosContratados(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

        List<EntregaDTO> servicosContratados = entregaService.listarServicosContratados(equipe.getNuEquipe(),
                faturamento.getNuFatura());

        XSSFRow row = addNewRow(sheet);
        addCell(row, 0, "Contratados", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
        return addCell(row, 1, BigDecimal.valueOf(servicosContratados.size()));
    }

    private XSSFCell contarServicosDevolvidos(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

        List<EntregaDTO> servicosDevolvidos = entregaService.listarServicosDevolvidos(equipe.getNuEquipe(),
                faturamento.getNuFatura());

        XSSFRow row = addNewRow(sheet);
        addCell(row, 0, "Devolvidos", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
        return addCell(row, 1, BigDecimal.valueOf(servicosDevolvidos.size()));
    }

    private XSSFCell contarServicosEntregues(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

        List<EntregaDTO> servicosEntregues = entregaService.listarServicosEntregues(equipe.getNuEquipe(),
                faturamento.getNuFatura());

        XSSFRow row = addNewRow(sheet);
        addCell(row, 0, "Entregues", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
        return addCell(row, 1, BigDecimal.valueOf(servicosEntregues.size()));
    }

    private void setCellFormulaDesempenho(XSSFCell cell, Long nuFatura) {

        StringBuilder s1 = new StringBuilder();
        StringBuilder s2 = new StringBuilder();

        String ieqql = parseFormulaDesempenho(listaMediaIEQQL, nuFatura, "Equipe!J6");
        if (ieqql != null) {
            s1.append("+" + ieqql.toString());
            s2.append("," + "Equipe!J6");
        }

        String ieqqpz = parseFormulaDesempenho(listaMediaIEQQPZ, nuFatura, "Equipe!J5");
        if (ieqqpz != null) {
            s1.append("+" + ieqqpz.toString());
            s2.append("," + "Equipe!J5");
        }

        String ieqqet = parseFormulaDesempenho(listaMediaIEQQET, nuFatura, "Equipe!J4");
        if (ieqqet != null) {
            s1.append("+" + ieqqet.toString());
            s2.append("," + "Equipe!J4");
        }

        if (s1.length() > 1) {
            setCellFormula(cell, //
                    String.format("(%s)/sum(%s)", s1.toString().substring(1), s2.toString().substring(1)));
        }
    }

    private String parseFormulaDesempenho(Map<Long, List<XSSFCell>> lista, Long nuFatura, String cellPesoIndicador) {

        StringBuilder p1 = new StringBuilder();

        List<XSSFCell> indicadores = getIndicadoresMediaFNS(lista, nuFatura, 1);

        for (XSSFCell c : indicadores) {
            try {

                if (c != null && !"N/A".equalsIgnoreCase(c.toString())) {
                    p1.append("," + c.getNumericCellValue());
                }
            } catch (Exception e) {
            }
        }

        if (p1.length() > 1) {
            return String.format("(%s*%s)", p1.toString().substring(1), cellPesoIndicador);
        }

        return null;
    }

    private void setCellFormulaValorFNS(XSSFCell cellValorFNS, XSSFCell cellDesempenho, int qtFaixas) {

        // essa posição fixa é o número da primeira linha das faixas
        // da seção "Valores Faixas Indicador" na aba equipe
        int pos = 15;

        if (!"N/A".equalsIgnoreCase(cellDesempenho.toString())) {

            StringBuilder formula = new StringBuilder();

            for (int i = 1; i <= qtFaixas; i++) {

                formula.append(
                        String.format("+if(and(trunc(%s,2)>=Equipe!H%s,trunc(%s,2)<=Equipe!I%s),Equipe!J%s)", //
                                cellDesempenho.getReference(), pos + i, //
                                cellDesempenho.getReference(), pos + i, //
                                pos + i));
            }

            setCellFormula(cellValorFNS, formula.toString().substring(1), CellStylesEnum.DECIMAL);

        }

    }

}
