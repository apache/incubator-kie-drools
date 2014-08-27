package org.jbpm.process.workitem.camel.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class S3URIMapper extends URIMapper {
	//aws-s3://bucket-name[?options]
	
	public S3URIMapper() {
		super("aws-s3");
	}
	
	@Override
	public URI toURI(Map<String, Object> options) throws URISyntaxException {
		String bucketName = (String) options.get("bucketName");
		options.remove("bucketName");

		String path = bucketName;
		return prepareCamelUri(path, options);
	}

	
}
