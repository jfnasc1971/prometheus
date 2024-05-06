@echo off

chcp 65001

lib\jdk1.8\bin\java.exe -Dfile.encoding=UTF-8 -Dspring.profiles.active=prd -cp prometheus.jar org.prometheus.relatorios.RelatorioEquipe
