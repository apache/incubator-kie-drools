/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.impl;


import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.Attachment;
import org.jbpm.task.Comment;
import org.jbpm.task.Content;
import org.jbpm.task.ContentData;
import org.jbpm.task.FaultData;
import org.jbpm.task.Group;
import org.jbpm.task.I18NText;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Status;
import org.jbpm.task.SubTasksStrategy;
import org.jbpm.task.Task;
import org.jbpm.task.TaskDef;
import org.jbpm.task.TaskEvent;
import org.jbpm.task.User;
import org.jbpm.task.UserInfo;
import org.jbpm.task.api.TaskAdminService;
import org.jbpm.task.api.TaskAttachmentService;
import org.jbpm.task.api.TaskCommentService;
import org.jbpm.task.api.TaskContentService;
import org.jbpm.task.api.TaskDefService;
import org.jbpm.task.api.TaskEventsService;
import org.jbpm.task.api.TaskIdentityService;
import org.jbpm.task.api.TaskInstanceService;
import org.jbpm.task.api.TaskQueryService;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.api.TaskStatisticsService;
import org.jbpm.task.lifecycle.listeners.TaskLifeCycleEventListener;
import org.jbpm.task.query.TaskSummary;


/**
 * 
 */
@Transactional
@ApplicationScoped
public class TaskServiceEntryPointImpl implements TaskServiceEntryPoint {

    @Inject
    private TaskDefService taskDefService;
    @Inject
    private TaskInstanceService taskInstanceService;
    @Inject
    private TaskIdentityService taskIdentityService;
    @Inject
    private TaskAdminService taskAdminService;
    @Inject
    private TaskQueryService taskQueryService;
    @Inject
    private TaskEventsService taskEventsService;
    @Inject
    private TaskContentService taskContentService;
    @Inject
    private TaskCommentService taskCommentService;
    
    @Inject
    private TaskAttachmentService taskAttachmentService;
    
    @Inject 
    private TaskStatisticsService taskStatisticService;
    
    @Inject
    private TaskLifeCycleEventListener taskLifeCycleEventListener;
    
    private UserInfo userInfo;
    
    
    public TaskServiceEntryPointImpl() {
    }

    @Override
    public TaskDefService getTaskDefService() {
        return taskDefService;
    }

    @Override
    public TaskInstanceService getTaskInstanceService() {
        return taskInstanceService;
    }

    @Override
    public TaskIdentityService getTaskIdentityService() {
        return taskIdentityService;
    }

    @Override
    public TaskAdminService getTaskAdminService() {
        return taskAdminService;
    }

    @Override
    public TaskQueryService getTaskQueryService() {
        return taskQueryService;
    }

    public TaskEventsService getTaskEventsService() {
        return taskEventsService;
    }
    
    public List<TaskSummary> getActiveTasks() {
        return taskAdminService.getActiveTasks();
    }

    public List<TaskSummary> getActiveTasks(Date since) {
        return taskAdminService.getActiveTasks(since);
    }

    public List<TaskSummary> getCompletedTasks() {
        return taskAdminService.getCompletedTasks();
    }

    public List<TaskSummary> getCompletedTasks(Date since) {
        return taskAdminService.getCompletedTasks(since);
    }

    public List<TaskSummary> getCompletedTasksByProcessId(Long processId) {
        return taskAdminService.getCompletedTasksByProcessId(processId);
    }

    public int archiveTasks(List<TaskSummary> tasks) {
        return taskAdminService.archiveTasks(tasks);
    }

    public List<TaskSummary> getArchivedTasks() {
        return taskAdminService.getArchivedTasks();
    }

    public int removeTasks(List<TaskSummary> tasks) {
        return taskAdminService.removeTasks(tasks);
    }

    public void deployTaskDef(TaskDef def) {
        taskDefService.deployTaskDef(def);
    }

    public List<TaskDef> getAllTaskDef(String filter) {
        return taskDefService.getAllTaskDef(filter);
    }

    public TaskDef getTaskDefById(String id) {
        return taskDefService.getTaskDefById(id);
    }

    public void undeployTaskDef(String id) {
        taskDefService.undeployTaskDef(id);
    }

    public void addUser(User user) {
        taskIdentityService.addUser(user);
    }

    public void addGroup(Group group) {
        taskIdentityService.addGroup(group);
    }

    public void removeGroup(String groupId) {
        taskIdentityService.removeGroup(groupId);
    }

    public void removeUser(String userId) {
        taskIdentityService.removeUser(userId);
    }

    public List<User> getUsers() {
        return taskIdentityService.getUsers();
    }

    public List<Group> getGroups() {
        return taskIdentityService.getGroups();
    }

    public User getUserById(String userId) {
        return taskIdentityService.getUserById(userId);
    }

    public Group getGroupById(String groupId) {
        return taskIdentityService.getGroupById(groupId);
    }

    public long newTask(String name, Map<String, Object> params) {
        return taskInstanceService.newTask(name, params);
    }

    public void activate(long taskId, String userId) {
        taskInstanceService.activate(taskId, userId);
    }

    public void claim(long taskId, String userId) {
        taskInstanceService.claim(taskId, userId);
    }

    public void claim(long taskId, String userId, List<String> groupIds) {
        taskInstanceService.claim(taskId, userId, groupIds);
    }

    public void claimNextAvailable(String userId, String language) {
        taskInstanceService.claimNextAvailable(userId, language);
    }

    public void claimNextAvailable(String userId, List<String> groupIds, String language) {
        taskInstanceService.claimNextAvailable(userId, groupIds, language);
    }

    public void complete(long taskId, String userId, Map<String, Object> data) {
        taskInstanceService.complete(taskId, userId, data);
    }

    public void delegate(long taskId, String userId, String targetUserId) {
        taskInstanceService.delegate(taskId, userId, targetUserId);
    }

    public void deleteFault(long taskId, String userId) {
        taskInstanceService.deleteFault(taskId, userId);
    }

    public void deleteOutput(long taskId, String userId) {
        taskInstanceService.deleteOutput(taskId, userId);
    }

    public void exit(long taskId, String userId) {
        taskInstanceService.exit(taskId, userId);
    }

    public void fail(long taskId, String userId, Map<String, Object> faultData) {
        taskInstanceService.fail(taskId, userId, faultData);
    }

    public void forward(long taskId, String userId, String targetEntityId) {
        taskInstanceService.forward(taskId, userId, targetEntityId);
    }

    public void release(long taskId, String userId) {
        taskInstanceService.release(taskId, userId);
    }

    public void remove(long taskId, String userId) {
        taskInstanceService.remove(taskId, userId);
    }

    public void resume(long taskId, String userId) {
        taskInstanceService.resume(taskId, userId);
    }

    public void setFault(long taskId, String userId, FaultData fault) {
        taskInstanceService.setFault(taskId, userId, fault);
    }

    public void setOutput(long taskId, String userId, Object outputContentData) {
        taskInstanceService.setOutput(taskId, userId, outputContentData);
    }

    public void setPriority(long taskId, int priority) {
        taskInstanceService.setPriority(taskId, priority);
    }

    public void skip(long taskId, String userId) {
        taskInstanceService.skip(taskId, userId);
    }

    public void start(long taskId, String userId) {
        taskInstanceService.start(taskId, userId);
    }

    public void stop(long taskId, String userId) {
        taskInstanceService.stop(taskId, userId);
    }

    public void suspend(long taskId, String userId) {
        taskInstanceService.suspend(taskId, userId);
    }

   
    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
        return taskQueryService.getTasksAssignedAsBusinessAdministrator(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language) {
        return taskQueryService.getTasksAssignedAsExcludedOwner(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
        return taskQueryService.getTasksAssignedAsPotentialOwner(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language) {
        return taskQueryService.getTasksAssignedAsPotentialOwner(userId, groupIds, language);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResult) {
        return taskQueryService.getTasksAssignedAsPotentialOwner(userId, groupIds, language, firstResult, maxResult);
    }

    public List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language) {
        return taskQueryService.getTasksAssignedAsRecipient(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language) {
        return taskQueryService.getTasksAssignedAsTaskInitiator(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language) {
        return taskQueryService.getTasksAssignedAsTaskStakeholder(userId, language);
    }

    public List<TaskSummary> getTasksOwned(String userId) {
        return taskQueryService.getTasksOwned(userId);
    }

    public List<TaskSummary> getTasksOwned(String userId, List<Status> status, String language) {
        return taskQueryService.getTasksOwned(userId, status, language);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String salaboy, List<Status> status, String language) {
        return taskQueryService.getTasksAssignedAsPotentialOwnerByStatus(salaboy, status, language);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status, String language) {
        return taskQueryService.getTasksAssignedAsPotentialOwnerByStatusByGroup(userId, groupIds, status, language);
    }

    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language) {
        return taskQueryService.getSubTasksAssignedAsPotentialOwner(parentId, userId, language);
    }

    public List<TaskSummary> getSubTasksByParent(long parentId) {
        return taskQueryService.getSubTasksByParent(parentId);
    }

    public Task getTaskById(long taskId) {
        return taskQueryService.getTaskInstanceById(taskId);
    }

    public Task getTaskByWorkItemId(long workItemId) {
        return taskQueryService.getTaskByWorkItemId(workItemId);
    }

    public List<TaskEvent> getTaskEventsById(long taskId) {
        return taskEventsService.getTaskEventsById(taskId);
    }

    public long newTask(TaskDef def, Map<String, Object> params) {
        return taskInstanceService.newTask(def, params);
    }
    
    public long newTask(TaskDef def, Map<String, Object> params, boolean deploy) {
        return taskInstanceService.newTask(def, params, deploy);
    }

    public long addTask(Task task, Map<String, Object> params){
        return this.taskInstanceService.addTask(task, params);
    }
    
    public long addTask(Task task, ContentData data){
        return this.taskInstanceService.addTask(task, data);
    }
    
    public void setTaskDefService(TaskDefService taskDefService) {
        this.taskDefService = taskDefService;
    }

    public void setTaskInstanceService(TaskInstanceService taskInstanceService) {
        this.taskInstanceService = taskInstanceService;
    }

    public void setTaskIdentityService(TaskIdentityService taskIdentityService) {
        this.taskIdentityService = taskIdentityService;
    }

    public void setTaskAdminService(TaskAdminService taskAdminService) {
        this.taskAdminService = taskAdminService;
    }

    public void setTaskQueryService(TaskQueryService taskQueryService) {
        this.taskQueryService = taskQueryService;
    }

    public void setTaskEventsService(TaskEventsService taskEventsService) {
        this.taskEventsService = taskEventsService;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    
    public void addUsersAndGroups(Map<String, User> users, Map<String, Group> groups) {
        
        for (User user : users.values()) {
            getTaskIdentityService().addUser(user);
        }

        for (Group group : groups.values()) {
            getTaskIdentityService().addGroup(group);
        }
        
    }
    
    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        this.taskInstanceService.nominate(taskId, userId, potentialOwners); 
    }

    public int removeAllTasks() {
        return this.taskAdminService.removeAllTasks();
    }

    public long addContent(long taskId, Content content) {
        return this.taskContentService.addContent(taskId, content);
    }

    public void deleteContent(long taskId, long contentId) {
        this.taskContentService.deleteContent(taskId, contentId);
    }

    public List<Content> getAllContentByTaskId(long taskId) {
        return this.taskContentService.getAllContentByTaskId(taskId);
    }

    public Content getContentById(long contentId) {
        return this.taskContentService.getContentById(contentId);
    }

    public long addAttachment(long taskId, Attachment attachment, Content content) {
        return this.taskAttachmentService.addAttachment(taskId, attachment, content);
    }

    public void deleteAttachment(long taskId, long attachmentId) {
        this.taskAttachmentService.deleteAttachment(taskId, attachmentId);
    }

    public List<Attachment> getAllAttachmentsByTaskId(long taskId) {
        return this.taskAttachmentService.getAllAttachmentsByTaskId(taskId);
    }

    public Attachment getAttachmentById(long attachId) {
        return this.taskAttachmentService.getAttachmentById(attachId);
    }

    public int getPendingSubTasksByParent(long parentId) {
        return this.taskQueryService.getPendingSubTasksByParent(parentId);
    }

    public TaskLifeCycleEventListener getTaskLifeCycleEventListener() {
        return taskLifeCycleEventListener;
    }

    public void removeTaskEventsById(long taskId) {
        taskEventsService.removeTaskEventsById(taskId);
    }

    public OrganizationalEntity getOrganizationalEntityById(String entityId) {
        return taskIdentityService.getOrganizationalEntityById(entityId);
    }

    public void setExpirationDate(long taskId, Date date) {
        taskInstanceService.setExpirationDate(taskId, date);
    }

    public void setDescriptions(long taskId, List<I18NText> descriptions) {
        taskInstanceService.setDescriptions(taskId, descriptions);
    }

    public void setSkipable(long taskId, boolean skipable) {
        taskInstanceService.setSkipable(taskId, skipable);
    }

    public void setSubTaskStrategy(long taskId, SubTasksStrategy strategy) {
        taskInstanceService.setSubTaskStrategy(taskId, strategy);
    }

    public int getPriority(long taskId) {
        return taskInstanceService.getPriority(taskId);
    }

    public Date getExpirationDate(long taskId) {
        return taskInstanceService.getExpirationDate(taskId);
    }

    public List<I18NText> getDescriptions(long taskId) {
        return taskInstanceService.getDescriptions(taskId);
    }

    public boolean isSkipable(long taskId) {
        return taskInstanceService.isSkipable(taskId);
    }

    public SubTasksStrategy getSubTaskStrategy(long taskId) {
        return taskInstanceService.getSubTaskStrategy(taskId);
    }

    public Task getTaskInstanceById(long taskId) {
        return taskQueryService.getTaskInstanceById(taskId);
    }

    public int getCompletedTaskByUserId(String userId) {
        return taskStatisticService.getCompletedTaskByUserId(userId);
    }

    public int getPendingTaskByUserId(String userId) {
        return taskStatisticService.getPendingTaskByUserId(userId);
    }

    public List<TaskSummary> getTasksAssignedByGroup(String groupId, String language) {
        return taskQueryService.getTasksAssignedByGroup(groupId, language);
    }

    public List<TaskSummary> getTasksAssignedByGroups(List<String> groupIds, String language) {
        return taskQueryService.getTasksAssignedByGroups(groupIds, language);
    }

    public long addComment(long taskId, Comment comment) {
        return taskCommentService.addComment(taskId, comment);
    }

    public void deleteComment(long taskId, long commentId) {
        taskCommentService.deleteComment(taskId, commentId);
    }

    public List<Comment> getAllCommentsByTaskId(long taskId) {
        return taskCommentService.getAllCommentsByTaskId(taskId);
    }

    public Comment getCommentById(long commentId) {
        return taskCommentService.getCommentById(commentId);
    }
    
    
    
    
}
