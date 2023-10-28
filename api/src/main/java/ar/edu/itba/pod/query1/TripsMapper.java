package ar.edu.itba.pod.query1;

import ar.edu.itba.pod.IntegerPair;
import ar.edu.itba.pod.data.Bike;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.spi.NodeAware;

public class TripsMapper implements Mapper<Integer, Bike, IntegerPair, Integer> {

    public TripsMapper() {

    }
    @Override
    public void map(Integer integer, Bike bike, Context<IntegerPair, Integer> context) {
        if (bike.getStartStationPK() == bike.getEndStationPK()) {
            return;
        }

        context.emit(IntegerPair.of(bike.getStartStationPK(), bike.getEndStationPK()), 1);
    }
}
