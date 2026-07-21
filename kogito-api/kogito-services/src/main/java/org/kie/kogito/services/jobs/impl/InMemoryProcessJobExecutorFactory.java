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

import org.kie.kogito.Model;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.descriptors.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.descriptors.ProcessJobDescription;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstanceOptimisticLockingException;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryProcessJobExecutorFactory implements JobExecutorFactory {

    private InMemoryJobContext jobsConfiguration;

    public InMemoryProcessJobExecutorFactory(InMemoryJobContext jobsConfiguration) {
        this.jobsConfiguration = jobsConfiguration;
    }

    @Override
    public Set<Class<? extends JobDescription>> types() {
        return Set.of(ProcessInstanceJobDescription.class, ProcessJobDescription.class);
    }

    @Override
    public Runnable createNewRunnable(JobsService jobService, JobDescription jobDescription) {
        if (jobDescription instanceof ProcessInstanceJobDescription processInstanceJobDescription) {
            return processInstanceJobDescription(jobService, jobsConfiguration, processInstanceJobDescription, true, 1);
        } else if (jobDescription instanceof ProcessJobDescription processJobDescription) {
            return processJobByDescription(jobService, jobsConfiguration, processJobDescription);
        }
        throw new IllegalArgumentException("single job description not supported for " + jobDescription);
    }

    @Override
    public Runnable createNewRepeteableRunnable(JobsService jobService, JobDescription jobDescription) {
        if (jobDescription instanceof ProcessInstanceJobDescription processInstanceJobDescription) {
            return processInstanceJobDescription(jobService, jobsConfiguration, processInstanceJobDescription, false, processInstanceJobDescription.expirationTime().repeatLimit());
        } else if (jobDescription instanceof ProcessJobDescription processJobDescription) {
            return repeatableJobByDescription(jobService, jobsConfiguration, processJobDescription);
        }
        throw new IllegalArgumentException("repeteable job description not supported for " + jobDescription);
    }

    protected Runnable processInstanceJobDescription(JobsService jobService, InMemoryJobContext jobsConfiguration, ProcessInstanceJobDescription description, boolean remove, int limit) {
        return new SignalProcessInstanceOnExpiredTimer(
                jobService,
                jobsConfiguration,
                description.id(),
                description.timerId(),
                description.processInstanceId(),
                remove,
                limit);
    }

    protected Runnable processJobByDescription(JobsService jobService, InMemoryJobContext jobsConfiguration, ProcessJobDescription description) {
        return new StartProcessOnExpiredTimer(jobService, jobsConfiguration, description.id(), description.processId(), true, -1);
    }

    protected Runnable repeatableJobByDescription(JobsService jobService, InMemoryJobContext jobsConfiguration, ProcessJobDescription description) {
        return new StartProcessOnExpiredTimer(jobService, jobsConfiguration, description.id(), description.processId(), false,
                description.expirationTime().repeatLimit());
    }

}

class SignalProcessInstanceOnExpiredTimer implements Runnable {

    private static Logger LOGGER = LoggerFactory.getLogger(SignalProcessInstanceOnExpiredTimer.class);

    private final String id;
    private final String timerId;
    private boolean removeAtExecution;
    private String processInstanceId;
    private Integer limit;
    private JobsService jobService;

    private InMemoryJobContext jobsConfiguration;

    public SignalProcessInstanceOnExpiredTimer(JobsService jobService, InMemoryJobContext jobsConfiguration, String id, String timerId, String processInstanceId, boolean removeAtExecution,
            Integer limit) {
        this.id = id;
        this.timerId = timerId;
        this.processInstanceId = processInstanceId;
        this.removeAtExecution = removeAtExecution;
        this.limit = limit;
        this.jobsConfiguration = jobsConfiguration;
        this.jobService = jobService;
    }

    @Override
    public void run() {
        try {
            Optional<Process<? extends Model>> process = jobsConfiguration.processes().processByProcessInstanceId(processInstanceId);
            if (process.isEmpty()) {
                LOGGER.info("Skipping Job {}. There is no process for pid {} ", id, processInstanceId);
                return;
            }
            LOGGER.info("Job {} started", id);
            limit--;
            boolean executed = new TriggerJobCommand(processInstanceId, id, timerId, limit, process.get(), jobsConfiguration.unitOfWorkManager()).execute();
            if (limit == 0 || !executed) {
                jobService.cancelJob(id);
            }
            LOGGER.debug("Job {} completed", id);
        } catch (ProcessInstanceOptimisticLockingException ex) {
            LOGGER.info("Retrying Job {} due to: {}", id, ex.getMessage());
            limit++;
            run();
        } finally {
            if (removeAtExecution) {
                jobService.cancelJob(id);
            }
        }
    }
}

class StartProcessOnExpiredTimer implements Runnable {

    private static Logger LOGGER = LoggerFactory.getLogger(StartProcessOnExpiredTimer.class);

    private static final String TRIGGER = "timer";

    private final String id;

    private boolean removeAtExecution;
    @SuppressWarnings("rawtypes")
    private String processId;

    private Integer limit;

    private JobsService jobService;
    private InMemoryJobContext jobsConfiguration;

    public StartProcessOnExpiredTimer(JobsService jobService, InMemoryJobContext jobsConfiguration, String id, String processId, boolean removeAtExecution, Integer limit) {
        this.id = id;
        this.processId = processId;
        this.removeAtExecution = removeAtExecution;
        this.limit = limit;
        this.jobsConfiguration = jobsConfiguration;
        this.jobService = jobService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        try {
            LOGGER.debug("Job {} started", id);
            UnitOfWorkExecutor.executeInUnitOfWork(jobsConfiguration.unitOfWorkManager(), () -> {
                org.kie.kogito.process.Process process = jobsConfiguration.processes().processById(processId);
                org.kie.kogito.process.ProcessInstance<?> pi = process.createInstance(process.createModel());
                if (pi != null) {
                    pi.start(TRIGGER, null);
                }
                return null;
            });
            limit--;
            if (limit == 0) {
                jobService.cancelJob(id);
            }
            LOGGER.debug("Job {} completed", id);
        } finally {
            if (removeAtExecution) {
                jobService.cancelJob(id);
            }
        }
    }
}