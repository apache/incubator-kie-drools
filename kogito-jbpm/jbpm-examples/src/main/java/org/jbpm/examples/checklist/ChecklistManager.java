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