package msu.bigdata;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;


public class JoinReducer extends Reducer<Text, InstrumentAndClosePrice, CorrelationKey, CorrelationValue> {

    public void reduce(Text key, Iterable<InstrumentAndClosePrice> values, Context context) throws IOException, InterruptedException {
        // Store previous values in array
        ArrayList<InstrumentAndClosePrice> previousValues = new ArrayList<InstrumentAndClosePrice>();

        for (InstrumentAndClosePrice currentValue : values) {
            for (InstrumentAndClosePrice previousValue : previousValues) {
                InstrumentAndClosePrice lowerValue, higherValue;

                if (previousValue.compareTo(currentValue) <= 0) {
                    lowerValue = previousValue;
                    higherValue = currentValue;
                } else {
                    lowerValue = currentValue;
                    higherValue = previousValue;
                }

                CorrelationKey outputKey = new CorrelationKey(lowerValue.getInstrument(), higherValue.getInstrument(), key);
                CorrelationValue outputValue = new CorrelationValue(key, lowerValue.getClosePrice(), higherValue.getClosePrice());
                context.write(outputKey, outputValue);
            }

            previousValues.add(new InstrumentAndClosePrice(currentValue));
        }

    }

}
