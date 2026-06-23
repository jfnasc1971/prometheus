package org.prometheus.indicador;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.RelatorioContext;
import org.prometheus.utils.XSSFSheetUtils;

public abstract class Indicador {
	public static final String NAO_APURADO = "N/A";
	protected IndicadorContratoDTO indicador;
	protected RelatorioContext context;
	protected int pos = 0;

	public Indicador(IndicadorContratoDTO indicador, RelatorioContext context) {
		this.indicador = indicador;
		this.context = context;
	}

	public void escreverTitulo() {
		XSSFRow row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 2, //
				indicador.getSgTipoIndicador() + " - " + indicador.getNoTipoIndicador(), //
				CellStylesEnum.LIGHT_GREY_ALIGN_CENTER_BOLD);
	}

	public abstract XSSFCell calcular();
}
