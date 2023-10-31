package ar.edu.itba.pod;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class StationByDate implements DataSerializable {

    private int stationId;
    private LocalDate date;

    public StationByDate() {

    }

    private StationByDate(int stationId, LocalDate date) {
        this.stationId = stationId;
        this.date = date;
    }

    public static StationByDate of(int stationId, LocalDate date) {
        return new StationByDate(stationId, date);
    }

    public int getStationId() {
        return stationId;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StationByDate that = (StationByDate) o;

        if (stationId != that.stationId) return false;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        int result = stationId;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(stationId);
        out.writeObject(date);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        stationId = in.readInt();
        date = in.readObject();
    }
}
