package org.drools.server.profile;

import java.util.List;

import javax.xml.bind.JAXBContext;

import org.drools.runtime.CommandExecutor;

public class KnowledgeServiceConfiguration {

	private String id;
	private CommandExecutor session;
	private String sessionId;
	private String marshaller;
	private JAXBContext context;
	private List<String> commands;

	public KnowledgeServiceConfiguration(String id, String sessionId, CommandExecutor session, String marshaller, JAXBContext context, List<String> commands) {
		this.id = id;
		this.sessionId = sessionId;
		this.session = session;
		this.marshaller = marshaller;
		this.context = context;
		this.setCommands(commands);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setSession(CommandExecutor session) {
		this.session = session;
	}

	public CommandExecutor getSession() {
		return session;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setMarshaller(String marshaller) {
		this.marshaller = marshaller;
	}

	public String getMarshaller() {
		return marshaller;
	}

	public void setContext(JAXBContext context) {
		this.context = context;
	}

	public JAXBContext getContext() {
		return context;
	}

	public void setCommands(List<String> commands) {
		this.commands = commands;
	}

	public List<String> getCommands() {
		return commands;
	}

}



