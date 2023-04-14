package org.prometheus.relatorios;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.relatorios.equipe.AbaAjuste;
import org.prometheus.relatorios.equipe.AbaCalculoValorDevido;
import org.prometheus.relatorios.equipe.AbaCalculoVrDevidoQualidade;
import org.prometheus.relatorios.equipe.AbaCalculosAcionamento;
import org.prometheus.relatorios.equipe.AbaCalculosAcionamentoQualidade;
import org.prometheus.relatorios.equipe.AbaCalculosG4;
import org.prometheus.relatorios.equipe.AbaCalculosSprint;
import org.prometheus.relatorios.equipe.AbaEquipe;
import org.prometheus.relatorios.equipe.AbaPagamentos;
import org.prometheus.relatorios.equipe.AbaPagamentosQualidade;
import org.prometheus.relatorios.equipe.AbaServicosAcionamento;
import org.prometheus.relatorios.equipe.AbaServicosAcionamentoQualidade;
import org.prometheus.service.EquipeService;
import org.prometheus.service.FaturamentoService;
import org.prometheus.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RelatorioEquipe extends Relatorio {

	private static final Logger LOG = LogManager.getLogger(RelatorioEquipe.class.getSimpleName());
	
	public static final String ABA_CALCULOS = "Memória de Cálculo";
	public static final String ABA_CALCULO_VALOR_DEVIDO = "Cálculo Valor Devido";
	public static final String ABA_PAGAMENTOS = "Pagamentos";
	public static final String ABA_SERVICOS = "Serviços";
	
	private static final String PATH = System.getProperty("user.dir") + File.separator + "Demonstrativos";

	@Autowired
	private EquipeService equipeService;

	@Autowired
	private FaturamentoService service;

	public static void main(String[] args) {

		(new File(PATH)).mkdirs();

		RelatorioEquipe relatorio = ctxt.getBean(RelatorioEquipe.class);
		String coEquipe = null;
		BufferedReader reader = null;

		if (args != null && args.length > 0) {
			coEquipe = args[0];
		}

		try {

			if (coEquipe == null) {
				reader = new BufferedReader(new FileReader("lista_equipes.properties"));

				while ((coEquipe = reader.readLine()) != null) {
					if (Utils.isNotEmpty(coEquipe) && !coEquipe.trim().startsWith("#")) {
						relatorio.gerar(coEquipe);// , DateUtil.parse("2018-08-20"));
					}
				}

			} else {

				relatorio.gerar(coEquipe);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		((ConfigurableApplicationContext) ctxt).close();
	}

	public String gerar(String coEquipe) {
		(new File(PATH)).mkdirs();
		return gerar(coEquipe, (Date) null);
	}

	public String gerar(String coEquipe, Date dtFinalFatura) {

		List<FaturamentoDTO> faturamentos = service.listar(coEquipe, dtFinalFatura);

		try {
			return gerar(coEquipe, faturamentos);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public String gerar(String coEquipe, List<FaturamentoDTO> faturamentos) throws Exception {

		File file = new File(PATH + "/SIGCT_Demonstrativo_" + coEquipe + ".xlsx");
		file.delete();
		FileOutputStream fos = new FileOutputStream(file);
		System.out.println(file.getAbsolutePath());

		XSSFWorkbook wb = new XSSFWorkbook();

		EquipeDTO equipe = equipeService.findBy(coEquipe);

		LOG.info(equipe.getNuContrato() + " - " + equipe.getNoContrato() + //
				" - " + equipe.getNuEquipe() + " - " + equipe.getCoEquipe() + //
				" - " + equipe.getNoTipoGrupo() + //
				" - " + equipe.getIcTipoAtendimento());

		Map<Long, InsumosFaturamentoDTO> insumos = new LinkedHashMap<Long, InsumosFaturamentoDTO>();

		// Equipe
		AbaEquipe abaEquipe = ctxt.getBean(AbaEquipe.class);
		abaEquipe.write(wb, coEquipe, faturamentos);

		XSSFCell cellValorPagamentos = null;

		if ("D".equals(equipe.getIcDistribuiProfissional())) {

			AbaPagamentosQualidade abaPagamentos = ctxt.getBean(AbaPagamentosQualidade.class);
			cellValorPagamentos = abaPagamentos.gerar(wb, coEquipe, insumos);

		} else {

			AbaPagamentos abaPagamentos = ctxt.getBean(AbaPagamentos.class);
			cellValorPagamentos = abaPagamentos.write(wb, coEquipe);

		}

		//
		// Cálculos
		//
		if (Long.valueOf(62L).equals(equipe.getNuTipoGrupo())) {
			AbaCalculosSprint calculos = ctxt.getBean(AbaCalculosSprint.class);
			insumos = calculos.gerar(wb, coEquipe, faturamentos);
		}

		if (Long.valueOf(63L).equals(equipe.getNuTipoGrupo()) || Long.valueOf(64L).equals(equipe.getNuTipoGrupo())) {
			AbaCalculosAcionamento calculos = ctxt.getBean(AbaCalculosAcionamento.class);
			insumos = calculos.write(wb, coEquipe, faturamentos);
		}

		if (Long.valueOf(83L).equals(equipe.getNuTipoGrupo()) || Long.valueOf(84L).equals(equipe.getNuTipoGrupo())) {
			AbaCalculosAcionamentoQualidade calculos = ctxt.getBean(AbaCalculosAcionamentoQualidade.class);
			calculos.gerar(wb, coEquipe, faturamentos, insumos);
		}

		if (Long.valueOf(65L).equals(equipe.getNuTipoGrupo())) {
			AbaCalculosG4 calculos = ctxt.getBean(AbaCalculosG4.class);
			insumos = calculos.write(wb, coEquipe, faturamentos);
		}

		if (Long.valueOf(87L).equals(equipe.getNuTipoGrupo())) {
			AbaCalculosG4 calculos = ctxt.getBean(AbaCalculosG4.class);
			insumos = calculos.write(wb, coEquipe, faturamentos);
		}

		//
		// Serviços
		//
		if (Long.valueOf(63L).equals(equipe.getNuTipoGrupo()) || Long.valueOf(64L).equals(equipe.getNuTipoGrupo())
				|| Long.valueOf(65L).equals(equipe.getNuTipoGrupo())) {
			AbaServicosAcionamento calculos = ctxt.getBean(AbaServicosAcionamento.class);
			calculos.write(wb, coEquipe, faturamentos);
		}
		
		if ("D".equals(equipe.getIcDistribuiProfissional())) {
			AbaServicosAcionamentoQualidade calculos = ctxt.getBean(AbaServicosAcionamentoQualidade.class);
			calculos.write(wb, coEquipe, faturamentos);
		}

		if (Utils.isNotEmpty(insumos)) {

			XSSFCell cellValorDevido = null;

			// Calculos Valor Devido
			if ("D".equals(equipe.getIcDistribuiProfissional())) {

				AbaCalculoVrDevidoQualidade abaCalculoValorDevido = ctxt.getBean(AbaCalculoVrDevidoQualidade.class);
				cellValorDevido = abaCalculoValorDevido.gerar(wb, coEquipe, faturamentos, insumos);

			} else {

				AbaCalculoValorDevido abaCalculoValorDevido = ctxt.getBean(AbaCalculoValorDevido.class);
				cellValorDevido = abaCalculoValorDevido.write(wb, coEquipe, faturamentos, insumos);

			}

			// Ajuste
			AbaAjuste abaAjuste = ctxt.getBean(AbaAjuste.class);
			abaAjuste.write(wb, coEquipe, cellValorDevido, cellValorPagamentos);

		}

		wb.write(fos);
		wb.close();

		return file.getAbsolutePath();
	}

}
