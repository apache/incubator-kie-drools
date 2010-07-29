/**
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

package org.drools.workflow.instance.node;

import org.drools.WorkingMemory;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.base.SequentialKnowledgeHelper;
import org.drools.common.InternalRuleBase;
import org.drools.process.instance.ProcessInstance;
import org.drools.runtime.process.NodeInstance;
import org.drools.spi.Action;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.ProcessContext;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * Runtime counterpart of an action node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class ActionNodeInstance extends NodeInstanceImpl {

    private static final long serialVersionUID = 510l;

    protected ActionNode getActionNode() {
        return (ActionNode) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "An ActionNode only accepts default incoming connections!");
        }
		Action action = (Action) getActionNode().getAction().getMetaData("Action");
		try {
		    KnowledgeHelper knowledgeHelper = createKnowledgeHelper();
		    ProcessContext context = new ProcessContext();
		    context.setNodeInstance(this);
	        action.execute(knowledgeHelper, ((ProcessInstance) getProcessInstance()).getWorkingMemory(), context);		    
		} catch (Exception e) {
		    throw new RuntimeException("unable to execute Action", e);
		}
    	triggerCompleted();
    }

    public void triggerCompleted() {
        triggerCompleted(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
    }
    
    private KnowledgeHelper createKnowledgeHelper() {
        WorkingMemory workingMemory = ((ProcessInstance) getProcessInstance()).getWorkingMemory();
        if ( ((InternalRuleBase) workingMemory.getRuleBase()).getConfiguration().isSequential() ) {
            return new SequentialKnowledgeHelper( workingMemory );
        } else {
            return new DefaultKnowledgeHelper( workingMemory );
        }
    }

}
