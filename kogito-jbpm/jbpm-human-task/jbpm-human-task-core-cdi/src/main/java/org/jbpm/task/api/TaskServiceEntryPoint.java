/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.api;


import java.util.Date;
import java.util.List;
import java.util.Map;
import org.jbpm.task.Content;
import org.jbpm.task.ContentData;
import org.jbpm.task.FaultData;
import org.jbpm.task.Group;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskDef;
import org.jbpm.task.TaskEvent;
import org.jbpm.task.User;
import org.jbpm.task.UserInfo;
import org.jbpm.task.query.TaskSummary;


/**
 *
 */

public interface TaskServiceEntryPoint {
    
    //Main Service

    TaskAdminService getTaskAdminService();

    TaskDefService getTaskDefService();

    TaskIdentityService getTaskIdentityService();

    TaskInstanceService getTaskInstanceService();

    TaskQueryService getTaskQueryService();

    TaskEventsService getTaskEventsService();
    
    void setTaskAdminService(TaskAdminService adminService);

    void setTaskDefService(TaskDefService defService);

    void setTaskIdentityService(TaskIdentityService identityService);

    void setTaskInstanceService(TaskInstanceService taskInstanceService);

    void setTaskQueryService(TaskQueryService queryService);
    
    void setTaskEventsService(TaskEventsService eventsService);
    
    // Delegates

    void activate(long taskId, String userId);

    void addGroup(Group group);

    void addUser(User user);

    int archiveTasks(List<TaskSummary> tasks);

    void claim(long taskId, String userId);

    void claim(long taskId, String userId, List<String> groupIds);

    void claimNextAvailable(String userId, List<String> groupIds, String language);

    void claimNextAvailable(String userId, String language);

    void complete(long taskId, String userId, Map<String, Object> data);

    void delegate(long taskId, String userId, String targetUserId);

    void deleteFault(long taskId, String userId);

    void deleteOutput(long taskId, String userId);

    void deployTaskDef(TaskDef def);

    void exit(long taskId, String userId);

    void fail(long taskId, String userId, Map<String, Object> faultData);

    void forward(long taskId, String userId, String targetEntityId);

    List<TaskSummary> getActiveTasks();

    List<TaskSummary> getActiveTasks(Date since);

    List<TaskDef> getAllTaskDef(String filter);

    List<TaskSummary> getArchivedTasks();

    List<TaskSummary> getCompletedTasks();

    List<TaskSummary> getCompletedTasks(Date since);

    List<TaskSummary> getCompletedTasksByProcessId(Long processId);

    Content getContentById(long contentId);

    Group getGroupById(String groupId);

    List<Group> getGroups();

    List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language);

    List<TaskSummary> getSubTasksByParent(long parentId);

    Task getTaskByWorkItemId(long workItemId);

    TaskDef getTaskDefById(String id);

    Task getTaskById(long taskId);

    List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language);

    List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResult);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String salaboy, List<Status> status, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status, String language);

    List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language);

    List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language);

    List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language);

    List<TaskSummary> getTasksOwned(String userId);

    List<TaskSummary> getTasksOwned(String userId, List<Status> status, String language);

    User getUserById(String userId);

    List<User> getUsers();

    long newTask(String name, Map<String, Object> params);
    
    long newTask(TaskDef def, Map<String, Object> params);
    
    long newTask(TaskDef def, Map<String, Object> params, boolean deploy);
    
    long addTask(Task task, Map<String, Object> params);
    
    long addTask(Task task, ContentData data);

    void release(long taskId, String userId);

    void remove(long taskId, String userId);

    void removeGroup(String groupId);

    int removeTasks(List<TaskSummary> tasks);

    void removeUser(String userId);

    void resume(long taskId, String userId);

    void setFault(long taskId, String userId, FaultData fault);

    void setOutput(long taskId, String userId, Object outputContentData);

    void setPriority(long taskId, String userId, int priority);

    void skip(long taskId, String userId);

    void start(long taskId, String userId);

    void stop(long taskId, String userId);

    void suspend(long taskId, String userId);

    void undeployTaskDef(String id);
    
    public List<TaskEvent> getTaskEventsById(long taskId);
    
    public UserInfo getUserInfo();

    public void setUserInfo(UserInfo userInfo);
    
    public void addUsersAndGroups(Map<String, User> users, Map<String, Group> groups);
    
    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners);
}
