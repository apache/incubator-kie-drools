package org.jbpm.process.workitem.camel.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class FTPURIMapper extends URIMapper {

	public FTPURIMapper(String schema) {
		super(schema);
	}

	@Override
	public URI toURI(Map<String, Object> params) throws URISyntaxException {
		String hostname = (String) params.remove("hostname");
		String username = (String) params.remove("username");
		String port = (String) params.remove("port");
		String directoryname = (String) params.remove("directoryname");
		
		String path = (username == null ? "" : username +"@")
			+ (hostname == null ? "" : hostname)
		    + (port == null ? "" : ":"+port)
		    + (directoryname == null ? "" : "/" + directoryname);

		return prepareCamelUri(path, params);
	}

}
