package msu.bigdata;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.InputSampler;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.mapreduce.lib.partition.TotalOrderPartitioner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.lang.Exception;

public class FinanceDataAnalysis {

    public static class Map extends Mapper<LongWritable, Text, DayInstrumentWritable, FloatWritable> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            try {
                String line = value.toString();
                if (line.startsWith("#"))
                    return;

                String[] tokens = line.split(",");

                DayInstrumentWritable di = new DayInstrumentWritable(tokens[2].substring(0, 8), tokens[0]);
                FloatWritable price = new FloatWritable(Float.parseFloat(tokens[4]));

                context.write(di, price);
            } catch (Exception e) {
                return;
            }
        }
    }

    public static class Combine extends Reducer<DayInstrumentWritable, FloatWritable, DayInstrumentWritable, FloatWritable> {
        public void reduce(DayInstrumentWritable key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {
            float min = Float.MAX_VALUE;
            float max = -1 * Float.MAX_VALUE;

            for (FloatWritable val: values) {
                float value = val.get();
                if (value < min)
                    min = value;
                if (value > max)
                    max = value;
            }

            context.write(key, new FloatWritable(min));
            context.write(key, new FloatWritable(max));
        }
    }

    public static class Reduce extends Reducer<DayInstrumentWritable, FloatWritable, DayInstrumentWritable, MinMaxFloatWritable> {
        public void reduce(DayInstrumentWritable key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {
            float min = Float.MAX_VALUE;
            float max = -1 * Float.MAX_VALUE;

            for (FloatWritable val: values) {
                float value = val.get();
                if (value < min)
                    min = value;
                if (value > max)
                    max = value;
            }

            context.write(key, new MinMaxFloatWritable(min, max));
        }
    }


    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        conf.set("mapred.textoutputformat.separator", " ");

        FileSystem fs = FileSystem.get(conf);

        String jobName = "FinanceDataAnalysis";
        String mapJobName = jobName + "-Map";
        String reduceJobName = jobName + "-Reduce";

        String inputDir = args[0];
        String outputDir = args[1];
        String numReduceTasksStr = args[2];

        Path mapInputPath = new Path(inputDir);
        Path mapOutputPath = new Path(outputDir + "-inter");
        Path reduceOutputPath = new Path(outputDir);
        Path partitionPath = new Path(outputDir + "-part.lst");
        int numReduceTasks = Integer.parseInt(numReduceTasksStr);

        /*********************************
        * Map Job
        *********************************/
        Job mapJob = Job.getInstance(conf, mapJobName);
        mapJob.setJarByClass(FinanceDataAnalysis.class);
        mapJob.setMapperClass(Map.class);

        mapJob.setCombinerClass(Combine.class);
//        mapJob.setNumReduceTasks(0);

        mapJob.setMapOutputKeyClass(DayInstrumentWritable.class);x
        mapJob.setMapOutputValueClass(FloatWritable.class);

        mapJob.setOutputKeyClass(DayInstrumentWritable.class);
        mapJob.setOutputValueClass(FloatWritable.class);

        mapJob.setInputFormatClass(TextInputFormat.class);
        mapJob.setOutputFormatClass(SequenceFileOutputFormat.class);

        TextInputFormat.addInputPath(mapJob, mapInputPath);
        SequenceFileOutputFormat.setOutputPath(mapJob, mapOutputPath);

        int exitCode = mapJob.waitForCompletion(true) ? 0 : 1;
        if (exitCode != 0) {
            fs.delete(mapOutputPath, true);
            System.exit(exitCode);
        }

        /*********************************
         * Reduce Job
         *********************************/
        Job reduceJob = Job.getInstance(conf, reduceJobName);
        reduceJob.setNumReduceTasks(numReduceTasks);
        reduceJob.setJarByClass(FinanceDataAnalysis.class);

        reduceJob.setReducerClass(Reduce.class);

        reduceJob.setMapOutputKeyClass(DayInstrumentWritable.class);
        reduceJob.setMapOutputValueClass(FloatWritable.class);

        reduceJob.setOutputKeyClass(DayInstrumentWritable.class);
        reduceJob.setOutputValueClass(MinMaxFloatWritable.class);

        reduceJob.setInputFormatClass(SequenceFileInputFormat.class);
        reduceJob.setOutputFormatClass(TextOutputFormat.class);

        SequenceFileInputFormat.addInputPath(reduceJob, mapOutputPath);
        TextOutputFormat.setOutputPath(reduceJob, reduceOutputPath);

        reduceJob.setPartitionerClass(TotalOrderPartitioner.class);
        TotalOrderPartitioner.setPartitionFile(reduceJob.getConfiguration(), partitionPath);
        InputSampler.writePartitionFile(reduceJob, new InputSampler.RandomSampler(1, 10000));

        exitCode = reduceJob.waitForCompletion(true) ? 0 : 1;
        fs.delete(mapOutputPath, true);
        fs.delete(partitionPath, true);

//        Counters counters = reduceJob.getCounters();
//        for (CounterGroup group : counters) {
//            System.out.println("- Counter Group: " + group.getDisplayName() + " (" + group.getName() + ")");
//            System.out.println("  number of counters in this group: " + group.size());
//            for (Counter counter : group) {
//                System.out.println("  - " + counter.getDisplayName() + ": " + counter.getName() + " - " + counter.getValue());
//            }
//        }

        System.exit(exitCode);
    }
}
