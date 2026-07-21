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

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.descriptors.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.descriptors.ProcessJobDescription;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.timer.TimerInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.services.jobs.impl.TriggerJobCommand.SIGNAL;

public class LegacyInMemoryJobExecutorFactory implements JobExecutorFactory {

    private static final String TRIGGER = "timer";

    private static Logger LOGGER = LoggerFactory.getLogger(LegacyInMemoryJobExecutorFactory.class);

    private InMemoryJobContext jobsConfiguration;

    public LegacyInMemoryJobExecutorFactory(InMemoryJobContext jobsConfiguration) {
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
        throw new IllegalArgumentException("multiple job description not supported for " + jobDescription);
    }

    protected Runnable processInstanceJobDescription(JobsService jobService, InMemoryJobContext jobsConfiguration, ProcessInstanceJobDescription description, boolean remove, int limit) {
        String id = description.id();
        AtomicInteger counter = new AtomicInteger(limit);
        return () -> {
            try {
                UnitOfWorkExecutor.executeInUnitOfWork(jobsConfiguration.unitOfWorkManager(), () -> {
                    ProcessInstance pi = jobsConfiguration.runtime().getProcessInstance(description.processInstanceId());
                    if (pi != null) {
                        pi.signalEvent(SIGNAL, TimerInstance.with(description.id(), description.timerId(), counter.decrementAndGet()));
                        if (counter.get() == 0) {
                            jobService.cancelJob(id);
                        }
                    } else {
                        // since owning process instance does not exist cancel timers
                        jobService.cancelJob(id);
                    }
                    return null;
                });
                LOGGER.debug("Job {} completed", id);
            } finally {
                if (remove) {
                    jobService.cancelJob(id);
                }
            }
        };
    }

    protected Runnable processJobByDescription(JobsService jobService, InMemoryJobContext jobsConfiguration, ProcessJobDescription description) {
        return processCommand(jobService, jobsConfiguration, description, true);
    }

    protected Runnable repeatableJobByDescription(JobsService jobService, InMemoryJobContext jobsConfiguration, ProcessJobDescription description) {
        return processCommand(jobService, jobsConfiguration, description, false);
    }

    private Runnable processCommand(JobsService jobService, InMemoryJobContext jobsConfiguration, ProcessJobDescription description, boolean remove) {

        String id = description.id();
        AtomicInteger counter = new AtomicInteger(description.expirationTime().repeatLimit());
        String processId = description.processId();
        return () -> {
            try {
                LOGGER.debug("Job {} started", id);
                UnitOfWorkExecutor.executeInUnitOfWork(jobsConfiguration.unitOfWorkManager(), () -> {
                    KogitoProcessInstance pi = jobsConfiguration.runtime().createProcessInstance(processId, null);
                    if (pi != null) {
                        jobsConfiguration.runtime().startProcessInstance(pi.getStringId(), TRIGGER);
                    }
                    return null;
                });
                if (counter.decrementAndGet() == 0) {
                    jobService.cancelJob(id);
                }
                LOGGER.debug("Job {} completed", id);
            } finally {
                if (remove) {
                    jobService.cancelJob(id);
                }
            }
        };
    }

}
