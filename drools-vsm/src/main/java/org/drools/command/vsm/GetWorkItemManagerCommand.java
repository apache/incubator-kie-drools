package org.drools.command.vsm;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.runtime.process.WorkItemManager;
import org.drools.vsm.remote.WorkItemManagerRemoteClient;

/**
 * 
 * @author Lucas Amador
 *
 */
public class GetWorkItemManagerCommand implements GenericCommand<WorkItemManager> {
	
	private static final long serialVersionUID = 1L;

	public WorkItemManager execute(Context context) {
        WorkItemManager workItemManager = ((KnowledgeCommandContext) context).getWorkItemManager();
        ((ExecutionResultImpl)((KnowledgeCommandContext) context ).getExecutionResults()).getResults().put("workItemManager", getRemoteClient(workItemManager) );
        return workItemManager;
	}
	
	private WorkItemManager getRemoteClient(WorkItemManager workItemManager) {
        return new WorkItemManagerRemoteClient();
    }

}