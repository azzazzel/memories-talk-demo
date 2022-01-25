package io.axoniq.demo.memories.service.upsell;


import java.io.IOException;

import io.axoniq.demo.memories.service.upsell.infra.Axon;
import org.axonframework.config.Configuration;
import org.axonframework.config.DefaultConfigurer;

import io.axoniq.demo.memories.api.SecureXStreamSerializer;


public class UpsellServicesApp {
	
	public static void main (String[] args) {

		Axon.start();
		
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Axon.shutdown();

	}
}
