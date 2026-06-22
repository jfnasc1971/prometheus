package org.prometheus.indicador;

import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.relatorios.RelatorioContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class IndicadorSprintFactory {

	@Autowired
	private ApplicationContext context;

	public Indicador get(IndicadorContratoDTO indicador, RelatorioContext indicadorContext) {

		String beanName = "IndicadorSprint" + indicador.getSgTipoIndicador().trim();

		if (!context.containsBean(beanName)) {
			throw new IllegalArgumentException("Indicador não encontrado: " + beanName);
		}

		return (Indicador)context.getBean(beanName, indicador, indicadorContext);
	}
}