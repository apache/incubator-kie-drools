/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.kie.kogito.internal.process.event.KogitoObjectListener;
import org.kie.kogito.internal.process.event.KogitoProcessEventSupport;

public class VariableScopeListener implements KogitoObjectListener {

    private final ProcessInstance processInstance;
    private final String variableIdPrefix;
    private final String variableInstanceIdPrefix;
    private final List<String> tags;

    public VariableScopeListener(ProcessInstance processInstance, String name, String variableIdPrefix, String variableInstanceIdPrefix, List<String> tags) {
        this.processInstance = processInstance;
        this.variableIdPrefix = getString(variableIdPrefix, name);
        this.variableInstanceIdPrefix = getString(variableInstanceIdPrefix, name);
        this.tags = tags;
    }

    private String getString(String prefix, String suffix) {
        return (prefix == null ? "" : prefix + ".") + suffix;
    }

    @Override
    public void afterValueChanged(Object container, String property, Object oldValue, Object newValue) {
        getProcessEventSupport().fireAfterVariableChanged(
                getString(variableIdPrefix, property),
                getString(variableInstanceIdPrefix, property),
                oldValue, newValue, tags, processInstance, null, processInstance.getKnowledgeRuntime());
    }

    @Override
    public void beforeValueChanged(Object container, String property, Object oldValue, Object newValue) {
        getProcessEventSupport().fireBeforeVariableChanged(
                getString(variableIdPrefix, property),
                getString(variableInstanceIdPrefix, property),
                oldValue, newValue, tags, processInstance, null, processInstance.getKnowledgeRuntime());
    }

    private KogitoProcessEventSupport getProcessEventSupport() {
        return ((InternalProcessRuntime) processInstance.getKnowledgeRuntime().getProcessRuntime()).getProcessEventSupport();
    }
}
