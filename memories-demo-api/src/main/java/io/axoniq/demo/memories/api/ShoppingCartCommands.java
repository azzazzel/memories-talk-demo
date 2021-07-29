package io.axoniq.demo.memories.api;

import java.util.UUID;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

public class ShoppingCartCommands {

	@Value
	@Builder
	public static class AddBookCommand {
		@TargetAggregateIdentifier
		private UUID cartId;
		private int bookId;
	}

	@Value
	@AllArgsConstructor
	public static class CheckoutCommand {
		@TargetAggregateIdentifier
		private UUID cartId;
	}

	@Value
	@Builder
	public static class CreateShoppingCartCommand {
		@TargetAggregateIdentifier
		private UUID cartId;
		private int personId;
	}

	@Value
	@Builder
	public static class RemoveBookCommand {
		@TargetAggregateIdentifier
		private UUID cartId;
		private int bookId;
	}

}
