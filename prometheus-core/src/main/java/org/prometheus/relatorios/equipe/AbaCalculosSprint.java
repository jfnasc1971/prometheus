package org.prometheus.relatorios.equipe;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.CicloTrabalhoDTO;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.dto.SolicitacaoDTO;
import org.prometheus.dto.VigenciaMembrosDTO;
import org.prometheus.dto.VigenciaMembrosSprintDTO;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioEquipe;
import org.prometheus.service.CicloTrabalhoService;
import org.prometheus.service.EquipeService;
import org.prometheus.service.FaixaIndicadorService;
import org.prometheus.service.PrecosEquipeService;
import org.prometheus.service.SolicitacaoService;
import org.prometheus.service.VigenciaMembrosService;
import org.prometheus.service.VigenciaMembrosSprintService;
import org.prometheus.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaCalculosSprint extends Relatorio {

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

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 75);
		setColumnWidth(sheet, 0, 7);
		setColumnWidth(sheet, 3, 7);
		setColumnWidth(sheet, 6, 7);
		setColumnWidth(sheet, 9, 7);

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

		InsumosFaturamentoDTO insumo = new InsumosFaturamentoDTO();

		insumo.setNuFatura(faturamento.getNuFatura());
		insumo.setConsiderarFNS("S".equalsIgnoreCase(faturamento.getIcConsiderarFns()));

		BigDecimal vrPrecoGrupo = precoEquipeService.getVrPrecoGrupoVigente(equipe.getNuEquipe(),
				faturamento.getNuFatura());
		insumo.setVrPreco(vrPrecoGrupo);

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1,
				faturamento.getNuFatura() + " - " + faturamento.getDeFatura(), //
				ALIGN_CENTER_GREY_25_PERCENT_BOLD);

		/**
		 * Vigência dos Membros do Faturamento
		 */
		addBlank(sheet);

		row = addNewRow(sheet);

		int posRowVigencias = row.getRowNum(); // preciso guardar essa posicao

		addMergedRegion(sheet, row, 10, 14, "Vigência dos Membros do Faturamento", //
				ALIGN_CENTER_GREY_40_PERCENT_BOLD);

		if (Utils.isNotEmpty(vigencias)) {

			int pos = 10;

			row = addNewRow(sheet);
			addCell(row, pos++, "Dt. Início", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
			addCell(row, pos++, "Dt. Final", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
			addCell(row, pos++, "Qt. Membros", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
			addCell(row, pos++, "Qt. Dias Trab.", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
			addCell(row, pos++, "Qt. Efetiva", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

			XSSFCell cellInicioVigencias = null;
			XSSFCell cellFinalVigencias = null;

			for (VigenciaMembrosDTO vigencia : vigencias) {
				pos = 10;
				row = addNewRow(sheet);
				addCell(row, pos++, vigencia.getDtInicioVigenciaMembros());
				addCell(row, pos++, vigencia.getDtFinalVigenciaMembros());
				addCell(row, pos++, vigencia.getQtMembrosEquipe());
				addCell(row, pos++, vigencia.getQtDiasTrabalhados(), STYLE_DECIMAL);

				if (cellInicioVigencias == null) {
					cellInicioVigencias = addCell(row, pos++, vigencia.getQtEfetiva(), STYLE_DECIMAL_5DIGITS);
				} else {
					cellFinalVigencias = addCell(row, pos++, vigencia.getQtEfetiva(), STYLE_DECIMAL_5DIGITS);
				}
			}

			if (cellFinalVigencias == null) {
				cellFinalVigencias = cellInicioVigencias;
			}

			row = addNewRow(sheet);
			insumo.setQtEfetiva(setCellFormula(sheet, row, String.format("sum(%s:%s)", //
					cellInicioVigencias.getReference(), cellFinalVigencias.getReference()), pos - 1,
					STYLE_DECIMAL_5DIGITS));
		}

		/**
		 * Dias Trabalhados no Faturamento
		 */
		row = getRow(sheet, posRowVigencias++);
		addMergedRegion(sheet, row, 0, 7, "Dias Trabalhados no Faturamento", //
				ALIGN_CENTER_GREY_25_PERCENT_BOLD);

		row = getRow(sheet, posRowVigencias++);
		addMergedRegion(sheet, row, 0, 1, "Dt. Início", ALIGN_CENTER_BOLD);
		addMergedRegion(sheet, row, 2, 3, "Dt. Final", ALIGN_CENTER_BOLD);
		addCell(row, 4, "Qt. Dias", ALIGN_CENTER_BOLD);
		addCell(row, 5, "Qt. Dias Extra", ALIGN_CENTER_BOLD);
		addMergedRegion(sheet, row, 6, 7, "Qt. Dias Ausência", ALIGN_CENTER_BOLD);

		row = getRow(sheet, posRowVigencias++);
		addMergedRegion(sheet, row, 0, 1, faturamento.getDtInicioFatura(), ALIGN_CENTER);
		addMergedRegion(sheet, row, 2, 3, faturamento.getDtFinalFatura(), ALIGN_CENTER);
		addCell(row, 4, faturamento.getQtDiasFaturamento(), STYLE_DECIMAL);
		insumo.setQtDiasExtra(addCell(row, 5, faturamento.getQtDiasServicoExtra(), STYLE_DECIMAL));
		insumo.setQtDiasAusencia(addMergedRegion(sheet, row, 6, 7, faturamento.getQtDiasAusencia(), STYLE_DECIMAL));

		// ciclos
		List<CicloTrabalhoDTO> ciclos = cicloTrabalhoService.listar(faturamento.getNuFatura(), equipe.getNuEquipe());

		if (ciclos == null || ciclos.isEmpty()) {

			cellQualidadeProduto = null;
			cellValorAgregado = null;
			cellProdutividade = null;

			addBlank(sheet);

			row = addNewRow(sheet);
			addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, //
					"Nenhuma Sprint", ALIGN_CENTER_BLUE_BOLD);

			row = addNewRow(sheet);
			addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, "Não realizou atendimentos.", //
					ALIGN_LEFT);

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

					System.out.println(
							String.format("Calculos - Contrato: %s Equipe: %s Fatura: %s - %s Ciclo: %s - Dt. Alvo: %s", //
									equipe.getNuContrato(), equipe.getNuEquipe() + " - " + equipe.getCoEquipe(), faturamento.getNuFatura(),
									faturamento.getDeFatura(), //
									ciclo.getNuCicloTrabalho(), ciclo.getDtAlvoCicloTrabalho()));

					CicloTrabalhoDTO diasTrabalhados = cicloTrabalhoService.getDiasTrabalhados(
							faturamento.getNuFatura(), equipe.getNuEquipe(), ciclo.getNuCicloTrabalho());

					row = addNewRow(sheet);
					addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, //
							"Sprint " + ciclo.getNuCicloTrabalho(), ALIGN_CENTER_BLUE_BOLD);

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
					setCellFormula(sheet, row, "Equipe!K5", 5, STYLE_DECIMAL_5DIGITS);
					addCell(row, 7, "");
					addCell(row, 8, "");

					row = addNewRow(sheet);
					addCell(row, 1, "Falha Caixa");
					addCell(row, 2, "NF".equals(ciclo.getCoFalhaCicloTrabalho()) ? "Não" : "Sim", ALIGN_CENTER);
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

					int pos = 10;

					addMergedRegion(sheet, getRow(sheet, rowNumTituloSprint + 2), pos, pos + 4, //
							"Vigência dos membros da Sprint", ALIGN_CENTER_GREY_40_PERCENT_BOLD);

					row = getRow(sheet, rowNumTituloSprint + 3);
					addCell(row, pos++, "Início", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
					addCell(row, pos++, "Fim", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
					addCell(row, pos++, "Qt. Membros", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
					addCell(row, pos++, "Qt. Trabalhados", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
					addCell(row, pos++, "Qt. Efetiva", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

					List<VigenciaMembrosSprintDTO> vigenciaMembrosSprint = vigenciasMembrosSprintService
							.listar(faturamento.getNuFatura(), equipe.getNuEquipe(), ciclo.getNuCicloTrabalho());

					int posInicialVigencias = row.getRowNum() + 2;
					int posFinalVigencias = row.getRowNum() + 1;

					for (VigenciaMembrosSprintDTO d : vigenciaMembrosSprint) {

						pos = 10;

						row = getRow(sheet, row.getRowNum() + 1);
						addCell(row, pos++, d.getDtInicioVigenciaMembros());
						addCell(row, pos++, d.getDtFinalVigenciaMembros());
						addCell(row, pos++, d.getQtMembrosEquipe());
						addCell(row, pos++, d.getQtDiasTrab());
						addCell(row, pos++, d.getQtEfetiva(), STYLE_DECIMAL_5DIGITS);

						posFinalVigencias++;

					}

					row = getRow(sheet, row.getRowNum() + 1);

					if (Utils.isNotEmpty(vigenciaMembrosSprint)) {

						setCellFormula(sheet, row, "sum(O" + posInicialVigencias + ":O" + posFinalVigencias + ")", 14,
								STYLE_DECIMAL_5DIGITS);

						setCellFormula(cellQtEfetiva, "sum(O" + posInicialVigencias + ":O" + posFinalVigencias + ")",
								STYLE_DECIMAL_5DIGITS);

						setCellFormula(cellQtDiasTrabalhados,
								"sum(N" + posInicialVigencias + ":N" + posFinalVigencias + ") + " + //
										"(C" + (cellQtDiasTrabalhados.getRowIndex() + 6) + " / F"
										+ (cellQtDiasTrabalhados.getRowIndex() + 3) + ")", //
								STYLE_DECIMAL);

					} else {

						setCellFormula(sheet, row, "0", 14, STYLE_DECIMAL_5DIGITS);
						setCellFormula(cellQtEfetiva, "0", STYLE_DECIMAL_5DIGITS);
						setCellFormula(cellQtDiasTrabalhados, "0", STYLE_DECIMAL);

					}

					/**
					 * Solicitações
					 */
					int posInicialSolicitacoes = 0;
					pos = 10;
					row = getRow(sheet, row.getRowNum() + 2);
					addMergedRegion(sheet, row, pos, pos + 4, "Solicitações", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

					List<SolicitacaoDTO> solicitacoes = solicitacaoService.listar(equipe.getNuEquipe(),
							ciclo.getNuCicloTrabalho());
					for (SolicitacaoDTO s : solicitacoes) {
						pos = 10;
						row = getRow(sheet, row.getRowNum() + 1);
						if (posInicialSolicitacoes == 0) {
							posInicialSolicitacoes = row.getRowNum() + 1;
						}
						addCell(row, pos++, s.getCoSolicitacao(), ALIGN_CENTER);
						addMergedRegion(sheet, row, pos++, pos + 1, s.getNoTipoServico(), ALIGN_CENTER);
						addCell(row, pos + 2, s.getNuTamanhoServico(), STYLE_DECIMAL);
					}

					row = getRow(sheet, row.getRowNum() + 1);

					XSSFCell cellSomaTamanho = addCell(row, 14);

					setCellFormula(cellSomaTamanho, "sum(O" + posInicialSolicitacoes + ":O" + row.getRowNum() + ")",
							STYLE_DECIMAL);

					setCellFormula(cellTamanho, "sum(O" + posInicialSolicitacoes + ":O" + row.getRowNum() + ")",
							STYLE_DECIMAL);

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
			addMergedRegion(sheet, row, 0, 4, "Média dos Indicadores", ALIGN_CENTER_ORANGE_BOLD);

			row = getRow(sheet, posRowMediaIndicadores + 2);
			addMergedRegion(sheet, row, 0, 3, "Valor Agregado", ALIGN_CENTER);
			cellMediaValorAgregado = addCell(row, 4, "N/A", ALIGN_CENTER);

			row = getRow(sheet, posRowMediaIndicadores + 3);
			addMergedRegion(sheet, row, 0, 3, "Qualidade Produto", ALIGN_CENTER);
			cellMediaQualidadeProduto = addCell(row, 4, "N/A", ALIGN_CENTER);

			row = getRow(sheet, posRowMediaIndicadores + 4);
			addMergedRegion(sheet, row, 0, 3, "Produtividade", ALIGN_CENTER);
			cellMediaProdutividade = addCell(row, 4, "N/A", ALIGN_CENTER);

			if (getMediaIndicador(indicadoresProdutividade, faturamento.getNuFatura(), 3) != null) {
				setCellFormula(cellMediaProdutividade, //
						"average(" + getMediaIndicador(indicadoresProdutividade, faturamento.getNuFatura(), 3) + ")", //
						STYLE_DECIMAL_5DIGITS);
			}

			if (getMediaIndicador(indicadoresQualideProduto, faturamento.getNuFatura(), 3) != null) {
				setCellFormula(cellMediaQualidadeProduto, //
						"average(" + getMediaIndicador(indicadoresQualideProduto, faturamento.getNuFatura(), 3) + ")", //
						STYLE_DECIMAL_5DIGITS);
			}

			if (getMediaIndicador(indicadoresValorAgregado, faturamento.getNuFatura(), 3) != null) {
				setCellFormula(cellMediaValorAgregado, //
						"average(" + getMediaIndicador(indicadoresValorAgregado, faturamento.getNuFatura(), 3) + ")", //
						STYLE_DECIMAL_5DIGITS);
			}

			/**
			 * Desempenho / Valor FNS
			 */
			row = getRow(sheet, posRowMediaIndicadores + 1);
			addCell(row, 7, "Desempenho", ALIGN_CENTER_GREEN_BOLD);
			addCell(row, 8, "Valor FNS", ALIGN_CENTER_GREEN_BOLD);

			row = getRow(sheet, posRowMediaIndicadores + 2);
			cellDesempenho = addCell(row, 7, "N/A", ALIGN_CENTER);
			cellValorFNS = addCell(row, 8, "N/A", ALIGN_CENTER);

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
				formula.append(String.format("+if(and(H%s>=Equipe!H%s,H%s<Equipe!I%s),Equipe!J%s)", //
						cellDesempenho.getRowIndex() + 1, pos + i, cellDesempenho.getRowIndex() + 1, pos + i, pos + i));

			}

			setCellFormula(cellValorFNS, formula.toString().substring(1), STYLE_DECIMAL);

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
			//System.out.println(String.format("(%s)/sum(%s)", s1.toString().substring(1), s2.toString().substring(1)));
			setCellFormula(cell, //
					String.format("(%s)/sum(%s)", s1.toString().substring(1), s2.toString().substring(1)), //
					STYLE_DECIMAL_5DIGITS);
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

		setCellFormula(cell2, formula, STYLE_DECIMAL_5DIGITS);
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

		setCellFormula(cell, f.toString(), STYLE_DECIMAL_5DIGITS);
	}

	private void setCellFormulaQualidadeProduto(XSSFCell cell) {
		setCellFormula(cell, ("C" + (cell.getRow().getRowNum() + 3)) + "*Equipe!K4", //
				STYLE_DECIMAL_5DIGITS);
	}

	private void setCellFormulaValorAgregado(XSSFCell cell) {
		setCellFormula(cell, ("C" + (cell.getRow().getRowNum() + 5)) + "*Equipe!K3", //
				STYLE_DECIMAL_5DIGITS);
	}

}
