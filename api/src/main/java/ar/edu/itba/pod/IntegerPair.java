package ar.edu.itba.pod;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

public class IntegerPair implements DataSerializable {
    private int key;
    private int value;

    public IntegerPair() {

    }

    private IntegerPair(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public static IntegerPair of(int key, int value) {
        return new IntegerPair(key, value);
    }

    public int getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerPair that = (IntegerPair) o;
        return key == that.key && value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(key);
        out.writeInt(value);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        key = in.readInt();
        value = in.readInt();
    }
}
