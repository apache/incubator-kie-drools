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

import org.drools.common.InternalAgenda;
import org.drools.common.RuleFlowGroupListener;
import org.drools.process.instance.ProcessInstance;
import org.drools.runtime.process.NodeInstance;
import org.drools.workflow.core.node.RuleSetNode;

/**
 * Runtime counterpart of a ruleset node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleSetNodeInstance extends StateBasedNodeInstance
    implements
    RuleFlowGroupListener {

    private static final long               serialVersionUID = 400L;

    protected RuleSetNode getRuleSetNode() {
        return (RuleSetNode) getNode();
    }

    public void internalTrigger(final NodeInstance from,
                                String type) {
        if ( !org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals( type ) ) {
            throw new IllegalArgumentException( "A RuleSetNode only accepts default incoming connections!" );
        }
        addRuleSetListener();
        ((ProcessInstance) getProcessInstance()).getAgenda().activateRuleFlowGroup( getRuleSetNode().getRuleFlowGroup() );
    }

    public void addEventListeners() {
        super.addEventListeners();
        addRuleSetListener();
    }
    
    private void addRuleSetListener() {
        ((InternalAgenda) ((ProcessInstance) getProcessInstance()).getWorkingMemory().getAgenda()).addRuleFlowGroupListener( getRuleSetNode().getRuleFlowGroup(),
                                                                                                         this );
    }

    public void removeEventListeners() {
        super.removeEventListeners();
        ((InternalAgenda) ((ProcessInstance) getProcessInstance()).getWorkingMemory().getAgenda()).removeRuleFlowGroupListener( getRuleSetNode().getRuleFlowGroup(),
                                                                                                         this );
    }

    public void cancel() {
        super.cancel();
        ((ProcessInstance) getProcessInstance()).getAgenda().deactivateRuleFlowGroup( getRuleSetNode().getRuleFlowGroup() );
    }

    public void ruleFlowGroupDeactivated() {
    	synchronized (getProcessInstance()) {
            removeEventListeners();
            triggerCompleted();
		}
    }

}