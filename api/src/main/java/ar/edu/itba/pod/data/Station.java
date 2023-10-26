package ar.edu.itba.pod.data;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

public class Station implements DataSerializable {
    private int pk;
    private String name;
    private double latitude;
    private double longitude;

    public Station() {

    }

    private Station(
            int pk,
            String name,
            double latitude,
            double longitude
    ) {
        this.pk = pk;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Station of(
            int pk,
            String name,
            double latitude,
            double longitude
    ) {
        return new Station(
                pk,
                name,
                latitude,
                longitude
        );
    }

    public int getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(pk);
        out.writeUTF(name);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        pk = in.readInt();
        name = in.readUTF();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }
}
