/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.process.impl;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.kie.api.event.process.ProcessEventListener;
import org.kie.kogito.Addons;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessEventListenerConfig;
import org.kie.kogito.process.ProcessVersionResolver;
import org.kie.kogito.process.WorkItemHandlerConfig;
import org.kie.kogito.services.signal.DefaultSignalManagerHub;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.kie.kogito.signal.SignalManagerHub;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.kogito.uow.events.UnitOfWorkEventListener;

public abstract class AbstractProcessConfig implements ProcessConfig {

    private final WorkItemHandlerConfig workItemHandlerConfig;
    private final SignalManagerHub signalManagerHub = new DefaultSignalManagerHub();
    private final ProcessEventListenerConfig processEventListenerConfig;
    private final UnitOfWorkManager unitOfWorkManager;
    private final JobsService jobsService;
    private final ProcessVersionResolver versionResolver;

    protected AbstractProcessConfig(
            Iterable<WorkItemHandlerConfig> workItemHandlerConfig,
            Iterable<ProcessEventListenerConfig> processEventListenerConfigs,
            Iterable<ProcessEventListener> processEventListeners,
            Iterable<UnitOfWorkManager> unitOfWorkManager,
            Iterable<JobsService> jobsService,
            Iterable<EventPublisher> eventPublishers,
            String kogitoService,
            Iterable<UnitOfWorkEventListener> unitOfWorkListeners,
            Iterable<ProcessVersionResolver> versionResolver) {

        this.workItemHandlerConfig = orDefault(workItemHandlerConfig, DefaultWorkItemHandlerConfig::new);
        this.processEventListenerConfig = merge(processEventListenerConfigs, processEventListeners);
        this.unitOfWorkManager = orDefault(unitOfWorkManager,
                () -> new DefaultUnitOfWorkManager(
                        new CollectingUnitOfWorkFactory()));
        this.jobsService = orDefault(jobsService, () -> null);
        this.versionResolver = orDefault(versionResolver, () -> null);

        eventPublishers.forEach(publisher -> unitOfWorkManager().eventManager().addPublisher(publisher));
        unitOfWorkListeners.forEach(listener -> unitOfWorkManager().register(listener));
        unitOfWorkManager().eventManager().setService(kogitoService);
    }

    @Override
    public WorkItemHandlerConfig workItemHandlers() {
        return workItemHandlerConfig;
    }

    @Override
    public ProcessEventListenerConfig processEventListeners() {
        return processEventListenerConfig;
    }

    @Override
    public SignalManagerHub signalManagerHub() {
        return signalManagerHub;
    }

    @Override
    public UnitOfWorkManager unitOfWorkManager() {
        return unitOfWorkManager;
    }

    @Override
    public JobsService jobsService() {
        return jobsService;
    }

    @Override
    public ProcessVersionResolver versionResolver() {
        return versionResolver;
    }

    public org.kie.kogito.Addons addons() {
        return Addons.EMTPY;
    }

    static <T> T orDefault(Iterable<T> instance, Supplier<? extends T> supplier) {
        Iterator<T> iterator = instance.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return supplier.get();
        }
    }

    static ProcessEventListenerConfig merge(
            Iterable<ProcessEventListenerConfig> processEventListenerConfigs,
            Iterable<ProcessEventListener> processEventListeners) {
        List<ProcessEventListenerConfig> l1 = StreamSupport.stream(processEventListenerConfigs.spliterator(), false).collect(Collectors.toList());
        List<ProcessEventListener> l2 = StreamSupport.stream(processEventListeners.spliterator(), false).collect(Collectors.toList());

        Stream<ProcessEventListener> processEventListenerStream = l1.stream().flatMap(c -> c.listeners().stream());
        Stream<ProcessEventListener> eventListenerStream = l2.stream();

        return new CachedProcessEventListenerConfig(
                Stream.concat(processEventListenerStream, eventListenerStream)
                        .collect(Collectors.toList()));
    }
}
