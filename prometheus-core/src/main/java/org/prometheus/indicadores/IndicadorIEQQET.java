package org.prometheus.indicadores;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("IndicadorIEQQET")
@Scope("prototype")
public class IndicadorIEQQET implements Indicador {
	
	private IndicadorContext indicadorContext;

	public IndicadorIEQQET(IndicadorContext indicadorContext) {
		this.indicadorContext = indicadorContext;
	}
	
	@Override
	public void calcular() {
		System.out.println(indicadorContext.getRownum());
		indicadorContext.setRownum(indicadorContext.getRownum()+1);
		indicadorContext.setRownum(indicadorContext.getRownum()+1);
		indicadorContext.setRownum(indicadorContext.getRownum()+1);
		indicadorContext.setRownum(indicadorContext.getRownum()+1);
	}


}
