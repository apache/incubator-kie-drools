package org.jbpm.process.workitem.camel.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class CXFURIMapper extends URIMapper {
	//cxf://someAddress[?options]
	
	public CXFURIMapper() {
		super("cxf");
	}
	
	@Override
	public URI toURI(Map<String, Object> options) throws URISyntaxException {
		String address = (String) options.get("address");
		options.remove("address");

		String path = address;
		return prepareCamelUri(path, options);
	} 
}
