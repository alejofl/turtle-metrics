package ar.edu.itba.pod;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public class LongTripValues implements DataSerializable {
    private int endStation;
    private LocalDateTime startDate;
    private long minutes;

    public LongTripValues() {

    }

    private LongTripValues(int endStation, LocalDateTime startDate, long minutes) {
        this.endStation = endStation;
        this.startDate = startDate;
        this.minutes = minutes;
    }

    public static LongTripValues of(int endStation, LocalDateTime startDate, long minutes) {
        return new LongTripValues(endStation,startDate,minutes);
    }

    public int getEndStation() {
        return endStation;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public long getMinutes() {
        return minutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongTripValues that = (LongTripValues) o;
        return endStation == that.endStation && minutes == that.minutes && Objects.equals(startDate, that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endStation, startDate, minutes);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(endStation);
        out.writeLong(startDate.toEpochSecond(ZoneOffset.UTC));
        out.writeLong(minutes);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        endStation = in.readInt();
        startDate = LocalDateTime.ofEpochSecond(in.readLong(), 0, ZoneOffset.UTC);
        minutes = in.readLong();
    }
}
