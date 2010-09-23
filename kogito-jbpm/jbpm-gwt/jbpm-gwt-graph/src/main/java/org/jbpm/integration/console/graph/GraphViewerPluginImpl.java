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

package org.jbpm.integration.console.graph;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.definition.process.Node;
import org.drools.definition.process.NodeContainer;
import org.drools.definition.process.Process;
import org.drools.definition.process.WorkflowProcess;
import org.drools.io.ResourceFactory;
import org.jboss.bpm.console.client.model.ActiveNodeInfo;
import org.jboss.bpm.console.client.model.DiagramInfo;
import org.jboss.bpm.console.client.model.DiagramNodeInfo;
import org.jboss.bpm.console.server.plugin.GraphViewerPlugin;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceDbLog;
import org.jbpm.process.audit.ProcessInstanceLog;

/**
 * @author Kris Verlaenen
 */
public class GraphViewerPluginImpl implements GraphViewerPlugin {

	public List<ActiveNodeInfo> getActiveNodeInfo(String instanceId) {
		ProcessInstanceLog processInstance = ProcessInstanceDbLog.findProcessInstance(new Long(instanceId));
		if (processInstance == null) {
			throw new IllegalArgumentException("Could not find process instance " + instanceId);
		}
		Map<String, NodeInstanceLog> nodeInstances = new HashMap<String, NodeInstanceLog>();
		for (NodeInstanceLog nodeInstance: ProcessInstanceDbLog.findNodeInstances(new Long(instanceId))) {
			if (nodeInstance.getType() == NodeInstanceLog.TYPE_ENTER) {
				nodeInstances.put(nodeInstance.getNodeInstanceId(), nodeInstance);
			} else {
				nodeInstances.remove(nodeInstance.getNodeInstanceId());
			}
		}
		if (!nodeInstances.isEmpty()) {
			List<ActiveNodeInfo> result = new ArrayList<ActiveNodeInfo>();
			for (NodeInstanceLog nodeInstance: nodeInstances.values()) {
				boolean found = false;
				DiagramInfo diagramInfo = getDiagramInfo(processInstance.getProcessId());
				for (DiagramNodeInfo nodeInfo: diagramInfo.getNodeList()) {
					if (nodeInfo.getName().equals("id=" + nodeInstance.getNodeId())) {
						result.add(new ActiveNodeInfo(diagramInfo.getWidth(), diagramInfo.getHeight(), nodeInfo));
						found = true;
						break;
					}
				}
				if (!found) {
					throw new IllegalArgumentException("Could not find info for node "
						+ nodeInstance.getNodeId() + " of process " + processInstance.getProcessId());
				}
			}
			return result;
		}
		return null;
	}

	public DiagramInfo getDiagramInfo(String processId) {
		KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("Guvnor default");
		kagent.applyChangeSet(ResourceFactory.newClassPathResource("ChangeSet.xml"));
		kagent.monitorResourceChangeEvents(false);
		KnowledgeBase kbase = kagent.getKnowledgeBase();
		Process process = kbase.getProcess(processId);
		if (process == null) {
			return null;
		}

		DiagramInfo result = new DiagramInfo();
		// TODO: diagram width and height?
		result.setWidth(932);
		result.setHeight(541);
		List<DiagramNodeInfo> nodeList = new ArrayList<DiagramNodeInfo>();
		if (process instanceof WorkflowProcess) {
			addNodesInfo(nodeList, ((WorkflowProcess) process).getNodes(), "id=");
		}
		result.setNodeList(nodeList);
		return result;
	}
	
	private void addNodesInfo(List<DiagramNodeInfo> nodeInfos, Node[] nodes, String prefix) {
		for (Node node: nodes) {
			nodeInfos.add(new DiagramNodeInfo(
				prefix + node.getId(),
				(Integer) node.getMetaData("x"),
				(Integer) node.getMetaData("y"),
				(Integer) node.getMetaData("width"),
				(Integer) node.getMetaData("height")));
			if (node instanceof NodeContainer) {
				addNodesInfo(nodeInfos, ((NodeContainer) node).getNodes(), prefix + node.getId() + ":");
			}
		}
	}

	public byte[] getProcessImage(String processId) {
		InputStream is = GraphViewerPluginImpl.class.getResourceAsStream("/" + processId + ".png");
		if (is != null) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				transfer(is, os);
			} catch (IOException e) {
				throw new RuntimeException("Could not read process image: " + e.getMessage());
			}
			return os.toByteArray();
		}
		return null;
	}
	
	private static final int BUFFER_SIZE = 512;

	public static int transfer(InputStream in, OutputStream out) throws IOException {
		int total = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = in.read(buffer);
		while (bytesRead != -1) {
			out.write(buffer, 0, bytesRead);
			total += bytesRead;
			bytesRead = in.read(buffer);
		}
		return total;
	}

	public URL getDiagramURL(String id) {
		return GraphViewerPluginImpl.class.getResource("/" + id + ".png");
	}

	public List<ActiveNodeInfo> getNodeInfoForActivities(
			String processDefinitionId, List<String> activities) {
		// TODO Auto-generated method stub
		return new ArrayList<ActiveNodeInfo>();
	}

}
