package org.drools.process.command;

import java.util.Iterator;

import org.drools.StatefulSession;

public class GetObjectsCommand implements Command<Iterator<?>> {

	public GetObjectsCommand() {
	}
	
	public Iterator<?> execute(StatefulSession session) {
		return session.iterateObjects();
	}
	
	public String toString() {
		return "session.iterateObjects();";
	}

}
