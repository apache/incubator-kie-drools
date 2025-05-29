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

import java.util.HashMap;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.WorkflowProcess;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.AbstractProcess;

public class BpmnProcess extends AbstractProcess<BpmnVariables> {

    private static StaticApplicationAssembler COMPILER = new StaticApplicationAssembler();

    private final Process process;

    private Application application;

    public BpmnProcess(Process p) {
        process = p;
    }

    public BpmnProcess(Process process, ProcessConfig config, Application application) {
        super(config, application);
        this.process = process;
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }

    @Override
    public ProcessInstance<BpmnVariables> createInstance(String businessKey, CompositeCorrelation correlation, BpmnVariables workingMemory) {
        return createInstance(businessKey, workingMemory);
    }

    @Override
    public ProcessInstance<BpmnVariables> createInstance(Model m) {
        BpmnVariables variables = createModel();
        variables.fromMap(m.toMap());
        return new BpmnProcessInstance(this, variables, this.createProcessRuntime());
    }

    public ProcessInstance<BpmnVariables> createInstance() {
        return new BpmnProcessInstance(this, createModel(), this.createProcessRuntime());
    }

    @Override
    public ProcessInstance<BpmnVariables> createInstance(String businessKey, BpmnVariables variables) {
        BpmnVariables variablesModel = createModel();
        variablesModel.fromMap(variables.toMap());
        return new BpmnProcessInstance(this, variablesModel, businessKey, this.createProcessRuntime());
    }

    @Override
    public ProcessInstance<BpmnVariables> createInstance(BpmnVariables variables) {
        BpmnVariables variablesModel = createModel();
        variablesModel.fromMap(variables.toMap());
        return new BpmnProcessInstance(this, variablesModel, this.createProcessRuntime());
    }

    @Override
    public ProcessInstance<BpmnVariables> createInstance(WorkflowProcessInstance wpi) {
        return new BpmnProcessInstance(this, createModel(), this.createProcessRuntime(), wpi);
    }

    @Override
    public ProcessInstance<BpmnVariables> createReadOnlyInstance(WorkflowProcessInstance wpi) {
        return new BpmnProcessInstance(this, createModel(), wpi);
    }

    @Override
    public Process process() {
        return process;
    }

    @Override
    public BpmnVariables createModel() {
        VariableScope variableScope = (VariableScope) ((WorkflowProcess) get()).getDefaultContext(VariableScope.VARIABLE_SCOPE);
        return new BpmnVariables(variableScope.getVariables(), new HashMap<>());
    }

}
