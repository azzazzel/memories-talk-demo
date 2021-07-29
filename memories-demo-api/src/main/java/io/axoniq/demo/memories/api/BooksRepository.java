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

	private static BooksRepository _instance;

	private ArrayList<Book> books = new ArrayList<>();

	public static BooksRepository get() {
		if (_instance == null) {
			_instance = new BooksRepository();
		}
		return _instance;
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

	
	public Book randomBook () {
		return books.get(random.nextInt(books.size()));
	}

	public Book get (int id) {
		return books.get(id);
	}

	public Optional<Book> get (String title) {
		return books.stream().filter(b -> b.getTitle().equals(title)).findFirst();
	}

	
	public List<String> getAllTitles () {
		return books.stream().map(Book::getTitle).collect(Collectors.toList());
	}

	
}
