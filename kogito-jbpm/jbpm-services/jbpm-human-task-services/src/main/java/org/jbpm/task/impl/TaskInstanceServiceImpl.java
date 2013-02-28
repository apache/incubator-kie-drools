/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.Content;
import org.jbpm.task.ContentData;
import org.jbpm.task.FaultData;
import org.jbpm.task.I18NText;
import org.jbpm.task.Operation;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.SubTasksStrategy;
import org.jbpm.task.Task;
import org.jbpm.task.TaskDef;
import org.jbpm.task.annotations.Mvel;
import org.jbpm.task.api.TaskDefService;
import org.jbpm.task.api.TaskInstanceService;
import org.jbpm.task.api.TaskQueryService;
import org.jbpm.task.events.AfterTaskAddedEvent;
import org.jbpm.task.impl.factories.TaskFactory;
import org.jbpm.task.internals.lifecycle.LifeCycleManager;
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
    private TaskDefService taskDefService;
    @Inject
    private TaskQueryService taskQueryService;
    @Inject
    @Mvel
    private LifeCycleManager lifeCycleManager;
    @Inject
    private EntityManager em;
    @Inject
    private Logger logger;
    @Inject
    private Event<Task> taskEvents;

    public TaskInstanceServiceImpl() {
    }

    public TaskInstanceServiceImpl(TaskDefService taskDefService, LifeCycleManager lifeCycleManager) {
        this.taskDefService = taskDefService;
        this.lifeCycleManager = lifeCycleManager;
    }

    public long newTask(String name, Map<String, Object> params) {
        TaskDef taskDef = taskDefService.getTaskDefById(name);

        Task task = TaskFactory.newTask(taskDef);
        em.persist(task);
        if (params != null) {
            ContentData contentData = ContentMarshallerHelper.marshal(params, null);
            Content content = new Content(contentData.getContent());
            em.persist(content);
            task.getTaskData().setDocument(content.getId(), contentData);
        }

        return task.getId();

    }

    public long newTask(TaskDef taskDef, Map<String, Object> params) {
        return newTask(taskDef, params, true);
    }

    public long newTask(TaskDef taskDef, Map<String, Object> params, boolean deploy) {
        //TODO: need to deal with the params for the content
        if (deploy) {
            taskDefService.deployTaskDef(taskDef);
        }
        Task task = TaskFactory.newTask(taskDef);
        em.persist(task);
        if (params != null) {
            ContentData contentData = ContentMarshallerHelper.marshal(params, null);
            Content content = new Content(contentData.getContent());
            em.persist(content);
            task.getTaskData().setDocument(content.getId(), contentData);
        }

        return task.getId();
    }

    public long addTask(Task task, Map<String, Object> params) {
        if (params != null) {
            ContentData contentData = ContentMarshallerHelper.marshal(params, null);
            Content content = new Content(contentData.getContent());
            em.persist(content);
            task.getTaskData().setDocument(content.getId(), contentData);
        }
        em.persist(task);
        taskEvents.select(new AnnotationLiteral<AfterTaskAddedEvent>() {}).fire(task);
        return task.getId();
    }

    public long addTask(Task task, ContentData contentData) {
        em.persist(task);
        if (contentData != null) {
            Content content = new Content(contentData.getContent());
            em.persist(content);
            task.getTaskData().setDocument(content.getId(), contentData);
        }
        taskEvents.select(new AnnotationLiteral<AfterTaskAddedEvent>() {}).fire(task);
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
        Task task = em.find(Task.class, taskId);
        task.setPriority(priority);
    }

    public void setTaskNames(long taskId, List<I18NText> taskNames) {
        Task task = em.find(Task.class, taskId);
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
        ((MVELLifeCycleManager) lifeCycleManager).nominate(taskId, userId, potentialOwners);
    }

    public void setSubTaskStrategy(long taskId, SubTasksStrategy strategy) {
        Task task = em.find(Task.class, taskId);
        task.setSubTaskStrategy(strategy);
    }

    public void setExpirationDate(long taskId, Date date) {
        Task task = em.find(Task.class, taskId);
        task.getTaskData().setExpirationTime(date);
    }

    public void setDescriptions(long taskId, List<I18NText> descriptions) {
        Task task = em.find(Task.class, taskId);
        task.setDescriptions(descriptions);
    }

    public void setSkipable(long taskId, boolean skipable) {
        Task task = em.find(Task.class, taskId);
        task.getTaskData().setSkipable(skipable);
    }

    public int getPriority(long taskId) {
        Task task = em.find(Task.class, taskId);
        return task.getPriority();
    }

    public Date getExpirationDate(long taskId) {
        Task task = em.find(Task.class, taskId);
        return task.getTaskData().getExpirationTime();
    }

    public List<I18NText> getDescriptions(long taskId) {
        Task task = em.find(Task.class, taskId);
        return task.getDescriptions();
    }

    public boolean isSkipable(long taskId) {
        Task task = em.find(Task.class, taskId);
        return task.getTaskData().isSkipable();
    }

    public SubTasksStrategy getSubTaskStrategy(long taskId) {
        Task task = em.find(Task.class, taskId);
        return task.getSubTaskStrategy();
    }
}
