package org.drools.bpel.instance;

import org.drools.bpel.core.BPELFaultHandler;
import org.drools.process.core.context.exception.ExceptionHandler;
import org.drools.process.instance.context.exception.ExceptionHandlerInstance;
import org.drools.process.instance.context.exception.ExceptionScopeInstance;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.context.WorkflowContextInstance;

public class BPELExceptionScopeInstance extends ExceptionScopeInstance implements WorkflowContextInstance {

    private static final long serialVersionUID = 400L;
    
    private NodeInstanceContainer nodeInstanceContainer;

    protected ExceptionHandlerInstance getExceptionHandlerInstance(ExceptionHandler exceptionHandler) {
        BPELExceptionHandlerInstance handlerInstance = new BPELExceptionHandlerInstance();
        handlerInstance.setFaultHandler((BPELFaultHandler) exceptionHandler);
        handlerInstance.setNodeInstanceContainer(getNodeInstanceContainer());
        return handlerInstance;
    }

    public NodeInstanceContainer getNodeInstanceContainer() {
        return nodeInstanceContainer;
    }

    public void setNodeInstanceContainer(NodeInstanceContainer nodeInstanceContainer) {
        this.nodeInstanceContainer = nodeInstanceContainer;
    }

}
