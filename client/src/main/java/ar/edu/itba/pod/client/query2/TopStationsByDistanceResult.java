package ar.edu.itba.pod.client.query2;

import ar.edu.itba.pod.client.Result;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class TopStationsByDistanceResult implements Result, Comparable<TopStationsByDistanceResult> {
    private final String station;
    private final double avgDistance;

    public TopStationsByDistanceResult(String station, double avgDistance) {
        this.station = station;
        this.avgDistance = avgDistance;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.DOWN);
        return String.format("%s;%s", station, df.format(avgDistance));
    }

    @Override
    public int compareTo(TopStationsByDistanceResult o) {
        int res = Double.compare(o.avgDistance, this.avgDistance);
        if (res == 0) {
            res = this.station.compareTo(o.station);
        }
        return res;
    }
}
