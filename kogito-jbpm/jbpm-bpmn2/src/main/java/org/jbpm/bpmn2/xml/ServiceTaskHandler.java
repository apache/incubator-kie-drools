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

package org.jbpm.bpmn2.xml;

import java.util.List;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.Interface;
import org.jbpm.bpmn2.core.Interface.Operation;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ServiceTaskHandler extends TaskHandler {
    
    protected Node createNode(Attributes attrs) {
        return new WorkItemNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return Node.class;
    }
    
    @SuppressWarnings("unchecked")
    protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        WorkItemNode workItemNode = (WorkItemNode) node;
        String operationRef = element.getAttribute("operationRef");
        String implementation = element.getAttribute("implementation");
        List<Interface> interfaces = (List<Interface>) ((ProcessBuildData) parser.getData()).getMetaData("Interfaces");
        
        workItemNode.setMetaData("OperationRef", operationRef);
        workItemNode.setMetaData("Implementation", implementation);
        workItemNode.setMetaData("Type", "Service Task");
        if (interfaces != null) {
//            throw new IllegalArgumentException("No interfaces found");
        
	        Operation operation = null;
	        for (Interface i: interfaces) {
	            operation = i.getOperation(operationRef);
	            if (operation != null) {
	                break;
	            }
	        }
	        if (operation == null) {
	            throw new IllegalArgumentException("Could not find operation " + operationRef);
	        }
	        // avoid overriding parameters set by data input associations
	        if (workItemNode.getWork().getParameter("Interface") == null) {
	            workItemNode.getWork().setParameter("Interface", operation.getInterface().getName());
	        }
	        if (workItemNode.getWork().getParameter("Operation") == null) {
	            workItemNode.getWork().setParameter("Operation", operation.getName());
	        }
	        if (workItemNode.getWork().getParameter("ParameterType") == null) {
	            workItemNode.getWork().setParameter("ParameterType", operation.getMessage().getType());
	        }
	        // parameters to support web service invocation 
	        if (implementation != null) {
	            workItemNode.getWork().setParameter("interfaceImplementationRef", operation.getInterface().getImplementationRef());
	            workItemNode.getWork().setParameter("operationImplementationRef", operation.getImplementationRef());
	            workItemNode.getWork().setParameter("implementation", implementation);
	        }
        }
    }
    
    protected String getTaskName(final Element element) {
        return "Service Task";
    }
    
    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
        throw new IllegalArgumentException("Writing out should be handled by TaskHandler");
    }
    
}
