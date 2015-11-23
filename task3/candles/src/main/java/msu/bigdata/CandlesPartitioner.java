package msu.bigdata;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.mapreduce.Partitioner;

public class CandlesPartitioner extends Partitioner<CandleKey, FloatWritable> {
    @Override
    public int getPartition(CandleKey key, FloatWritable value, int numReduceTasks) {
        String keyBase = key.getInstrument().toString() + key.getCandleMoment().toString();
        return (keyBase.hashCode() & Integer.MAX_VALUE) % numReduceTasks;
    }
}
