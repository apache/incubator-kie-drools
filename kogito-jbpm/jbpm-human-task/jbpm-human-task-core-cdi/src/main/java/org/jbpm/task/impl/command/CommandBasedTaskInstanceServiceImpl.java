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
package org.jbpm.task.impl.command;

import java.util.List;
import java.util.Map;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.ContentData;
import org.jbpm.task.FaultData;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Task;
import org.jbpm.task.TaskDef;
import org.jbpm.task.annotations.CommandBased;
import org.jbpm.task.api.TaskCommandExecutor;
import org.jbpm.task.api.TaskInstanceService;
import org.jbpm.task.commands.ActivateTaskCommand;
import org.jbpm.task.commands.AddTaskCommand;
import org.jbpm.task.commands.ClaimNextAvailableTaskCommand;
import org.jbpm.task.commands.ClaimTaskCommand;
import org.jbpm.task.commands.CompleteTaskCommand;
import org.jbpm.task.commands.DelegateTaskCommand;
import org.jbpm.task.commands.ExitTaskCommand;
import org.jbpm.task.commands.FailTaskCommand;
import org.jbpm.task.commands.ForwardTaskCommand;
import org.jbpm.task.commands.NominateTaskCommand;
import org.jbpm.task.commands.ReleaseTaskCommand;
import org.jbpm.task.commands.ResumeTaskCommand;
import org.jbpm.task.commands.SkipTaskCommand;
import org.jbpm.task.commands.StartTaskCommand;
import org.jbpm.task.commands.StopTaskCommand;
import org.jbpm.task.commands.SuspendTaskCommand;

/**
 *
 */
@Alternative @Transactional
public class CommandBasedTaskInstanceServiceImpl implements TaskInstanceService{
    
    @Inject @CommandBased
    private TaskCommandExecutor executor;
    
    public long newTask(String name, Map<String, Object> params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long newTask(TaskDef def, Map<String, Object> params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long newTask(TaskDef def, Map<String, Object> params, boolean deploy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long addTask(Task task, Map<String, Object> params) {
        return (Long) executor.executeTaskCommand(new AddTaskCommand(task, params));
    }

    public long addTask(Task task, ContentData data) {
        return (Long) executor.executeTaskCommand(new AddTaskCommand(task, data));
    }

    public void activate(long taskId, String userId) {
        executor.executeTaskCommand(new ActivateTaskCommand(taskId, userId));
    }

    public void claim(long taskId, String userId) {
        executor.executeTaskCommand(new ClaimTaskCommand(taskId, userId));
    }

    public void claim(long taskId, String userId, List<String> groupIds) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void claimNextAvailable(String userId, String language) {
        executor.executeTaskCommand(new ClaimNextAvailableTaskCommand(userId, language));
    }

    public void claimNextAvailable(String userId, List<String> groupIds, String language) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void complete(long taskId, String userId, Map<String, Object> data) {
        executor.executeTaskCommand(new CompleteTaskCommand(taskId, userId, data));        
    }

    public void delegate(long taskId, String userId, String targetUserId) {
        executor.executeTaskCommand(new DelegateTaskCommand(taskId, userId, targetUserId));        
        executor.executeTaskCommand(new ClaimTaskCommand(taskId, targetUserId));        
    }

    public void deleteFault(long taskId, String userId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteOutput(long taskId, String userId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void exit(long taskId, String userId) {
        executor.executeTaskCommand(new ExitTaskCommand(taskId, userId));
    }

    public void fail(long taskId, String userId, Map<String, Object> faultData) {
        executor.executeTaskCommand(new FailTaskCommand(taskId, userId, faultData));        
    }

    public void forward(long taskId, String userId, String targetEntityId) {
        executor.executeTaskCommand(new ForwardTaskCommand(taskId, userId, targetEntityId));        
    }

    public void release(long taskId, String userId) {
        executor.executeTaskCommand(new ReleaseTaskCommand(taskId, userId));        
    }

    public void remove(long taskId, String userId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resume(long taskId, String userId) {
        executor.executeTaskCommand(new ResumeTaskCommand(taskId, userId));        
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
        executor.executeTaskCommand(new SkipTaskCommand(taskId, userId));        
    }

    public void start(long taskId, String userId) {
        executor.executeTaskCommand(new StartTaskCommand(taskId, userId));        
    }

    public void stop(long taskId, String userId) {
        executor.executeTaskCommand(new StopTaskCommand(taskId, userId));        
    }

    public void suspend(long taskId, String userId) {
        executor.executeTaskCommand(new SuspendTaskCommand(taskId, userId));        
    }

    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        executor.executeTaskCommand(new NominateTaskCommand(taskId, userId, potentialOwners));        
    }
    
}
