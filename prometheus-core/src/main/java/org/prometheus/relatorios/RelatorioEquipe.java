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
import org.prometheus.AppConfig;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.dto.InsumosFaturamentoDTO;
import org.prometheus.enums.DistribuicaoProfissionalEnum;
import org.prometheus.enums.TipoAtendimentoEnum;
import org.prometheus.relatorios.equipe.AbaAjuste;
import org.prometheus.relatorios.equipe.AbaCalculoValorDevido;
import org.prometheus.relatorios.equipe.AbaCalculoValorDevidoMilheiros;
import org.prometheus.relatorios.equipe.AbaCalculoValorDevidoPerfil;
import org.prometheus.relatorios.equipe.AbaCalculosAcionamento;
import org.prometheus.relatorios.equipe.AbaCalculosAcionamentoQualidade;
import org.prometheus.relatorios.equipe.AbaCalculosAcionamentoUnisys;
import org.prometheus.relatorios.equipe.AbaCalculosG4;
import org.prometheus.relatorios.equipe.AbaCalculosMilheiros;
import org.prometheus.relatorios.equipe.AbaCalculosSprint;
import org.prometheus.relatorios.equipe.AbaCalculosSprintUnisys;
import org.prometheus.relatorios.equipe.AbaEquipe;
import org.prometheus.relatorios.equipe.AbaPagamentos;
import org.prometheus.relatorios.equipe.AbaPagamentosPerfil;
import org.prometheus.relatorios.equipe.AbaServicosAcionamento;
import org.prometheus.relatorios.equipe.AbaServicosAcionamentoQualidade;
import org.prometheus.relatorios.equipe.AbaVigenciaMembros;
import org.prometheus.service.EquipeService;
import org.prometheus.service.FaturamentoService;
import org.prometheus.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RelatorioEquipe extends Relatorio {

    private static final Logger LOG = LogManager.getLogger(RelatorioEquipe.class.getSimpleName());

    public static final String ABA_CALCULOS = "Memória de Cálculo";
    public static final String ABA_CALCULO_VALOR_DEVIDO = "Cálculo Valor Devido";
    public static final String ABA_PAGAMENTOS = "Pagamentos";
    public static final String ABA_SERVICOS = "Serviços";

    @Autowired
    private EquipeService equipeService;

    @Autowired
    private FaturamentoService service;

    public static void main(String[] args) {

        java.util.logging.LogManager.getLogManager().reset();
        
        printVersion();

        (new File(PATH)).mkdirs();
        
        File file = (new File(PATH + File.separator + "consolidado.csv"));
        try {
            file.delete();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        String coEquipe = null;

        if (args != null && args.length > 0) {
            coEquipe = args[0];
        }

        RelatorioEquipe relatorio = AppConfig.getApplicationContext().getBean(RelatorioEquipe.class);
        relatorio.gerarRelatorios(coEquipe);
    }

    public void gerarRelatorios(String coEquipe) {

        String mensagemErro = null;
        RelatorioEquipe relatorio = AppConfig.getApplicationContext().getBean(RelatorioEquipe.class);
        BufferedReader reader = null;

        try {

            if (coEquipe == null) {

                if (!(new File("equipes.txt")).exists() && !(new File("contratos.txt")).exists()) {
                    mensagemErro = "Erro! Nao foi encontrado o arquivo equipes.txt ou contratos.txt :(";
                }
                ;

                if (mensagemErro != null) {
                    System.out.println();
                    System.out.println(mensagemErro);
                    System.out.println();
                    return;
                }

                if ((new File("contratos.txt")).exists()) {
                    reader = new BufferedReader(new FileReader("contratos.txt"));

                    while ((coEquipe = reader.readLine()) != null) {
                        if (Utils.isNotEmpty(coEquipe) && !coEquipe.trim().startsWith("#")) {
                            List<EquipeDTO> equipes = equipeService.findByContrato(coEquipe);
                            for (EquipeDTO equipe : equipes) {
                                relatorio.gerar(equipe.getCoEquipe().trim());
                            }
                        }
                    }
                }

                if ((new File("equipes.txt")).exists()) {
                    reader = new BufferedReader(new FileReader("equipes.txt"));

                    while ((coEquipe = reader.readLine()) != null) {
                        if (Utils.isNotEmpty(coEquipe) && !coEquipe.trim().startsWith("#")) {
                            relatorio.gerar(coEquipe.trim());// , DateUtil.parse("2018-08-20"));
                        }
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

        System.out.println("Encerrado! :)");
    }

    public String gerar(String coEquipe) {
        return gerar(coEquipe, (Date) null);
    }

    public String gerar(String coEquipe, Date dtFinalFatura) {

        List<FaturamentoDTO> faturamentos = service.listar(coEquipe, dtFinalFatura);

        if (Utils.isNotEmpty(faturamentos)) {
            try {
                return gerar(coEquipe, faturamentos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public String gerar(String coEquipe, List<FaturamentoDTO> faturamentos) throws Exception {

        EquipeDTO equipe = equipeService.findBy(coEquipe);

        if (Utils.isEmpty(equipe)) {
            LOG.info(String.format("A equipe informada %s não existe.", coEquipe));
            return null;
        }

        //String dir = ""; // equipe.getCoContrato().replaceAll("[ /]", "_");
        String dir = equipe.getCoContrato().replaceAll("[ /]", "_") + "/" + equipe.getNoTipoGrupo();

        (new File(PATH + "/" + dir)).mkdirs();

        File file = new File(PATH + "/" + dir + "/SIGCT_MemoriaCalculo_" + coEquipe + ".xlsx");
        file.delete();
        FileOutputStream fos = new FileOutputStream(file);

        LOG.info(file.getAbsolutePath());

        XSSFWorkbook wb = createWorkbook();

        LOG.info(equipe.getNuContrato() + " - " + equipe.getNoContrato() + //
                " - " + equipe.getNuEquipe() + " - " + equipe.getCoEquipe() + //
                " - " + equipe.getNoTipoGrupo() + //
                " - " + equipe.getIcTipoAtendimento());

        Map<Long, InsumosFaturamentoDTO> insumos = new LinkedHashMap<Long, InsumosFaturamentoDTO>();

        // Equipe
        AbaEquipe abaEquipe = AppConfig.getApplicationContext().getBean(AbaEquipe.class);
        abaEquipe.write(wb, coEquipe, faturamentos);

        // VIGENCIAS DOS MEMBROS
        // Nao vamos gerar para o MILHEIROS
        if (equipe.getNuTipoGrupo() != 70L) {
            AbaVigenciaMembros abaVigencias = AppConfig.getApplicationContext().getBean(AbaVigenciaMembros.class);
            abaVigencias.write(wb, coEquipe, faturamentos);
        }

        // PAGAMENTOS
        XSSFCell cellValorPagamentos = null;

        if (DistribuicaoProfissionalEnum.PERFIL.SIGLA.equals(equipe.getIcDistribuiProfissional())) {

            AbaPagamentosPerfil abaPagamentos = AppConfig.getApplicationContext().getBean(AbaPagamentosPerfil.class);
            cellValorPagamentos = abaPagamentos.gerar(wb, coEquipe, insumos);

        }

        if (DistribuicaoProfissionalEnum.GLOBAL.SIGLA.equals(equipe.getIcDistribuiProfissional())) {

            AbaPagamentos abaPagamentos = AppConfig.getApplicationContext().getBean(AbaPagamentos.class);
            cellValorPagamentos = abaPagamentos.write(wb, coEquipe);

        }

        //
        // Cálculos
        //
        if (!equipe.getCoContrato().equals("08718/2023")) {
            
            if (DistribuicaoProfissionalEnum.GLOBAL.SIGLA.equals(equipe.getIcDistribuiProfissional())) {

                if (TipoAtendimentoEnum.ACIONAMENTO.SIGLA.equals(equipe.getIcTipoAtendimento())) {

                    boolean isTipoGrupoG4 = equipe.getNoTipoGrupo().startsWith("Equipes de Grupo 04");

                    if (!isTipoGrupoG4) {
                        AbaCalculosAcionamento calculos = AppConfig.getApplicationContext()
                                .getBean(AbaCalculosAcionamento.class);
                        insumos = calculos.write(wb, coEquipe, faturamentos);
                    }

                    if (isTipoGrupoG4) {
                        AbaCalculosG4 calculos = AppConfig.getApplicationContext().getBean(AbaCalculosG4.class);
                        insumos = calculos.write(wb, coEquipe, faturamentos);
                    }

                }

                if (TipoAtendimentoEnum.SPRINT.SIGLA.equals(equipe.getIcTipoAtendimento())) {
                    AbaCalculosSprint calculos = AppConfig.getApplicationContext().getBean(AbaCalculosSprint.class);
                    insumos = calculos.gerar(wb, coEquipe, faturamentos);
                }
            }

            if (DistribuicaoProfissionalEnum.PERFIL.SIGLA.equals(equipe.getIcDistribuiProfissional())) {

                if (TipoAtendimentoEnum.ACIONAMENTO.SIGLA.equals(equipe.getIcTipoAtendimento())) {
                    AbaCalculosAcionamentoQualidade calculos = AppConfig.getApplicationContext()
                            .getBean(AbaCalculosAcionamentoQualidade.class);
                    calculos.gerar(wb, coEquipe, faturamentos, insumos);
                }

            }
        }

        if (equipe.getCoContrato().equals("08718/2023")) {

            if (TipoAtendimentoEnum.ACIONAMENTO.SIGLA.equals(equipe.getIcTipoAtendimento())) {
                
                if (equipe.getNuTipoGrupo() != 70L) {
                
                    AbaCalculosAcionamentoUnisys calculos = AppConfig.getApplicationContext()
                            .getBean(AbaCalculosAcionamentoUnisys.class);
                    calculos.gerar(wb, coEquipe, faturamentos, insumos);
                    
                }
                
                if (equipe.getNuTipoGrupo() == 70L) {
                    
                    AbaCalculosMilheiros calculos = AppConfig.getApplicationContext()
                            .getBean(AbaCalculosMilheiros.class);
                    calculos.gerar(wb, coEquipe, faturamentos, insumos);
                    
                }
                
            }

            if (TipoAtendimentoEnum.SPRINT.SIGLA.equals(equipe.getIcTipoAtendimento())) {
                AbaCalculosSprintUnisys calculos = AppConfig.getApplicationContext()
                        .getBean(AbaCalculosSprintUnisys.class);
                insumos = calculos.gerar(wb, coEquipe, faturamentos);
            }

        }

        //
        // Serviços (SOMENTE ACIONAMENTO)
        //
        if (TipoAtendimentoEnum.ACIONAMENTO.SIGLA.equals(equipe.getIcTipoAtendimento())) {

            if (DistribuicaoProfissionalEnum.GLOBAL.SIGLA.equals(equipe.getIcDistribuiProfissional())) {
                AbaServicosAcionamento servicos = AppConfig.getApplicationContext()
                        .getBean(AbaServicosAcionamento.class);
                servicos.write(wb, coEquipe, faturamentos);
            }

            if (DistribuicaoProfissionalEnum.PERFIL.SIGLA.equals(equipe.getIcDistribuiProfissional())) {
                AbaServicosAcionamentoQualidade servicos = AppConfig.getApplicationContext()
                        .getBean(AbaServicosAcionamentoQualidade.class);
                servicos.write(wb, coEquipe, faturamentos);
            }

        }

        XSSFCell cellValorDevido = null;

        //
        // Calculos Valor Devido
        //
        if (DistribuicaoProfissionalEnum.PERFIL.SIGLA.equals(equipe.getIcDistribuiProfissional())) {

            if (equipe.getNuTipoGrupo() != 70L) {
                AbaCalculoValorDevidoPerfil abaCalculoValorDevido = AppConfig.getApplicationContext()
                        .getBean(AbaCalculoValorDevidoPerfil.class);
                cellValorDevido = abaCalculoValorDevido.write(wb, coEquipe, faturamentos, insumos);
            }

            if (equipe.getNuTipoGrupo() == 70L) {
                AbaCalculoValorDevidoMilheiros abaCalculoValorDevido = AppConfig.getApplicationContext()
                        .getBean(AbaCalculoValorDevidoMilheiros.class);
                cellValorDevido = abaCalculoValorDevido.write(wb, coEquipe, faturamentos, insumos);
            }
            
        }

        if (DistribuicaoProfissionalEnum.GLOBAL.SIGLA.equals(equipe.getIcDistribuiProfissional())) {
            AbaCalculoValorDevido abaCalculoValorDevido = AppConfig.getApplicationContext()
                    .getBean(AbaCalculoValorDevido.class);
            cellValorDevido = abaCalculoValorDevido.write(wb, coEquipe, faturamentos, insumos);
        }

        // Ajuste
        AbaAjuste abaAjuste = AppConfig.getApplicationContext().getBean(AbaAjuste.class);
        abaAjuste.write(wb, equipe, cellValorDevido, cellValorPagamentos);

        wb.write(fos);
        wb.close();

        return file.getAbsolutePath();
    }

    private XSSFWorkbook createWorkbook() {
        
        XSSFWorkbook wb = new XSSFWorkbook();

        carregarFontes(wb);
        carregarEstilos(wb);
        
        return wb;
    }
}
