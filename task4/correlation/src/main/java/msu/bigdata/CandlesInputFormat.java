package msu.bigdata;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import java.io.IOException;


public class CandlesInputFormat extends FileInputFormat<Text, InstrumentAndClosePrice> {

    @Override
    public RecordReader<Text, InstrumentAndClosePrice> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException {
        context.setStatus(split.toString());
        return new CandlesRecordReader(context.getConfiguration());
    }

    @Override
    protected boolean isSplitable(JobContext context, Path file) {
        CompressionCodec codec = new CompressionCodecFactory(context.getConfiguration()).getCodec(file);
        return codec == null;
    }

}
