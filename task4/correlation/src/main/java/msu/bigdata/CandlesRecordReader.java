package msu.bigdata;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;

import java.io.IOException;
import java.util.ArrayList;

public class CandlesRecordReader extends RecordReader<Text, InstrumentAndClosePrice> {
    public static final String KEY_VALUE_SEPERATOR =
            "mapreduce.input.candlesrecordreader.key.value.separator";

    private final LineRecordReader lineRecordReader;
    private byte separator = (byte) ',';

    private Text key;
    private InstrumentAndClosePrice value;

    public CandlesRecordReader(Configuration conf) throws IOException {
        lineRecordReader = new LineRecordReader();
        String sepStr = conf.get(KEY_VALUE_SEPERATOR, ",");
        this.separator = (byte) sepStr.charAt(0);
    }

    public void initialize(InputSplit genericSplit, TaskAttemptContext context) throws IOException {
        lineRecordReader.initialize(genericSplit, context);
    }

    public static ArrayList<Text> breakOnSeparator(Text value, byte sep) {
        ArrayList<Text> result = new ArrayList<Text>();

        byte[] line = value.getBytes();
        int lineLen = value.getLength();

        if (line == null)
            return result;

        int tokenStart = 0;
        for (int currentByte = 0; currentByte < lineLen; ++currentByte) {

            if (line[currentByte] == sep) {
                Text token = new Text();
                token.set(line, tokenStart, currentByte - tokenStart);
                result.add(token);

                tokenStart = currentByte + 1;
            }
        }

        if (tokenStart < lineLen - 1) {
            Text token = new Text();
            token.set(line, tokenStart, lineLen - tokenStart);
            result.add(token);

            tokenStart = lineLen;
        }

        return result;
    };

    public synchronized boolean nextKeyValue() throws IOException {
        if (lineRecordReader.nextKeyValue()) {
            Text innerValue = lineRecordReader.getCurrentValue();

            ArrayList<Text> tokens = breakOnSeparator(innerValue, this.separator);

            if (tokens.size() == 0)
                return false;

            Text moment = tokens.get(1);
            Text instrument = tokens.get(0);
            FloatWritable closePrice = new FloatWritable(Float.parseFloat(tokens.get(5).toString()));

            key = moment;
            value = new InstrumentAndClosePrice(instrument, closePrice);

            return true;

        } else {
            return false;
        }
    }

    public Text getCurrentKey() { return key; }

    public InstrumentAndClosePrice getCurrentValue() { return value; }

    public float getProgress() throws IOException {
        return lineRecordReader.getProgress();
    }

    public synchronized void close() throws IOException {
        lineRecordReader.close();
    }

}
