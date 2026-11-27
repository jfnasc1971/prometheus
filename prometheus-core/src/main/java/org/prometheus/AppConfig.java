package org.prometheus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prometheus.utils.Utils;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
@ComponentScan("br.gov.caixa.sigct")
public class AppConfig {

    private static final Logger LOG = LogManager.getLogger(AppConfig.class.getSimpleName());
	
	private static GenericXmlApplicationContext ctx;

	public static final GenericXmlApplicationContext getApplicationContext() {

		if (ctx == null) {

			ctx = new GenericXmlApplicationContext();

			ConfigurableEnvironment env = ctx.getEnvironment();

			if (Utils.isEmpty(env.getActiveProfiles())) {
				LOG.info("ARQUIVO DE CONFIGURACAO: profile-local.xml");
				ctx.load("profile-local.xml");
			} else {
				LOG.info("ARQUIVO DE CONFIGURACAO: profile-" + env.getActiveProfiles()[0] + ".xml");
				ctx.load("profile-" + env.getActiveProfiles()[0] + ".xml");
			}

			ctx.refresh();
		}

		return ctx;
	}
}
