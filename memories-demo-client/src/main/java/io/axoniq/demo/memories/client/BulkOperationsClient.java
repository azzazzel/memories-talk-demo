package io.axoniq.demo.memories.client;

import io.axoniq.demo.memories.api.PeopleRepository;
import io.axoniq.demo.memories.api.Person;
import io.axoniq.demo.memories.client.infra.Axon;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BulkOperationsClient {

	private final CommandGateway commandGateway;
	private final QueryGateway queryGateway;
	private final Random random = new Random();
	private static BulkOperationsClient THIS;

	private static long count = 0;


	private BulkOperationsClient() {
		commandGateway = Axon.getConfiguration().commandGateway();
		queryGateway = Axon.getConfiguration().queryGateway();
	}

	public static BulkOperationsClient get() throws IOException, URISyntaxException {
		if (THIS == null) {
			THIS = new BulkOperationsClient();
		}
		return THIS;
	}


	public void generateSales(int attempts) {
		count = 0;
		ExecutorService executorService = Executors.newFixedThreadPool(20);
		for (int i = 0; i < attempts; i++) {
			executorService
					.submit(new ClientShoppingProcess(PeopleRepository.get().randomPerson(), random.nextInt(5) + 1));
		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private class ClientShoppingProcess implements Runnable {
		private final Person person;
		private final int numberOfBooks;
		BookRemover bookRemover = new BookRemover.SameLetterRemover();
		Client client = Client.get();

		public ClientShoppingProcess(Person client, int numberOfBooks) {
			this.person = client;
			this.numberOfBooks = numberOfBooks;
		}

		@Override
		public void run() {
			count++;
			if ( count % 100 == 0) {
				System.out.print(".");
			}
			if ( count % 10000 == 0) {
				System.out.println("\n created " + count + " carts");
			}
			client.simulateClientShopping(person, numberOfBooks, bookRemover, false);
		}

	}

}
