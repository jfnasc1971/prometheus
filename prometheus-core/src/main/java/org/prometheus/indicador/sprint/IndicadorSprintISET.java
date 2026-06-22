package org.prometheus.indicador.sprint;

import java.math.BigDecimal;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.indicador.Indicador;
import org.prometheus.indicador.ParametrosIndicador;
import org.prometheus.indicador.service.IndicadorISETSprintService;
import org.prometheus.relatorios.RelatorioContext;
import org.prometheus.utils.XSSFSheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("IndicadorSprintISET")
@Scope("prototype")
public class IndicadorSprintISET extends Indicador {

	@Autowired
	private IndicadorISETSprintService service;

	public IndicadorSprintISET(IndicadorContratoDTO indicador, RelatorioContext indicadorContext) {
		super(indicador, indicadorContext);
		pos = 10;
	}

	@Override
	public XSSFCell calcular() {
		escreverTitulo();

		BigDecimal qtServicosEntregues = service.contarServicosEntregues(context.getEquipe().getNuEquipe(),
				context.getCiclo().getNuCicloTrabalho());
		
		BigDecimal qtServicosContratatos = service.contarServicosContratados(context.getEquipe().getNuEquipe(),
				context.getCiclo().getNuCicloTrabalho());

		XSSFCell cellQtServicosEntregues = null;
		XSSFCell cellQtServicosContratados = null;

		XSSFRow row = null;

		if (qtServicosContratatos != null && qtServicosContratatos.doubleValue() != 0) {

			row = context.nextRow();
			XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Qt. Serviços Entregues");
			cellQtServicosEntregues = XSSFSheetUtils.addCell(row, pos + 2, qtServicosEntregues);

			row = context.nextRow();
			XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Qt. Serviços Contratados");
			cellQtServicosContratados = XSSFSheetUtils.addCell(row, pos + 2, qtServicosContratatos);

			row = context.nextRow();
			XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Peso");
			XSSFSheetUtils.addCell(row, pos + 2, indicador.getVrPesoTipoIndicador());
		}

		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Valor Indicador");
		XSSFCell result = XSSFSheetUtils.addCell(row, pos + 2, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

		if (qtServicosContratatos != null && qtServicosContratatos.doubleValue() != 0) {
			XSSFSheetUtils.setCellFormula(result, String.format("(%s/%s)", //
					cellQtServicosEntregues.getReference(), cellQtServicosContratados.getReference()), //
					CellStylesEnum.DECIMAL);
		}

		return result;
	}

}
