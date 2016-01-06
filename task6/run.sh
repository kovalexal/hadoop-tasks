#!/bin/bash

export PYTHONHASHSEED=1234
export PYSPARK_PYTHON=/usr/bin/python3
export PYSPARK_DRIVER_PYTHON=/usr/bin/python3

spark-submit \
	--conf spark.executorEnv.PYTHONHASHSEED=1234 \
	--num-executors 16 \
	main.py \
	--candles.securities=".*" \
	--candle.date.from=00000000 \
	--candle.date.to=99999999 \
	--candle.time.from=0000 \
	--candle.time.to=9999 \
	--candle.widths=5,10 \
	--candle.shifts=0,1,2 \
	--input="wasb://financedata@bigdatamsu2.blob.core.windows.net/" \
	--output="wasb:///candles/output" \
