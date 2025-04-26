package org.prometheus.relatorios.equipe;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.CategoriaServicoMilheirosDTO;
import org.prometheus.dto.EntregaDTO;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.InsumoCalculoDescontoProporcionalDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.dto.QuantidadePrecoMilheirosDTO;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioEquipe;
import org.prometheus.service.CalculoValorMilheirosService;
import org.prometheus.service.EntregaServiceQualidade;
import org.prometheus.service.EquipeService;
import org.prometheus.utils.DateUtil;
import org.prometheus.utils.MergeRegionUtil;
import org.prometheus.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AbaCalculosMilheiros extends Relatorio {

    private static final Logger LOG = LogManager.getLogger(AbaCalculosMilheiros.class.getSimpleName());

    private static final int LAYOUT_COLUMNS_COUNT = 10;

    @Autowired
    private EquipeService equipeService;

    @Autowired
    private CalculoValorMilheirosService service;

    @Autowired
    private EntregaServiceQualidade entregaService;

    public void gerar(XSSFWorkbook wb, String coEquipe, List<FaturamentoDTO> faturamentos,
            Map<Long, InsumosFaturamentoDTO> insumos) throws Exception {

        EquipeDTO equipe = equipeService.findBy(coEquipe);

        XSSFSheet sheet = wb.createSheet(RelatorioEquipe.ABA_CALCULOS);

        carregarFontes(wb);
        carregarEstilos(wb);

        sheet.setZoom(90);
        sheet.setDisplayGridlines(false);

        setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 100);
        setColumnWidth(sheet, 2, 5);
        setColumnWidth(sheet, 6, 5);

        XSSFRow row = null;

        // faturamentos
        for (FaturamentoDTO faturamento : faturamentos) {

            InsumosFaturamentoDTO insumo = new InsumosFaturamentoDTO();
            
            LOG.info(String.format("Contrato: %s Equipe: %s Fatura: %s - %s", //
                    equipe.getNuContrato(), equipe.getNuEquipe() + " - " + equipe.getCoEquipe(),
                    faturamento.getNuFatura(), faturamento.getDeFatura()));

            row = addNewRow(sheet);
            MergeRegionUtil.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, //
                    "Fatura: " + faturamento.getNuFatura() + " - " + faturamento.getDeFatura(), //
                    getCellStyle(ALIGN_CENTER_GREY_40_PERCENT_BOLD));

            addBlank(sheet);

            row = addNewRow(sheet);
            addCell(row, 0, "QT. Dias Faturamento", ALIGN_LEFT_GREY_25_PERCENT_BOLD);
            addCell(row, 1, faturamento.getQtDiasFaturamento(), STYLE_DECIMAL);

            List<QuantidadePrecoMilheirosDTO> quantidades = service.listarQuantidades(faturamento.getNuFatura(),
                    equipe.getNuEquipe());

            addCell(row, 3, "Tipo Preço", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 4, "Preço", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addCell(row, 5, "Quantidades", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
            addMergedRegion(sheet, row, 6, 7, "Valor", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

            XSSFCell qtMilheiros = null;
            XSSFCell vrPreco = null;
            XSSFCell vrPrevistoInicio = null;
            XSSFCell vrPrevistoFinal = null;
            XSSFCell vrPrevisto = null;
            XSSFCell vrDesconto = null;
            XSSFCell vrPrevistoQuantidade = null;
            XSSFCell vrFNS = null;

            if (Utils.isNotEmpty(quantidades)) {
                for (QuantidadePrecoMilheirosDTO dto : quantidades) {
                    row = addNewRow(sheet);
                    addCell(row, 3, dto.getNoTipoPreco());
                    vrPreco = addCell(row, 4, dto.getVrPrecoGrupo(), STYLE_DECIMAL);
                    qtMilheiros = addCell(row, 5, dto.getQtSolicitacao(), STYLE_DECIMAL);

                    if (vrPrevistoInicio == null) {
                        vrPrevistoInicio = setCellFormula(addMergedRegion(sheet, row, 6, 7, "N/A"), //
                                String.format("%s*%s", qtMilheiros.getReference(), vrPreco.getReference()), //
                                STYLE_DECIMAL);
                        vrPrevistoFinal = vrPrevistoInicio;
                    } else {
                        vrPrevistoFinal = setCellFormula(addMergedRegion(sheet, row, 6, 7, "N/A"), //
                                String.format("%s*%s", qtMilheiros.getReference(), vrPreco.getReference()), //
                                STYLE_DECIMAL);
                    }
                }
            }

            row = addNewRow(sheet);
            vrPrevistoQuantidade = setCellFormula(addMergedRegion(sheet, row, 6, 7, "N/A"), //
                    String.format("sum(%s:%s)", vrPrevistoInicio.getReference(), vrPrevistoFinal.getReference()), //
                    STYLE_DECIMAL);

            addBlank(sheet);

            //
            // CATEGORIAS
            //
            List<CategoriaServicoMilheirosDTO> categorias = service.listarCategorias(equipe.getNuEquipe());

            row = addNewRow(sheet);
            addMergedRegion(sheet, row, 0, 5, "Categoria de Serviços", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

            XSSFCell pcParticipacaoCategoria = null;
            XSSFCell pcDescontoCategoria = null;
            XSSFCell vrPrevistoCategoria = null;
            XSSFCell vrDescontoCategoria = null;
            XSSFCell vrPrevistoCategoriaInicio = null;
            XSSFCell vrPrevistoCategoriaFinal = null;

            for (CategoriaServicoMilheirosDTO dto : categorias) {

                XSSFRow rowServicos = addNewRow(sheet);

                int qtLinhas = 2;

                if ("S".equalsIgnoreCase(dto.getIcAplicaDesconto())) {
                    qtLinhas = 5;
                }

                MergeRegionUtil.addMergedRegion(sheet, rowServicos, 0, 3, qtLinhas, //
                        dto.getNoTpCategoriaServico(), //
                        getCellStyle(ALIGN_CENTER_GREY_25_PERCENT_BOLD));

                int pos = 0;

                row = getRow(sheet, rowServicos.getRowNum() + (pos++));
                addCell(row, 4, "Participação");
                pcParticipacaoCategoria = addCell(row, 5, dto.getPcParticipacao(), STYLE_PERCENT);

                if ("S".equalsIgnoreCase(dto.getIcAplicaDesconto())) {

                    row = getRow(sheet, rowServicos.getRowNum() + (pos++));
                    addCell(row, 4, "Valor previsto(VMFf)");
                    vrPrevistoCategoria = setCellFormula(sheet, row, //
                            String.format("%s*%s", vrPrevistoQuantidade.getReference(),
                                    pcParticipacaoCategoria.getReference()),
                            5);

                    row = getRow(sheet, rowServicos.getRowNum() + (pos++));
                    addCell(row, 4, "Desconto Proporcional(DP)");
                    pcDescontoCategoria = addCell(row, 5, BigDecimal.ZERO, STYLE_DECIMAL_5DIGITS);
                    insumo.setPcDescontoProporcional(pcDescontoCategoria);

                    row = getRow(sheet, rowServicos.getRowNum() + (pos++));
                    addCell(row, 4, "Valor desconto");
                    vrDescontoCategoria = setCellFormula(sheet, row, //
                            String.format("%s*%s", vrPrevistoCategoria.getReference(),
                                    pcDescontoCategoria.getReference()),
                            5);
                    insumo.setVrDescontoProporcional(vrDescontoCategoria);
                    
                    row = getRow(sheet, rowServicos.getRowNum() + (pos++));
                    addCell(row, 4, "Valor financeiro(VFM)");
                    vrPrevistoCategoria = setCellFormula(sheet, row, //
                            String.format("%s-%s", vrPrevistoCategoria.getReference(),
                                    vrDescontoCategoria.getReference()),
                            5);
                } else {
                    row = getRow(sheet, rowServicos.getRowNum() + (pos++));
                    addCell(row, 4, "Valor previsto(VMF)");
                    vrPrevistoCategoria = setCellFormula(sheet, row, //
                            String.format("%s*%s", vrPrevistoQuantidade.getReference(),
                                    pcParticipacaoCategoria.getReference()),
                            5);
                }

                if (vrPrevistoCategoriaInicio == null) {
                    vrPrevistoCategoriaInicio = vrPrevistoCategoria;
                    vrPrevistoCategoriaFinal = vrPrevistoCategoria;
                } else {
                    vrPrevistoCategoriaFinal = vrPrevistoCategoria;
                }

            }

            row = addNewRow(sheet);
            addCell(row, 4, "Valor Previsto");
            vrPrevisto = setCellFormula(sheet, row, //
                    String.format("sum(%s,%s)", //
                            vrPrevistoCategoriaInicio.getReference(), //
                            vrPrevistoCategoriaFinal.getReference()),
                    5);

            addBlank(sheet);

            //
            // INDICADORES
            //

            /**
             * Indicadores
             */
            if ("S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {

                row = addNewRow(sheet);
                addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1,
                        "Grupo configurado para aplicar Indicadores somente a partir de "
                                + DateUtil.format(faturamento.getDtConsiderarFns()),
                        ALIGN_LEFT_BOLD, false);
                addBlank(sheet);
            }

            if (!"S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {

                row = addNewRow(sheet);

                int rownum = getLastRow(sheet).getRowNum();

                addMergedRegion(sheet, row, 0, 1, "Serviços", ALIGN_CENTER_GREEN_BOLD);

                XSSFCell cellQtServicosEntregues = contarServicosEntregues(equipe, sheet, faturamento);
                XSSFCell cellQtServicosDevolvidos = contarServicosDevolvidos(equipe, sheet, faturamento);
                XSSFCell cellQtServicosEntreguesComAtraso = contarServicosAtrasados(equipe, sheet, faturamento);
                XSSFCell cellQtServicosEntreguesEstoque = contarServicosEntreguesEstoque(equipe, sheet, faturamento);
                XSSFCell cellQtServicosContratados = contarServicosContratados(equipe, sheet, faturamento);

                addMergedRegion(sheet, row, 3, 5, "Indicadores", ALIGN_CENTER_GREY_40_PERCENT_BOLD);

                addMergedRegion(sheet, row, 7, 9, "Média dos Indicadores", ALIGN_CENTER_ORANGE_BOLD);

                rownum++;

                /**
                 * ICQL - Qualidade do Serviço
                 */
                row = getRow(sheet, rownum++);
                addMergedRegion(sheet, row, 3, 4, "ICQL - Qualidade", ALIGN_LEFT_WHITE_SMOKE_BOLD);
                addMergedRegion(sheet, row, 7, 8, "ICQL - Qualidade", ALIGN_LEFT_WHITE_SMOKE_BOLD);
                XSSFCell cellFormulaIEQQL = addCell(row, 5, "N/A", ALIGN_CENTER);
                addCell(row, 9, "N/A", ALIGN_CENTER);

                /**
                 * IEQQPZ - Prazo do Serviço
                 */
                row = getRow(sheet, rownum++);
                addMergedRegion(sheet, row, 3, 4, "ICPZ - Prazo", ALIGN_LEFT_WHITE_SMOKE_BOLD);
                addMergedRegion(sheet, row, 7, 8, "ICPZ - Prazo", ALIGN_LEFT_WHITE_SMOKE_BOLD);
                XSSFCell cellFormulaICPZ = addCell(row, 5, "N/A", ALIGN_CENTER);
                addCell(row, 9, "N/A", ALIGN_CENTER);

                /**
                 * ICET - Estoque
                 */
                row = getRow(sheet, rownum++);
                addMergedRegion(sheet, row, 3, 4, "ICET - Estoque", ALIGN_LEFT_WHITE_SMOKE_BOLD);
                addMergedRegion(sheet, row, 7, 8, "ICET - Estoque", ALIGN_LEFT_WHITE_SMOKE_BOLD);
                XSSFCell cellFormulaICET = addCell(row, 5, "N/A", ALIGN_CENTER);
                addCell(row, 9, "N/A", ALIGN_CENTER);

                /**
                 * IEQDF - Disponibilidade das Ferramentas
                 */
                row = getRow(sheet, rownum++);
                addMergedRegion(sheet, row, 3, 4, "IEQDF - Disponiblidade Ferramentas", ALIGN_LEFT_WHITE_SMOKE_BOLD);
                addMergedRegion(sheet, row, 7, 8, "IEQDF - Disponiblidade Ferramentas", ALIGN_LEFT_WHITE_SMOKE_BOLD);
                XSSFCell cellFormulaIEQDF = addCell(row, 5, "N/A", ALIGN_CENTER);
                addCell(row, 9, "N/A", ALIGN_CENTER);

                /**
                 * IEQRSA - Resultado do Serviço Assistido
                 */
                row = getRow(sheet, rownum++);
                addMergedRegion(sheet, row, 3, 4, "IEQRSA - Resultado Serviço Assistido", ALIGN_LEFT_WHITE_SMOKE_BOLD);
                addMergedRegion(sheet, row, 7, 8, "IEQRSA - Resultado Serviço Assistido", ALIGN_LEFT_WHITE_SMOKE_BOLD);
                XSSFCell cellFormulaIEQRSA = addCell(row, 5, "N/A", ALIGN_CENTER);
                addCell(row, 9, "N/A", ALIGN_CENTER);

                // addIndicador(listaIEQQPZ, faturamento.getNuFatura(), cellFormulaIEQQPZ);
//
//                /**
//                 * ICET - Estoque
//                 */
//                row = getRow(sheet, rownum++);
//                addMergedRegion(sheet, row, 4, 5, "ICET - Estoque", ALIGN_LEFT);
//                
//                XSSFCell cellFormulaIEQQET = addCell(row, 6, "N/A", ALIGN_CENTER);
//
//                if ("NUMERIC".equals(cellQtServicosContratados.getCellType().name())
//                        && cellQtServicosContratados.getNumericCellValue() != 0) {
//                    setCellFormula(cellFormulaIEQQET, //
//                            String.format("%s/%s", //
//                                    cellQtServicosEntreguesEstoque.getReference(),
//                                    cellQtServicosContratados.getReference()), //
//                            STYLE_DECIMAL_5DIGITS);
//                }
//
//                //addIndicador(listaIEQQET, faturamento.getNuFatura(), cellFormulaIEQQET);
//
//                /**
//                 * IEQRCP - Resultado Capacitação
//                 */
//                row = getRow(sheet, rownum++);
//                addMergedRegion(sheet, row, 4, 5, "IEQRCP - Resultado Capacitação", ALIGN_LEFT);
//                
//                XSSFCell cellFormulaIEQRCP = addCell(row, 6, "N/A", ALIGN_CENTER);
//                
//                /**
//                 * IEQRSA - Resultado Assistido
//                 */
//                row = getRow(sheet, rownum++);
//                addMergedRegion(sheet, row, 4, 5, "IEQRSA - Resultado Assistido", ALIGN_LEFT);
//                
//                XSSFCell cellFormulaIEQRSA = addCell(row, 6, "N/A", ALIGN_CENTER);
//                
//                /**
//                 * Média dos Indicadores
//                 */
//                rownum = listaValorPrevisto.get(listaValorPrevisto.size() - 1).getRowIndex() + 6;
//
//                row = getRow(sheet, rownum++);
//                addMergedRegion(sheet, row, 8, 10, "Média dos Indicadores", ALIGN_CENTER_ORANGE_BOLD);
//
//                row = getRow(sheet, rownum++);
//                addMergedRegion(sheet, row, 8, 9, "ICQL - Qualidade");
//                cellFormulaIEQQL = addCell(row, 10, "N/A", ALIGN_CENTER);
//                if (getMediaIndicador(listaIEQQL, faturamento.getNuFatura(), indicadorFNS) != null) {
//                    setCellFormula(cellFormulaIEQQL, //
//                            "average(" + getMediaIndicador(listaIEQQL, faturamento.getNuFatura(), indicadorFNS) + ")", //
//                            STYLE_DECIMAL_5DIGITS);
//                }
//                addIndicador(listaMediaIEQQL, faturamento.getNuFatura(), cellFormulaIEQQL);
//
//                row = getRow(sheet, rownum++);
//                addMergedRegion(sheet, row, 8, 9, "ICPZ - Prazo");
//                cellFormulaIEQQPZ = addCell(row, 10, "N/A", ALIGN_CENTER);
//                if (getMediaIndicador(listaIEQQPZ, faturamento.getNuFatura(), indicadorFNS) != null) {
//                    setCellFormula(cellFormulaIEQQPZ, //
//                            "average(" + getMediaIndicador(listaIEQQPZ, faturamento.getNuFatura(), indicadorFNS) + ")", //
//                            STYLE_DECIMAL_5DIGITS);
//                }
//                addIndicador(listaMediaIEQQPZ, faturamento.getNuFatura(), cellFormulaIEQQPZ);
//
//                row = getRow(sheet, rownum++);
//                addMergedRegion(sheet, row, 8, 9, "ICET - Estoque");
//                cellFormulaIEQQET = addCell(row, 10, "N/A", ALIGN_CENTER);
//                if (getMediaIndicador(listaIEQQET, faturamento.getNuFatura(), indicadorFNS) != null) {
//                    setCellFormula(cellFormulaIEQQET, //
//                            "average(" + getMediaIndicador(listaIEQQET, faturamento.getNuFatura(), indicadorFNS) + ")", //
//                            STYLE_DECIMAL_5DIGITS);
//                }
//                addIndicador(listaMediaIEQQET, faturamento.getNuFatura(), cellFormulaIEQQET);
//
//                row = getRow(sheet, rownum++);
//                addMergedRegion(sheet, row, 8, 9, "IEQRCP - Resultado Capacitação");
//                cellFormulaIEQRCP = addCell(row, 10, "N/A", ALIGN_CENTER);
//                
//                row = getRow(sheet, rownum++);
//                addMergedRegion(sheet, row, 8, 9, "IEQRSA - Resultado Assistido");
//                cellFormulaIEQRSA = addCell(row, 10, "N/A", ALIGN_CENTER);
//
//                /**
//                 * Desempenho / Valor FNS
//                 */
//                rownum = listaValorPrevisto.get(listaValorPrevisto.size() - 1).getRowIndex() + 13;
//
//                row = getRow(sheet, rownum++);
//                addCell(row, 8, "Desempenho", ALIGN_CENTER_GREEN_BOLD);
//                addCell(row, 9, "Valor FNS", ALIGN_CENTER_GREEN_BOLD);
//                addCell(row, 10, "Valor Financeiro", ALIGN_CENTER_GREEN_BOLD);
//
//                row = getRow(sheet, rownum++);
//
//                XSSFCell cellDesempenho = addCell(row, 8, "N/A", ALIGN_CENTER);
//                XSSFCell cellValorFNS = addCell(row, 9, "N/A", ALIGN_CENTER);
//                XSSFCell cellValorFinanceiro = addCell(row, 10, "N/A", ALIGN_CENTER);
//
//                setCellFormulaDesempenho(cellDesempenho, faturamento.getNuFatura());
//                setCellFormulaValorFNS(cellValorFNS, cellDesempenho, qtFaixas);
//                setCellFormula(cellValorFinanceiro, //
//                        String.format("%s*%s", //
//                                "STRING".equalsIgnoreCase(cellValorFNS.getCellType().name()) ? //
//                                        BigDecimal.ONE : cellValorFNS.getReference(), //
//                                insumo.getVrPrevistoTermo().getReference()), //
//                        STYLE_DECIMAL);
//
//                insumo.setVrFns(cellValorFNS);
            }

            // apenas uma linha em branco
            addBlank(sheet);

            /**
             * Desempenho / Valor FNS
             */
            row = addNewRow(sheet);
            addCell(row, 7, "Desempenho", ALIGN_CENTER_GREEN_BOLD);
            addCell(row, 8, "Valor FNS", ALIGN_CENTER_GREEN_BOLD);
            addCell(row, 9, "Valor Financeiro", ALIGN_CENTER_GREEN_BOLD);

            row = addNewRow(sheet);
            addCell(row, 7, "N/A", ALIGN_CENTER);
            vrFNS = addCell(row, 8, BigDecimal.ONE);
            setCellFormula(addCell(row, 9, BigDecimal.ZERO, STYLE_DECIMAL), //
                    String.format("%s*%s", //
                            vrPrevisto.getReference(), //
                            vrFNS.getReference()));
            
            insumo.setVrFns(vrFNS);
            
            addBlank(sheet);

            //
            // INSUMOS CALCULO DESCONTO PROPORCIONAL
            //
            row = addNewRow(sheet);
            addMergedRegion(sheet, row, 0, 7, "Insumos do cálculo de Desconto proporcional (DP)", //
                    ALIGN_CENTER_GREY_25_PERCENT_BOLD);

            List<InsumoCalculoDescontoProporcionalDTO> insumosCalculoDesconto = service
                    .listar(faturamento.getNuFatura(), equipe.getNuEquipe());

            XSSFCell qtMinutosFaturamento = null;
            XSSFCell qtMinutosIndisponivel = null;
            XSSFCell qtUltimaFaixaDPC = null;
            XSSFCell qtPrimeiraFaixaDPC = null;
            XSSFCell pcDesconto = null;
            XSSFCell pcParticipacao = null;

            if (Utils.isNotEmpty(insumosCalculoDesconto)) {

                row = addNewRow(sheet);
                
                addCell(row, 0, "Ferramenta", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
                addCell(row, 1, "Participação(P)", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
                addMergedRegion(sheet, row, 2, 3, "Qtd. Min. Faturamento (QtF)", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
                addCell(row, 4, "Qtd. Min. Indisponível(QtMI)", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
                addCell(row, 5, "Qtd. Min. Disponível(QtMD)", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
                addMergedRegion(sheet, row, 6, 7, "Desconto Proporcional", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
                
                // devemos definir a alttura da linha depois de fechar o conteudo
                row.setHeight((short) (row.getHeight() * 2));
                
                for (InsumoCalculoDescontoProporcionalDTO dto : insumosCalculoDesconto) {
                    row = addNewRow(sheet);
                    addCell(row, 0, dto.getNoFerramentaSuporte());
                    pcParticipacao = addCell(row, 1, dto.getPcParticipacao(), STYLE_PERCENT);
                    qtMinutosFaturamento = addMergedRegion(sheet, row, 2, 3, dto.getQtMinutosFaturamento(), STYLE_NUMBER);
                    qtMinutosIndisponivel = addCell(row, 4, dto.getQtMinutosIndisponivel(), STYLE_NUMBER);
                    setCellFormula(addCell(row, 5, dto.getQtMinutosIndisponivel(), STYLE_NUMBER), //
                            String.format("%s-%s", qtMinutosFaturamento.getReference(),
                                    qtMinutosIndisponivel.getReference()));

                    if (qtPrimeiraFaixaDPC == null) {
                        qtPrimeiraFaixaDPC = setCellFormula(addMergedRegion(sheet, row, 6, 7, BigDecimal.ZERO, STYLE_DECIMAL_5DIGITS), //
                                String.format("(%s/%s)*%s", //
                                        qtMinutosIndisponivel.getReference(), //
                                        qtMinutosFaturamento.getReference(), //
                                        pcParticipacao.getReference()));
                        qtUltimaFaixaDPC = qtPrimeiraFaixaDPC;
                    } else {
                        qtPrimeiraFaixaDPC = setCellFormula(addMergedRegion(sheet, row, 6, 7, BigDecimal.ZERO, STYLE_DECIMAL_5DIGITS), //
                                String.format("(%s/%s)*%s", //
                                        qtMinutosIndisponivel.getReference(), //
                                        qtMinutosFaturamento.getReference(), //
                                        pcParticipacao.getReference()));
                    }

                }

                row = addNewRow(sheet);
                addCell(row, 4, "Desconto Proporcional", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
                pcDesconto = setCellFormula(sheet, row, //
                        String.format("sum(%s:%s)", qtPrimeiraFaixaDPC.getReference(), qtUltimaFaixaDPC.getReference()),
                        5, STYLE_DECIMAL_5DIGITS);

                setCellFormula(pcDescontoCategoria, String.format("%s", pcDesconto.getReference()));
            }

            // apenas uma linha em branco
            addBlank(sheet);

            insumos.put(faturamento.getNuFatura(), insumo);
            
        } // faturamentos
    }

    private XSSFCell contarServicosAtrasados(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

        List<EntregaDTO> servicosEntreguesComAtraso = entregaService
                .listarServicosEntreguesComAtraso(equipe.getNuEquipe(), faturamento.getNuFatura());

        XSSFRow row = addNewRow(sheet);
        addCell(row, 0, "Atrasados", ALIGN_LEFT_WHITE_SMOKE_BOLD);
        return addCell(row, 1, BigDecimal.valueOf(servicosEntreguesComAtraso.size()));
    }

    private XSSFCell contarServicosEntreguesEstoque(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

        List<EntregaDTO> servicosEntreguesEstoque = entregaService.listarServicosEntreguesEstoque(equipe.getNuEquipe(),
                faturamento.getNuFatura());

        XSSFRow row = addNewRow(sheet);
        addCell(row, 0, "Estoque", ALIGN_LEFT_WHITE_SMOKE_BOLD);
        return addCell(row, 1, BigDecimal.valueOf(servicosEntreguesEstoque.size()));
    }

    private XSSFCell contarServicosContratados(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

        List<EntregaDTO> servicosContratados = entregaService.listarServicosContratados(equipe.getNuEquipe(),
                faturamento.getNuFatura());

        XSSFRow row = addNewRow(sheet);
        addCell(row, 0, "Contratados", ALIGN_LEFT_WHITE_SMOKE_BOLD);
        return addCell(row, 1, BigDecimal.valueOf(servicosContratados.size()));
    }

    private XSSFCell contarServicosDevolvidos(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

        List<EntregaDTO> servicosDevolvidos = entregaService.listarServicosDevolvidos(equipe.getNuEquipe(),
                faturamento.getNuFatura());

        XSSFRow row = addNewRow(sheet);
        addCell(row, 0, "Devolvidos", ALIGN_LEFT_WHITE_SMOKE_BOLD);
        return addCell(row, 1, BigDecimal.valueOf(servicosDevolvidos.size()));
    }

    private XSSFCell contarServicosEntregues(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

        List<EntregaDTO> servicosEntregues = entregaService.listarServicosEntregues(equipe.getNuEquipe(),
                faturamento.getNuFatura());

        XSSFRow row = addNewRow(sheet);
        addCell(row, 0, "Entregues", ALIGN_LEFT_WHITE_SMOKE_BOLD);
        return addCell(row, 1, BigDecimal.valueOf(servicosEntregues.size()));
    }

}
