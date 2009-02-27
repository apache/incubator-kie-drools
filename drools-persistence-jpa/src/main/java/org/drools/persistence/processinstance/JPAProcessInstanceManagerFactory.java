package org.drools.persistence.processinstance;

import org.drools.WorkingMemory;
import org.drools.process.instance.ProcessInstanceManager;
import org.drools.process.instance.ProcessInstanceManagerFactory;

public class JPAProcessInstanceManagerFactory implements ProcessInstanceManagerFactory {

	public ProcessInstanceManager createProcessInstanceManager(WorkingMemory workingMemory) {
		JPAProcessInstanceManager result = new JPAProcessInstanceManager();
		result.setWorkingMemory(workingMemory);
		return result;
	}

}
