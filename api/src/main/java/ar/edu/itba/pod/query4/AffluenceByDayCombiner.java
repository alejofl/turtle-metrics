package ar.edu.itba.pod.query4;

import ar.edu.itba.pod.StationByDate;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class AffluenceByDayCombiner implements CombinerFactory<StationByDate, Integer, Integer> {
    @Override
    public Combiner<Integer, Integer> newCombiner(StationByDate stationByDate) {
        return new Combiner<>() {
            private int sum;

            @Override
            public void beginCombine() {
                sum = 0;
            }

            @Override
            public void combine(Integer integer) {
                sum += integer;
            }

            @Override
            public Integer finalizeChunk() {
                return sum;
            }

            @Override
            public void reset() {
                sum = 0;
            }
        };
    }
}
