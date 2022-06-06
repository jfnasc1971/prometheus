package org.prometheus.beans;

import javax.faces.bean.ManagedBean;

import org.prometheus.relatorios.RelatorioEquipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ManagedBean
public class PrincipalBean {

	@Autowired
	private RelatorioEquipe relatorio;
	
	public void gerarRelatorio() {
		relatorio.gerar("BRE201800015");
	}
	
}
