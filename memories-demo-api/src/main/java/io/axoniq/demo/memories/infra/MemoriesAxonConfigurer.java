package io.axoniq.demo.memories.infra;

import io.axoniq.demo.memories.api.SecureXStreamSerializer;
import org.axonframework.axonserver.connector.AxonServerConfiguration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;

public class MemoriesAxonConfigurer {


    public static Configurer defaultConfiguration () {
        return DefaultConfigurer.defaultConfiguration()
                .configureSerializer(configuration -> SecureXStreamSerializer.get())
                .configureMessageSerializer(configuration -> SecureXStreamSerializer.get())
                .configureEventSerializer(configuration -> SecureXStreamSerializer.get())
                ;
    }

    public static Configurer customConfiguration(String servers, String context, String token) {
         AxonServerConfiguration serverConfig = AxonServerConfiguration.builder().build();
         serverConfig.setServers(servers);
         serverConfig.setKeepAliveTimeout(0);
         serverConfig.setSslEnabled(true);
         serverConfig.setContext(context);
         serverConfig.setToken(token);

        return defaultConfiguration()
                .configureSerializer(configuration -> SecureXStreamSerializer.get())
                .configureMessageSerializer(configuration -> SecureXStreamSerializer.get())
                .configureEventSerializer(configuration -> SecureXStreamSerializer.get())
                .registerComponent(AxonServerConfiguration.class, config -> serverConfig)
                ;
    }

    public static Configurer defaultCloudConfiguration (String context, String token) {
        return customConfiguration("axonserver.cloud.axoniq.io:443", context, token);
    }


}
