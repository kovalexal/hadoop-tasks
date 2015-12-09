package msu.bigdata.Candles;

import java.io.IOException;
//import org.apache.commons.lang3.StringUtils;
import org.apache.pig.EvalFunc;
import org.apache.pig.PigWarning;
import org.apache.pig.data.Tuple;
import org.apache.commons.logging.Log;

public class TestUDF extends EvalFunc<String> {

    @Override
    public String exec(Tuple arg0) throws IOException {
        try {

            return "Hello!";
        } catch (Exception e) {
            throw new IOException("Caught exception while processing the input row ", e);
        }
    }
}
