package msu.bigdata;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class SortComparator extends WritableComparator {

    public SortComparator() { super(DoubleWritable.class, true); }

    @Override
    public int compare(WritableComparable v1, WritableComparable v2) {
        DoubleWritable d1 = (DoubleWritable) v1;
        DoubleWritable d2 = (DoubleWritable) v2;

        return (Math.abs(d1.get()) < Math.abs(d2.get())) ? 1 : -1;
    }

}
