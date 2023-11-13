#!/bin/bash
IFS=$'\n'
JAVA_CMD="/projetos/lib/jvm/jdk1.8.0_231/bin/java"
CLASSPATH="/projetos/prometheus/Fontes/prometheus-core/target/prometheus.jar"

if [ -n "$1" ];then
  $JAVA_CMD -Dfile.encoding=UTF-8 -classpath "$CLASSPATH" org.prometheus.relatorios.RelatorioEquipe $1
elif [ -f lista_equipes.properties ];then
  $JAVA_CMD -Dfile.encoding=UTF-8 -classpath "$CLASSPATH" org.prometheus.relatorios.RelatorioEquipe
else
  clear
  echo ""
  echo "ERRO! NAO foi informada nenhum equipe e NAO encontrei o arquivo lista_equipes.properties. :("
  echo ""
  exit
fi

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
