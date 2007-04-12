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

import org.drools.ruleflow.core.INode;
import org.drools.ruleflow.instance.IRuleFlowNodeInstance;
import org.drools.ruleflow.instance.IRuleFlowProcessInstance;

/**
 * Default implementation of a RuleFlow node instance.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public abstract class RuleFlowNodeInstance
    implements
    IRuleFlowNodeInstance {

    private long                     id;
    private long                     nodeId;
    private IRuleFlowProcessInstance processInstance;

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

    public void setProcessInstance(final IRuleFlowProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    public IRuleFlowProcessInstance getProcessInstance() {
        return this.processInstance;
    }

    protected INode getNode() {
        return this.processInstance.getRuleFlowProcess().getNode( this.nodeId );
    }
}
