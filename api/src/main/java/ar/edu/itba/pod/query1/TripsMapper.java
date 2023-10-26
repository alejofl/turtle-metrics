package ar.edu.itba.pod.query1;

import ar.edu.itba.pod.IntegerPair;
import ar.edu.itba.pod.data.Bike;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class TripsMapper implements Mapper<Integer, Bike, IntegerPair, Integer> {
    @Override
    public void map(Integer integer, Bike bike, Context<IntegerPair, Integer> context) {
        if (bike.getStartStationPK() == bike.getEndStationPK()) {
            return;
        }

        context.emit(IntegerPair.of(bike.getStartStationPK(), bike.getEndStationPK()), 1);
    }
}
