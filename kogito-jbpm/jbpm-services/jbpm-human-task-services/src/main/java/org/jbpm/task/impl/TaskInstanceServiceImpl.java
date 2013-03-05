/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import org.drools.core.util.StringUtils;
import org.jbpm.task.internals.lifecycle.LifeCycleManager;
import org.jbpm.task.annotations.Mvel;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.task.Content;
import org.jbpm.task.ContentData;
import org.jbpm.task.Deadline;
import org.jbpm.task.Deadlines;
import org.jbpm.task.Escalation;
import org.jbpm.task.FaultData;
import org.jbpm.task.Group;
import org.jbpm.task.I18NText;
import org.jbpm.task.Notification;
import org.jbpm.task.Operation;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Reassignment;
import org.jbpm.task.Status;
import org.jbpm.task.SubTasksStrategy;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.api.TaskInstanceService;
import org.jbpm.task.api.TaskQueryService;
import org.jbpm.task.events.AfterTaskAddedEvent;
import org.jbpm.task.exception.CannotAddTaskException;
import org.jbpm.task.identity.UserGroupCallback;
import org.jbpm.task.identity.UserGroupLifeCycleManagerDecorator;
import org.jbpm.task.internals.lifecycle.MVELLifeCycleManager;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.utils.ContentMarshallerHelper;

/**
 *
 */
@Transactional
@ApplicationScoped
public class TaskInstanceServiceImpl implements TaskInstanceService {

    
    @Inject
    private TaskQueryService taskQueryService;
    @Inject
    @Mvel
    private LifeCycleManager lifeCycleManager;
    @Inject
    private JbpmServicesPersistenceManager pm;
    @Inject
    private Logger logger;
    @Inject
    private Event<Task> taskEvents;

    public TaskInstanceServiceImpl() {
    }

    public void setTaskQueryService(TaskQueryService taskQueryService) {
        this.taskQueryService = taskQueryService;
    }

    public void setLifeCycleManager(LifeCycleManager lifeCycleManager) {
        this.lifeCycleManager = lifeCycleManager;
    }

    public void setTaskEvents(Event<Task> taskEvents) {
        this.taskEvents = taskEvents;
    }

    public void setUserGroupCallback(UserGroupCallback userGroupCallback) {
        this.userGroupCallback = userGroupCallback;
    }

    
    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }


    public long addTask(Task task, Map<String, Object> params) {
        doCallbackOperationForPeopleAssignments(task.getPeopleAssignments());
        doCallbackOperationForTaskData(task.getTaskData());
        doCallbackOperationForTaskDeadlines(task.getDeadlines());
        if (params != null) {
            ContentData contentData = ContentMarshallerHelper.marshal(params, null);
            Content content = new Content(contentData.getContent());
            pm.persist(content);
            task.getTaskData().setDocument(content.getId(), contentData);
        }
        pm.persist(task);
        if(taskEvents != null){
            taskEvents.select(new AnnotationLiteral<AfterTaskAddedEvent>() {}).fire(task);
        }
        return task.getId();
    }

    public long addTask(Task task, ContentData contentData) {
        doCallbackOperationForPeopleAssignments(task.getPeopleAssignments());
        doCallbackOperationForTaskData(task.getTaskData());
        doCallbackOperationForTaskDeadlines(task.getDeadlines());
        pm.persist(task);
        if (contentData != null) {
            Content content = new Content(contentData.getContent());
            pm.persist(content);
            task.getTaskData().setDocument(content.getId(), contentData);
        }
        if(taskEvents != null){
            taskEvents.select(new AnnotationLiteral<AfterTaskAddedEvent>() {}).fire(task);
        }
        return task.getId();
    }

    public void activate(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Activate, taskId, userId, null, null, null);
    }

    public void claim(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Claim, taskId, userId, null, null, null);
    }

    public void claim(long taskId, String userId, List<String> groupIds) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void claimNextAvailable(String userId, String language) {
        List<org.jbpm.task.Status> status = new ArrayList<org.jbpm.task.Status>();
        status.add(org.jbpm.task.Status.Ready);
        List<TaskSummary> queryTasks = taskQueryService.getTasksAssignedAsPotentialOwnerByStatus(userId, status, language);
        if (queryTasks.size() > 0) {
            lifeCycleManager.taskOperation(Operation.Claim, queryTasks.get(0).getId(), userId, null, null, null);
        } else {
            //log.log(Level.SEVERE, " No Task Available to Assign");
        }
    }

    public void claimNextAvailable(String userId, List<String> groupIds, String language) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void complete(long taskId, String userId, Map<String, Object> data) {
        lifeCycleManager.taskOperation(Operation.Complete, taskId, userId, null, data, null);
    }

    public void delegate(long taskId, String userId, String targetUserId) {
        lifeCycleManager.taskOperation(Operation.Delegate, taskId, userId, targetUserId, null, null);
    }

    public void deleteFault(long taskId, String userId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteOutput(long taskId, String userId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void exit(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Exit, taskId, userId, null, null, null);
    }

    public void fail(long taskId, String userId, Map<String, Object> faultData) {
        lifeCycleManager.taskOperation(Operation.Fail, taskId, userId, null, faultData, null);
    }

    public void forward(long taskId, String userId, String targetEntityId) {
        lifeCycleManager.taskOperation(Operation.Forward, taskId, userId, targetEntityId, null, null);
    }

    public void release(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Release, taskId, userId, null, null, null);
    }

    public void remove(long taskId, String userId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resume(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Resume, taskId, userId, null, null, null);
    }

    public void setFault(long taskId, String userId, FaultData fault) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setOutput(long taskId, String userId, Object outputContentData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPriority(long taskId, int priority) {
        Task task = pm.find(Task.class, taskId);
        task.setPriority(priority);
    }

    public void setTaskNames(long taskId, List<I18NText> taskNames) {
        Task task = pm.find(Task.class, taskId);
        task.setNames(taskNames);
    }

    public void skip(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Skip, taskId, userId, null, null, null);
    }

    public void start(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Start, taskId, userId, null, null, null);
    }

    public void stop(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Stop, taskId, userId, null, null, null);
    }

    public void suspend(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Suspend, taskId, userId, null, null, null);
    }

    //@TODO: WHY THE HELL THIS IS NOT AN OPERATION???
    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        doCallbackOperationForPotentialOwners(potentialOwners);
        if(lifeCycleManager instanceof UserGroupLifeCycleManagerDecorator){
            ((MVELLifeCycleManager)((UserGroupLifeCycleManagerDecorator) lifeCycleManager).getManager()).nominate(taskId, userId, potentialOwners);
        } else if(lifeCycleManager instanceof MVELLifeCycleManager){
            ((MVELLifeCycleManager)lifeCycleManager).nominate(taskId, userId, potentialOwners);
        }
    }

    public void setSubTaskStrategy(long taskId, SubTasksStrategy strategy) {
        Task task = pm.find(Task.class, taskId);
        task.setSubTaskStrategy(strategy);
    }

    public void setExpirationDate(long taskId, Date date) {
        Task task = pm.find(Task.class, taskId);
        task.getTaskData().setExpirationTime(date);
    }

    public void setDescriptions(long taskId, List<I18NText> descriptions) {
        Task task = pm.find(Task.class, taskId);
        task.setDescriptions(descriptions);
    }

    public void setSkipable(long taskId, boolean skipable) {
        Task task = pm.find(Task.class, taskId);
        task.getTaskData().setSkipable(skipable);
    }

    private List<String> doUserGroupCallbackOperation(String userId, List<String> groupIds) {

        doCallbackUserOperation(userId);
        doCallbackGroupsOperation(userId, groupIds);

        return userGroupCallback.getGroupsForUser(userId, groupIds, null);

    }

    private boolean doCallbackUserOperation(String userId) {

        if (userId != null && userGroupCallback.existsUser(userId)) {
            addUserFromCallbackOperation(userId);
            return true;
        }
        return false;

    }

    private boolean doCallbackGroupOperation(String groupId) {

        if (groupId != null && userGroupCallback.existsGroup(groupId)) {
            addGroupFromCallbackOperation(groupId);
            return true;
        }
        return false;

    }

    private void addUserFromCallbackOperation(String userId) {
        try {
            boolean userExists = pm.find(User.class, userId) != null;
            if (!StringUtils.isEmpty(userId) && !userExists) {
                User user = new User(userId);
                pm.persist(user);
            }
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Unable to add user " + userId);
        }
    }
   
    // ALL THIS CODE SHOULD NOT BE HERE>> THIS IS PLACED HERE TO DEMONSTRATE THAT IS WRONG
    @Inject
    private UserGroupCallback userGroupCallback;
    private Map<String, Boolean> userGroupsMap = new HashMap<String, Boolean>();
    
    private void doCallbackGroupsOperation(String userId, List<String> groupIds) {

        if (userId != null) {

            if (groupIds != null && groupIds.size() > 0) {

                List<String> userGroups = userGroupCallback.getGroupsForUser(userId, groupIds, null);
                for (String groupId : groupIds) {

                    if (userGroupCallback.existsGroup(groupId) && userGroups != null && userGroups.contains(groupId)) {
                        addGroupFromCallbackOperation(groupId);
                    }
                }
            } else {
                if (!(userGroupsMap.containsKey(userId) && userGroupsMap.get(userId).booleanValue())) {
                    List<String> userGroups = userGroupCallback.getGroupsForUser(userId, null, null);
                    if (userGroups != null && userGroups.size() > 0) {
                        for (String group : userGroups) {
                            addGroupFromCallbackOperation(group);
                        }
                        userGroupsMap.put(userId, true);
                    }
                }
            }
        } else {
            if (groupIds != null) {
                for (String groupId : groupIds) {
                    addGroupFromCallbackOperation(groupId);
                }
            }
        }

    }

    private void addGroupFromCallbackOperation(String groupId) {
        try {
            boolean groupExists = pm.find(Group.class, groupId) != null;
            if (!StringUtils.isEmpty(groupId) && !groupExists) {
                Group group = new Group(groupId);
                pm.persist(group);
            }
        } catch (Throwable t) {
            logger.log(Level.WARNING, "UserGroupCallback has not been registered.");
        }
    }

    private void doCallbackOperationForPeopleAssignments(PeopleAssignments assignments) {

        List<OrganizationalEntity> nonExistingEntities = new ArrayList<OrganizationalEntity>();

        if (assignments != null) {
            List<OrganizationalEntity> businessAdmins = assignments.getBusinessAdministrators();
            if (businessAdmins != null) {
                for (OrganizationalEntity admin : businessAdmins) {
                    if (admin instanceof User) {
                        boolean userExists = doCallbackUserOperation(admin.getId());
                        if (!userExists) {
                            nonExistingEntities.add(admin);
                        }
                    }
                    if (admin instanceof Group) {
                        boolean groupExists = doCallbackGroupOperation(admin.getId());
                        if (!groupExists) {
                            nonExistingEntities.add(admin);
                        }
                    }
                }

                if (!nonExistingEntities.isEmpty()) {
                    businessAdmins.removeAll(nonExistingEntities);
                    nonExistingEntities.clear();
                }
            }

            if (businessAdmins == null || businessAdmins.isEmpty()) {
                // throw an exception as it should not be allowed to create task without administrator
                throw new CannotAddTaskException("There are no known Business Administrators, task cannot be created according to WS-HT specification");
            }

            List<OrganizationalEntity> potentialOwners = assignments.getPotentialOwners();
            if (potentialOwners != null) {
                for (OrganizationalEntity powner : potentialOwners) {
                    if (powner instanceof User) {
                        boolean userExists = doCallbackUserOperation(powner.getId());
                        if (!userExists) {
                            nonExistingEntities.add(powner);
                        }
                    }
                    if (powner instanceof Group) {
                        boolean groupExists = doCallbackGroupOperation(powner.getId());
                        if (!groupExists) {
                            nonExistingEntities.add(powner);
                        }
                    }
                }
                if (!nonExistingEntities.isEmpty()) {
                    potentialOwners.removeAll(nonExistingEntities);
                    nonExistingEntities.clear();
                }
            }

            if (assignments.getTaskInitiator() != null && assignments.getTaskInitiator().getId() != null) {
                doCallbackUserOperation(assignments.getTaskInitiator().getId());
            }

            List<OrganizationalEntity> excludedOwners = assignments.getExcludedOwners();
            if (excludedOwners != null) {
                for (OrganizationalEntity exowner : excludedOwners) {
                    if (exowner instanceof User) {
                        boolean userExists = doCallbackUserOperation(exowner.getId());
                        if (!userExists) {
                            nonExistingEntities.add(exowner);
                        }
                    }
                    if (exowner instanceof Group) {
                        boolean groupExists = doCallbackGroupOperation(exowner.getId());
                        if (!groupExists) {
                            nonExistingEntities.add(exowner);
                        }
                    }
                }
                if (!nonExistingEntities.isEmpty()) {
                    excludedOwners.removeAll(nonExistingEntities);
                    nonExistingEntities.clear();
                }
            }

            List<OrganizationalEntity> recipients = assignments.getRecipients();
            if (recipients != null) {
                for (OrganizationalEntity recipient : recipients) {
                    if (recipient instanceof User) {
                        boolean userExists = doCallbackUserOperation(recipient.getId());
                        if (!userExists) {
                            nonExistingEntities.add(recipient);
                        }
                    }
                    if (recipient instanceof Group) {
                        boolean groupExists = doCallbackGroupOperation(recipient.getId());
                        if (!groupExists) {
                            nonExistingEntities.add(recipient);
                        }
                    }
                }
                if (!nonExistingEntities.isEmpty()) {
                    recipients.removeAll(nonExistingEntities);
                    nonExistingEntities.clear();
                }
            }

            List<OrganizationalEntity> stakeholders = assignments.getTaskStakeholders();
            if (stakeholders != null) {
                for (OrganizationalEntity stakeholder : stakeholders) {
                    if (stakeholder instanceof User) {
                        boolean userExists = doCallbackUserOperation(stakeholder.getId());
                        if (!userExists) {
                            nonExistingEntities.add(stakeholder);
                        }
                    }
                    if (stakeholder instanceof Group) {
                        boolean groupExists = doCallbackGroupOperation(stakeholder.getId());
                        if (!groupExists) {
                            nonExistingEntities.add(stakeholder);
                        }
                    }
                }
                if (!nonExistingEntities.isEmpty()) {
                    stakeholders.removeAll(nonExistingEntities);
                    nonExistingEntities.clear();
                }
            }
        }


    }

    private void doCallbackOperationForTaskDeadlines(Deadlines deadlines) {
        if (deadlines != null) {
            if (deadlines.getStartDeadlines() != null) {
                List<Deadline> startDeadlines = deadlines.getStartDeadlines();
                for (Deadline startDeadline : startDeadlines) {
                    List<Escalation> escalations = startDeadline.getEscalations();
                    if (escalations != null) {
                        for (Escalation escalation : escalations) {
                            List<Notification> notifications = escalation.getNotifications();
                            List<Reassignment> ressignments = escalation.getReassignments();
                            if (notifications != null) {
                                for (Notification notification : notifications) {
                                    List<OrganizationalEntity> recipients = notification.getRecipients();
                                    if (recipients != null) {
                                        for (OrganizationalEntity recipient : recipients) {
                                            if (recipient instanceof User) {
                                                doCallbackUserOperation(recipient.getId());
                                            }
                                            if (recipient instanceof Group) {
                                                doCallbackGroupOperation(recipient.getId());
                                            }
                                        }
                                    }
                                    List<OrganizationalEntity> administrators = notification.getBusinessAdministrators();
                                    if (administrators != null) {
                                        for (OrganizationalEntity administrator : administrators) {
                                            if (administrator instanceof User) {
                                                doCallbackUserOperation(administrator.getId());
                                            }
                                            if (administrator instanceof Group) {
                                                doCallbackGroupOperation(administrator.getId());
                                            }
                                        }
                                    }
                                }
                            }
                            if (ressignments != null) {
                                for (Reassignment reassignment : ressignments) {
                                    List<OrganizationalEntity> potentialOwners = reassignment.getPotentialOwners();
                                    if (potentialOwners != null) {
                                        for (OrganizationalEntity potentialOwner : potentialOwners) {
                                            if (potentialOwner instanceof User) {
                                                doCallbackUserOperation(potentialOwner.getId());
                                            }
                                            if (potentialOwner instanceof Group) {
                                                doCallbackGroupOperation(potentialOwner.getId());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (deadlines.getEndDeadlines() != null) {
                List<Deadline> endDeadlines = deadlines.getEndDeadlines();
                for (Deadline endDeadline : endDeadlines) {
                    List<Escalation> escalations = endDeadline.getEscalations();
                    if (escalations != null) {
                        for (Escalation escalation : escalations) {
                            List<Notification> notifications = escalation.getNotifications();
                            List<Reassignment> ressignments = escalation.getReassignments();
                            if (notifications != null) {
                                for (Notification notification : notifications) {
                                    List<OrganizationalEntity> recipients = notification.getRecipients();
                                    if (recipients != null) {
                                        for (OrganizationalEntity recipient : recipients) {
                                            if (recipient instanceof User) {
                                                doCallbackUserOperation(recipient.getId());
                                            }
                                            if (recipient instanceof Group) {
                                                doCallbackGroupOperation(recipient.getId());
                                            }
                                        }
                                    }
                                    List<OrganizationalEntity> administrators = notification.getBusinessAdministrators();
                                    if (administrators != null) {
                                        for (OrganizationalEntity administrator : administrators) {
                                            if (administrator instanceof User) {
                                                doCallbackUserOperation(administrator.getId());
                                            }
                                            if (administrator instanceof Group) {
                                                doCallbackGroupOperation(administrator.getId());
                                            }
                                        }
                                    }
                                }
                            }
                            if (ressignments != null) {
                                for (Reassignment reassignment : ressignments) {
                                    List<OrganizationalEntity> potentialOwners = reassignment.getPotentialOwners();
                                    if (potentialOwners != null) {
                                        for (OrganizationalEntity potentialOwner : potentialOwners) {
                                            if (potentialOwner instanceof User) {
                                                doCallbackUserOperation(potentialOwner.getId());
                                            }
                                            if (potentialOwner instanceof Group) {
                                                doCallbackGroupOperation(potentialOwner.getId());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void doCallbackOperationForTaskData(TaskData data) {

        if (data.getActualOwner() != null) {
            boolean userExists = doCallbackUserOperation(data.getActualOwner().getId());
            if (!userExists) {
                // remove it from the task to avoid foreign key constraint exception
                data.setActualOwner(null);
                data.setStatus(Status.Ready);
            }
        }

        if (data.getCreatedBy() != null) {
            boolean userExists = doCallbackUserOperation(data.getCreatedBy().getId());
            if (!userExists) {
                // remove it from the task to avoid foreign key constraint exception
                data.setCreatedBy(null);
            }
        }

    }

    private void doCallbackOperationForPotentialOwners(List<OrganizationalEntity> potentialOwners) {

        List<OrganizationalEntity> nonExistingEntities = new ArrayList<OrganizationalEntity>();

        for (OrganizationalEntity orgEntity : potentialOwners) {
            if (orgEntity instanceof User) {
                boolean userExists = doCallbackUserOperation(orgEntity.getId());
                if (!userExists) {
                    nonExistingEntities.add(orgEntity);
                }
            }
            if (orgEntity instanceof Group) {
                boolean groupExists = doCallbackGroupOperation(orgEntity.getId());
                if (!groupExists) {
                    nonExistingEntities.add(orgEntity);
                }
            }
        }
        if (!nonExistingEntities.isEmpty()) {
            potentialOwners.removeAll(nonExistingEntities);
        }

    }

    public int getPriority(long taskId) {
        Task task = pm.find(Task.class, taskId);
        return task.getPriority();
    }

    public Date getExpirationDate(long taskId) {
        Task task = pm.find(Task.class, taskId);
        return task.getTaskData().getExpirationTime();
    }

    public List<I18NText> getDescriptions(long taskId) {
        Task task = pm.find(Task.class, taskId);
        return task.getDescriptions();
    }

    public boolean isSkipable(long taskId) {
        Task task = pm.find(Task.class, taskId);
        return task.getTaskData().isSkipable();
    }

    public SubTasksStrategy getSubTaskStrategy(long taskId) {
        Task task = pm.find(Task.class, taskId);
        return task.getSubTaskStrategy();
    }
}
