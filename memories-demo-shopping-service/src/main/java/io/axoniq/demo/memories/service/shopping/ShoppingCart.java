package io.axoniq.demo.memories.service.shopping;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;

import io.axoniq.demo.memories.api.BooksRepository;
import io.axoniq.demo.memories.api.PeopleRepository;
import io.axoniq.demo.memories.api.ShoppingCartCommands.AddBookCommand;
import io.axoniq.demo.memories.api.ShoppingCartCommands.CheckoutCommand;
import io.axoniq.demo.memories.api.ShoppingCartCommands.CreateShoppingCartCommand;
import io.axoniq.demo.memories.api.ShoppingCartCommands.RemoveBookCommand;
import io.axoniq.demo.memories.api.ShoppingCartEvents.BookAddedEvent;
import io.axoniq.demo.memories.api.ShoppingCartEvents.BookRemovedEvent;
import io.axoniq.demo.memories.api.ShoppingCartEvents.CheckoutEvent;
import io.axoniq.demo.memories.api.ShoppingCartEvents.ShoppingCartCreatedEvent;
import lombok.ToString;

@ToString
public class ShoppingCart {


	@AggregateIdentifier
	UUID cartID;
	int personId;
	List<Integer> books = new ArrayList<Integer>();

	public ShoppingCart() {
		System.out.println("🏗  new ShoppingCart()");
	}

	@CommandHandler
	public ShoppingCart(CreateShoppingCartCommand command) {
		System.out.println("🏗  new ShoppingCart()");

		System.out.println("✨ \t [ShoppingCart] handle 'CreateShoppingCartCommand'");

		/*
		 * Some logic here to decide if the cart can be created
		 */

		System.out.println("📮 \t [ShoppingCart] send 'ShoppingCartCreatedEvent'");
		AggregateLifecycle.apply(
			ShoppingCartCreatedEvent.builder()
				.cartId(command.getCartId())
				.personId(command.getPersonId()
			).build()
		);
	}

	@CommandHandler
	public void addBook(AddBookCommand command) {

		System.out.println("✨ \t [ShoppingCart] handle 'AddBookCommand'");
		/*
		 * Some logic here to decide if the book can be added to the cart
		 */

		System.out.println("📮  \t [ShoppingCart] send 'BookAddedEvent'");
		AggregateLifecycle.apply(
			BookAddedEvent.builder()
				.cartId(command.getCartId())
				.bookId(command.getBookId())
				.build()
		);



	}

	@CommandHandler
	public void removeBook(RemoveBookCommand command) {
		System.out.println("✨ \t [ShoppingCart] handle 'remove book'");
		/*
		 * Some logic here to decide if the book can be removed from the cart
		 */

		System.out.println("📮 \t [ShoppingCart] send 'BookRemovedEvent'");
		AggregateLifecycle.apply(
			BookRemovedEvent.builder()
				.cartId(command.getCartId())
				.bookId(command.getBookId())
				.build()
		);
	}

	@CommandHandler
	public void checkOut(CheckoutCommand command) {
		System.out.println("✨ \t [ShoppingCart] handle 'CheckoutCommand'");
		/*
		 * Some logic here to decide if the book can be removed from the cart
		 */

		System.out.println("📮 \t [ShoppingCart] send 'CheckoutEvent'");
		AggregateLifecycle.apply(
			CheckoutEvent.builder()
				.cartId(command.getCartId())
				.bookIds(books)
				.build()
		);
	}


    @EventSourcingHandler
    public void on(ShoppingCartCreatedEvent event) {
		cartID = event.getCartId();
		personId = event.getPersonId();
		System.out.println(getIcon() + " \t [ShoppingCart] [ ShoppingCartCreatedEvent ] setUser('" + PeopleRepository.get().get(personId).getName() + "')");
    }

    @EventSourcingHandler
    public void on(BookAddedEvent event) {
    	books.add(event.getBookId());
		System.out.println(getIcon() + " \t [ShoppingCart] [ BookAddedEvent           ] books.add('" + BooksRepository.get(event.getBookId()).getTitle() + "')");
    }

    @EventSourcingHandler
    public void on(BookRemovedEvent event) {
    	books.remove(Integer.valueOf(event.getBookId()));
		System.out.println(getIcon() + " \t [ShoppingCart] [ BookRemovedEvent         ] books.remove('" + BooksRepository.get(event.getBookId()).getTitle() + "')");
    }

    @EventSourcingHandler
    public void on(CheckoutEvent event) {
		AggregateLifecycle.markDeleted();
		System.out.println(getIcon() + " \t [ShoppingCart] [ CheckoutEvent            ] checkout!");
    }


	private String getIcon() {
		return AggregateLifecycle.isLive() ? "📩" : "🏗";
	}

}
