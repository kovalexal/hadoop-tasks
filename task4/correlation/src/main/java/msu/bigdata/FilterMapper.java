package msu.bigdata;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class FilterMapper extends Mapper<Text, InstrumentAndClosePrice, Text, InstrumentAndClosePrice> {

    private static String dateFromR, dateToR, timeFromR, timeToR;
    private static final Log LOG = LogFactory.getLog(FilterMapper.class);

    protected void setup(Context context) throws InterruptedException {
        Configuration conf = context.getConfiguration();

        dateFromR = conf.get("candle.date.from");
        dateToR = conf.get("candle.date.to");
        timeFromR = conf.get("candle.time.from");
        timeToR = conf.get("candle.time.to");
    }

    public void map(Text key, InstrumentAndClosePrice value, Context context) throws IOException, InterruptedException {

        try {

            String keyString = key.toString();
            String date = keyString.substring(0, 8);
            String time = keyString.substring(8, 12);


            // Output only if date and time is in range
            if (date.compareTo(dateFromR) >= 0 && date.compareTo(dateToR) < 0)
                if (time.compareTo(timeFromR) >= 0 && time.compareTo(timeToR) < 0) {
                    context.write(key, value);
                }

        } catch (Exception e) {
            return;
        }
    }

}
