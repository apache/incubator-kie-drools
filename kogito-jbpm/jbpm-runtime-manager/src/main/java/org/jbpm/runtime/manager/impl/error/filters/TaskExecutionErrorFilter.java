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

import java.util.Date;

import org.jbpm.services.task.exception.PermissionDeniedException;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.error.ExecutionError;
import org.kie.internal.runtime.error.ExecutionError.Builder;
import org.kie.internal.runtime.error.ExecutionErrorContext;
import org.kie.internal.task.exception.TaskException;


public class TaskExecutionErrorFilter extends AbstractExecutionErrorFilter {

    public static final String TYPE = "Task";

    @Override
    public boolean accept(ExecutionErrorContext errorContext) {
        if (isCausedBy(errorContext.getCause(), TaskException.class)) {
            return true;
        }
        return false;
    }
    
    @Override
    public ExecutionError filter(ExecutionErrorContext errorContext) {
        
        
        if (isCausedBy(errorContext.getCause(), PermissionDeniedException.class)) {
            return null;
        }
        Builder taskErrorBuilder = ExecutionError.builder().type(TYPE).initActivityId(getInitActivityId(errorContext));
                
        TaskException exception = extract(errorContext.getCause(), TaskException.class);
        String stacktrace = getStackTrace(exception);
        
        Task task = errorContext.getLastExecutedTask();
        
        if (task != null) {
            taskErrorBuilder
            .deploymentId(task.getTaskData().getDeploymentId())
            .processInstanceId(task.getTaskData().getProcessInstanceId())
            .processId(task.getTaskData().getProcessId())
            .activityId(task.getId())
            .activityName(task.getName());
        }
        
        return taskErrorBuilder
            .message(exception.getMessage())
            .error(stacktrace)
            .errorDate(new Date())
                .build();
        
    }

    @Override
    public Integer getPriority() {
        return 80;
    }

    @Override
    public String toString() {
        return "TaskExecutionErrorFilter [accepts=CannotAddTaskException, ignores=PermissionDeniedException]";
    }


}
