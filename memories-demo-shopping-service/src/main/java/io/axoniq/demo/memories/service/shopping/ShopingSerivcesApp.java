package io.axoniq.demo.memories.service.shopping;

import io.axoniq.demo.memories.service.shopping.infra.Axon;

import java.io.IOException;


public class ShopingSerivcesApp {

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
