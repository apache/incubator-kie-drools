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
package org.kie.kogito.process.bpmn2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.core.xml.SemanticModule;
import org.drools.core.xml.SemanticModules;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.workflow.core.WorkflowProcess;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.kie.api.io.Resource;
import org.kie.kogito.Application;
import org.kie.kogito.StaticApplication;
import org.kie.kogito.StaticConfig;
import org.kie.kogito.process.ProcessConfig;

public class BpmnProcessCompiler {

    private final SemanticModules bpmnSemanticModules;

    public BpmnProcessCompiler(SemanticModule... modules) {
        this.bpmnSemanticModules = new SemanticModules();

        if (modules.length == 0) {
            // add default
            this.bpmnSemanticModules.addSemanticModule(new BPMNSemanticModule());
            this.bpmnSemanticModules.addSemanticModule(new BPMNExtensionsSemanticModule());
            this.bpmnSemanticModules.addSemanticModule(new BPMNDISemanticModule());
        } else {
            for (SemanticModule module : modules) {
                this.bpmnSemanticModules.addSemanticModule(module);
            }
        }
    }

    protected SemanticModules getSemanticModules() {
        return bpmnSemanticModules;
    }

    public List<BpmnProcess> from(ProcessConfig config, Resource... resources) {
        try {
            BpmnProcesses bpmnProcesses = new BpmnProcesses();
            StaticApplication application = new StaticApplication(new StaticConfig(null, config), bpmnProcesses);

            List<Process> processes = new ArrayList<>();
            XmlProcessReader xmlReader = new XmlProcessReader(
                    getSemanticModules(),
                    Thread.currentThread().getContextClassLoader());
            configureProcessReader(xmlReader, config);

            for (Resource resource : resources) {
                processes.addAll(xmlReader.read(resource.getReader()));
            }
            List<BpmnProcess> bpmnProcessesList = processes.stream()
                    .map(p -> create(p, config, application))
                    .collect(Collectors.toList());

            bpmnProcessesList.forEach(p -> {

                for (Node node : ((WorkflowProcess) p.process()).getNodesRecursively()) {

                    processNode(node, bpmnProcessesList);
                }
            });

            return bpmnProcessesList;
        } catch (Exception e) {
            throw new BpmnProcessReaderException(e);
        }
    }

    protected void configureProcessReader(XmlProcessReader xmlReader, ProcessConfig config) {

    }

    protected BpmnProcess create(Process process, ProcessConfig config, Application application) {
        return config == null ? new BpmnProcess(process) : new BpmnProcess(process, config, application);
    }

    protected void processNode(Node node, List<BpmnProcess> bpmnProcesses) {

    }
}
