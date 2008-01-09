package org.drools.workflow.instance.node;

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

import org.drools.WorkingMemory;
import org.drools.event.RuleFlowCompletedEvent;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.RuleFlowGroupActivatedEvent;
import org.drools.event.RuleFlowGroupDeactivatedEvent;
import org.drools.event.RuleFlowNodeTriggeredEvent;
import org.drools.event.RuleFlowStartedEvent;
import org.drools.process.instance.ProcessInstance;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.SubProcessNode;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * Runtime counterpart of a SubFlow node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class SubProcessNodeInstance extends NodeInstanceImpl implements RuleFlowEventListener {

    private static final long serialVersionUID = 400L;
    
    private long processInstanceId;
	
    protected SubProcessNode getSubFlowNode() {
        return (SubProcessNode) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A SubProcess node only accepts default incoming connections!");
        }
    	ProcessInstance processInstance = 
    		getProcessInstance().getWorkingMemory().startProcess(getSubFlowNode().getProcessId());
    	if (!getSubFlowNode().isWaitForCompletion()
    	        || processInstance.getState() == ProcessInstance.STATE_COMPLETED) {
    		triggerCompleted();
    	} else {
    	    getProcessInstance().getWorkingMemory().addEventListener(this);
    		this.processInstanceId = processInstance.getId();
    	}
    }
    
    public long getProcessInstanceId() {
    	return processInstanceId;
    }

    public void triggerCompleted() {
        getNodeInstanceContainer().removeNodeInstance(this);
        getNodeInstanceContainer().getNodeInstance(getSubFlowNode().getTo().getTo())
            .trigger(this, getSubFlowNode().getTo().getToType());
    }

    public void afterRuleFlowCompleted(RuleFlowCompletedEvent event,
            WorkingMemory workingMemory) {
        if ( event.getRuleFlowProcessInstance().getId() == processInstanceId ) {
            getProcessInstance().getWorkingMemory().removeEventListener(this);
            triggerCompleted();
        }
    }

    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void afterRuleFlowGroupDeactivated(
            RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
        // Do nothing
    }

    public void afterRuleFlowNodeTriggered(RuleFlowNodeTriggeredEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void afterRuleFlowStarted(RuleFlowStartedEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void beforeRuleFlowCompleted(RuleFlowCompletedEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void beforeRuleFlowGroupDeactivated(
            RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
        // Do nothing
    }

    public void beforeRuleFlowNodeTriggered(RuleFlowNodeTriggeredEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void beforeRuleFlowStarted(RuleFlowStartedEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

}