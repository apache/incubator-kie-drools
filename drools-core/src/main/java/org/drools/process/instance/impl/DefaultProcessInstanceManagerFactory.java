package org.drools.process.instance.impl;

import org.drools.WorkingMemory;
import org.drools.process.instance.ProcessInstanceManager;
import org.drools.process.instance.ProcessInstanceManagerFactory;

public class DefaultProcessInstanceManagerFactory implements ProcessInstanceManagerFactory {

	public ProcessInstanceManager createProcessInstanceManager(WorkingMemory workingMemory) {
		return new DefaultProcessInstanceManager();
	}

}
