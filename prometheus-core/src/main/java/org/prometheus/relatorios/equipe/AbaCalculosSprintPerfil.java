package org.prometheus.relatorios.equipe;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.prometheus.utils.XSSFSheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaCalculosSprintPerfil extends Relatorio {

	private static final Logger LOG = LogManager.getLogger(AbaCalculosSprintPerfil.class.getSimpleName());

	private static final int LAYOUT_COLUMNS_COUNT = 19;

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

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, DEFAULT_COLUMNS_WIDTH);

		setColumnWidth(sheet, 9, 5);

		XSSFRow row = null;

		int qtFaixas = faixaIndicadorService.listar(equipe.getNuEquipe()).size();

		// faturamentos
		for (FaturamentoDTO faturamento : faturamentos) {

			LOG.info(equipe.getNuContrato() + " - " + equipe.getNoContrato() + //
					" - " + equipe.getNuEquipe() + " - " + equipe.getCoEquipe() + //
					" - " + equipe.getNoTipoGrupo() + //
					" - " + faturamento.getNuFatura() + //
					" (" + faturamento.getDtFinalFatura() + ")");
			
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
		XSSFCell cellQtDiasTrabalhadosSprint = null;

		IndicadorContratoDTO indicadorFNS = indicadorService.getIndicadorContratoFNS(equipe.getCoEquipe());

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
		addCell(row, 2, "Dias Faturamento", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

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
					cellVrMembros.getReference(), cellVrExtras.getReference(), cellVrAusencias.getReference()), pos - 1,//
					CellStylesEnum.DECIMAL);

		}

		// ciclos
		List<CicloTrabalhoDTO> ciclos = cicloTrabalhoService.listar(faturamento.getNuFatura(), equipe.getNuEquipe());

		if (ciclos == null || ciclos.isEmpty()) {

			cellQualidadeProduto = null;
			cellValorAgregado = null;
			cellProdutividade = null;

			addBlank(sheet);

			row = addNewRow(sheet);
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, //
					"Se não houver Sprint no período ou se os indicadores não estiverem configurados, o FNS não será apurado.", //
					CellStylesEnum.NOTA_BOLD);

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

					LOG.info(String.format("Contrato: %s Equipe: %s Fatura: %s - %s Ciclo: %s - Dt. Alvo: %s", //
							equipe.getNuContrato(), equipe.getNuEquipe() + " - " + equipe.getCoEquipe(),
							faturamento.getNuFatura(), faturamento.getDeFatura(), //
							ciclo.getNuCicloTrabalho(), ciclo.getDtAlvoCicloTrabalho()));

					CicloTrabalhoDTO diasTrabalhados = cicloTrabalhoService.getDiasTrabalhados(
							faturamento.getNuFatura(), equipe.getNuEquipe(), ciclo.getNuCicloTrabalho());

					row = addNewRow(sheet);
					XSSFSheetUtils.addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1, //
							"Sprint " + ciclo.getNuCicloTrabalho(), CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

					int rowNumTituloSprint = row.getRowNum();

					addBlank(sheet);

					posPrimeiraLinha = sheet.getLastRowNum() + 1;

					row = addNewRow(sheet);
					addCell(row, 1, "Data Início");
					addCell(row, 2, ciclo.getDtInicioCicloTrabalho());
					
					addCell(row, 4, "Qt. Dias Sprint");
					if (diasTrabalhados != null) {
						cellQtDiasTrabalhadosSprint = addCell(row, 5, diasTrabalhados.getQtDiasSprint());
					} else {
						cellQtDiasTrabalhadosSprint = addCell(row, 5, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
					}
					addCell(row, 7, "Valor Agregado");
					cellValorAgregado = addCell(row, 8, "");

					row = addNewRow(sheet);
					addCell(row, 1, "Data Alvo");
					addCell(row, 2, ciclo.getDtAlvoCicloTrabalho());
					addCell(row, 4, "Qt. Dias Trab.");
					cellQtDiasTrabalhados = addCell(row, 5, "");
					addCell(row, 7, "Qualidade Produto");
					cellQualidadeProduto = addCell(row, 8, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

					row = addNewRow(sheet);
					addCell(row, 1, "Data Final");
					addCell(row, 2, ciclo.getDtFinalCicloTrabalho());
					addCell(row, 4, "Tamanho");
					cellTamanho = addCell(row, 5);
					addCell(row, 7, "Produtividade");
					cellProdutividade = addCell(row, 8, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

					row = addNewRow(sheet);
					addCell(row, 1, "Nota do Código");
					XSSFCell cellVrNotaCodigo = addCell(row, 2, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER); 
					if (ciclo.getVrNotaQualidadeProduto() == null) {
						cellVrNotaCodigo = addCell(row, 2, ciclo.getVrNotaQualidadeProduto(), CellStylesEnum.DECIMAL);
					}
					
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
					addCell(row, 2, "NF".equals(ciclo.getCoFalhaCicloTrabalho()) ? "Não" : "Sim",
							CellStylesEnum.NORMAL_ALIGN_CENTER);
					addCell(row, 4, "");
					addCell(row, 5, "");
					addCell(row, 7, "");
					addCell(row, 8, "");

					row = addNewRow(sheet);
					addCell(row, 1, "Qt. Dias Extra");
					addCell(row, 2, ciclo.getQtDiasExtra());
					addCell(row, 4, "Prod. Auferida");
					cellProdAuferida = addCell(row, 5, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
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

					XSSFSheetUtils.addMergedRegion(sheet, getRow(sheet, rowNumTituloSprint + 2), pos, pos + 4, //
							"Vigência dos membros da Sprint", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

					row = getRow(sheet, rowNumTituloSprint + 3);
					addCell(row, pos++, "Dt. Início", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
					addCell(row, pos++, "Dt. Final", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
					addCell(row, pos++, "Qt. Membros", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
					addCell(row, pos++, "Qt. Trabalhados", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
					addCell(row, pos++, "Qt. Efetiva", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

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
					XSSFSheetUtils.addMergedRegion(sheet, row, pos, pos + 4, "Solicitações",
							CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

					List<SolicitacaoDTO> solicitacoes = solicitacaoService.listar(equipe.getNuEquipe(),
							ciclo.getNuCicloTrabalho());
					for (SolicitacaoDTO s : solicitacoes) {
						pos = 10;
						row = getRow(sheet, row.getRowNum() + 1);
						if (posInicialSolicitacoes == 0) {
							posInicialSolicitacoes = row.getRowNum() + 1;
						}
						addCell(row, pos++, s.getCoSolicitacao(), CellStylesEnum.NORMAL_ALIGN_CENTER);
						XSSFSheetUtils.addMergedRegion(sheet, row, pos++, pos + 1, s.getNoTipoServico(),
								CellStylesEnum.NORMAL_ALIGN_CENTER);
						addCell(row, pos + 2, s.getNuTamanhoServico(), CellStylesEnum.DECIMAL);
					}

					row = getRow(sheet, row.getRowNum() + 1);

					XSSFCell cellSomaTamanho = addCell(row, 14);

					setCellFormula(cellSomaTamanho, "sum(O" + posInicialSolicitacoes + ":O" + row.getRowNum() + ")",
							CellStylesEnum.DECIMAL);

					setCellFormula(cellTamanho, "sum(O" + posInicialSolicitacoes + ":O" + row.getRowNum() + ")",
							CellStylesEnum.DECIMAL);

					if (!"N/A".equalsIgnoreCase(cellQtDiasTrabalhadosSprint.toString())) {
						setCellFormulaProdAuferida(cellProdAuferida);
					}

					setCellFormulaProdutividade(cellProdAuferida, cellProdutividade);
					setCellFormulaValorAgregado(cellValorAgregado);
					setCellFormulaQualidadeProduto(cellQualidadeProduto, cellVrNotaCodigo);

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
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, 4, "Média dos Indicadores",
					CellStylesEnum.ORANGE_ALIGN_CENTER_BOLD);

			row = getRow(sheet, posRowMediaIndicadores + 2);
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, 3, "Valor Agregado", CellStylesEnum.NORMAL_ALIGN_CENTER);
			cellMediaValorAgregado = addCell(row, 4, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

			row = getRow(sheet, posRowMediaIndicadores + 3);
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, 3, "Qualidade Produto", CellStylesEnum.NORMAL_ALIGN_CENTER);
			cellMediaQualidadeProduto = addCell(row, 4, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

			row = getRow(sheet, posRowMediaIndicadores + 4);
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, 3, "Produtividade", CellStylesEnum.NORMAL_ALIGN_CENTER);
			cellMediaProdutividade = addCell(row, 4, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

			if (getMediaIndicador(indicadoresProdutividade, faturamento.getNuFatura(), indicadorFNS) != null) {
				setCellFormula(cellMediaProdutividade, //
						"average("
								+ getMediaIndicador(indicadoresProdutividade, faturamento.getNuFatura(), indicadorFNS)
								+ ")", //
						CellStylesEnum.DECIMAL5);
			}

			if (getMediaIndicador(indicadoresQualideProduto, faturamento.getNuFatura(), indicadorFNS) != null) {
				setCellFormula(cellMediaQualidadeProduto, //
						"average("
								+ getMediaIndicador(indicadoresQualideProduto, faturamento.getNuFatura(), indicadorFNS)
								+ ")", //
						CellStylesEnum.DECIMAL5);
			}

			if (getMediaIndicador(indicadoresValorAgregado, faturamento.getNuFatura(), indicadorFNS) != null) {
				setCellFormula(cellMediaValorAgregado, //
						"average("
								+ getMediaIndicador(indicadoresValorAgregado, faturamento.getNuFatura(), indicadorFNS)
								+ ")", //
						CellStylesEnum.DECIMAL5);
			}

			/**
			 * Desempenho / Valor FNS
			 */
			row = getRow(sheet, posRowMediaIndicadores + 1);
			addCell(row, 7, "Desempenho", CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);
			addCell(row, 8, "Valor FNS", CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);

			row = getRow(sheet, posRowMediaIndicadores + 2);
			cellDesempenho = addCell(row, 7, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
			cellValorFNS = addCell(row, 8, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

			if (cellQtDiasTrabalhadosSprint != null && !"N/A".equalsIgnoreCase(cellQtDiasTrabalhadosSprint.toString())) {	
				setCellFormulaDesempenho(cellDesempenho, faturamento.getNuFatura());
			}
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
		setCellFormula(cell,  cellVrNotaCodigo.getReference() + "*Equipe!K4", //
				CellStylesEnum.DECIMAL5);
	}

	private void setCellFormulaValorAgregado(XSSFCell cell) {
		setCellFormula(cell, ("C" + (cell.getRow().getRowNum() + 5)) + "*Equipe!K3", //
				CellStylesEnum.DECIMAL5);
	}

}
