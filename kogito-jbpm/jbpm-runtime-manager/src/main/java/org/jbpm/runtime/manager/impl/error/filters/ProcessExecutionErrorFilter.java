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

import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.kie.internal.runtime.error.ExecutionError;
import org.kie.internal.runtime.error.ExecutionErrorContext;


public class ProcessExecutionErrorFilter extends AbstractExecutionErrorFilter {
    
    public static final String TYPE = "Process";

    @Override
    public boolean accept(ExecutionErrorContext errorContext) {
        if (isCausedBy(errorContext.getCause(), WorkflowRuntimeException.class)) {
            return true;
        }
        return false;
    }
    
    @Override
    public ExecutionError filter(ExecutionErrorContext errorContext) {
        
        WorkflowRuntimeException exception = extract(errorContext.getCause(), WorkflowRuntimeException.class);
        String stacktrace = getStackTrace(errorContext.getCause());
        
        return ExecutionError.builder()
            .type(TYPE)
            .initActivityId(getInitActivityId(errorContext))
            .deploymentId(exception.getDeploymentId())
            .processInstanceId(exception.getProcessInstanceId())
            .processId(exception.getProcessId())
            .activityId(exception.getNodeInstanceId())
            .activityName(exception.getNodeName())
            .message(exception.getMessage())
            .error(stacktrace)
            .errorDate(new Date())
            .build();
       
    }

    @Override
    public Integer getPriority() {
        return 100;
    }

    @Override
    public String toString() {
        return "ProcessExecutionErrorFilter [accepts=WorkflowRuntimeException, ignores=]";
    }


}
