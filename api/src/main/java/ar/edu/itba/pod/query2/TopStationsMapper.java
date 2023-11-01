package ar.edu.itba.pod.query2;

import ar.edu.itba.pod.Util;
import ar.edu.itba.pod.data.Bike;
import ar.edu.itba.pod.data.Station;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.util.Map;

public class TopStationsMapper implements Mapper<Integer, Bike, Integer, Double>, HazelcastInstanceAware {
    private transient Map<Integer, Station> stations;

    @Override
    public void map(Integer integer, Bike bike, Context<Integer, Double> context) {
        if (bike.getStartStationPK() == bike.getEndStationPK() || !bike.isMember()) {
            return;
        }

        Station stationA = stations.getOrDefault(bike.getStartStationPK(), null);
        Station stationB = stations.getOrDefault(bike.getEndStationPK(), null);
        if (stationA == null || stationB == null) {
            return;
        }

        context.emit(
                integer,
                haversine(
                        stationA.getLatitude(),
                        stationA.getLongitude(),
                        stationB.getLatitude(),
                        stationB.getLongitude()
                )
        );
    }

    // Retrieved from https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
    private static double haversine(
            double lat1, double lon1,
            double lat2, double lon2
    ) {
        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                Math.cos(lat1) *
                Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.stations = hazelcastInstance.getMap(Util.HAZELCAST_NAMESPACE);
    }
}
