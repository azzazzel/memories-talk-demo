package io.axoniq.demo.memories.service.shopping;

import java.util.HashMap;

import io.axoniq.demo.memories.api.BooksRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;

import io.axoniq.demo.memories.api.ShoppingCartEvents.CheckoutEvent;

public class BooksPurchasedProjection {


	private static HashMap<Integer, Integer> sales = new HashMap<Integer, Integer>();


	@EventHandler
	public void on(CheckoutEvent event) {
		System.out.println("\n\n==================================================");
		System.out.println("🗄️  BooksPurchasedProjection received an event: " + CheckoutEvent.class);
		System.out.println("--------------------------------------------------");

		event.getBookIds().forEach(bookId -> {
			int soldCopies = (sales.containsKey(bookId) ? sales.get(bookId) : 0) + 1;
			sales.put(bookId, Integer.valueOf(soldCopies));
			System.out.println("📝  Adding a copy of '" + BooksRepository.get(bookId).getTitle() + "' to books purchased projection ");
		});


		System.out.println("--------------------------------------------------");
		System.out.println("🗄️  Event was successfully processed");
		System.out.println("==================================================");

	}


	@QueryHandler (queryName = "bookPurchased")
	public Integer handle(Integer bookId) {
		System.out.println("🔎  looking for sold copies");
		return sales.get(bookId);
	}

}
