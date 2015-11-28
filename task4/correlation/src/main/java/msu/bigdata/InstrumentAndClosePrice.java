package msu.bigdata;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class InstrumentAndClosePrice implements WritableComparable {

    private Text instrument;
    private FloatWritable closePrice;

    public InstrumentAndClosePrice() {
        instrument = new Text();
        closePrice = new FloatWritable(0);
    }

    public InstrumentAndClosePrice(InstrumentAndClosePrice arg0) {
        instrument = new Text(arg0.instrument);
        closePrice = new FloatWritable(arg0.closePrice.get());
    }

    public InstrumentAndClosePrice(Text instrument, FloatWritable closePrice) {
        set(instrument, closePrice);
    }

    public void set(Text instrument, FloatWritable closePrice) {
        this.instrument = instrument;
        this.closePrice = closePrice;
    }

    public Text getInstrument() {
        return instrument;
    }

    public void setInstrument(Text instrument) {
        this.instrument = instrument;
    }

    public FloatWritable getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(FloatWritable closePrice) {
        this.closePrice = closePrice;
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        instrument.readFields(in);
        closePrice.readFields(in);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        instrument.write(out);
        closePrice.write(out);
    }

    @Override
    public int compareTo(Object arg0) {
        InstrumentAndClosePrice that = (InstrumentAndClosePrice) arg0;
        int cmp = this.instrument.compareTo(that.getInstrument());

        if (cmp != 0)
            return cmp;

        return this.closePrice.compareTo(that.getClosePrice());
    }

    //    @Override
    public String toString() {
        return instrument.toString() + "," + closePrice.toString();
    }

}
