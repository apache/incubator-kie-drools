package org.drools.process.instance;

public interface NodeInstance {

    long getId();

    long getNodeId();
    
    String getNodeName();

    WorkflowProcessInstance getProcessInstance();

    NodeInstanceContainer getNodeInstanceContainer();
    
}
