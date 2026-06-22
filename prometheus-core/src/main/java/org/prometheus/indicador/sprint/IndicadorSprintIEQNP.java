package org.prometheus.indicador.sprint;

import java.math.BigDecimal;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.indicador.Indicador;
import org.prometheus.relatorios.RelatorioContext;
import org.prometheus.utils.XSSFSheetUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("IndicadorSprintIEQNP")
@Scope("prototype")
public class IndicadorSprintIEQNP extends Indicador {

	public IndicadorSprintIEQNP(IndicadorContratoDTO indicador, RelatorioContext indicadorContext) {
		super(indicador, indicadorContext);
		pos = 10;
	}

	@Override
	public XSSFCell calcular() {
		escreverTitulo();

		XSSFRow row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Nota");
		XSSFCell cellNota = XSSFSheetUtils.addCell(row, pos + 2, context.getCiclo().getVrNotaQualidadeProduto());

		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Fator de Ajuste");
		XSSFCell cellVrAjuste = XSSFSheetUtils.addCell(row, pos + 2, indicador.getVrFatorAjuste());

		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Peso");
		XSSFSheetUtils.addCell(row, pos + 2, indicador.getVrPesoTipoIndicador());

		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Valor Indicador");

		XSSFCell result = XSSFSheetUtils.addCell(row, pos + 2, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

		if (cellNota.getReference() != null) {
			XSSFSheetUtils.setCellFormula(result,
					String.format("%s*%s", cellNota.getReference(), cellVrAjuste.getReference()), //
					CellStylesEnum.DECIMAL);
		}

		return result;
	}

}
