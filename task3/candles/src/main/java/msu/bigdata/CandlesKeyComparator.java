package msu.bigdata;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;


public class CandlesKeyComparator extends WritableComparator {
    public CandlesKeyComparator() {
        super(CandleKey.class, true);
    }

    @Override
    public int compare(WritableComparable v1, WritableComparable v2) {
        CandleKey k1 = (CandleKey) v1;
        CandleKey k2 = (CandleKey) v2;

        int cmp = k1.getInstrument().compareTo(k2.getInstrument());
        if (cmp != 0)
            return cmp;

        cmp = k1.getCandleMoment().compareTo(k2.getCandleMoment());
        if (cmp != 0)
            return cmp;

        return k1.getRecordId().compareTo(k2.getRecordId());
    }
}
