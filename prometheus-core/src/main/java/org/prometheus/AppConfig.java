package org.prometheus;

import org.prometheus.utils.Utils;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
@ComponentScan("br.gov.caixa.sigct")
public class AppConfig {

	private static GenericXmlApplicationContext ctx;

	public static final GenericXmlApplicationContext getApplicationContext() {

		if (ctx == null) {

			ctx = new GenericXmlApplicationContext();

			ConfigurableEnvironment env = ctx.getEnvironment();

			if (Utils.isEmpty(env.getActiveProfiles())) {
				System.out.println("-----------------------> des-profile.xml");
				ctx.load("des-profile.xml");
			} else {
				ctx.load(env.getActiveProfiles()[0] + "-profile.xml");
			}

			ctx.refresh();
		}

		return ctx;
	}
}
