package ar.edu.itba.pod.client.query4;

import ar.edu.itba.pod.StationByDate;
import ar.edu.itba.pod.TripleInteger;
import ar.edu.itba.pod.Util;
import ar.edu.itba.pod.client.QueryClient;
import ar.edu.itba.pod.data.Bike;
import ar.edu.itba.pod.data.Station;
import ar.edu.itba.pod.query4.*;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

public class AffluenceByStation extends QueryClient {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public AffluenceByStation() {
        super();
    }

    @Override
    public void checkArguments() throws IllegalArgumentException {
        super.checkArguments();

        StringBuilder errors = new StringBuilder();

        String startDateArgument = System.getProperty("startDate");
        String endDateArgument = System.getProperty("endDate");

        if (startDateArgument == null) {
            errors.append("Argument 'startDate' must be provided\n");
        }
        if (endDateArgument == null) {
            errors.append("Argument 'endDate' must be provided\n");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }

        try {
            startDate = LocalDate.parse(startDateArgument,TIME_FORMATTER).atStartOfDay();
        } catch (DateTimeParseException e) {
            errors.append("Argument 'startDate' must have 'dd/mm/yyyy' format\n");
        }

        try {
            endDate = LocalDate.parse(endDateArgument, TIME_FORMATTER).atTime(LocalTime.MAX);
        } catch (DateTimeParseException e) {
            errors.append("Argument 'endDate' must have 'dd/mm/yyyy' format\n");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }
    }

    @Override
    public void resolveQuery() throws ExecutionException, InterruptedException, IOException {
        final JobTracker jobTracker = getHz().getJobTracker(Util.HAZELCAST_NAMESPACE);

        final KeyValueSource<Integer, Bike> source = KeyValueSource.fromMultiMap(getHz().getMultiMap(Util.HAZELCAST_NAMESPACE));

        Job<Integer, Bike> job = jobTracker.newJob(source);

        Map<StationByDate, Integer> stationsByDays = job
                .mapper(new AffluenceByDayMapper(startDate, endDate))
                .combiner(new AffluenceByDayCombiner())
                .reducer(new AffluenceByDayReducer())
                .submit()
                .get();

        IMap<StationByDate, Integer> stationsByDaysMap = getHz().getMap(Util.HAZELCAST_NAMESPACE_2);

        stationsByDaysMap.putAll(stationsByDays);

        final KeyValueSource<StationByDate, Integer> otherSource = KeyValueSource.fromMap(stationsByDaysMap);

        Job<StationByDate, Integer> otherJob = jobTracker.newJob(otherSource);

        Map<Integer, TripleInteger> stationAffluence = otherJob
                .mapper(new AffluenceMapper())
                .reducer(new AffluenceReducer())
                .submit()
                .get();

        Map<Integer, Station> stations = getHz().getMap(Util.HAZELCAST_NAMESPACE);
        Set<AffluenceByStationResult> results = new TreeSet<>();
        for (Map.Entry<Integer, TripleInteger> entry : stationAffluence.entrySet()) {
            Station station = stations.get(entry.getKey());

            results.add(new AffluenceByStationResult(
                    station.getName(),
                    entry.getValue().getFirst(),
                    entry.getValue().getSecond(),
                    entry.getValue().getThird())
            );
        }
        writeResults(results);

    }

    @Override
    public String getQueryNumber() {
        return "4";
    }

    @Override
    public String getQueryHeader() {
        return "station;pos_afflux;neutral_afflux;negative_afflux";
    }

    public static void main(String[] args) {
        QueryClient query = new AffluenceByStation();
    }
}
