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
package org.jbpm.services.task.commands;

import org.jbpm.services.task.events.TaskEventSupport;
import org.jbpm.services.task.identity.UserGroupLifeCycleManagerDecorator;
import org.jbpm.services.task.impl.TaskAdminServiceImpl;
import org.jbpm.services.task.impl.TaskAttachmentServiceImpl;
import org.jbpm.services.task.impl.TaskCommentServiceImpl;
import org.jbpm.services.task.impl.TaskContentServiceImpl;
import org.jbpm.services.task.impl.TaskDeadlinesServiceImpl;
import org.jbpm.services.task.impl.TaskDefServiceImpl;
import org.jbpm.services.task.impl.TaskIdentityServiceImpl;
import org.jbpm.services.task.impl.TaskInstanceServiceImpl;
import org.jbpm.services.task.impl.TaskQueryServiceImpl;
import org.jbpm.services.task.internals.lifecycle.MVELLifeCycleManager;
import org.jbpm.services.task.rule.TaskRuleService;
import org.jbpm.services.task.rule.impl.RuleContextProviderImpl;
import org.jbpm.services.task.rule.impl.TaskRuleServiceImpl;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.command.Context;
import org.kie.internal.command.World;
import org.kie.internal.task.api.TaskAdminService;
import org.kie.internal.task.api.TaskAttachmentService;
import org.kie.internal.task.api.TaskCommentService;
import org.kie.internal.task.api.TaskContentService;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.kie.internal.task.api.TaskDefService;
import org.kie.internal.task.api.TaskIdentityService;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.TaskQueryService;

/**
 *
 */
public class TaskContext implements org.kie.internal.task.api.TaskContext {
    
    private TaskPersistenceContext persistenceContext;
    private Environment environment;
    private TaskEventSupport taskEventSupport;
    
    public TaskContext() {
    }   
    
    public TaskContext(Context context, Environment environment, TaskEventSupport taskEventSupport) {
    	if (context instanceof org.kie.internal.task.api.TaskContext) {
    		this.persistenceContext = ((org.kie.internal.task.api.TaskContext) context).getPersistenceContext();
    	}
    	this.environment = environment;
    	this.taskEventSupport = taskEventSupport;
    }  
    
    public TaskInstanceService getTaskInstanceService() {
        return new TaskInstanceServiceImpl(persistenceContext,
        		new UserGroupLifeCycleManagerDecorator(getUserGroupCallback(),
        		new MVELLifeCycleManager(persistenceContext, getTaskContentService(), taskEventSupport)),
        		taskEventSupport);
    }
    
    public TaskDefService getTaskDefService() {
        return new TaskDefServiceImpl(persistenceContext);
    }

    public TaskQueryService getTaskQueryService() {
        return new TaskQueryServiceImpl(persistenceContext);
    }

    public TaskContentService getTaskContentService() {
        return new TaskContentServiceImpl(persistenceContext);
    }
    
    public TaskCommentService getTaskCommentService() {
    	return new TaskCommentServiceImpl(persistenceContext);
    }
    
    public TaskAttachmentService getTaskAttachmentService() {
        return new TaskAttachmentServiceImpl(persistenceContext);
    }

    public TaskIdentityService getTaskIdentityService() {
        return new TaskIdentityServiceImpl(persistenceContext);
    }
    
    public TaskAdminService getTaskAdminService() {
    	return new TaskAdminServiceImpl(persistenceContext);
    }
    
    public TaskDeadlinesService getTaskDeadlinesService() {
    	return new TaskDeadlinesServiceImpl(persistenceContext);
    }

    public TaskRuleService getTaskRuleService() {
    	return new TaskRuleServiceImpl(RuleContextProviderImpl.get());
    }
    
    public TaskPersistenceContext getPersistenceContext() {
    	return persistenceContext;
    }

	public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
		this.persistenceContext = persistenceContext;
	}	

    public Object get(String string) {
        return this.environment.get(string);
    }

    public void set(String string, Object o) {
        if (this.environment.get(string) != null) {
        	throw new IllegalArgumentException("Cannot override value for property " + string);
        }
    	this.environment.set(string, o);
    }


	@Override
	public UserGroupCallback getUserGroupCallback() {
		return (UserGroupCallback) get(EnvironmentName.TASK_USER_GROUP_CALLBASK);
	}
	
	/*
	 * currently not used methods 
	 */
	
    public World getContextManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void remove(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }    

}
