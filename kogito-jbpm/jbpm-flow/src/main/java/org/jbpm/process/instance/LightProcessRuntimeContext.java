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
package org.jbpm.process.instance;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.drools.core.common.WorkingMemoryAction;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.ProcessSupplier;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.kie.api.definition.process.Process;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.internal.process.CorrelationKey;
import org.kie.kogito.Application;
import org.kie.kogito.process.Processes;

public class LightProcessRuntimeContext implements ProcessRuntimeContext {

    private Processes allProcesses;
    private Collection<Process> processes;

    public LightProcessRuntimeContext(Application app, Collection<Process> processes) {
        this.allProcesses = app != null ? app.get(Processes.class) : null;
        this.processes = processes;
    }

    @Override
    public Collection<Process> getProcesses() {
        return processes;
    }

    @Override
    public Optional<Process> findProcess(String id) {
        Optional<Process> result = processes.stream().filter(p -> p.getId().equals(id)).findAny();
        if (result.isEmpty() && allProcesses != null) {
            result = Optional.ofNullable(((ProcessSupplier) allProcesses.processById(id)).get());
        }
        return result;
    }

    @Override
    public void startOperation() {

    }

    @Override
    public void endOperation() {

    }

    @Override
    public void queueWorkingMemoryAction(WorkingMemoryAction action) {

    }

    @Override
    public void addEventListener(DefaultAgendaEventListener conditional) {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public ProcessInstance createProcessInstance(Process process, CorrelationKey correlationKey) {

        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setProcess(process);

        if (correlationKey != null) {
            processInstance.getMetaData().put("CorrelationKey", correlationKey);
        }

        return processInstance;
    }

    @Override
    public void setupParameters(ProcessInstance processInstance, Map<String, Object> parameters) {
        Process process = processInstance.getProcess();
        // set variable default values
        // TODO: should be part of processInstanceImpl?
        VariableScope variableScope = (VariableScope) ((ContextContainer) process).getDefaultContext(VariableScope.VARIABLE_SCOPE);
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
        // set input parameters
        if (parameters != null) {
            if (variableScope != null) {
                for (Map.Entry<String, Object> entry : parameters.entrySet()) {

                    variableScope.validateVariable(process.getName(), entry.getKey(), entry.getValue());
                    variableScopeInstance.setVariable(entry.getKey(), entry.getValue());
                }
            } else {
                throw new IllegalArgumentException("This process does not support parameters!");
            }
        }

        variableScopeInstance.enforceRequiredVariables();
    }
}
