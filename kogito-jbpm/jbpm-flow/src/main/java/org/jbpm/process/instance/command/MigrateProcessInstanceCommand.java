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

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.drools.core.command.impl.RegistryContext;
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
import org.kie.internal.command.ProcessInstanceIdCommand;

@XmlRootElement(name="get-completed-tasks-command")
@XmlAccessorType(XmlAccessType.NONE)
public class MigrateProcessInstanceCommand implements ExecutableCommand<Void>, ProcessInstanceIdCommand  {
	
    private static final long serialVersionUID = 6L;

    @XmlElement
    @XmlSchemaType(name="long")
	private Long processInstanceId;
	
    @XmlElement
    @XmlSchemaType(name="string")
    private String processId;
   
    @XmlElement
    private Map<String, Long> nodeMapping;

    public MigrateProcessInstanceCommand(Long processInstanceId, String processId) {
    	this.processInstanceId = processInstanceId;
    	this.processId = processId;
    }
    
    public MigrateProcessInstanceCommand(Long processInstanceId, String processId, Map<String, Long> nodeMapping) {
    	this.processInstanceId = processInstanceId;
    	this.processId = processId;
    	this.nodeMapping = nodeMapping;
    }
   
    @Override
    public Long getProcessInstanceId() {
		return processInstanceId;
	}

    @Override
	public void setProcessInstanceId(Long processInstanceId) {
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

	public Void execute(Context context ) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl)
    		ksession.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            throw new IllegalArgumentException("Could not find process instance " + processInstanceId);
        }
        if (processId == null) {
            throw new IllegalArgumentException("Null process id");
        }
        WorkflowProcess process = (WorkflowProcess)
            ksession.getKieBase().getProcess(processId);
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
	    		nodeMapping = new HashMap<String, Long>();
	    	}
	        updateNodeInstances(processInstance, nodeMapping);
	        processInstance.setKnowledgeRuntime((InternalKnowledgeRuntime) ksession);
	        processInstance.setProcess(process);
	        processInstance.reconnect();
		}
        return null;
    }

    private void updateNodeInstances(NodeInstanceContainer nodeInstanceContainer, Map<String, Long> nodeMapping) {
        for (NodeInstance nodeInstance: nodeInstanceContainer.getNodeInstances()) {
            String oldNodeId = ((NodeImpl)
        		((org.jbpm.workflow.instance.NodeInstance) nodeInstance).getNode()).getUniqueId();
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
    	return "migrateProcessInstance(" + processInstanceId +", \"" + processId + "\");";
    }
    
}
