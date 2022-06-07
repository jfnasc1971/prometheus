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

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.service.EquipeService;
import org.prometheus.service.FaturamentoService;
import org.prometheus.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RelatorioEquipe extends Relatorio {

	private static final String PATH = "./Demonstrativos";

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

	public void gerar(Long nuContrato) {

	}

	public void gerar(Long nuContrato, Long nuTipoGrupo) {

	}

	public String gerar(String coEquipe) {
		(new File(PATH)).mkdirs();
		return gerar(coEquipe, null);
	}

	public String gerar(String coEquipe, Date dtFinalFatura) {

		List<FaturamentoDTO> faturamentos = service.listar(coEquipe, dtFinalFatura);

		try {
			return writeFile(coEquipe, faturamentos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public String writeFile(String coEquipe, List<FaturamentoDTO> faturamentos) throws Exception {

		File file = new File(PATH + "/SIGCT_Demonstrativo_" + coEquipe + ".xlsx");
		file.delete();
		FileOutputStream fos = new FileOutputStream(file);
		System.out.println(file.getAbsolutePath());

		XSSFWorkbook wb = new XSSFWorkbook();

		EquipeDTO equipe = equipeService.findBy(coEquipe);

		System.out.println(equipe.getNuContrato() + " - " + equipe.getNoContrato() + //
				" - " + equipe.getNuEquipe() + " - " + equipe.getCoEquipe() + //
				" - " + equipe.getNoTipoGrupo() + //
				" - " + equipe.getIcTipoAtendimento());

		// Equipe
		AbaEquipe abaEquipe = ctxt.getBean(AbaEquipe.class);
		abaEquipe.write(wb, coEquipe, faturamentos);

		//AbaMemoriaCalculo abaMemoria = ctxt.getBean(AbaMemoriaCalculo.class);
		//abaMemoria.write(wb, coEquipe, faturamentos);

		Map<Long, InsumosFaturamentoDTO> insumos = new LinkedHashMap<Long, InsumosFaturamentoDTO>();
		XSSFCell cellValorDevido = null;

		// Cálculos
		if (Long.valueOf(62L).equals(equipe.getNuTipoGrupo())) {
			AbaCalculosSprint calculos = ctxt.getBean(AbaCalculosSprint.class);
			insumos = calculos.write(wb, coEquipe, faturamentos);
		}

		if (Long.valueOf(63L).equals(equipe.getNuTipoGrupo()) || Long.valueOf(64L).equals(equipe.getNuTipoGrupo())) {
			AbaCalculosAcionamento calculos = ctxt.getBean(AbaCalculosAcionamento.class);
			insumos = calculos.write(wb, coEquipe, faturamentos);
		}

		if (Long.valueOf(65L).equals(equipe.getNuTipoGrupo())) {
			AbaCalculosG4 calculos = ctxt.getBean(AbaCalculosG4.class);
			insumos = calculos.write(wb, coEquipe, faturamentos);
		}

		if (Long.valueOf(87L).equals(equipe.getNuTipoGrupo())) {
			AbaCalculosG4 calculos = ctxt.getBean(AbaCalculosG4.class);
			insumos = calculos.write(wb, coEquipe, faturamentos);
		}

		if (Utils.isNotEmpty(insumos)) {

			// Calculos Valor Devido
			AbaCalculoValorDevido abaCalculoValorDevido = ctxt.getBean(AbaCalculoValorDevido.class);
			cellValorDevido = abaCalculoValorDevido.write(wb, coEquipe, faturamentos, insumos);

			// Pagamentos
			AbaPagamentos abaPagamentos = ctxt.getBean(AbaPagamentos.class);
			XSSFCell cellValorPagamentos = abaPagamentos.write(wb, coEquipe);

			// Ajuste
			AbaAjuste abaAjuste = ctxt.getBean(AbaAjuste.class);
			abaAjuste.write(wb, coEquipe, cellValorDevido, cellValorPagamentos);

		}

		wb.write(fos);
		wb.close();

		return file.getAbsolutePath();
	}

}
