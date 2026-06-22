package org.prometheus.indicador.sprint;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.indicador.Indicador;
import org.prometheus.relatorios.RelatorioContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("IndicadorSprintISET")
@Scope("prototype")
public class IndicadorSprintISET extends Indicador {

	public IndicadorSprintISET(IndicadorContratoDTO indicador, RelatorioContext indicadorContext) {
		super(indicador, indicadorContext);
	}
	
	@Override
	public XSSFCell calcular() {
		// TODO Auto-generated method stub
		return null;
	}

}
