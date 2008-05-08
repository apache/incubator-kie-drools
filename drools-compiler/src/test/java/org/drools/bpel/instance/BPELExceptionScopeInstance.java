package org.drools.bpel.instance;

import org.drools.bpel.core.BPELFaultHandler;
import org.drools.process.core.context.exception.ExceptionHandler;
import org.drools.process.instance.context.exception.ExceptionScopeInstance;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.context.WorkflowContextInstance;

public class BPELExceptionScopeInstance extends ExceptionScopeInstance implements WorkflowContextInstance {

    private static final long serialVersionUID = 400L;
    
    private NodeInstanceContainer nodeInstanceContainer;

    public NodeInstanceContainer getNodeInstanceContainer() {
        return nodeInstanceContainer;
    }

    public void setNodeInstanceContainer(NodeInstanceContainer nodeInstanceContainer) {
        this.nodeInstanceContainer = nodeInstanceContainer;
    }

	public void handleException(ExceptionHandler handler, String exception, Object params) {
		if (handler instanceof BPELFaultHandler) {
			BPELExceptionHandlerInstance handlerInstance = new BPELExceptionHandlerInstance();
	        handlerInstance.setExceptionHandler(handler);
	        handlerInstance.setNodeInstanceContainer(nodeInstanceContainer);
			handlerInstance.handleException(exception, params);
		} else {
			throw new IllegalArgumentException(
				"A BPEL Exception scope can only handle BPELFaultHandlers: " + handler);
		}
	}

}
