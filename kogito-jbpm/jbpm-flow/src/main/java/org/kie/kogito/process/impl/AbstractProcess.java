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
package org.kie.kogito.process.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jbpm.process.core.ProcessSupplier;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.LightProcessRuntime;
import org.jbpm.process.instance.LightProcessRuntimeServiceProvider;
import org.jbpm.process.instance.ProcessRuntimeServiceProvider;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.core.node.StartNode;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.correlation.CorrelationService;
import org.kie.kogito.internal.process.runtime.KogitoNode;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.kie.kogito.jobs.DurationExpirationTime;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.ExpirationTime;
import org.kie.kogito.jobs.ProcessJobDescription;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.ProcessInstancesFactory;
import org.kie.kogito.process.ProcessVersionResolver;
import org.kie.kogito.process.Signal;
import org.kie.kogito.services.event.correlation.DefaultCorrelationService;

@SuppressWarnings("unchecked")
public abstract class AbstractProcess<T extends Model> implements Process<T>, ProcessSupplier {

    protected final ProcessRuntimeServiceProvider services;
    protected ProcessInstancesFactory processInstancesFactory;
    protected MutableProcessInstances<T> instances;
    protected CompletionEventListener completionEventListener = new CompletionEventListener();

    protected Application app;

    protected boolean activated;
    protected List<String> startTimerInstances = new ArrayList<>();
    protected KogitoProcessRuntime processRuntime;

    private org.kie.api.definition.process.Process process;
    private Lock processInitLock = new ReentrantLock();
    private CorrelationService correlations;
    private ProcessVersionResolver versionResolver;

    protected AbstractProcess() {
        this(new LightProcessRuntimeServiceProvider());
    }

    protected AbstractProcess(ProcessConfig config, Application application) {
        this(new ConfiguredProcessServices(config));
        this.app = application;
    }

    protected AbstractProcess(ProcessRuntimeServiceProvider services) {
        this(services, Collections.emptyList(), null, null, null);
    }

    protected AbstractProcess(Application app, Collection<KogitoWorkItemHandler> handlers, CorrelationService correlations) {
        this(app, handlers, correlations, null);
    }

    protected AbstractProcess(Application app, Collection<KogitoWorkItemHandler> handlers, CorrelationService correlations, ProcessInstancesFactory factory) {
        this(new ConfiguredProcessServices(app.config().get(ProcessConfig.class)), handlers, correlations, factory, app.config().get(ProcessConfig.class).versionResolver());
        this.app = app;
    }

    protected AbstractProcess(ProcessRuntimeServiceProvider services, Collection<KogitoWorkItemHandler> handlers, CorrelationService correlations, ProcessInstancesFactory factory,
            ProcessVersionResolver versionResolver) {
        this.services = services;
        this.instances = new MapProcessInstances<>();
        this.processInstancesFactory = factory;
        this.correlations = Optional.ofNullable(correlations).orElseGet(() -> new DefaultCorrelationService());
        this.versionResolver = Optional.ofNullable(versionResolver).orElse(p -> get().getVersion());
        KogitoWorkItemManager workItemManager = services.getKogitoWorkItemManager();
        for (KogitoWorkItemHandler handler : handlers) {
            workItemManager.registerWorkItemHandler(handler.getName(), handler);
        }
    }

    @Override
    public String id() {
        return get().getId();
    }

    @Override
    public String name() {
        return get().getName();
    }

    @Override
    public String version() {
        return versionResolver.apply(this);
    }

    @Override
    public T createModel() {
        return null;
    }

    @Override
    public ProcessInstance<T> createInstance(String businessKey, Model m) {
        return createInstance(businessKey, m);
    }

    public abstract ProcessInstance<T> createInstance(WorkflowProcessInstance wpi);

    public abstract ProcessInstance<T> createReadOnlyInstance(WorkflowProcessInstance wpi);

    @Override
    public Collection<KogitoNode> findNodes(Predicate<KogitoNode> filter) {
        RuleFlowProcess p = (RuleFlowProcess) this.process;
        return p.getNodesRecursively().stream().map(n -> (KogitoNode) n).filter(filter).collect(Collectors.toList());
    }

    @Override
    public ProcessInstances<T> instances() {
        return instances;
    }

    @Override
    public CorrelationService correlations() {
        return correlations;
    }

    @Override
    public <S> void send(Signal<S> signal) {
        instances().values(ProcessInstanceReadMode.MUTABLE).forEach(pi -> pi.send(signal));
    }

    public Process<T> configure() {
        registerListeners();
        if (isProcessFactorySet()) {
            this.instances = (MutableProcessInstances<T>) processInstancesFactory.createProcessInstances(this);
        }
        return this;
    }

    protected void registerListeners() {

    }

    @Override
    public void activate() {
        if (this.activated) {
            return;
        }
        WorkflowProcessImpl p = (WorkflowProcessImpl) get();
        configure();
        List<StartNode> startNodes = p.getTimerStart();
        if (startNodes != null && !startNodes.isEmpty()) {
            this.processRuntime = createProcessRuntime().getKogitoProcessRuntime();
            for (StartNode startNode : startNodes) {
                if (startNode != null && startNode.getTimer() != null) {
                    String timerId = processRuntime.getJobsService().scheduleProcessJob(ProcessJobDescription.of(configureTimerInstance(startNode.getTimer()), this));
                    startTimerInstances.add(timerId);
                }
            }
        }
        this.activated = true;
    }

    @Override
    public void deactivate() {
        for (String startTimerId : startTimerInstances) {
            this.processRuntime.getJobsService().cancelJob(startTimerId);
        }
        this.activated = false;
    }

    protected ExpirationTime configureTimerInstance(Timer timer) {
        switch (timer.getTimeType()) {
            case Timer.TIME_CYCLE:
                // when using ISO date/time period is not set
                long[] repeatValues = DateTimeUtils.parseRepeatableDateTime(timer.getDelay());
                if (repeatValues.length == 3) {
                    int parsedReapedCount = (int) repeatValues[0];
                    if (parsedReapedCount <= -1) {
                        parsedReapedCount = Integer.MAX_VALUE;
                    }
                    return DurationExpirationTime.repeat(repeatValues[1], repeatValues[2], parsedReapedCount);
                } else if (repeatValues.length == 2) {
                    return DurationExpirationTime.repeat(repeatValues[0], repeatValues[1], Integer.MAX_VALUE);
                } else {
                    return DurationExpirationTime.repeat(repeatValues[0], repeatValues[0], Integer.MAX_VALUE);
                }

            case Timer.TIME_DURATION:
                long duration = DateTimeUtils.parseDuration(timer.getDelay());
                return DurationExpirationTime.repeat(duration);

            case Timer.TIME_DATE:

                return ExactExpirationTime.of(timer.getDate());

            default:
                throw new UnsupportedOperationException("Not supported timer definition");
        }
    }

    @Override
    public org.kie.api.definition.process.Process get() {
        processInitLock.lock();
        try {
            if (process == null) {
                process = process();
            }
        } finally {
            processInitLock.unlock();
        }
        return process;
    }

    protected abstract org.kie.api.definition.process.Process process();

    protected InternalProcessRuntime createProcessRuntime() {
        return LightProcessRuntime.of(app, Collections.singletonList(get()), services);
    }

    protected boolean isProcessFactorySet() {
        return processInstancesFactory != null;
    }

    public void setProcessInstancesFactory(ProcessInstancesFactory processInstancesFactory) {
        this.processInstancesFactory = processInstancesFactory;
    }

    public EventListener eventListener() {
        return completionEventListener;
    }

    protected class CompletionEventListener implements EventListener {

        public CompletionEventListener() {
            //Do nothing
        }

        @Override
        public void signalEvent(String type, Object event) {
            if (type.startsWith("processInstanceCompleted:")) {
                KogitoProcessInstance pi = (KogitoProcessInstance) event;
                if (!id().equals(pi.getProcessId()) && pi.getParentProcessInstanceId() != null) {
                    instances().findById(pi.getParentProcessInstanceId()).ifPresent(p -> p.send(Sig.of(type, event)));
                }
            }
        }

        @Override
        public String[] getEventTypes() {
            return new String[0];
        }
    }
}
