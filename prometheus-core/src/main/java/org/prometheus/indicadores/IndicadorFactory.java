package org.prometheus.indicadores;

import org.prometheus.relatorios.RelatorioContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class IndicadorFactory {

	@Autowired
	private ApplicationContext context;

	public Indicador get(String sigla, RelatorioContext indicadorContext) {

		String beanName = "Indicador" + sigla;

		if (!context.containsBean(beanName)) {
			throw new IllegalArgumentException("Indicador não encontrado: " + beanName);
		}

		return (Indicador)context.getBean(beanName, indicadorContext);
	}
}