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

import org.drools.ruleflow.common.core.Work;
import org.drools.ruleflow.common.instance.WorkItem;
import org.drools.ruleflow.common.instance.impl.WorkItemImpl;
import org.drools.ruleflow.core.WorkItemNode;
import org.drools.ruleflow.instance.RuleFlowNodeInstance;

/**
 * Runtime counterpart of a task node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class TaskNodeInstanceImpl extends RuleFlowNodeInstanceImpl {

    private WorkItemImpl taskInstance;
    
    protected WorkItemNode getTaskNode() {
        return (WorkItemNode) getNode();
    }
    
    public WorkItem getTaskInstance() {
        return taskInstance;
    }

    public void internalTrigger(final RuleFlowNodeInstance from) {
		Work task = getTaskNode().getWork();
		taskInstance = new WorkItemImpl();
		taskInstance.setName(task.getName());
		taskInstance.setProcessInstanceId(getProcessInstance().getId());
		taskInstance.setParameters(task.getParameters());
		getProcessInstance().getWorkingMemory().getWorkItemManager().executeWorkItem(taskInstance);
    }

    public void triggerCompleted() {
        getProcessInstance().getNodeInstance( getTaskNode().getTo().getTo() ).trigger( this );
        getProcessInstance().removeNodeInstance(this);
    }

}