package ar.edu.itba.pod.client.query1;

import ar.edu.itba.pod.client.Result;

public class TripsBetweenStationsResult implements Result, Comparable<TripsBetweenStationsResult> {
    private final String stationA;
    private final String stationB;
    private final int tripsQuantity;

    public TripsBetweenStationsResult(String stationA, String stationB, int tripsQuantity) {
        this.stationA = stationA;
        this.stationB = stationB;
        this.tripsQuantity = tripsQuantity;
    }

    @Override
    public String toString() {
        return String.format("%s;%s;%d", stationA, stationB, tripsQuantity);
    }

    @Override
    public int compareTo(TripsBetweenStationsResult o) {
        int res = Integer.compare(o.tripsQuantity, this.tripsQuantity);
        if (res == 0) {
            res = this.stationA.compareTo(o.stationA);
            if (res == 0) {
                res = this.stationB.compareTo(o.stationB);
            }
        }
        return res;
    }
}
