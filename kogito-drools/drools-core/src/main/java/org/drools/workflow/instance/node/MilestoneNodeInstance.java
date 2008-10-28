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
import org.drools.common.InternalAgenda;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.workflow.instance.NodeInstance;

/**
 * Runtime counterpart of a milestone node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class MilestoneNodeInstance extends EventBasedNodeInstance implements AgendaEventListener {

    private static final long serialVersionUID = 400L;

    protected MilestoneNode getMilestoneNode() {
        return (MilestoneNode) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
    	super.internalTrigger(from, type);
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A MilestoneNode only accepts default incoming connections!");
        }
        String rule = "RuleFlow-Milestone-" + getProcessInstance().getProcess().getId()
        + "-" + getNode().getId();

        if( ((InternalAgenda)getProcessInstance().getAgenda()).isRuleActiveInRuleFlowGroup( "DROOLS_SYSTEM", rule ) ) {
            triggerCompleted();
        } else {
            addActivationListener();
        }
    }
    
    public void addEventListeners() {
        super.addEventListeners();
        addActivationListener();
    }
    
    private void addActivationListener() {
        getProcessInstance().getWorkingMemory().addEventListener(this);
    }

    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().getWorkingMemory().removeEventListener(this);
    }

    public void activationCancelled(ActivationCancelledEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void activationCreated(ActivationCreatedEvent event,
            WorkingMemory workingMemory) {
        // check whether this activation is from the DROOLS_SYSTEM agenda group
        String ruleFlowGroup = event.getActivation().getRule().getRuleFlowGroup();
        if ("DROOLS_SYSTEM".equals(ruleFlowGroup)) {
            // new activations of the rule associate with a milestone node
            // trigger node instances of that milestone node
            String ruleName = event.getActivation().getRule().getName();
            String milestoneName = "RuleFlow-Milestone-" + getProcessInstance().getProcess().getId() + "-" + getNodeId();
            if (milestoneName.equals(ruleName)) {
            	synchronized(getProcessInstance()) {
	                removeEventListeners();
	                triggerCompleted();
            	}
            }
        }
    }

    public void afterActivationFired(AfterActivationFiredEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void beforeActivationFired(BeforeActivationFiredEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

}