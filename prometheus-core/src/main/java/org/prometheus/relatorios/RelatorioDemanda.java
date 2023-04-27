package org.prometheus.relatorios;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.AppConfig;
import org.prometheus.relatorios.demanda.AbaDemanda;
import org.prometheus.utils.Utils;
import org.springframework.stereotype.Component;

@Component
public class RelatorioDemanda extends Relatorio {

    private static final String PATH = System.getProperty("user.dir") + File.separator + "Demonstrativos";

    public static void main(String[] args) {

        (new File(PATH)).mkdirs();

        RelatorioDemanda relatorio = AppConfig.getApplicationContext().getBean(RelatorioDemanda.class);
        String coDemanda = null;
        BufferedReader reader = null;

        if (args != null && args.length > 0) {
            coDemanda = args[0];
        }

        try {

            if (coDemanda == null) {
                reader = new BufferedReader(new FileReader("lista_demandas.properties"));

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

    public String gerar(String coDemanda) throws Exception {
        (new File(PATH)).mkdirs();

        File file = new File(PATH + "/SIGCT_Demonstrativo_" + coDemanda + ".xlsx");
        file.delete();
        FileOutputStream fos = new FileOutputStream(file);
        System.out.println(file.getAbsolutePath());

        XSSFWorkbook wb = new XSSFWorkbook();

        AbaDemanda abaDemanda = AppConfig.getApplicationContext().getBean(AbaDemanda.class);
        abaDemanda.write(wb, coDemanda);

        wb.write(fos);
        wb.close();

        return file.getAbsolutePath();
    }
}
