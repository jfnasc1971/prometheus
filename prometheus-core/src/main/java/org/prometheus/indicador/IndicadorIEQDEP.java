package org.prometheus.indicador;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.RelatorioContext;
import org.prometheus.service.FaixaIndicadorService;
import org.prometheus.service.IndicadorService;
import org.prometheus.utils.Utils;
import org.prometheus.utils.XSSFSheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("IndicadorIEQDEP")
@Scope("prototype")
public class IndicadorIEQDEP extends Indicador {

	@Autowired
	private IndicadorService indicadorService;

	@Autowired
	private FaixaIndicadorService faixaIndicadorService;	
	
	public IndicadorIEQDEP(IndicadorContratoDTO indicador, RelatorioContext context) {
		super(indicador, context);
	}

	@Override
	public XSSFCell calcular() {

		XSSFRow row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 4, 7, //
				"Média dos Indicadores", CellStylesEnum.ORANGE_ALIGN_CENTER_BOLD);

		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 4, 5, "Indicadores",
				CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
		XSSFSheetUtils.addCell(row, 6, "Peso", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);
		XSSFSheetUtils.addCell(row, 7, "Valor", CellStylesEnum.WHITE_SMOKE_ALIGN_CENTER_BOLD);

		List<Long> listaIndicadoresFaturamento = new ArrayList<Long>(context.getIndicadoresFaturamento().keySet());

		List<IndicadorContratoDTO> indicadores = indicadorService
				.listarIndicadoresContrato(context.getEquipe().getCoEquipe(), false);

		StringBuilder formula = new StringBuilder();
		StringBuilder pesos = new StringBuilder();

		for (IndicadorContratoDTO indicadorDTO : indicadores) {

			StringBuilder sb = new StringBuilder();

			row = context.nextRow();
			XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 4, 5, indicadorDTO.getDescricao());
			XSSFCell vrPesoIndicador = XSSFSheetUtils.addCell(row, 6, indicadorDTO.getVrPesoTipoIndicador());
			XSSFCell resultado = XSSFSheetUtils.addCell(row, 7, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

			List<Long> listaMedias = listaIndicadoresFaturamento;

			if (listaIndicadoresFaturamento.size() > this.indicador.getQtFaturamento()) {
				listaMedias = listaIndicadoresFaturamento.subList(
						listaIndicadoresFaturamento.size() - this.indicador.getQtFaturamento(),
						listaIndicadoresFaturamento.size());
			}

			for (Long nuFatura : listaMedias) {

				XSSFCell cell = context.getIndicadoresFaturamento().get(nuFatura)
						.get(indicadorDTO.getSgTipoIndicador());

				try {
					if (cell != null && !"N/A".equalsIgnoreCase(cell.toString())) {
						sb.append("," + cell.getReference());
					}
				} catch (Exception e) {
				}
			}

			if (Utils.isNotEmpty(sb.toString())) {
				XSSFCell vrIndicador = XSSFSheetUtils.setCellFormula(resultado, //
						"average(" + sb.toString().substring(1) + ")", //
						CellStylesEnum.DECIMAL5);
				formula.append(String.format("+(%s*%s)", //
						vrIndicador.getReference(), vrPesoIndicador.getReference()));
				pesos.append(String.format("+%s", vrPesoIndicador.getReference()));

			}

		}

		context.nextRow();
		row = context.nextRow();
		XSSFSheetUtils.addCell(row, 4, "Desempenho", CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);
		XSSFSheetUtils.addCell(row, 5, "FNS Apurado", CellStylesEnum.GREEN_ALIGN_CENTER_BOLD);

		row = context.nextRow();

		XSSFCell cellDesempenho = XSSFSheetUtils.addCell(row, 4, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

		if (formula != null && pesos != null) {
			XSSFSheetUtils.setCellFormula(cellDesempenho,//
					String.format("(%s)/(%s)", formula.substring(1), pesos.substring(1)), //
					CellStylesEnum.DECIMAL5);
		}
		
		XSSFCell resultado = XSSFSheetUtils.addCell(row, 5, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

		int qtFaixas = faixaIndicadorService.listar(context.getEquipe().getNuEquipe()).size();
		
		setCellFormulaValorFNS(resultado, cellDesempenho, qtFaixas);
		
		return resultado;
	}

	private void setCellFormulaValorFNS(XSSFCell cellFNSApurado, XSSFCell cellDesempenho, int qtFaixas) {

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
				XSSFSheetUtils.setCellFormula(cellFNSApurado, formula.toString().substring(1), CellStylesEnum.DECIMAL);	
			}
		}

	}
	
}
