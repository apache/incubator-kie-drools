/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.services.jobs.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.ProcessJobDescription;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.timer.TimerInstance;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.services.jobs.impl.TriggerJobCommand.SIGNAL;

public class LegacyInMemoryJobService extends InMemoryJobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyInMemoryJobService.class);
    private KogitoProcessRuntime processRuntime;

    public LegacyInMemoryJobService(KogitoProcessRuntime processRuntime, UnitOfWorkManager unitOfWorkManager) {
        super(null, unitOfWorkManager);
        this.processRuntime = processRuntime;
    }

    @Override
    public Runnable getSignalProcessInstanceCommand(ProcessInstanceJobDescription description, boolean remove, int limit) {
        String id = description.id();
        AtomicInteger counter = new AtomicInteger(limit);
        return () -> {
            try {
                UnitOfWorkExecutor.executeInUnitOfWork(unitOfWorkManager, () -> {
                    ProcessInstance pi = processRuntime.getProcessInstance(description.processInstanceId());
                    if (pi != null) {
                        pi.signalEvent(SIGNAL, TimerInstance.with(id, counter.decrementAndGet()));
                        if (counter.get() == 0) {
                            cancelJob(id, false);
                        }
                    } else {
                        // since owning process instance does not exist cancel timers
                        cancelJob(id, false);
                    }
                    return null;
                });
                LOGGER.debug("Job {} completed", id);
            } finally {
                if (remove) {
                    cancelJob(id);
                }
            }
        };
    }

    @Override
    protected Runnable processJobByDescription(ProcessJobDescription description) {
        return processCommand(description, true);
    }

    private Runnable processCommand(ProcessJobDescription description, boolean remove) {
        String id = description.id();
        AtomicInteger counter = new AtomicInteger(description.expirationTime().repeatLimit());
        String processId = description.processId();
        return () -> {
            try {
                LOGGER.debug("Job {} started", id);
                UnitOfWorkExecutor.executeInUnitOfWork(unitOfWorkManager, () -> {
                    KogitoProcessInstance pi = processRuntime.createProcessInstance(processId, null);
                    if (pi != null) {
                        processRuntime.startProcessInstance(pi.getStringId(), TRIGGER);
                    }
                    return null;
                });
                if (counter.decrementAndGet() == 0) {
                    cancelJob(id, false);
                }
                LOGGER.debug("Job {} completed", id);
            } finally {
                if (remove) {
                    cancelJob(id);
                }
            }
        };
    }

    @Override
    protected Runnable repeatableProcessJobByDescription(ProcessJobDescription description) {
        return processCommand(description, false);
    }
}
