package msu.bigdata;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MinMaxFloatWritable implements WritableComparable<MinMaxFloatWritable> {

    private FloatWritable min, max;

    public MinMaxFloatWritable() {
        set(new FloatWritable(new Float(0.0)), new FloatWritable(new Float(0.0)));
    }

    public MinMaxFloatWritable(float min, float max) {
        set(new FloatWritable(new Float(min)), new FloatWritable(new Float(max)));
    }

    public MinMaxFloatWritable(Float min, Float max) {
        set(new FloatWritable(min), new FloatWritable(max));
    }

    public FloatWritable getMin() {
        return min;
    }

    public FloatWritable getMax() {
        return max;
    }

    public void set(FloatWritable min, FloatWritable max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        min.readFields(in);
        max.readFields(in);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        min.write(out);
        max.write(out);
    }

    @Override
    public String toString() {
        return max + " " + min;
    }

    @Override
    public int compareTo(MinMaxFloatWritable o) {
        int cmp = min.compareTo(o.min);

        if (cmp != 0)
            return cmp;

        return max.compareTo(o.max);
    }

    @Override
    public int hashCode() {
        return min.hashCode() * 163 + max.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MinMaxFloatWritable) {
            MinMaxFloatWritable minmax = (MinMaxFloatWritable) o;
            return min.equals(minmax.min) && max.equals(minmax.max);
        }
        return false;
    }
}
