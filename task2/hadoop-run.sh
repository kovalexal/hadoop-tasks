#!/bin/bash

for i in 1 2 3 4 6 8
do
	echo Processing with $i reducers
	output_file=result_2nodes_${i}reducers.txt
	hadoop fs -rm -r -f /financedata/output-inter
	hadoop fs -rm -r -f /financedata/output
	rm -Rf finance_result.txt
	{ time { hadoop jar FinanceDataAnalysis.jar msu.bigdata.FinanceDataAnalysis wasb://financedata@bigdatamsu.blob.core.windows.net/ wasb:///financedata/output $i 2> $output_file && hadoop fs -getmerge /financedata/output finance_result.txt; } } 2>> $output_file
done
exit 0