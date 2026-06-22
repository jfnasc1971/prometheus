package org.prometheus.indicador.sprint;

import java.math.BigDecimal;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.indicador.Indicador;
import org.prometheus.indicador.service.IndicadorIEQPRDSprintService;
import org.prometheus.relatorios.RelatorioContext;
import org.prometheus.utils.XSSFSheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("IndicadorSprintIEQPRD")
@Scope("prototype")
public class IndicadorSprintIEQPRD extends Indicador {

	@Autowired
	private IndicadorIEQPRDSprintService service;

	public IndicadorSprintIEQPRD(IndicadorContratoDTO indicador, RelatorioContext indicadorContext) {
		super(indicador, indicadorContext);
		pos = 10;
	}

	@Override
	public XSSFCell calcular() {
		escreverTitulo();

		BigDecimal nuTamanhoSprint = service.calcularTamanhoSprint(context.getEquipe().getNuEquipe(),
				context.getCiclo().getNuCicloTrabalho());
		
		BigDecimal qtDiasSprint = service.contarDiasSprint(context.getEquipe().getNuEquipe(),
				context.getCiclo().getNuCicloTrabalho());

		BigDecimal qtMembrosSprint = service.contarMembrosSprint(context.getEquipe().getNuEquipe(),
				context.getCiclo().getNuCicloTrabalho());

		BigDecimal vrProdutividadeEsperada = service.contarMembrosSprint(context.getEquipe().getNuEquipe(),
				context.getCiclo().getNuCicloTrabalho());
		
		XSSFCell cellNuTamanhoSprint = null;
		XSSFCell cellQtDiasSprint = null;
		XSSFCell cellQtMembrosSprint = null;
		XSSFCell cellVrProdutividadeEsperada = null;

		XSSFRow row = null;

		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Tamanho");
		cellNuTamanhoSprint = XSSFSheetUtils.addCell(row, pos + 2, nuTamanhoSprint);

		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Qt. Dias Sprint");
		cellQtDiasSprint = XSSFSheetUtils.addCell(row, pos + 2, qtDiasSprint);

		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Qt. Membros Sprint");
		cellQtMembrosSprint = XSSFSheetUtils.addCell(row, pos + 2, qtMembrosSprint);

		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Produtividade Esperada");
		cellVrProdutividadeEsperada = XSSFSheetUtils.addCell(row, pos + 2, vrProdutividadeEsperada);
		
		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Peso");
		XSSFSheetUtils.addCell(row, pos + 2, indicador.getVrPesoTipoIndicador());

		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Valor Indicador");
		XSSFCell result = XSSFSheetUtils.addCell(row, pos + 2, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

//		if (qtServicosContratatos != null && qtServicosContratatos.doubleValue() != 0) {
//			XSSFSheetUtils.setCellFormula(result, String.format("(%s/%s)", //
//					cellQtServicosEntregues.getReference(), cellQtServicosContratados.getReference()), //
//					CellStylesEnum.DECIMAL);
//		}

		return result;
	}

}
