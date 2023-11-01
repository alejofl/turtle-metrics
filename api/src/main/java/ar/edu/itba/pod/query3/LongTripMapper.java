package ar.edu.itba.pod.query3;

import ar.edu.itba.pod.IntegerPair;
import ar.edu.itba.pod.LongTripValues;
import ar.edu.itba.pod.data.Bike;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.temporal.ChronoUnit;

public class LongTripMapper implements Mapper<Integer, Bike, Integer, LongTripValues> {
    @Override
    public void map(Integer integer, Bike bike, Context<Integer, LongTripValues> context) {
        if (bike.getStartStationPK() == bike.getEndStationPK()) {
            return;
        }

        context.emit(bike.getStartStationPK(),
                LongTripValues.of(bike.getEndStationPK(), bike.getStartDate(),
                        bike.getStartDate().until(bike.getEndDate(), ChronoUnit.MINUTES)));
    }
}
