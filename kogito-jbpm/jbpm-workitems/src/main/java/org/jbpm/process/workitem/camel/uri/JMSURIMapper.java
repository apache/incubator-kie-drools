package org.jbpm.process.workitem.camel.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class JMSURIMapper extends URIMapper {

	//jms:[queue:|topic:]destinationName[?options]
	public JMSURIMapper() {
		super("jms");
	}

	@Override
	public URI toURI(Map<String, Object> options) throws URISyntaxException {
		String queue = (String) options.get("queue");
		String topic = (String) options.get("topic");
		String destinationName = (String) options.get("destinationName");
		
		options.remove("queue");
		options.remove("topic");
		options.remove("destinationName");
		
		String path = queue == null ? "" : "queue:";
		path += (topic == null ? "" : "topic:");
		path += destinationName;
		
		return prepareCamelUri(path, options);
	}

}
