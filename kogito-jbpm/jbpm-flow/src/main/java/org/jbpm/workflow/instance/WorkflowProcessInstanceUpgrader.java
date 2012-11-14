/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.workflow.instance;

import java.util.HashMap;
import java.util.Map;

import org.drools.common.InternalKnowledgeRuntime;
import org.kie.definition.process.WorkflowProcess;
import org.kie.runtime.KnowledgeRuntime;
import org.kie.runtime.process.NodeInstance;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;

public class WorkflowProcessInstanceUpgrader {

    public static void upgradeProcessInstance(
    		KnowledgeRuntime kruntime,
    		long processInstanceId,
    		String processId,
    		Map<String, Long> nodeMapping) {
    	if (nodeMapping == null) {
    		nodeMapping = new HashMap<String, Long>();
    	}
        WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl)
            kruntime.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            throw new IllegalArgumentException("Could not find process instance " + processInstanceId);
        }
        if (processId == null) {
            throw new IllegalArgumentException("Null process id");
        }
        WorkflowProcess process = (WorkflowProcess)
            kruntime.getKnowledgeBase().getProcess(processId);
        if (process == null) {
            throw new IllegalArgumentException("Could not find process " + processId);
        }
        if (processInstance.getProcessId().equals(processId)) {
            return;
        }
        synchronized (processInstance) {
        	org.kie.definition.process.Process oldProcess = processInstance.getProcess();
	        processInstance.disconnect();
	        processInstance.setProcess(oldProcess);
	        updateNodeInstances(processInstance, nodeMapping);
	        processInstance.setKnowledgeRuntime((InternalKnowledgeRuntime) kruntime);
	        processInstance.setProcess(process);
	        processInstance.reconnect();
		}
    }
    
    private static void updateNodeInstances(NodeInstanceContainer nodeInstanceContainer, Map<String, Long> nodeMapping) {
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

}
