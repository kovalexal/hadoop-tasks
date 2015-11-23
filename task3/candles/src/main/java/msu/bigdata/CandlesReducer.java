package msu.bigdata;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import java.io.IOException;
import java.util.Iterator;

public class CandlesReducer extends Reducer<CandleKey, FloatWritable, Text, CandleValue> {

    private static MultipleOutputs mos;

    @Override
    public void setup(Context context) {
        mos = new MultipleOutputs(context);
    }

    public void reduce(CandleKey key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {

        // Create an iterator for all records
        final Iterator<FloatWritable> itr = values.iterator();

        // Remember high, low, open and close prices
        FloatWritable currentPrice = itr.next();

        // Retain open, high, low and close prices
        float openPrice, highPrice, lowPrice, closePrice;
        openPrice = highPrice = lowPrice = currentPrice.get();

        // Iterate through data
        while (itr.hasNext()) {
            currentPrice = itr.next();

            float price = currentPrice.get();

            if (price < lowPrice)
                lowPrice = price;
            if (price > highPrice)
                highPrice = price;
        }

        // Remember the last element
        closePrice = currentPrice.get();

        CandleValue outputValue = new CandleValue(key.getCandleMoment(), new FloatWritable(openPrice), new FloatWritable(highPrice), new FloatWritable(lowPrice), new FloatWritable(closePrice));

        // Output results
        mos.write("OutputByInstrument", new Text(key.getInstrument()), outputValue, key.getInstrument().toString() + ".csv");

    }

    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        mos.close();
    }
}
