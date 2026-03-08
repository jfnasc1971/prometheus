package org.prometheus.indicadores;

import org.prometheus.AppConfig;

public class IndicadorFactory {

	public static final IndicadorBase getIndicador(String noIndicador) {
		try {
	        String className = IndicadorBase.class.getPackage().getName() + ".Indicador" + noIndicador.trim();

	        Class<? extends IndicadorBase> clazz =
	                Class.forName(className).asSubclass(IndicadorBase.class);

	        return AppConfig.getApplicationContext().getBean(clazz);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Indicador inválido: " + noIndicador);
		}
		
		return null;
	}

}
