package ar.edu.itba.pod;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public class LocalDateTimeLongPair implements DataSerializable {
    private LocalDateTime key;
    private long value;

    public LocalDateTimeLongPair() {

    }

    private LocalDateTimeLongPair(LocalDateTime key,long value) {
        this.key = key;
        this.value = value;
    }

    public static LocalDateTimeLongPair of(LocalDateTime key,long value) {
        return new LocalDateTimeLongPair(key,value);
    }

    public LocalDateTime getKey() {
        return key;
    }

    public long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalDateTimeLongPair that = (LocalDateTimeLongPair) o;
        return value == that.value && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(key.toEpochSecond(ZoneOffset.UTC));
        out.writeLong(value);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        key = LocalDateTime.ofEpochSecond(in.readLong(), 0, ZoneOffset.UTC);
        value = in.readLong();
    }
}
