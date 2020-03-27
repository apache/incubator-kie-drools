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

package org.kie.kogito.process.bpmn2;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.kie.api.definition.process.Process;
import org.kie.api.io.Resource;
import org.kie.kogito.Model;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.AbstractProcess;

public class BpmnProcess extends AbstractProcess<BpmnVariables> {

    private static BpmnProcessCompiler COMPILER = new BpmnProcessCompiler();

    private final Process process;

    public BpmnProcess(Process p) {
        process = p;
    }

    public BpmnProcess(Process p, ProcessConfig config) {
        super(config);
        process = p;
    }

    @Override
    public ProcessInstance<BpmnVariables> createInstance(Model m) {
        return new BpmnProcessInstance(this, BpmnVariables.create(m.toMap()), this.createLegacyProcessRuntime());
    }

    public ProcessInstance<BpmnVariables> createInstance() {
        return new BpmnProcessInstance(this, BpmnVariables.create(), this.createLegacyProcessRuntime());
    }

    @Override
    public ProcessInstance<BpmnVariables> createInstance(BpmnVariables variables) {
        return new BpmnProcessInstance(this, variables, this.createLegacyProcessRuntime());
    }

    @Override
    public Process legacyProcess() {
        return process;
    }

    @Override
    public BpmnVariables createModel() {
        return BpmnVariables.create(new HashMap<String, Object>());
    }

    /**
     *
     */
    public static void overrideCompiler(BpmnProcessCompiler compiler) {
        COMPILER = Objects.requireNonNull(compiler);
    }

    public static List<BpmnProcess> from(Resource... resource) {
        return from(null, resource);
    }

    public static List<BpmnProcess> from(ProcessConfig config, Resource... resources) {
        return COMPILER.from(config, resources);
    }

}
