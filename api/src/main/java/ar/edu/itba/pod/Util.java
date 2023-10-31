package ar.edu.itba.pod;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Util {
    public final static String HAZELCAST_GROUP_NAME = "g2";
    public final static String HAZELCAST_GROUP_PASSWORD = "c77f042090be14f682c74afda6e4cc18";
    public final static String HAZELCAST_NAMESPACE = "g2-namespace";

    public final static String HAZELCAST_NAMESPACE_2 = "g2-namespace-aux";

    public final static String STATIONS_FILENAME = "stations.csv";
    public final static String BIKES_FILENAME = "bikes.csv";

    public final static DateTimeFormatter INPUT_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final int SYSTEM_TIMEOUT = 1;
    public static final TimeUnit SYSTEM_TIMEOUT_UNIT = TimeUnit.MINUTES;
}
