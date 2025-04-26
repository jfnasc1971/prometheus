package org.prometheus.relatorios.equipe;

import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaixaIndicadorDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.dto.PrecosEquipeDTO;
import org.prometheus.dto.QntdeMembrosEquipeDTO;
import org.prometheus.dto.SituacaoEntregaIndicadorDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.service.EquipeService;
import org.prometheus.service.FaixaIndicadorService;
import org.prometheus.service.IndicadorService;
import org.prometheus.service.PrecosEquipeService;
import org.prometheus.service.QntdeMembrosEquipeService;
import org.prometheus.service.SituacaoEntregaIndicadorService;
import org.prometheus.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class AbaPrecosContrato extends Relatorio {

	private static final int LAYOUT_COLUMNS_COUNT = 20;

	@Autowired
	private EquipeService equipeService;

	@Autowired
	private SituacaoEntregaIndicadorService situacaoEntregaIndicadorService;

	@Autowired
	private QntdeMembrosEquipeService qntdeMembrosEquipeService;

	@Autowired
	private PrecosEquipeService precosEquipeService;

	@Autowired
	private IndicadorService indicadorService;

	@Autowired
	private FaixaIndicadorService faixaIndicadorService;

	public void write(XSSFWorkbook workBook, String coEquipe, List<FaturamentoDTO> faturamentos)
			throws InvalidFormatException, IOException {

		EquipeDTO equipe = equipeService.findBy(coEquipe);

		carregarFontes(workBook);

		carregarEstilos(workBook);

		XSSFSheet sheet = workBook.createSheet("Equipe");

		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 75);
		setColumnWidth(sheet, 6, 10); // Coluna G

		XSSFRow row = addNewRow(sheet);

		int pos = 0;

		addMergedRegion(sheet, row, 0, 4, equipe.getNuEquipe() + " - " + equipe.getCoEquipe(),
				CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 1, "Contrato");
		addMergedRegion(sheet, row, 2, 4, equipe.getNuContrato() + " - " + equipe.getNoContrato());

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 1, "Site");
		addMergedRegion(sheet, row, 2, 4, equipe.getCoSitio());

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 1, "Grupo");
		addMergedRegion(sheet, row, 2, 4, equipe.getNoTipoGrupo());

		// Tipo de Atendimento:
		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 1, "Tipo Atendimento");
		addMergedRegion(sheet, row, 2, 4, equipe.getIcTipoAtendimento());

		// Permite Serviços Extraordinários:
		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 1, "Permite Serviços Extraordinários");
		addMergedRegion(sheet, row, 2, 4, equipe.getIcPermiteServicoExtra());

		// Permite Registrar Ausências:
		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 1, "Permite Registrar Ausências");
		addMergedRegion(sheet, row, 2, 4, equipe.getIcPermiteRegistroAusencia());

		// Calcular Valor Proporcional (Pró-rata):
		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 1, "Considerar para Cálculos Dias");
		addMergedRegion(sheet, row, 2, 4,
				"U".equalsIgnoreCase(equipe.getIcDiasCalculoPagamento()) ? "Úteis" : "Corridos");

		// Aplicar Indicadores a partir de:
		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 1, "Aplicar Indicadores a partir de");
		addMergedRegion(sheet, row, 2, 4, equipe.getDtConsiderarFns());

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 1, "Situação");
		addMergedRegion(sheet, row, 2, 4, equipe.getNoSituacaoEquipe());

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 1, "Data início atendimento");
		addMergedRegion(sheet, row, 2, 4, equipe.getDtInicioAtendimento());

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, 1, "Data final atendimento");
		addMergedRegion(sheet, row, 2, 4, equipe.getDtFinalAtendimento());

		addNewRow(sheet);

		/**
		 * Preços
		 */
		List<PrecosEquipeDTO> precos = precosEquipeService.listar(coEquipe);

		row = addNewRow(sheet);

		if ("D".equals(equipe.getIcDistribuiProfissional())) {
			addMergedRegion(sheet, row, 0, 4, "Preços", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		} else {
			addMergedRegion(sheet, row, 0, 2, "Preços", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		}

		pos = 0;
		row = addNewRow(sheet);

		if ("D".equals(equipe.getIcDistribuiProfissional())) {
			addMergedRegion(sheet, row, pos++, pos++, "Perfil", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		}
		addCell(row, pos++, "Dt. Início", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Dt. Final", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Preço", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

		for (PrecosEquipeDTO preco : precos) {

			pos = 0;
			row = addNewRow(sheet);

			if ("D".equals(equipe.getIcDistribuiProfissional())) {
				addMergedRegion(sheet, row, pos++, pos++, preco.getDePerfil());
			}
			addCell(row, pos++, preco.getDtInicioVigencia());
			addCell(row, pos++, preco.getDtFinalVigencia());
			addCell(row, pos++, preco.getVrPrecoGrupo(), CellStylesEnum.DECIMAL);

		}

		addNewRow(sheet);

		/**
		 * Vigências de Membros da Equipe
		 */
		List<QntdeMembrosEquipeDTO> membros = qntdeMembrosEquipeService.listar(coEquipe);

		pos = 0;
		row = addNewRow(sheet);

		if ("D".equals(equipe.getIcDistribuiProfissional())) {
			addMergedRegion(sheet, row, 0, 4, "Vigências de Membros da Equipe", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		} else {
			addMergedRegion(sheet, row, 0, 2, "Vigências de Membros da Equipe", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		}

		row = addNewRow(sheet);
		if ("D".equals(equipe.getIcDistribuiProfissional())) {
			addMergedRegion(sheet, row, pos++, pos++, "Perfil do Profisional", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		}
		addCell(row, pos++, "Dt. Início", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Dt. Final", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Qt. Membros", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

		for (QntdeMembrosEquipeDTO membro : membros) {
			pos = 0;
			row = addNewRow(sheet);

			if ("D".equals(equipe.getIcDistribuiProfissional())) {
				addMergedRegion(sheet, row, pos++, pos++, membro.getDePerfil());
			}
			addCell(row, pos++, membro.getDtInicioVigenciaMembros());
			addCell(row, pos++, membro.getDtFinalVigenciaMembros());
			addCell(row, pos++, membro.getQtMembrosEquipe());

//			if (membro.getQtIntervalos() > 1) {
//				setCellComment(row.getCell(row.getLastCellNum() - 1),
//						"Essa vigência está incorreta. Verifique se há sobreposição de datas.");
//			}

		}

		addNewRow(sheet);

		/**
		 * Faturamentos
		 */
		int rownum = row.getRowNum() + 2;
		pos = 0;
		row = addNewRow(sheet, rownum++);
		addMergedRegion(sheet, row, pos, pos + 5, "Faturamentos", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet, rownum++);
		addCell(row, pos++, "Mes Referencia", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Dt. Início", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Dt. Final", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Dias Trabalhados", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Dias Extra", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Dias Ausência", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

		for (FaturamentoDTO faturamento : faturamentos) {

			pos = 0;
			row = addNewRow(sheet, rownum++);
			addCell(row, pos++, faturamento.getDeFatura());
			addCell(row, pos++, faturamento.getDtInicioFatura());
			addCell(row, pos++, faturamento.getDtFinalFatura());
			addCell(row, pos++, faturamento.getQtDiasFaturamento());
			addCell(row, pos++, faturamento.getQtDiasServicoExtra());
			addCell(row, pos++, faturamento.getQtDiasAusencia());

		}

		/**
		 * Indicadores
		 */
		pos = 7;
		rownum = 0;

		List<IndicadorContratoDTO> indicadores = indicadorService.listarIndicadoresContrato(equipe.getCoEquipe());

		row = addNewRow(sheet, rownum++);
		addMergedRegion(sheet, row, pos, pos + 5, "Indicadores", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet, rownum++);
		addMergedRegion(sheet, row, pos, pos + 1, "Indicador", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos + 2, "Peso", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos + 3, "Fator Ajuste", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos + 4, "Limite Máximo", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos + 5, "Qtd. Fat. Média", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

		for (IndicadorContratoDTO indicador : indicadores) {
			pos = 7;
			row = addNewRow(sheet, rownum++);

			addMergedRegion(sheet, row, pos, pos + 1, indicador.getSgTipoIndicador() + " - " + indicador.getNoTipoIndicador());
			setNoNull(row, pos + 2, indicador.getVrPesoTipoIndicador(), CellStylesEnum.DECIMAL);
			setNoNull(row, pos + 3, indicador.getVrFatorAjuste(), CellStylesEnum.DECIMAL);
			setNoNull(row, pos + 4, indicador.getVrLimiteMaximoIndicador(), CellStylesEnum.DECIMAL5);
			setNoNull(row, pos + 5, indicador.getQtFaturamento());
		}

		addNewRow(sheet, rownum++);

		/**
		 * Valores Faixas Indicador
		 */
		List<FaixaIndicadorDTO> faixas = faixaIndicadorService.listar(equipe.getNuEquipe());

		pos = 7;
		rownum = 13;
		row = addNewRow(sheet, rownum++);
		addMergedRegion(sheet, row, pos, pos + 2, "Valores Faixas Indicador", CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);

		row = addNewRow(sheet, rownum++);
		addCell(row, pos++, "Vr. Inicial", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Vr. Final", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
		addCell(row, pos++, "Valor", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

		for (FaixaIndicadorDTO faixa : faixas) {
			pos = 7;
			row = addNewRow(sheet, rownum++);

			addCell(row, pos++, faixa.getQtInicioFaixa(), CellStylesEnum.DECIMAL);
			addCell(row, pos++, faixa.getQtFinalFaixa(), CellStylesEnum.DECIMAL);
			addCell(row, pos++, faixa.getVrFatorFaixa(), CellStylesEnum.DECIMAL);
		}

		addNewRow(sheet, rownum++);

		/**
		 * Situações Consideradas para Serviços Contratados
		 */
		pos = 7;
		row = addNewRow(sheet, rownum++);

		if ("D".equals(equipe.getIcDistribuiProfissional())) {
			addMergedRegion(sheet, row, pos, pos + 5, //
					"Situações Consideradas para Serviços Contratados", //
					CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		} else {
			addMergedRegion(sheet, row, pos, pos + 3, //
					"Situações Consideradas para Serviços Contratados", //
					CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
		}

		List<SituacaoEntregaIndicadorDTO> situacoes = situacaoEntregaIndicadorService.listar(equipe.getNuEquipe());

		if (Utils.isEmpty(situacoes)) {
			row = addNewRow(sheet, rownum++);
			addMergedRegion(sheet, row, pos, pos + 3, "Nenhuma situação configurada.", CellStylesEnum.RED_ALIGN_CENTER_BOLD);
		}

		if (Utils.isNotEmpty(situacoes)) {

			row = addNewRow(sheet, rownum++);

			addMergedRegion(sheet, row, pos, pos + 1, "Indicador", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);

			if ("D".equals(equipe.getIcDistribuiProfissional())) {
				addMergedRegion(sheet, row, pos + 2, pos + 3, "Agrupador", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
				addMergedRegion(sheet, row, pos + 4, pos + 5, "Situação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			} else {
				addMergedRegion(sheet, row, pos + 2, pos + 3, "Situação", CellStylesEnum.NORMAL_ALIGN_CENTER_BOLD);
			}

			for (SituacaoEntregaIndicadorDTO situacao : situacoes) {

				row = addNewRow(sheet, rownum++);

				addMergedRegion(sheet, row, pos, pos + 1, situacao.getNoTipoIndicador());

				if ("D".equals(equipe.getIcDistribuiProfissional())) {
					addMergedRegion(sheet, row, pos + 2, pos + 3, situacao.getNoAgrupador());
					addMergedRegion(sheet, row, pos + 4, pos + 5, situacao.getNuSituacaoAtendimento() + //
							" - " + situacao.getNoSituacaoAtendimento());
				} else {
					addMergedRegion(sheet, row, pos + 2, pos + 3, situacao.getNuSituacaoAtendimento() + //
							" - " + situacao.getNoSituacaoAtendimento());
				}
			}
		}

		addNewRow(sheet, rownum++);

	}
}
