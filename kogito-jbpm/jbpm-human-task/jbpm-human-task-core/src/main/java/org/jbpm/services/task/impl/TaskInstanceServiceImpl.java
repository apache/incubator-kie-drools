/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.drools.core.command.impl.FixedKnowledgeCommandContext;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.annotations.Mvel;
import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.commands.TaskContext;
import org.jbpm.services.task.events.AfterTaskAddedEvent;
import org.jbpm.services.task.identity.UserGroupLifeCycleManagerDecorator;
import org.jbpm.services.task.impl.model.ContentDataImpl;
import org.jbpm.services.task.impl.model.ContentImpl;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.jbpm.services.task.internals.lifecycle.LifeCycleManager;
import org.jbpm.services.task.internals.lifecycle.MVELLifeCycleManager;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.TaskQueryService;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.InternalTaskData;
import org.kie.internal.task.api.model.Operation;
import org.kie.internal.task.api.model.SubTasksStrategy;

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

    
    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }

   
    public long addTask(Task task, Map<String, Object> params) {
         if (params != null) {
            ContentDataImpl contentData = ContentMarshallerHelper.marshal(params, null);
            ContentImpl content = new ContentImpl(contentData.getContent());
            pm.persist(content);
            ((InternalTaskData) task.getTaskData()).setDocument(content.getId(), contentData);
        }
         
        pm.persist(task);
        if(taskEvents != null){
            taskEvents.select(new AnnotationLiteral<AfterTaskAddedEvent>() {}).fire(task);
        }
        return task.getId();
    }

    public long addTask(Task task, ContentData contentData) {
        pm.persist(task);

        if (contentData != null) {
            ContentImpl content = new ContentImpl(contentData.getContent());
            pm.persist(content);
            ((InternalTaskData) task.getTaskData()).setDocument(content.getId(), contentData);
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
        List<Status> status = new ArrayList<Status>();
        status.add(Status.Ready);
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
        TaskImpl task = pm.find(TaskImpl.class, taskId);
        task.setPriority(priority);
    }

    public void setTaskNames(long taskId, List<I18NText> taskNames) {
        TaskImpl task = pm.find(TaskImpl.class, taskId);
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
        if(lifeCycleManager instanceof UserGroupLifeCycleManagerDecorator){
            ((MVELLifeCycleManager)((UserGroupLifeCycleManagerDecorator) lifeCycleManager).getManager()).nominate(taskId, userId, potentialOwners);
        } else if(lifeCycleManager instanceof MVELLifeCycleManager){
            ((MVELLifeCycleManager)lifeCycleManager).nominate(taskId, userId, potentialOwners);
        }

    }

    public void setSubTaskStrategy(long taskId, SubTasksStrategy strategy) {
        TaskImpl task = pm.find(TaskImpl.class, taskId);
        task.setSubTaskStrategy(strategy);
    }

    public void setExpirationDate(long taskId, Date date) {
        TaskImpl task = pm.find(TaskImpl.class, taskId);
        ((InternalTaskData) task.getTaskData()).setExpirationTime(date);
    }

    public void setDescriptions(long taskId, List<I18NText> descriptions) {
        TaskImpl task = pm.find(TaskImpl.class, taskId);
        task.setDescriptions(descriptions);
    }

    public void setSkipable(long taskId, boolean skipable) {
        TaskImpl task = pm.find(TaskImpl.class, taskId);
        ((InternalTaskData) task.getTaskData()).setSkipable(skipable);
    }

    public int getPriority(long taskId) {
        TaskImpl task = pm.find(TaskImpl.class, taskId);
        return task.getPriority();
    }

    public Date getExpirationDate(long taskId) {
        TaskImpl task = pm.find(TaskImpl.class, taskId);
        return task.getTaskData().getExpirationTime();
    }

    public List<I18NText> getDescriptions(long taskId) {
        TaskImpl task = pm.find(TaskImpl.class, taskId);
        return (List<I18NText>) task.getDescriptions();
    }

    public boolean isSkipable(long taskId) {
        TaskImpl task = pm.find(TaskImpl.class, taskId);
        return task.getTaskData().isSkipable();
    }

    public SubTasksStrategy getSubTaskStrategy(long taskId) {
        TaskImpl task = pm.find(TaskImpl.class, taskId);
        return task.getSubTaskStrategy();
    }
    
    public <T> T execute(Command<T> command) {
        return (T) ((TaskCommand) command).execute( new TaskContext() );
    }
}
