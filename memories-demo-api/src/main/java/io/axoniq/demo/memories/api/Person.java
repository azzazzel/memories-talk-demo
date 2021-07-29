package io.axoniq.demo.memories.api;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Person {

	private int id;
	private String name;


}
