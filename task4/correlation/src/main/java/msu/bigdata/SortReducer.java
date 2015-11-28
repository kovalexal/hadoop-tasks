package msu.bigdata;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;


public class SortReducer extends Reducer<DoubleWritable, CalculatedCorrelation, NullWritable, CalculatedCorrelation> {

    private static NullWritable outputKey = NullWritable.get();

    public void reduce(DoubleWritable key, Iterable<CalculatedCorrelation> values, Context context) throws IOException, InterruptedException {
        for (CalculatedCorrelation value : values) {
            context.write(outputKey, value);
        }
    }

}
