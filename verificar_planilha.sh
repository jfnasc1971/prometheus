#!/bin/bash
IFS=$'\n'

BASE_DIR="$(pwd)"

for f in $(find -L "$BASE_DIR" -name 'SIGCT_Demonstrativo_*.xlsx');do 
  ssconvert -O 'sheet=Ajuste separator=; format=preserve' --recalc $f /tmp/1.txt &>/dev/null;
  if [ -f /tmp/1.txt ];then
    op="$(cat /tmp/1.txt | grep 'Valor Ajuste')";
    echo "$f $op"|sed -e 's|"||gi'
  fi
done
