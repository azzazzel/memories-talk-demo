package io.axoniq.demo.memories.api;

import java.util.List;
import java.util.UUID;

import org.axonframework.modelling.command.AggregateIdentifier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

public class ShoppingCartEvents {

	@Value
	@Builder
	public static class BookAddedEvent {
		@AggregateIdentifier
		private UUID cartId;
		private int bookId;
	}

	@Value
	@Builder
	public static class BookRemovedEvent {
		@AggregateIdentifier
		private UUID cartId;
		private int bookId;
	}

	@Value
	@Builder
	public static class CheckoutEvent {
		@AggregateIdentifier
		private UUID cartId;
		private List<Integer> bookIds;
	}

	@Value
	@Builder
	public static class ShoppingCartCreatedEvent {
		@AggregateIdentifier
		private UUID cartId;
		private int personId;
	}

}
