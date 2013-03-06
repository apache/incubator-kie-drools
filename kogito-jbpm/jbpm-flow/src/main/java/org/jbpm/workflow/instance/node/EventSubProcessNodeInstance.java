/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.workflow.instance.node;

import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.kie.definition.process.NodeContainer;
import org.kie.runtime.process.NodeInstance;

public class EventSubProcessNodeInstance extends CompositeContextNodeInstance {


    private static final long serialVersionUID = 7095736653568661510L;

    protected EventSubProcessNode getCompositeNode() {
        return (EventSubProcessNode) getNode();
    }
    
    public NodeContainer getNodeContainer() {
        return getCompositeNode();
    }
    
    @Override
    protected String getActivationType() {
       return "RuleFlowStateEventSubProcess-" + getProcessInstance().getProcessId() + "-" + getCompositeNode().getUniqueId();
    }

    @Override
    public void internalTrigger(NodeInstance from, String type) {
        super.internalTriggerOnlyParent(from, type);
    }

    @Override
    public void signalEvent(String type, Object event) {
        if (getProcessInstance().getNodeInstances().contains(this) || type.startsWith("Error-")) {
            StartNode startNode = getCompositeNode().findStartNode();
            NodeInstance nodeInstance = getNodeInstance(startNode);
            ((StartNodeInstance) nodeInstance).signalEvent(type, event);
        }
    }

    @Override
    public void nodeInstanceCompleted(org.jbpm.workflow.instance.NodeInstance nodeInstance, String outType) {
        if (nodeInstance instanceof EndNodeInstance) { 
            if (getCompositeNode().isKeepActive()) {
                StartNode startNode = getCompositeNode().findStartNode();
                triggerCompleted(true);
                if (startNode.isInterrupting()) {
                    ((NodeInstanceContainer) getNodeInstanceContainer()).setState( ProcessInstance.STATE_ABORTED);
                }                
            }            
        } else {
            throw new IllegalArgumentException(
                "Completing a node instance that has no outgoing connection not supported.");
        }
    }
    
    
}
