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
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.commands.IdentifiableResult;
import org.drools.commands.jaxb.JaxbMapAdapter;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.command.RegistryContext;

@XmlAccessorType(XmlAccessType.NONE)
public class CreateProcessInstanceCommand implements ExecutableCommand<ProcessInstance>, IdentifiableResult {

    @XmlAttribute(required = true)
    private String processId;

    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement(name="parameter")
    private Map<String, Object> parameters = new HashMap<>();

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
        	this.parameters = new HashMap<>(parameters);
	} else {
        	this.parameters = new HashMap<>();
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
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        if (data != null) {
            for (Object o: data) {
                ksession.insert(o);
            }
        }
        ProcessInstance processInstance = ksession.createProcessInstance(processId, parameters);
        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, processInstance.getId());
        }
        return processInstance;
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("session.createProcessInstance(");
        result.append(processId);
        result.append(", [");
        if (parameters != null) {
            int i = 0;
            for (final Map.Entry<String, Object> entry: parameters.entrySet()) {
                if (i++ > 0) {
                    result.append(", ");
                }
                result.append(entry.getKey());
                result.append("=");
                result.append(entry.getValue());
            }
        }
        result.append("]);");
        return result.toString();
    }
}
