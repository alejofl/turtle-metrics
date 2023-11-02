package ar.edu.itba.pod.query4;

import ar.edu.itba.pod.TripleInteger;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class AffluenceReducer implements ReducerFactory<Integer, Integer, TripleInteger> {
    @Override
    public Reducer<Integer, TripleInteger> newReducer(Integer integer) {
        return new Reducer<>() {
            private int positiveAffluence;
            private int neutralAffluence;
            private int negativeAffluence;

            @Override
            public void beginReduce () {
                positiveAffluence = 0;
                neutralAffluence = 0;
                negativeAffluence = 0;
            }

            @Override
            public void reduce(Integer valueIn) {
                if (valueIn > 0) {
                    positiveAffluence++;
                } else if (valueIn < 0) {
                    negativeAffluence++;
                } else {
                    neutralAffluence++;
                }
            }

            @Override
            public TripleInteger finalizeReduce() {
                return TripleInteger.of(positiveAffluence, neutralAffluence, negativeAffluence);
            }
        };
    }
}
