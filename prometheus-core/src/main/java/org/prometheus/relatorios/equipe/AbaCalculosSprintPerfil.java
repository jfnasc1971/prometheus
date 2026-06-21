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
import org.prometheus.dto.VigenciaMembrosDTO;
import org.prometheus.enums.CellStylesEnum;
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

		this.context = new RelatorioContext(equipe, wb, sheet);

		carregarFontes(wb);
		carregarEstilos(wb);

		sheet.setZoom(90);
		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, DEFAULT_COLUMNS_WIDTH);

		setColumnWidth(sheet, 9, 5);

		int qtFaixas = faixaIndicadorService.listar(equipe.getNuEquipe()).size();

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

		context.addEmptyRow();
		
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

		context.addEmptyRow();
	}
	
	private void processarFaturamento(EquipeDTO equipe, FaturamentoDTO faturamento, XSSFRow rowReferencia,
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
		addCell(row, 2, "Dias Faturamento", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

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
		addCell(row, 5, "Qt. Dias Ausência", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 6, "Qt. Dias Extra", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 7, "Vr. Dia Ausência", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		addCell(row, 8, "Vr. Dia Extra", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

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

		int posColVigencias = 10;
		int rownum = rowReferencia.getRowNum() + 2;
		
		row = context.getRow(rownum++);
		
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, posColVigencias, LAYOUT_COLUMNS_COUNT - 1,
				"Vigência dos Membros", //
				CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		if (Utils.isNotEmpty(vigencias)) {

			int pos = posColVigencias;

			row = context.getRow(rownum++);
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
				
				row = context.getRow(rownum++);
				
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

			row = context.getRow(rownum++);

			addCell(row, pos - 2, "Valor Membros", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			XSSFCell cellVrMembros = addCell(row, pos - 1, BigDecimal.ZERO, CellStylesEnum.DECIMAL);
			if (cellInicioVrPrevisto != null && cellFinalVrPrevisto != null) {
				setCellFormula(cellVrMembros, String.format("sum(%s:%s)", //
						cellInicioVrPrevisto.getReference(), cellFinalVrPrevisto.getReference()), //
						CellStylesEnum.DECIMAL);
			}

			row = context.getRow(rownum++);

			addCell(row, pos - 2, "APEC", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			XSSFCell cellVrExtras = addCell(row, pos - 1, BigDecimal.ZERO, CellStylesEnum.DECIMAL);
			if (cellInicioVrExtras != null) {
				setCellFormula(cellVrExtras, String.format("sum(%s:%s)", //
						cellInicioVrExtras.getReference(), cellFinalVrExtras.getReference()), //
						CellStylesEnum.DECIMAL);
			} else {
				addCell(row, pos - 1, BigDecimal.ZERO);
			}

			row = context.getRow(rownum++);

			addCell(row, pos - 2, "DPEC", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			XSSFCell cellVrAusencias = addCell(row, pos - 1, BigDecimal.ZERO, CellStylesEnum.DECIMAL);
			if (cellInicioVrAusencias != null) {
				setCellFormula(cellVrAusencias, String.format("sum(%s:%s)", //
						cellInicioVrAusencias.getReference(), cellFinalVrAusencias.getReference()), //
						CellStylesEnum.DECIMAL);
			} else {
				addCell(row, pos - 1, BigDecimal.ZERO);
			}

			row = context.getRow(rownum++);

			addCell(row, pos - 2, "Valor Previsto", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			setCellFormula(context.getSheet(), row, String.format("sum(%s:%s:%s)", //
					cellVrMembros.getReference(), cellVrExtras.getReference(), cellVrAusencias.getReference()), pos - 1, //
					CellStylesEnum.DECIMAL);
			
		}
		
		// define a linha do proximo faturamento
		if(cellFinalVrAusencias != null && cellFinalVrAusencias.getRow().getRowNum() > rownum) {
			context.setRownum(cellFinalVrAusencias.getRow().getRowNum());
		} else if (rownum > context.getRownum()) {
			context.setRownum(rownum);	
		}
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

				LOG.info(String.format("Contrato: %s Equipe: %s Fatura: %s - %s Ciclo: %s - Dt. Alvo: %s", //
						equipe.getNuContrato(), equipe.getNuEquipe() + " - " + equipe.getCoEquipe(),
						faturamento.getNuFatura(), faturamento.getDeFatura(), //
						ciclo.getNuCicloTrabalho(), ciclo.getDtAlvoCicloTrabalho()));

				CicloTrabalhoDTO diasTrabalhados = cicloTrabalhoService.getDiasTrabalhados(faturamento.getNuFatura(),
						equipe.getNuEquipe(), ciclo.getNuCicloTrabalho());

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
