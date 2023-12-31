package ar.edu.itba.pod.query1;

import ar.edu.itba.pod.IntegerPair;
import ar.edu.itba.pod.Util;
import ar.edu.itba.pod.data.Bike;
import ar.edu.itba.pod.data.Station;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.util.Map;

public class TripsMapper implements Mapper<Integer, Bike, IntegerPair, Integer>, HazelcastInstanceAware {
    private transient Map<Integer, Station> stations;

    public TripsMapper() {
    }

    @Override
    public void map(Integer integer, Bike bike, Context<IntegerPair, Integer> context) {
        if (bike.getStartStationPK() == bike.getEndStationPK()) {
            return;
        }

        if (!stations.containsKey(bike.getStartStationPK()) || !stations.containsKey(bike.getEndStationPK())) {
            return;
        }

        context.emit(IntegerPair.of(bike.getStartStationPK(), bike.getEndStationPK()), 1);
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.stations = hazelcastInstance.getMap(Util.HAZELCAST_NAMESPACE);
    }
}
