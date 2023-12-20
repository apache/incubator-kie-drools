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

import java.util.List;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.command.CorrelationKeyCommand;
import org.kie.internal.command.RegistryContext;
import org.kie.internal.jaxb.CorrelationKeyXmlAdapter;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class StartProcessFromNodeIdsCommand extends StartProcessCommand implements CorrelationKeyCommand {

    private static final long serialVersionUID = -6839934578407787510L;

    @XmlElementWrapper(name = "node-id-list")
    @XmlElement(name = "node-id")
    private List<String> nodeIds;

    @XmlElement(name = "correlation-key")
    @XmlJavaTypeAdapter(value = CorrelationKeyXmlAdapter.class)
    private CorrelationKey correlationKey;

    public StartProcessFromNodeIdsCommand() {
    }


    public StartProcessFromNodeIdsCommand(String processId, String outIdentifier) {
        super(processId);
        setOutIdentifier(outIdentifier);
    }

    public StartProcessFromNodeIdsCommand(String processId, Map<String, Object> parameters) {
        super(processId);
        setParameters(parameters);
    }

    public StartProcessFromNodeIdsCommand(String processId, Map<String, Object> parameters, String outIdentifier) {
        this(processId, outIdentifier);
        setParameters(parameters);
    }

    public List<String> getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(List<String> nodeIds) {
        this.nodeIds = nodeIds;
    }

    public void setCorrelationKey(CorrelationKey key) {
        this.correlationKey = key;
    }

    public CorrelationKey getCorrelationKey() {
        return correlationKey;
    }

    @Override
    public ProcessInstance execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        if (getData() != null) {
            for (Object o : getData()) {
                ksession.insert(o);
            }
        }

        String[] ids = nodeIds != null ? nodeIds.stream().toArray(String[]::new) : new String[0];

        ProcessInstance processInstance = null;
        if (correlationKey == null) {
            processInstance = ksession.startProcessFromNodeIds(getProcessId(), getParameters(), ids);
        } else {
            processInstance = ((CorrelationAwareProcessRuntime) ksession).startProcessFromNodeIds(getProcessId(), correlationKey, getParameters(), ids);
        }
        if ( getOutIdentifier() != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(getOutIdentifier(), processInstance.getId());
        }
        return processInstance;
    }

}
