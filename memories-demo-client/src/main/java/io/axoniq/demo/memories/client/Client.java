package io.axoniq.demo.memories.client;

import io.axoniq.demo.memories.api.Book;
import io.axoniq.demo.memories.api.BooksRepository;
import io.axoniq.demo.memories.api.PeopleRepository;
import io.axoniq.demo.memories.api.Person;
import io.axoniq.demo.memories.api.ShoppingCartCommands.AddBookCommand;
import io.axoniq.demo.memories.api.ShoppingCartCommands.CheckoutCommand;
import io.axoniq.demo.memories.api.ShoppingCartCommands.CreateShoppingCartCommand;
import io.axoniq.demo.memories.client.infra.Axon;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Client {

	private final CommandGateway commandGateway;
	private final QueryGateway queryGateway;
	private final Random random = new Random();
	private static Client THIS;

	private static final long count = 0;

	private Client() {
		commandGateway = Axon.getConfiguration().commandGateway();
		queryGateway = Axon.getConfiguration().queryGateway();
		// send dummy request to the server so the app pops in the dashboard immediately
		try {
			Axon.getConfiguration().eventStore().readEvents("");
		} catch (Exception e) {}
	}

	public static Client get() {
		if (THIS == null) {
			THIS = new Client();
		}
		return THIS;
	}

	public void printSoldCopies(int bookId) {
		try {
			Integer copies = queryGateway
					.query("bookPurchased", Integer.valueOf(bookId), ResponseTypes.instanceOf(Integer.class)).get();
			if (copies == null || copies == 0) {
				System.out.println("No copies of '" + BooksRepository.get(bookId).getTitle() + "' were sold!");
			} else {
				System.out.println(
						copies + " copies of '" + BooksRepository.get(bookId).getTitle() + "' were sold!");
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}

	public void simulateClientShopping() {
		simulateClientShopping(null, 4, new BookRemover.RemoveOne(), true);
	}

	public void simulateClientShopping(String name) {
		simulateClientShopping(PeopleRepository.get().get(name).get(), 4, new BookRemover.RemoveOne(), true);
	}

	public void simulateClientShopping(Person person, int numberOfBooks, BookRemover bookRemover, boolean singleOperation) {

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
					System.out.println("👉  10% DISCOUNT on " + BooksRepository.get(b).getTitle());
				});
			}

		} catch (InterruptedException | ExecutionException e) {
		}

		UUID cartId = UUID.randomUUID();
		CreateShoppingCartCommand createCardCommand = CreateShoppingCartCommand.builder().cartId(cartId)
				.personId(person.getId()).build();
		commandGateway.sendAndWait(createCardCommand);


		for (int i = 0; i < numberOfBooks; i++) {
			Book book = BooksRepository.randomBook();

			if (singleOperation) System.out.println("📗  ->  🛒 " + book.title );
			commandGateway.sendAndWait(AddBookCommand.builder().cartId(cartId).bookId(book.getId()).build());

			bookRemover.removeBookIfNeeded(cartId, person, book, singleOperation);
		}

		if (singleOperation) System.out.println("🛒 ✅ ");
		commandGateway.sendAndWait(new CheckoutCommand(cartId));

	}
}
