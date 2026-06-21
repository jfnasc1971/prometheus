package org.prometheus.indicador;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.relatorios.RelatorioContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("IndicadorIEQNP")
@Scope("prototype")
public class IndicadorIEQNP extends Indicador {

	public IndicadorIEQNP(IndicadorContratoDTO indicador, RelatorioContext indicadorContext) {
		super(indicador, indicadorContext);
	}
	
	@Override
	public XSSFCell calcular() {
		// TODO Auto-generated method stub
		return null;
	}

}
