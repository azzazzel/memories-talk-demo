package io.axoniq.demo.memories.api;

import org.axonframework.serialization.xml.XStreamSerializer;

import com.thoughtworks.xstream.XStream;

public class SecureXStreamSerializer {

	private static XStreamSerializer _instance;
	
	public static XStreamSerializer get() {
		if (_instance == null) {
			_instance = secureXStreamSerializer();
		}
		return _instance;
	}
	
	private static XStreamSerializer secureXStreamSerializer() {
		XStream xStream = new XStream();
		xStream.setClassLoader(SecureXStreamSerializer.class.getClassLoader());
		xStream.allowTypesByWildcard(new String[]{
			"org.axonframework.**",
			"io.axoniq.demo.memories.**"
		});
		return XStreamSerializer.builder().xStream(xStream).build();
	}

}
