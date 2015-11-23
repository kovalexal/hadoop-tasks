package msu.bigdata;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CandlesMapper extends Mapper<LongWritable, Text, CandleKey, FloatWritable> {

    private static long width;
    private static String securities, date_from, date_to, time_start, time_stop;
    private static SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    protected void setup(Context context) throws InterruptedException {
        Configuration conf = context.getConfiguration();

        width = conf.getLong("candle.width", 0);
        securities = conf.get("candle.securities");
        date_from = conf.get("candle.date.from");
        date_to = conf.get("candle.date.to");
        time_start = conf.get("candle.time.start");
        time_stop = conf.get("candle.time.stop");
    }

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        try {
            String line = value.toString();
            if (line.startsWith("#"))
                return;

            String[] tokens = line.split(",");

            // Check that instrument matches regexp
            String instrument = tokens[0];
            if (!instrument.matches(securities))
                return;

            // Check that date is in requested range
            String date = tokens[2].substring(0, 8);
            if (!(date.compareTo(date_from) >= 0 && date.compareTo(date_to) <= 0))
                return;

            // Check that time is in requested range
            String time = tokens[2].substring(8, 17);
            if (!(time.compareTo(time_start) >= 0 && time.compareTo(time_stop) <= 0))
                return;

            // Get price
            Float price = Float.parseFloat(tokens[4]);

            // Get current candle start time
            Date candle_start_date = new Date((long) Math.floor(date_format.parse(tokens[2]).getTime() / (double) width) * width);
            String candle_start = date_format.format(candle_start_date);

            CandleKey outputKey = new CandleKey(new Text(instrument), new Text(candle_start), new LongWritable(Long.parseLong(tokens[3])));
            FloatWritable outputValue = new FloatWritable(price);

            // Write read data into context
            context.write(outputKey, outputValue);
        } catch (Exception e) {
            return;
        }
    }
}