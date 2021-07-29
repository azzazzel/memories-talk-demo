package io.axoniq.demo.memories.api;

import java.io.IOException;
import java.util.Optional;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PeopleRepository {

	private static Random random = new Random();

	private List<Person> people = new ArrayList<>();

	private static PeopleRepository _instance;
	
	public static PeopleRepository get() {
		if (_instance == null) {
			_instance = new PeopleRepository();
		}
		return _instance;
	}
	
	private PeopleRepository() {
		List<String> lines;
		try {
			lines = Files
					.readAllLines(Paths.get(PeopleRepository.class.getClassLoader().getResource("names.txt").toURI()));
			
			int pos = 0;
			for (String line : lines) {
				Person person = Person.builder().id(pos++).name(line).build();
				people.add(person);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Person randomPerson() {
		return people.get(random.nextInt(people.size()));
	}


	public Person get(int id) {
		return people.get(id);
	}

	public Optional<Person> get(String name) {
		return people.stream().filter(p -> p.getName().equals(name)).findFirst();
	}

}
