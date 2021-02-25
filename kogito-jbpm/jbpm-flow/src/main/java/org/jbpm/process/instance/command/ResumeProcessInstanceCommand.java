/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.instance.command;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

@XmlRootElement(name = "get-completed-tasks-command")
@XmlAccessorType(XmlAccessType.NONE)
public class ResumeProcessInstanceCommand implements ExecutableCommand<Object>, KogitoProcessInstanceIdCommand {

    /** Generated serial version UID */
    private static final long serialVersionUID = 3153292964867981793L;

    @XmlAttribute
    @XmlSchemaType(name = "string")
    private String processInstanceId;

    @Override
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
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
        if (processInstance.getState() != KogitoProcessInstance.STATE_SUSPENDED) {
            throw new IllegalArgumentException("Process instance with id " + processInstanceId + " in state " + processInstance.getState());
        }
        ((org.jbpm.process.instance.ProcessInstance) processInstance).setState(KogitoProcessInstance.STATE_ACTIVE);
        return null;
    }

    public String toString() {
        return "session.abortProcessInstance(" + processInstanceId + ");";
    }

}
