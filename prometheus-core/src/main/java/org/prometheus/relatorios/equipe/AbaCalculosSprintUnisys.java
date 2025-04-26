package org.prometheus.relatorios.equipe;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.AusenciaExtraFaturamentoDTO;
import org.prometheus.dto.CicloTrabalhoDTO;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.dto.SolicitacaoDTO;
import org.prometheus.dto.VigenciaMembrosDTO;
import org.prometheus.dto.VigenciaMembrosSprintDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioEquipe;
import org.prometheus.service.AusenciaExtraFaturamentoService;
import org.prometheus.service.CicloTrabalhoService;
import org.prometheus.service.EquipeService;
import org.prometheus.service.FaixaIndicadorService;
import org.prometheus.service.IndicadorService;
import org.prometheus.service.PrecosEquipeService;
import org.prometheus.service.SolicitacaoService;
import org.prometheus.service.VigenciaMembrosService;
import org.prometheus.service.VigenciaMembrosSprintService;
import org.prometheus.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaCalculosSprintUnisys extends Relatorio {

    private static final Logger LOG = LogManager.getLogger(AbaCalculosSprintUnisys.class.getSimpleName());
    
    private static final int LAYOUT_COLUMNS_COUNT = 15;

    @Autowired
    private SolicitacaoService solicitacaoService;

    @Autowired
    private VigenciaMembrosService vigenciasMembrosService;

    @Autowired
    private VigenciaMembrosSprintService vigenciasMembrosSprintService;

    @Autowired
    private CicloTrabalhoService cicloTrabalhoService;

    @Autowired
    private EquipeService equipeService;

    @Autowired
    private FaixaIndicadorService faixaIndicadorService;

    @Autowired
    private PrecosEquipeService precoEquipeService;

    @Autowired
    private IndicadorService indicadorService;
    
    @Autowired
    private AusenciaExtraFaturamentoService ausenciExtraService;
    
    private Map<Long, List<XSSFCell>> indicadoresProdutividade;
    private Map<Long, List<XSSFCell>> indicadoresQualideProduto;
    private Map<Long, List<XSSFCell>> indicadoresValorAgregado;

    public Map<Long, InsumosFaturamentoDTO> gerar(XSSFWorkbook wb, String coEquipe, List<FaturamentoDTO> faturamentos)
            throws InvalidFormatException, IOException {

        indicadoresProdutividade = new LinkedHashMap<Long, List<XSSFCell>>();
        indicadoresQualideProduto = new LinkedHashMap<Long, List<XSSFCell>>();
        indicadoresValorAgregado = new LinkedHashMap<Long, List<XSSFCell>>();

        Map<Long, InsumosFaturamentoDTO> result = new LinkedHashMap<Long, InsumosFaturamentoDTO>();

        EquipeDTO equipe = equipeService.findBy(coEquipe);

        XSSFSheet sheet = wb.createSheet(RelatorioEquipe.ABA_CALCULOS);

        carregarFontes(wb);
        carregarEstilos(wb);

        sheet.setZoom(90);
        sheet.setDisplayGridlines(false);

        setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);
        
//        setColumnWidth(sheet, 8, 5);
//        setColumnWidth(sheet, 9, 120);
//        setColumnWidth(sheet, 10, 25);
//        setColumnWidth(sheet, 11, 25);
//        setColumnWidth(sheet, 12, 25);
//        setColumnWidth(sheet, 13, 25);
//        setColumnWidth(sheet, 14, 25);

        XSSFRow row = null;

        int qtFaixas = faixaIndicadorService.listar(equipe.getNuEquipe()).size();

        // faturamentos
        for (FaturamentoDTO faturamento : faturamentos) {

            List<VigenciaMembrosDTO> vigencias = vigenciasMembrosService.listar(faturamento.getNuFatura(),
                    equipe.getNuEquipe());

            if (Utils.isNotEmpty(vigencias)) {
                processarFaturamento(equipe, faturamento, sheet, row, vigencias, qtFaixas, result);
            }

        }

        return result;
    }

    private void processarFaturamento(EquipeDTO equipe, FaturamentoDTO faturamento, XSSFSheet sheet, XSSFRow row,
            List<VigenciaMembrosDTO> vigencias, int qtFaixas, Map<Long, InsumosFaturamentoDTO> result) {

        XSSFCell cellQtEfetiva = null;
        XSSFCell cellQtDiasTrabalhados = null;
        XSSFCell cellProdAuferida = null;
        XSSFCell cellProdutividade = null;
        XSSFCell cellQualidadeProduto = null;
        XSSFCell cellValorAgregado = null;
        XSSFCell cellTamanho = null;
        XSSFCell cellMediaValorAgregado = null;
        XSSFCell cellMediaQualidadeProduto = null;
        XSSFCell cellMediaProdutividade = null;
        XSSFCell cellDesempenho = null;
        XSSFCell cellValorFNS = null;

        IndicadorContratoDTO indicadorFNS = indicadorService.getIndicadorContratoFNS(equipe.getCoEquipe());
        
        InsumosFaturamentoDTO insumo = new InsumosFaturamentoDTO();

        insumo.setNuFatura(faturamento.getNuFatura());
        insumo.setConsiderarFNS("S".equalsIgnoreCase(faturamento.getIcConsiderarFns()));
        insumo.setVigencias(vigencias);
        insumo.setNuEquipe(equipe.getNuEquipe());

        row = addNewRow(sheet);
        addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1,
                faturamento.getNuFatura() + " - " + faturamento.getDeFatura(), //
                CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

        /**
         * Vigência dos Membros do Faturamento
         */
        addBlank(sheet);

        row = addNewRow(sheet);

        int posRowVigencias = row.getRowNum(); // preciso guardar essa posicao

        int posColVigencias = 6;
        
        addMergedRegion(sheet, row, posColVigencias, 14, "Vigência dos Membros do Faturamento", //
                CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

        if (Utils.isNotEmpty(vigencias)) {

            int pos = posColVigencias;

            row = addNewRow(sheet);
            addMergedRegion(sheet, row, pos++, pos+=1, "Perfil", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            pos++;
            addCell(row, pos++, "Dt. Início", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Dt. Final", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Qt. Membros", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Qt. Dias Trab.", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Preço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
            addCell(row, pos++, "Qt. Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

            XSSFCell cellInicioVigencias = null;
            XSSFCell cellFinalVigencias = null;

            for (VigenciaMembrosDTO vigencia : vigencias) {
                pos = posColVigencias;
                row = addNewRow(sheet);
                addMergedRegion(sheet, row, pos++, pos+=1, vigencia.getDePerfil());
                pos++;
                addCell(row, pos++, vigencia.getDtInicioVigenciaMembros());
                addCell(row, pos++, vigencia.getDtFinalVigenciaMembros());
                addCell(row, pos++, vigencia.getQtMembrosEquipe());
                addCell(row, pos++, vigencia.getQtDiasTrabalhados(), CellStylesEnum.DECIMAL);
                
                BigDecimal vrPrecoPerfil = precoEquipeService.getVrPrecoGrupo(equipe.getNuEquipe(), //
                        faturamento.getNuFatura(), vigencia.getNuPerfil()); 
                
                addCell(row, pos++, vrPrecoPerfil, CellStylesEnum.DECIMAL);

                if (cellInicioVigencias == null) {
                    cellInicioVigencias = addCell(row, pos++, vigencia.getQtEfetiva(), CellStylesEnum.DECIMAL5);
                } else {
                    cellFinalVigencias = addCell(row, pos++, vigencia.getQtEfetiva(), CellStylesEnum.DECIMAL5);
                }
                
                insumo.addQtEfetivaPerfil(vigencia.getNuPerfil(), vigencia.getDtInicioVigenciaMembros(), vigencia.getQtEfetiva());
                insumo.addVrPrecoPerfil(vigencia.getNuPerfil(), vigencia.getDtInicioVigenciaMembros(), vrPrecoPerfil);
                
                AusenciaExtraFaturamentoDTO ausenciaExtra = 
                        ausenciExtraService.findBy(vigencia.getNuEquipe(), faturamento.getNuFatura(), vigencia.getNuPerfil());
                
                if(ausenciaExtra == null) {
                    insumo.addQtDiasExtraPerfil(vigencia.getNuPerfil(), BigDecimal.ZERO);
                    insumo.addQtDiasAusenciaPerfil(vigencia.getNuPerfil(), BigDecimal.ZERO);
                } else {
                    insumo.addQtDiasExtraPerfil(vigencia.getNuPerfil(), ausenciaExtra.getQtDiaServicoExtra());
                    insumo.addQtDiasAusenciaPerfil(vigencia.getNuPerfil(), ausenciaExtra.getQtDiaAusencia());
                }
            }

            if (cellFinalVigencias == null) {
                cellFinalVigencias = cellInicioVigencias;
            }

            row = addNewRow(sheet);
            
            insumo.setQtEfetiva(setCellFormula(sheet, row, String.format("sum(%s:%s)", //
                    cellInicioVigencias.getReference(), cellFinalVigencias.getReference()), pos - 1,
                    CellStylesEnum.DECIMAL5));
        }

        /**
         * Dias Trabalhados no Faturamento
         */
        row = getRow(sheet, posRowVigencias++);
        addMergedRegion(sheet, row, 0, 4, "Dias Trabalhados no Faturamento", //
                CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

        row = getRow(sheet, posRowVigencias++);
        addCell(row, 0, "Data Início", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, 1, "Data Final", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, 2, "Dias Faturamento", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, 3, "Dias Extra", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
        addCell(row, 4, "Dias Ausência", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

        row = getRow(sheet, posRowVigencias++);
        addCell(row, 0, faturamento.getDtInicioFatura());
        addCell(row, 1, faturamento.getDtFinalFatura());
        addCell(row, 2, faturamento.getQtDiasFaturamento(), CellStylesEnum.DECIMAL);
        insumo.setQtDiasExtra(addCell(row, 3, faturamento.getQtDiasServicoExtra(), CellStylesEnum.DECIMAL));
        insumo.setQtDiasAusencia(addCell(row, 4, faturamento.getQtDiasAusencia(), CellStylesEnum.DECIMAL));

        // ciclos
        List<CicloTrabalhoDTO> ciclos = cicloTrabalhoService.listar(faturamento.getNuFatura(), equipe.getNuEquipe());

        if (ciclos == null || ciclos.isEmpty()) {

            cellQualidadeProduto = null;
            cellValorAgregado = null;
            cellProdutividade = null;

            addBlank(sheet);

            row = addNewRow(sheet);
            addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, //
                    "Nenhuma Sprint avaliada. Os indicadores não serão apurados.", //
                    CellStylesEnum.TEXT_BOLD);

            addIndicador(indicadoresQualideProduto, faturamento.getNuFatura(), cellQualidadeProduto);
            addIndicador(indicadoresValorAgregado, faturamento.getNuFatura(), cellValorAgregado);
            addIndicador(indicadoresProdutividade, faturamento.getNuFatura(), cellProdutividade);

        }

        int posPrimeiraLinha = 0;
        int posUltimaLinha = 0;

        if (ciclos != null && !ciclos.isEmpty()) {

            for (CicloTrabalhoDTO ciclo : ciclos) {

                if ("S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {

                    addBlank(sheet);

                    LOG.info(
                            String.format("Contrato: %s Equipe: %s Fatura: %s - %s Ciclo: %s - Dt. Alvo: %s", //
                                    equipe.getNuContrato(), equipe.getNuEquipe() + " - " + equipe.getCoEquipe(),
                                    faturamento.getNuFatura(), faturamento.getDeFatura(), //
                                    ciclo.getNuCicloTrabalho(), ciclo.getDtAlvoCicloTrabalho()));

                    CicloTrabalhoDTO diasTrabalhados = cicloTrabalhoService.getDiasTrabalhados(
                            faturamento.getNuFatura(), equipe.getNuEquipe(), ciclo.getNuCicloTrabalho());

                    row = addNewRow(sheet);
                    addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, //
                            "Sprint " + ciclo.getNuCicloTrabalho(),CellStylesEnum.LIGHT_BLUE_ALIGN_CENTER_BOLD);

                    int rowNumTituloSprint = row.getRowNum();

                    addBlank(sheet);

                    posPrimeiraLinha = sheet.getLastRowNum() + 1;

                    row = addNewRow(sheet);
                    addCell(row, 1, "Data Início");
                    addCell(row, 2, ciclo.getDtInicioCicloTrabalho());
                    addCell(row, 4, "Qt. Dias Sprint");
                    if (diasTrabalhados != null) {
                        addCell(row, 5, diasTrabalhados.getQtDiasSprint());
                    } else {
                        addCell(row, 5, "");
                    }
                    addCell(row, 7, "Valor Agregado");
                    cellValorAgregado = addCell(row, 8, "");

                    row = addNewRow(sheet);
                    addCell(row, 1, "Data Alvo");
                    addCell(row, 2, ciclo.getDtAlvoCicloTrabalho());
                    addCell(row, 4, "Qt. Dias Trab.");
                    cellQtDiasTrabalhados = addCell(row, 5, "");
                    addCell(row, 7, "Qualidade Produto");
                    cellQualidadeProduto = addCell(row, 8, "");

                    row = addNewRow(sheet);
                    addCell(row, 1, "Data Final");
                    addCell(row, 2, ciclo.getDtFinalCicloTrabalho());
                    addCell(row, 4, "Tamanho");
                    cellTamanho = addCell(row, 5);
                    addCell(row, 7, "Produtividade");
                    cellProdutividade = addCell(row, 8, "");

                    row = addNewRow(sheet);
                    addCell(row, 1, "Nota do Código");
                    addCell(row, 2, ciclo.getVrNotaQualidadeProduto());
                    addCell(row, 4, "Qt. Efetiva");
                    cellQtEfetiva = addCell(row, 5, "");
                    addCell(row, 7, "");
                    addCell(row, 8, "");

                    row = addNewRow(sheet);
                    addCell(row, 1, "Questionário");
                    addCell(row, 2, ciclo.getVrQuestionarioAvaliacao());
                    addCell(row, 4, "Prod. Esperada");
                    setCellFormula(sheet, row, "Equipe!K5", 5, CellStylesEnum.DECIMAL5);
                    addCell(row, 7, "");
                    addCell(row, 8, "");

                    row = addNewRow(sheet);
                    addCell(row, 1, "Falha Caixa");
                    addCell(row, 2, "NF".equals(ciclo.getCoFalhaCicloTrabalho()) ? "Não" : "Sim", CellStylesEnum.NORMAL_ALIGN_CENTER);
                    addCell(row, 4, "");
                    addCell(row, 5, "");
                    addCell(row, 7, "");
                    addCell(row, 8, "");

                    row = addNewRow(sheet);
                    addCell(row, 1, "Qt. Dias Extra");
                    addCell(row, 2, ciclo.getQtDiasExtra());
                    addCell(row, 4, "Prod. Auferida");
                    cellProdAuferida = addCell(row, 5, BigDecimal.ZERO);
                    addCell(row, 7, "");
                    addCell(row, 8, "");

                    row = addNewRow(sheet);
                    addCell(row, 1, "Qt. Dias Ausência");
                    addCell(row, 2, ciclo.getQtDiasAusencia());
                    addCell(row, 4, "");
                    addCell(row, 5, "");
                    addCell(row, 7, "");
                    addCell(row, 8, "");

                    posUltimaLinha = sheet.getLastRowNum() - 1;

                    addMergedRegion2(sheet, posPrimeiraLinha, posUltimaLinha + 1, 0, "Dados informados");
                    addMergedRegion2(sheet, posPrimeiraLinha, posUltimaLinha + 1, 3, "Calculado");
                    addMergedRegion2(sheet, posPrimeiraLinha, posUltimaLinha + 1, 6, "Indicadores");

                    int pos = 6;

                    addMergedRegion(sheet, getRow(sheet, rowNumTituloSprint + 2), pos, pos + 4, //
                            "Vigência dos membros da Sprint", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

                    row = getRow(sheet, rowNumTituloSprint + 3);
                    addCell(row, pos++, "Início", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                    addCell(row, pos++, "Fim", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                    addCell(row, pos++, "Qt. Membros", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                    addCell(row, pos++, "Qt. Trabalhados", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
                    addCell(row, pos++, "Qt. Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

                    List<VigenciaMembrosSprintDTO> vigenciaMembrosSprint = vigenciasMembrosSprintService
                            .listar(faturamento.getNuFatura(), equipe.getNuEquipe(), ciclo.getNuCicloTrabalho());

                    int posInicialVigencias = row.getRowNum() + 2;
                    int posFinalVigencias = row.getRowNum() + 1;

                    for (VigenciaMembrosSprintDTO d : vigenciaMembrosSprint) {

                        pos = 6;

                        row = getRow(sheet, row.getRowNum() + 1);
                        addCell(row, pos++, d.getDtInicioVigenciaMembros());
                        addCell(row, pos++, d.getDtFinalVigenciaMembros());
                        addCell(row, pos++, d.getQtMembrosEquipe());
                        addCell(row, pos++, d.getQtDiasTrab());
                        addCell(row, pos++, d.getQtEfetiva(), CellStylesEnum.DECIMAL5);

                        posFinalVigencias++;

                    }

                    row = getRow(sheet, row.getRowNum() + 1);

                    if (Utils.isNotEmpty(vigenciaMembrosSprint)) {

                        setCellFormula(sheet, row, "sum(O" + posInicialVigencias + ":O" + posFinalVigencias + ")", 14,
                                CellStylesEnum.DECIMAL5);

                        setCellFormula(cellQtEfetiva, "sum(O" + posInicialVigencias + ":O" + posFinalVigencias + ")",
                                CellStylesEnum.DECIMAL5);

                        setCellFormula(cellQtDiasTrabalhados,
                                "sum(N" + posInicialVigencias + ":N" + posFinalVigencias + ") + " + //
                                        "(C" + (cellQtDiasTrabalhados.getRowIndex() + 6) + " / F"
                                        + (cellQtDiasTrabalhados.getRowIndex() + 3) + ")", //
                                CellStylesEnum.DECIMAL);

                    } else {

                        setCellFormula(sheet, row, "0", 14, CellStylesEnum.DECIMAL5);
                        setCellFormula(cellQtEfetiva, "0", CellStylesEnum.DECIMAL5);
                        setCellFormula(cellQtDiasTrabalhados, "0", CellStylesEnum.DECIMAL);

                    }

                    /**
                     * Solicitações
                     */
                    int posInicialSolicitacoes = 0;
                    pos = 10;
                    row = getRow(sheet, row.getRowNum() + 2);
                    addMergedRegion(sheet, row, pos, pos + 4, "Solicitações", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

                    List<SolicitacaoDTO> solicitacoes = solicitacaoService.listar(equipe.getNuEquipe(),
                            ciclo.getNuCicloTrabalho());
                    for (SolicitacaoDTO s : solicitacoes) {
                        pos = 10;
                        row = getRow(sheet, row.getRowNum() + 1);
                        if (posInicialSolicitacoes == 0) {
                            posInicialSolicitacoes = row.getRowNum() + 1;
                        }
                        addCell(row, pos++, s.getCoSolicitacao(), CellStylesEnum.NORMAL_ALIGN_CENTER);
                        addMergedRegion(sheet, row, pos++, pos + 1, s.getNoTipoServico(), CellStylesEnum.NORMAL_ALIGN_CENTER);
                        addCell(row, pos + 2, s.getNuTamanhoServico(), CellStylesEnum.DECIMAL);
                    }

                    row = getRow(sheet, row.getRowNum() + 1);

                    XSSFCell cellSomaTamanho = addCell(row, 14);

                    setCellFormula(cellSomaTamanho, "sum(O" + posInicialSolicitacoes + ":O" + row.getRowNum() + ")",
                            CellStylesEnum.DECIMAL);

                    setCellFormula(cellTamanho, "sum(O" + posInicialSolicitacoes + ":O" + row.getRowNum() + ")",
                            CellStylesEnum.DECIMAL);

                    setCellFormulaProdAuferida(cellProdAuferida);
                    setCellFormulaProdutividade(cellProdAuferida, cellProdutividade);

                    setCellFormulaValorAgregado(cellValorAgregado);
                    setCellFormulaQualidadeProduto(cellQualidadeProduto);

                    addIndicador(indicadoresQualideProduto, faturamento.getNuFatura(), cellQualidadeProduto);
                    addIndicador(indicadoresValorAgregado, faturamento.getNuFatura(), cellValorAgregado);
                    addIndicador(indicadoresProdutividade, faturamento.getNuFatura(), cellProdutividade);

                }

            }

        } // ciclos

        if ("S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {

            /**
             * Média dos Indicadores
             */
            addBlank(sheet);

            int posRowMediaIndicadores = sheet.getLastRowNum();

            row = getRow(sheet, posRowMediaIndicadores + 1);
            addMergedRegion(sheet, row, 0, 4, "Média dos Indicadores", CellStylesEnum.ORANGE_ALIGN_CENTER_BOLD);

            row = getRow(sheet, posRowMediaIndicadores + 2);
            addMergedRegion(sheet, row, 0, 3, "Valor Agregado", CellStylesEnum.NORMAL_ALIGN_CENTER);
            cellMediaValorAgregado = addCell(row, 4, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

            row = getRow(sheet, posRowMediaIndicadores + 3);
            addMergedRegion(sheet, row, 0, 3, "Qualidade Produto", CellStylesEnum.NORMAL_ALIGN_CENTER);
            cellMediaQualidadeProduto = addCell(row, 4, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

            row = getRow(sheet, posRowMediaIndicadores + 4);
            addMergedRegion(sheet, row, 0, 3, "Produtividade", CellStylesEnum.NORMAL_ALIGN_CENTER);
            cellMediaProdutividade = addCell(row, 4, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

            if (getMediaIndicador(indicadoresProdutividade, faturamento.getNuFatura(), indicadorFNS) != null) {
                setCellFormula(cellMediaProdutividade, //
                        "average(" + getMediaIndicador(indicadoresProdutividade, faturamento.getNuFatura(), indicadorFNS) + ")", //
                        CellStylesEnum.DECIMAL5);
            }

            if (getMediaIndicador(indicadoresQualideProduto, faturamento.getNuFatura(), indicadorFNS) != null) {
                setCellFormula(cellMediaQualidadeProduto, //
                        "average(" + getMediaIndicador(indicadoresQualideProduto, faturamento.getNuFatura(), indicadorFNS) + ")", //
                        CellStylesEnum.DECIMAL5);
            }

            if (getMediaIndicador(indicadoresValorAgregado, faturamento.getNuFatura(), indicadorFNS) != null) {
                setCellFormula(cellMediaValorAgregado, //
                        "average(" + getMediaIndicador(indicadoresValorAgregado, faturamento.getNuFatura(), indicadorFNS) + ")", //
                        CellStylesEnum.DECIMAL5);
            }

            /**
             * Desempenho / Valor FNS
             */
            row = getRow(sheet, posRowMediaIndicadores + 1);
            addCell(row, 7, "Desempenho",  CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);
            addCell(row, 8, "Valor FNS",  CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);

            row = getRow(sheet, posRowMediaIndicadores + 2);
            cellDesempenho = addCell(row, 7, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
            cellValorFNS = addCell(row, 8, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

            setCellFormulaDesempenho(cellDesempenho, faturamento.getNuFatura());
            setCellFormulaValorFNS(cellValorFNS, cellDesempenho, qtFaixas);
            insumo.setVrFns(cellValorFNS);

        }

        addBlank(sheet);

        result.put(faturamento.getNuFatura(), insumo);

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

    private void setCellFormulaDesempenho(XSSFCell cell, Long nuFatura) {

        StringBuilder s1 = new StringBuilder();
        StringBuilder s2 = new StringBuilder();

        String ieqvl = parseFormulaDesempenho(indicadoresValorAgregado, nuFatura, "Equipe!J3");
        if (ieqvl != null) {
            s1.append("+" + ieqvl.toString());
            s2.append("," + "Equipe!J3");
        }

        String ieqnp = parseFormulaDesempenho(indicadoresQualideProduto, nuFatura, "Equipe!J4");
        if (ieqvl != null) {
            s1.append("+" + ieqnp.toString());
            s2.append("," + "Equipe!J4");
        }

        String ieqpr = parseFormulaDesempenho(indicadoresProdutividade, nuFatura, "Equipe!J5");
        if (ieqpr != null) {
            s1.append("+" + ieqpr.toString());
            s2.append("," + "Equipe!J5");
        }

        if (s1.length() > 1) {
            setCellFormula(cell, //
                    String.format("(%s)/sum(%s)", s1.toString().substring(1), s2.toString().substring(1)), //
                    CellStylesEnum.DECIMAL5);
        }
    }

    private String parseFormulaDesempenho(Map<Long, List<XSSFCell>> lista, Long nuFatura, String cellPesoIndicador) {

        StringBuilder p1 = new StringBuilder();

        List<XSSFCell> indicadores = null;

        indicadores = getIndicadoresMediaFNS(lista, nuFatura, 3);
        for (XSSFCell c : indicadores) {
            try {
                // if (c != null && c.getNumericCellValue() != 0) {
                if (c != null && c.getCellType() == CellType.FORMULA) {
                    p1.append("," + c.getReference());
                }
            } catch (Exception e) {
            }
        }

        if (p1.length() > 1) {
            return String.format("(average(%s)*%s)", p1.toString().substring(1), cellPesoIndicador);
        }

        return null;
    }

    private void setCellFormulaProdutividade(XSSFCell cell1, XSSFCell cell2) {

        String formula = "if(%s > Equipe!L5, Equipe!L5, %s)";

        formula = String.format(formula, cell1.getReference(), cell1.getReference());

        setCellFormula(cell2, formula, CellStylesEnum.DECIMAL5);
    }

    private void setCellFormulaProdAuferida(XSSFCell cell) {

        StringBuilder f = new StringBuilder();

        f.append("if(");

        f.append("(");
        f.append("F" + (cell.getRow().getRowNum() - 3) + "*");
        f.append("F" + (cell.getRow().getRowNum() - 2) + "*");
        f.append("F" + (cell.getRow().getRowNum() - 4) + "*");
        f.append("F" + (cell.getRow().getRowNum() - 1));
        f.append(")<>0,");

        f.append("(");
        f.append("F" + (cell.getRow().getRowNum() - 3) + "/");
        f.append("F" + (cell.getRow().getRowNum() - 2) + "/");
        f.append("F" + (cell.getRow().getRowNum() - 4) + "/");
        f.append("F" + (cell.getRow().getRowNum() - 1));
        f.append(")");

        f.append(",0)");

        setCellFormula(cell, f.toString(), CellStylesEnum.DECIMAL5);
    }

    private void setCellFormulaQualidadeProduto(XSSFCell cell) {
        setCellFormula(cell, ("C" + (cell.getRow().getRowNum() + 3)) + "*Equipe!K4", //
                CellStylesEnum.DECIMAL5);
    }

    private void setCellFormulaValorAgregado(XSSFCell cell) {
        setCellFormula(cell, ("C" + (cell.getRow().getRowNum() + 5)) + "*Equipe!K3", //
                CellStylesEnum.DECIMAL5);
    }

}
