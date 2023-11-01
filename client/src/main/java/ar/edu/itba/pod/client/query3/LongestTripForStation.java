package ar.edu.itba.pod.client.query3;

import ar.edu.itba.pod.LongTripValues;
import ar.edu.itba.pod.Util;
import ar.edu.itba.pod.client.QueryClient;
import ar.edu.itba.pod.data.Bike;
import ar.edu.itba.pod.data.Station;
import ar.edu.itba.pod.query3.LongestTripMapper;
import ar.edu.itba.pod.query3.LongestTripReducer;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

public class LongestTripForStation extends QueryClient {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public LongestTripForStation() {
        super();
    }

    @Override
    public void resolveQuery() throws ExecutionException, InterruptedException, IOException {
        final JobTracker jobTracker = getHz().getJobTracker(Util.HAZELCAST_NAMESPACE);

        final KeyValueSource<Integer, Bike> source = KeyValueSource.fromMultiMap(getHz().getMultiMap(Util.HAZELCAST_NAMESPACE));

        Job<Integer, Bike> job = jobTracker.newJob(source);

        Map<Integer, LongTripValues> reducedData = job
                .mapper(new LongestTripMapper())
                .reducer(new LongestTripReducer())
                .submit()
                .get();

        Map<Integer, Station> stations = getHz().getMap(Util.HAZELCAST_NAMESPACE);
        Set<LongestTripForStationResult> results = new TreeSet<>();
        for (Map.Entry<Integer, LongTripValues> entry : reducedData.entrySet()) {
            Station stationA = stations.get(entry.getKey());
            Station stationB = stations.get(entry.getValue().getEndStation());

            results.add(new LongestTripForStationResult(
                    stationA.getName(),
                    stationB.getName(),
                    entry.getValue().getStartDate().format(DATE_FORMATTER),
                    entry.getValue().getMinutes())
            );
        }
        writeResults(results);
    }

    @Override
    public String getQueryNumber() {
        return "3";
    }

    @Override
    public String getQueryHeader() {
        return "start_station;end_station;start_date;minutes";
    }

    public static void main(String[] args) {
        QueryClient query = new LongestTripForStation();
    }
}
