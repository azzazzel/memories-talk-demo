package io.axoniq.demo.memories.service.shopping;

import java.io.IOException;

import org.axonframework.config.Configuration;
import org.axonframework.config.DefaultConfigurer;

import io.axoniq.demo.memories.api.SecureXStreamSerializer;


public class ShopingSerivcesApp {
	
	public static void main (String[] args) {
		System.setProperty("axon.application.name", "Shopping services app");

		Configuration axonConfiguration = DefaultConfigurer.defaultConfiguration()
			.configureSerializer(configuration -> SecureXStreamSerializer.get())
			.configureMessageSerializer(configuration -> SecureXStreamSerializer.get())
			.configureEventSerializer(configuration -> SecureXStreamSerializer.get())
			.configureAggregate(ShoppingCart.class)
			.registerMessageHandler(conf -> new BooksPurchasedProjection())
			.buildConfiguration();

		axonConfiguration.start();
		
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		axonConfiguration.shutdown();

	}
}
