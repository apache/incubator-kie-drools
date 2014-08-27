package org.jbpm.process.workitem.camel.response;

import java.util.Arrays;
import java.util.HashSet;

public class FTPResponsPayloadMapper extends ResponsePayloadMapper {
	
	private static final String[] MESSAGE_HEADERS = new String[]{
		"CamelFtpReplyCode",
		"CamelFtpReplyString"
	};

	public FTPResponsPayloadMapper(String requestLocation) {
		super(requestLocation, new HashSet<String>(Arrays.asList(MESSAGE_HEADERS)));
	}
}
