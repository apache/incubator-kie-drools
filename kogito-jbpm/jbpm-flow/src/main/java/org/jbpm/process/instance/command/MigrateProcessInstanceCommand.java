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

import java.util.HashMap;
import java.util.Map;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.internal.command.RegistryContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name = "get-completed-tasks-command")
@XmlAccessorType(XmlAccessType.NONE)
public class MigrateProcessInstanceCommand implements ExecutableCommand<Void>, KogitoProcessInstanceIdCommand {

    private static final long serialVersionUID = 6L;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String processInstanceId;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String processId;

    @XmlElement
    private Map<String, Long> nodeMapping;

    public MigrateProcessInstanceCommand(String processInstanceId, String processId) {
        this.processInstanceId = processInstanceId;
        this.processId = processId;
    }

    public MigrateProcessInstanceCommand(String processInstanceId, String processId, Map<String, Long> nodeMapping) {
        this.processInstanceId = processInstanceId;
        this.processId = processId;
        this.nodeMapping = nodeMapping;
    }

    @Override
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Map<String, Long> getNodeMapping() {
        return nodeMapping;
    }

    public void setNodeMapping(Map<String, Long> nodeMapping) {
        this.nodeMapping = nodeMapping;
    }

    public Void execute(Context context) {
        KogitoProcessRuntime runtime = (KogitoProcessRuntime) ((RegistryContext) context).lookup(KieSession.class);
        WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl) runtime.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            throw new IllegalArgumentException("Could not find process instance " + processInstanceId);
        }
        if (processId == null) {
            throw new IllegalArgumentException("Null process id");
        }

        WorkflowProcess process = (WorkflowProcess) runtime.getKieBase().getProcess(processId);
        if (process == null) {
            throw new IllegalArgumentException("Could not find process " + processId);
        }
        if (processInstance.getProcessId().equals(processId)) {
            return null;
        }
        synchronized (processInstance) {
            org.kie.api.definition.process.Process oldProcess = processInstance.getProcess();
            processInstance.disconnect();
            processInstance.setProcess(oldProcess);
            if (nodeMapping == null) {
                nodeMapping = new HashMap<>();
            }
            updateNodeInstances(processInstance, nodeMapping);
            processInstance.setKnowledgeRuntime((InternalKnowledgeRuntime) runtime);
            processInstance.setProcess(process);
            processInstance.reconnect();
        }
        return null;
    }

    private void updateNodeInstances(NodeInstanceContainer nodeInstanceContainer, Map<String, Long> nodeMapping) {
        for (NodeInstance nodeInstance : nodeInstanceContainer.getNodeInstances()) {
            String oldNodeId = ((NodeImpl) ((org.jbpm.workflow.instance.NodeInstance) nodeInstance).getNode()).getUniqueId();
            Long newNodeId = nodeMapping.get(oldNodeId);
            if (newNodeId == null) {
                newNodeId = nodeInstance.getNodeId();
            }
            ((NodeInstanceImpl) nodeInstance).setNodeId(newNodeId);
            if (nodeInstance instanceof NodeInstanceContainer) {
                updateNodeInstances((NodeInstanceContainer) nodeInstance, nodeMapping);
            }
        }
    }

    public String toString() {
        return "migrateProcessInstance(" + processInstanceId + ", \"" + processId + "\");";
    }

}
