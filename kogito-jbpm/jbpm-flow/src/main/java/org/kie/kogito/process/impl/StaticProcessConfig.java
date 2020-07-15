/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.process.impl;

import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessEventListenerConfig;
import org.kie.kogito.process.WorkItemHandlerConfig;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.kie.kogito.signal.SignalManagerHub;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.services.signal.DefaultSignalManagerHub;

public class StaticProcessConfig implements ProcessConfig {

    private final WorkItemHandlerConfig workItemHandlerConfig;
    private final ProcessEventListenerConfig processEventListenerConfig;
    private final SignalManagerHub signalManager;
    private final UnitOfWorkManager unitOfWorkManager;
    private final JobsService jobsService;

    public StaticProcessConfig(
            WorkItemHandlerConfig workItemHandlerConfig,
            ProcessEventListenerConfig processEventListenerConfig,
            UnitOfWorkManager unitOfWorkManager,
            JobsService jobsService) {
        this.unitOfWorkManager = unitOfWorkManager;
        this.workItemHandlerConfig = workItemHandlerConfig;
        this.processEventListenerConfig = processEventListenerConfig;
        this.signalManager = new DefaultSignalManagerHub();
        this.jobsService = jobsService;
    }

    public StaticProcessConfig() {
        this(new DefaultWorkItemHandlerConfig(),
             new DefaultProcessEventListenerConfig(),
             new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory()),
             null);
    }

    @Override
    public WorkItemHandlerConfig workItemHandlers() {
        return this.workItemHandlerConfig;
    }

    @Override
    public ProcessEventListenerConfig processEventListeners() {
        return this.processEventListenerConfig;
    }

    @Override
    public SignalManagerHub signalManagerHub() {
        return this.signalManager;
    }

    @Override
    public UnitOfWorkManager unitOfWorkManager() {
        return this.unitOfWorkManager;
    }

    @Override
    public JobsService jobsService() {
        return jobsService;
    }
}
