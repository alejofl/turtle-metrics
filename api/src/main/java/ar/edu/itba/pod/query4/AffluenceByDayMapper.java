package ar.edu.itba.pod.query4;

import ar.edu.itba.pod.StationByDate;
import ar.edu.itba.pod.data.Bike;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDateTime;

public class AffluenceByDayMapper implements Mapper<Integer, Bike, StationByDate, Integer> {

    LocalDateTime startDate;
    LocalDateTime endDate;

    public AffluenceByDayMapper(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
    @Override
    public void map(Integer integer, Bike bike, Context<StationByDate, Integer> context) {
//        if (isValidDate(bike.getStartDate(), bike.getEndDate())) {
//            return;
//        }
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
}
