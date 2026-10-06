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
import org.prometheus.dto.NotaCapacitacaoAssistido;
import org.prometheus.dto.PerfilDTO;
import org.prometheus.dto.VigenciaMembrosDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioEquipe;
import org.prometheus.service.AusenciaExtraFaturamentoService;
import org.prometheus.service.EntregaServiceUnisys;
import org.prometheus.service.EquipeService;
import org.prometheus.service.FaixaIndicadorService;
import org.prometheus.service.IndicadorService;
import org.prometheus.service.PerfilService;
import org.prometheus.service.PrecosEquipeService;
import org.prometheus.service.VigenciaMembrosService;
import org.prometheus.utils.DateUtil;
import org.prometheus.utils.Utils;
import org.prometheus.utils.XSSFSheetUtils;
import org.prometheus.utils.XSSFUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AbaCalculosAcionamentoUnisys extends Relatorio {

	private static final Logger LOG = LogManager.getLogger(AbaCalculosAcionamentoUnisys.class.getSimpleName());

	private static final int LAYOUT_COLUMNS_COUNT = 12;

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
	private EntregaServiceUnisys entregaService;

	@Autowired
	private IndicadorService indicadorService;

	Map<Long, List<XSSFCell>> listaIEQRSA = null;
	Map<Long, List<XSSFCell>> listaIEQRCP = null;
	Map<Long, List<XSSFCell>> listaIEQLDS = null;
	Map<Long, List<XSSFCell>> listaIEPRZS = null;
	Map<Long, List<XSSFCell>> listaIEETQS = null;

	Map<Long, List<XSSFCell>> listaMediaIEQRSA = null;
	Map<Long, List<XSSFCell>> listaMediaIEQRCP = null;
	Map<Long, List<XSSFCell>> listaMediaIEQLDS = null;
	Map<Long, List<XSSFCell>> listaMediaIEPRZS = null;
	Map<Long, List<XSSFCell>> listaMediaIEETQS = null;

	public void gerar(XSSFWorkbook wb, String coEquipe, List<FaturamentoDTO> faturamentos,
			Map<Long, InsumosFaturamentoDTO> insumos) throws Exception {

		EquipeDTO equipe = equipeService.findBy(coEquipe);

		IndicadorContratoDTO indicadorFNS = indicadorService.getIndicadorFNS(coEquipe);

		XSSFSheet sheet = wb.createSheet(RelatorioEquipe.ABA_CALCULOS);

		carregarFontes(wb);
		carregarEstilos(wb);

		sheet.setZoom(90);
		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);
		setColumnWidth(sheet, 3, 5); // Coluna D
		setColumnWidth(sheet, 4, 100); // Coluna E

		XSSFRow row = null;

		listaIEQRSA = new LinkedHashMap<Long, List<XSSFCell>>();
		listaIEQRCP = new LinkedHashMap<Long, List<XSSFCell>>();
		listaIEQLDS = new LinkedHashMap<Long, List<XSSFCell>>();
		listaIEPRZS = new LinkedHashMap<Long, List<XSSFCell>>();
		listaIEETQS = new LinkedHashMap<Long, List<XSSFCell>>();

		listaMediaIEQRSA = new LinkedHashMap<Long, List<XSSFCell>>();
		listaMediaIEQRCP = new LinkedHashMap<Long, List<XSSFCell>>();
		listaMediaIEQLDS = new LinkedHashMap<Long, List<XSSFCell>>();
		listaMediaIEPRZS = new LinkedHashMap<Long, List<XSSFCell>>();
		listaMediaIEETQS = new LinkedHashMap<Long, List<XSSFCell>>();

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
			XSSFSheetUtils.addMergedRegion(sheet, row, 0, 2, "Dias Trabalhados",
					CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

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
			XSSFSheetUtils.addMergedRegion(sheet, row, 4, 9, "Ausências/Extras",
					CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

			row = getRow(sheet, rowNumSecao2++);
			pos = 4;
			addCell(row, pos++, "Perfil", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Qt. Ausências", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Qt. Extras", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Preço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Valor Ausências", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			addCell(row, pos++, "Valor Extras", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

			List<PerfilDTO> perfis = perfilService.listar(faturamento.getNuFatura(), equipe.getNuEquipe());

			XSSFCell cellVrExtras = null;
			XSSFCell cellVrAusencias = null;
			List<XSSFCell> listaVrAusencias = new ArrayList<XSSFCell>();
			List<XSSFCell> listaVrExtras = new ArrayList<XSSFCell>();

			for (PerfilDTO perfil : perfis) {

				AusenciaExtraFaturamentoDTO ausenciaExtra = ausenciaExtraService.findBy(equipe.getNuEquipe(), //
						faturamento.getNuFatura(), perfil.getNuPerfil());

				row = getRow(sheet, rowNumSecao2++);
				pos = 4;

				addCell(row, pos++, perfil.getDePerfil());

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

				cellvrPrecoGrupo = addCell(row, pos++, vrPrecoGrupo == null ? BigDecimal.ZERO : vrPrecoGrupo,
						CellStylesEnum.DECIMAL);

				listaVrAusencias.add(setCellFormula(sheet, row, //
						String.format("(%s/%s)*%s", cellQtAusencias, cellQtDiasFaturamento, cellvrPrecoGrupo), //
						pos++, //
						CellStylesEnum.DECIMAL));

				listaVrExtras.add(setCellFormula(sheet, row, //
						String.format("(%s/%s)*%s", cellQtExtras, cellQtDiasFaturamento, cellvrPrecoGrupo), //
						pos++, //
						CellStylesEnum.DECIMAL));
			}

			// blank
			row = getRow(sheet, rowNumSecao2++);

			/**
			 * Vigência dos Membros do Faturamento
			 */
			row = getRow(sheet, rowNumSecao2++);
			XSSFSheetUtils.addMergedRegion(sheet, row, 4, 11, "Vigência dos Membros do Faturamento", //
					CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

			List<VigenciaMembrosDTO> vigencias = vigenciasMembrosService.listar(faturamento.getNuFatura(),
					equipe.getNuEquipe());

			if (Utils.isNotEmpty(vigencias)) {

				pos = 4;

				row = addNewRow(sheet);
				addCell(row, pos++, "Perfil", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
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

					insumo.addVrPrecoPerfil(vigencia.getNuPerfil(), vigencia.getDtInicioVigenciaMembros(),
							vrPrecoGrupo);

					pos = 4;
					row = addNewRow(sheet);

					addCell(row, pos++, vigencia.getDePerfil());
					addCell(row, pos++, vigencia.getDtInicioVigenciaMembros());
					addCell(row, pos++, vigencia.getDtFinalVigenciaMembros());
					addCell(row, pos++, vigencia.getQtMembrosEquipe());
					addCell(row, pos++, vigencia.getQtDiasTrabalhados(), CellStylesEnum.DECIMAL);
					listaQtEfetiva.add(addCell(row, pos++, vigencia.getQtEfetiva(), CellStylesEnum.DECIMAL));
					addCell(row, pos++, vrPrecoGrupo, CellStylesEnum.DECIMAL);

					listaValorPrevisto.add(setCellFormula(sheet, row, //
							String.format("%s*%s", row.getCell(row.getLastCellNum() - 2).getReference(), //
									row.getCell(row.getLastCellNum() - 1).getReference()),
							pos++, //
							CellStylesEnum.DECIMAL));

					insumo.addQtEfetivaPerfil(vigencia.getNuPerfil(), vigencia.getDtInicioVigenciaMembros(),
							vigencia.getQtEfetiva());

				}

				row = addNewRow(sheet);

				pos = 8;
				addCell(row, pos++, "Qtd. Membros", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
				insumo.setQtEfetivaTotal(setCellFormula(sheet, row, //
						String.format("sum(%s:%s)", //
								listaQtEfetiva.get(0).getReference(),
								listaQtEfetiva.get(listaValorPrevisto.size() - 1).getReference()), //
						pos++, //
						CellStylesEnum.DECIMAL));

				pos = 10;
				addCell(row, pos++, "Valor Equipe", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
				insumo.setVrEquipe(setCellFormula(sheet, row, //
						String.format("sum(%s:%s)", //
								listaValorPrevisto.get(0).getReference(),
								listaValorPrevisto.get(listaValorPrevisto.size() - 1).getReference()), //
						pos++, //
						CellStylesEnum.DECIMAL));

				if (Utils.isNotEmpty(listaVrExtras)) {
					pos = 10;
					row = addNewRow(sheet);
					addCell(row, pos++, "Extras", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
					cellVrExtras = setCellFormula(sheet, row, //
							"sum(" + listaVrExtras.get(0).getReference() + ":" + //
									listaVrExtras.get(listaVrExtras.size() - 1).getReference() + ")",
							pos++, //
							CellStylesEnum.DECIMAL);
					insumo.setVrExtras(cellVrExtras);
				}

				if (Utils.isNotEmpty(listaVrAusencias)) {
					pos = 10;
					row = addNewRow(sheet);
					addCell(row, pos++, "Ausências", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
					cellVrAusencias = setCellFormula(sheet, row, //
							"sum(" + listaVrAusencias.get(0).getReference() + ":" + //
									listaVrAusencias.get(listaVrAusencias.size() - 1).getReference() + ")",
							pos++, //
							CellStylesEnum.DECIMAL);
					insumo.setVrAusencias(cellVrAusencias);
				}

				pos = 10;
				row = addNewRow(sheet);
				addCell(row, pos++, "Valor Previsto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
				insumo.setVrPrevistoTermo(setCellFormula(sheet, row, //
						"sum(" + listaValorPrevisto.get(0).getReference() + ":" + //
								listaValorPrevisto.get(listaValorPrevisto.size() - 1).getReference() + ") + " + //
								cellVrExtras.getReference() + " - " + //
								cellVrAusencias.getReference(),
						pos++, //
						CellStylesEnum.DECIMAL));

				int rownum = listaValorPrevisto.get(listaValorPrevisto.size() - 1).getRowIndex() + 6;

				/**
				 * Indicadores
				 */
				if (!"S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {

					row = getRow(sheet, rownum++);
					XSSFSheetUtils.addMergedRegion(sheet, row, 4, 7,
							"Grupo configurado para aplicar Indicadores somente a partir de "
									+ DateUtil.format(faturamento.getDtConsiderarFns()),
							CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
				}

				if ("S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {

					row = getRow(sheet, rownum);

					XSSFSheetUtils.addMergedRegion(sheet, row, 0, 1, "Serviços",
							CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);

					XSSFCell cellQtServicosEntregues = contarServicosEntregues(equipe, sheet, faturamento);
					XSSFCell cellQtServicosDevolvidos = contarServicosDevolvidos(equipe, sheet, faturamento);
					XSSFCell cellQtServicosAtrasados = contarServicosAtrasados(equipe, sheet, faturamento);
					XSSFCell cellQtServicosContratados = contarServicosContratados(equipe, sheet, faturamento);

					row = getRow(sheet, rownum++);
					XSSFSheetUtils.addMergedRegion(sheet, row, 4, 6, "Indicadores",
							CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

					/**
					 * IEETQS - ESTOQUE do Serviço
					 */
					row = getRow(sheet, rownum++);
					XSSFSheetUtils.addMergedRegion(sheet, row, 4, 5, "IEETQS - ESTOQUE do Serviço",
							CellStylesEnum.NORMAL_ALIGN_LEFT);

					XSSFCell cellIEETQS = addCell(row, 6, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

					if ("NUMERIC".equals(cellQtServicosContratados.getCellType().name())
							&& cellQtServicosContratados.getNumericCellValue() != 0) {
						setCellFormula(cellIEETQS, //
								String.format("%s/%s", //
										cellQtServicosEntregues.getReference(), //
										cellQtServicosContratados.getReference()), //
								CellStylesEnum.DECIMAL5);
					}

					addIndicador(listaIEETQS, faturamento.getNuFatura(), cellIEETQS);

					/**
					 * IEPRZS - PRAZO do Serviço
					 */
					row = getRow(sheet, rownum++);
					XSSFSheetUtils.addMergedRegion(sheet, row, 4, 5, "IEPRZS - PRAZO do Serviço",
							CellStylesEnum.NORMAL_ALIGN_LEFT);

					XSSFCell cellIEPRZS = addCell(row, 6, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

					if ("NUMERIC".equals(cellQtServicosEntregues.getCellType().name())
							&& cellQtServicosEntregues.getNumericCellValue() != 0) {
						setCellFormula(cellIEPRZS, //
								String.format("1-(%s/%s)", //
										cellQtServicosAtrasados.getReference(), //
										cellQtServicosEntregues.getReference()), //
								CellStylesEnum.DECIMAL5);
					}

					addIndicador(listaIEPRZS, faturamento.getNuFatura(), cellIEPRZS);

					/**
					 * IEQLDS - QUALIDADE dos Serviços
					 */
					row = getRow(sheet, rownum++);
					XSSFSheetUtils.addMergedRegion(sheet, row, 4, 5, "IEQLDS - QUALIDADE dos Serviços",
							CellStylesEnum.NORMAL_ALIGN_LEFT);

					XSSFCell cellIEQLDS = addCell(row, 6, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

					if ("NUMERIC".equals(cellQtServicosEntregues.getCellType().name())
							&& cellQtServicosEntregues.getNumericCellValue() != 0) {
						setCellFormula(cellIEQLDS, //
								String.format("%s/(%s+%s)", //
										cellQtServicosEntregues.getReference(), //
										cellQtServicosEntregues.getReference(), //
										cellQtServicosDevolvidos.getReference()), //
								CellStylesEnum.DECIMAL5);
					}

					addIndicador(listaIEQLDS, faturamento.getNuFatura(), cellIEQLDS);

					/**
					 * IEQRCP - Resultado da Capacitação
					 */
					row = getRow(sheet, rownum++);
					XSSFSheetUtils.addMergedRegion(sheet, row, 4, 5, "IEQRCP - Resultado da Capacitação",
							CellStylesEnum.NORMAL_ALIGN_LEFT);

					XSSFCell cellIEQRCP = addCell(row, 6, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

					XSSFCell notaCapacitacao = getNotaCapacitacao(equipe, sheet, faturamento);

					if ("NUMERIC".equals(notaCapacitacao.getCellType().name())
							&& notaCapacitacao.getNumericCellValue() != 0) {
						setCellFormula(cellIEQRCP, //
								String.format("%s", notaCapacitacao.getReference()), //
								CellStylesEnum.DECIMAL5);
					}

					addIndicador(listaIEQRCP, faturamento.getNuFatura(), cellIEQRCP);

					/**
					 * IEQRSA - Resultado do Serviço Assistido
					 */
					row = getRow(sheet, rownum++);
					XSSFSheetUtils.addMergedRegion(sheet, row, 4, 5, "IEQRSA - Resultado do Serviço Assistido",
							CellStylesEnum.NORMAL_ALIGN_LEFT);

					XSSFCell cellIEQRSA = addCell(row, 6, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

					XSSFCell notaAssistido = getNotaAssistido(equipe, sheet, faturamento);

					if ("NUMERIC".equals(notaAssistido.getCellType().name())
							&& notaAssistido.getNumericCellValue() != 0) {
						setCellFormula(cellIEQRSA, //
								String.format("%s", notaAssistido.getReference()), //
								CellStylesEnum.DECIMAL5);
					}

					addIndicador(listaIEQRSA, faturamento.getNuFatura(), cellIEQRSA);

					/**
					 * Média dos Indicadores
					 */
					rownum = listaValorPrevisto.get(listaValorPrevisto.size() - 1).getRowIndex() + 6;

					row = getRow(sheet, rownum++);
					XSSFSheetUtils.addMergedRegion(sheet, row, 8, 10, "Média dos Indicadores",
							CellStylesEnum.ORANGE_ALIGN_CENTER_BOLD);

					row = getRow(sheet, rownum++);
					XSSFSheetUtils.addMergedRegion(sheet, row, 8, 9, "IEETQS - ESTOQUE do Serviço");
					XSSFCell cellFormulaIEETQS = addCell(row, 10, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
					if (getMediaIndicador(listaIEETQS, faturamento.getNuFatura(), indicadorFNS) != null) {
						setCellFormula(cellFormulaIEETQS, //
								"average(" + getMediaIndicador(listaIEETQS, faturamento.getNuFatura(), indicadorFNS)
										+ ")", //
								CellStylesEnum.DECIMAL5);
					}
					addIndicador(listaMediaIEETQS, faturamento.getNuFatura(), cellFormulaIEETQS);

					row = getRow(sheet, rownum++);
					XSSFSheetUtils.addMergedRegion(sheet, row, 8, 9, "IEPRZS - PRAZO do Serviço");
					XSSFCell cellFormulaIEPRZS = addCell(row, 10, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
					if (getMediaIndicador(listaIEPRZS, faturamento.getNuFatura(), indicadorFNS) != null) {
						setCellFormula(cellFormulaIEPRZS, //
								"average(" + getMediaIndicador(listaIEPRZS, faturamento.getNuFatura(), indicadorFNS)
										+ ")", //
								CellStylesEnum.DECIMAL5);
					}
					addIndicador(listaMediaIEPRZS, faturamento.getNuFatura(), cellFormulaIEPRZS);

					row = getRow(sheet, rownum++);
					XSSFSheetUtils.addMergedRegion(sheet, row, 8, 9, "IEQLDS - QUALIDADE dos Serviços");
					XSSFCell cellFormulaIEQLDS = addCell(row, 10, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
					if (getMediaIndicador(listaIEQLDS, faturamento.getNuFatura(), indicadorFNS) != null) {
						setCellFormula(cellFormulaIEQLDS, //
								"average(" + getMediaIndicador(listaIEQLDS, faturamento.getNuFatura(), indicadorFNS)
										+ ")", //
								CellStylesEnum.DECIMAL5);
					}
					addIndicador(listaMediaIEQLDS, faturamento.getNuFatura(), cellFormulaIEQLDS);

					row = getRow(sheet, rownum++);
					XSSFSheetUtils.addMergedRegion(sheet, row, 8, 9, "IEQRCP - Resultado da Capacitação");
					XSSFCell cellFormulaIEQRCP = addCell(row, 10, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
					if (getMediaIndicador(listaIEQRCP, faturamento.getNuFatura(), indicadorFNS) != null) {
						setCellFormula(cellFormulaIEQRCP, //
								"average(" + getMediaIndicador(listaIEQRCP, faturamento.getNuFatura(), indicadorFNS)
										+ ")", //
								CellStylesEnum.DECIMAL5);
					}
					addIndicador(listaMediaIEQRCP, faturamento.getNuFatura(), cellFormulaIEQRCP);

					row = getRow(sheet, rownum++);
					XSSFSheetUtils.addMergedRegion(sheet, row, 8, 9, "IEQRSA - Resultado do Serviço Assistido");
					XSSFCell cellFormulaIEQRSA = addCell(row, 10, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
					if (getMediaIndicador(listaIEQRSA, faturamento.getNuFatura(), indicadorFNS) != null) {
						setCellFormula(cellFormulaIEQRSA, //
								"average(" + getMediaIndicador(listaIEQRSA, faturamento.getNuFatura(), indicadorFNS)
										+ ")", //
								CellStylesEnum.DECIMAL5);
					}
					addIndicador(listaMediaIEQRSA, faturamento.getNuFatura(), cellFormulaIEQRSA);

					/**
					 * Desempenho / Valor FNS
					 */
					rownum = listaValorPrevisto.get(listaValorPrevisto.size() - 1).getRowIndex() + 13;

					row = getRow(sheet, rownum++);
					addCell(row, 8, "Desempenho", CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);
					addCell(row, 9, "Valor FNS", CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);
					addCell(row, 10, "Valor Financeiro", CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);

					row = getRow(sheet, rownum++);

					XSSFCell cellDesempenho = addCell(row, 8, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
					XSSFCell cellValorFNS = addCell(row, 9, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
					XSSFCell cellValorFinanceiro = addCell(row, 10, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

					setCellFormulaDesempenho(cellDesempenho, faturamento.getNuFatura());
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

		List<EntregaDTO> servicosEntreguesComAtraso = entregaService.listarServicosAtrasados(equipe.getNuEquipe(),
				faturamento.getNuFatura());

		XSSFRow row = addNewRow(sheet);
		addCell(row, 0, "Atrasados", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
		return addCell(row, 1, BigDecimal.valueOf(servicosEntreguesComAtraso.size()));
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

	private XSSFCell getNotaAssistido(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

		NotaCapacitacaoAssistido nota = entregaService.getNotasAssistido(equipe.getNuEquipe(),
				faturamento.getNuFatura());

		XSSFCell result = null;
		XSSFCell cellSomaAvaliacoes = null;
		XSSFCell cellQtAvaliadores = null;
		XSSFCell cellQtPerguntas = null;
		XSSFCell cellVrFator = null;

		XSSFRow row = null;

		row = addNewRow(sheet);
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, 1, "Assistido Período", //
				CellStylesEnum.LIGHT_BLUE_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet);
		addCell(row, 0, "Soma Avaliações", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
		cellSomaAvaliacoes = addCell(row, 1, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
		if (nota != null) {
			cellSomaAvaliacoes = addCell(row, 1, nota.getVrSomaAvaliacoes());
		}

		row = addNewRow(sheet);
		addCell(row, 0, "Qt. Avaliadores", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
		cellQtAvaliadores = addCell(row, 1, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
		if (nota != null) {
			cellQtAvaliadores = addCell(row, 1, nota.getQtAvaliadores());
		}

		row = addNewRow(sheet);
		addCell(row, 0, "Qt. Perguntas", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
		cellQtPerguntas = addCell(row, 1, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
		if (nota != null) {
			cellQtPerguntas = addCell(row, 1, nota.getQtPerguntas());
		}

		row = addNewRow(sheet);
		addCell(row, 0, "Fator", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
		cellVrFator = addCell(row, 1, BigDecimal.valueOf(0.10));

		row = addNewRow(sheet);
		addCell(row, 0, "Resultado", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

		result = addCell(row, 1, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

		if (XSSFUtils.isNumeric(cellSomaAvaliacoes) && //
				XSSFUtils.isNumeric(cellQtAvaliadores) && XSSFUtils.isNumeric(cellQtPerguntas)) {
			setCellFormula(result, //
					String.format("(%s/%s/%s)*%s", //
							cellSomaAvaliacoes.getReference(), //
							cellQtPerguntas.getReference(), //
							cellQtAvaliadores.getReference(), //
							cellVrFator.getReference()), //
					CellStylesEnum.DECIMAL);
		}

		row = addNewRow(sheet);

		return result;
	}

	private XSSFCell getNotaCapacitacao(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

		NotaCapacitacaoAssistido nota = entregaService.getNotasCapacitacao(equipe.getNuEquipe(),
				faturamento.getNuFatura());

		XSSFCell result = null;
		XSSFCell cellSomaAvaliacoes = null;
		XSSFCell cellQtAvaliadores = null;
		XSSFCell cellQtPerguntas = null;
		XSSFCell cellVrFator = null;

		XSSFRow row = addNewRow(sheet);

		row = addNewRow(sheet);
		XSSFSheetUtils.addMergedRegion(sheet, row, 0, 1, "Capacitação Período", //
				CellStylesEnum.LIGHT_BLUE_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet);
		addCell(row, 0, "Soma Avaliações", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
		cellSomaAvaliacoes = addCell(row, 1, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
		if (nota != null) {
			cellSomaAvaliacoes = addCell(row, 1, nota.getVrSomaAvaliacoes());
		}

		row = addNewRow(sheet);
		addCell(row, 0, "Qt. Avaliadores", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
		cellQtAvaliadores = addCell(row, 1, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
		if (nota != null) {
			cellQtAvaliadores = addCell(row, 1, nota.getQtAvaliadores());
		}

		row = addNewRow(sheet);
		addCell(row, 0, "Qt. Perguntas", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
		cellQtPerguntas = addCell(row, 1, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
		if (nota != null) {
			cellQtPerguntas = addCell(row, 1, nota.getQtPerguntas());
		}

		row = addNewRow(sheet);
		addCell(row, 0, "Fator", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
		cellVrFator = addCell(row, 1, BigDecimal.valueOf(0.10));

		row = addNewRow(sheet);
		addCell(row, 0, "Resultado", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

		result = addCell(row, 1, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

		if (XSSFUtils.isNumeric(cellSomaAvaliacoes) && //
				XSSFUtils.isNumeric(cellQtAvaliadores) && XSSFUtils.isNumeric(cellQtPerguntas)) {
			setCellFormula(result, //
					String.format("(%s/%s/%s)*%s", //
							cellSomaAvaliacoes.getReference(), //
							cellQtPerguntas.getReference(), //
							cellQtAvaliadores.getReference(), //
							cellVrFator.getReference()), //
					CellStylesEnum.DECIMAL);
		}

		row = addNewRow(sheet);

		return result;
	}

	private void setCellFormulaDesempenho(XSSFCell cell, Long nuFatura) {

		StringBuilder s1 = new StringBuilder();
		StringBuilder s2 = new StringBuilder();

//		IEQRSA - Resultado do Serviço Assistido		1,00
//		IEQRCP - Resultado da Capacitação		1,00
//		IEQLDS - QUALIDADE dos Serviços		3,00
//		IEPRZS - PRAZO do Serviço		2,50
//		IEETQS - ESTOQUE do Serviço		2,50

		String formula = parseFormulaDesempenho(listaMediaIEQRSA, nuFatura, "Equipe!J3");
		if (formula != null) {
			s1.append("+" + formula.toString());
			s2.append("," + "Equipe!J3");
		}

		formula = parseFormulaDesempenho(listaMediaIEQRCP, nuFatura, "Equipe!J4");
		if (formula != null) {
			s1.append("+" + formula.toString());
			s2.append("," + "Equipe!J4");
		}

		formula = parseFormulaDesempenho(listaMediaIEQLDS, nuFatura, "Equipe!J5");
		if (formula != null) {
			s1.append("+" + formula.toString());
			s2.append("," + "Equipe!J5");
		}

		formula = parseFormulaDesempenho(listaMediaIEPRZS, nuFatura, "Equipe!J6");
		if (formula != null) {
			s1.append("+" + formula.toString());
			s2.append("," + "Equipe!J6");
		}

		formula = parseFormulaDesempenho(listaMediaIEETQS, nuFatura, "Equipe!J7");
		if (formula != null) {
			s1.append("+" + formula.toString());
			s2.append("," + "Equipe!J7");
		}

		if (s1.length() > 1) {
			setCellFormula(cell, //
					String.format("(%s)/sum(%s)", //
							s1.toString().substring(1), //
							s2.toString().substring(1)),
					CellStylesEnum.DECIMAL5);
		}
	}

	private String parseFormulaDesempenho(Map<Long, List<XSSFCell>> lista, Long nuFatura, String cellPesoIndicador) {

		StringBuilder p1 = new StringBuilder();

		List<XSSFCell> indicadores = getIndicadoresMediaFNS(lista, nuFatura, 1);

		for (XSSFCell c : indicadores) {
			try {

				if (c != null && !"N/A".equalsIgnoreCase(c.toString())) {
					p1.append("," + c.getReference());
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

				formula.append(String.format("+if(and(trunc(%s,2)>=Equipe!H%s,trunc(%s,2)<=Equipe!I%s),Equipe!J%s)", //
						cellDesempenho.getReference(), pos + i, //
						cellDesempenho.getReference(), pos + i, //
						pos + i));
			}

			setCellFormula(cellValorFNS, formula.toString().substring(1), CellStylesEnum.DECIMAL);

		}

	}

}
