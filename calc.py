#!/usr/bin/env python3

# Calculates statistics time + data in hadoop output file

import sys
import re

inputpath = sys.argv[1]

time_execution = 0
data_transmitted = 0

with open(inputpath, 'r') as in_file:
	for line in in_file:
		if "Total time spent by all map tasks (ms)=" in line:
			time_execution += int(re.findall(r'\d+', line)[0])

		if "Total time spent by all reduce tasks (ms)=" in line:
			time_execution += int(re.findall(r'\d+', line)[0])

		if "FILE: Number of bytes read=" in line:
			data_transmitted += int(re.findall(r'\d+', line)[0])			
		if "FILE: Number of bytes written=" in line:
			data_transmitted += int(re.findall(r'\d+', line)[0])
		if "WASB: Number of bytes read=" in line:
			data_transmitted += int(re.findall(r'\d+', line)[0])			
		if "WASB: Number of bytes written=" in line:
			data_transmitted += int(re.findall(r'\d+', line)[0])

		if "real" in line:
			print(line)

print(time_execution)
print(data_transmitted)
