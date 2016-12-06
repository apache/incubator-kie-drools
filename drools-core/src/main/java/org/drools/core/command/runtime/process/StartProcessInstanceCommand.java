/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.Context;
import org.kie.internal.command.ProcessInstanceIdCommand;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.NONE)
public class StartProcessInstanceCommand implements ExecutableCommand<ProcessInstance>, ProcessInstanceIdCommand {

    /** Generated serial version UID */
    private static final long serialVersionUID = -270933342071833447L;
    
    @XmlAttribute(required = true)
    private Long processInstanceId;

    public StartProcessInstanceCommand() {
    }

    public StartProcessInstanceCommand(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public ProcessInstance execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        ProcessInstance processInstance = (ProcessInstance) ksession.startProcessInstance(processInstanceId);
        return processInstance;
    }

    public String toString() {
        return "session.startProcessInstance(" + processInstanceId + ");";
    }
}
