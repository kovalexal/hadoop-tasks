package msu.bigdata;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DayInstrumentWritable implements WritableComparable<DayInstrumentWritable> {

    private Text day;
    private Text instrument;

    public DayInstrumentWritable() {
        set(new Text(), new Text());
    }

    public DayInstrumentWritable(String d, String i) {
        set(new Text(d), new Text(i));
    }

    public DayInstrumentWritable(Text d, Text i) {
        set(d, i);
    }

    public Text getDay() {
        return day;
    }

    public Text getInstrument() {
        return instrument;
    }

    public void set(Text d, Text i) {
        this.day = d;
        this.instrument = i;
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        day.readFields(in);
        instrument.readFields(in);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        day.write(out);
        instrument.write(out);
    }

    @Override
    public String toString() {
        return day + " " + instrument;
    }

    @Override
    public int compareTo(DayInstrumentWritable o) {
        int cmp = day.compareTo(o.day);

        if (cmp != 0)
            return cmp;

        return instrument.compareTo(o.instrument);
    }

    @Override
    public int hashCode() {
        return day.hashCode() * 163 + instrument.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DayInstrumentWritable) {
            DayInstrumentWritable di = (DayInstrumentWritable) o;
            return day.equals(di.day) && instrument.equals(di.instrument);
        }
        return false;
    }
}
