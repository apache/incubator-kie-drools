package org.drools.workflow.instance.impl;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.Serializable;

import org.drools.common.EventSupport;
import org.drools.common.InternalWorkingMemory;
import org.drools.process.core.Context;
import org.drools.process.instance.ContextInstance;
import org.drools.process.instance.ContextInstanceContainer;
import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.WorkflowProcessInstance;

/**
 * Default implementation of a RuleFlow node instance.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public abstract class NodeInstanceImpl implements NodeInstance, Serializable {

    private long id;
    private long nodeId;
    private WorkflowProcessInstance processInstance;
    private NodeInstanceContainer nodeInstanceContainer;

    public void setId(final long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setNodeId(final long nodeId) {
        this.nodeId = nodeId;
    }

    public long getNodeId() {
        return this.nodeId;
    }

    public void setProcessInstance(final WorkflowProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    public WorkflowProcessInstance getProcessInstance() {
        return this.processInstance;
    }

    public NodeInstanceContainer getNodeInstanceContainer() {
        return this.nodeInstanceContainer;
    }
    
    public void setNodeInstanceContainer(NodeInstanceContainer nodeInstanceContainer) {
        this.nodeInstanceContainer = nodeInstanceContainer;
        if (nodeInstanceContainer != null) {
            nodeInstanceContainer.addNodeInstance(this);
        }
    }

    public Node getNode() {
        return this.nodeInstanceContainer.getNodeContainer().getNode( this.nodeId );
    }
    
    public void cancel() {
        nodeInstanceContainer.removeNodeInstance(this);
    }
    
    public final void trigger(NodeInstance from, String type) {
        ((EventSupport) getProcessInstance().getWorkingMemory()).getRuleFlowEventSupport().fireBeforeRuleFlowNodeTriggered(this, (InternalWorkingMemory) getProcessInstance().getWorkingMemory());
        internalTrigger(from, type);
        ((EventSupport) getProcessInstance().getWorkingMemory()).getRuleFlowEventSupport().fireAfterRuleFlowNodeTriggered(this, (InternalWorkingMemory) getProcessInstance().getWorkingMemory());
    }
    
    public abstract void internalTrigger(NodeInstance from, String type);
    
    protected void triggerCompleted(String type, boolean remove) {
        if (remove) {
            getNodeInstanceContainer().removeNodeInstance(this);
        }
        for (Connection connection: getNode().getOutgoingConnections(type)) {
            triggerConnection(connection);
        }
    }
    
    protected void triggerConnection(Connection connection) {
        getNodeInstanceContainer().getNodeInstance(connection.getTo())
            .trigger(this, connection.getToType());
    }
    
    public Context resolveContext(String contextId, Object param) {
        return ((NodeImpl) getNode()).resolveContext(contextId, param);
    }
    
    public ContextInstance resolveContextInstance(String contextId, Object param) {
        Context context = resolveContext(contextId, param);
        if (context == null) {
            return null;
        }
        // TODO: find right context instance container and get context instance
        // TODO: currently, only the process instance acts as a context instance container
        ContextInstanceContainer contextInstanceContainer = getProcessInstance();
        return contextInstanceContainer.getContextInstance(context);
    }
    
}
