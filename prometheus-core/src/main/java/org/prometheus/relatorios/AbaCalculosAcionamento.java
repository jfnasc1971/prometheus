package org.prometheus.relatorios;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.EntregaDTO;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.dto.VigenciaMembrosDTO;
import org.prometheus.service.EntregaService;
import org.prometheus.service.EquipeService;
import org.prometheus.service.FaixaIndicadorService;
import org.prometheus.service.PrecosEquipeService;
import org.prometheus.service.VigenciaMembrosService;
import org.prometheus.utils.DateUtil;
import org.prometheus.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaCalculosAcionamento extends Relatorio {

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

	Map<Long, List<XSSFCell>> listaIEQQL = null;
	Map<Long, List<XSSFCell>> listaIEQQPZ = null;
	Map<Long, List<XSSFCell>> listaIEQQET = null;

	Map<Long, List<XSSFCell>> listaMediaIEQQL = null;
	Map<Long, List<XSSFCell>> listaMediaIEQQPZ = null;
	Map<Long, List<XSSFCell>> listaMediaIEQQET = null;
	
	public Map<Long, InsumosFaturamentoDTO> write(XSSFWorkbook wb, String coEquipe, List<FaturamentoDTO> faturamentos)
			throws InvalidFormatException, IOException {

		Map<Long, InsumosFaturamentoDTO> result = new LinkedHashMap<Long, InsumosFaturamentoDTO>();

		EquipeDTO equipe = equipeService.findBy(coEquipe);

		XSSFSheet sheet = wb.createSheet(Relatorio.ABA_CALCULOS);

		carregarFontes(wb);
		carregarEstilos(wb);

		sheet.setZoom(90);
		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 75);

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

			System.out.println(String.format("Calculos - Contrato: %s Equipe: %s Fatura: %s - %s", //
					equipe.getNuContrato(), equipe.getNuEquipe(), faturamento.getNuFatura(),
					faturamento.getDeFatura()));

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

			addMergedRegion(sheet, row, 6, 10, "Vigência dos Membros do Faturamento", //
					ALIGN_CENTER_GREY_40_PERCENT_BOLD);

			List<VigenciaMembrosDTO> vigencias = vigenciasMembrosService.listar(faturamento.getNuFatura(),
					equipe.getNuEquipe());

			XSSFCell cellQtEfetiva = null;

			if (Utils.isNotEmpty(vigencias)) {

				int pos = 6;

				row = addNewRow(sheet);
				addCell(row, pos++, "Dt. Início", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
				addCell(row, pos++, "Dt. Final", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
				addCell(row, pos++, "Qt. Membros", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
				addCell(row, pos++, "Qt. Dias Trab.", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
				addCell(row, pos++, "Qt. Efetiva", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

				XSSFCell cellInicioVigencias = null;
				XSSFCell cellFinalVigencias = null;

				for (VigenciaMembrosDTO vigencia : vigencias) {
					pos = 6;
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

				cellQtEfetiva = setCellFormula(sheet, row, String.format("sum(%s:%s)", //
						cellInicioVigencias.getReference(), cellFinalVigencias.getReference()), pos - 1,
						STYLE_DECIMAL_5DIGITS);

				insumo.setQtEfetiva(cellQtEfetiva);
			}

			/**
			 * Dias Trabalhados no Faturamento
			 */
			row = getRow(sheet, posRowVigencias++);
			addMergedRegion(sheet, row, 0, 4, "Dias Trabalhados no Faturamento", //
					ALIGN_CENTER_GREY_25_PERCENT_BOLD);

			row = getRow(sheet, posRowVigencias++);
			addCell(row, 0, "Dt. Início", ALIGN_CENTER_BOLD);
			addCell(row, 1, "Dt. Final", ALIGN_CENTER_BOLD);
			addCell(row, 2, "Qt. Dias", ALIGN_CENTER_BOLD);
			addCell(row, 3, "Qt. Dias Extra", ALIGN_CENTER_BOLD);
			addCell(row, 4, "Qt. Dias Ausência", ALIGN_CENTER_BOLD);

			row = getRow(sheet, posRowVigencias++);
			addCell(row, 0, faturamento.getDtInicioFatura());
			addCell(row, 1, faturamento.getDtFinalFatura());
			addCell(row, 2, faturamento.getQtDiasFaturamento(), STYLE_DECIMAL);
			insumo.setQtDiasExtra(addCell(row, 3, faturamento.getQtDiasServicoExtra(), STYLE_DECIMAL));
			insumo.setQtDiasAusencia(addCell(row, 4, faturamento.getQtDiasAusencia(), STYLE_DECIMAL));

			int rownum = cellQtEfetiva.getRowIndex() + 1;

			//
			addNewRow(sheet);

			XSSFCell cellQtServicosEntregues = listarServicosEntregues(equipe, sheet, faturamento);
			XSSFCell cellQtServicosDevolvidos = listarServicosDevolvidos(equipe, sheet, faturamento);
			XSSFCell cellQtServicosEntreguesComAtraso = listarServicosAtrasados(equipe, sheet, faturamento);
			XSSFCell cellQtServicosEntreguesEstoque = listarServicosEntreguesEstoque(equipe, sheet, faturamento);
			XSSFCell cellQtServicosContratados = listarServicosContratados(equipe, sheet, faturamento);

			/**
			 * Indicadores
			 */
			if (!"S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {
				getRow(sheet, rownum++); // blank

				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 4, 7, "Grupo configurado para aplicar Indicadores somente a partir de "
						+ DateUtil.format(faturamento.getDtConsiderarFns()), ALIGN_CENTER_GREY_40_PERCENT_BOLD);
			}

			if ("S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {

				getRow(sheet, rownum++); // blank

				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 4, 6, "Indicadores", ALIGN_CENTER_GREY_40_PERCENT_BOLD);

				/**
				 * IEQQL - Qualidade do Serviço
				 */
				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 4, 5, "IEQQL - Qualidade do Serviço", ALIGN_LEFT);

				XSSFCell cellFormulaIEQQL = addCell(row, 6, "N/A", ALIGN_CENTER);

				if ("NUMERIC".equals(cellQtServicosEntregues.getCellType().name())
						&& cellQtServicosEntregues.getNumericCellValue() != 0) {
					setCellFormula(cellFormulaIEQQL, //
							String.format("%s/(%s+%s)", //
									cellQtServicosEntregues.getReference(), //
									cellQtServicosEntregues.getReference(), cellQtServicosDevolvidos.getReference()), //
							STYLE_DECIMAL_5DIGITS);
				}

				addIndicador(listaIEQQL, faturamento.getNuFatura(), cellFormulaIEQQL);

				/**
				 * IEQQPZ - Prazo do Serviço
				 */
				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 4, 5, "IEQQPZ - Prazo do Serviço", ALIGN_LEFT);

				XSSFCell cellFormulaIEQQPZ = addCell(row, 6, "N/A", ALIGN_CENTER);

				if ("NUMERIC".equals(cellQtServicosEntregues.getCellType().name())
						&& cellQtServicosEntregues.getNumericCellValue() != 0) {
					setCellFormula(cellFormulaIEQQPZ, //
							String.format("1-(%s/%s)", cellQtServicosEntreguesComAtraso.getReference(),
									cellQtServicosEntregues.getReference()), //
							STYLE_DECIMAL_5DIGITS);
				}

				addIndicador(listaIEQQPZ, faturamento.getNuFatura(), cellFormulaIEQQPZ);

				/**
				 * IEQQET - Estoque do Serviço
				 */
				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 4, 5, "IEQQET - Estoque do Serviço", ALIGN_LEFT);

				XSSFCell cellFormulaIEQQET = addCell(row, 6, "N/A", ALIGN_CENTER);

				if ("NUMERIC".equals(cellQtServicosContratados.getCellType().name())
						&& cellQtServicosContratados.getNumericCellValue() != 0) {
					setCellFormula(cellFormulaIEQQET, //
							String.format("%s/%s", cellQtServicosEntreguesEstoque.getReference(),
									cellQtServicosContratados.getReference()), //
							STYLE_DECIMAL_5DIGITS);
				}

				addIndicador(listaIEQQET, faturamento.getNuFatura(), cellFormulaIEQQET);

				/**
				 * Média dos Indicadores
				 */
				rownum = cellQtEfetiva.getRowIndex() + 2;

				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 8, 10, "Média dos Indicadores", ALIGN_CENTER_ORANGE_BOLD);

				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 8, 9, "IEQQL - Qualidade do Serviço");
				cellFormulaIEQQL = addCell(row, 10, "N/A", ALIGN_CENTER);
				if (getMediaIndicador(listaIEQQL, faturamento.getNuFatura(), 3) != null) {
					setCellFormula(cellFormulaIEQQL, //
							"average(" + getMediaIndicador(listaIEQQL, faturamento.getNuFatura(), 3) + ")", //
							STYLE_DECIMAL_5DIGITS);
				}
				addIndicador(listaMediaIEQQL, faturamento.getNuFatura(), cellFormulaIEQQL);

				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 8, 9, "IEQQPZ - Prazo do Serviço");
				cellFormulaIEQQPZ = addCell(row, 10, "N/A", ALIGN_CENTER);
				if (getMediaIndicador(listaIEQQPZ, faturamento.getNuFatura(), 3) != null) {
					setCellFormula(cellFormulaIEQQPZ, //
							"average(" + getMediaIndicador(listaIEQQPZ, faturamento.getNuFatura(), 3) + ")", //
							STYLE_DECIMAL_5DIGITS);
				}
				addIndicador(listaMediaIEQQPZ, faturamento.getNuFatura(), cellFormulaIEQQPZ);

				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 8, 9, "IEQQET - Estoque do Serviço");
				cellFormulaIEQQET = addCell(row, 10, "N/A", ALIGN_CENTER);
				if (getMediaIndicador(listaIEQQET, faturamento.getNuFatura(), 3) != null) {
					setCellFormula(cellFormulaIEQQET, //
							"average(" + getMediaIndicador(listaIEQQET, faturamento.getNuFatura(), 3) + ")", //
							STYLE_DECIMAL_5DIGITS);
				}
				addIndicador(listaMediaIEQQET, faturamento.getNuFatura(), cellFormulaIEQQET);

				/**
				 * Desempenho / Valor FNS
				 */
				rownum = cellQtEfetiva.getRowIndex() + 7;

				row = getRow(sheet, rownum++);
				addCell(row, 8, "Desempenho", ALIGN_CENTER_GREEN_BOLD);
				addCell(row, 9, "Valor FNS", ALIGN_CENTER_GREEN_BOLD);

				row = getRow(sheet, rownum++);

				XSSFCell cellDesempenho = addCell(row, 8, "N/A", ALIGN_CENTER);
				XSSFCell cellValorFNS = addCell(row, 9, "N/A", ALIGN_CENTER);

				getRow(sheet, rownum++); // blank

				setCellFormulaDesempenho(cellDesempenho, faturamento.getNuFatura());
				setCellFormulaValorFNS(cellValorFNS, cellDesempenho, qtFaixas);
				insumo.setVrFns(cellValorFNS);

			}

			result.put(faturamento.getNuFatura(), insumo);

		} // faturamentos

		return result;
	}

	private XSSFCell listarServicosAtrasados(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

		List<EntregaDTO> servicosEntreguesComAtraso = entregaService
				.listarServicosEntreguesComAtraso(equipe.getNuEquipe(), faturamento.getNuFatura());

		XSSFRow row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 2, "Serviços Entregues com Atraso", //
				ALIGN_CENTER_ORANGE_BOLD);

		row = addNewRow(sheet);
		addCell(row, 0, "Solicitação", ALIGN_LEFT_GREY_25_PERCENT_BOLD);
		addCell(row, 1, "Data Prevista", ALIGN_LEFT_GREY_25_PERCENT_BOLD);
		addCell(row, 2, "Data Efetiva", ALIGN_LEFT_GREY_25_PERCENT_BOLD);

		if (Utils.isEmpty(servicosEntreguesComAtraso)) {
			row = addNewRow(sheet);
			addMergedRegion(sheet, row, 0, 2, "Nenhuma entrega registrada.");
		}

		for (EntregaDTO e : servicosEntreguesComAtraso) {
			row = addNewRow(sheet);
			addCell(row, 0, e.getCoSolicitacao());
			addCell(row, 1, e.getDhPrevistaPendencias());
			addCell(row, 2, e.getDhEfetiva());
		}

		row = addNewRow(sheet);
		addCell(row, 1, "Quantidade", ALIGN_LEFT_GREY_25_PERCENT_BOLD);

		addBlank(sheet);

		XSSFCell cellQtServicosEntreguesComAtraso = addCell(row, 2, Long.valueOf(servicosEntreguesComAtraso.size()));
		return cellQtServicosEntreguesComAtraso;
	}

	private XSSFCell listarServicosEntreguesEstoque(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {
		List<EntregaDTO> servicosEntreguesEstoque = entregaService.listarServicosEntreguesEstoque(equipe.getNuEquipe(),
				faturamento.getNuFatura());

		XSSFRow row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 2, "Serviços Entregues/Estoque", //
				ALIGN_CENTER_GREEN_BOLD);

		row = addNewRow(sheet);
		addCell(row, 0, "Solicitação", ALIGN_LEFT_GREY_25_PERCENT_BOLD);
		addCell(row, 1, "Data Prevista", ALIGN_LEFT_GREY_25_PERCENT_BOLD);
		addCell(row, 2, "Data Efetiva", ALIGN_LEFT_GREY_25_PERCENT_BOLD);

		for (EntregaDTO e : servicosEntreguesEstoque) {
			row = addNewRow(sheet);
			addCell(row, 0, e.getCoSolicitacao());
			addCell(row, 1, e.getDhPrevistaPendencias());
			addCell(row, 2, e.getDhEfetiva());
		}

		row = addNewRow(sheet);
		addCell(row, 1, "Quantidade", ALIGN_LEFT_GREY_25_PERCENT_BOLD);

		addBlank(sheet);

		XSSFCell cellQtServicosEntreguesEstoque = addCell(row, 2, Long.valueOf(servicosEntreguesEstoque.size()));
		return cellQtServicosEntreguesEstoque;

	}

	private XSSFCell listarServicosContratados(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {
		
		List<EntregaDTO> servicosContratados = entregaService.listarServicosContratados(equipe.getNuEquipe(),
				faturamento.getNuFatura(), 19L);

		XSSFRow row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 2, "Serviços Contratados", //
				ALIGN_CENTER_GREY_40_PERCENT_BOLD);

		row = addNewRow(sheet);
		addCell(row, 0, "Solicitação", ALIGN_LEFT_GREY_25_PERCENT_BOLD);
		addCell(row, 1, "Data Prevista", ALIGN_LEFT_GREY_25_PERCENT_BOLD);
		addCell(row, 2, "Data Efetiva", ALIGN_LEFT_GREY_25_PERCENT_BOLD);

		if (Utils.isEmpty(servicosContratados)) {
			row = addNewRow(sheet);
			addMergedRegion(sheet, row, 0, 2, "Nenhuma entrega registrada.");
		}

		for (EntregaDTO e : servicosContratados) {
			row = addNewRow(sheet);
			addCell(row, 0, e.getCoSolicitacao());
			addCell(row, 1, e.getDhPrevistaPendencias());
			addCell(row, 2, e.getDhEfetiva());
		}

		row = addNewRow(sheet);
		addCell(row, 1, "Quantidade", ALIGN_LEFT_GREY_25_PERCENT_BOLD);

		addBlank(sheet);

		XSSFCell cellQtServicosContratados = addCell(row, 2, Long.valueOf(servicosContratados.size()));
		return cellQtServicosContratados;
	}

	private XSSFCell listarServicosDevolvidos(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

		List<EntregaDTO> servicosDevolvidos = entregaService.listarServicosDevolvidos(equipe.getNuEquipe(),
				faturamento.getNuFatura());

		XSSFRow row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 2, "Serviços Devolvidos", //
				ALIGN_CENTER_RED_BOLD);

		row = addNewRow(sheet);
		addCell(row, 0, "Solicitação", ALIGN_LEFT_GREY_25_PERCENT_BOLD);
		addCell(row, 1, "Data Prevista", ALIGN_LEFT_GREY_25_PERCENT_BOLD);
		addCell(row, 2, "Data Efetiva", ALIGN_LEFT_GREY_25_PERCENT_BOLD);

		if (Utils.isEmpty(servicosDevolvidos)) {
			row = addNewRow(sheet);
			addMergedRegion(sheet, row, 0, 2, "Nenhuma entrega registrada.");
		}

		for (EntregaDTO e : servicosDevolvidos) {
			row = addNewRow(sheet);
			addCell(row, 0, e.getCoSolicitacao());
			addCell(row, 1, e.getDhPrevistaPendencias());
			addCell(row, 2, e.getDhEfetiva());
		}

		row = addNewRow(sheet);
		addCell(row, 1, "Quantidade", ALIGN_LEFT_GREY_25_PERCENT_BOLD);

		addBlank(sheet);

		XSSFCell cellQtServicosDevolvidos = addCell(row, 2, Long.valueOf(servicosDevolvidos.size()));
		return cellQtServicosDevolvidos;
	}

	private XSSFCell listarServicosEntregues(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

		List<EntregaDTO> servicosEntregues = entregaService.listarServicosEntregues(equipe.getNuEquipe(),
				faturamento.getNuFatura());

		XSSFRow row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 2, "Serviços Entregues", //
				ALIGN_CENTER_GREEN_BOLD);

		row = addNewRow(sheet);
		addCell(row, 0, "Solicitação", ALIGN_LEFT_GREY_25_PERCENT_BOLD);
		addCell(row, 1, "Data Prevista", ALIGN_LEFT_GREY_25_PERCENT_BOLD);
		addCell(row, 2, "Data Efetiva", ALIGN_LEFT_GREY_25_PERCENT_BOLD);

		if (Utils.isEmpty(servicosEntregues)) {
			row = addNewRow(sheet);
			addMergedRegion(sheet, row, 0, 2, "Nenhuma entrega registrada.");
		}

		for (EntregaDTO e : servicosEntregues) {
			row = addNewRow(sheet);
			addCell(row, 0, e.getCoSolicitacao());
			addCell(row, 1, e.getDhPrevistaPendencias());
			addCell(row, 2, e.getDhEfetiva());
		}

		row = addNewRow(sheet);
		addCell(row, 1, "Quantidade", ALIGN_LEFT_GREY_25_PERCENT_BOLD);

		addBlank(sheet);

		XSSFCell cellQtServicosEntregues = addCell(row, 2, Long.valueOf(servicosEntregues.size()));
		return cellQtServicosEntregues;
	}

	private void setCellFormulaDesempenho(XSSFCell cell, Long nuFatura) {

		StringBuilder s1 = new StringBuilder();
		StringBuilder s2 = new StringBuilder();

		String ieqql = parseFormulaDesempenho(listaMediaIEQQL, nuFatura, "Equipe!J3");
		if (ieqql != null) {
			s1.append("+" + ieqql.toString());
			s2.append("," + "Equipe!J3");
		}

		String ieqqpz = parseFormulaDesempenho(listaMediaIEQQPZ, nuFatura, "Equipe!J4");
		if (ieqqpz != null) {
			s1.append("+" + ieqqpz.toString());
			s2.append("," + "Equipe!J4");
		}

		String ieqqet = parseFormulaDesempenho(listaMediaIEQQET, nuFatura, "Equipe!J5");
		if (ieqqet != null) {
			s1.append("+" + ieqqet.toString());
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
			return String.format("(average(%s)*%s)", p1.toString().substring(1), cellPesoIndicador);
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
				formula.append(String.format("+if(and(round(%s)>Equipe!H%s,%s<=Equipe!I%s),Equipe!J%s)", //
						cellDesempenho.getReference(), pos + i, //
						cellDesempenho.getReference(), pos + i, //
						pos + i));

			}

			setCellFormula(cellValorFNS, formula.toString().substring(1), STYLE_DECIMAL);

		}

	}

}
