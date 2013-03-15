package org.jbpm.process.instance;

import org.drools.core.common.AbstractWorkingMemory;
import org.drools.runtime.process.ProcessRuntimeFactoryService;

public class ProcessRuntimeFactoryServiceImpl implements ProcessRuntimeFactoryService {

	public InternalProcessRuntime newProcessRuntime(
			AbstractWorkingMemory workingMemory) {
		return new ProcessRuntimeImpl(workingMemory);
	}
	
}
