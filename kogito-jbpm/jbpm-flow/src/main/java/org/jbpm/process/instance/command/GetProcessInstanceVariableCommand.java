/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import javax.xml.bind.annotation.XmlSchemaType;

import org.drools.core.command.impl.RegistryContext;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.command.ProcessInstanceIdCommand;

@XmlAccessorType(XmlAccessType.NONE)
public class GetProcessInstanceVariableCommand implements ExecutableCommand<Object>, ProcessInstanceIdCommand {

    private static final long serialVersionUID = 6L;
	
    @XmlAttribute(required=true)
    @XmlSchemaType(name="long")
    private Long processInstanceId;

    @XmlAttribute(required=true)
    @XmlSchemaType(name="string")
    private String variableId;

    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public Object execute(Context context ) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        if (processInstanceId == null) {
            return null;
        }
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        if ( processInstance == null ) {
            throw new IllegalArgumentException( "Could not find process instance for id " + processInstanceId );
        }
        if ( processInstance instanceof WorkflowProcessInstance ) { 
        	return ((WorkflowProcessInstance) processInstance).getVariable(variableId);
        } else { 
            throw new IllegalStateException("Could not retrieve variable " + variableId 
                    + " because process instance " + processInstanceId + " was inaccessible!");
        }
    }

    public String toString() {
        return "session.getProcessInstanceVariable(" + processInstanceId + ", " + variableId +");";
    }

}
