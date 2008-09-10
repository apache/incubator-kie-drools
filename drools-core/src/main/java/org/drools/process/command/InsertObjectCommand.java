package org.drools.process.command;

import java.util.ArrayList;
import java.util.List;

import org.drools.WorkingMemory;

public class InsertObjectCommand implements Command {
	
	private List<Object> objects;
	
	public InsertObjectCommand(Object object) {
		objects = new ArrayList<Object>();
		objects.add(object);
	}
	
	public InsertObjectCommand(List<Object> objects) {
		this.objects = objects;
	}
	
	public Object execute(WorkingMemory workingMemory) {
		for (Object object: objects) {
			workingMemory.insert(object);
		}
		return null;
	}

}
