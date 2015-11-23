package msu.bigdata;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class CandleKey implements WritableComparable<CandleKey> {

    private Text instrument;
    private Text candleMoment;
    private LongWritable recordId;

    public CandleKey() {
        set(new Text(), new Text(), new LongWritable(0));
    }

    public CandleKey(String i, String c, long r) {
        set(new Text(i), new Text(c), new LongWritable(r));
    }

    public CandleKey(Text i, Text c, LongWritable r) {
        set(i, c, r);
    }

    public void set(Text i, Text c, LongWritable r) {
        this.instrument = i;
        this.candleMoment = c;
        this.recordId = r;
    }

    public Text getInstrument() {
        return instrument;
    }

    public Text getCandleMoment() {
        return candleMoment;
    }

    public LongWritable getRecordId() { return recordId; }

    @Override
    public void readFields(DataInput in) throws IOException {
        instrument.readFields(in);
        candleMoment.readFields(in);
        recordId.readFields(in);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        instrument.write(out);
        candleMoment.write(out);
        recordId.write(out);
    }

    @Override
    public String toString() {
        return instrument + "," + candleMoment + "," + recordId;
    }

    @Override
    public int compareTo(CandleKey o) {
        int cmp = instrument.compareTo(o.instrument);
        if (cmp != 0)
            return cmp;

        cmp = candleMoment.compareTo(o.candleMoment);
        if (cmp != 0)
            return cmp;

        return recordId.compareTo(o.recordId);
    }
}
