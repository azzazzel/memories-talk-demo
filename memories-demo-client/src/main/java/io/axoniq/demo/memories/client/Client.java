package io.axoniq.demo.memories.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.Configuration;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;

import io.axoniq.demo.memories.api.Book;
import io.axoniq.demo.memories.api.BooksRepository;
import io.axoniq.demo.memories.api.PeopleRepository;
import io.axoniq.demo.memories.api.Person;
import io.axoniq.demo.memories.api.SecureXStreamSerializer;
import io.axoniq.demo.memories.api.ShoppingCartCommands.AddBookCommand;
import io.axoniq.demo.memories.api.ShoppingCartCommands.CheckoutCommand;
import io.axoniq.demo.memories.api.ShoppingCartCommands.CreateShoppingCartCommand;
import io.axoniq.demo.memories.api.ShoppingCartCommands.RemoveBookCommand;

public class Client {

	private static CommandGateway commandGateway;
	private static QueryGateway queryGateway;
	private static Configuration axonConfiguration;
	private static Random random = new Random();
	private static Client _instance;

	private static long count = 0;

	private Client() {
		System.setProperty("axon.application.name", "Client app");

		// JacksonSerializer messageSerializer = JacksonSerializer.defaultSerializer();

		axonConfiguration = DefaultConfigurer.defaultConfiguration()
				// .configureSerializer(configuration -> messageSerializer)
				// .configureMessageSerializer(configuration -> messageSerializer)
				// .configureEventSerializer(configuration -> messageSerializer)
				.configureSerializer(configuration -> SecureXStreamSerializer.get())
				.configureMessageSerializer(configuration -> SecureXStreamSerializer.get())
				.configureEventSerializer(configuration -> SecureXStreamSerializer.get())
				.buildConfiguration();

		axonConfiguration.start();
		commandGateway = axonConfiguration.commandGateway();
		queryGateway = axonConfiguration.queryGateway();
	}

	public static Client get() throws IOException, URISyntaxException {
		if (_instance == null) {
			_instance = new Client();
		}
		return _instance;
	}

	public void shutdown() {
		axonConfiguration.shutdown();
	}

	public void generate(int attempts) {
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

	public void printSoldCopies(int bookId) {
		try {
			Integer copies = queryGateway
					.query("bookPurchased", Integer.valueOf(bookId), ResponseTypes.instanceOf(Integer.class)).get();
			if (copies == null || copies == 0) {
				System.out.println("No copies of '" + BooksRepository.get().get(bookId).getTitle() + "' were sold!");
			} else {
				System.out.println(
						copies + " copies of '" + BooksRepository.get().get(bookId).getTitle() + "' were sold!");
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}

	public void simulateClientShopping() {
		simulateClientShopping(null, 4, new RemoveOne(), true);
	}

	public void simulateClientShopping(String name) {
		simulateClientShopping(PeopleRepository.get().get(name).get(), 4, new RemoveOne(), true);
	}

	public void simulateClientShopping(Person person, int numberOfBooks, BookRemover bookRemover,
			boolean singleOperation) {
		
		if (person == null) {
			person = PeopleRepository.get().randomPerson();
		}

		if (singleOperation) {
			System.out.println();
			System.out.println(person.getName() + " logs in");
		}

		try {
			List<Integer> consideredBooks = queryGateway.query("booksConsideredBefore", Integer.valueOf(person.getId()),
					ResponseTypes.multipleInstancesOf(Integer.class)).get();

			if (consideredBooks != null && !consideredBooks.isEmpty()) {
				consideredBooks.forEach(b -> {
					System.out.println("👉  10% DISCOUNT on " + BooksRepository.get().get(b).getTitle());
				});
			}

		} catch (InterruptedException | ExecutionException e) {
		}

		UUID cartId = UUID.randomUUID();
		CreateShoppingCartCommand createCardCommand = CreateShoppingCartCommand.builder().cartId(cartId)
				.personId(person.getId()).build();
		commandGateway.sendAndWait(createCardCommand);


		for (int i = 0; i < numberOfBooks; i++) {
			Book book = BooksRepository.get().randomBook();

			if (singleOperation) System.out.println("📗  ->  🛒 " + book.title );
			commandGateway.sendAndWait(AddBookCommand.builder().cartId(cartId).bookId(book.getId()).build());

			bookRemover.removeBookIfNeeded(cartId, person, book, singleOperation);
		}

		if (singleOperation) System.out.println("🛒 ✅ ");
		commandGateway.sendAndWait(new CheckoutCommand(cartId));

	}



	private class ClientShoppingProcess implements Runnable {
		private Person person;
		private int numberOfBooks;
		// BookRemover bookRemover = new OldBooksRemover();
		BookRemover bookRemover = new SameLetterRemover();
		
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
			simulateClientShopping(person, numberOfBooks, bookRemover, false);
		}

	}

	private interface BookRemover {
		void removeBookIfNeeded (UUID cartId, Person person, Book book, boolean singleOperation);
	}

	private class RemoveOne implements BookRemover {
		boolean removed = false;
		public void removeBookIfNeeded (UUID cartId, Person person, Book book, boolean printActions) {
			if (!removed) {
				if (printActions) System.out.println("🛒📕 -> 🗑  " + book.title);
				commandGateway.sendAndWait(RemoveBookCommand.builder().cartId(cartId).bookId(book.getId()).build());
				removed = true;
			}
		}
	}

	private class OldBooksRemover implements BookRemover {
		int year = 1900;
		public void removeBookIfNeeded (UUID cartId, Person person, Book book, boolean singleOperation) {
			if (Integer.parseInt(book.getYear()) < year) {
				if (singleOperation) System.out.println("🛒📕 -> 🗑  " + book.title);
				commandGateway.sendAndWait(RemoveBookCommand.builder().cartId(cartId).bookId(book.getId()).build());
			}
		}
	}

	private class SameLetterRemover implements BookRemover {
		public void removeBookIfNeeded (UUID cartId, Person person, Book book, boolean singleOperation) {
			if (book.getTitle().startsWith(person.getName().substring(0, 1))) {
				if (singleOperation) System.out.println("🛒📕 -> 🗑  " + book.title);
				commandGateway.sendAndWait(RemoveBookCommand.builder().cartId(cartId).bookId(book.getId()).build());
			}
		}
	}

}
