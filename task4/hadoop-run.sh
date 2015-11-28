#!/bin/bash

for i in 1 2 3 4 6 8
do
	echo Processing with $i reducers
	output_file=result_2nodes_${i}reducers.txt
	export HADOOP_CLASSPATH=/home/kovalexal/correlation-1.0.jar
	params="-D mapred.reduce.tasks=${i} -D candle.date.from=19000101 -D candle.date.to=20200101 -D candle.time.from=1000 -D candle.time.to=1800"
	hadoop fs -rm -r -f /output
	hadoop fs -rm -r -f /output-inter1
	hadoop fs -rm -r -f /output-inter2
	echo $params
	{ time { hadoop jar correlation-1.0.jar $params wasb:///task4 wasb:///output 2> $output_file; } } 2>> $output_file
done
exit 0