package io.axoniq.demo.memories.api;


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


public class BooksRepository {

	private static Random random = new Random();

	private static BooksRepository THIS;

	private ArrayList<Book> books = new ArrayList<>();

	private static BooksRepository get() {
		if (THIS == null) {
			THIS = new BooksRepository();
		}
		return THIS;
	}
	
	private BooksRepository()  {
		List<String> lines;
		try {
			lines = Files
					.readAllLines(Paths.get(BooksRepository.class.getClassLoader().getResource("books.csv").toURI()));
			int pos = 0;
			for (String line : lines) {
				String[] cols = line.split(",");
				Book book = Book.builder().id(pos++).title(cols[0]).author(cols[1]).language(cols[2]).year(cols[3]).build();
				books.add(book);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	
	public static Book randomBook () {
		BooksRepository THIS = get();
		return THIS.books.get(random.nextInt(THIS.books.size()));
	}

	public static Book get (int id) {
		return get().books.get(id);
	}

	public static Optional<Book> get (String title) {
		return get().books.stream().filter(b -> b.getTitle().equals(title)).findFirst();
	}

	
	public static List<String> getAllTitles () {
		return get().books.stream().map(Book::getTitle).collect(Collectors.toList());
	}

	
}
