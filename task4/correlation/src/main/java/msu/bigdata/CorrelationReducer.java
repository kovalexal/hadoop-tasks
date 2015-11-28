package msu.bigdata;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;


public class CorrelationReducer extends Reducer<CorrelationKey, CorrelationValue, DoubleWritable, CalculatedCorrelation> {

    private static NullWritable outputKey = NullWritable.get();

    public void reduce(CorrelationKey key, Iterable<CorrelationValue> values, Context context) throws IOException, InterruptedException {

        int n = 0;
        float prevPrice1, prevPrice2;
        float sumPrice1 = 0, sumPrice2 = 0, sum2Price1 = 0, sum2Price2 = 0, sumPrice1Price2 = 0;

        // Get first element prices
        final Iterator<CorrelationValue> itr = values.iterator();
        CorrelationValue currentValue = itr.next();
        prevPrice1 = currentValue.getClosePrice1().get();
        prevPrice2 = currentValue.getClosePrice2().get();

        // Iterate through other values and calculate statistics needed for correlation
        while (itr.hasNext()) {
            currentValue = itr.next();

            float currentPrice1 = currentValue.getClosePrice1().get();
            float currentPrice2 = currentValue.getClosePrice2().get();

            float x = (currentPrice1 - prevPrice1) / prevPrice1;
            float y = (currentPrice2 - prevPrice2) / prevPrice2;

            sumPrice1 += x;
            sumPrice2 += y;

            sum2Price1 += x * x;
            sum2Price2 += y * y;

            sumPrice1Price2 += x * y;

            prevPrice1 = currentPrice1;
            prevPrice2 = currentPrice2;

            n += 1;
        }

        if (n > 1) {
            double numerator = (n * sumPrice1Price2 - sumPrice1 * sumPrice2);;
            double denominator = (Math.sqrt(n * sum2Price1 - sumPrice1 * sumPrice1) * Math.sqrt(n * sum2Price2 - sumPrice2 * sumPrice2));

            if (denominator == 0)
                return;

            double correlation = numerator / denominator;

            CalculatedCorrelation outputValue = new CalculatedCorrelation(key.getInstrument1(), key.getInstrument2(), new DoubleWritable(correlation));
            context.write(new DoubleWritable(correlation), outputValue);
        }

    }

}
