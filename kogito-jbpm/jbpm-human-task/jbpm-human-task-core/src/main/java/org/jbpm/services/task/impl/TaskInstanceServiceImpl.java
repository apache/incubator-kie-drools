/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.commands.TaskContext;
import org.jbpm.services.task.events.TaskEventSupport;
import org.jbpm.services.task.internals.lifecycle.LifeCycleManager;
import org.jbpm.services.task.utils.ClassUtil;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.command.Command;
import org.kie.api.runtime.Environment;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.InternalContent;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;
import org.kie.internal.task.api.model.Operation;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class TaskInstanceServiceImpl implements TaskInstanceService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskInstanceServiceImpl.class);
    
    private LifeCycleManager lifeCycleManager;
   
    private TaskPersistenceContext persistenceContext;    
    private TaskEventSupport taskEventSupport;
    private Environment environment;

    public TaskInstanceServiceImpl() {
    }

    public TaskInstanceServiceImpl(TaskPersistenceContext persistenceContext,
    		LifeCycleManager lifeCycleManager, TaskEventSupport taskEventSupport,
    		Environment environment) {
    	this.persistenceContext = persistenceContext;
    	this.lifeCycleManager = lifeCycleManager;
    	this.taskEventSupport = taskEventSupport;
    	this.environment = environment;
    }

    public void setLifeCycleManager(LifeCycleManager lifeCycleManager) {
        this.lifeCycleManager = lifeCycleManager;
    }

    public void setTaskEventSupport(TaskEventSupport taskEventSupport) {
        this.taskEventSupport = taskEventSupport;
    }

    
    public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

   
    public long addTask(Task task, Map<String, Object> params) {    	
    	taskEventSupport.fireBeforeTaskAdded(task, persistenceContext);
    	
    	if (params != null) {
			ContentData contentData = ContentMarshallerHelper.marshal(params, environment);
			Content content = TaskModelProvider.getFactory().newContent();
			((InternalContent) content).setContent(contentData.getContent());
			persistenceContext.persistContent(content);
			((InternalTaskData) task.getTaskData()).setDocument(
					content.getId(), contentData);
		}

		persistenceContext.persistTask(task);
		taskEventSupport.fireAfterTaskAdded(task, persistenceContext);
		return task.getId();
    }

    public long addTask(Task task, ContentData contentData) {
    	taskEventSupport.fireBeforeTaskAdded(task, persistenceContext);   	
        if (contentData != null) {
            Content content = TaskModelProvider.getFactory().newContent();
            ((InternalContent) content).setContent(contentData.getContent());
            persistenceContext.persistContent(content);
            ((InternalTaskData) task.getTaskData()).setDocument(content.getId(), contentData);
        }
        
        persistenceContext.persistTask(task);
        taskEventSupport.fireAfterTaskAdded(task, persistenceContext);
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

    public void claimNextAvailable(String userId) {
        List<Status> status = new ArrayList<Status>();
        status.add(Status.Ready);
        List<TaskSummary> queryTasks = persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByStatus", 
                persistenceContext.addParametersToMap("userId", userId, "status", status),
                ClassUtil.<List<TaskSummary>>castClass(List.class));;
        if (queryTasks.size() > 0) {
            lifeCycleManager.taskOperation(Operation.Claim, queryTasks.get(0).getId(), userId, null, null, null);
        } else {
            //log.log(Level.SEVERE, " No Task Available to Assign");
        }
    }

    public void claimNextAvailable(String userId, List<String> groupIds) {
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
        Task task = persistenceContext.findTask(taskId);
        ((InternalTask) task).setPriority(priority);
    }

    public void setTaskNames(long taskId, List<I18NText> taskNames) {
        Task task = persistenceContext.findTask(taskId);
        ((InternalTask) task).setNames(taskNames);
        ((InternalTask) task).setName(taskNames.get(0).getText());
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

    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {      
        lifeCycleManager.taskOperation(Operation.Nominate, taskId, userId, null, null, null, 
        		potentialOwners.toArray(new OrganizationalEntity[potentialOwners.size()]));
    }

    public void setSubTaskStrategy(long taskId, SubTasksStrategy strategy) {
        Task task = persistenceContext.findTask(taskId);
        ((InternalTask) task).setSubTaskStrategy(strategy);
    }

    public void setExpirationDate(long taskId, Date date) {
        Task task = persistenceContext.findTask(taskId);
        ((InternalTaskData) task.getTaskData()).setExpirationTime(date);
    }

    public void setDescriptions(long taskId, List<I18NText> descriptions) {
        Task task = persistenceContext.findTask(taskId);
        ((InternalTask) task).setDescriptions(descriptions);
        ((InternalTask) task).setDescription(descriptions.get(0).getText());
    }

    public void setSkipable(long taskId, boolean skipable) {
        Task task = persistenceContext.findTask(taskId);
        ((InternalTaskData) task.getTaskData()).setSkipable(skipable);
    }

    public int getPriority(long taskId) {
        Task task = persistenceContext.findTask(taskId);
        return task.getPriority();
    }

    public Date getExpirationDate(long taskId) {
        Task task = persistenceContext.findTask(taskId);
        return task.getTaskData().getExpirationTime();
    }

    public List<I18NText> getDescriptions(long taskId) {
        Task task = persistenceContext.findTask(taskId);
        return (List<I18NText>) task.getDescriptions();
    }

    public boolean isSkipable(long taskId) {
        Task task = persistenceContext.findTask(taskId);
        return task.getTaskData().isSkipable();
    }

    public SubTasksStrategy getSubTaskStrategy(long taskId) {
        Task task = persistenceContext.findTask(taskId);
        return ((InternalTask) task).getSubTaskStrategy();
    }
    
    public <T> T execute(Command<T> command) {
        return (T) ((TaskCommand) command).execute( new TaskContext() );
    }

    @Override
    public void setName(long taskId, String name) {
        Task task = persistenceContext.findTask(taskId);
        ((InternalTask) task).setName(name);
    }

    @Override
    public void setDescription(long taskId, String description) {
        Task task = persistenceContext.findTask(taskId);
        ((InternalTask) task).setDescription(description);
    }

    @Override
    public void setSubject(long taskId, String subject) {
        Task task = persistenceContext.findTask(taskId);
        ((InternalTask) task).setSubject(subject);
    }
    

}
