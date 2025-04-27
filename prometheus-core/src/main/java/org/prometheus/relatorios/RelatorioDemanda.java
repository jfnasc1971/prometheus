package org.prometheus.relatorios;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.AppConfig;
import org.prometheus.dto.DemandaDTO;
import org.prometheus.relatorios.demanda.AbaDemandaAjuste;
import org.prometheus.relatorios.demanda.AbaDemanda;
import org.prometheus.relatorios.demanda.AbaDemandaMensalidade;
import org.prometheus.relatorios.demanda.AbaDemandaSustentacao;
import org.prometheus.relatorios.demanda.AbaDemandaTermos;
import org.prometheus.service.DemandaService;
import org.prometheus.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RelatorioDemanda extends Relatorio {

    private static final Logger LOGGER = LogManager.getLogger(RelatorioDemanda.class.getSimpleName());

    private static final String PATH = System.getProperty("user.dir") + File.separator + "Demonstrativos";

    public static final String ABA_DEMANDA = "Demanda";
    public static final String ABA_TERMOS = "Termos";
    public static final Integer NU_CONTRATO = 649;

    @Autowired
    private DemandaService demandaService;

    public static void main(String[] args) {

        printVersion();
        
        (new File(PATH)).mkdirs();

        RelatorioDemanda relatorio = AppConfig.getApplicationContext().getBean(RelatorioDemanda.class);
        String coDemanda = null;
        String coContrato = null;
        BufferedReader reader = null;

        if (args != null && args.length > 0) {
            coDemanda = args[0];
        }

        try {

            if ((new File("contratos.txt")).exists()) {
                reader = new BufferedReader(new FileReader("contratos.txt"));

                while ((coContrato = reader.readLine()) != null) {
                    if (Utils.isNotEmpty(coContrato) && !coContrato.trim().startsWith("#")) {
                        relatorio.gerarTodas(coContrato);
                    }
                }
            }

            if (coDemanda == null) {
                reader = new BufferedReader(new FileReader("demandas.txt"));

                while ((coDemanda = reader.readLine()) != null) {
                    if (Utils.isNotEmpty(coDemanda) && !coDemanda.trim().startsWith("#")) {
                        relatorio.gerar(coDemanda);
                    }
                }

            } else {

                relatorio.gerar(coDemanda);

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
    }

    public void gerarTodas(String coContrato) throws Exception {
        List<DemandaDTO> demandas = demandaService.findByContrato(coContrato);
        for (DemandaDTO demanda : demandas) {
            gerar(demanda.getCoDemanda().trim());
        }
    }

    public String gerar(String coDemanda) throws Exception {
        DemandaDTO demanda = demandaService.findByDemanda(coDemanda);
        if (demanda == null) {
            System.out.println("Demanda: " + coDemanda + " NAO encontrada;");
            return null;
        }
        return gerar(demanda);
    }

    public String gerar(DemandaDTO demanda) throws Exception {

        String coContrato = demanda.getCoContrato().replaceAll("[ /]", "_");
        
        String basedir = PATH + File.separatorChar + coContrato + //
                File.separatorChar + demanda.getNoTipoGrupo().replaceAll("\\*", ""); 
        
        (new File(basedir)).mkdirs();

        File file = new File(basedir + File.separatorChar + "SIGCT_Demonstrativo_" + demanda.getCoDemanda() + ".xlsx");
        file.delete();
        FileOutputStream fos = new FileOutputStream(file);
        LOGGER.debug(file.getAbsolutePath());

        XSSFWorkbook wb = new XSSFWorkbook();

        XSSFCell cellValorDevido = null;

        // Grupo 01 - Desenvolvimento, Manutenção e Doc.
        if (isG1Configuracao1(demanda)) {

            AbaDemanda abaDemanda = AppConfig.getApplicationContext().getBean(AbaDemanda.class);
            cellValorDevido = abaDemanda.write(wb, demanda.getCoDemanda());

        }

        // Grupo 02 - Mensalidade
        if (isG2Mensalidade(demanda)) {

            AbaDemandaMensalidade abaDemanda = AppConfig.getApplicationContext().getBean(AbaDemandaMensalidade.class);
            cellValorDevido = abaDemanda.write(wb, demanda.getCoDemanda());

        }
        
        // Grupo 02 - Sustentação
        if (isG2SustentacaoSistemas(demanda)) {

            AbaDemandaSustentacao abaDemanda = AppConfig.getApplicationContext().getBean(AbaDemandaSustentacao.class);
            cellValorDevido = abaDemanda.write(wb, demanda.getCoDemanda());

        }         
        
        AbaDemandaTermos abaTermosDemanda = AppConfig.getApplicationContext().getBean(AbaDemandaTermos.class);
        XSSFCell cellValorPagamentos = abaTermosDemanda.write(wb, demanda.getCoDemanda());

        AbaDemandaAjuste abaAjusteDemanda = AppConfig.getApplicationContext().getBean(AbaDemandaAjuste.class);
        abaAjusteDemanda.write(wb, demanda.getCoDemanda(), cellValorDevido, cellValorPagamentos);

        wb.write(fos);
        wb.close();

        return file.getAbsolutePath();

    }

    public boolean isG1Configuracao1(DemandaDTO demanda) {
        // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
        // | no_tipo_grupo | ic_ordem_servico_acionamento | ic_quantidade |
        // ic_valor_os_informado | ic_calculo_quantidade_os | ic_quantidade_os |
        // | --------------------------------------------- |
        // ---------------------------- | ------------- | --------------------- |
        // ------------------------ | ---------------- |
        // | Grupo 01 - Desenvolvimento, Manutenção e Doc. | N | S | N | S | N |
        // | Grupo 02 - Sustentação de Sistemas | S | S | N | N | S |
        // | Grupo 02 - Sustentação de Sistemas | N | N | N | N | S |
        // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
        if ("N".equalsIgnoreCase(demanda.getIcOrdemServicoAcionamento())
                && "S".equalsIgnoreCase(demanda.getIcQuantidade())
                && "N".equalsIgnoreCase(demanda.getIcValorOsInformado())
                && "S".equalsIgnoreCase(demanda.getIcCalculoQuantidadeOs())
                && "N".equalsIgnoreCase(demanda.getIcQuantidadeOs())) {
            return true;
        }

        return false;
    }

    public boolean isG2Mensalidade(DemandaDTO demanda) {
        // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
        // | no_tipo_grupo | ic_ordem_servico_acionamento | ic_quantidade |
        // ic_valor_os_informado | ic_calculo_quantidade_os | ic_quantidade_os |
        // | --------------------------------------------- |
        // ---------------------------- | ------------- | --------------------- |
        // ------------------------ | ---------------- |
        // | Grupo 01 - Desenvolvimento, Manutenção e Doc. | N | S | N | S | N |
        // | Grupo 02 - Sustentação de Sistemas | S | S | N | N | S |
        // | Grupo 02 - Sustentação de Sistemas | N | N | N | N | S |
        // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
        if ("N".equalsIgnoreCase(demanda.getIcOrdemServicoAcionamento())
                && "N".equalsIgnoreCase(demanda.getIcQuantidade())
                && "N".equalsIgnoreCase(demanda.getIcValorOsInformado())
                && "N".equalsIgnoreCase(demanda.getIcCalculoQuantidadeOs())
                && "S".equalsIgnoreCase(demanda.getIcQuantidadeOs())) {
            return true;
        }

        return false;
    }
    
    public boolean isG2SustentacaoSistemas(DemandaDTO demanda) {
        // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
        // | no_tipo_grupo | ic_ordem_servico_acionamento | ic_quantidade |
        // ic_valor_os_informado | ic_calculo_quantidade_os | ic_quantidade_os |
        // | --------------------------------------------- |
        // ---------------------------- | ------------- | --------------------- |
        // ------------------------ | ---------------- |
        // | Grupo 01 - Desenvolvimento, Manutenção e Doc. | N | S | N | S | N |
        // | Grupo 02 - Sustentação de Sistemas | S | S | N | N | S |
        // | Grupo 02 - Sustentação de Sistemas | N | N | N | N | S |
        // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
        if ("S".equalsIgnoreCase(demanda.getIcOrdemServicoAcionamento())
                && "S".equalsIgnoreCase(demanda.getIcQuantidade())
                && "N".equalsIgnoreCase(demanda.getIcValorOsInformado())
                && "N".equalsIgnoreCase(demanda.getIcCalculoQuantidadeOs())
                && "S".equalsIgnoreCase(demanda.getIcQuantidadeOs())) {
            return true;
        }

        if ("N".equalsIgnoreCase(demanda.getIcOrdemServicoAcionamento())
                && "S".equalsIgnoreCase(demanda.getIcQuantidade())
                && "N".equalsIgnoreCase(demanda.getIcValorOsInformado())
                && "S".equalsIgnoreCase(demanda.getIcCalculoQuantidadeOs())
                && "S".equalsIgnoreCase(demanda.getIcQuantidadeOs())) {
            return true;
        }        
        
        return false;
    }
}
