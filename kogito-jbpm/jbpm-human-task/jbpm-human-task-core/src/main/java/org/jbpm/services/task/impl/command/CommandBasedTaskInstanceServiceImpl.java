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
package org.jbpm.services.task.impl.command;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.annotations.CommandBased;
import org.jbpm.services.task.commands.ActivateTaskCommand;
import org.jbpm.services.task.commands.AddTaskCommand;
import org.jbpm.services.task.commands.ClaimNextAvailableTaskCommand;
import org.jbpm.services.task.commands.ClaimTaskCommand;
import org.jbpm.services.task.commands.CompleteTaskCommand;
import org.jbpm.services.task.commands.DelegateTaskCommand;
import org.jbpm.services.task.commands.ExitTaskCommand;
import org.jbpm.services.task.commands.FailTaskCommand;
import org.jbpm.services.task.commands.ForwardTaskCommand;
import org.jbpm.services.task.commands.NominateTaskCommand;
import org.jbpm.services.task.commands.ReleaseTaskCommand;
import org.jbpm.services.task.commands.ResumeTaskCommand;
import org.jbpm.services.task.commands.SkipTaskCommand;
import org.jbpm.services.task.commands.StartTaskCommand;
import org.jbpm.services.task.commands.StopTaskCommand;
import org.jbpm.services.task.commands.SuspendTaskCommand;
import org.kie.api.command.Command;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.TaskDef;

/**
 *
 */
@Alternative @Transactional
public class CommandBasedTaskInstanceServiceImpl implements TaskInstanceService{
    
    @Inject @CommandBased
    private CommandExecutor executor;
    
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
        return (Long) executor.execute(new AddTaskCommand(task, params));
    }

    public long addTask(Task task, ContentData data) {
        return (Long) executor.execute(new AddTaskCommand(task, data));
    }

    public void activate(long taskId, String userId) {
        executor.execute(new ActivateTaskCommand(taskId, userId));
    }

    public void claim(long taskId, String userId) {
        executor.execute(new ClaimTaskCommand(taskId, userId));
    }

    public void claim(long taskId, String userId, List<String> groupIds) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void claimNextAvailable(String userId, String language) {
        executor.execute(new ClaimNextAvailableTaskCommand(userId, language));
    }

    public void claimNextAvailable(String userId, List<String> groupIds, String language) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void complete(long taskId, String userId, Map<String, Object> data) {
        executor.execute(new CompleteTaskCommand(taskId, userId, data));        
    }

    public void delegate(long taskId, String userId, String targetUserId) {
        executor.execute(new DelegateTaskCommand(taskId, userId, targetUserId));        
        executor.execute(new ClaimTaskCommand(taskId, targetUserId));        
    }

    public void deleteFault(long taskId, String userId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteOutput(long taskId, String userId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void exit(long taskId, String userId) {
        executor.execute(new ExitTaskCommand(taskId, userId));
    }

    public void fail(long taskId, String userId, Map<String, Object> faultData) {
        executor.execute(new FailTaskCommand(taskId, userId, faultData));        
    }

    public void forward(long taskId, String userId, String targetEntityId) {
        executor.execute(new ForwardTaskCommand(taskId, userId, targetEntityId));        
    }

    public void release(long taskId, String userId) {
        executor.execute(new ReleaseTaskCommand(taskId, userId));        
    }

    public void remove(long taskId, String userId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resume(long taskId, String userId) {
        executor.execute(new ResumeTaskCommand(taskId, userId));        
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
        executor.execute(new SkipTaskCommand(taskId, userId));        
    }

    public void start(long taskId, String userId) {
        executor.execute(new StartTaskCommand(taskId, userId));        
    }

    public void stop(long taskId, String userId) {
        executor.execute(new StopTaskCommand(taskId, userId));        
    }

    public void suspend(long taskId, String userId) {
        executor.execute(new SuspendTaskCommand(taskId, userId));        
    }

    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        executor.execute(new NominateTaskCommand(taskId, userId, potentialOwners));        
    }

    public void setPriority(long taskId, int priority) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setExpirationDate(long taskId, Date date) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDescriptions(long taskId, List<I18NText> descriptions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSkipable(long taskId, boolean skipable) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSubTaskStrategy(long taskId, org.kie.internal.task.api.model.SubTasksStrategy strategy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getPriority(long taskId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Date getExpirationDate(long taskId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<I18NText> getDescriptions(long taskId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isSkipable(long taskId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public org.kie.internal.task.api.model.SubTasksStrategy getSubTaskStrategy(long taskId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTaskNames(long taskId, List<I18NText> taskNames) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	@Override
	public <T> T execute(Command<T> command) {
		return executor.execute(command);
	}
    
}
