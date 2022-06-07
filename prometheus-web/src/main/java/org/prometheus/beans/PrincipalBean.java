package org.prometheus.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.compress.utils.IOUtils;
import org.prometheus.relatorios.RelatorioEquipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ManagedBean
public class PrincipalBean {

	@Autowired
	private RelatorioEquipe relatorio;

	private String coEquipe;

	public void baixarRelatorio() {
		String filename = relatorio.gerar(getCoEquipe());
		download(filename);
	}

	public void download(String filename) {

		try {
			// informacoes do arquivo
			File file = new File(filename);
			String fileName = file.getName();
			String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			int contentLength = (int) file.length();

			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ec = ctx.getExternalContext();

			// seta cabeçalhos http
			ec.responseReset(); // limpa qualquer possível sujeira feita por algum filtro ou componente
			ec.setResponseContentType(contentType); // tipo do conteúdo, como "application/pdf" por exemplo
			ec.setResponseContentLength(contentLength); // opcional. ajuda o browser com a barra de progresso
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

			// escreve arquivo no fluxo de saida
			OutputStream out = ec.getResponseOutputStream();

			IOUtils.copy(new FileInputStream(file), out);
			
			// finaliza ciclo de vida
			ctx.responseComplete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getCoEquipe() {
		return coEquipe;
	}

	public void setCoEquipe(String coEquipe) {
		this.coEquipe = coEquipe;
	}

}
