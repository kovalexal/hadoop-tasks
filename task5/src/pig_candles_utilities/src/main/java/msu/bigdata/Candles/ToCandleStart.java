package msu.bigdata.Candles;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;

import java.io.IOException;

public class ToCandleStart extends EvalFunc<String> {

    private long candleWidth;

    public ToCandleStart(String candleWidth) {
        this.candleWidth = Long.parseLong(candleWidth);
    }

    @Override
    public String exec(Tuple input) throws IOException {
        if (input == null || input.size() == 0)
            return null;

        try {
            // Get input params
            String moment = (String) input.get(0);
            String date = moment.substring(0, 8);
            String time = moment.substring(8);

            // Convert moment to milliseconds
            long ms = (
                (
                    Integer.parseInt(time.substring(0, 2)) * 60 +
                    Integer.parseInt(time.substring(2, 4))
                ) * 60 + Integer.parseInt(time.substring(4, 6))
            ) * 1000 + Integer.parseInt(time.substring(6, 9));

            // Convert milliseconds to candle start time
            String candleStartTime = DurationFormatUtils.formatDuration(
                (ms / this.candleWidth) * this.candleWidth,
                "HHmmssSSS"
            );

            return date + candleStartTime;

        } catch (Exception e) {
            throw new IOException("Caught exception processing input row ", e);
        }
    }

}
