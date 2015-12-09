SET default_parallel $REDUCERS_NUMBER;

-- Register jar to take UDFs from
REGISTER pig_candles_utilities-1.0-SNAPSHOT.jar;
REGISTER datafu-1.2.0.jar
REGISTER piggybank-0.15.0.jar

-- Pass parameters to filter function
DEFINE FilterMoment msu.bigdata.Candles.FilterMoment('$CANDLE_WIDTH', '$CANDLE_DATE_FROM', '$CANDLE_DATE_TO', '$CANDLE_TIME_FROM', '$CANDLE_TIME_TO');
DEFINE ToCandleStart msu.bigdata.Candles.ToCandleStart('$CANDLE_WIDTH');
DEFINE FirstTupleFromBag datafu.pig.bags.FirstTupleFromBag();
DEFINE LastTupleFromBag msu.bigdata.Candles.LastTupleFromBag();
define MaxMin datafu.pig.stats.Quantile('1.0','0.0');

-- Convert moments to candle start time
A = FOREACH (
		-- Filter records
		FILTER (

			-- Retain some columns
			FOREACH (

				-- Load dataset as comma delimeted text file
				LOAD '$INPUT'
				USING PigStorage(',')
				AS (symbol:chararray, system:chararray, moment:chararray, id_deal:long, price_deal:float, volume:int, open_pos:int, direction:chararray)

			) GENERATE symbol, moment, id_deal, price_deal

		) BY ((not STARTSWITH(symbol, '#')) and (symbol matches '$CANDLE_SECURITIES') and (price_deal is not null) and (id_deal is not null) and (FilterMoment(moment)))
) GENERATE symbol, ToCandleStart(moment) as candle_start, id_deal, price_deal;

D = FOREACH (GROUP A BY (symbol, candle_start)) {
	sorted_by_id = ORDER A BY id_deal;
	sorted_by_price = ORDER A BY price_deal;
	GENERATE
		FLATTEN(group),
		FirstTupleFromBag(sorted_by_id, null).price_deal as first_price,
		FLATTEN(MaxMin(sorted_by_price.price_deal)) as (max_price:float, min_price:float),
		LastTupleFromBag(sorted_by_id, null).price_deal as last_price;
}

STORE D
INTO '$OUTPUT'
USING org.apache.pig.piggybank.storage.MultiStorage('$OUTPUT', '0', 'none', ',');