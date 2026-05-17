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

	public Indicador(IndicadorContratoDTO indicador, RelatorioContext indicadorContext) {
		this.indicador = indicador;
		this.context = indicadorContext;
	}
	
	public void escreverTitulo() {
		XSSFRow row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, 2, //
				indicador.getSgTipoIndicador() + " - " + indicador.getNoTipoIndicador(), //
				CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
	}

	public abstract XSSFCell calcular();
}
