package org.drools.process.command;

import java.util.ArrayList;
import java.util.List;

import org.drools.StatefulSession;

public class InsertObjectCommand implements Command<Object> {
	
	private List<Object> objects;
	
	public InsertObjectCommand(Object object) {
		objects = new ArrayList<Object>();
		objects.add(object);
	}
	
	public InsertObjectCommand(List<Object> objects) {
		this.objects = objects;
	}
	
	public Object execute(StatefulSession session) {
		for (Object object: objects) {
			session.insert(object);
		}
		return null;
	}

	public String toString() {
		String result = "";
		int i = 0;
		for (Object object: objects) {
			if (i++ > 0) {
				result += "\n";
			}
			result += "session.insert(" + object + ");";
		}
		return result;
	}

}
