package org.drools.workflow.instance.impl;

import java.util.List;

import org.drools.WorkingMemory;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.base.SequentialKnowledgeHelper;
import org.drools.common.InternalRuleBase;
import org.drools.process.core.context.exception.ExceptionScope;
import org.drools.process.instance.context.exception.ExceptionScopeInstance;
import org.drools.spi.Action;
import org.drools.spi.KnowledgeHelper;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.impl.ExtendedNodeImpl;
import org.drools.workflow.instance.NodeInstance;

public abstract class ExtendedNodeInstanceImpl extends NodeInstanceImpl {

	public ExtendedNodeImpl getExtendedNode() {
		return (ExtendedNodeImpl) getNode();
	}
	
	public void internalTrigger(NodeInstance from, String type) {
		triggerEvent(ExtendedNodeImpl.EVENT_NODE_ENTER);
	}
	
	protected void triggerCompleted(String type, boolean remove) {
		triggerEvent(ExtendedNodeImpl.EVENT_NODE_EXIT);
		super.triggerCompleted(type, remove);
	}
	
	protected void triggerEvent(String type) {
		List<DroolsAction> actions = getExtendedNode().getActions(type);
		if (actions != null) {
			WorkingMemory workingMemory = getProcessInstance().getWorkingMemory();
			KnowledgeHelper knowledgeHelper = null;
			if (((InternalRuleBase) workingMemory.getRuleBase()).getConfiguration().isSequential()) {
				knowledgeHelper = new SequentialKnowledgeHelper(workingMemory);
	        } else {
	        	knowledgeHelper = new DefaultKnowledgeHelper(workingMemory);
	        }
			for (DroolsAction droolsAction: actions) {
				Action action = (Action) droolsAction.getMetaData("Action");
				try {
					action.execute(knowledgeHelper, workingMemory);
				} catch (Exception exception) {
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
	}
	
}
