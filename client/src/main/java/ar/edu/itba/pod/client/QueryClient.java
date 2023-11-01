package ar.edu.itba.pod.client;

import ar.edu.itba.pod.Util;
import ar.edu.itba.pod.data.Bike;
import ar.edu.itba.pod.data.Station;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public abstract class QueryClient {
    private final static Logger logger = LoggerFactory.getLogger(QueryClient.class);

    private HazelcastInstance hz;
    private String[] addresses;
    private Path bikesPath;
    private Path stationsPath;
    private Path outPath;

    public QueryClient() {
        int status = 0;
        try {
            checkArguments();
            this.hz = startHazelcastClient(this.addresses);
            logger.warn("Hazelcast client started.");
            logger.info("Starting to load data.");
            loadData();
            logger.info("Finished loading data.");
            logger.info("Starting map/reduce job.");
            resolveQuery();
            logger.info("Finished map/reduce job.");
        } catch (IllegalArgumentException e) {
            System.err.println("Oops! Invalid arguments were sent:\n" + e.getMessage());
            status = 64;
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Oops! Something went wrong, try again!");
            logger.error(e.getMessage(), e);
            status = 130;
        } catch (IOException e) {
            System.err.println("Oops! Something went wrong when trying to write the results, try again!");
            logger.error(e.getMessage(), e);
            status = 74;
        } catch (IllegalStateException e) {
            System.err.println("Oops! We weren't able to connect to Hazelcast. Is the server running?");
            logger.error(e.getMessage(), e);
            status = 69;
        } catch (Exception e) {
            System.err.println("Oops! Something unexpected went wrong, try again!");
            logger.error(e.getMessage(), e);
            status = 127;
        } finally {
            logger.info("Destroying data.");
            destroyData();
            logger.info("All data was destroyed.");
            if (this.hz != null) {
                this.hz.shutdown();
            }
        }
        System.exit(status);
    }

    public HazelcastInstance getHz() {
        return hz;
    }

    private HazelcastInstance startHazelcastClient(String[] addresses) {
        ClientConfig clientConfig = new ClientConfig();
        GroupConfig groupConfig = new GroupConfig()
                .setName(Util.HAZELCAST_GROUP_NAME)
                .setPassword(Util.HAZELCAST_GROUP_PASSWORD);
        clientConfig.setGroupConfig(groupConfig);

        ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
        clientNetworkConfig.addAddress(addresses);
        clientConfig.setNetworkConfig(clientNetworkConfig);

        clientConfig.setProperty("hazelcast.logging.type", "none");

        return HazelcastClient.newHazelcastClient(clientConfig);
    }

    public void checkArguments() throws IllegalArgumentException {
        StringBuilder errors = new StringBuilder();

        String addressesArgument = System.getProperty("addresses");
        String inPathArgument = System.getProperty("inPath");
        String outPathArgument = System.getProperty("outPath");

        if (addressesArgument == null) {
            errors.append("Argument 'addresses' must be provided\n");
        }
        if (inPathArgument == null) {
            errors.append("Argument 'inPath' must be provided\n");
        }
        if (outPathArgument == null) {
            errors.append("Argument 'outPath' must be provided\n");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }

        this.addresses = addressesArgument.split(";");
        Path inPath = Path.of(inPathArgument);
        this.outPath = Path.of(outPathArgument);

        if (!Files.isDirectory(inPath)) {
            errors.append("Provided 'inPath' is not a directory\n");
        } else {
            this.bikesPath = Path.of(inPathArgument, Util.BIKES_FILENAME);
            this.stationsPath = Path.of(inPathArgument, Util.STATIONS_FILENAME);

            if (!Files.exists(this.bikesPath)) {
                errors.append(String.format("File %s does not exist in provided 'inPath'\n", Util.BIKES_FILENAME));
            }
            if (!Files.exists(this.stationsPath)) {
                errors.append(String.format("File %s does not exist in provided 'inPath'\n", Util.STATIONS_FILENAME));
            }
        }

        if (!Files.isDirectory(this.outPath)) {
            errors.append("Provided 'outPath' is not a directory\n");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }
    }

    private void loadData() {
        if (hz == null) {
            return;
        }

        try (
                ExecutorService service = Executors.newCachedThreadPool();
        ) {
            service.submit(new LoadBikesRunnable(
                    hz.getMultiMap(Util.HAZELCAST_NAMESPACE),
                    bikesPath
            ));
            service.submit(new LoadStationsRunnable(
                    hz.getMap(Util.HAZELCAST_NAMESPACE),
                    stationsPath
            ));

            service.shutdown();
            service.awaitTermination(Util.SYSTEM_TIMEOUT, Util.SYSTEM_TIMEOUT_UNIT);
        } catch (InterruptedException e) {
            logger.error("Interrupted load of data");
            System.exit(2);
        }
    }

    public abstract void resolveQuery() throws ExecutionException, InterruptedException, IOException;

    public abstract String getQueryNumber();

    public abstract String getQueryHeader();

    private void destroyData() {
        if (hz == null) {
            return;
        }

        hz.getMap(Util.HAZELCAST_NAMESPACE).clear();
        hz.getMultiMap(Util.HAZELCAST_NAMESPACE).clear();
    }

    public void writeResults(Collection<? extends Result> results) throws IOException {
        Path queryPath = outPath.resolve("query" + getQueryNumber() + ".csv");
        try (BufferedWriter writer = Files.newBufferedWriter(
                queryPath, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            writer.write(getQueryHeader());
            writer.newLine();
            for (Result result : results) {
                writer.write(result.toString());
                writer.newLine();
            }
        }
    }

    private static class LoadBikesRunnable implements Runnable {
        private final MultiMap<Integer, Bike> mm;
        private final Path filePath;

        public LoadBikesRunnable(MultiMap<Integer, Bike> mm, Path filePath) {
            this.mm = mm;
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try (
                    Stream<String> lines = Files.lines(filePath).skip(1).parallel()
            ) {
                lines.forEach(line -> {
                    String[] fields = line.split(";");

                    mm.put(
                            Integer.parseInt(fields[1]),
                            Bike.of(
                                Integer.parseInt(fields[1]),
                                Integer.parseInt(fields[3]),
                                LocalDateTime.parse(fields[0], Util.INPUT_DATETIME_FORMAT),
                                LocalDateTime.parse(fields[2], Util.INPUT_DATETIME_FORMAT),
                                fields[4].charAt(0) == '1'
                            )
                    );
                });
            } catch (IOException e) {
                logger.error("Error in data loading");
                logger.error(e.getMessage());
            }
        }
    }

    private static class LoadStationsRunnable implements Runnable {
        private final Map<Integer, Station> m;
        private final Path filePath;

        public LoadStationsRunnable(Map<Integer, Station> m, Path filePath) {
            this.m = m;
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try (
                    Stream<String> lines = Files.lines(filePath).skip(1).parallel()
            ) {
                lines.forEach(line -> {
                    String[] fields = line.split(";");

                    m.put(
                            Integer.parseInt(fields[0]),
                            Station.of(
                                    Integer.parseInt(fields[0]),
                                    fields[1],
                                    Double.parseDouble(fields[2]),
                                    Double.parseDouble(fields[3])
                            )
                    );
                });
            } catch (IOException e) {
                logger.error("Error in data loading");
                logger.error(e.getMessage());
            }
        }
    }
}
