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
import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.command.Context;
import org.kie.internal.command.ProcessInstanceIdCommand;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class SetProcessInstanceVariablesCommand implements ExecutableCommand<Void>, ProcessInstanceIdCommand {

	/** Generated serial version UID */
    private static final long serialVersionUID = 7802415761845739379L;

    @XmlAttribute(required = true)
    private Long processInstanceId;

    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement(name="variables")
    private Map<String, Object> variables = new HashMap<String, Object>();

    public SetProcessInstanceVariablesCommand() {
    }

    public SetProcessInstanceVariablesCommand(long processInstanceId, 
    		Map<String, Object> variables) {
        this.processInstanceId = processInstanceId;
        this.variables = variables;
    }

    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        if (processInstance != null) {
        	if (variables != null) {
	        	for (Map.Entry<String, Object> entry: variables.entrySet()) {
	        		((WorkflowProcessInstance) processInstance).setVariable(entry.getKey(), entry.getValue());
	        	}
        	}
        }
        return null;
    }

    public String toString() {
        String result = "WorkflowProcessInstance processInstance = (WorkflowProcessInstance) session.getProcessInstance(" + processInstanceId + ");";
        if (variables != null) {
            for (Map.Entry<String, Object> entry: variables.entrySet()) {
                result += "\nprocessInstance.setVariable(\"" + entry.getKey() + "\", " 
            		+ (entry.getValue() == null ? "null" : entry.getValue().toString()) + ");";
            }
        }
        return result;
    }

}
