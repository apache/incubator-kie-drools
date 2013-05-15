/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.task.deadlines;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.kie.internal.task.api.TaskDeadlinesService.DeadlineType;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.TaskQueryService;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.InternalTask;

/**
 *
 */
@Decorator
public class DeadlinesDecorator implements TaskInstanceService {

    @Delegate
    @Inject
    private TaskInstanceService instanceService;
    @Inject
    private TaskQueryService queryService;
    @Inject
    private TaskDeadlinesService deadlineService;
    @Inject 
    private JbpmServicesPersistenceManager pm;

    public DeadlinesDecorator() { 
    }

    public void setInstanceService(TaskInstanceService instanceService) {
        this.instanceService = instanceService;
    }

    public void setQueryService(TaskQueryService queryService) {
        this.queryService = queryService;
    }

    public void setDeadlineService(TaskDeadlinesService deadlineService) {
        this.deadlineService = deadlineService;
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }
    
    
    
    public long addTask(Task task, Map<String, Object> params) {
        long taskId = instanceService.addTask(task, params);
        scheduleDeadlinesForTask((InternalTask) task);
        return taskId;
    }

    public long addTask(Task task, ContentData data) {
        long taskId = instanceService.addTask(task, data);
        scheduleDeadlinesForTask((InternalTask) task);
        return taskId;
    }

    public void activate(long taskId, String userId) {
        instanceService.activate(taskId, userId);
    }

    public void claim(long taskId, String userId) {
        instanceService.claim(taskId, userId);
    }

    public void claim(long taskId, String userId, List<String> groupIds) {
        instanceService.claim(taskId, userId, groupIds);
    }

    public void claimNextAvailable(String userId, String language) {
        instanceService.claimNextAvailable(userId, language);
    }

    public void claimNextAvailable(String userId, List<String> groupIds, String language) {
        instanceService.claimNextAvailable(userId, groupIds, language);
    }

    public void complete(long taskId, String userId, Map<String, Object> data) {
        clearDeadlines(taskId, true, true);
        instanceService.complete(taskId, userId, data);
    }

    public void delegate(long taskId, String userId, String targetUserId) {
        instanceService.delegate(taskId, userId, targetUserId);
    }

    public void deleteFault(long taskId, String userId) {
        instanceService.deleteFault(taskId, userId);
    }

    public void deleteOutput(long taskId, String userId) {
        instanceService.deleteOutput(taskId, userId);
    }

    public void exit(long taskId, String userId) {
        instanceService.exit(taskId, userId);
        clearDeadlines(taskId, true, true);
    }

    public void fail(long taskId, String userId, Map<String, Object> faultData) {
        clearDeadlines(taskId, true, true);
        instanceService.fail(taskId, userId, faultData);
        
    }

    public void forward(long taskId, String userId, String targetEntityId) {
        instanceService.forward(taskId, userId, targetEntityId);
    }

    public void release(long taskId, String userId) {
        instanceService.release(taskId, userId);
    }

    public void remove(long taskId, String userId) {
        instanceService.remove(taskId, userId);
    }

    public void resume(long taskId, String userId) {
        instanceService.resume(taskId, userId);
    }

    public void setFault(long taskId, String userId, FaultData fault) {
        instanceService.setFault(taskId, userId, fault);
    }

    public void setOutput(long taskId, String userId, Object outputContentData) {
        instanceService.setOutput(taskId, userId, outputContentData);
    }

    public void setPriority(long taskId, int priority) {
        instanceService.setPriority(taskId, priority);
    }

    public void skip(long taskId, String userId) {
        instanceService.skip(taskId, userId);
        clearDeadlines(taskId, true, true);
    }

    public void start(long taskId, String userId) {
        clearDeadlines(taskId, true, false);
        instanceService.start(taskId, userId);
        
    }

    public void stop(long taskId, String userId) {
        instanceService.stop(taskId, userId);
    }

    public void suspend(long taskId, String userId) {
        instanceService.suspend(taskId, userId);
    }

    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        instanceService.nominate(taskId, userId, potentialOwners);
    }

    private void scheduleDeadlinesForTask(final InternalTask task) {
        final long now = System.currentTimeMillis();

        Deadlines deadlines = task.getDeadlines();
        
        if (deadlines != null) {
            final List<? extends Deadline> startDeadlines = deadlines.getStartDeadlines();
    
            if (startDeadlines != null) {
                scheduleDeadlines(startDeadlines, now, task.getId(), DeadlineType.START);
            }
    
            final List<? extends Deadline> endDeadlines = deadlines.getEndDeadlines();
    
            if (endDeadlines != null) {
                scheduleDeadlines(endDeadlines, now, task.getId(), DeadlineType.END);
            }
        }
    }

    private void scheduleDeadlines(final List<? extends Deadline> deadlines, final long now, final long taskId, DeadlineType type) {
        for (Deadline deadline : deadlines) {
            if (!deadline.isEscalated()) {
                // only escalate when true - typically this would only be true
                // if the user is requested that the notification should never be escalated
                Date date = deadline.getDate();
                deadlineService.schedule(taskId, deadline.getId(), date.getTime() - now, type);
            }
        }
    }

    private void clearDeadlines(final long taskId, boolean removeStart, boolean removeEnd) {
        InternalTask task = (InternalTask) queryService.getTaskInstanceById(taskId);
        if (task == null || task.getDeadlines() == null) {
            return;
        }

        Iterator<? extends Deadline> it = null;

        if (removeStart) {
            if (task.getDeadlines().getStartDeadlines() != null) {
                it = task.getDeadlines().getStartDeadlines().iterator();
                while (it.hasNext()) {
                    deadlineService.unschedule(taskId, DeadlineType.START);
                    pm.remove(it.next());
                    it.remove();
                }
            }
        }

        if (removeEnd) {
            if (task.getDeadlines().getEndDeadlines() != null) {
                it = task.getDeadlines().getEndDeadlines().iterator();
                while (it.hasNext()) {
                    deadlineService.unschedule(taskId, DeadlineType.END);
                    pm.remove(it.next());
                    it.remove();
                }

            }
        }
    }

    public void setExpirationDate(long taskId, Date date) {
        instanceService.setExpirationDate(taskId, date);
    }

    public void setDescriptions(long taskId, List<I18NText> descriptions) {
        instanceService.setDescriptions(taskId, descriptions);
    }

    public void setSkipable(long taskId, boolean skipable) {
        instanceService.setSkipable(taskId, skipable);
    }

    public void setSubTaskStrategy(long taskId, org.kie.internal.task.api.model.SubTasksStrategy strategy) {
        instanceService.setSubTaskStrategy(taskId, strategy);
    }

    public int getPriority(long taskId) {
        return instanceService.getPriority(taskId);
    }

    public Date getExpirationDate(long taskId) {
        return instanceService.getExpirationDate(taskId);
    }

    public List<I18NText> getDescriptions(long taskId) {
        return instanceService.getDescriptions(taskId);
    }

    public boolean isSkipable(long taskId) {
        return instanceService.isSkipable(taskId);
    }

    public org.kie.internal.task.api.model.SubTasksStrategy getSubTaskStrategy(long taskId) {
        return instanceService.getSubTaskStrategy(taskId);
    }

    public void setTaskNames(long taskId, List<I18NText> taskNames) {
        instanceService.setTaskNames(taskId, taskNames);
    }

}
