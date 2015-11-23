#!/bin/bash

for i in 1 2 4 6 8
do
	echo Processing with $i reducers
	output_file=result_2nodes_${i}reducers.txt
	params="-D mapred.reduce.tasks=${i} -D candle.width=300000 -D candle.securities=.* -D candle.date.from=19000101 -D candle.date.to=20200101 -D candle.time.from=1000 -D candle.time.to=1800"
	hadoop fs -rm -r -f /candles/output
	echo $params
	{ time { hadoop jar candles-1.0.jar $params wasb://financedata@bigdatamsu.blob.core.windows.net/ wasb:///candles/output 2> $output_file; } } 2>> $output_file
done
exit 0
