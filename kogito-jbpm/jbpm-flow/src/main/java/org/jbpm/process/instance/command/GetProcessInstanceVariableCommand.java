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
package org.jbpm.process.instance.command;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;

@XmlAccessorType(XmlAccessType.NONE)
public class GetProcessInstanceVariableCommand implements ExecutableCommand<Object>, KogitoProcessInstanceIdCommand {

    private static final long serialVersionUID = 6L;

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "string")
    private String processInstanceId;

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "string")
    private String variableId;

    @Override
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public Object execute(Context context) {
        KogitoProcessRuntime runtime = (KogitoProcessRuntime) ((RegistryContext) context).lookup(KieSession.class);
        if (processInstanceId == null) {
            return null;
        }
        KogitoProcessInstance processInstance = runtime.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            throw new IllegalArgumentException("Could not find process instance for id " + processInstanceId);
        }
        if (processInstance instanceof KogitoWorkflowProcessInstance) {
            return ((KogitoWorkflowProcessInstance) processInstance).getVariable(variableId);
        } else {
            throw new IllegalStateException("Could not retrieve variable " + variableId
                    + " because process instance " + processInstanceId + " was inaccessible!");
        }
    }

    public String toString() {
        return "session.getProcessInstanceVariable(" + processInstanceId + ", " + variableId + ");";
    }

}
