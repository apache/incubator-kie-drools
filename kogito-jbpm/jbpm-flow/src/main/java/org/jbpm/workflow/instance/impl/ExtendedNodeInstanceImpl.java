/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.instance.impl;

import java.util.List;

import org.kie.runtime.process.NodeInstance;
import org.drools.spi.ProcessContext;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;

public abstract class ExtendedNodeInstanceImpl extends NodeInstanceImpl {

	private static final long serialVersionUID = 510l;
	
	public ExtendedNodeImpl getExtendedNode() {
		return (ExtendedNodeImpl) getNode();
	}
	
	public void internalTrigger(NodeInstance from, String type) {
		triggerEvent(ExtendedNodeImpl.EVENT_NODE_ENTER);
	}
	
    public void triggerCompleted(boolean remove) {
        triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, remove);
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
			for (DroolsAction droolsAction: actions) {
				executeAction(droolsAction);
			}
		}
	}

	protected void executeAction(DroolsAction droolsAction) {
		Action action = (Action) droolsAction.getMetaData("Action");
		ProcessContext context = new ProcessContext(getProcessInstance().getKnowledgeRuntime());
		context.setNodeInstance(this);
		try {
			action.execute(context);
		} catch (Exception e) {
			String exceptionName = e.getClass().getName();
			ExceptionScopeInstance exceptionScopeInstance = (ExceptionScopeInstance)
				resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, exceptionName);
			if (exceptionScopeInstance == null) {
				throw new RuntimeException("unable to execute Action: " + e.getMessage(), e);
			}
			exceptionScopeInstance.handleException(exceptionName, e);
		}
	}
	
}
