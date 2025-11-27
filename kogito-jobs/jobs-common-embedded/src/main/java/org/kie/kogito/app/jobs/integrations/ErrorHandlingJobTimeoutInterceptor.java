/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.app.jobs.integrations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.kie.kogito.app.jobs.api.JobTimeoutExecution;
import org.kie.kogito.app.jobs.api.JobTimeoutInterceptor;
import org.kie.kogito.handler.ExceptionHandler;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandlingJobTimeoutInterceptor implements JobTimeoutInterceptor {

    private static Logger LOG = LoggerFactory.getLogger(ErrorHandlingJobTimeoutInterceptor.class);

    private List<ExceptionHandler> exceptionHandlers;

    public ErrorHandlingJobTimeoutInterceptor(List<ExceptionHandler> exceptionHandlers) {
        this.exceptionHandlers = new ArrayList<>(exceptionHandlers);
    }

    @Override
    public Integer priority() {
        return 50;
    }

    @Override
    public Callable<JobTimeoutExecution> chainIntercept(Callable<JobTimeoutExecution> callable) {
        return new Callable<JobTimeoutExecution>() {
            @Override
            public JobTimeoutExecution call() throws Exception {
                JobTimeoutExecution execution = callable.call();
                if (execution.getJobDetails() != null
                        && (JobStatus.ERROR.equals(execution.getJobDetails().getStatus()) || (JobStatus.RETRY.equals(execution.getJobDetails().getStatus()) && execution.getException() != null))) {
                    if (exceptionHandlers.isEmpty()) {
                        LOG.warn("there was an error in job {} but not handler were registered", execution.getJobDetails());
                    } else {
                        LOG.error("there was error in job {}. Handling error {}", execution.getJobDetails(), execution.getException().getMessage());
                        exceptionHandlers.stream().forEach(e -> e.handle(execution.getException()));
                    }
                }
                return execution;
            }
        };
    }

}
