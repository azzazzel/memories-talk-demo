package io.axoniq.demo.memories.client.infra;

import io.axoniq.demo.memories.infra.MemoriesAxonConfigurer;
import org.axonframework.config.Configuration;

public class Axon {

    private final Configuration configuration;

    private static Axon THIS;

    private Axon() {
        System.setProperty("axon.application.name", "Client app");
        configuration = MemoriesAxonConfigurer.defaultConfiguration().buildConfiguration();
        configuration.start();
    }

    public static Configuration getConfiguration() {
        start();
        return THIS.configuration;
    }

    public static void start() {
        if (THIS == null) {
            THIS = new Axon();
        }
    }
    public static void shutdown() {
        if (THIS != null) {
            THIS.configuration.shutdown();
        }
    }


}
