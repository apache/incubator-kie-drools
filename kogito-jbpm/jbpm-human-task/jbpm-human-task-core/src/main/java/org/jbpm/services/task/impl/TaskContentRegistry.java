package org.jbpm.services.task.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.task.model.Task;
import org.kie.internal.task.api.ContentMarshallerContext;

public class TaskContentRegistry {

	private static TaskContentRegistry INSTANCE = new TaskContentRegistry();
	
    private ConcurrentHashMap<String, ContentMarshallerContext> marhsalContexts = new ConcurrentHashMap<String, ContentMarshallerContext>();
    
    private TaskContentRegistry() {
    	
    }
	public static TaskContentRegistry get() {
		return INSTANCE;
	}
    
	public synchronized void addMarshallerContext(String ownerId, ContentMarshallerContext context) {
		this.marhsalContexts.put(ownerId, context);
	}

	
	public synchronized void removeMarshallerContext(String ownerId) {
		this.marhsalContexts.remove(ownerId);
	}

	
	public ContentMarshallerContext getMarshallerContext(Task task) {
		if (task.getTaskData().getDeploymentId() != null && this.marhsalContexts.containsKey(task.getTaskData().getDeploymentId())) {
            return this.marhsalContexts.get(task.getTaskData().getDeploymentId());
        }
        
        return new ContentMarshallerContext();
	}
	
	public ContentMarshallerContext getMarshallerContext(String deploymentId) {
		if (deploymentId != null && this.marhsalContexts.containsKey(deploymentId)) {
            return this.marhsalContexts.get(deploymentId);
        }
        
        return new ContentMarshallerContext();
	}
}
