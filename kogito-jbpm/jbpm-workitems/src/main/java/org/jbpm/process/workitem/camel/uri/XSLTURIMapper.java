package org.jbpm.process.workitem.camel.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class XSLTURIMapper extends URIMapper {
	//xslt:templateName[?options]
	
	public XSLTURIMapper() {
		super("xslt");
	}
	
	@Override
	public URI toURI(Map<String, Object> options) throws URISyntaxException {
		String address = (String) options.get("templateName");
		options.remove("templateName");

		String path = address;
		return prepareCamelUri(path, options);
	}

	
}
