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
package org.jbpm.task.deadlines;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.jbpm.task.ContentData;
import org.jbpm.task.Deadline;
import org.jbpm.task.FaultData;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Task;
import org.jbpm.task.TaskDef;
import org.jbpm.task.api.TaskDeadlinesService;
import org.jbpm.task.api.TaskInstanceService;
import org.jbpm.task.api.TaskQueryService;

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
    private EntityManager em;

    public long newTask(String name, Map<String, Object> params) {
        return instanceService.newTask(name, params);
    }

    public long newTask(TaskDef def, Map<String, Object> params) {
        return instanceService.newTask(def, params);
    }

    public long newTask(TaskDef def, Map<String, Object> params, boolean deploy) {
        return instanceService.newTask(def, params, deploy);
    }

    public long addTask(Task task, Map<String, Object> params) {
        long taskId = instanceService.addTask(task, params);
        scheduleDeadlinesForTask(task);
        return taskId;
    }

    public long addTask(Task task, ContentData data) {
        long taskId = instanceService.addTask(task, data);
        scheduleDeadlinesForTask(task);
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
        instanceService.complete(taskId, userId, data);
        clearDeadlines(taskId);
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
        clearDeadlines(taskId);
    }

    public void fail(long taskId, String userId, Map<String, Object> faultData) {
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

    public void setPriority(long taskId, String userId, int priority) {
        instanceService.setPriority(taskId, userId, priority);
    }

    public void skip(long taskId, String userId) {
        instanceService.skip(taskId, userId);
        clearDeadlines(taskId);
    }

    public void start(long taskId, String userId) {
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

    private void scheduleDeadlinesForTask(final Task task) {
        final long now = System.currentTimeMillis();

        final List<Deadline> startDeadlines = task.getDeadlines().getStartDeadlines();

        if (startDeadlines != null) {
            scheduleDeadlines(startDeadlines, now, task.getId());
        }

        final List<Deadline> endDeadlines = task.getDeadlines().getEndDeadlines();

        if (endDeadlines != null) {
            scheduleDeadlines(endDeadlines, now, task.getId());
        }
    }

    private void scheduleDeadlines(final List<Deadline> deadlines, final long now, final long taskId) {
        for (Deadline deadline : deadlines) {
            if (!deadline.isEscalated()) {
                // only escalate when true - typically this would only be true
                // if the user is requested that the notification should never be escalated
                Date date = deadline.getDate();
                deadlineService.schedule(taskId, deadline.getId(), date.getTime() - now);
            }
        }
    }

    private void clearDeadlines(final long taskId) {
        Task task = queryService.getTaskInstanceById(taskId);
        if (task.getDeadlines() == null) {
            return;
        }

        Iterator<Deadline> it = null;
        if (task.getDeadlines().getStartDeadlines() != null) {
            it = task.getDeadlines().getStartDeadlines().iterator();
            while (it.hasNext()) {
                em.remove(it.next());
                it.remove();
            }
        }

        if (task.getDeadlines().getEndDeadlines() != null) {
            it = task.getDeadlines().getEndDeadlines().iterator();
            while (it.hasNext()) {
                em.remove(it.next());
                it.remove();
            }
        }
    }
}
