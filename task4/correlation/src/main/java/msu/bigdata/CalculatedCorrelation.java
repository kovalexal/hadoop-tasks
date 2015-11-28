package msu.bigdata;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class CalculatedCorrelation implements WritableComparable {

    private Text instrument1, instrument2;
    private DoubleWritable pearsonCoefficient;

    public CalculatedCorrelation() { set(new Text(), new Text(), new DoubleWritable()); }

    public CalculatedCorrelation(Text instrument1, Text instrument2, DoubleWritable coeff) { set(instrument1, instrument2, coeff); }

    public void set(Text instrument1, Text instrument2, DoubleWritable coeff) {
        this.instrument1 = new Text(instrument1);
        this.instrument2 = new Text(instrument2);
        this.pearsonCoefficient = new DoubleWritable(coeff.get());
    }

    public Text getInstrument1() { return instrument1; }
    public Text getInstrument2() { return instrument2; }
    public DoubleWritable getPearsonCoefficient() { return pearsonCoefficient; }

    @Override
    public void readFields(DataInput in) throws IOException {
        instrument1.readFields(in);
        instrument2.readFields(in);
        pearsonCoefficient.readFields(in);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        instrument1.write(out);
        instrument2.write(out);
        pearsonCoefficient.write(out);
    }

    @Override
    public String toString() {
        return instrument1.toString() + "," + instrument2.toString() + "\t" + pearsonCoefficient.toString();
    }

    @Override
    public int compareTo(Object arg0) {
        CalculatedCorrelation that = (CalculatedCorrelation) arg0;

        return this.getPearsonCoefficient().compareTo(that.getPearsonCoefficient());
    }

}
