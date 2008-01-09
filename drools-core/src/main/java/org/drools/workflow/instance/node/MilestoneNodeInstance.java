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

import java.util.Iterator;

import org.drools.WorkingMemory;
import org.drools.common.RuleFlowGroupNode;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.spi.Activation;
import org.drools.spi.RuleFlowGroup;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * Runtime counterpart of a milestone node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class MilestoneNodeInstance extends NodeInstanceImpl implements AgendaEventListener {

    private static final long serialVersionUID = 400L;

    protected MilestoneNode getMilestoneNode() {
        return (MilestoneNode) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A MilestoneNode only accepts default incoming connections!");
        }
    	RuleFlowGroup systemRuleFlowGroup = getProcessInstance().getAgenda().getRuleFlowGroup("DROOLS_SYSTEM");
    	String rule = "RuleFlow-Milestone-" + getProcessInstance().getProcess().getId()
    		+ "-" + getNode().getId();
    	for (Iterator activations = systemRuleFlowGroup.iterator(); activations.hasNext(); ) {
    		Activation activation = ((RuleFlowGroupNode) activations.next()).getActivation();
    		if (rule.equals(activation.getRule().getName())) {
    			triggerCompleted();
        		return;
    		}
    	}
    	getProcessInstance().getWorkingMemory().addEventListener(this);
    }

    public void triggerCompleted() {
        getNodeInstanceContainer().removeNodeInstance(this);
        getNodeInstanceContainer().getNodeInstance(getMilestoneNode().getTo().getTo())
            .trigger(this, getMilestoneNode().getTo().getToType());
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
                getProcessInstance().getWorkingMemory().removeEventListener(this);
                triggerCompleted();
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