package ar.edu.itba.pod.query2;

import ar.edu.itba.pod.IntegerPair;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class TopStationsReducer implements ReducerFactory<Integer, Double, Double> {
    @Override
    public Reducer<Double, Double> newReducer(Integer integer) {
        return new Reducer<>() {
            private long count;
            private double sum;

            @Override
            public void beginReduce() {
                count = 0;
                sum = 0;
            }

            @Override
            public void reduce(Double distance) {
                count++;
                sum += distance;
            }

            @Override
            public Double finalizeReduce() {
                return sum / count;
            }
        };
    }
}
