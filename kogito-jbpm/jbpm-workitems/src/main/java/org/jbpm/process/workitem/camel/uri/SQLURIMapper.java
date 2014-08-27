package org.jbpm.process.workitem.camel.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class SQLURIMapper extends URIMapper {
	//sql:select * from table where id=# order by name[?options]
	
	public SQLURIMapper() {
		super("sql");
	}
	
	@Override
	public URI toURI(Map<String, Object> options) throws URISyntaxException {
		String query = (String) options.get("query");
		options.remove("query");

		String path = query;
		return prepareCamelUri(path, options);
	}

	
}
