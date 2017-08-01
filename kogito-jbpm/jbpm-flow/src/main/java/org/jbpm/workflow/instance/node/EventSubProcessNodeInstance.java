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

import java.util.List;
import java.util.stream.Collectors;

import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.runtime.process.NodeInstance;

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
        if (getNodeInstanceContainer().getNodeInstances().contains(this) || type.startsWith("Error-") || type.equals("timerTriggered") ) {
            StartNode startNode = getCompositeNode().findStartNode();
            if (resolveVariables(((EventSubProcessNode) getEventBasedNode()).getEvents()).contains(type) || type.equals("timerTriggered")) {
                NodeInstance nodeInstance = getNodeInstance(startNode);
                ((StartNodeInstance) nodeInstance).signalEvent(type, event);
            }
        }
        super.signalEvent(type, event);
    }
    
    @Override
    public void nodeInstanceCompleted(org.jbpm.workflow.instance.NodeInstance nodeInstance, String outType) {
        if (nodeInstance instanceof EndNodeInstance) { 
            if (getCompositeNode().isKeepActive()) {
                StartNode startNode = getCompositeNode().findStartNode();
                triggerCompleted(true);
                if (startNode.isInterrupting()) {
                	String faultName = getProcessInstance().getOutcome()==null?"":getProcessInstance().getOutcome();
                	
                	if (startNode.getMetaData("FaultCode") != null) {
                		faultName = (String) startNode.getMetaData("FaultCode");
                	}
                	if (getNodeInstanceContainer() instanceof ProcessInstance) {
                		((ProcessInstance) getProcessInstance()).setState(ProcessInstance.STATE_ABORTED, faultName);
                	} else {
                		((NodeInstanceContainer) getNodeInstanceContainer()).setState( ProcessInstance.STATE_ABORTED);
                	}
                    
                }                
            }            
        } else {
            throw new IllegalArgumentException(
                "Completing a node instance that has no outgoing connection not supported.");
        }
    }
    
    protected List<String> resolveVariables(List<String> events) {
        return events.stream().map( event -> resolveVariable(event)).collect(Collectors.toList());
    }
}
