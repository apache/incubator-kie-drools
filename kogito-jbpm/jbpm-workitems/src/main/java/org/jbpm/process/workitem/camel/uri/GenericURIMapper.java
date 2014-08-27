package org.jbpm.process.workitem.camel.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class GenericURIMapper extends URIMapper {

	private final String pathLocation;
	
	public GenericURIMapper(String schema) {
		super(schema);
		this.pathLocation = "path";
	}
	
	public GenericURIMapper(String schema, String pathLocation) {
		super(schema);
		this.pathLocation = pathLocation;
	}
	
	@Override
	public URI toURI(Map<String, Object> options) throws URISyntaxException {
		String path = (String) options.remove(pathLocation);
		return prepareCamelUri(path, options);
	}
	
}
