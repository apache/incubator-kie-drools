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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.core.command.IdentifiableResult;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import org.kie.internal.command.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

@XmlAccessorType(XmlAccessType.NONE)
public class CreateProcessInstanceCommand implements GenericCommand<ProcessInstance>, IdentifiableResult {

    @XmlAttribute(required = true)
    private String processId;

    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement(name="parameter")
    private Map<String, Object> parameters = new HashMap<String, Object>();

    @XmlElementWrapper(name="data")
    private List<Object> data = null;
    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    public CreateProcessInstanceCommand() {
    }

    public CreateProcessInstanceCommand(String processId) {
        this.processId = processId;
    }

    public CreateProcessInstanceCommand(String processId, String outIdentifier) {
        this(processId);
        this.outIdentifier = outIdentifier;
    }

    public CreateProcessInstanceCommand(String processId, Map<String, Object> parameters) {
        this(processId);
        this.parameters = parameters;
    }

    public CreateProcessInstanceCommand(String processId, Map<String, Object> parameters, String outIdentifier) {
        this(processId, outIdentifier);
        this.parameters = parameters;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
	if (parameters != null) {
        	this.parameters = new HashMap<String, Object>(parameters);
	} else {
        	this.parameters = new HashMap<String, Object>();
	}
    }

    public void putParameter(String key, Object value) {
        getParameters().put(key, value);
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public ProcessInstance execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();

        if (data != null) {
            for (Object o: data) {
                ksession.insert(o);
            }
        }
        ProcessInstance processInstance = (ProcessInstance) ksession.createProcessInstance(processId, parameters);
        if ( this.outIdentifier != null ) {
            ((ExecutionResultImpl) ((KnowledgeCommandContext) context).getExecutionResults()).getResults().put(this.outIdentifier,
                                                                                                               processInstance.getId());
        }
        return processInstance;
    }

    public String toString() {
        String result = "session.createProcessInstance(" + processId + ", [";
        if (parameters != null) {
            int i = 0;
            for (Map.Entry<String, Object> entry: parameters.entrySet()) {
                if (i++ > 0) {
                    result += ", ";
                }
                result += entry.getKey() + "=" + entry.getValue();
            }
        }
        result += "]);";
        return result;
    }
}
