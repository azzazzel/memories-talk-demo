package io.axoniq.demo.memories.api;


import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Book {

	public int id;
	public String title;
	public String author;
	public String language;
	public String year;
	
}
