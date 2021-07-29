package io.axoniq.demo.memories.service.upsell;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;

import io.axoniq.demo.memories.api.ShoppingCartEvents.BookRemovedEvent;
import io.axoniq.demo.memories.api.ShoppingCartEvents.CheckoutEvent;
import io.axoniq.demo.memories.api.ShoppingCartEvents.ShoppingCartCreatedEvent;

public class ConsideredBeforeProjection {

	private static Map<Integer, Set<Integer>> consideredBeforeBooks = new HashMap<Integer, Set<Integer>>();

	private static Map<UUID, Integer> cart2person = new HashMap<>();
	
	private static long count = 0;

	@EventHandler
	public void on(ShoppingCartCreatedEvent event) {
		countEvent();

		cart2person.put(event.getCartId(), event.getPersonId());
	}

	@EventHandler
	public void on(BookRemovedEvent event) {
		countEvent();
		
		Integer personId = cart2person.get(event.getCartId());

		Set<Integer> books = consideredBeforeBooks.get(personId);
		if (books == null) {
			books = new HashSet<>();
			consideredBeforeBooks.put(personId, books);
		}
		books.add(event.getBookId());
	}

	@EventHandler
	public void on(CheckoutEvent event) {
		countEvent();
		
		Integer personId = cart2person.get(event.getCartId());
		
		if (consideredBeforeBooks.containsKey(personId)) {
			Set<Integer> books = consideredBeforeBooks.get(personId);
			event.getBookIds().forEach(bookId -> {
				books.remove(bookId);
			});
		}
	}

	@QueryHandler (queryName = "booksConsideredBefore")
	public Set<Integer> potentialBooks(Integer personId) {
		return consideredBeforeBooks.containsKey(personId) ? consideredBeforeBooks.get(personId) : Collections.emptySet();
	}

	private void countEvent() {
		count++;
		if (count % 1000 == 0) {
			System.out.print(".");
		}
		if (count % 10000 == 0) {
			System.out.println(count + " events processed");
		}
	}

}
