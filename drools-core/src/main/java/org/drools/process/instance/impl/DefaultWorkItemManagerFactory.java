package org.drools.process.instance.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.process.instance.InternalWorkItemManager;
import org.drools.process.instance.WorkItemManagerFactory;
import org.drools.WorkingMemory;

public class DefaultWorkItemManagerFactory implements WorkItemManagerFactory, Externalizable {

	public InternalWorkItemManager createWorkItemManager(WorkingMemory workingMemory) {
		return new DefaultWorkItemManager(workingMemory);
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
	}

	public void writeExternal(ObjectOutput out) throws IOException {
	}

}
