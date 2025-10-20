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
package org.kie.kogito.serverless.workflow.executor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.kogito.Addons;
import org.kie.kogito.KogitoEngine;
import org.kie.kogito.Model;
import org.kie.kogito.StaticApplication;
import org.kie.kogito.StaticConfig;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.config.StaticConfigBean;
import org.kie.kogito.event.EventManager;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.impl.EventFactoryUtils;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemHandler;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstancesFactory;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.CachedWorkItemHandlerConfig;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;
import org.kie.kogito.process.impl.StaticProcessConfig;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.models.JsonNodeModel;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;
import org.kie.kogito.serverless.workflow.utils.ConfigResolverHolder;
import org.kie.kogito.serverless.workflow.utils.MultiSourceConfigResolver;
import org.kie.kogito.services.jobs.impl.InMemoryJobContext;
import org.kie.kogito.services.jobs.impl.InMemoryJobService;
import org.kie.kogito.services.jobs.impl.InMemoryProcessJobExecutorFactory;
import org.kie.kogito.services.jobs.impl.StaticJobService;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.Workflow;

/**
 * This is the entry point for executing a workflow from a JVM
 * Given a <code>Workflow</code> object, you can execute it by writing
 * <code>
 *  // Generated a flow definition or read it from a file
 *  Workflow flow = ....;
 *  try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
 *       // set input model for flow
 *       Map<String,Object> params = ...;
 *       JsonNodeModel model = application.execute(flow, params);
 *       // do something with returned data;
 * }
 * </code>
 * 
 *
 */
public class StaticWorkflowApplication extends StaticApplication implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(StaticWorkflowApplication.class);
    private final StaticWorkflowProcesses processes = new StaticWorkflowProcesses();
    private final Collection<KogitoWorkItemHandler> handlers = new ArrayList<>();
    private Iterable<StaticApplicationRegister> applicationRegisters;
    private Iterable<StaticWorkflowRegister> workflowRegisters;
    private Iterable<StaticProcessRegister> processRegisters;
    private final Collection<AutoCloseable> closeables = new ArrayList<>();
    private final ConcurrentMap<String, BlockingQueue<JsonNodeModel>> queues;
    private final UnitOfWorkManager manager;
    private ProcessInstancesFactory processInstancesFactory;
    private ExecutorService executor;

    private static class StaticCompletionEventListener extends DefaultKogitoProcessEventListener {

        private final ConcurrentMap<String, BlockingQueue<JsonNodeModel>> queues;

        public StaticCompletionEventListener(ConcurrentMap<String, BlockingQueue<JsonNodeModel>> queues) {
            this.queues = queues;
        }

        @Override
        public void beforeProcessStarted(ProcessStartedEvent event) {
            WorkflowProcessInstance instance = (WorkflowProcessInstance) event.getProcessInstance();
            queues.putIfAbsent(instance.getId(), new LinkedBlockingQueue<JsonNodeModel>());
        }

        @Override
        public void afterProcessCompleted(ProcessCompletedEvent event) {
            WorkflowProcessInstance instance = (WorkflowProcessInstance) event.getProcessInstance();
            BlockingQueue<JsonNodeModel> queue = queues.get(instance.getId());
            queue.add(new JsonNodeModel(instance.getId(), instance.getVariables().get(SWFConstants.DEFAULT_WORKFLOW_VAR)));
        }

    }

    public static class WorkflowApplicationBuilder {

        private Map<String, Object> properties;
        private String serviceName = "EmbeddedKogito";
        private Collection<KogitoProcessEventListener> listeners = new ArrayList<>();
        private Optional<UnitOfWorkManager> manager = Optional.empty();
        private Collection<EventPublisher> publishers = new ArrayList<>();
        private ExecutorService executor;
        private Optional<JobsService> jobsService = Optional.empty();

        private WorkflowApplicationBuilder() {
        }

        public WorkflowApplicationBuilder withProperties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        public WorkflowApplicationBuilder withEventListener(KogitoProcessEventListener listener, KogitoProcessEventListener... extraListeners) {
            listeners.add(listener);
            for (KogitoProcessEventListener extraListener : extraListeners) {
                listeners.add(extraListener);
            }
            return this;
        }

        public WorkflowApplicationBuilder withManager(UnitOfWorkManager manager) {
            this.manager = Optional.ofNullable(manager);
            return this;
        }

        public WorkflowApplicationBuilder withService(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public WorkflowApplicationBuilder withEventPublisher(EventPublisher publisher, EventPublisher... extraPublishers) {
            publishers.add(publisher);
            for (EventPublisher extraPublisher : extraPublishers) {
                publishers.add(extraPublisher);
            }
            return this;
        }

        public WorkflowApplicationBuilder withExecutorService(ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        public WorkflowApplicationBuilder withJobsService(JobsService jobsService) {
            this.jobsService = Optional.ofNullable(jobsService);
            return this;
        }

        public StaticWorkflowApplication build() {
            if (properties == null) {
                this.properties = loadApplicationDotProperties();
            }
            ConcurrentMap<String, BlockingQueue<JsonNodeModel>> queues = new ConcurrentHashMap<>();
            listeners.add(new StaticCompletionEventListener(queues));
            StaticWorkflowApplication application =
                    new StaticWorkflowApplication(properties, queues, listeners, manager.orElseGet(() -> new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory())), executor,
                            jobsService.orElseGet(() -> StaticJobService.staticJobService()));
            application.applicationRegisters.forEach(register -> register.register(application));
            EventManager eventManager = application.manager.eventManager();
            eventManager.setService(serviceName);
            publishers.forEach(p -> eventManager.addPublisher(p));
            return application;
        }
    }

    private static Map<String, Object> loadApplicationDotProperties() {
        Map<String, Object> allProperties = new HashMap<>();
        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources("application.properties");
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                try (InputStream is = url.openStream()) {
                    Properties fileProperties = new Properties();
                    fileProperties.load(is);
                    fileProperties.entrySet().forEach(e -> allProperties.put(e.getKey().toString(), e.getValue()));
                } catch (IOException io) {
                    logger.info("Error loading properties from URL {}", url, io);
                }
            }
        } catch (IOException io) {
            logger.warn("Error searching for application.properties in classpath", io);
        }
        return allProperties;
    }

    public static WorkflowApplicationBuilder builder() {
        return new WorkflowApplicationBuilder();
    }

    public static StaticWorkflowApplication create() {
        return builder().build();
    }

    public static StaticWorkflowApplication create(Map<String, Object> properties) {
        return builder().withProperties(properties).build();
    }

    private StaticWorkflowApplication(Map<String, Object> properties, ConcurrentMap<String, BlockingQueue<JsonNodeModel>> queues, Collection<KogitoProcessEventListener> listeners,
            UnitOfWorkManager manager, ExecutorService executor, JobsService jobsService) {
        super(new StaticConfig(new Addons(Collections.emptySet()), new StaticProcessConfig(new CachedWorkItemHandlerConfig(),
                new DefaultProcessEventListenerConfig(listeners), manager, jobsService), new StaticConfigBean()));
        if (jobsService instanceof InMemoryJobService inMemoryJobService) {
            inMemoryJobService.registerJobExecutorFactory(new InMemoryProcessJobExecutorFactory(new InMemoryJobContext(null, manager, processes, null)));
        }
        if (!properties.isEmpty()) {
            ConfigResolverHolder.setConfigResolver(MultiSourceConfigResolver.withSystemProperties(properties));
        }
        this.queues = queues;
        this.manager = manager;
        this.executor = executor;
        applicationRegisters = ServiceLoader.load(StaticApplicationRegister.class);
        workflowRegisters = ServiceLoader.load(StaticWorkflowRegister.class);
        processRegisters = ServiceLoader.load(StaticProcessRegister.class);
    }

    /**
     * Given a workflow, executes it. This is a shortcut for <code>
     * execute(process(flow),data);
     * </code>. It is expected to be used only when you want to execute your flow once.
     * 
     * @param workflow Serverless workflow definition
     * @param data A map containing workflow input parameters
     * @return
     */
    public JsonNodeModel execute(Workflow workflow, Map<String, Object> data) {
        return execute(findOrCreate(workflow), data);
    }

    /**
     * Given a workflow, executes it. This is a shortcut for <code>
     * 	execute(process(flow),data);
     * </code>. It is expected to be used only when you want to execute your flow once.
     * 
     * @param workflow Serverless workflow definition
     * @param data A json containing workflow input parameters
     * @return
     */
    public JsonNodeModel execute(Workflow workflow, JsonNode data) {
        return execute(findOrCreate(workflow), data);
    }

    public StaticWorkflowApplication processInstancesFactory(ProcessInstancesFactory processInstanceFactory) {
        this.processInstancesFactory = processInstanceFactory;
        return this;
    }

    private Process<JsonNodeModel> findOrCreate(Workflow workflow) {
        return findProcessById(workflow.getId()).orElseGet(() -> process(workflow));
    }

    /**
     * Given a process definition, executes it. A process definition can be obtained from the flow by using <code>process</code> method
     * 
     * @param workflow Serverless Workflow definition
     * @param data A map containing workflow input parameters.
     * @return
     */
    public JsonNodeModel execute(Process<JsonNodeModel> process, Map<String, Object> data) {
        return execute(process, new JsonNodeModel(data));
    }

    /**
     * Given a process definition, executes it. A process definition can be obtained from the flow by using <code>process</code> method
     * 
     * @param workflow Serverless Workflow definition
     * @param data A JsonNode containing workflow input parameters.
     * @return
     */
    public JsonNodeModel execute(Process<JsonNodeModel> process, JsonNode data) {
        return execute(process, new JsonNodeModel(data));
    }

    /**
     * Given a process definition, executes it. A process definition can be obtained from a flow by using <code>process</code> method
     * 
     * @param workflow Serverless Workflow definition
     * @param model JsnoNodeModel obtained from a previous execution of another flow
     * @return
     */
    public JsonNodeModel execute(Process<JsonNodeModel> process, JsonNodeModel model) {
        ProcessInstance<JsonNodeModel> processInstance = process.createInstance(model);
        return UnitOfWorkExecutor.executeInUnitOfWork(manager, () -> {
            processInstance.start();
            return processInstance.variables();
        });
    }

    /**
     * Parses the flow, generating a process definition. You can reuse that process definition to invoke
     * the same flow several times, using <code>execute</code> method
     * 
     * @param workflow Serverless Worflow definition
     * @return Executable process definition
     */
    public Process<JsonNodeModel> process(Workflow workflow) {
        Process<JsonNodeModel> process = createProcess(workflow);
        processes.map.put(workflow.getId(), process);
        return process;
    }

    public void registerHandler(KogitoWorkItemHandler handler) {
        handlers.add(handler);
    }

    public void registerCloseable(AutoCloseable closeable) {
        closeables.add(closeable);
    }

    public Optional<Process<JsonNodeModel>> findProcessById(String id) {
        return Optional.ofNullable((Process<JsonNodeModel>) processes.processById(id));
    }

    private Optional<ProcessInstance<JsonNodeModel>> findProcessInstance(String id) {
        for (Process<JsonNodeModel> process : processes.map.values()) {
            Optional<ProcessInstance<JsonNodeModel>> pi = process.instances().findById(id);
            if (pi.isPresent()) {
                return pi;
            }
        }
        return Optional.empty();
    }

    public Optional<JsonNodeModel> variables(String id) {
        return findProcessInstance(id).map(ProcessInstance::variables);
    }

    public Optional<JsonNodeModel> waitForFinish(String id, Duration duration) throws InterruptedException, TimeoutException {
        JsonNodeModel model = queues.get(id).poll(duration.toMillis(), TimeUnit.MILLISECONDS);
        if (model == null) {
            Optional<ProcessInstance<JsonNodeModel>> pi = findProcessInstance(id);
            if (pi.isEmpty()) {
                queues.remove(id);
                return pi.map(ProcessInstance::variables);
            }
            throw new TimeoutException("Process " + id + " has not finished after " + duration);
        }
        return Optional.of(model);
    }

    private Process<JsonNodeModel> createProcess(Workflow workflow) {
        workflowRegisters.forEach(r -> r.register(this, workflow));
        StaticWorkflowProcess process = new StaticWorkflowProcess(this, handlers, processInstancesFactory, ServerlessWorkflowParser
                .of(workflow, JavaKogitoBuildContext.builder().withApplicationProperties(System.getProperties()).build()).getProcessInfo().info());
        processRegisters.forEach(r -> r.register(this, workflow, process));
        WorkflowProcessImpl workflowProcess = (WorkflowProcessImpl) process.get();
        workflowProcess.getNodesRecursively().forEach(node -> {
            if (node instanceof SubProcessNode) {
                SubProcessNode subProcess = (SubProcessNode) node;
                subProcess.setSubProcessFactory(new StaticSubprocessFactory((Process<JsonNodeModel>) processes.processById(subProcess.getProcessId())));
            }
        });
        EventFactoryUtils.ready();
        return process;
    }

    @Override
    public <T extends KogitoEngine> T get(Class<T> clazz) {
        if (Processes.class.isAssignableFrom(clazz)) {
            return clazz.cast(processes);
        }
        return super.get(clazz);
    }

    private class StaticWorkflowProcesses implements Processes {
        private Map<String, Process<JsonNodeModel>> map = new ConcurrentHashMap<>();

        @Override
        public Process<? extends Model> processById(String processId) {
            return map.get(processId);
        }

        @Override
        public Collection<String> processIds() {
            return map.keySet();
        }
    }

    ExecutorService executorService() {
        return executor;
    }

    @Override
    public void close() {
        processRegisters.forEach(StaticProcessRegister::close);
        workflowRegisters.forEach(StaticWorkflowRegister::close);
        applicationRegisters.forEach(StaticApplicationRegister::close);
        closeables.forEach(t -> {
            try {
                t.close();
            } catch (Exception e) {
                logger.warn("Error closing resource", e);
            }
        });
    }
}
