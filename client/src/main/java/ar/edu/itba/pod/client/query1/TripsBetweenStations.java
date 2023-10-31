package ar.edu.itba.pod.client.query1;

import ar.edu.itba.pod.IntegerPair;
import ar.edu.itba.pod.Util;
import ar.edu.itba.pod.client.QueryClient;
import ar.edu.itba.pod.data.Bike;
import ar.edu.itba.pod.data.Station;
import ar.edu.itba.pod.query1.TripsMapper;
import ar.edu.itba.pod.query1.TripsReducer;
import com.hazelcast.mapreduce.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class TripsBetweenStations extends QueryClient {
    public TripsBetweenStations() {
        super();
    }

    @Override
    public void resolveQuery() throws ExecutionException, InterruptedException, IOException {
        final JobTracker jobTracker = getHz().getJobTracker(Util.HAZELCAST_NAMESPACE);

        final KeyValueSource<Integer, Bike> source = KeyValueSource.fromMultiMap(getHz().getMultiMap(Util.HAZELCAST_NAMESPACE));

        Job<Integer, Bike> job = jobTracker.newJob(source);

        Map<IntegerPair, Integer> reducedData = job
                .mapper(new TripsMapper())
                .reducer(new TripsReducer())
                .submit()
                .get();

        Map<Integer, Station> stations = getHz().getMap(Util.HAZELCAST_NAMESPACE);
        Set<TripsBetweenStationsResult> results = new TreeSet<>();
        for (Map.Entry<IntegerPair, Integer> entry : reducedData.entrySet()) {
            Station stationA = stations.get(entry.getKey().getKey());
            Station stationB = stations.get(entry.getKey().getValue());

            results.add(new TripsBetweenStationsResult(
                    stationA.getName(),
                    stationB.getName(),
                    entry.getValue())
            );
        }
        writeResults(results);
    }

    @Override
    public String getQueryNumber() {
        return "1";
    }

    @Override
    public String getQueryHeader() {
        return "station_a;station_b;trips_between_a_b";
    }

    public static void main(String[] args) {
        QueryClient query = new TripsBetweenStations();
    }
}
