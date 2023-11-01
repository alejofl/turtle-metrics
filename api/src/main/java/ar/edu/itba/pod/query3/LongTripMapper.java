package ar.edu.itba.pod.query3;

import ar.edu.itba.pod.IntegerPair;
import ar.edu.itba.pod.LocalDateTimeLongPair;
import ar.edu.itba.pod.data.Bike;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.temporal.ChronoUnit;

public class LongTripMapper implements Mapper<Integer, Bike, IntegerPair, LocalDateTimeLongPair> {
    @Override
    public void map(Integer integer, Bike bike, Context<IntegerPair, LocalDateTimeLongPair> context) {
        if (bike.getStartStationPK() == bike.getEndStationPK()) {
            return;
        }

        context.emit(IntegerPair.of(bike.getStartStationPK(), bike.getEndStationPK()),
                LocalDateTimeLongPair.of(bike.getStartDate(),
                        bike.getStartDate().until(bike.getEndDate(), ChronoUnit.MINUTES)));
    }
}
