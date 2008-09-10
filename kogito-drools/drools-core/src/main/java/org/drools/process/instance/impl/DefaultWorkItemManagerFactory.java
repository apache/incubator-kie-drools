package org.drools.process.instance.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.WorkingMemory;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.WorkItemManagerFactory;

public class DefaultWorkItemManagerFactory implements WorkItemManagerFactory, Externalizable {

	public WorkItemManager createWorkItemManager(WorkingMemory workingMemory) {
		return new DefaultWorkItemManager(workingMemory);
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
	}

	public void writeExternal(ObjectOutput out) throws IOException {
	}

}
