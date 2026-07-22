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
package org.kie.kogito.process.bpmn2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.io.ClassPathResource;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.compiler.xml.core.SemanticModules;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.impl.NodeIoHelper;
import org.jbpm.workflow.core.node.SubProcessFactory;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.kie.api.definition.process.Process;
import org.kie.api.io.Resource;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.kogito.Application;
import org.kie.kogito.StaticApplication;
import org.kie.kogito.StaticConfig;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstancesFactory;

public class StaticApplicationAssembler {

    private static StaticApplicationAssembler INSTANCE = new StaticApplicationAssembler();

    private SemanticModules bpmnSemanticModules;

    public StaticApplicationAssembler() {
        this.bpmnSemanticModules = new SemanticModules();
        // add default
        this.bpmnSemanticModules.addSemanticModule(new BPMNSemanticModule());
        this.bpmnSemanticModules.addSemanticModule(new BPMNExtensionsSemanticModule());
        this.bpmnSemanticModules.addSemanticModule(new BPMNDISemanticModule());
    }

    protected SemanticModules getSemanticModules() {
        return bpmnSemanticModules;
    }

    public Application newStaticApplication(ProcessInstancesFactory processInstanceFactory, ProcessConfig processConfig, String... resources) {
        ClassPathResource[] classPathResources = List.of(resources).stream()
                .map(r -> new ClassPathResource(r, Thread.currentThread().getContextClassLoader()))
                .toArray(ClassPathResource[]::new);
        return newStaticApplication(processInstanceFactory, processConfig, classPathResources);
    }

    public Application newStaticApplication(ProcessInstancesFactory processInstanceFactory, ProcessConfig processConfig, Resource... resources) {
        try {
            List<Process> processes = new ArrayList<>();
            XmlProcessReader xmlReader = new XmlProcessReader(
                    getSemanticModules(),
                    Thread.currentThread().getContextClassLoader());

            for (Resource resource : resources) {
                processes.addAll(xmlReader.read(resource.getReader()));
            }

            return newStaticApplication(processInstanceFactory, processConfig, processes.toArray(Process[]::new));
        } catch (Exception e) {
            throw new BpmnProcessReaderException(e);
        }
    }

    public Application newStaticApplication(ProcessInstancesFactory processInstanceFactory, ProcessConfig processConfig, Process... processes) {
        BpmnProcesses container = new BpmnProcesses();
        container.setProcessInstancesFactory(processInstanceFactory);
        Application application = new StaticApplication(new StaticConfig(null, processConfig), container);

        // Create all BpmnProcess instances first
        List.of(processes).stream()
                .map(process -> new BpmnProcess(process, processConfig, application))
                .forEach(container::addProcess);

        // Initialize SubProcessFactory for all CallActivity nodes
        container.processIds().forEach(processId -> {
            BpmnProcess bpmnProcess = (BpmnProcess) container.processById(processId);
            WorkflowProcess workflowProcess = (WorkflowProcess) bpmnProcess.process();

            workflowProcess.getNodesRecursively().forEach(node -> {
                if (node instanceof SubProcessNode) {
                    SubProcessNode subProcessNode = (SubProcessNode) node;
                    String subProcessId = subProcessNode.getProcessId();

                    // Find the subprocess in the container
                    org.kie.kogito.process.Process<?> subprocess = container.processById(subProcessId);
                    if (subprocess != null) {
                        subProcessNode.setSubProcessFactory(
                                new BpmnSubProcessFactory((org.kie.kogito.process.Process<BpmnVariables>) subprocess));
                    }
                }
            });
        });

        return application;
    }

    public static StaticApplicationAssembler instance() {
        return INSTANCE;
    }

    /**
     * SubProcessFactory implementation for BPMN CallActivity nodes.
     * Handles parameter binding between parent and subprocess.
     */
    private static class BpmnSubProcessFactory implements SubProcessFactory<BpmnVariables> {

        private final org.kie.kogito.process.Process<BpmnVariables> subprocess;

        BpmnSubProcessFactory(org.kie.kogito.process.Process<BpmnVariables> subprocess) {
            this.subprocess = subprocess;
        }

        @Override
        public BpmnVariables bind(ProcessContext kcontext) {
            Map<String, Object> parameters = NodeIoHelper.processInputs(
                    (NodeInstanceImpl) kcontext.getNodeInstance(),
                    kcontext::getVariable);
            return BpmnVariables.create(parameters);
        }

        @Override
        public ProcessInstance<BpmnVariables> createInstance(BpmnVariables model) {
            return subprocess.createInstance(model);
        }

        @Override
        public void unbind(ProcessContext kcontext, BpmnVariables model) {
            Map<String, Object> outputs = new HashMap<>(model.toMap());
            NodeIoHelper.processOutputs(
                    (NodeInstanceImpl) kcontext.getNodeInstance(),
                    outputs::get,
                    kcontext::getVariable);
        }
    }

}
