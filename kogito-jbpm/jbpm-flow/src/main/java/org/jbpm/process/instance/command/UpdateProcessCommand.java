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

import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name = "update-process-command")
@XmlAccessorType(XmlAccessType.NONE)
public class UpdateProcessCommand implements ExecutableCommand<Void>, KogitoProcessInstanceIdCommand {

    private static final long serialVersionUID = 6L;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String processInstanceId;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String processXml;

    public UpdateProcessCommand(String processInstanceId, String processXml) {
        this.processInstanceId = processInstanceId;
        this.processXml = processXml;
    }

    @Override
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessXml() {
        return processXml;
    }

    public void setProcessXml(String processXml) {
        this.processXml = processXml;
    }

    public Void execute(Context context) {
        KogitoProcessRuntime runtime = (KogitoProcessRuntime) ((RegistryContext) context).lookup(KieSession.class);
        ProcessInstanceImpl processInstance = (ProcessInstanceImpl) runtime.getProcessInstance(processInstanceId);
        if (processInstance != null) {
            processInstance.setProcessXml(processXml);
        }
        return null;
    }

    public String toString() {
        return "((ProcessInstanceImpl) ksession.getProcessInstance("
                + processInstanceId + ")).setProcessXml(" + processXml + ");";
    }
}
