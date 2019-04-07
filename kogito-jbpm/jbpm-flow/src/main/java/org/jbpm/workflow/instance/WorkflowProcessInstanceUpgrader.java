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

package org.jbpm.workflow.instance;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.NodeInstance;

public class WorkflowProcessInstanceUpgrader {

    public static void upgradeProcessInstance( KieRuntime kruntime, long processInstanceId, String processId,
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
                kruntime.getKieBase().getProcess(processId);
        if (process == null) {
            throw new IllegalArgumentException("Could not find process " + processId);
        }
        if (processInstance.getProcessId().equals(processId)) {
            return;
        }
        synchronized (processInstance) {
            org.kie.api.definition.process.Process oldProcess = processInstance.getProcess();
            processInstance.disconnect();
            processInstance.setProcess(oldProcess);
            updateNodeInstances(processInstance, nodeMapping);
            processInstance.setKnowledgeRuntime((InternalKnowledgeRuntime) kruntime);
            processInstance.setProcess(process);
            processInstance.reconnect();
        }
    }

    /**
     * Do the same as upgradeProcessInstance() but user provides mapping by node names, not by node id's
     * @param kruntime
     * @param activeProcessId
     * @param newProcessId
     * @param nodeNamesMapping
     */
    public static void upgradeProcessInstanceByNodeNames(
            KieRuntime kruntime,
            Long fromProcessId,
            String toProcessId,
            Map<String, String> nodeNamesMapping) {

        Map<String, Long> nodeIdMapping = new HashMap<String, Long>();

        String fromProcessIdString = kruntime.getProcessInstance(fromProcessId).getProcessId();
        Process processFrom = kruntime.getKieBase().getProcess(fromProcessIdString);
        Process processTo = kruntime.getKieBase().getProcess(toProcessId);

        for (Map.Entry<String, String> entry : nodeNamesMapping.entrySet()) {
            String from = null;
            Long to = null;

            if (processFrom instanceof WorkflowProcess) {
                from = getNodeId(((WorkflowProcess) processFrom).getNodes(), entry.getKey(), true);
            } else if (processFrom instanceof RuleFlowProcess) {
                from = getNodeId(((RuleFlowProcess) processFrom).getNodes(), entry.getKey(), true);
            } else if (processFrom != null) {
                throw new IllegalArgumentException("Suported processes are WorkflowProcess and RuleFlowProcess, it was:" + processFrom.getClass());
            } else {
                throw new IllegalArgumentException("Can not find process with id: " + fromProcessIdString);
            }

            if (processTo instanceof WorkflowProcess) {
                to = Long.valueOf(getNodeId(((WorkflowProcess) processTo).getNodes(), entry.getValue(), false));
            } else if (processTo instanceof RuleFlowProcess) {
                to = Long.valueOf(getNodeId(((RuleFlowProcess) processTo).getNodes(), entry.getValue(), false));
            } else if (processTo != null) {
                throw new IllegalArgumentException("Suported processes are WorkflowProcess and RuleFlowProcess, it was:" + processTo.getClass());
            } else {
                throw new IllegalArgumentException("Can not find process with id: " + toProcessId);
            }
            nodeIdMapping.put(from, to);
        }

        upgradeProcessInstance(kruntime, fromProcessId, toProcessId, nodeIdMapping);
    }

    private static String getNodeId(Node[] nodes, String nodeName, boolean unique) {

        Stack<Node> nodeStack = new Stack<Node>();
        for (Node node : nodes) {
            nodeStack.push(node);
        }

        Node match = null;
        while (!nodeStack.isEmpty()) {
            Node topNode = nodeStack.pop();

            if (topNode.getName().compareTo(nodeName) == 0) {
                match = topNode;
                break;
            }

            if (topNode instanceof NodeContainer) {
                for (Node node : ((NodeContainer) topNode).getNodes()) {
                    nodeStack.push(node);
                }
            }
        }

        if (match == null) {
            throw new IllegalArgumentException("No node with name " + nodeName);
        }

        String id = "";

        if (unique) {
            while (!(match.getNodeContainer() instanceof Process)) {
                id = ":" + match.getId() + id;
                match = (Node) match.getNodeContainer();
            }
        }

        id = match.getId() + id;

        return id;
    }

    private static void updateNodeInstances(NodeInstanceContainer nodeInstanceContainer, Map<String, Long> nodeMapping) {
        for (NodeInstance nodeInstance : nodeInstanceContainer.getNodeInstances()) {
            String oldNodeId = ((NodeImpl)
                    ((org.jbpm.workflow.instance.NodeInstance) nodeInstance).getNode()).getUniqueId();
            Long newNodeId = nodeMapping.get(oldNodeId);
            if (newNodeId == null) {
                newNodeId = nodeInstance.getNodeId();
            }

            // clean up iteration levels for removed (old) nodes
            Map<String, Integer> iterLevels = ((WorkflowProcessInstanceImpl) nodeInstance.getProcessInstance()).getIterationLevels();
            String uniqueId = (String) ((NodeImpl) nodeInstance.getNode()).getMetaData("UniqueId");
            iterLevels.remove(uniqueId);
            // and now set to new node id
            ((NodeInstanceImpl) nodeInstance).setNodeId(newNodeId);

            if (nodeInstance instanceof NodeInstanceContainer) {
                updateNodeInstances((NodeInstanceContainer) nodeInstance, nodeMapping);
            }
        }

    }

}
