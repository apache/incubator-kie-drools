package org.drools.process.instance.context.exception;

import org.drools.WorkingMemory;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.base.SequentialKnowledgeHelper;
import org.drools.common.InternalRuleBase;
import org.drools.process.core.context.exception.ActionExceptionHandler;
import org.drools.process.core.context.exception.ExceptionHandler;
import org.drools.process.instance.ContextInstanceContainer;
import org.drools.process.instance.InternalProcessInstance;
import org.drools.process.instance.ProcessInstance;
import org.drools.spi.Action;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.ProcessContext;
import org.drools.workflow.instance.NodeInstance;

public class DefaultExceptionScopeInstance extends ExceptionScopeInstance {

	private static final long serialVersionUID = 4L;

	public void handleException(ExceptionHandler handler, String exception, Object params) {
		
		if (handler instanceof ActionExceptionHandler) {
			Action action = (Action) ((ActionExceptionHandler) handler).getAction().getMetaData("Action");
			try {
			    KnowledgeHelper knowledgeHelper = createKnowledgeHelper();
			    ProcessContext context = new ProcessContext();
		    	ProcessInstance processInstance = getProcessInstance();
			    ContextInstanceContainer contextInstanceContainer = getContextInstanceContainer();
			    if (contextInstanceContainer instanceof NodeInstance) {
			    	context.setNodeInstance((NodeInstance) contextInstanceContainer);
			    } else {
			    	context.setProcessInstance(processInstance);
			    }
			    String faultVariable = handler.getFaultVariable();
			    if (faultVariable != null) {
			    	context.setVariable(faultVariable, params);
			    }
		        action.execute(knowledgeHelper, ((InternalProcessInstance) processInstance).getWorkingMemory(), context);
			} catch (Exception e) {
			    throw new RuntimeException("unable to execute Action", e);
			}
		} else {
			throw new IllegalArgumentException("Unknown exception handler " + handler);
		}
	}

    private KnowledgeHelper createKnowledgeHelper() {
        WorkingMemory workingMemory = ((InternalProcessInstance) getProcessInstance()).getWorkingMemory();
        if ( ((InternalRuleBase) workingMemory.getRuleBase()).getConfiguration().isSequential() ) {
            return new SequentialKnowledgeHelper( workingMemory );
        } else {
            return new DefaultKnowledgeHelper( workingMemory );
        }
    }

}
