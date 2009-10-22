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
import org.drools.common.EventSupport;
import org.drools.common.InternalWorkingMemory;
import org.drools.process.instance.ProcessInstance;
import org.drools.runtime.process.NodeInstance;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * Runtime counterpart of an end node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class EndNodeInstance extends NodeInstanceImpl {

    private static final long serialVersionUID = 400L;

    public EndNode getEndNode() {
    	return (EndNode) getNode();
    }
    
    public void internalTrigger(final NodeInstance from, String type) {
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "An EndNode only accepts default incoming connections!");
        }
        ((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
        if (getEndNode().isTerminate()) {
        	boolean hidden = false;
        	if (getNode().getMetaData("hidden") != null) {
        		hidden = true;
        	}
        	WorkingMemory workingMemory = ((ProcessInstance) getProcessInstance()).getWorkingMemory();
        	if (!hidden) {
        		((EventSupport) workingMemory).getRuleFlowEventSupport().fireBeforeRuleFlowNodeLeft(this, (InternalWorkingMemory) workingMemory);
        	}
        	((ProcessInstance) getProcessInstance()).setState( ProcessInstance.STATE_COMPLETED );
            if (!hidden) {
                ((EventSupport) workingMemory).getRuleFlowEventSupport().fireAfterRuleFlowNodeLeft(this, (InternalWorkingMemory) workingMemory);
            }
        } else {
            ((NodeInstanceContainer) getNodeInstanceContainer())
                .nodeInstanceCompleted(this, null);
        }
    }

}
