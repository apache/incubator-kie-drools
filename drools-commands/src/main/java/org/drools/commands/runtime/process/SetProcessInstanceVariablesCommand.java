/**
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
package org.drools.commands.runtime.process;

import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.commands.jaxb.JaxbMapAdapter;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class SetProcessInstanceVariablesCommand implements ExecutableCommand<Void>,
                                                           ProcessInstanceIdCommand {

    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = 7802415761845739379L;

    @XmlAttribute(required = true)
    private String processInstanceId;

    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement(name = "variables")
    private Map<String, Object> variables = new HashMap<>();

    public SetProcessInstanceVariablesCommand() {
    }

    public SetProcessInstanceVariablesCommand(String processInstanceId,
                                              Map<String, Object> variables) {
        this.processInstanceId = processInstanceId;
        this.variables = variables;
    }

    @Override
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
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
        KieSession ksession = ((RegistryContext) context).lookup(KieSession.class);
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        if (processInstance != null) {
            if (variables != null) {
                for (Map.Entry<String, Object> entry : variables.entrySet()) {
                    ((WorkflowProcessInstance) processInstance).setVariable(entry.getKey(), entry.getValue());
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
