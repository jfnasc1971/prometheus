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
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.dto.VigenciaMembrosDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.relatorios.RelatorioEquipe;
import org.prometheus.service.EntregaService;
import org.prometheus.service.EquipeService;
import org.prometheus.service.FaixaIndicadorService;
import org.prometheus.service.IndicadorService;
import org.prometheus.service.PrecosEquipeService;
import org.prometheus.service.VigenciaMembrosService;
import org.prometheus.utils.DateUtil;
import org.prometheus.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaCalculosAcionamento extends Relatorio {

    private static final Logger LOG = LogManager.getLogger(AbaCalculosAcionamento.class.getSimpleName());
    
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
    private IndicadorService indicadorService;
	
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

		IndicadorContratoDTO indicadorFNS = indicadorService.getIndicadorContratoFNS(coEquipe);
		
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
		listaMediaIEQQL = new LinkedHashMap<Long, List<XSSFCell>>();
		listaMediaIEQQPZ = new LinkedHashMap<Long, List<XSSFCell>>();
		listaMediaIEQQET = new LinkedHashMap<Long, List<XSSFCell>>();

		int qtFaixas = faixaIndicadorService.listar(equipe.getNuEquipe()).size();

		// faturamentos
		for (FaturamentoDTO faturamento : faturamentos) {

			LOG.info(String.format("Memoria de Calculo - Contrato: %s Equipe: %s Fatura: %s - %s", //
					equipe.getNuContrato(), equipe.getNuEquipe() + " - " + equipe.getCoEquipe(), //
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
			addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT - 1,
					"Fatura: " + faturamento.getNuFatura() + " - " + faturamento.getDeFatura(), //
					CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

			/**
			 * Vigência dos Membros do Faturamento
			 */
			addBlank(sheet);

			row = addNewRow(sheet);

			int posRowVigencias = row.getRowNum(); // preciso guardar essa posicao

			addMergedRegion(sheet, row, 6, 10, "Vigência dos Membros do Faturamento", //
					CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

			XSSFCell cellQtEfetiva = null;

			if (Utils.isNotEmpty(vigencias)) {

				int pos = 6;

				row = addNewRow(sheet);
				addCell(row, pos++, "Dt. Início", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
				addCell(row, pos++, "Dt. Final", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
				addCell(row, pos++, "Qt. Membros", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
				addCell(row, pos++, "Qt. Dias Trab.", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
				addCell(row, pos++, "Qt. Efetiva", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

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
						cellInicioVigencias = addCell(row, pos++, vigencia.getQtEfetiva(), CellStylesEnum.DECIMAL5);
					} else {
						cellFinalVigencias = addCell(row, pos++, vigencia.getQtEfetiva(), CellStylesEnum.DECIMAL5);
					}
				}

				if (cellFinalVigencias == null) {
					cellFinalVigencias = cellInicioVigencias;
				}

				row = addNewRow(sheet);

				cellQtEfetiva = setCellFormula(sheet, row, String.format("sum(%s:%s)", //
						cellInicioVigencias.getReference(), cellFinalVigencias.getReference()), pos - 1,
						CellStylesEnum.DECIMAL5);

				insumo.setQtEfetiva(cellQtEfetiva);
			}

			/**
			 * Dias Trabalhados no Faturamento
			 */
			row = getRow(sheet, posRowVigencias++);
			addMergedRegion(sheet, row, 0, 4, "Dias Trabalhados no Faturamento", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

			row = getRow(sheet, posRowVigencias++);
			addCell(row, 0, "Dt. Início", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD); 
			addCell(row, 1, "Dt. Final", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
			addCell(row, 2, "Qt. Dias", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
			addCell(row, 3, "Qt. Dias Extra", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
			addCell(row, 4, "Qt. Dias Ausência", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

			row = getRow(sheet, posRowVigencias++);
			addCell(row, 0, faturamento.getDtInicioFatura());
			addCell(row, 1, faturamento.getDtFinalFatura());
			addCell(row, 2, faturamento.getQtDiasFaturamento(), CellStylesEnum.DECIMAL);
			insumo.setQtDiasExtra(addCell(row, 3, faturamento.getQtDiasServicoExtra(), CellStylesEnum.DECIMAL));
			insumo.setQtDiasAusencia(addCell(row, 4, faturamento.getQtDiasAusencia(), CellStylesEnum.DECIMAL));

			//
			addNewRow(sheet);

			row = getRow(sheet, cellQtEfetiva.getRowIndex() + 2);
			addMergedRegion(sheet, row, 0, 1, "Serviços", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

			XSSFCell cellQtServicosEntregues = contarServicosEntregues(equipe, sheet, faturamento);
			XSSFCell cellQtServicosDevolvidos = contarServicosDevolvidos(equipe, sheet, faturamento);
			XSSFCell cellQtServicosEntreguesComAtraso = contarServicosAtrasados(equipe, sheet, faturamento);
			XSSFCell cellQtServicosEntreguesEstoque = contarServicosEntreguesEstoque(equipe, sheet, faturamento);
			XSSFCell cellQtServicosContratados = contarServicosContratados(equipe, sheet, faturamento);

			int rownum = cellQtEfetiva.getRowIndex() + 1;

			/**
			 * Indicadores
			 */
			if (!"S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {
				getRow(sheet, rownum++); // blank

				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 4, 7, "Grupo configurado para aplicar Indicadores somente a partir de "
						+ DateUtil.format(faturamento.getDtConsiderarFns()), CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
			}

			if ("S".equalsIgnoreCase(faturamento.getIcConsiderarFns())) {

				getRow(sheet, rownum++); // blank

				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 3, 5, "Indicadores", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

				/**
				 * IEQQL - Qualidade do Serviço
				 */
				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 3, 4, "IEQQL - Qualidade do Serviço", CellStylesEnum.NORMAL_ALIGN_LEFT);

				XSSFCell cellFormulaIEQQL = addCell(row, 5, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

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
				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 3, 4, "IEQQPZ - Prazo do Serviço", CellStylesEnum.NORMAL_ALIGN_LEFT);

				XSSFCell cellFormulaIEQQPZ = addCell(row, 5, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

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
				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 3, 4, "IEQQET - Estoque do Serviço", CellStylesEnum.NORMAL_ALIGN_LEFT);

				XSSFCell cellFormulaIEQQET = addCell(row, 5, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

				if ("NUMERIC".equals(cellQtServicosContratados.getCellType().name())
						&& cellQtServicosContratados.getNumericCellValue() != 0) {
					setCellFormula(cellFormulaIEQQET, //
							String.format("%s/%s", cellQtServicosEntreguesEstoque.getReference(),
									cellQtServicosContratados.getReference()), //
							CellStylesEnum.DECIMAL5);
				}

				addIndicador(listaIEQQET, faturamento.getNuFatura(), cellFormulaIEQQET);

				/**
				 * Média dos Indicadores
				 */
				rownum = cellQtEfetiva.getRowIndex() + 2;

				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 8, 10, "Média dos Indicadores", CellStylesEnum.ORANGE_ALIGN_CENTER_BOLD);

				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 8, 9, "IEQQL - Qualidade do Serviço");
				cellFormulaIEQQL = addCell(row, 10, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
				if (getMediaIndicador(listaIEQQL, faturamento.getNuFatura(), indicadorFNS) != null) {
					setCellFormula(cellFormulaIEQQL, //
							"average(" + getMediaIndicador(listaIEQQL, faturamento.getNuFatura(), indicadorFNS) + ")", //
							CellStylesEnum.DECIMAL5);
				}
				addIndicador(listaMediaIEQQL, faturamento.getNuFatura(), cellFormulaIEQQL);

				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 8, 9, "IEQQPZ - Prazo do Serviço");
				cellFormulaIEQQPZ = addCell(row, 10, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
				if (getMediaIndicador(listaIEQQPZ, faturamento.getNuFatura(), indicadorFNS) != null) {
					setCellFormula(cellFormulaIEQQPZ, //
							"average(" + getMediaIndicador(listaIEQQPZ, faturamento.getNuFatura(), indicadorFNS) + ")", //
							CellStylesEnum.DECIMAL5);
				}
				addIndicador(listaMediaIEQQPZ, faturamento.getNuFatura(), cellFormulaIEQQPZ);

				row = getRow(sheet, rownum++);
				addMergedRegion(sheet, row, 8, 9, "IEQQET - Estoque do Serviço");
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
				rownum = cellQtEfetiva.getRowIndex() + 7;

				row = getRow(sheet, rownum++);
				addCell(row, 8, "Desempenho",  CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);
				addCell(row, 9, "Valor FNS",  CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);

				row = getRow(sheet, rownum++);

				XSSFCell cellDesempenho = addCell(row, 8, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
				XSSFCell cellValorFNS = addCell(row, 9, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

				getRow(sheet, rownum++); // blank

				setCellFormulaDesempenho(cellDesempenho, faturamento.getNuFatura());
				setCellFormulaValorFNS(cellValorFNS, cellDesempenho, qtFaixas);
				insumo.setVrFns(cellValorFNS);

			}

			result.put(faturamento.getNuFatura(), insumo);

		} // faturamentos

		return result;
	}

	private XSSFCell contarServicosAtrasados(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {
		
		List<EntregaDTO> servicosEntreguesComAtraso = entregaService
				.listarServicosEntreguesComAtraso(equipe.getNuEquipe(), faturamento.getNuFatura());

		XSSFRow row = addNewRow(sheet);
		addCell(row, 0, "Atrasados", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		return addCell(row, 1, BigDecimal.valueOf(servicosEntreguesComAtraso.size()));
	}

	private XSSFCell contarServicosEntreguesEstoque(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {
		
		List<EntregaDTO> servicosEntreguesEstoque = entregaService.listarServicosEstoque(equipe.getNuEquipe(),
				faturamento.getNuFatura());

		XSSFRow row = addNewRow(sheet);
		addCell(row, 0, "Estoque", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		return addCell(row, 1, BigDecimal.valueOf(servicosEntreguesEstoque.size()));
	}

	private XSSFCell contarServicosContratados(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {
		
		List<EntregaDTO> servicosContratados = entregaService.listarServicosContratados(equipe.getNuEquipe(),
				faturamento.getNuFatura(), 19L);

		XSSFRow row = addNewRow(sheet);
		addCell(row, 0, "Contratados", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		return addCell(row, 1, BigDecimal.valueOf(servicosContratados.size()));
	}

	private XSSFCell contarServicosDevolvidos(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {
		
		List<EntregaDTO> servicosDevolvidos = entregaService.listarServicosDevolvidos(equipe.getNuEquipe(),
				faturamento.getNuFatura());

		XSSFRow row = addNewRow(sheet);
		addCell(row, 0, "Devolvidos", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		return addCell(row, 1, BigDecimal.valueOf(servicosDevolvidos.size()));
	}

	private XSSFCell contarServicosEntregues(EquipeDTO equipe, XSSFSheet sheet, FaturamentoDTO faturamento) {

		List<EntregaDTO> servicosEntregues = entregaService.listarServicosEntregues(equipe.getNuEquipe(),
				faturamento.getNuFatura());

		XSSFRow row = addNewRow(sheet);
		addCell(row, 0, "Entregues", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

		return addCell(row, 1, BigDecimal.valueOf(servicosEntregues.size()));
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
			setCellFormula(cell, //
					String.format("(%s)/sum(%s)", s1.toString().substring(1), s2.toString().substring(1)), //
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

                formula.append(
                        String.format("+if(and(trunc(%s,2)>=Equipe!H%s,trunc(%s,2)<=Equipe!I%s),Equipe!J%s)", //
                                cellDesempenho.getReference(), pos + i, //
                                cellDesempenho.getReference(), pos + i, //
                                pos + i));
            }

			if (qtFaixas > 0) {
				setCellFormula(cellValorFNS, formula.toString().substring(1), CellStylesEnum.DECIMAL);	
			}
		}

	}

}
