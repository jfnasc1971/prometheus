package org.prometheus.indicador;

import java.math.BigDecimal;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.indicador.service.IndicadorIEQQETService;
import org.prometheus.relatorios.RelatorioContext;
import org.prometheus.utils.XSSFSheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("IndicadorIEQQET")
@Scope("prototype")
public class IndicadorIEQQET extends Indicador {

	@Autowired
	private IndicadorIEQQETService service;

	public IndicadorIEQQET(IndicadorContratoDTO indicador, RelatorioContext indicadorContext) {
		super(indicador, indicadorContext);
	}

	@Override
	public XSSFCell calcular() {

		escreverTitulo();

		//
		BigDecimal qtServicosEntregues = service.contarServicosEntregues(context.getEquipe().getNuEquipe(),
				context.getFaturamento().getNuFatura());
		XSSFRow row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, 1, "Serviços Entregues");
		XSSFCell cellQtServicosEntregues = XSSFSheetUtils.addCell(row, 2, qtServicosEntregues);

		//
		BigDecimal qtServicosContratados = service.contarServicosContratados(context.getEquipe().getNuEquipe(),
				context.getFaturamento().getNuFatura());
		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, 1, "Serviços Contratados");
		XSSFCell cellQtServicosContratados = XSSFSheetUtils.addCell(row, 2, qtServicosContratados);

		// =B30/B31
		row = context.nextRow();
		XSSFCell resultado = XSSFSheetUtils.addCell(row, 2, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

		if ("NUMERIC".equals(cellQtServicosContratados.getCellType().name())
				&& cellQtServicosContratados.getNumericCellValue() != 0) {
			XSSFSheetUtils.setCellFormula(resultado, //
					String.format("%s/%s", //
							cellQtServicosEntregues.getReference(), //
							cellQtServicosContratados.getReference()), //
					CellStylesEnum.DECIMAL5);
		}

		return resultado;
	}

}
