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

import java.util.HashSet;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.bpmn2.xml.di.BPMNPlaneHandler.ProcessInfo;
import org.jbpm.bpmn2.xml.di.BPMNShapeHandler.NodeInfo;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BPMNEdgeHandler extends BaseAbstractHandler implements Handler {

    public BPMNEdgeHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = false;
    }
    
    protected void initValidParents() {
        this.validParents = new HashSet<Class<?>>();
        this.validParents.add(ProcessInfo.class);
    }
    
    protected void initValidPeers() {
        this.validPeers = new HashSet<Class<?>>();
        this.validPeers.add(null);
        this.validPeers.add(NodeInfo.class);
        this.validPeers.add(ConnectionInfo.class);
    }
    
    public Object start(final String uri, final String localName,
                        final Attributes attrs, final ExtensibleXmlParser parser)
            throws SAXException {
        parser.startElementBuilder(localName, attrs);

        final String elementRef = attrs.getValue("bpmnElement");
        ConnectionInfo info = new ConnectionInfo(elementRef);
        ProcessInfo processInfo = (ProcessInfo) parser.getParent();
        processInfo.addConnectionInfo(info);
        return info;
    }

    public Object end(final String uri, final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        Element element = parser.endElementBuilder();
        // now get bendpoints
        String bendpoints = null;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode instanceof Element) {
            String nodeName = xmlNode.getNodeName();
            if ("waypoint".equals(nodeName)) {
                // ignore first and last waypoint
                String x = ((Element) xmlNode).getAttribute("x");
                String y = ((Element) xmlNode).getAttribute("y");
                try {
                    int xValue = new Float(x).intValue();
                    int yValue = new Float(y).intValue();
                    if (bendpoints == null) {
                        bendpoints = "[";
                    } else if (xmlNode.getNextSibling() != null) {
                        bendpoints += xValue + "," + yValue;
                        bendpoints += ";";
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid bendpoint value", e);
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
        ConnectionInfo connectionInfo = (ConnectionInfo) parser.getCurrent();
        if (bendpoints != null && bendpoints.length() > 1) {
            connectionInfo.setBendpoints(bendpoints + "]");
        }
        return connectionInfo;
    }

    public Class<?> generateNodeFor() {
        return ConnectionInfo.class;
    }
    
    public static class ConnectionInfo {
        
        private String elementRef;
        private String bendpoints;

        public ConnectionInfo(String elementRef) {
            this.elementRef = elementRef;
        }
        
        public String getElementRef() {
            return elementRef;
        }

        public String getBendpoints() {
            return bendpoints;
        }

        public void setBendpoints(String bendpoints) {
            this.bendpoints = bendpoints;
        }
        
    }

}
