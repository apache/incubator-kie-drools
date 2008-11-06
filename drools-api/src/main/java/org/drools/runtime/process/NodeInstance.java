package org.drools.runtime.process;

public interface NodeInstance {

    long getId();

    long getNodeId();
    
    String getNodeName();

    WorkflowProcessInstance getProcessInstance();

    NodeInstanceContainer getNodeInstanceContainer();
    
}
