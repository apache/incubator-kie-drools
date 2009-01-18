package org.drools.process.command;

import org.drools.StatefulSession;
import org.drools.runtime.rule.FactHandle;

public class InsertObjectCommand implements Command<FactHandle> {
	
	private Object object;
	
	public InsertObjectCommand(Object object) {
		this.object = object;
	}
	
	public FactHandle execute(StatefulSession session) {
		return session.insert(object);
	}

	public String toString() {
		return "session.insert(" + object + ");";
	}

}
