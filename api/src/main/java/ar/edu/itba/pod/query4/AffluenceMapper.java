package ar.edu.itba.pod.query4;

import ar.edu.itba.pod.StationByDate;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class AffluenceMapper implements Mapper<StationByDate, Integer, Integer, Integer> {
    @Override
    public void map(StationByDate stationByDate, Integer integer, Context<Integer, Integer> context) {
        context.emit(stationByDate.getStationId(), integer);
    }
}
