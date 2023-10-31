package ar.edu.itba.pod;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

public class TripleInteger implements DataSerializable {
    private int first;
    private int second;
    private int third;

    public TripleInteger() {

    }
    private TripleInteger(int first, int second, int third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static TripleInteger of(int first, int second, int third) {
        return new TripleInteger(first, second, third);
    }

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }

    public int getThird() {
        return third;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(first);
        out.writeInt(second);
        out.writeInt(third);
    }

    @Override
    public void readData(ObjectDataInput out) throws IOException {
        first = out.readInt();
        second = out.readInt();
        third = out.readInt();
    }
}
