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

import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.workflow.instance.impl.ExtendedNodeInstanceImpl;
import org.kie.api.runtime.process.NodeInstance;

/**
 * Runtime counterpart of an end node.
 * 
 */
public class EndNodeInstance extends ExtendedNodeInstanceImpl {

    private static final long serialVersionUID = 510l;

    public EndNode getEndNode() {
    	return (EndNode) getNode();
    }
    
    public void internalTrigger(final NodeInstance from, String type) {
        super.internalTrigger(from, type);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "An EndNode only accepts default incoming connections!");
        }
        boolean hidden = false;
        if (getNode().getMetaData().get("hidden") != null) {
            hidden = true;
        }
        InternalKnowledgeRuntime kruntime = getProcessInstance().getKnowledgeRuntime();
        if (!hidden) {
            ((InternalProcessRuntime) kruntime.getProcessRuntime())
                .getProcessEventSupport().fireBeforeNodeLeft(this, kruntime);
        }
        ((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
        if (getEndNode().isTerminate()) {
        	if (getNodeInstanceContainer() instanceof CompositeNodeInstance) {
        	    if (getEndNode().getScope() == EndNode.PROCESS_SCOPE) {
                    getProcessInstance().setState( ProcessInstance.STATE_COMPLETED );
                } else {
                	while (!getNodeInstanceContainer().getNodeInstances().isEmpty()) {
                		((org.jbpm.workflow.instance.NodeInstance) getNodeInstanceContainer().getNodeInstances().iterator().next()).cancel();
                	}
                    ((NodeInstanceContainer) getNodeInstanceContainer()).nodeInstanceCompleted(this, null);
                }
        	} else {
        	    ((NodeInstanceContainer) getNodeInstanceContainer()).setState( ProcessInstance.STATE_COMPLETED );
        	}
        	
        } else {
            ((NodeInstanceContainer) getNodeInstanceContainer())
                .nodeInstanceCompleted(this, null);
        }
        if (!hidden) {
            ((InternalProcessRuntime) kruntime.getProcessRuntime())
                .getProcessEventSupport().fireAfterNodeLeft(this, kruntime);
        }
    }

}
