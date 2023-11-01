package ar.edu.itba.pod.query4;

import ar.edu.itba.pod.StationByDate;
import ar.edu.itba.pod.Util;
import ar.edu.itba.pod.data.Bike;
import ar.edu.itba.pod.data.Station;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDateTime;
import java.util.Map;

public class AffluenceByDayMapper implements Mapper<Integer, Bike, StationByDate, Integer>, HazelcastInstanceAware {
    private Map<Integer, Station> stations;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public AffluenceByDayMapper(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
    @Override
    public void map(Integer integer, Bike bike, Context<StationByDate, Integer> context) {
        if (!isValidDate(bike.getStartDate(), bike.getEndDate())) {
            return;
        }

        if (!stations.containsKey(bike.getStartStationPK()) || !stations.containsKey(bike.getEndStationPK())) {
            return;
        }

        context.emit(StationByDate.of(bike.getStartStationPK(), bike.getStartDate().toLocalDate()), -1);
        context.emit(StationByDate.of(bike.getEndStationPK(), bike.getEndDate().toLocalDate()), 1);
    }

    private boolean isValidDate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isBefore(this.startDate)) {
            return false;
        }
        if (endDate.isAfter(this.endDate)) {
            return false;
        }
        return true;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.stations = hazelcastInstance.getMap(Util.HAZELCAST_NAMESPACE);
    }
}
