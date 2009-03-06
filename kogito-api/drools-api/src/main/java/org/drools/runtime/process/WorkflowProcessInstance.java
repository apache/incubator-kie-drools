package org.drools.runtime.process;

public interface WorkflowProcessInstance
    extends
    ProcessInstance,
    NodeInstanceContainer {
	
	Object getVariable(String name);

}