#!/usr/bin/env python3
import re
import argparse
from datetime import datetime
from math import sqrt

from pyspark import SparkContext, StorageLevel

start_time = datetime.now()

parser = argparse.ArgumentParser()
parser.add_argument('--candles.securities', action='store', required=True, type=str, dest='candles_securities')
parser.add_argument('--candle.date.from', action='store', required=True, type=(lambda x: x[0:8]), dest='candle_date_from')
parser.add_argument('--candle.date.to', action='store', required=True, type=(lambda x: x[0:8]), dest='candle_date_to')
parser.add_argument('--candle.time.from', action='store', required=True, type=(lambda x: x[0:4]), dest='candle_time_from')
parser.add_argument('--candle.time.to', action='store', required=True, type=(lambda x: x[0:4]), dest='candle_time_to')
parser.add_argument('--candle.widths', action='store', required=True, type=(lambda x: list(map(int, x.split(',')))), dest='candle_widths')
parser.add_argument('--candle.shifts', action='store', required=True, type=(lambda x: list(map(int, x.split(',')))), dest='candle_shifts')
parser.add_argument('--input', action='store', required=True, type=str, dest='input')
parser.add_argument('--output', action='store', required=True, type=str, dest='output')
args = parser.parse_args()

sc = SparkContext(appName='Correlations')

securities = sc.broadcast(re.compile(args.candles_securities))
date_from = sc.broadcast(args.candle_date_from)
date_to = sc.broadcast(args.candle_date_to)
time_from = sc.broadcast(args.candle_time_from)
time_to = sc.broadcast(args.candle_time_to)


def filter_moment(x):
    if securities.value.match(x[0]):
        if date_from.value <= x[1][0:8] < date_to.value:
            if time_from.value <= x[1][8:12] < time_to.value:
                return True
    return False


def moment_to_candle_start(x, width):
    return (x[1] // width * width, x[0]), (x[2], x[3])


def combinations(x, shift):
    results = []
    lst1, lst2 = x[1]
    for c1 in lst1:
        for c2 in lst2:
            if c1[0] < c2[0]:
                results.append(((c1[0], c2[0], shift), (x[0], c1[1], c2[1])))
            elif c1[0] > c2[0]:
                results.append(((c2[0], c1[0], -shift), (x[0], c2[1], c1[1])))

    return set(results)


def calculate_correlation(pricesList):
    values = sorted(pricesList)
    correlation = None

    s_xy = s_x = s_y = s_x2 = s_y2 = n = 0

    for prev, current in zip(values, values[1:]):
        x = (current[1] - prev[1]) / prev[1]
        y = (current[2] - prev[2]) / prev[2]

        s_x += x
        s_y += y
        s_x2 += x ** 2
        s_y2 += y ** 2
        s_xy += x * y

        n+=1

    try:
        correlation = (n * s_xy - s_x * s_y) / (sqrt( (n * s_x2 - s_x ** 2) * (n * s_y2 - s_y ** 2) ))
    except:
        pass
    return correlation

input = sc\
    .textFile(args.input)\
    .map(lambda line: line.split(','))\
    .filter(lambda splits: len(splits) == 8 and splits[0][0] != '#')\
    .map(lambda x: (x[0], x[2], int(x[3]), float(x[4])))\
    .filter(filter_moment)\
    .map(lambda x: (x[0], int(datetime.strptime(x[1], '%Y%m%d%H%M%S%f').timestamp()), x[2], x[3]))\
    .cache()

result = sc.emptyRDD()

for width in args.candle_widths:
    candles = input\
        .map(lambda x: moment_to_candle_start(x, width))\
        .reduceByKey(lambda x, y: x if x[0] > y[0] else y) \
        .map(lambda x: (x[0][0], (x[0][1], x[1][1])))\
        .groupByKey()\
        .cache()

    for shift in args.candle_shifts:
        shifted_candles = candles\
            .map(lambda x: (x[0] + shift * width, x[1])).cache()

        correlations = candles\
            .join(shifted_candles)\
            .flatMap(lambda x: combinations(x, shift))\
            .groupByKey()\
            .mapValues(calculate_correlation)\
            .filter(lambda x: x[1] is not None)\
            .map(lambda x: (x[0][0], x[0][1], width, x[0][2], x[1])).cache()

        result = result.union(correlations)

result = result.repartition(1).sortBy((lambda x: abs(x[4])), ascending=False).map(lambda x: ' '.join(map(str, x))).saveAsTextFile(args.output)

sc.stop()

end_time = datetime.now()

print('Execution took', end_time - start_time)