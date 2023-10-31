package ar.edu.itba.pod.query4;

import ar.edu.itba.pod.StationByDate;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class AffluenceByDayReducer implements ReducerFactory<StationByDate, Integer, Integer> {
    @Override
    public Reducer<Integer, Integer> newReducer(StationByDate stationByDate) {
        return new Reducer<>() {
            private int sum;

            @Override
            public void beginReduce () {
                sum = 0;
            }

            @Override
            public void reduce(Integer valueIn) {
                sum += valueIn;
            }

            @Override
            public Integer finalizeReduce() {
                return sum;
            }
        };
    }
}
