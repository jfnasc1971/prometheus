#!/bin/bash
IFS=$'\n'
JAVA_CMD="/projetos/lib/jvm/jdk1.8.0_231/bin/java"
CLASSPATH="/projetos/prometheus/dist/prometheus.jar"

$JAVA_CMD -Dfile.encoding=UTF-8 -classpath "$CLASSPATH" org.prometheus.relatorios.RelatorioEquipe $1

echo -e "\n\n"
echo "----------------------------------------------------"
echo "VALORES DE AJUSTE"
echo "----------------------------------------------------"

for f in $(find Demonstrativos -name 'SIGCT_Demonstrativo_*.xlsx');do
  rm -f /tmp/1.txt
  ssconvert -O 'sheet=Ajuste separator=; format=preserve' --recalc $f /tmp/1.txt &>/dev/null;
  op="$(cat /tmp/1.txt | grep 'Valor Ajuste')";
  echo "$f $op"|sed -e 's|"||gi'
done;

echo -e "\n\n"
