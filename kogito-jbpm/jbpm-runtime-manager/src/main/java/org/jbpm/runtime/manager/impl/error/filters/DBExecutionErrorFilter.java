/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.runtime.manager.impl.error.filters;

import java.sql.SQLException;
import java.util.Date;

import javax.transaction.RollbackException;

import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.error.ExecutionError;
import org.kie.internal.runtime.error.ExecutionError.Builder;
import org.kie.internal.runtime.error.ExecutionErrorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DBExecutionErrorFilter extends AbstractExecutionErrorFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(DBExecutionErrorFilter.class);
    
    public static final String TYPE = "DB";
    
    private Class<?> hibernateException;
    
    public DBExecutionErrorFilter() {
        try {
            this.hibernateException = Class.forName("org.hibernate.HibernateException");
        } catch (Exception e) {
            logger.warn("Not able to find org.hibernate.HibernateException class");
        }
    }

    @Override
    public boolean accept(ExecutionErrorContext errorContext) {
        if (isCausedBy(errorContext.getCause(), SQLException.class, RollbackException.class, hibernateException)) {
            return true;
        }
        return false;
    }
    
    @Override
    public ExecutionError filter(ExecutionErrorContext errorContext) {
        Builder errorBuilder = ExecutionError.builder().type(TYPE).initActivityId(getInitActivityId(errorContext));
                
        String stacktrace = getStackTrace(errorContext.getCause());
        
        Task task = errorContext.getLastExecutedTask();
        NodeInstance nodeInstance = errorContext.getLastExecutedNode();
        logger.debug("Last executed node instance {}, last executed task {}", nodeInstance, task);
        if (nodeInstance != null) {
            logger.debug("Last executed node instance {} will be used to populate error details", nodeInstance);
            errorBuilder
            .deploymentId(((ProcessInstanceImpl)nodeInstance.getProcessInstance()).getDeploymentId())
            .processInstanceId(nodeInstance.getProcessInstance().getId())
            .processId(nodeInstance.getProcessInstance().getProcessId())
            .activityId(nodeInstance.getId())
            .activityName(nodeName(nodeInstance));
        } else if (task != null) {
            logger.debug("Last executed task {} will be used to populate error details", task);
            errorBuilder
            .deploymentId(task.getTaskData().getDeploymentId())
            .processInstanceId(task.getTaskData().getProcessInstanceId())
            .processId(task.getTaskData().getProcessId())
            .activityId(task.getId())
            .activityName(task.getName());
        }
        
        return errorBuilder
            .message(errorContext.getCause().getMessage())
            .error(stacktrace)
            .errorDate(new Date())
            .build();
        
    }

    @Override
    public Integer getPriority() {
        return 200;
    }

    @Override
    public String toString() {
        return "DBExecutionErrorFilter [accepts=SQLException,HibernateException, ignores=]";
    }

}
