package org.jbpm;

import org.drools.runtime.process.ProcessRuntimeFactory;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;

import junit.framework.TestCase;

public abstract class JbpmTestCase extends TestCase {
	
	static {
		ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
	}

}
