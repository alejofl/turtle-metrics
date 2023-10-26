package ar.edu.itba.pod.data;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Bike implements DataSerializable {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int startStationPK;
    private int endStationPK;
    private boolean isMember;

    public Bike() {

    }

    private Bike(
            int startStationPK,
            int endStationPK,
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean isMember
    ) {
        this.startStationPK = startStationPK;
        this.endStationPK = endStationPK;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isMember = isMember;
    }

    public static Bike of(
            int startStationPK,
            int endStationPK,
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean isMember
    ) {
        return new Bike(
                startStationPK,
                endStationPK,
                startDate,
                endDate,
                isMember
        );
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public int getStartStationPK() {
        return startStationPK;
    }

    public int getEndStationPK() {
        return endStationPK;
    }

    public boolean isMember() {
        return isMember;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(startStationPK);
        out.writeInt(endStationPK);
        out.writeLong(startDate.toEpochSecond(ZoneOffset.UTC));
        out.writeLong(endDate.toEpochSecond(ZoneOffset.UTC));
        out.writeBoolean(isMember);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        startStationPK = in.readInt();
        endStationPK = in.readInt();
        startDate = LocalDateTime.ofEpochSecond(in.readLong(), 0, ZoneOffset.UTC);
        endDate = LocalDateTime.ofEpochSecond(in.readLong(), 0, ZoneOffset.UTC);
        isMember = in.readBoolean();
    }
}
