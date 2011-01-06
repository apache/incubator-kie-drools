/**
 * Copyright 2010 JBoss Inc
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

package org.drools.command.runtime.process;


import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemManager;
import org.drools.runtime.StatefulKnowledgeSession;

public class GetWorkItemCommand implements GenericCommand<WorkItem> {
	
	private long workItemId;

    public GetWorkItemCommand() {
    }

    public GetWorkItemCommand(long workItemId) {
        this.workItemId = workItemId;
    }
        
	public long getWorkItemId() {
		return workItemId;
	}
	
	public void setWorkItemId(long workItemId) {
		this.workItemId = workItemId;
	}

    public WorkItem execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();        
        return ((WorkItemManager) ksession.getWorkItemManager()).getWorkItem(workItemId);
	}

	public String toString() {
		return "((org.drools.process.instance.WorkItemManager) session.getWorkItemManager()).getWorkItem("
			+ workItemId +  ");";
	}

}