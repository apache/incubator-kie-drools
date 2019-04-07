/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.instance.node;

import org.drools.core.spi.ProcessContext;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.kie.api.runtime.process.NodeInstance;

/**
 * Runtime counterpart of an action node.
 * 
 */
public class ActionNodeInstance extends NodeInstanceImpl {

    private static final long serialVersionUID = 510l;

    protected ActionNode getActionNode() {
        return (ActionNode) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "An ActionNode only accepts default incoming connections!");
        }
		Action action = (Action) getActionNode().getAction().getMetaData("Action");
		try {
		    ProcessContext context = new ProcessContext(getProcessInstance().getKnowledgeRuntime());
		    context.setNodeInstance(this);
	        executeAction(action);
		} catch( WorkflowRuntimeException wre) { 
		    throw wre;
		} catch (Exception e) {
		    // for the case that one of the following throws an exception
		    // - the ProcessContext() constructor 
		    // - or context.setNodeInstance(this) 
		    throw new WorkflowRuntimeException(this, getProcessInstance(), "Unable to execute Action: " + e.getMessage(), e);
		} 
    	triggerCompleted();
    }

    public void triggerCompleted() {
        triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
    }


}
