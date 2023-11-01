package ar.edu.itba.pod.query3;

import ar.edu.itba.pod.LongTripValues;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.time.LocalDateTime;

public class LongestTripReducer implements ReducerFactory<Integer, LongTripValues, LongTripValues> {
    @Override
    public Reducer<LongTripValues, LongTripValues> newReducer(Integer integerPair) {
        return new Reducer<>() {
            private int endStation;
            private LocalDateTime startDate;
            private long max;

            @Override
            public void beginReduce () {
                max = 0;
            }

            @Override
            public void reduce(LongTripValues value) {
                if(value.getMinutes() > max || max == 0) {
                    endStation = value.getEndStation();
                    startDate = value.getStartDate();
                    max = value.getMinutes();
                } else if(value.getMinutes() == max && value.getStartDate().isBefore(startDate)) {
                    endStation = value.getEndStation();
                    startDate = value.getStartDate();
                }
            }

            @Override
            public LongTripValues finalizeReduce() {
                return LongTripValues.of(endStation,startDate,max);
            }
        };
    }
}
