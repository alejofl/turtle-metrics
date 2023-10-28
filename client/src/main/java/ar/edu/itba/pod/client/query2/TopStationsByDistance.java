package ar.edu.itba.pod.client.query2;

import ar.edu.itba.pod.Util;
import ar.edu.itba.pod.client.QueryClient;
import ar.edu.itba.pod.data.Bike;
import ar.edu.itba.pod.data.Station;
import ar.edu.itba.pod.query2.TopStationsMapper;
import ar.edu.itba.pod.query2.TopStationsReducer;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

public class TopStationsByDistance extends QueryClient {
    private int resultsQuantity;

    public TopStationsByDistance() {
        super();
    }

    @Override
    public void checkArguments() throws IllegalArgumentException {
        super.checkArguments();
        if (System.getProperty("n") == null) {
            throw new IllegalArgumentException("Argument 'n' must be provided");
        }
        try {
            resultsQuantity = Integer.parseInt(System.getProperty("n"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Provided 'n' is not a number");
        }

        System.out.println(resultsQuantity);
    }

    @Override
    public void resolveQuery() throws ExecutionException, InterruptedException {
        final JobTracker jobTracker = getHz().getJobTracker(Util.HAZELCAST_NAMESPACE);

        final KeyValueSource<Integer, Bike> source = KeyValueSource.fromMultiMap(getHz().getMultiMap(Util.HAZELCAST_NAMESPACE));

        Job<Integer, Bike> job = jobTracker.newJob(source);

        Map<Integer, Double> reducedData = job
                .mapper(new TopStationsMapper())
                .reducer(new TopStationsReducer())
                .submit()
                .get();

        Map<Integer, Station> stations = getHz().getMap(Util.HAZELCAST_NAMESPACE);
        Set<TopStationsByDistanceResult> results = new TreeSet<>();
        int readResults = 0;
        for (Map.Entry<Integer, Double> entry : reducedData.entrySet()) {
            if (readResults == resultsQuantity) {
                break;
            }

            Station station = stations.get(entry.getKey());
            results.add(new TopStationsByDistanceResult(
                    station.getName(),
                    entry.getValue()
            ));

            readResults++;
        }
        writeResults(results);
    }

    @Override
    public String getQueryNumber() {
        return "2";
    }

    @Override
    public String getQueryHeader() {
        return "station;avg_distance";
    }

    public static void main(String[] args) {
        QueryClient query = new TopStationsByDistance();
    }
}
