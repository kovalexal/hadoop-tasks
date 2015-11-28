package msu.bigdata;


import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class CorrelationKeyGroupComparator extends WritableComparator {
    public CorrelationKeyGroupComparator() { super(CorrelationKey.class, true); }

    @Override
    public int compare(WritableComparable v1, WritableComparable v2) {
        CorrelationKey k1 = (CorrelationKey) v1;
        CorrelationKey k2 = (CorrelationKey) v2;

        int cmp = k1.getInstrument1().compareTo(k2.getInstrument1());
        if (cmp != 0)
            return cmp;

        return k1.getInstrument2().compareTo(k2.getInstrument2());
    }

}
