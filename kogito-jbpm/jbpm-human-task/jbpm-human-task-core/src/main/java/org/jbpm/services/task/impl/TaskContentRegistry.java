/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
