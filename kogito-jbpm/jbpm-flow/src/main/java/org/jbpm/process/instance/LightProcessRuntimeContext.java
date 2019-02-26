package org.jbpm.process.instance;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.WorkingMemoryAction;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.kie.api.definition.process.Process;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.process.CorrelationKey;

public class LightProcessRuntimeContext implements ProcessRuntimeContext {

    private final List<Process> processes;

    public LightProcessRuntimeContext(
            List<Process> processes) {
        this.processes = processes;
    }

    @Override
    public Collection<Process> getProcesses() {
        return processes;
    }

    @Override
    public Optional<Process> findProcess(String id) {
        return processes.stream().filter(p -> p.getId().equals(id)).findFirst();
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
    public WorkItemManager getWorkItemManager() {
        return null;
    }

    @Override
    public void addEventListener(DefaultAgendaEventListener conditional) {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public ProcessInstance createProcessInstance(
            Process process,
            CorrelationKey correlationKey,
            Map<String, Object> parameters) {

        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setProcess(process);

        if (correlationKey != null) {
            processInstance.getMetaData().put("CorrelationKey", correlationKey);
        }

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

        return processInstance;
    }
}
