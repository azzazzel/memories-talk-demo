package io.axoniq.demo.memories.client;

import io.axoniq.demo.memories.api.Book;
import io.axoniq.demo.memories.api.Person;
import io.axoniq.demo.memories.api.ShoppingCartCommands.RemoveBookCommand;
import io.axoniq.demo.memories.client.infra.Axon;
import org.axonframework.commandhandling.gateway.CommandGateway;

import java.util.UUID;


public interface BookRemover {
    void removeBookIfNeeded (UUID cartId, Person person, Book book, boolean singleOperation);

    default CommandGateway getGateway () {
        return Axon.getConfiguration().commandGateway();
    }

    class NoRemover implements BookRemover {
        public void removeBookIfNeeded (UUID cartId, Person person, Book book, boolean printActions) {
            // do nothing
        }
    }

    class RemoveOne implements BookRemover {
        boolean removed = false;
        public void removeBookIfNeeded (UUID cartId, Person person, Book book, boolean printActions) {
            if (!removed) {
                if (printActions) System.out.println("🛒📕 -> 🗑  " + book.title);
                getGateway().sendAndWait(RemoveBookCommand.builder().cartId(cartId).bookId(book.getId()).build());
                removed = true;
            }
        }
    }

    class OldBooksRemover implements BookRemover {
        int year = 1900;
        public void removeBookIfNeeded (UUID cartId, Person person, Book book, boolean singleOperation) {
            if (Integer.parseInt(book.getYear()) < year) {
                if (singleOperation) System.out.println("🛒📕 -> 🗑  " + book.title);
                getGateway().sendAndWait(RemoveBookCommand.builder().cartId(cartId).bookId(book.getId()).build());
            }
        }
    }

    class SameLetterRemover implements BookRemover {
        public void removeBookIfNeeded (UUID cartId, Person person, Book book, boolean singleOperation) {
            if (book.getTitle().startsWith(person.getName().substring(0, 1))) {
                if (singleOperation) System.out.println("🛒📕 -> 🗑  " + book.title);
                getGateway().sendAndWait(RemoveBookCommand.builder().cartId(cartId).bookId(book.getId()).build());
            }
        }
    }

}
