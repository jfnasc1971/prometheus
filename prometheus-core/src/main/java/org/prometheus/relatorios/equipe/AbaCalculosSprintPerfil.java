package org.prometheus.relatorios.equipe;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
import org.prometheus.indicador.Indicador;
import org.prometheus.indicador.IndicadorFactory;
import org.prometheus.indicador.IndicadorSprintFactory;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioContext;
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
import org.prometheus.utils.DateUtil;
import org.prometheus.utils.Utils;
import org.prometheus.utils.XSSFSheetUtils;
import org.prometheus.utils.XSSFUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaCalculosSprintPerfil extends Relatorio {

	private static final Logger LOG = LogManager.getLogger(AbaCalculosSprintPerfil.class.getSimpleName());

	private static final int LAYOUT_COLUMNS_COUNT = 19;

	private RelatorioContext context;

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
	private IndicadorFactory indicadorFactory;

	@Autowired
	private IndicadorSprintFactory indicadorSprintFactory;

	@Autowired
	private AusenciaExtraFaturamentoService ausenciExtraService;

	private Map<Long, List<XSSFCell>> indicadoresProdutividade;
	private Map<Long, List<XSSFCell>> indicadoresQualideProduto;
	private Map<Long, List<XSSFCell>> indicadoresValorAgregado;

	private List<FaturamentoDTO> faturamentos = new ArrayList<FaturamentoDTO>();

	/**
	 * 
	 * @param wb
	 * @param coEquipe
	 * @param faturamentos
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public Map<Long, InsumosFaturamentoDTO> gerar(XSSFWorkbook wb, String coEquipe, List<FaturamentoDTO> faturamentos)
			throws InvalidFormatException, IOException {

		Map<Long, InsumosFaturamentoDTO> result = new LinkedHashMap<Long, InsumosFaturamentoDTO>();

		EquipeDTO equipe = equipeService.findBy(coEquipe);

		XSSFSheet sheet = wb.createSheet(RelatorioEquipe.ABA_CALCULOS);

		this.context = new RelatorioContext(equipe, wb, sheet);

		carregarFontes(wb);
		carregarEstilos(wb);

		sheet.setZoom(90);
		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, DEFAULT_COLUMNS_WIDTH);

		setColumnWidth(sheet, 9, 5);

		// faturamentos
		for (FaturamentoDTO faturamento : faturamentos) {

			LOG.info(equipe.getNuContrato() + " - " + equipe.getNoContrato() + //
					" - " + equipe.getNuEquipe() + " - " + equipe.getCoEquipe() + //
					" - " + equipe.getNoTipoGrupo() + //
					" - " + faturamento.getNuFatura() + //
					" (" + faturamento.getDtFinalFatura() + ")");

			context.setFaturamento(faturamento);

			XSSFRow rowReferencia = context.nextRow();

			processarFaturamento(equipe, faturamento, rowReferencia, result);

			processarIndicadoresFaturamento(equipe, faturamento, rowReferencia, result);

		}

		return result;
	}

	private void processarIndicadoresFaturamento(EquipeDTO equipe, FaturamentoDTO faturamento, XSSFRow rowReferencia,
			Map<Long, InsumosFaturamentoDTO> result) {

		/**
		 * Indicadores
		 */
		if (!"S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {
			XSSFRow row = context.nextRow();
			XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, 5,
					"Indicadores configurados para serem aplicados somente a partir de "
							+ DateUtil.format(faturamento.getDtConsiderarFns()),
					CellStylesEnum.NOTA_BOLD);
		}

		/**
		 * Indicadores
		 */
		if ("S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {

			List<IndicadorContratoDTO> indicadores = indicadorService.listarIndicadores(equipe.getCoEquipe(), false);

			XSSFRow row = context.nextRow();

			int rowNum = row.getRowNum();

			XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, 3, //
					"Indicadores Apurados", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

			for (IndicadorContratoDTO indicador : indicadores) {
				row = context.nextRow();
				XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, 2, indicador.getDescricao());
				XSSFCell vrIndicador = XSSFSheetUtils.addCell(row, 3, Indicador.NAO_APURADO,
						CellStylesEnum.NORMAL_ALIGN_CENTER);
				faturamento.addIndicador(indicador.getSgTipoIndicador(), vrIndicador);
			}

			context.addEmptyRow();

			processarCiclosTrabalho(equipe, faturamento, result);

			processarMediaIndicadores(equipe, faturamento, result, indicadores, rowNum);
		}

		context.addEmptyRow();
	}

	private void processarMediaIndicadores(EquipeDTO equipe, FaturamentoDTO faturamento,
			Map<Long, InsumosFaturamentoDTO> result, List<IndicadorContratoDTO> indicadores, int rowNum) {

		IndicadorContratoDTO indicadorFNS = indicadorService.getIndicadorFNS(equipe.getCoEquipe());

		int fromIndex = 0;
		int toIndex = this.faturamentos.size();

		if (this.faturamentos.size() > indicadorFNS.getQtFaturamento()) {
			fromIndex = (this.faturamentos.size() - indicadorFNS.getQtFaturamento());
		}

		XSSFRow row = context.getRow(rowNum++);

		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 5, 10, //
				"Média dos Indicadores (" + indicadorFNS.getQtFaturamento() + " meses)", //
				CellStylesEnum.ORANGE_ALIGN_CENTER_BOLD);

		//
		XSSFSheetUtils.addCell(row, 12, "Desempenho", CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);
		XSSFCell cellVrDesempenho = XSSFSheetUtils.addCell(row, 13, Indicador.NAO_APURADO,
				CellStylesEnum.NORMAL_ALIGN_CENTER);

		row = context.getRow(rowNum++);

		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 5, 7, //
				"Indicador", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 8, 9, //
				"Peso", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

		XSSFSheetUtils.addCell(row, 10, "Valor", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

		//
		XSSFSheetUtils.addCell(row, 12, "FNS", CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);
		XSSFCell cellVrFNS = XSSFSheetUtils.addCell(row, 13, Indicador.NAO_APURADO, CellStylesEnum.NORMAL_ALIGN_CENTER);

		StringBuilder formulaDesempenho = new StringBuilder();
		StringBuilder pesos = new StringBuilder();

		for (IndicadorContratoDTO indicador : indicadores) {

			row = context.getRow(rowNum++);

			XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 5, 7, indicador.getDescricao());

			XSSFCell cellVrPesoIndicador = XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 8, 9, //
					indicador.getVrPesoTipoIndicador(), CellStylesEnum.DECIMAL);

			XSSFCell cellVrIndicador = XSSFSheetUtils.addCell(row, 10, Indicador.NAO_APURADO,
					CellStylesEnum.NORMAL_ALIGN_CENTER);

			StringBuilder formula = new StringBuilder();

			for (FaturamentoDTO fa : this.faturamentos.subList(fromIndex, toIndex)) {
				if (XSSFUtils.isApurado(fa.getVrIndicador(indicador.getSgTipoIndicador()))) {

					formula.append(String.format(",%s", //
							fa.getVrIndicador(indicador.getSgTipoIndicador()).getReference()));

				}
			}

			if (Utils.isNotEmpty(formula.toString())) {

				pesos.append("," + cellVrPesoIndicador.getReference());

				cellVrIndicador.setCellFormula(String.format("average(%s)", //
						formula.toString().substring(1)));

				XSSFSheetUtils.setCellStyle(cellVrIndicador, CellStylesEnum.DECIMAL5);

				formulaDesempenho.append(String.format(",%s*%s", //
						cellVrIndicador.getReference(), cellVrPesoIndicador.getReference()));
			}
		}

		if (Utils.isNotEmpty(formulaDesempenho.toString())) {
			cellVrDesempenho.setCellFormula(String.format("sum(%s)/sum(%s)", //
					formulaDesempenho.toString().substring(1), //
					pesos.toString().substring(1)));
			XSSFSheetUtils.setCellStyle(cellVrDesempenho, CellStylesEnum.DECIMAL5);
		}

		setCellFormulaValorFNS(cellVrDesempenho, cellVrFNS);

		result.get(faturamento.getNuFatura()).setVrFns(cellVrFNS);
	}

	private void setCellFormulaValorFNS(XSSFCell cellVrDesempenho, XSSFCell cellVrFNS) {

		int qtFaixas = faixaIndicadorService.listar(context.getEquipe().getNuEquipe()).size();

		// essa posição fixa é o número da primeira linha das faixas
		// da seção "Valores Faixas Indicador" na aba equipe
		int pos = 16;

		if (!"N/A".equalsIgnoreCase(cellVrDesempenho.toString())) {

			StringBuilder formula = new StringBuilder();

			for (int i = 1; i <= qtFaixas; i++) {
				formula.append(String.format("+if(and(trunc(%s,2)>=Equipe!H%s,trunc(%s,2)<=Equipe!I%s),Equipe!J%s)", //
						cellVrDesempenho.getReference(), pos + i, //
						cellVrDesempenho.getReference(), pos + i, //
						pos + i));
			}

			setCellFormula(cellVrFNS, formula.toString().substring(1), CellStylesEnum.DECIMAL);
		}
	}

	private int processarCiclosTrabalho(EquipeDTO equipe, FaturamentoDTO faturamento,
			Map<Long, InsumosFaturamentoDTO> result) {

		context.addEmptyRow();

		// ciclos
		List<CicloTrabalhoDTO> ciclos = cicloTrabalhoService.listar(faturamento.getNuFatura(), equipe.getNuEquipe());

		if (Utils.isEmpty(ciclos)) {

			XSSFRow row = context.nextRow();
			XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, LAYOUT_COLUMNS_COUNT - 1, //
					"Se não houver Sprint no período ou se os indicadores não estiverem configurados, o FNS não será apurado.", //
					CellStylesEnum.NOTA_BOLD);
		}

		if (Utils.isNotEmpty(ciclos)) {

			for (CicloTrabalhoDTO ciclo : ciclos) {

				if ("S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {

					/**
					 * DADOS DA SPRINT
					 */
					LOG.info(String.format("%s - %s - %s - Fatura: %s - %s Ciclo: %s - Dt. Alvo: %s", //
							equipe.getNuContrato(), equipe.getNoContrato(),
							equipe.getNuEquipe() + " - " + equipe.getCoEquipe(), faturamento.getNuFatura(),
							faturamento.getDeFatura(), //
							ciclo.getNuCicloTrabalho(), ciclo.getDtAlvoCicloTrabalho()));

					context.setCiclo(ciclo);

					XSSFRow row = context.nextRow();
					XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, LAYOUT_COLUMNS_COUNT - 1, //
							ciclo.getNoCicloTrabalho(), CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

					int rowInicioDados = context.getRownum();

					context.addEmptyRow();

					row = context.nextRow();
					XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, 1, //
							"Dados da Sprint", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

					row = context.nextRow();
					XSSFSheetUtils.addCell(row, 0, "Data Início");
					XSSFSheetUtils.addCell(row, 1, ciclo.getDtInicioCicloTrabalho());

					row = context.nextRow();
					XSSFSheetUtils.addCell(row, 0, "Data Alvo");
					XSSFSheetUtils.addCell(row, 1, ciclo.getDtAlvoCicloTrabalho());

					row = context.nextRow();
					XSSFSheetUtils.addCell(row, 0, "Data Final");
					XSSFSheetUtils.addCell(row, 1, ciclo.getDtFinalCicloTrabalho());

					row = context.nextRow();
					XSSFSheetUtils.addCell(row, 0, "Tamanho");
					XSSFCell cellTamanho = XSSFSheetUtils.addCell(row, 1, ciclo.getQtDiasSprint());

					row = context.nextRow();
					XSSFSheetUtils.addCell(row, 0, "Falha Caixa");
					XSSFSheetUtils.addCell(row, 1, ciclo.getCoFalhaCicloTrabalho(),
							CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

					row = context.nextRow();
					XSSFSheetUtils.addCell(row, 0, "Qt. Membros");
					XSSFCell cellQtMembrosCiclo = XSSFSheetUtils.addCell(row, 1, ciclo.getQtDiasSprint());

					row = context.nextRow();
					XSSFSheetUtils.addCell(row, 0, "Qt. Dias Sprint.");
					XSSFSheetUtils.addCell(row, 1, ciclo.getQtDiasSprint());

					context.addEmptyRow();

					context.setRownum(rowInicioDados + 1);

					row = context.nextRow();

					/**
					 * VIGENCIA DE MEMBROS DA SPRINT
					 */
					XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 3, 7, //
							"Vigência dos membros da Sprint", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

					int pos = 3;
					row = context.nextRow();
					XSSFSheetUtils.addCell(row, pos++, "Início", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
					XSSFSheetUtils.addCell(row, pos++, "Fim", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
					XSSFSheetUtils.addCell(row, pos++, "Qt. Membros", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
					XSSFSheetUtils.addCell(row, pos++, "Qt. Trabalhados", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
					XSSFSheetUtils.addCell(row, pos++, "Qt. Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

					List<VigenciaMembrosSprintDTO> vigenciaMembrosSprint = vigenciasMembrosSprintService
							.listar(faturamento.getNuFatura(), equipe.getNuEquipe(), ciclo.getNuCicloTrabalho());

					XSSFCell prineiraVigencia = null;
					XSSFCell ultimaVigencia = null;

					for (VigenciaMembrosSprintDTO d : vigenciaMembrosSprint) {

						pos = 3;

						row = context.nextRow();

						XSSFSheetUtils.addCell(row, pos++, d.getDtInicioVigenciaMembros());
						XSSFSheetUtils.addCell(row, pos++, d.getDtFinalVigenciaMembros());
						XSSFSheetUtils.addCell(row, pos++, d.getQtMembrosEquipe());
						XSSFSheetUtils.addCell(row, pos++, d.getQtDiasTrab());

						if (prineiraVigencia == null) {
							prineiraVigencia = XSSFSheetUtils.addCell(row, pos++, d.getQtEfetiva(),
									CellStylesEnum.DECIMAL5);
							ultimaVigencia = prineiraVigencia;
						} else {
							ultimaVigencia = XSSFSheetUtils.addCell(row, pos++, d.getQtEfetiva(),
									CellStylesEnum.DECIMAL5);
						}

					}

					row = context.nextRow();

					if (prineiraVigencia != null) {

						XSSFSheetUtils.setCellFormula(cellQtMembrosCiclo, //
								String.format("sum(%s:%s)", prineiraVigencia.getReference(),
										ultimaVigencia.getReference()), //
								CellStylesEnum.DECIMAL);
					}

					/**
					 * SOLICITACOES
					 */
					context.addEmptyRow();

					row = context.nextRow();

					pos = 0;

					XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 4, //
							"Solicitações", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

					List<SolicitacaoDTO> solicitacoes = solicitacaoService.listar(equipe.getNuEquipe(),
							ciclo.getNuCicloTrabalho());

					row = context.nextRow();

					XSSFSheetUtils.addCell(row, pos++, "Código", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
					XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos++, pos + 1, "Tipo de Serviço",
							CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
					XSSFSheetUtils.addCell(row, pos + 2, "Tamanho", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

					XSSFCell prineiraSolicitacao = null;
					XSSFCell ultimaSolicitacao = null;

					for (SolicitacaoDTO s : solicitacoes) {
						pos = 0;
						row = context.nextRow();
						XSSFSheetUtils.addCell(row, pos++, s.getCoSolicitacao(), CellStylesEnum.NORMAL_ALIGN_CENTER);
						XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos++, pos + 1, s.getNoTipoServico(),
								CellStylesEnum.NORMAL_ALIGN_LEFT);

						if (prineiraSolicitacao == null) {
							prineiraSolicitacao = XSSFSheetUtils.addCell(row, pos + 2, s.getNuTamanhoServico(),
									CellStylesEnum.DECIMAL);
							ultimaSolicitacao = prineiraSolicitacao;
						} else {
							ultimaSolicitacao = XSSFSheetUtils.addCell(row, pos + 2, s.getNuTamanhoServico(),
									CellStylesEnum.DECIMAL);
						}

					}

					if (prineiraSolicitacao != null) {

						XSSFSheetUtils.setCellFormula(cellTamanho, //
								String.format("sum(%s:%s)", prineiraSolicitacao.getReference(),
										ultimaSolicitacao.getReference()), //
								CellStylesEnum.DECIMAL);
					}

					int rowFinalDados = context.getRownum();

					/**
					 * INDICADORES DA SPRINT
					 */
					context.setRownum(rowInicioDados + 1);

					row = context.nextRow();

					pos = 10;

					XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 2, //
							"INDICADORES DA SPRINT", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

					List<IndicadorContratoDTO> indicadores = indicadorService.listarIndicadores(equipe.getCoEquipe(),
							false);

					for (IndicadorContratoDTO indicador : indicadores) {
						Indicador indicadorImpl = indicadorSprintFactory.get(indicador, context);
						XSSFCell vrIndicador = indicadorImpl.calcular();
						ciclo.addIndicador(indicador.getSgTipoIndicador(), vrIndicador);
						row = context.nextRow();
					}

					if (rowFinalDados > context.getRownum()) {
						context.setRownum(rowFinalDados + 1);
					}

					context.addEmptyRow();
				}

				faturamento.getCiclosTrabalho().add(ciclo);

			} // ciclos

		}

		apurarIndicadores(equipe, faturamento);

		context.addEmptyRow();

		return context.getRownum();
	}

	private void apurarIndicadores(EquipeDTO equipe, FaturamentoDTO faturamento) {

		List<IndicadorContratoDTO> indicadores = indicadorService.listarIndicadores(equipe.getCoEquipe(), false);

		for (IndicadorContratoDTO indicador : indicadores) {

			StringBuilder formula = new StringBuilder();

			XSSFCell vrIndicador = faturamento.getVrIndicador(indicador.getSgTipoIndicador());

			int qtCiclos = 0;

			for (CicloTrabalhoDTO ciclo : faturamento.getCiclosTrabalho()) {

				XSSFCell vrIndicadorCiclo = ciclo.getVrIndicador(indicador.getSgTipoIndicador());

				if (XSSFUtils.isApurado(vrIndicadorCiclo)) {
					formula.append("+" + vrIndicadorCiclo.getReference());
					qtCiclos++;
				}
			}

			if (Utils.isNotEmpty(formula.toString())) {

				XSSFSheetUtils.setCellStyle(vrIndicador, CellStylesEnum.DECIMAL5);

				if (qtCiclos == 1) {
					vrIndicador.setCellFormula(formula.toString().substring(1));
				} else {
					vrIndicador.setCellFormula("(" + formula.toString().substring(1) + ")/" + qtCiclos);
				}
			}

		}

		// GUARDAR EM ORDEM PARA PROCESSAR AS MEDIAS DOS INDICADORES
		this.faturamentos.add(faturamento);
	}

	private int processarFaturamento(EquipeDTO equipe, FaturamentoDTO faturamento, XSSFRow rowReferencia,
			Map<Long, InsumosFaturamentoDTO> result) {

		List<VigenciaMembrosDTO> vigencias = vigenciasMembrosService.listar(faturamento.getNuFatura(),
				equipe.getNuEquipe());

		InsumosFaturamentoDTO insumo = new InsumosFaturamentoDTO();

		insumo.setNuFatura(faturamento.getNuFatura());
		insumo.setConsiderarFNS("S".equalsIgnoreCase(faturamento.getIcConsiderarFns()));
		insumo.setVigencias(vigencias);
		insumo.setNuEquipe(equipe.getNuEquipe());

		XSSFSheetUtils.addMergedRegion(context.getSheet(), rowReferencia, 0, LAYOUT_COLUMNS_COUNT - 1,
				faturamento.getNuFatura() + " - " + faturamento.getDeFatura(), //
				CellStylesEnum.LIGHT_BLUE_ALIGN_CENTER_BOLD);

		/**
		 * Dias Trabalhados no Faturamento
		 */
		context.addEmptyRow();

		XSSFRow row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, 2, "Dias Trabalhados no Faturamento", //
				CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = context.nextRow();
		addCell(row, 0, "Data Início", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 1, "Data Final", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 2, "Quantidade", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = context.nextRow();
		addCell(row, 0, faturamento.getDtInicioFatura());
		addCell(row, 1, faturamento.getDtFinalFatura());
		addCell(row, 2, faturamento.getQtDiasFaturamento(), CellStylesEnum.DECIMAL);

		context.nextRow();

		/**
		 * Ausencias e extras
		 */
		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, 8, "Ausências e extras", //
				CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = context.nextRow();
		addCell(row, 0, "Dt. Registro", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 1, 3, "Perfil Profissional",
				CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 4, "Preço", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 5, "Ausências", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 6, "Extras", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 7, "Vr. Ausências", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 8, "Vr. Extras", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		List<AusenciaExtraFaturamentoDTO> ausenciasExtras = ausenciExtraService
				.listarAusenciasExtrasPerfil(equipe.getCoEquipe(), faturamento.getNuFatura());

		if (Utils.isEmpty(ausenciasExtras)) {
			row = context.nextRow();
			XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, 8, "Nenhum lançamento registrado.");
		}

		XSSFCell cellInicioVrAusencias = null;
		XSSFCell cellFinalVrAusencias = null;
		XSSFCell cellInicioVrExtras = null;
		XSSFCell cellFinalVrExtras = null;

		if (Utils.isNotEmpty(ausenciasExtras)) {
			for (AusenciaExtraFaturamentoDTO ausenciaExtra : ausenciasExtras) {
				row = context.nextRow();

				BigDecimal vrPrecoGrupo = precoEquipeService.getVrPrecoGrupo(equipe.getNuEquipe(),
						faturamento.getNuFatura(), ausenciaExtra.getNuPerfil());

				addCell(row, 0, ausenciaExtra.getDhInclusaoInformacao());
				XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 1, 3, ausenciaExtra.getDePerfil());
				addCell(row, 4, vrPrecoGrupo);
				addCell(row, 5, ausenciaExtra.getQtDiaAusencia());
				addCell(row, 6, ausenciaExtra.getQtDiaServicoExtra());

				if (!BigDecimal.ZERO.equals(ausenciaExtra.getQtDiaAusencia()) && vrPrecoGrupo != null) {
					if (cellInicioVrAusencias == null) {
						cellInicioVrAusencias = addCell(row, 7, vrPrecoGrupo.multiply(//
								ausenciaExtra.getQtDiaAusencia().divide(faturamento.getQtDiasFaturamento(), 5,
										RoundingMode.HALF_UP)));
						cellFinalVrAusencias = cellInicioVrAusencias;
					} else {
						cellFinalVrAusencias = addCell(row, 7, vrPrecoGrupo.multiply(//
								ausenciaExtra.getQtDiaAusencia().divide(faturamento.getQtDiasFaturamento(), 5,
										RoundingMode.HALF_UP)));
					}
				}

				if (!BigDecimal.ZERO.equals(ausenciaExtra.getQtDiaServicoExtra()) && vrPrecoGrupo != null) {
					if (cellInicioVrExtras == null) {
						cellInicioVrExtras = addCell(row, 8, vrPrecoGrupo.multiply(//
								ausenciaExtra.getQtDiaServicoExtra().divide(faturamento.getQtDiasFaturamento(), 5,
										RoundingMode.HALF_UP)));
						cellFinalVrExtras = cellInicioVrExtras;
					} else {
						cellFinalVrExtras = addCell(row, 8, vrPrecoGrupo.multiply(//
								ausenciaExtra.getQtDiaServicoExtra().divide(faturamento.getQtDiasFaturamento(), 5,
										RoundingMode.HALF_UP)));
					}
				}
			}
		}

		int ultimaLinhaAusencias = context.getRownum();

		int posColVigencias = 10;
		context.setRownum(rowReferencia.getRowNum() + 2);

		row = context.nextRow();

		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, posColVigencias, LAYOUT_COLUMNS_COUNT - 1,
				"Vigência dos Membros", //
				CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		if (Utils.isNotEmpty(vigencias)) {

			int pos = posColVigencias;

			row = context.nextRow();
			XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos++, pos, "Perfil",
					CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			pos++;
			addCell(row, pos++, "Dt. Início", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Dt. Final", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Qt. Membros", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Qt. Dias Trab.", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Preço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Qt. Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Valor Membro", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

			XSSFCell cellInicioVigencias = null;
			XSSFCell cellFinalVigencias = null;
			XSSFCell cellVrPrecoPerfil = null;
			XSSFCell cellInicioVrPrevisto = null;
			XSSFCell cellFinalVrPrevisto = null;

			for (VigenciaMembrosDTO vigencia : vigencias) {

				pos = posColVigencias;

				row = context.nextRow();

				XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos++, pos, vigencia.getDePerfil());
				pos++;
				addCell(row, pos++, vigencia.getDtInicioVigenciaMembros());
				addCell(row, pos++, vigencia.getDtFinalVigenciaMembros());
				addCell(row, pos++, vigencia.getQtMembrosEquipe());
				addCell(row, pos++, vigencia.getQtDiasTrabalhados(), CellStylesEnum.DECIMAL);

				BigDecimal vrPrecoPerfil = precoEquipeService.getVrPrecoGrupo(equipe.getNuEquipe(), //
						faturamento.getNuFatura(), vigencia.getNuPerfil());

				cellVrPrecoPerfil = addCell(row, pos++, vrPrecoPerfil, CellStylesEnum.DECIMAL);

				if (cellInicioVigencias == null) {
					cellInicioVigencias = addCell(row, pos++, vigencia.getQtEfetiva(), CellStylesEnum.DECIMAL5);
					cellInicioVrPrevisto = setCellFormula(context.getSheet(), row, String.format("%s*%s", //
							cellInicioVigencias.getReference(), cellVrPrecoPerfil.getReference()), pos++,
							CellStylesEnum.DECIMAL);
					cellFinalVrPrevisto = cellInicioVrPrevisto;
				} else {
					cellFinalVigencias = addCell(row, pos++, vigencia.getQtEfetiva(), CellStylesEnum.DECIMAL5);
					cellFinalVrPrevisto = setCellFormula(context.getSheet(), row, String.format("%s*%s", //
							cellFinalVigencias.getReference(), cellVrPrecoPerfil.getReference()), pos++,
							CellStylesEnum.DECIMAL);
				}

				insumo.addQtEfetivaPerfil(vigencia.getNuPerfil(), vigencia.getDtInicioVigenciaMembros(),
						vigencia.getQtEfetiva());
				insumo.addVrPrecoPerfil(vigencia.getNuPerfil(), vigencia.getDtInicioVigenciaMembros(), vrPrecoPerfil);

				AusenciaExtraFaturamentoDTO ausenciaExtra = ausenciExtraService.findBy(vigencia.getNuEquipe(),
						faturamento.getNuFatura(), vigencia.getNuPerfil());

				if (ausenciaExtra == null) {
					insumo.addQtDiasExtraPerfil(vigencia.getNuPerfil(), BigDecimal.ZERO);
					insumo.addQtDiasAusenciaPerfil(vigencia.getNuPerfil(), BigDecimal.ZERO);
				} else {
					insumo.addQtDiasExtraPerfil(vigencia.getNuPerfil(), ausenciaExtra.getQtDiaServicoExtra());
					insumo.addQtDiasAusenciaPerfil(vigencia.getNuPerfil(), ausenciaExtra.getQtDiaAusencia());
				}

			} // vigencias

			if (cellFinalVigencias == null) {
				cellFinalVigencias = cellInicioVigencias;
			}

			row = context.nextRow();

			addCell(row, pos - 2, "Valor Membros", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			XSSFCell cellVrMembros = addCell(row, pos - 1, BigDecimal.ZERO, CellStylesEnum.DECIMAL);
			if (cellInicioVrPrevisto != null && cellFinalVrPrevisto != null) {
				setCellFormula(cellVrMembros, String.format("sum(%s:%s)", //
						cellInicioVrPrevisto.getReference(), cellFinalVrPrevisto.getReference()), //
						CellStylesEnum.DECIMAL);
			}

			row = context.nextRow();

			addCell(row, pos - 2, "APEC", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			XSSFCell cellVrExtras = addCell(row, pos - 1, BigDecimal.ZERO, CellStylesEnum.DECIMAL);
			if (cellInicioVrExtras != null) {
				setCellFormula(cellVrExtras, String.format("sum(%s:%s)", //
						cellInicioVrExtras.getReference(), cellFinalVrExtras.getReference()), //
						CellStylesEnum.DECIMAL);
			} else {
				addCell(row, pos - 1, BigDecimal.ZERO);
			}

			row = context.nextRow();

			addCell(row, pos - 2, "DPEC", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			XSSFCell cellVrAusencias = addCell(row, pos - 1, BigDecimal.ZERO, CellStylesEnum.DECIMAL);
			if (cellInicioVrAusencias != null) {
				setCellFormula(cellVrAusencias, String.format("sum(%s:%s)", //
						cellInicioVrAusencias.getReference(), cellFinalVrAusencias.getReference()), //
						CellStylesEnum.DECIMAL);
			} else {
				addCell(row, pos - 1, BigDecimal.ZERO);
			}

			row = context.nextRow();

			addCell(row, pos - 2, "Valor Previsto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			setCellFormula(context.getSheet(), row, String.format("sum(%s:%s:%s)", //
					cellVrMembros.getReference(), cellVrExtras.getReference(), cellVrAusencias.getReference()), pos - 1, //
					CellStylesEnum.DECIMAL);

		}

		if (ultimaLinhaAusencias > context.getRownum()) {
			context.setRownum(ultimaLinhaAusencias);
		}

		context.addEmptyRow();

		result.put(faturamento.getNuFatura(), insumo);

		return context.getRownum();
	}

	private void processarFaturamento2(EquipeDTO equipe, FaturamentoDTO faturamento, XSSFSheet sheet, XSSFRow row,
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
		XSSFCell cellQtDiasTrabalhadosSprint = null;

		IndicadorContratoDTO indicadorFNS = indicadorService.getIndicadorFNS(equipe.getCoEquipe());

		InsumosFaturamentoDTO insumo = new InsumosFaturamentoDTO();

		insumo.setNuFatura(faturamento.getNuFatura());
		insumo.setConsiderarFNS("S".equalsIgnoreCase(faturamento.getIcConsiderarFns()));
		insumo.setVigencias(vigencias);
		insumo.setNuEquipe(equipe.getNuEquipe());

		row = addNewRow(sheet);
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1,
				faturamento.getNuFatura() + " - " + faturamento.getDeFatura(), //
				CellStylesEnum.LIGHT_BLUE_ALIGN_CENTER_BOLD);

		/**
		 * Vigência dos Membros do Faturamento
		 */
		addBlank(sheet);

		row = addNewRow(sheet);

		int posRowReferencia = row.getRowNum(); // preciso guardar essa posicao

		int posRowVigencias = posRowReferencia;

		/**
		 * Dias Trabalhados no Faturamento
		 */
		row = getRow(sheet, posRowVigencias++);
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, "Dias Trabalhados no Faturamento", //
				CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = getRow(sheet, posRowVigencias++);
		addCell(row, 0, "Data Início", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 1, "Data Final", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 2, "Quantidade", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = getRow(sheet, posRowVigencias++);
		addCell(row, 0, faturamento.getDtInicioFatura());
		addCell(row, 1, faturamento.getDtFinalFatura());
		addCell(row, 2, faturamento.getQtDiasFaturamento(), CellStylesEnum.DECIMAL);

		getRow(sheet, posRowVigencias++);

		/**
		 * Ausencias e extras
		 */
		row = getRow(sheet, posRowVigencias++);
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, 8, "Ausências e extras", //
				CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = getRow(sheet, posRowVigencias++);
		addCell(row, 0, "Dt. Registro", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		XSSFSheetUtils.addMergedRegion(sheet, row, 1, 3, "Perfil Profissional",
				CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 4, "Preço", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 5, "Qt. Dias Ausência", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 6, "Qt. Dias Extra", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 7, "Vr. Dia Ausência", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 8, "Vr. Dia Extra", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		List<AusenciaExtraFaturamentoDTO> ausenciasExtras = ausenciExtraService
				.listarAusenciasExtrasPerfil(equipe.getCoEquipe(), faturamento.getNuFatura());

		if (Utils.isEmpty(ausenciasExtras)) {
			row = getRow(sheet, posRowVigencias++);
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, 8, "Nenhum lançamento registrado.");
		}

		XSSFCell cellInicioVrAusencias = null;
		XSSFCell cellFinalVrAusencias = null;
		XSSFCell cellInicioVrExtras = null;
		XSSFCell cellFinalVrExtras = null;

		if (Utils.isNotEmpty(ausenciasExtras)) {
			for (AusenciaExtraFaturamentoDTO ausenciaExtra : ausenciasExtras) {
				row = getRow(sheet, posRowVigencias++);

				BigDecimal vrPrecoGrupo = precoEquipeService.getVrPrecoGrupo(equipe.getNuEquipe(),
						faturamento.getNuFatura(), ausenciaExtra.getNuPerfil());

				addCell(row, 0, ausenciaExtra.getDhInclusaoInformacao());
				XSSFSheetUtils.addMergedRegion(sheet, row, 1, 3, ausenciaExtra.getDePerfil());
				addCell(row, 4, vrPrecoGrupo);
				addCell(row, 5, ausenciaExtra.getQtDiaAusencia());
				addCell(row, 6, ausenciaExtra.getQtDiaServicoExtra());

				if (!BigDecimal.ZERO.equals(ausenciaExtra.getQtDiaAusencia()) && vrPrecoGrupo != null) {
					if (cellInicioVrAusencias == null) {
						cellInicioVrAusencias = addCell(row, 7, vrPrecoGrupo.multiply(//
								ausenciaExtra.getQtDiaAusencia().divide(faturamento.getQtDiasFaturamento(), 5,
										RoundingMode.HALF_UP)));
						cellFinalVrAusencias = cellInicioVrAusencias;
					} else {
						cellFinalVrAusencias = addCell(row, 7, vrPrecoGrupo.multiply(//
								ausenciaExtra.getQtDiaAusencia().divide(faturamento.getQtDiasFaturamento(), 5,
										RoundingMode.HALF_UP)));
					}
				}

				if (!BigDecimal.ZERO.equals(ausenciaExtra.getQtDiaServicoExtra()) && vrPrecoGrupo != null) {
					if (cellInicioVrExtras == null) {
						cellInicioVrExtras = addCell(row, 8, vrPrecoGrupo.multiply(//
								ausenciaExtra.getQtDiaServicoExtra().divide(faturamento.getQtDiasFaturamento(), 5,
										RoundingMode.HALF_UP)));
						cellFinalVrExtras = cellInicioVrExtras;
					} else {
						cellFinalVrExtras = addCell(row, 8, vrPrecoGrupo.multiply(//
								ausenciaExtra.getQtDiaServicoExtra().divide(faturamento.getQtDiasFaturamento(), 5,
										RoundingMode.HALF_UP)));
					}
				}
			}
		}

		// reset da linha
		posRowVigencias = posRowReferencia;

		row = getRow(sheet, posRowVigencias++);

		int posColVigencias = 10;

		XSSFSheetUtils.addMergedRegion(sheet, row, posColVigencias, LAYOUT_COLUMNS_COUNT - 1, "Vigência dos Membros", //
				CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		if (Utils.isNotEmpty(vigencias)) {

			int pos = posColVigencias;

			row = getRow(sheet, posRowVigencias++);
			XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos, "Perfil", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			pos++;
			addCell(row, pos++, "Dt. Início", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Dt. Final", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Qt. Membros", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Qt. Dias Trab.", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Preço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Qt. Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Valor Membro", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

			XSSFCell cellInicioVigencias = null;
			XSSFCell cellFinalVigencias = null;
			XSSFCell cellVrPrecoPerfil = null;
			XSSFCell cellInicioVrPrevisto = null;
			XSSFCell cellFinalVrPrevisto = null;

			for (VigenciaMembrosDTO vigencia : vigencias) {
				pos = posColVigencias;
				row = getRow(sheet, posRowVigencias++);
				XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos, vigencia.getDePerfil());
				pos++;
				addCell(row, pos++, vigencia.getDtInicioVigenciaMembros());
				addCell(row, pos++, vigencia.getDtFinalVigenciaMembros());
				addCell(row, pos++, vigencia.getQtMembrosEquipe());
				addCell(row, pos++, vigencia.getQtDiasTrabalhados(), CellStylesEnum.DECIMAL);

				BigDecimal vrPrecoPerfil = precoEquipeService.getVrPrecoGrupo(equipe.getNuEquipe(), //
						faturamento.getNuFatura(), vigencia.getNuPerfil());

				cellVrPrecoPerfil = addCell(row, pos++, vrPrecoPerfil, CellStylesEnum.DECIMAL);

				if (cellInicioVigencias == null) {
					cellInicioVigencias = addCell(row, pos++, vigencia.getQtEfetiva(), CellStylesEnum.DECIMAL5);
					cellInicioVrPrevisto = setCellFormula(sheet, row, String.format("%s*%s", //
							cellInicioVigencias.getReference(), cellVrPrecoPerfil.getReference()), pos++,
							CellStylesEnum.DECIMAL);
					cellFinalVrPrevisto = cellInicioVrPrevisto;
				} else {
					cellFinalVigencias = addCell(row, pos++, vigencia.getQtEfetiva(), CellStylesEnum.DECIMAL5);
					cellFinalVrPrevisto = setCellFormula(sheet, row, String.format("%s*%s", //
							cellFinalVigencias.getReference(), cellVrPrecoPerfil.getReference()), pos++,
							CellStylesEnum.DECIMAL);
				}

				insumo.addQtEfetivaPerfil(vigencia.getNuPerfil(), vigencia.getDtInicioVigenciaMembros(),
						vigencia.getQtEfetiva());
				insumo.addVrPrecoPerfil(vigencia.getNuPerfil(), vigencia.getDtInicioVigenciaMembros(), vrPrecoPerfil);

				AusenciaExtraFaturamentoDTO ausenciaExtra = ausenciExtraService.findBy(vigencia.getNuEquipe(),
						faturamento.getNuFatura(), vigencia.getNuPerfil());

				if (ausenciaExtra == null) {
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

			row = getRow(sheet, posRowVigencias++);

			addCell(row, pos - 2, "Valor Membros", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			XSSFCell cellVrMembros = addCell(row, pos - 1, BigDecimal.ZERO, CellStylesEnum.DECIMAL);
			if (cellInicioVrPrevisto != null && cellFinalVrPrevisto != null) {
				setCellFormula(cellVrMembros, String.format("sum(%s:%s)", //
						cellInicioVrPrevisto.getReference(), cellFinalVrPrevisto.getReference()), //
						CellStylesEnum.DECIMAL);
			}

			row = getRow(sheet, posRowVigencias++);
			addCell(row, pos - 2, "APEC", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			XSSFCell cellVrExtras = addCell(row, pos - 1, BigDecimal.ZERO, CellStylesEnum.DECIMAL);
			if (cellInicioVrExtras != null) {
				setCellFormula(cellVrExtras, String.format("sum(%s:%s)", //
						cellInicioVrExtras.getReference(), cellFinalVrExtras.getReference()), //
						CellStylesEnum.DECIMAL);
			} else {
				addCell(row, pos - 1, BigDecimal.ZERO);
			}

			row = getRow(sheet, posRowVigencias++);
			addCell(row, pos - 2, "DPEC", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			XSSFCell cellVrAusencias = addCell(row, pos - 1, BigDecimal.ZERO, CellStylesEnum.DECIMAL);
			if (cellInicioVrAusencias != null) {
				setCellFormula(cellVrAusencias, String.format("sum(%s:%s)", //
						cellInicioVrAusencias.getReference(), cellFinalVrAusencias.getReference()), //
						CellStylesEnum.DECIMAL);
			} else {
				addCell(row, pos - 1, BigDecimal.ZERO);
			}

			row = getRow(sheet, posRowVigencias++);
			addCell(row, pos - 2, "Valor Previsto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			setCellFormula(sheet, row, String.format("sum(%s:%s:%s)", //
					cellVrMembros.getReference(), cellVrExtras.getReference(), cellVrAusencias.getReference()), pos - 1, //
					CellStylesEnum.DECIMAL);

		}

		// ciclos
		List<CicloTrabalhoDTO> ciclos = cicloTrabalhoService.listar(faturamento.getNuFatura(), equipe.getNuEquipe());

		if (Utils.isNotEmpty(ciclos)) {

			addBlank(sheet);

			for (CicloTrabalhoDTO ciclo : ciclos) {

				cellQualidadeProduto = null;
				cellValorAgregado = null;
				cellProdutividade = null;

				LOG.info(String.format("%s - % Equipe: %s - Fatura: %s - %s Ciclo: %s - Dt. Alvo: %s", //
						equipe.getNuContrato(), equipe.getNoContrato(),
						equipe.getNuEquipe() + " - " + equipe.getCoEquipe(), faturamento.getNuFatura(),
						faturamento.getDeFatura(), //
						ciclo.getNuCicloTrabalho(), ciclo.getDtAlvoCicloTrabalho()));

				row = addNewRow(sheet);
				XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, //
						"Sprint " + ciclo.getNuCicloTrabalho(), CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
			}
		} // ciclos

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
				formula.append(String.format("+if(and(trunc(%s,2)>=Equipe!H%s,trunc(%s,2)<=Equipe!I%s),Equipe!J%s)", //
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
		if (ieqnp != null) {
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

		if ("N/A".equalsIgnoreCase(cell1.toString()) || "N/A".equalsIgnoreCase(cell1.toString())) {
			return;
		}

		String formula = "if(%s > Equipe!L5, Equipe!L5, %s)";

		formula = String.format(formula, cell1.getReference(), cell1.getReference());

		setCellFormula(cell2, formula, CellStylesEnum.DECIMAL5);
	}

	private void setCellFormulaProdAuferida(XSSFCell cell) {

		StringBuilder f = new StringBuilder();

		f.append("if(");

		f.append("(");
		f.append("F" + (cell.getRow().getRowNum() - 4) + "*");
		f.append("F" + (cell.getRow().getRowNum() - 3) + "*");
		f.append("F" + (cell.getRow().getRowNum() - 2) + "*");
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

	private void setCellFormulaQualidadeProduto(XSSFCell cell, XSSFCell cellVrNotaCodigo) {
		if ("N/A".equalsIgnoreCase(cellVrNotaCodigo.toString()) || cellVrNotaCodigo.getNumericCellValue() == 0) {
			return;
		}
		setCellFormula(cell, cellVrNotaCodigo.getReference() + "*Equipe!K4", //
				CellStylesEnum.DECIMAL5);
	}

	private void setCellFormulaValorAgregado(XSSFCell cell) {
		setCellFormula(cell, ("C" + (cell.getRow().getRowNum() + 5)) + "*Equipe!K3", //
				CellStylesEnum.DECIMAL5);
	}

}
