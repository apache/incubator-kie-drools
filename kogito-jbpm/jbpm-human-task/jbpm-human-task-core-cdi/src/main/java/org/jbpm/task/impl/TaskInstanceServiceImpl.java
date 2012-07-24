/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.SSLEngineResult.Status;
import javax.persistence.EntityManager;
import org.jbpm.task.annotations.Local;
import org.jbpm.task.internals.lifecycle.LifeCycleManager;
import org.jbpm.task.internals.lifecycle.Mvel;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.Content;
import org.jbpm.task.ContentData;
import org.jbpm.task.FaultData;
import org.jbpm.task.Operation;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Task;
import org.jbpm.task.TaskDef;
import org.jbpm.task.User;
import org.jbpm.task.api.TaskDefService;
import org.jbpm.task.api.TaskInstanceService;
import org.jbpm.task.api.TaskQueryService;
import org.jbpm.task.impl.factories.TaskFactory;
import org.jbpm.task.internals.lifecycle.MVELLifeCycleManager;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.utils.ContentMarshallerHelper;

/**
 *
 * @author salaboy
 */
@Local
@Named
@Transactional
public class TaskInstanceServiceImpl implements TaskInstanceService {

    private @Inject
    Logger log;
    
    @Inject
    @Local
    private TaskDefService taskDefService;
    
    @Inject
    @Local
    private TaskQueryService taskQueryService;
    
    @Inject
    @Mvel
    private LifeCycleManager lifeCycleManager;
    @Inject
    private EntityManager em;

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
        //TODO: need to deal with the params for the content
        if (params != null) {
            ContentData contentData = ContentMarshallerHelper.marshal(params, null);
            Content content = new Content(contentData.getContent());
            em.persist(content);
            task.getTaskData().setDocument(content.getId(), contentData);
        }
        em.persist(task);
        return task.getId();
    }

    public long addTask(Task task, ContentData contentData) {
        //TODO: need to deal with the params for the content
        em.persist(task);
        if (contentData != null) {
            Content content = new Content(contentData.getContent());
            em.persist(content);
            task.getTaskData().setDocument(content.getId(), contentData);
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
        if(queryTasks.size() > 0){
            lifeCycleManager.taskOperation(Operation.Claim, queryTasks.get(0).getId(), userId, null, null, null );
        } else{
            log.log(Level.SEVERE, " No Task Available to Assign");
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

    public void setPriority(long taskId, String userId, int priority) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        ((MVELLifeCycleManager)lifeCycleManager).nominate(taskId, userId, potentialOwners);
    }
}
