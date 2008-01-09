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
import java.util.Map;

import org.drools.process.core.Work;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemListener;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * Runtime counterpart of a task node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class WorkItemNodeInstance extends NodeInstanceImpl implements WorkItemListener {

    private static final long serialVersionUID = 400L;
    
    private WorkItemImpl workItem;
    
    protected WorkItemNode getWorkItemNode() {
        return (WorkItemNode) getNode();
    }
    
    public WorkItem getWorkItem() {
        return workItem;
    }

    public void internalTrigger(final NodeInstance from, String type) {
        // TODO this should be included for ruleflow only, not for BPEL
//        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
//            throw new IllegalArgumentException(
//                "A WorkItemNode only accepts default incoming connections!");
//        }
        WorkItemNode workItemNode = getWorkItemNode();
        Work work = workItemNode.getWork();
		workItem = new WorkItemImpl();
		workItem.setName(work.getName());
		workItem.setProcessInstanceId(getProcessInstance().getId());
		workItem.setParameters(work.getParameters());
		for (Iterator<Map.Entry<String, String>> iterator = workItemNode.getInMappings().entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> mapping = iterator.next();
            workItem.setParameter(mapping.getKey(), getProcessInstance().getVariable(mapping.getValue()));
        }
		getProcessInstance().addWorkItemListener(this);
		getProcessInstance().getWorkingMemory().getWorkItemManager().executeWorkItem(workItem);
    }

    public void triggerCompleted(WorkItem workItem) {
        getNodeInstanceContainer().removeNodeInstance(this);
        for (Iterator<Map.Entry<String, String>> iterator = getWorkItemNode().getOutMappings().entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> mapping = iterator.next();
            getProcessInstance().setVariable(mapping.getValue(), workItem.getResult(mapping.getKey()));
        }
        getNodeInstanceContainer().getNodeInstance( getWorkItemNode().getTo().getTo() ).trigger( this, getWorkItemNode().getTo().getToType() );
    }

    public void workItemAborted(WorkItem workItem) {
        if ( this.workItem.getId() == workItem.getId() ) {
            getProcessInstance().removeWorkItemListener(this);
            triggerCompleted(workItem);
        }
    }

    public void workItemCompleted(WorkItem workItem) {
        if ( this.workItem.getId() == workItem.getId() ) {
            getProcessInstance().removeWorkItemListener(this);
            triggerCompleted(workItem);
        }
    }

}