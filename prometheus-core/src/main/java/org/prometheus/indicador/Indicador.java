package org.prometheus.indicador;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.RelatorioContext;
import org.prometheus.utils.XSSFSheetUtils;

public abstract class Indicador {

	protected IndicadorContratoDTO indicador;
	protected RelatorioContext context;
	protected int pos = 0;

	public Indicador(IndicadorContratoDTO indicador, RelatorioContext indicadorContext) {
		this.indicador = indicador;
		this.context = indicadorContext;
	}
	
	public void escreverTitulo() {
		XSSFRow row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 2, //
				indicador.getSgTipoIndicador() + " - " + indicador.getNoTipoIndicador(), //
				CellStylesEnum.WHITE_SMOKE_ALIGN_LEFT);
	}

	public abstract XSSFCell calcular();
}
