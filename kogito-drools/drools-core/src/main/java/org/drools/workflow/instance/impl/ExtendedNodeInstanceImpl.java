package org.drools.workflow.instance.impl;

import java.util.List;

import org.drools.WorkingMemory;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.base.SequentialKnowledgeHelper;
import org.drools.common.InternalRuleBase;
import org.drools.process.core.context.exception.ExceptionScope;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.context.exception.ExceptionScopeInstance;
import org.drools.runtime.process.NodeInstance;
import org.drools.spi.Action;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.ProcessContext;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.impl.ExtendedNodeImpl;

public abstract class ExtendedNodeInstanceImpl extends NodeInstanceImpl {

	private static final long serialVersionUID = 4L;
	
	public ExtendedNodeImpl getExtendedNode() {
		return (ExtendedNodeImpl) getNode();
	}
	
	public void internalTrigger(NodeInstance from, String type) {
		triggerEvent(ExtendedNodeImpl.EVENT_NODE_ENTER);
	}
	
    public void triggerCompleted(boolean remove) {
        triggerCompleted(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE, remove);
    }
    
	protected void triggerCompleted(String type, boolean remove) {
		triggerEvent(ExtendedNodeImpl.EVENT_NODE_EXIT);
		super.triggerCompleted(type, remove);
	}
	
	protected void triggerEvent(String type) {
		ExtendedNodeImpl extendedNode = getExtendedNode();
		if (extendedNode == null) {
			return;
		}
		List<DroolsAction> actions = extendedNode.getActions(type);
		if (actions != null) {
			KnowledgeHelper knowledgeHelper = createKnowledgeHelper();
			for (DroolsAction droolsAction: actions) {
				executeAction(droolsAction, knowledgeHelper);
			}
		}
	}
	
	protected KnowledgeHelper createKnowledgeHelper() {
		KnowledgeHelper knowledgeHelper = null;
		WorkingMemory workingMemory = ((ProcessInstance) getProcessInstance()).getWorkingMemory();
		if (((InternalRuleBase) workingMemory.getRuleBase()).getConfiguration().isSequential()) {
			knowledgeHelper = new SequentialKnowledgeHelper(workingMemory);
        } else {
        	knowledgeHelper = new DefaultKnowledgeHelper(workingMemory);
        }
		return knowledgeHelper;
	}
	
	protected void executeAction(DroolsAction droolsAction, KnowledgeHelper knowledgeHelper) {
		Action action = (Action) droolsAction.getMetaData("Action");
		ProcessContext context = new ProcessContext();
		context.setNodeInstance(this);
		try {
			action.execute(knowledgeHelper, ((ProcessInstance) getProcessInstance()).getWorkingMemory(), context);
		} catch (Exception exception) {
			exception.printStackTrace();
			String exceptionName = exception.getClass().getName();
			ExceptionScopeInstance exceptionScopeInstance = (ExceptionScopeInstance)
				resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, exceptionName);
			if (exceptionScopeInstance == null) {
				exception.printStackTrace();
				throw new IllegalArgumentException(
					"Could not find exception handler for " + exceptionName + " while executing node " + getNodeId());
			}
			exceptionScopeInstance.handleException(exceptionName, exception);
		}
	}
	
}
