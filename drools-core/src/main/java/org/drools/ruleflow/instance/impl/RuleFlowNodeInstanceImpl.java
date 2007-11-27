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

import org.drools.common.EventSupport;
import org.drools.common.InternalWorkingMemory;
import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.instance.RuleFlowNodeInstance;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;

/**
 * Default implementation of a RuleFlow node instance.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public abstract class RuleFlowNodeInstanceImpl
    implements
    RuleFlowNodeInstance {

    private long                     id;
    private long                     nodeId;
    private RuleFlowProcessInstance processInstance;

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

    public void setProcessInstance(final RuleFlowProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    public RuleFlowProcessInstance getProcessInstance() {
        return this.processInstance;
    }

    public Node getNode() {
        return this.processInstance.getRuleFlowProcess().getNode( this.nodeId );
    }
    
    public void cancel() {
    	getProcessInstance().removeNodeInstance(this);
    }
    
    public final void trigger(RuleFlowNodeInstance from) {
        ((EventSupport) getProcessInstance().getWorkingMemory()).getRuleFlowEventSupport().fireBeforeRuleFlowNodeTriggered(this, (InternalWorkingMemory) getProcessInstance().getWorkingMemory());
        internalTrigger(from);
        ((EventSupport) getProcessInstance().getWorkingMemory()).getRuleFlowEventSupport().fireAfterRuleFlowNodeTriggered(this, (InternalWorkingMemory) getProcessInstance().getWorkingMemory());
    }
    
    public abstract void internalTrigger(RuleFlowNodeInstance from);

}
