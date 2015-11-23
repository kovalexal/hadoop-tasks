package msu.bigdata;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class CandleValue implements WritableComparable<CandleValue> {
    private Text candleMoment;
    private FloatWritable open, high, low, close;

    public CandleValue() {
        set(new Text(), new FloatWritable(0), new FloatWritable(0), new FloatWritable(0), new FloatWritable(0));
    }

    public CandleValue(CandleValue o) {
        candleMoment = new Text(o.candleMoment);
        open = new FloatWritable(o.open.get());
        high = new FloatWritable(o.high.get());
        low = new FloatWritable(o.low.get());
        close = new FloatWritable(o.close.get());
    }

    public CandleValue(String candleMoment, float open, float high, float low, float close) {
        set(new Text(candleMoment), new FloatWritable(open), new FloatWritable(high), new FloatWritable(low), new FloatWritable(close));
    }

    public CandleValue(Text candleMoment, FloatWritable open, FloatWritable high, FloatWritable low, FloatWritable close) {
        set(candleMoment, open, high, low, close);
    }

    public void set(Text candleMoment, FloatWritable open, FloatWritable high, FloatWritable low, FloatWritable close) {
        this.candleMoment = candleMoment;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }

    public Text getCandleMoment() {
        return candleMoment;
    }

    public FloatWritable getOpen() {
        return open;
    }

    public FloatWritable getHigh() {
        return high;
    }

    public FloatWritable getLow() {
        return low;
    }

    public FloatWritable getClose() {
        return close;
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        candleMoment.readFields(in);
        open.readFields(in);
        high.readFields(in);
        low.readFields(in);
        close.readFields(in);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        candleMoment.write(out);
        open.write(out);
        high.write(out);
        low.write(out);
        close.write(out);
    }

    @Override
    public String toString() {
        return candleMoment + "," + open + "," + high + "," + low + "," + close;
    }

    @Override
    public int compareTo(CandleValue o) {
        return candleMoment.compareTo(o.candleMoment);
    }
}
