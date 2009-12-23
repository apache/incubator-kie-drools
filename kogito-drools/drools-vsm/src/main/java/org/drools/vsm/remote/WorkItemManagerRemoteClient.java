package org.drools.vsm.remote;

import java.io.Serializable;
import java.util.Map;

import org.drools.command.KnowledgeContextResolveFromContextCommand;
import org.drools.command.runtime.process.CompleteWorkItemCommand;
import org.drools.command.vsm.GetWorkItemManagerCommand;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.vsm.Message;

/**
 *
 * @author Lucas Amador
 */
public class WorkItemManagerRemoteClient implements WorkItemManager, Serializable {

	private static final long serialVersionUID = 1L;
	
	private ServiceManagerRemoteClient serviceManager;
	private String instanceId;

	public void abortWorkItem(long id) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void completeWorkItem(long id, Map<String, Object> results) {
		String kresultsId = "kresults_" + serviceManager.getSessionId();
        Message msg = new Message( serviceManager.getSessionId(),
                                   serviceManager.counter.incrementAndGet(),
                                   true,
                                   new KnowledgeContextResolveFromContextCommand( new CompleteWorkItemCommand(id, results),
                                                                                  null,
                                                                                  null,
                                                                                  instanceId,
                                                                                  kresultsId ) );
        try {
            serviceManager.client.write( msg );
        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to execute message", e );
        }
	}

	public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setServiceManager(ServiceManagerRemoteClient serviceManager) {
		this.serviceManager = serviceManager;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

}
