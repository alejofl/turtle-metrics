package ar.edu.itba.pod.query4;

import ar.edu.itba.pod.IntegerPair;
import ar.edu.itba.pod.data.Bike;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDateTime;

public class AffluenceMapper implements Mapper<Integer, Bike, Integer, IntegerPair> {

    LocalDateTime startDate;
    LocalDateTime endDate;

    public AffluenceMapper(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
    @Override
    public void map(Integer integer, Bike bike, Context<Integer, IntegerPair> context) {
        // Filtro fechas
        if (isValidDate(bike.getStartDate(), bike.getEndDate())) {
            return;
        }
        // Obtengo el key de la estacion | PairInteger
        context.emit(bike.getStartStationPK(), IntegerPair.of(bike.getStartStationPK(), bike.getEndStationPK()));
    }

    private boolean isValidDate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isBefore(this.startDate)) {
            return false;
        }
        if (endDate.isAfter(this.endDate)) {
            return false
        }
        return true;
    }
}
