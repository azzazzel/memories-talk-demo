package io.axoniq.demo.memories.service.shopping.infra;

import io.axoniq.demo.memories.infra.MemoriesAxonConfigurer;
import io.axoniq.demo.memories.service.shopping.BooksPurchasedProjection;
import io.axoniq.demo.memories.service.shopping.ShoppingCart;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;

public class Axon {

    private final Configuration configuration;

    private static Axon THIS;

    private Axon() {
        System.setProperty("axon.application.name", "Shopping services app");
        Configurer axonConfigurer = MemoriesAxonConfigurer.defaultConfiguration()
                .configureAggregate(ShoppingCart.class)
                .registerMessageHandler(conf -> new BooksPurchasedProjection())
                .registerModule(conf -> {
                    conf.commandBus().registerHandlerInterceptor(new CommandLogger());
                });

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
