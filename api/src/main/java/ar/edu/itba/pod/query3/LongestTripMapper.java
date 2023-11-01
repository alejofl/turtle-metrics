package ar.edu.itba.pod.query3;

import ar.edu.itba.pod.LongTripValues;
import ar.edu.itba.pod.Util;
import ar.edu.itba.pod.data.Bike;
import ar.edu.itba.pod.data.Station;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.temporal.ChronoUnit;
import java.util.Map;

public class LongestTripMapper implements Mapper<Integer, Bike, Integer, LongTripValues>, HazelcastInstanceAware {
    private transient Map<Integer, Station> stations;

    @Override
    public void map(Integer integer, Bike bike, Context<Integer, LongTripValues> context) {
        if (bike.getStartStationPK() == bike.getEndStationPK()) {
            return;
        }

        if (!stations.containsKey(bike.getStartStationPK()) || !stations.containsKey(bike.getEndStationPK())) {
            return;
        }

        context.emit(bike.getStartStationPK(),
                LongTripValues.of(bike.getEndStationPK(), bike.getStartDate(),
                        bike.getStartDate().until(bike.getEndDate(), ChronoUnit.MINUTES)));
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.stations = hazelcastInstance.getMap(Util.HAZELCAST_NAMESPACE);
    }
}
