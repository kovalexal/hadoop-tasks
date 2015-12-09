package msu.bigdata.Candles;

import datafu.pig.util.SimpleEvalFunc;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import com.google.common.collect.Iterators;

import java.io.IOException;

public class LastTupleFromBag extends SimpleEvalFunc<Tuple> {

//    public Tuple call(DataBag bag) throws IOException {
//        return call(bag, null);
//    }

    public Tuple call(DataBag bag, Tuple defaultValue) throws IOException {
        return Iterators.getLast(bag.iterator(), defaultValue);
    }

    @Override
    public Schema outputSchema(Schema input) {
        try {
            return new Schema(input.getField(0).schema);
        }
        catch (Exception e) {
            return null;
        }
    }
}
