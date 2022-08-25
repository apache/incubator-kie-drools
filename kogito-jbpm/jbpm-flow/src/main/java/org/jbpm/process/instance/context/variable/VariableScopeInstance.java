/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.instance.context.variable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.context.AbstractContextInstance;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.instance.node.CompositeContextNodeInstance;
import org.kie.kogito.internal.process.event.KogitoObjectListenerAware;
import org.kie.kogito.internal.process.event.KogitoProcessEventSupport;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.process.VariableViolationException;

/**
 * 
 */
public class VariableScopeInstance extends AbstractContextInstance {

    private static final long serialVersionUID = 510l;

    private Map<String, Object> variables = new HashMap<String, Object>();
    private transient String variableIdPrefix = null;
    private transient String variableInstanceIdPrefix = null;

    @Override
    public String getContextType() {
        return VariableScope.VARIABLE_SCOPE;
    }

    public Object getVariable(String name) {

        Object value = variables.get(name);
        if (value != null) {
            return value;
        }

        // support for processInstanceId and parentProcessInstanceId
        if ("processInstanceId".equals(name) && getProcessInstance() != null) {
            return getProcessInstance().getStringId();
        } else if ("parentProcessInstanceId".equals(name) && getProcessInstance() != null) {
            return getProcessInstance().getParentProcessInstanceId();
        }

        if (getProcessInstance() != null && getProcessInstance().getKnowledgeRuntime() != null) {
            // support for globals
            value = getProcessInstance().getKnowledgeRuntime().getGlobal(name);
            if (value != null) {
                return value;
            }

        }

        return null;
    }

    public Map<String, Object> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public void setVariable(String name, Object value) {
        setVariable(null, name, value);
    }

    public void setVariable(KogitoNodeInstance nodeInstance, String name, Object value) {
        Objects.requireNonNull(name, "The name of a variable may not be null!");
        // check if variable that is being set is readonly and has already been set
        Object oldValue = getVariable(name);
        if (oldValue != null && getVariableScope().isReadOnly(name)) {
            throw new VariableViolationException(getProcessInstance().getStringId(), name, "Variable '" + name + "' is already set and is marked as read only");
        }
        // ignore similar value
        if (ignoreChange(oldValue, value)) {
            return;
        }
        final Object clonedValue = getProcessInstance().getKnowledgeRuntime() != null ? clone(name, value) : null;
        if (clonedValue != null) {
            getProcessEventSupport().fireBeforeVariableChanged(
                    (variableIdPrefix == null ? "" : variableIdPrefix + ":") + name,
                    (variableInstanceIdPrefix == null ? "" : variableInstanceIdPrefix + ":") + name,
                    oldValue, clonedValue, getVariableScope().tags(name), getProcessInstance(),
                    nodeInstance,
                    getProcessInstance().getKnowledgeRuntime());
        }
        internalSetVariable(name, value);
        if (clonedValue != null) {
            getProcessEventSupport().fireAfterVariableChanged(
                    (variableIdPrefix == null ? "" : variableIdPrefix + ":") + name,
                    (variableInstanceIdPrefix == null ? "" : variableInstanceIdPrefix + ":") + name,
                    oldValue, clonedValue, getVariableScope().tags(name), getProcessInstance(),
                    nodeInstance,
                    getProcessInstance().getKnowledgeRuntime());
        }
    }

    private Object clone(String name, Object newValue) {
        Variable variable = getVariableScope().findVariable(name);
        return variable != null ? variable.getType().clone(newValue) : newValue;
    }

    private boolean ignoreChange(Object oldValue, Object newValue) {
        if (newValue instanceof KogitoObjectListenerAware) {
            return Objects.equals(oldValue, newValue) || (oldValue == null && ((KogitoObjectListenerAware) newValue).isEmpty());
        } else {
            return oldValue == null && newValue == null;
        }
    }

    private KogitoProcessEventSupport getProcessEventSupport() {
        return ((InternalProcessRuntime) getProcessInstance().getKnowledgeRuntime().getProcessRuntime()).getProcessEventSupport();
    }

    public void internalSetVariable(String name, Object value) {
        if (value instanceof KogitoObjectListenerAware) {
            ((KogitoObjectListenerAware) value).addKogitoObjectListener(
                    new VariableScopeListener(getProcessInstance(), name, variableIdPrefix, variableInstanceIdPrefix, getVariableScope().tags(name)));
        }
        variables.put(name, value);
    }

    public VariableScope getVariableScope() {
        return (VariableScope) getContext();
    }

    @Override
    public void setContextInstanceContainer(ContextInstanceContainer contextInstanceContainer) {
        super.setContextInstanceContainer(contextInstanceContainer);
        for (Variable variable : getVariableScope().getVariables()) {
            if (variable.getValue() != null) {
                internalSetVariable(variable.getName(), variable.cloneValue());
            }
        }
        if (contextInstanceContainer instanceof CompositeContextNodeInstance) {
            this.variableIdPrefix = ((Node) ((CompositeContextNodeInstance) contextInstanceContainer).getNode()).getUniqueId();
            this.variableInstanceIdPrefix = ((CompositeContextNodeInstance) contextInstanceContainer).getUniqueId();
        }
    }

    public void enforceRequiredVariables() {
        VariableScope variableScope = getVariableScope();
        for (Variable variable : variableScope.getVariables()) {
            if (variableScope.isRequired(variable.getName()) && !variables.containsKey(variable.getName())) {
                throw new VariableViolationException(getProcessInstance().getStringId(), variable.getName(), "Variable '" + variable.getName() + "' is required but not set");
            }
        }
    }
}
