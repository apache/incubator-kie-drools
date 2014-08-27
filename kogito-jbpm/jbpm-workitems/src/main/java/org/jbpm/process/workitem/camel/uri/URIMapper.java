package org.jbpm.process.workitem.camel.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.camel.util.URISupport;

public abstract class URIMapper {

	protected String schema;
	
	public URIMapper(String schema) {
		this.schema = schema;
	}
	
	public abstract URI toURI(Map<String, Object> params) throws URISyntaxException;
	
	protected URI prepareCamelUri(String path, Map<String, Object> params) throws URISyntaxException {
		return prepareCamelUri(this.schema, path, params);
	}
	
	protected URI prepareCamelUri(String schema, String path, Map<String, Object> params) throws URISyntaxException {
		String url;
		if (schema == null) {
			url = path;
		} else {
			url = schema + "://" + path;
		}
		URI camelUri = new URI(url);
		if (params.isEmpty()) {
			return camelUri;
		} else {
			return URISupport.createURIWithQuery(camelUri, URISupport.createQueryString(params));
		}
	}
	
}
