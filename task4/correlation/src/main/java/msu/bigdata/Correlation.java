package msu.bigdata;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class Correlation extends Configured implements Tool {
    public static void main(String[] args) throws Exception {
        int result = ToolRunner.run(new Configuration(), new Correlation(), args);
        System.exit(result);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();

        FileSystem fs = FileSystem.get(conf);

        String jobName = "Correlation";
        String joinJobName = jobName + "-Join";
        String calculateJobName = jobName + "-Calculate";
        String sortJobName = jobName + "-Sort";

        String inputDir = args[0];
        String outputDir = args[1];

        Path joinInputPath = new Path(inputDir);
        Path joinOutputPath = new Path(outputDir + "-inter1");
        Path calculateOutputPath = new Path(outputDir + "-inter2");
        Path sortOutputPath = new Path(outputDir);

        /*********************************
         * Join Job
         *********************************/

        // Setup job
        Job joinJob = Job.getInstance(conf, joinJobName);
        joinJob.setJarByClass(Correlation.class);

        // Set input and output paths
        FileInputFormat.addInputPath(joinJob, joinInputPath);
        FileOutputFormat.setOutputPath(joinJob, joinOutputPath);

        // Set job map settings
        joinJob.setMapperClass(FilterMapper.class);
        joinJob.setMapOutputKeyClass(Text.class);
        joinJob.setMapOutputValueClass(InstrumentAndClosePrice.class);

        // Set reducer settings
        joinJob.setReducerClass(JoinReducer.class);
        joinJob.setOutputKeyClass(CorrelationKey.class);
        joinJob.setOutputValueClass(CorrelationValue.class);

        // Set input and output format class
        joinJob.setInputFormatClass(CandlesInputFormat.class);
        joinJob.setOutputFormatClass(SequenceFileOutputFormat.class);

        int exitCode = joinJob.waitForCompletion(true) ? 0 : 1;
        if (exitCode != 0) {
            fs.delete(joinOutputPath, true);
            return exitCode;
        }

        /*********************************
         * Calculate correlation job
         *********************************/

        // Setup job
        Job calculateCorrelationJob = Job.getInstance(conf, calculateJobName);
        calculateCorrelationJob.setJarByClass(Correlation.class);

        // Set input and output paths
        SequenceFileInputFormat.addInputPath(calculateCorrelationJob, joinOutputPath);
        FileOutputFormat.setOutputPath(calculateCorrelationJob, calculateOutputPath);

        // Set mapper settings
        calculateCorrelationJob.setMapperClass(Mapper.class);
        calculateCorrelationJob.setMapOutputKeyClass(CorrelationKey.class);
        calculateCorrelationJob.setMapOutputValueClass(CorrelationValue.class);

        // Set secondary sort settings
        calculateCorrelationJob.setGroupingComparatorClass(CorrelationKeyGroupComparator.class);

        // Set reducer settings
        calculateCorrelationJob.setReducerClass(CorrelationReducer.class);
        calculateCorrelationJob.setOutputKeyClass(DoubleWritable.class);
        calculateCorrelationJob.setOutputValueClass(CalculatedCorrelation.class);

        // Set input and output format class
        calculateCorrelationJob.setInputFormatClass(SequenceFileInputFormat.class);
        calculateCorrelationJob.setOutputFormatClass(SequenceFileOutputFormat.class);

        exitCode = calculateCorrelationJob.waitForCompletion(true) ? 0 : 1;
        fs.delete(joinOutputPath, true);

        if (exitCode != 0) {
            fs.delete(calculateOutputPath, true);
            return exitCode;
        }

        /*********************************
         * Sort job
         *********************************/

        // Setup job
        Job sortJob = Job.getInstance(conf, sortJobName);
        sortJob.setJarByClass(Correlation.class);

        // Set input and output paths
        SequenceFileInputFormat.addInputPath(sortJob, calculateOutputPath);
        FileOutputFormat.setOutputPath(sortJob, sortOutputPath);

        // Set mapper settings
        sortJob.setMapperClass(Mapper.class);
        sortJob.setMapOutputKeyClass(DoubleWritable.class);
        sortJob.setMapOutputValueClass(CalculatedCorrelation.class);

        // Set grouping comparator class
        sortJob.setGroupingComparatorClass(SortKeyGroupComparator.class);
        sortJob.setSortComparatorClass(SortComparator.class);

        // Set reducer settings
        sortJob.setReducerClass(SortReducer.class);
        sortJob.setOutputKeyClass(NullWritable.class);
        sortJob.setOutputValueClass(CalculatedCorrelation.class);

        // Set input and output format class
        sortJob.setInputFormatClass(SequenceFileInputFormat.class);
        sortJob.setOutputKeyClass(TextOutputFormat.class);

        // Set only one reducer
        sortJob.setNumReduceTasks(1);

        exitCode = sortJob.waitForCompletion(true) ? 0 : 1;
        fs.delete(calculateOutputPath, true);

        if (exitCode != 0) {
            fs.delete(sortOutputPath, true);
            return exitCode;
        }

        return 0;
    }
}
