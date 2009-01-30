package org.drools.workflow.instance;

import org.drools.process.instance.ProcessInstance;
import org.drools.runtime.process.EventListener;

public interface WorkflowProcessInstance extends ProcessInstance, org.drools.runtime.process.WorkflowProcessInstance {

    void addEventListener(String type, EventListener eventListener, boolean external);
    
    void removeEventListener(String type, EventListener eventListener, boolean external);
    
}
