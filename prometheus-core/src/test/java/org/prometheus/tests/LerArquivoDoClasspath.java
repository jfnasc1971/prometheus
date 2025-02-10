package org.prometheus.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.poi.xssf.usermodel.XSSFColor;

public class LerArquivoDoClasspath {
    public static void main(String[] args) {
        // Nome do arquivo no classpath (deve estar na pasta src/main/resources, por
        // exemplo)
        String nomeArquivo = "RGB_Colors.csv";

        // Obtém o InputStream do arquivo usando o ClassLoader
        InputStream inputStream = LerArquivoDoClasspath.class.getClassLoader().getResourceAsStream(nomeArquivo);

        // Verifica se o arquivo foi encontrado
        if (inputStream == null) {
            System.err.println("Arquivo não encontrado no classpath: " + nomeArquivo);
            return;
        }

        // Lê o arquivo linha por linha e imprime no console
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String linha = null;
            while ((linha = reader.readLine()) != null) {
                System.out.println(linha.split(";")[0]+"("+linha.split(";")[1]+"), //");
//                String bytes = linha.split(";")[1];
//                String template = "new XSSFColor(new byte[]{(byte) %s, (byte) %s, (byte) %s}, null); //";
//                System.out.print(String.format(template, bytes.split(",")[0], bytes.split(",")[1], bytes.split(",")[2]));
//                System.out.println(linha.split(";")[2]);
//                System.out.println();
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}