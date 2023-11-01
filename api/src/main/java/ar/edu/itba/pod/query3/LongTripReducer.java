package ar.edu.itba.pod.query3;

import ar.edu.itba.pod.IntegerPair;
import ar.edu.itba.pod.LocalDateTimeLongPair;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.time.LocalDateTime;

public class LongTripReducer implements ReducerFactory<IntegerPair, LocalDateTimeLongPair, LocalDateTimeLongPair> {
    @Override
    public Reducer<LocalDateTimeLongPair, LocalDateTimeLongPair> newReducer(IntegerPair integerPair) {
        return new Reducer<>() {
            private long max;
            private LocalDateTime startDate;

            @Override
            public void beginReduce () {
                max = 0;
            }

            @Override
            public void reduce(LocalDateTimeLongPair value) {
                if(value.getValue() > max || max == 0) {
                    max = value.getValue();
                    startDate = value.getKey();
                }
            }

            @Override
            public LocalDateTimeLongPair finalizeReduce() {
                return LocalDateTimeLongPair.of(startDate,max);
            }
        };
    }
}
