package org.jbpm.services.task.identity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.TaskQueryService;

@Decorator
public class UserGroupTaskQueryServiceDecorator extends
        AbstractUserGroupCallbackDecorator implements TaskQueryService {

    @Inject
    @Delegate
    private TaskQueryService delegate;

    public void setDelegate(TaskQueryService delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(
            String userId, String language) {

        return delegate.getTasksAssignedAsBusinessAdministrator(userId, language);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId,
            String language) {

        return delegate.getTasksAssignedAsExcludedOwner(userId, language);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId,
            String language) {
        List<String> groupIds = doUserGroupCallbackOperation(userId, null);
        
        return delegate.getTasksAssignedAsPotentialOwner(userId, groupIds, language);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId,
            List<String> groupIds, String language) {

        return delegate.getTasksAssignedAsPotentialOwner(userId, groupIds, language);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId,
            List<String> groupIds, String language, int firstResult,
            int maxResults) {

        return delegate.getTasksAssignedAsPotentialOwner(userId, groupIds, language, firstResult, maxResults);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(
            String userId, List<Status> status, String language) {
        List<String> groupIds = doUserGroupCallbackOperation(userId, null);
        
        return delegate.getTasksAssignedAsPotentialOwnerByStatusByGroup(userId, groupIds, status, language);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(
            String userId, List<String> groupIds, List<Status> status,
            String language) {

        return delegate.getTasksAssignedAsPotentialOwnerByStatusByGroup(userId, groupIds, status, language);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsRecipient(String userId,
            String language) {

        return delegate.getTasksAssignedAsRecipient(userId, language);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId,
            String language) {
        
        return delegate.getTasksAssignedAsTaskInitiator(userId, language);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId,
            String language) {

        return delegate.getTasksAssignedAsTaskStakeholder(userId, language);
    }

    @Override
    public List<TaskSummary> getTasksAssignedByGroup(String groupId,
            String language) {

        return delegate.getTasksAssignedByGroup(groupId, language);
    }

    @Override
    public List<TaskSummary> getTasksAssignedByGroups(List<String> groupsId,
            String language) {

        return delegate.getTasksAssignedByGroups(groupsId, language);
    }

    @Override
    public List<TaskSummary> getTasksAssignedByGroupsByExpirationDate(
            List<String> groupIds, String language, Date expirationDate) {

        return delegate.getTasksAssignedByGroupsByExpirationDate(groupIds, language, expirationDate);
    }

    @Override
    public List<TaskSummary> getTasksAssignedByGroupsByExpirationDateOptional(
            List<String> groupIds, String language, Date expirationDate) {

        return delegate.getTasksAssignedByGroupsByExpirationDateOptional(groupIds, language, expirationDate);
    }

    @Override
    public List<TaskSummary> getTasksOwned(String userId, String language) {

        return delegate.getTasksOwned(userId, language);
    }

    @Override
    public List<TaskSummary> getTasksOwnedByStatus(String userId, List<Status> status,
            String language) {

        return delegate.getTasksOwnedByStatus(userId, status, language);
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDate(String userId,
            List<Status> status, Date expirationDate) {

        return delegate.getTasksOwnedByExpirationDate(userId, status, expirationDate);
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateOptional(
            String userId, List<Status> status, Date expirationDate) {

        return delegate.getTasksOwnedByExpirationDateOptional(userId, status, expirationDate);
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateBeforeSpecifiedDate(
            String userId, java.util.List<Status> status, Date date) {
        return delegate.getTasksOwnedByExpirationDateBeforeSpecifiedDate(userId, status, date);
    }

    @Override
    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId,
            String userId, String language) {

        return delegate.getSubTasksAssignedAsPotentialOwner(parentId, userId, language);
    }

    @Override
    public List<TaskSummary> getSubTasksByParent(long parentId) {
        
        return delegate.getSubTasksByParent(parentId);
    }

    @Override
    public int getPendingSubTasksByParent(long parentId) {

        return delegate.getPendingSubTasksByParent(parentId);
    }

    @Override
    public Task getTaskByWorkItemId(long workItemId) {

        return delegate.getTaskByWorkItemId(workItemId);
    }

    @Override
    public Task getTaskInstanceById(long taskId) {

        return delegate.getTaskInstanceById(taskId);
    }

    @Override
    public List<TaskSummary> getTasksByStatusByProcessInstanceId(
            long processInstanceId, List<Status> status, String language) {

        return delegate.getTasksByStatusByProcessInstanceId(processInstanceId, status, language);
    }

    @Override
    public List<TaskSummary> getTasksByStatusByProcessInstanceIdByTaskName(
            long processInstanceId, List<Status> status, String taskName, String language) {
        return delegate.getTasksByStatusByProcessInstanceIdByTaskName(processInstanceId, status, taskName, language);
    }

    @Override
    public List<Long> getTasksByProcessInstanceId(long processInstanceId) {

        return delegate.getTasksByProcessInstanceId(processInstanceId);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDate(String userId, List<Status> status, Date expirationDate) {
        
        List<String> groupIds = doUserGroupCallbackOperation(userId, null);
        return delegate.getTasksAssignedAsPotentialOwnerByExpirationDate(userId, groupIds, status, expirationDate);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<Status> status, Date expirationDate) {
        List<String> groupIds = doUserGroupCallbackOperation(userId, null);
        return delegate.getTasksAssignedAsPotentialOwnerByExpirationDateOptional(userId, groupIds, status, expirationDate);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDate(String userId, List<String> groupIds, List<Status> status, Date expirationDate) {
        return delegate.getTasksAssignedAsPotentialOwnerByExpirationDate(userId, groupIds, status, expirationDate);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<String> groupIds, List<Status> status, Date expirationDate) {
        return delegate.getTasksAssignedAsPotentialOwnerByExpirationDateOptional(userId, groupIds, status, expirationDate);
    }

    @Override
    public Map<Long, List<OrganizationalEntity>> getPotentialOwnersForTaskIds(List<Long> taskIds) {
        return delegate.getPotentialOwnersForTaskIds(taskIds);
    }

    @Override
    public List<TaskSummary> getTasksByVariousFields(List<Long> workItemIds, List<Long> taskIds, List<Long> procInstIds,
            List<String> busAdmins, List<String> potOwners, List<String> taskOwners, 
            List<Status> status, List<String> language, boolean union) {
    	potOwners = populateOrganizationalEntityWithGroupInfo(potOwners);
    	busAdmins = populateOrganizationalEntityWithGroupInfo(busAdmins);
        return delegate.getTasksByVariousFields(workItemIds, taskIds, procInstIds, 
                busAdmins, potOwners, taskOwners, 
                status, language, union);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<TaskSummary> getTasksByVariousFields(Map<String, List<?>> parameters, boolean union) {
    	List<String> potOwners = populateOrganizationalEntityWithGroupInfo((List<String>) parameters.get(POTENTIAL_OWNER_ID_LIST));
    	parameters.put(POTENTIAL_OWNER_ID_LIST, potOwners);
    	List<String> bussinessAdmins = populateOrganizationalEntityWithGroupInfo((List<String>) parameters.get(BUSINESS_ADMIN_ID_LIST));
    	parameters.put(BUSINESS_ADMIN_ID_LIST, bussinessAdmins);
        return delegate.getTasksByVariousFields(parameters, union);
    }
    /**
     * Populates given list with group information taken from UserGroupCallback implementation
     * to allow proper query for tasks based on user assignments.
     * @param entities - "raw" list of organizational entities 
     * @return if list is not null and not empty returns list of org entities populated with group info, otherwise same as argument
     */
    protected List<String> populateOrganizationalEntityWithGroupInfo(List<String> entities) {
    	if (entities != null && entities.size() > 0) {
    		Set<String> groupIds = new HashSet<String>();
    		for (String userId : entities) {
    			List<String> tmp = doUserGroupCallbackOperation(userId, null);
    			if (tmp != null) {
    				groupIds.addAll(tmp);
    			}
    		}
    		groupIds.addAll(entities);
    		return new ArrayList<String>(groupIds);
    	}
    	
    	return entities;
    }

}
