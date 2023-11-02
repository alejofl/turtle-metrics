package ar.edu.itba.pod.client.query4;

import ar.edu.itba.pod.client.Result;

public class AffluenceByStationResult implements Result, Comparable<AffluenceByStationResult> {

    private final String station;
    private final int posAfflux;
    private final int neutralAfflux;
    private final int negativeAfflux;

    public AffluenceByStationResult(String station, int posAfflux, int neutralAfflux, int negativeAfflux) {
        this.station = station;
        this.posAfflux = posAfflux;
        this.neutralAfflux = neutralAfflux;
        this.negativeAfflux = negativeAfflux;
    }

    @Override
    public String toString() {
        return String.format("%s;%d;%d;%d", station, posAfflux, neutralAfflux, negativeAfflux);
    }

    @Override
    public int compareTo(AffluenceByStationResult o) {
        int ans = Integer.compare(posAfflux, o.posAfflux);
        if (ans == 0) {
            ans = this.station.compareTo(o.station);
        }
        return ans;
    }
}
