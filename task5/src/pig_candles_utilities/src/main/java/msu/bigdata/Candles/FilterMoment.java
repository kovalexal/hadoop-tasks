package msu.bigdata.Candles;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.pig.FilterFunc;
import org.apache.pig.data.Tuple;

import java.io.IOException;

public class FilterMoment extends FilterFunc {

    private long candleWidth;
    private String dateFrom, dateTo, timeFrom, timeTo;

    public FilterMoment(String candleWidth, String dateFrom, String dateTo, String timeFrom, String timeTo) {
        this.candleWidth = Long.parseLong(candleWidth);

        this.dateFrom = dateFrom;
        this.dateTo = dateTo;

        // Cast timeFrom to first candle start time
        long msStart = (
                Integer.parseInt(timeFrom.substring(0, 2)) * 60 +
                Integer.parseInt(timeFrom.substring(2, 4))
        ) * 60 * 1000;
        this.timeFrom = DurationFormatUtils.formatDuration(
                (msStart / this.candleWidth) * this.candleWidth,
                "HHmmssSSS"
        );

        // Cast timeTo to last candle end time
        long msEnd = (
                Integer.parseInt(timeTo.substring(0, 2)) * 60 +
                Integer.parseInt(timeTo.substring(2, 4))
        ) * 60 * 1000;
        this.timeTo = DurationFormatUtils.formatDuration(
                (msEnd / this.candleWidth) * this.candleWidth,
                "HHmmssSSS"
        );
    }

    @Override
    public Boolean exec(Tuple input) throws IOException {
        if (input == null || input.size() == 0)
            return Boolean.FALSE;

        try {

            String moment = (String) input.get(0);

            // Filter by date
            String date = moment.substring(0, 8);
            if (!(date.compareTo(this.dateFrom) >= 0 && date.compareTo(this.dateTo) < 0))
                return Boolean.FALSE;

            // Filter by time
            String time = moment.substring(8, 17);
            if (!(time.compareTo(this.timeFrom) >= 0 && time.compareTo(this.timeTo) <= 0))
                return Boolean.FALSE;

            return Boolean.TRUE;

        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }
}
