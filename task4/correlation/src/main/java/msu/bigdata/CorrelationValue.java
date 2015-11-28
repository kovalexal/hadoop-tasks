package msu.bigdata;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class CorrelationValue implements WritableComparable {

    private Text moment;
    private FloatWritable closePrice1, closePrice2;

    public CorrelationValue() {
        this.moment = new Text();
        this.closePrice1 = new FloatWritable(0);
        this.closePrice2 = new FloatWritable(0);
    }

    public CorrelationValue(Text moment, FloatWritable closePrice1, FloatWritable closePrice2) {
        set(moment, closePrice1, closePrice2);
    }

    public void set(Text moment, FloatWritable closePrice1, FloatWritable closePrice2) {
        this.moment = new Text(moment);
        this.closePrice1 = new FloatWritable(closePrice1.get());
        this.closePrice2 = new FloatWritable(closePrice2.get());
    }

    public Text getMoment() { return moment; }
    public FloatWritable getClosePrice1() { return closePrice1; }
    public FloatWritable getClosePrice2() { return closePrice2; }

    @Override
    public void readFields(DataInput in) throws IOException {
        moment.readFields(in);
        closePrice1.readFields(in);
        closePrice2.readFields(in);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        moment.write(out);
        closePrice1.write(out);
        closePrice2.write(out);
    }

    @Override
    public String toString() {
        return moment.toString() + "," + closePrice1.toString() + "," + closePrice2.toString();
    }

    @Override
    public int compareTo(Object arg0) {
        CorrelationValue that = (CorrelationValue) arg0;

        return this.getMoment().compareTo(that.getMoment());
    }

}
