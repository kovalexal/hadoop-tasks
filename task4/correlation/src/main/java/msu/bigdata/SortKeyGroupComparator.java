package msu.bigdata;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;


public class SortKeyGroupComparator extends WritableComparator {

    public SortKeyGroupComparator() { super(DoubleWritable.class, true); }

    @Override
    public int compare(WritableComparable v1, WritableComparable v2) {
        return 0;
    }

}
