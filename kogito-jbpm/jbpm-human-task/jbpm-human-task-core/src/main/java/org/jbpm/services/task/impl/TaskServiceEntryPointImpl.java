/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.commands.TaskContext;
import org.jbpm.services.task.impl.model.GroupImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.jbpm.services.task.rule.TaskRuleService;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.shared.services.impl.events.JbpmServicesEventImpl;
import org.jbpm.shared.services.impl.events.JbpmServicesEventListener;
import org.kie.api.command.Command;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.EventService;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.TaskAdminService;
import org.kie.internal.task.api.TaskAttachmentService;
import org.kie.internal.task.api.TaskCommentService;
import org.kie.internal.task.api.TaskContentService;
import org.kie.internal.task.api.TaskDefService;
import org.kie.internal.task.api.TaskEventsService;
import org.kie.internal.task.api.TaskIdentityService;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.TaskQueryService;
import org.kie.internal.task.api.TaskStatisticsService;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTaskData;
import org.kie.internal.task.api.model.NotificationEvent;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.kie.internal.task.api.model.TaskDef;
import org.kie.internal.task.api.model.TaskEvent;


/**
 * 
 */
@Transactional
@ApplicationScoped
public class TaskServiceEntryPointImpl implements InternalTaskService, EventService<JbpmServicesEventListener<NotificationEvent>,JbpmServicesEventListener<Task>> {

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
    private TaskRuleService taskRuleService;
    
    // External NON CDI event Listeners for Task Lifecycle
    private Event<Task> taskEvents = new JbpmServicesEventImpl<Task>();
    // External NON CDI event listener for Task Deadline and Email notifications
    private Event<NotificationEvent> taskNotificationEvents = new JbpmServicesEventImpl<NotificationEvent>();
    
    private UserInfo userInfo;
    
    
    public TaskServiceEntryPointImpl() {
    }

    public TaskDefService getTaskDefService() {
        return taskDefService;
    }
    
    public void registerTaskLifecycleEventListener(JbpmServicesEventListener<Task> taskLifecycleEventListener){
        ((JbpmServicesEventImpl<Task>)taskEvents).addListener(taskLifecycleEventListener);
    }

    public void registerTaskNotificationEventListener(JbpmServicesEventListener<NotificationEvent> notificationEventListener){
        ((JbpmServicesEventImpl<NotificationEvent>)taskNotificationEvents).addListener(notificationEventListener);
    }
    
    public void setTaskContentService(TaskContentService taskContentService) {
        this.taskContentService = taskContentService;
    }

    public void setTaskCommentService(TaskCommentService taskCommentService) {
        this.taskCommentService = taskCommentService;
    }

    public void setTaskAttachmentService(TaskAttachmentService taskAttachmentService) {
        this.taskAttachmentService = taskAttachmentService;
    }

    public void setTaskStatisticService(TaskStatisticsService taskStatisticService) {
        this.taskStatisticService = taskStatisticService;
    }
    
    public TaskInstanceService getTaskInstanceService() {
        return taskInstanceService;
    }

    public TaskIdentityService getTaskIdentityService() {
        return taskIdentityService;
    }

    public TaskAdminService getTaskAdminService() {
        return taskAdminService;
    }

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
        Task task = taskQueryService.getTaskInstanceById(taskId);
        this.taskRuleService.executeRules(task, userId, data, TaskRuleService.COMPLETE_TASK_SCOPE);
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

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResults) {
        return taskQueryService.getTasksAssignedAsPotentialOwner(userId, groupIds, language, firstResult, maxResults);
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

    public List<TaskSummary> getTasksOwned(String userId, String language) {
        return taskQueryService.getTasksOwned(userId, language);
    }
 
    public Map<Long, List<OrganizationalEntity>> getPotentialOwnersForTaskIds(List<Long> taskIds) {
        return taskQueryService.getPotentialOwnersForTaskIds(taskIds);
    }
    
    public List<TaskSummary> getTasksOwnedByStatus(String userId, List<Status> status, String language) {
        return taskQueryService.getTasksOwnedByStatus(userId, status, language);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, String language) {
        return taskQueryService.getTasksAssignedAsPotentialOwnerByStatus(userId, status, language);
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

    public long addTask(Task task, Map<String, Object> params){        
        initializeTask(task);
        this.taskRuleService.executeRules(task, null, params, TaskRuleService.ADD_TASK_SCOPE);
        return this.taskInstanceService.addTask(task, params);
    }
    
    public long addTask(Task task, ContentData data){        
        initializeTask(task);
        ContentMarshallerContext context = getMarshallerContext(task);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(data.getContent(), context.getEnvironment(), context.getClassloader());
        
        this.taskRuleService.executeRules(task, null, unmarshalledObject, TaskRuleService.ADD_TASK_SCOPE);
        return this.taskInstanceService.addTask(task, data);
    }
    
    private void initializeTask(Task task){
        Status assignedStatus = null;

        if (task.getPeopleAssignments() != null && task.getPeopleAssignments().getPotentialOwners() != null && task.getPeopleAssignments().getPotentialOwners().size() == 1) {
            // if there is a single potential owner, assign and set status to Reserved
            OrganizationalEntity potentialOwner = task.getPeopleAssignments().getPotentialOwners().get(0);
            // if there is a single potential user owner, assign and set status to Reserved
            if (potentialOwner instanceof UserImpl) {
            	((InternalTaskData) task.getTaskData()).setActualOwner((UserImpl) potentialOwner);

                assignedStatus = Status.Reserved;
            }
            //If there is a group set as potentialOwners, set the status to Ready ??
            if (potentialOwner instanceof GroupImpl) {

                assignedStatus = Status.Ready;
            }
        } else if (task.getPeopleAssignments() != null && task.getPeopleAssignments().getPotentialOwners() != null && task.getPeopleAssignments().getPotentialOwners().size() > 1) {
            // multiple potential owners, so set to Ready so one can claim.
            assignedStatus = Status.Ready;
        } else {
            //@TODO: we have no potential owners
        }

        if (assignedStatus != null) {
            ((InternalTaskData) task.getTaskData()).setStatus(assignedStatus);
        }
        
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
    
    public long addContent(long taskId, Map<String, Object> params) {
        return this.taskContentService.addContent(taskId, params);
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

    public void setTaskNames(long taskId, List<I18NText> taskName) {
        taskInstanceService.setTaskNames(taskId, taskName);
    }
    
    public Map<String, Object> getTaskContent(long taskId){
        Task taskById = taskQueryService.getTaskInstanceById(taskId);
        Content contentById = taskContentService.getContentById(taskById.getTaskData().getDocumentContentId());
        ContentMarshallerContext context = getMarshallerContext(taskById);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(contentById.getContent(), context.getEnvironment(), context.getClassloader());
        if (!(unmarshalledObject instanceof Map)) {
            throw new IllegalStateException(" The Task Content Needs to be a Map in order to use this method and it was: "+unmarshalledObject.getClass());

        }
        Map<String, Object> content = (Map<String, Object>) unmarshalledObject;
        
        return content;
    }
    
   
    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDate(String userId, List<Status> statuses, Date expirationDate) {
        return taskQueryService.getTasksAssignedAsPotentialOwnerByExpirationDate(userId, statuses, expirationDate);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<Status> statuses, Date expirationDate) {
        return taskQueryService.getTasksAssignedAsPotentialOwnerByExpirationDateOptional(userId, statuses, expirationDate);
    }
    
    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDate(String userId, List<Status> status, Date expirationDate){
        return taskQueryService.getTasksOwnedByExpirationDate(userId, status, expirationDate);
    }
    
    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateOptional(String userId, List<Status> statuses, Date expirationDate) {
        return taskQueryService.getTasksOwnedByExpirationDateOptional(userId, statuses, expirationDate);
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateBeforeSpecifiedDate(String userId, List<Status> status, Date date) {
        return taskQueryService.getTasksOwnedByExpirationDateBeforeSpecifiedDate(userId, status, date);
    }

    @Override
    public List<TaskSummary> getTasksByStatusByProcessInstanceId(
            long processInstanceId, List<Status> status, String language) {
        return taskQueryService.getTasksByStatusByProcessInstanceId(processInstanceId, status, language);
    }

    @Override
    public List<TaskSummary> getTasksByStatusByProcessInstanceIdByTaskName(
            long processInstanceId, List<Status> status, String taskName,
            String language) {
        return taskQueryService.getTasksByStatusByProcessInstanceIdByTaskName(processInstanceId, status, taskName, language);
    }
    

    @Override
    public List<Long> getTasksByProcessInstanceId(long processInstanceId) {
        return taskQueryService.getTasksByProcessInstanceId(processInstanceId);
    }

    
    public Event<Task> getTaskLifecycleEventListeners() {
        return taskEvents;
    }

    public Event<NotificationEvent> getTaskNotificationEventListeners() {
        return taskNotificationEvents;
    }

    public void clearTaskLifecycleEventListeners() {
        ((JbpmServicesEventImpl)taskEvents).clearListeners();
    }

    public void clearTasknotificationEventListeners() {
        ((JbpmServicesEventImpl)taskNotificationEvents).clearListeners();
    }
    
    public <T> T execute(Command<T> command) {
    	return ((TaskCommand<T>) command).execute(new TaskContext(this));
    }

    public TaskRuleService getTaskRuleService() {
        return taskRuleService;
    }

    public void setTaskRuleService(TaskRuleService taskRuleService) {
        this.taskRuleService = taskRuleService;
    }

    @Override
    public void addMarshallerContext(String ownerId, ContentMarshallerContext context) {
        this.taskContentService.addMarshallerContext(ownerId, context);
    }

    @Override
    public void removeMarshallerContext(String ownerId) {
        this.taskContentService.removeMarshallerContext(ownerId);
    }   

    public ContentMarshallerContext getMarshallerContext(Task task) {
        ContentMarshallerContext context = this.taskContentService.getMarshallerContext(task);
        if (context != null) {
            return context;
        }
        
        return new ContentMarshallerContext();
    }
    
}
