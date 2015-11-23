package msu.bigdata;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Candles extends Configured implements Tool {
    private static SimpleDateFormat time_format = new SimpleDateFormat("HHmmssSSS");

    public static void main(String[] args) throws Exception {
        int result = ToolRunner.run(new Configuration(), new Candles(), args);
        System.exit(result);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();

        conf.set("mapred.textoutputformat.separator", ",");
        long width = conf.getLong("candle.width", 0);

        // Get time of first candle start
        Date time = time_format.parse(conf.get("candle.time.from") + "00000");
        time = new Date((long) Math.ceil(time.getTime() / (double) width) * width);
        conf.set("candle.time.start", time_format.format(time));

        // Get time of last candle end
        time = time_format.parse(conf.get("candle.time.to") + "00000");
        time = new Date((long) Math.ceil(time.getTime() / (double) width - 1) * width);
        conf.set("candle.time.stop", time_format.format(time));

        // Setup job parameters
        String jobName = "Candles";
        String inputDir = args[0];
        Path inputPath = new Path(inputDir);
        String outputDir = args[1];
        Path outputPath = new Path(outputDir);

        // Setup job
        Job candlesJob = Job.getInstance(conf, jobName);
        candlesJob.setJarByClass(Candles.class);

        // Set job map settings
        candlesJob.setMapperClass(CandlesMapper.class);
        candlesJob.setMapOutputKeyClass(CandleKey.class);
        candlesJob.setMapOutputValueClass(FloatWritable.class);

        // Secondary sort classes
        candlesJob.setPartitionerClass(CandlesPartitioner.class);
        candlesJob.setCombinerKeyGroupingComparatorClass(CandlesKeyComparator.class);
        candlesJob.setGroupingComparatorClass(CandlesGroupComparator.class);

        // Set job reduce settings
        candlesJob.setReducerClass(CandlesReducer.class);
        candlesJob.setOutputKeyClass(NullWritable.class);
        candlesJob.setOutputValueClass(CandleValue.class);

        candlesJob.setInputFormatClass(TextInputFormat.class);
        candlesJob.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(candlesJob, inputPath);
        FileOutputFormat.setOutputPath(candlesJob, outputPath);

        MultipleOutputs.addNamedOutput(candlesJob, "OutputByInstrument", TextOutputFormat.class, Text.class, CandleValue.class);

        return (candlesJob.waitForCompletion(true) ? 0 : 1);
    }
}
