package org.prometheus.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class LerArquivoTextoJava8 {
    public static void main(String[] args) {
        // Caminho do arquivo de texto
        String caminhoArquivo = "/projetos/prometheus/src/prometheus-core/RGB_Colors.txt.csv";

        // Tenta abrir e ler o arquivo
        try (Stream<String> linhas = Files.lines(Paths.get(caminhoArquivo))) {
            // Itera sobre cada linha e imprime no console
            linhas.forEach(System.out::println);
        } catch (IOException e) {
            // Trata possíveis erros de leitura do arquivo
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}