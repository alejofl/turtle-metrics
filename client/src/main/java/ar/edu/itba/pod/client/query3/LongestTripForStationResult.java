package ar.edu.itba.pod.client.query3;

import ar.edu.itba.pod.client.Result;

public class LongestTripForStationResult implements Result, Comparable<LongestTripForStationResult>{
    private final String stationA;
    private final String stationB;
    private final String startDate;
    private final long minutes;

    public LongestTripForStationResult(String stationA, String stationB, String startDate, long minutes) {
        this.stationA = stationA;
        this.stationB = stationB;
        this.startDate = startDate;
        this.minutes = minutes;
    }

    @Override
    public String toString() {
        return String.format("%s;%s;%s;%d",stationA,stationB,startDate,minutes);
    }

    @Override
    public int compareTo(LongestTripForStationResult o) {
        int res = Long.compare(o.minutes, this.minutes);
        if (res == 0) {
            res = this.stationA.compareTo(o.stationA);
        }
        return res;
    }
}
