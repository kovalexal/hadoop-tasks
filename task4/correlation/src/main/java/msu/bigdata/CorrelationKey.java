package msu.bigdata;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class CorrelationKey implements WritableComparable {

    private Text instrument1, instrument2, moment;

    public CorrelationKey() {
        this.instrument1 = new Text();
        this.instrument2 = new Text();
        this.moment = new Text();
    }

    public CorrelationKey(Text instrument1, Text instrument2, Text moment) {
        set(instrument1, instrument2, moment);
    }

    public void set(Text instrument1, Text instrument2, Text moment) {
        this.instrument1 = new Text(instrument1);
        this.instrument2 = new Text(instrument2);

        this.moment = new Text(moment);
    }

    public Text getInstrument1() { return instrument1; }
    public Text getInstrument2() { return instrument2; }
    public Text getMoment() { return moment; }

    @Override
    public void readFields(DataInput in) throws IOException {
        instrument1.readFields(in);
        instrument2.readFields(in);
        moment.readFields(in);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        instrument1.write(out);
        instrument2.write(out);
        moment.write(out);
    }

    @Override
    public String toString() {
        return instrument1.toString() + "," + instrument2.toString() + "," + moment.toString();
    }

    @Override
    public int compareTo(Object arg0) {
        CorrelationKey that = (CorrelationKey) arg0;
        int cmp = this.instrument1.compareTo(that.instrument1);
        if (cmp != 0)
            return cmp;

        cmp = this.instrument2.compareTo(that.instrument2);
        if (cmp != 0)
            return cmp;

        return this.moment.compareTo(that.moment);
    }

    @Override
    public int hashCode() {
        return instrument1.toString().hashCode() * 163 + instrument2.toString().hashCode() * 109;
    }
}
