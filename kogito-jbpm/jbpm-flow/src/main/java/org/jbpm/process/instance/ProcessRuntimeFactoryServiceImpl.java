package org.jbpm.process.instance;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.runtime.process.ProcessRuntimeFactoryService;

public class ProcessRuntimeFactoryServiceImpl implements ProcessRuntimeFactoryService {

	public InternalProcessRuntime newProcessRuntime(InternalWorkingMemory workingMemory) {
		return new ProcessRuntimeImpl(workingMemory);
	}
	
}
