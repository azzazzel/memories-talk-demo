package io.axoniq.demo.memories.service.shopping;

import java.util.HashMap;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;

import io.axoniq.demo.memories.api.ShoppingCartEvents.CheckoutEvent;

public class BooksPurchasedProjection {

	
	private static HashMap<Integer, Integer> sales = new HashMap<Integer, Integer>();
	
	
	@EventHandler
	public void on(CheckoutEvent event) {
		event.getBookIds().forEach(bookId -> {
			int soldCopies = (sales.containsKey(bookId) ? sales.get(bookId) : 0) + 1;
			sales.put(bookId, Integer.valueOf(soldCopies));
		});
	}
	

	@QueryHandler (queryName = "bookPurchased")
	public Integer handle(Integer bookId) {
		return sales.get(bookId);
	}

}
