package org.prometheus.indicadores;

import java.io.Serializable;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;

public abstract class IndicadorBase implements Serializable {

	private static final long serialVersionUID = 1L;

	public IndicadorBase() {
	}

	public abstract ResultadoIndicador calcularIndicador(XSSFSheet sheet, int rownum, EquipeDTO equipe, FaturamentoDTO faturamento);
}
