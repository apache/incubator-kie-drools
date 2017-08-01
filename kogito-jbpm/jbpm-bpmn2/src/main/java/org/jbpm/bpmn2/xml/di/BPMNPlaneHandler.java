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

package org.jbpm.bpmn2.xml.di;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.xml.di.BPMNEdgeHandler.ConnectionInfo;
import org.jbpm.bpmn2.xml.di.BPMNShapeHandler.NodeInfo;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.Process;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BPMNPlaneHandler extends BaseAbstractHandler implements Handler {

    public BPMNPlaneHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = false;
    }
    
    protected void initValidParents() {
        this.validParents = new HashSet<Class<?>>();
        this.validParents.add(Definitions.class);
    }
    
    protected void initValidPeers() {
        this.validPeers = new HashSet<Class<?>>();
		this.validPeers.add(null);
        this.validPeers.add(Process.class);
    }
    
    public Object start(final String uri, final String localName,
                        final Attributes attrs, final ExtensibleXmlParser parser)
            throws SAXException {
        parser.startElementBuilder(localName, attrs);

        final String processRef = attrs.getValue("bpmnElement");
        ProcessInfo info = new ProcessInfo(processRef);
        return info;
    }

    public Object end(final String uri, final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        ProcessInfo processInfo = (ProcessInfo) parser.getCurrent();
        List<Process> processes = ((ProcessBuildData) parser.getData()).getProcesses();
        RuleFlowProcess process = null;
        for (Process p : processes) {
            if (p.getId() != null && p.getId().equals(processInfo.getProcessRef())) {
                process = (RuleFlowProcess) p;
                break;
            }
        }
        if (process != null) {
	        for (NodeInfo nodeInfo: processInfo.getNodeInfos()) {
	        	processNodeInfo(nodeInfo, process.getNodes());
	        }
	        postProcessNodeOffset(process.getNodes(), 0, 0);
	        for (ConnectionInfo connectionInfo: processInfo.getConnectionInfos()) {
	            if (connectionInfo.getBendpoints() != null) {
	            	processConnectionInfo(connectionInfo, process.getNodes());
	            }
	        }
        }
        return processInfo;
    }
    
    private boolean processNodeInfo(NodeInfo nodeInfo, Node[] nodes) {
    	if (nodeInfo == null || nodeInfo.getNodeRef() == null) {
    		return false;
    	}
        for (Node node: nodes) {
            String id = (String) node.getMetaData().get("UniqueId");
            if (nodeInfo.getNodeRef().equals(id)) {
                ((org.jbpm.workflow.core.Node) node).setMetaData("x", nodeInfo.getX());
                ((org.jbpm.workflow.core.Node) node).setMetaData("y", nodeInfo.getY());
                ((org.jbpm.workflow.core.Node) node).setMetaData("width", nodeInfo.getWidth());
                ((org.jbpm.workflow.core.Node) node).setMetaData("height", nodeInfo.getHeight());
                return true;
            }
        	if (node instanceof NodeContainer) {
        		boolean found = processNodeInfo(nodeInfo, ((NodeContainer) node).getNodes());
        		if (found) {
        			return true;
        		}
            }
        }
        return false;
    }
    
    private void postProcessNodeOffset(Node[] nodes, int xOffset, int yOffset) {
    	for (Node node: nodes) {
    		Integer x = (Integer) node.getMetaData().get("x");
    		if (x != null) {
    			((org.jbpm.workflow.core.Node) node).setMetaData("x", x - xOffset);
    		}
    		Integer y = (Integer) node.getMetaData().get("y");
    		if (y != null) {
    			((org.jbpm.workflow.core.Node) node).setMetaData("y", y - yOffset);
    		}
    		if (node instanceof NodeContainer) {
    			postProcessNodeOffset(((NodeContainer) node).getNodes(), xOffset + (x == null ? 0 : x), yOffset + (y == null ? 0 : y));
    		}
    	}
    }
    
    private boolean processConnectionInfo(ConnectionInfo connectionInfo, Node[] nodes) {
        for (Node node: nodes) {
        	for (List<Connection> connections: node.getOutgoingConnections().values()) {
        		for (Connection connection: connections) {
                    String id = (String) connection.getMetaData().get("UniqueId");
                    if (id != null && id.equals(connectionInfo.getElementRef())) {
                        ((ConnectionImpl) connection).setMetaData(
                            "bendpoints", connectionInfo.getBendpoints());
                        return true;
                    }
        		}
        	}
        	if (node instanceof NodeContainer) {
        		boolean found = processConnectionInfo(connectionInfo, ((NodeContainer) node).getNodes());
        		if (found) {
        			return true;
        		}
        	}
		}
        return false;
    }

    public Class<?> generateNodeFor() {
        return ProcessInfo.class;
    }
    
    public static class ProcessInfo {
        
        private String processRef;
        private List<NodeInfo> nodeInfos = new ArrayList<NodeInfo>();
        private List<ConnectionInfo> connectionInfos = new ArrayList<ConnectionInfo>();

        public ProcessInfo(String processRef) {
            this.processRef = processRef;
        }
        
        public String getProcessRef() {
            return processRef;
        }
        
        public void addNodeInfo(NodeInfo nodeInfo) {
            this.nodeInfos.add(nodeInfo);
        }
        
        public List<NodeInfo> getNodeInfos() {
            return nodeInfos;
        }
        
        public void addConnectionInfo(ConnectionInfo connectionInfo) {
            connectionInfos.add(connectionInfo);
        }
        
        public List<ConnectionInfo> getConnectionInfos() {
            return connectionInfos;
        }
        
    }

}
