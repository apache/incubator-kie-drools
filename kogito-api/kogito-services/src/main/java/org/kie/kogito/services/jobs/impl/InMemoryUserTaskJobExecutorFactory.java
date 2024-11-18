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
package org.kie.kogito.services.jobs.impl;

import java.util.Optional;
import java.util.Set;

import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.descriptors.UserTaskInstanceJobDescription;
import org.kie.kogito.process.ProcessInstanceOptimisticLockingException;
import org.kie.kogito.usertask.UserTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.services.uow.UnitOfWorkExecutor.executeInUnitOfWork;

public class InMemoryUserTaskJobExecutorFactory implements JobExecutorFactory {

    private InMemoryJobContext jobsConfiguration;

    public InMemoryUserTaskJobExecutorFactory(InMemoryJobContext jobsConfiguration) {
        this.jobsConfiguration = jobsConfiguration;
    }

    @Override
    public Set<Class<? extends JobDescription>> types() {
        return Set.of(UserTaskInstanceJobDescription.class);
    }

    @Override
    public Runnable createNewRunnable(JobsService jobService, JobDescription jobDescription) {
        if (jobDescription instanceof UserTaskInstanceJobDescription userTaskInstanceJobDescription) {
            return userTaskJobDescription(jobService, jobsConfiguration, userTaskInstanceJobDescription);
        }
        throw new IllegalArgumentException("single job description not supported for " + jobDescription);
    }

    @Override
    public Runnable createNewRepeteableRunnable(JobsService jobService, JobDescription jobDescription) {
        if (jobDescription instanceof UserTaskInstanceJobDescription userTaskInstanceJobDescription) {
            return repetableUserTaskJobDescription(jobService, jobsConfiguration, userTaskInstanceJobDescription);
        }
        throw new IllegalArgumentException("repeteable job description not supported for " + jobDescription);
    }

    private Runnable userTaskJobDescription(JobsService jobService, InMemoryJobContext jobsConfiguration, UserTaskInstanceJobDescription userTaskInstanceJobDescription) {
        return new SignalUserTaskInstanceOnExpiredTimer(jobService, jobsConfiguration, userTaskInstanceJobDescription, true, -1);
    }

    private Runnable repetableUserTaskJobDescription(JobsService jobService, InMemoryJobContext jobsConfiguration, UserTaskInstanceJobDescription userTaskInstanceJobDescription) {
        return new SignalUserTaskInstanceOnExpiredTimer(jobService, jobsConfiguration, userTaskInstanceJobDescription, false, userTaskInstanceJobDescription.expirationTime().repeatLimit());
    }

}

class SignalUserTaskInstanceOnExpiredTimer implements Runnable {

    private static Logger LOGGER = LoggerFactory.getLogger(SignalUserTaskInstanceOnExpiredTimer.class);

    private boolean removeAtExecution;
    private Integer limit;
    private JobsService jobService;

    private InMemoryJobContext jobsConfiguration;

    private UserTaskInstanceJobDescription userTaskInstanceJobDescription;

    public SignalUserTaskInstanceOnExpiredTimer(JobsService jobService, InMemoryJobContext jobsConfiguration, UserTaskInstanceJobDescription userTaskInstanceJobDescription, boolean removeAtExecution,
            Integer limit) {
        this.userTaskInstanceJobDescription = userTaskInstanceJobDescription;
        this.removeAtExecution = removeAtExecution;
        this.limit = limit;
        this.jobsConfiguration = jobsConfiguration;
        this.jobService = jobService;
    }

    @Override
    public void run() {
        String jobId = userTaskInstanceJobDescription.id();
        String userTaskInstanceId = userTaskInstanceJobDescription.getUserTaskInstanceId();
        try {
            Optional<UserTaskInstance> userTaskInstance = jobsConfiguration.userTasks().instances().findById(userTaskInstanceId);
            if (userTaskInstance.isEmpty()) {
                LOGGER.info("Skipping Job {}. There is no user task instance of id {} ", jobId, userTaskInstanceId);
                return;
            }
            limit--;
            executeInUnitOfWork(jobsConfiguration.unitOfWorkManager(), () -> {
                userTaskInstance.get().trigger(userTaskInstanceJobDescription);
                return null;
            });
            if (limit == 0) {
                jobService.cancelJob(jobId);
            }
            LOGGER.debug("Job {} completed", jobId);
        } catch (ProcessInstanceOptimisticLockingException ex) {
            LOGGER.info("Retrying Job {} due to: {}", jobId, ex.getMessage());
            limit++;
            run();
        } finally {
            if (removeAtExecution) {
                jobService.cancelJob(jobId);
            }
        }
    }
}
