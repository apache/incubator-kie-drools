/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.command.runtime.process;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.kie.internal.command.RegistryContext;

public class KogitoSetProcessInstanceVariablesCommand implements ExecutableCommand<Void>,
        ProcessInstanceIdCommand {

    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = 7802415761845739379L;

    private String processInstanceId;

    private Map<String, Object> variables = new HashMap<String, Object>();

    public KogitoSetProcessInstanceVariablesCommand() {
    }

    public KogitoSetProcessInstanceVariablesCommand( String processInstanceId,
                                                     Map<String, Object> variables) {
        this.processInstanceId = processInstanceId;
        this.variables = variables;
    }

    @Override
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId( String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables( Map<String, Object> variables) {
        this.variables = variables;
    }

    public Void execute( Context context) {
        KieSession ksession = (( RegistryContext ) context).lookup( KieSession.class);
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        if (processInstance != null) {
            if (variables != null) {
                for (Map.Entry<String, Object> entry : variables.entrySet()) {
                    (( WorkflowProcessInstance ) processInstance).setVariable(entry.getKey(), entry.getValue());
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
