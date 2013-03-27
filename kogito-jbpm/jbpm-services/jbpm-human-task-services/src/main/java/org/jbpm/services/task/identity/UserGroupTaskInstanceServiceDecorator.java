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
package org.jbpm.services.task.identity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.I18NText;
import org.kie.internal.task.api.model.OrganizationalEntity;
import org.kie.internal.task.api.model.Task;

@Decorator
public class UserGroupTaskInstanceServiceDecorator extends
        AbstractUserGroupCallbackDecorator implements TaskInstanceService {
    
    @Inject
    @Delegate
    private TaskInstanceService delegate;

    public void setDelegate(TaskInstanceService delegate) {
        this.delegate = delegate;
    }

    

    @Override
    public long addTask(Task task, Map<String, Object> params) {
        doCallbackOperationForPeopleAssignments(task.getPeopleAssignments());
        doCallbackOperationForTaskData(task.getTaskData());
        doCallbackOperationForTaskDeadlines(task.getDeadlines());
        return delegate.addTask(task, params);
    }

    @Override
    public long addTask(Task task, ContentData data) {
        doCallbackOperationForPeopleAssignments(task.getPeopleAssignments());
        doCallbackOperationForTaskData(task.getTaskData());
        doCallbackOperationForTaskDeadlines(task.getDeadlines());
        return delegate.addTask(task, data);
    }

    @Override
    public void activate(long taskId, String userId) {
        delegate.activate(taskId, userId);
    }

    @Override
    public void claim(long taskId, String userId) {
        delegate.claim(taskId, userId);

    }

    @Override
    public void claim(long taskId, String userId, List<String> groupIds) {
        delegate.claim(taskId, userId, groupIds);
    }

    @Override
    public void claimNextAvailable(String userId, String language) {
        delegate.claimNextAvailable(userId, language);
    }

    @Override
    public void claimNextAvailable(String userId, List<String> groupIds,
            String language) {
        delegate.claimNextAvailable(userId, groupIds, language);

    }

    @Override
    public void complete(long taskId, String userId, Map<String, Object> data) {
        delegate.complete(taskId, userId, data);
    }

    @Override
    public void delegate(long taskId, String userId, String targetUserId) {
        delegate.delegate(taskId, userId, targetUserId);
    }

    @Override
    public void exit(long taskId, String userId) {
        delegate.exit(taskId, userId);

    }

    @Override
    public void fail(long taskId, String userId, Map<String, Object> faultData) {
        delegate.fail(taskId, userId, faultData);

    }

    @Override
    public void forward(long taskId, String userId, String targetEntityId) {
        delegate.forward(taskId, userId, targetEntityId);
    }

    @Override
    public void release(long taskId, String userId) {
        delegate.release(taskId, userId);

    }

    @Override
    public void remove(long taskId, String userId) {
        delegate.remove(taskId, userId);
    }

    @Override
    public void resume(long taskId, String userId) {
        delegate.resume(taskId, userId);
    }

    @Override
    public void skip(long taskId, String userId) {
        delegate.skip(taskId, userId);
    }

    @Override
    public void start(long taskId, String userId) {
        delegate.start(taskId, userId);
    }

    @Override
    public void stop(long taskId, String userId) {
        delegate.stop(taskId, userId);
    }

    @Override
    public void suspend(long taskId, String userId) {
        delegate.suspend(taskId, userId);
    }

    @Override
    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        doCallbackOperationForPotentialOwners(potentialOwners);
        delegate.nominate(taskId, userId, potentialOwners);
    }

    @Override
    public void setFault(long taskId, String userId, FaultData fault) {
        delegate.setFault(taskId, userId, fault);
    }

    @Override
    public void setOutput(long taskId, String userId, Object outputContentData) {
        delegate.setOutput(taskId, userId, outputContentData);
    }

    @Override
    public void deleteFault(long taskId, String userId) {
        delegate.deleteFault(taskId, userId);
    }

    @Override
    public void deleteOutput(long taskId, String userId) {
        delegate.deleteOutput(taskId, userId);
    }

    @Override
    public void setPriority(long taskId, int priority) {
        delegate.setPriority(taskId, priority);
    }

    @Override
    public void setTaskNames(long taskId, List<I18NText> taskNames) {
        delegate.setTaskNames(taskId, taskNames);
    }

    @Override
    public void setExpirationDate(long taskId, Date date) {
        delegate.setExpirationDate(taskId, date);
    }

    @Override
    public void setDescriptions(long taskId, List<I18NText> descriptions) {
        delegate.setDescriptions(taskId, descriptions);
    }

    @Override
    public void setSkipable(long taskId, boolean skipable) {
        delegate.setSkipable(taskId, skipable);
    }

    @Override
    public void setSubTaskStrategy(long taskId, org.kie.internal.task.api.model.SubTasksStrategy strategy) {
        delegate.setSubTaskStrategy(taskId, strategy);
    }

    @Override
    public int getPriority(long taskId) {
        return delegate.getPriority(taskId);
    }

    @Override
    public Date getExpirationDate(long taskId) {

        return delegate.getExpirationDate(taskId);
    }

    @Override
    public List<I18NText> getDescriptions(long taskId) {
        return delegate.getDescriptions(taskId);
    }

    @Override
    public boolean isSkipable(long taskId) {
        return delegate.isSkipable(taskId);
    }

    @Override
    public org.kie.internal.task.api.model.SubTasksStrategy getSubTaskStrategy(long taskId) {
        return delegate.getSubTaskStrategy(taskId);
    }

}
