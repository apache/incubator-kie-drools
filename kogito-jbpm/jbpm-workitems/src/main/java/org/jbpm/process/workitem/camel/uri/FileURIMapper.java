package org.jbpm.process.workitem.camel.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class FileURIMapper extends URIMapper {

	public FileURIMapper() {
		super("file");
	}

	@Override
	public URI toURI(Map<String, Object> options) throws URISyntaxException {
		String path = (String) options.get("path");
		options.remove("path");
		
		return prepareCamelUri(path, options);
	}

}
