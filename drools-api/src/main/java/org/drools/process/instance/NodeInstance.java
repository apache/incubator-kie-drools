package org.drools.process.instance;

public interface NodeInstance {

    long getId();

    long getNodeId();

    WorkflowProcessInstance getProcessInstance();

    NodeInstanceContainer getNodeInstanceContainer();
    
}
