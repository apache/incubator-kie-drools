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
package org.jbpm.bpmn2;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.instance.InternalProcessRuntime;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;

public class KogitoSetProcessInstanceVariablesCommand implements ExecutableCommand<Void> {

    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = 7802415761845739379L;

    private String processInstanceId;

    private Map<String, Object> variables = new HashMap<>();

    public KogitoSetProcessInstanceVariablesCommand() {
    }

    public KogitoSetProcessInstanceVariablesCommand(String processInstanceId,
            Map<String, Object> variables) {
        this.processInstanceId = processInstanceId;
        this.variables = variables;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Void execute(Context context) {
        KogitoProcessRuntime kruntime = InternalProcessRuntime.asKogitoProcessRuntime(((RegistryContext) context).lookup(KieSession.class));
        KogitoProcessInstance processInstance = kruntime.getProcessInstance(processInstanceId);
        if (processInstance != null) {
            if (variables != null) {
                for (Map.Entry<String, Object> entry : variables.entrySet()) {
                    ((KogitoWorkflowProcessInstance) processInstance).setVariable(entry.getKey(), entry.getValue());
                }
            }
        }
        return null;
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("WorkflowProcessInstance processInstance = (WorkflowProcessInstance) session.getProcessInstance(");
        result.append(processInstanceId);
        result.append(");");
        if (variables != null) {
            for (final Map.Entry<String, Object> entry : variables.entrySet()) {
                result.append("\nprocessInstance.setVariable(\"");
                result.append(entry.getKey());
                result.append("\", ");
                result.append((entry.getValue() == null ? "null" : entry.getValue().toString()));
                result.append(");");
            }
        }
        return result.toString();
    }
}
