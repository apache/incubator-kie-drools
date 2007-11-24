package org.drools.ruleflow.instance.impl;

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

import org.drools.common.RuleFlowGroupNode;
import org.drools.ruleflow.core.MilestoneNode;
import org.drools.ruleflow.instance.RuleFlowNodeInstance;
import org.drools.spi.Activation;
import org.drools.spi.RuleFlowGroup;

/**
 * Runtime counterpart of a milestone node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class MilestoneNodeInstanceImpl extends RuleFlowNodeInstanceImpl {

    protected MilestoneNode getMilestoneNode() {
        return (MilestoneNode) getNode();
    }

    public void internalTrigger(final RuleFlowNodeInstance from) {
    	RuleFlowGroup systemRuleFlowGroup = getProcessInstance().getAgenda().getRuleFlowGroup("DROOLS_SYSTEM");
    	String rule = "RuleFlow-Milestone-" + getProcessInstance().getProcess().getId()
    		+ "-" + getNode().getId();
    	for (Iterator activations = systemRuleFlowGroup.iterator(); activations.hasNext(); ) {
    		Activation activation = ((RuleFlowGroupNode) activations.next()).getActivation();
    		if (rule.equals(activation.getRule().getName())) {
    			triggerCompleted();
        		break;
    		}
    	}
    }

    public void triggerCompleted() {
        getProcessInstance().getNodeInstance( getMilestoneNode().getTo().getTo() ).trigger( this );
        getProcessInstance().removeNodeInstance(this);
    }

}