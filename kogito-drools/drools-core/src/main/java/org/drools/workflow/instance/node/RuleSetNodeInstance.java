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

import org.drools.common.InternalRuleFlowGroup;
import org.drools.common.RuleFlowGroupListener;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.RuleSetNode;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * Runtime counterpart of a ruleset node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleSetNodeInstance extends NodeInstanceImpl implements RuleFlowGroupListener {

    private static final long serialVersionUID = 400L;
    
    private InternalRuleFlowGroup ruleFlowGroup;

    public RuleSetNodeInstance() {
    }
    
    protected RuleSetNode getRuleSetNode() {
        return (RuleSetNode) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
        ruleFlowGroup = (InternalRuleFlowGroup)
        getProcessInstance().getWorkingMemory().getAgenda()
            .getRuleFlowGroup(getRuleSetNode().getRuleFlowGroup());
        ruleFlowGroup.addRuleFlowGroupListener(this);
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A RuleSetNode only accepts default incoming connections!");
        }
        getProcessInstance().getAgenda().activateRuleFlowGroup( getRuleSetNode().getRuleFlowGroup() );
    }

    public void triggerCompleted() {
        getNodeInstanceContainer().removeNodeInstance(this);
        getNodeInstanceContainer().getNodeInstance( getRuleSetNode().getTo().getTo() ).trigger( this, getRuleSetNode().getTo().getToType() );
    }
    
    public void cancel() {
    	getProcessInstance().getAgenda().deactivateRuleFlowGroup( getRuleSetNode().getRuleFlowGroup() );
    	super.cancel();
    }

    public void ruleFlowGroupDeactivated() {
        ruleFlowGroup.removeRuleFlowGroupListener(this);
        triggerCompleted();
    }

}