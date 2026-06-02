package org.prometheus.indicador;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.relatorios.RelatorioContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("IndicadorIEQDES")
@Scope("prototype")
public class IndicadorIEQDES extends Indicador {

	public IndicadorIEQDES(IndicadorContratoDTO indicador, RelatorioContext indicadorContext) {
		super(indicador, indicadorContext);
	}

	@Override
	public XSSFCell calcular() {

		escreverTitulo();
		
		return null;
	}

}
