/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.examples.checklist;

import java.util.List;

import org.kie.api.task.model.OrganizationalEntity;

public interface ChecklistManager {
	
	List<ChecklistContext> getContexts();
	
	long createContext(String name, String actorId);
	
	List<ChecklistItem> getTasks(long processInstanceId, List<ChecklistContextConstraint> contexts);
	
	ChecklistItem addTask(String userId, String[] actorIds, String[] groupIds, String name, String orderingId, long processInstanceId);
	
	void updateTaskName(long taskId, String name);
	
	void updateTaskDescription(long taskId, String description);

	void updateTaskPriority(long taskId, int priority);
	
	void updateTaskPotentialOwners(long taskId, List<OrganizationalEntity> potentialOwners);
	
	void claimTask(String userId, long taskId);
	
	void releaseTask(String userId, long taskId);
	
	void completeTask(String userId, long taskId);
	
	void abortTask(String userId, long taskId);
	
	void selectOptionalTask(String taskName, long processInstanceId);
	
}