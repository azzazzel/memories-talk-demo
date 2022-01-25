package io.axoniq.demo.memories.service.upsell.infra;

import io.axoniq.demo.memories.infra.MemoriesAxonConfigurer;
import io.axoniq.demo.memories.service.upsell.ConsideredBeforeProjection;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;

public class Axon {

    private final Configuration configuration;

    private static Axon THIS;

    private Axon() {
        System.setProperty("axon.application.name", "Upsell services app");
        Configurer axonConfigurer = MemoriesAxonConfigurer.defaultConfiguration()
                .registerMessageHandler(conf -> new ConsideredBeforeProjection());

        configuration = axonConfigurer.buildConfiguration();
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
