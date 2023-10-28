package ar.edu.itba.pod.client.query4;

import ar.edu.itba.pod.IntegerPair;
import ar.edu.itba.pod.Util;
import ar.edu.itba.pod.client.QueryClient;
import ar.edu.itba.pod.data.Bike;
import ar.edu.itba.pod.query1.TripsMapper;
import ar.edu.itba.pod.query1.TripsReducer;
import ar.edu.itba.pod.query4.AffluenceMapper;
import ar.edu.itba.pod.query4.AffluenceReducer;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class AffluenceByStation extends QueryClient {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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

        try {
            LocalDateTime.parse(startDateArgument, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            errors.append("Argument 'startDate' must have 'dd/mm/yyyy' format\n");
        }

        try {
            LocalDateTime.parse(endDateArgument, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            errors.append("Argument 'endDate' must have 'dd/mm/yyyy' format\n");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }
    }

    @Override
    public void resolveQuery() throws ExecutionException, InterruptedException {
        final JobTracker jobTracker = getHz().getJobTracker(Util.HAZELCAST_NAMESPACE);

        final KeyValueSource<Integer, Bike> source = KeyValueSource.fromMultiMap(getHz().getMultiMap(Util.HAZELCAST_NAMESPACE));

        Job<Integer, Bike> job = jobTracker.newJob(source);

        Map<IntegerPair, Integer> reducedData = job
                .mapper(new AffluenceMapper(startDate, endDate))
                .reducer(new AffluenceReducer())
                .submit()
                .get();


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
