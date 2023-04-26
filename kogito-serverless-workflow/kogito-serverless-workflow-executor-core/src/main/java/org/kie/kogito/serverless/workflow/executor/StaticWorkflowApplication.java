/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.executor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.kie.kogito.Addons;
import org.kie.kogito.KogitoEngine;
import org.kie.kogito.Model;
import org.kie.kogito.StaticApplication;
import org.kie.kogito.StaticConfig;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.StaticProcessConfig;
import org.kie.kogito.serverless.workflow.models.JsonNodeModel;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;

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

    private final StaticWorkflowProcesses processes = new StaticWorkflowProcesses();
    private Collection<KogitoWorkItemHandler> handlers = new ArrayList<>();
    private Iterable<StaticApplicationRegister> applicationRegisters;
    private Iterable<StaticWorkflowRegister> workflowRegisters;

    public static StaticWorkflowApplication create() {
        StaticWorkflowApplication application = new StaticWorkflowApplication();
        application.applicationRegisters.forEach(register -> register.register(application));
        return application;
    }

    private StaticWorkflowApplication() {
        super(new StaticConfig(new Addons(Collections.emptySet()), new StaticProcessConfig()));
        applicationRegisters = ServiceLoader.load(StaticApplicationRegister.class);
        workflowRegisters = ServiceLoader.load(StaticWorkflowRegister.class);
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
        return execute(process(workflow), data);
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
        return execute(process(workflow), data);
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
        processInstance.start();
        return processInstance.variables();
    }

    /**
     * Parses the flow, generating a process definition. You can reuse that process definition to invoke
     * the same flow several times, using <code>execute</code> method
     * 
     * @param workflow Serverless Worflow definition
     * @return Executable process definition
     */
    public Process<JsonNodeModel> process(Workflow workflow) {
        return processes.map.computeIfAbsent(workflow.getId(), k -> createProcess(workflow));
    }

    public void registerHandler(KogitoWorkItemHandler handler) {
        handlers.add(handler);
    }

    private Process<JsonNodeModel> createProcess(Workflow workflow) {
        workflowRegisters.forEach(register -> register.register(this, workflow));
        StaticWorkflowProcess process = new StaticWorkflowProcess(this, handlers, ServerlessWorkflowParser
                .of(workflow, JavaKogitoBuildContext.builder().withApplicationProperties(System.getProperties()).build()).getProcessInfo().info());
        WorkflowProcessImpl workflowProcess = (WorkflowProcessImpl) process.get();
        workflowProcess.getNodesRecursively().forEach(node -> {
            if (node instanceof SubProcessNode) {
                SubProcessNode subProcess = (SubProcessNode) node;
                subProcess.setSubProcessFactory(new StaticSubprocessFactory(processes.map.get(subProcess.getProcessId())));
            }
        });
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

    @Override
    public void close() {
        workflowRegisters.forEach(StaticWorkflowRegister::close);
        applicationRegisters.forEach(StaticApplicationRegister::close);
    }
}
