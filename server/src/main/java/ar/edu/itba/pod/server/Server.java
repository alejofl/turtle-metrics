package ar.edu.itba.pod.server;

import ar.edu.itba.pod.Util;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        logger.info("Starting Hazelcast cluster...");

        final Config config = new Config();
        final GroupConfig groupConfig = new GroupConfig()
                .setName(Util.HAZELCAST_GROUP_NAME)
                .setPassword(Util.HAZELCAST_GROUP_PASSWORD);
        config.setGroupConfig(groupConfig);

        final JoinConfig joinConfig = new JoinConfig().setMulticastConfig(new MulticastConfig());
        final NetworkConfig networkConfig = new NetworkConfig().setJoin(joinConfig);
        config.setNetworkConfig(networkConfig);

        config.setProperty("hazelcast.logging.type", "none");

        // Start cluster
        final HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);
        logger.info("Hazelcast cluster discoverable on " + instance.getCluster().getLocalMember().getAddress());
    }
}
