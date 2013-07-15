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

package org.jbpm.bpmn2.xml;

import java.util.Map;

import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.node.Split;
import org.xml.sax.Attributes;

public class SplitHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
    	throw new IllegalArgumentException("Reading in should be handled by gateway handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return Split.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
		Split split = (Split) node;
		switch (split.getType()) {
			case Split.TYPE_AND:
				writeNode("parallelGateway", node, xmlDump, metaDataType);
				break;
			case Split.TYPE_XOR:
				writeNode("exclusiveGateway", node, xmlDump, metaDataType);
				for (Map.Entry<ConnectionRef, Constraint> entry: split.getConstraints().entrySet()) {
					if (entry.getValue() != null && entry.getValue().isDefault()) {
						xmlDump.append("default=\"" +
							XmlBPMNProcessDumper.getUniqueNodeId(split) + "-" +
							XmlBPMNProcessDumper.getUniqueNodeId(node.getNodeContainer().getNode(entry.getKey().getNodeId())) + 
							"\" ");
						break;
					}
				}
				break;
			case Split.TYPE_OR:
                writeNode("inclusiveGateway", node, xmlDump, metaDataType);
				for (Map.Entry<ConnectionRef, Constraint> entry: split.getConstraints().entrySet()) {
					if (entry.getValue() != null && entry.getValue().isDefault()) {
						xmlDump.append("default=\"" +
							XmlBPMNProcessDumper.getUniqueNodeId(split) + "-" +
							XmlBPMNProcessDumper.getUniqueNodeId(node.getNodeContainer().getNode(entry.getKey().getNodeId())) + 
							"\" ");
						break;
					}
				}
                break;
			case Split.TYPE_XAND:
				writeNode("eventBasedGateway", node, xmlDump, metaDataType);
				break;
            default:
				writeNode("complexGateway", node, xmlDump, metaDataType);
		}
		xmlDump.append("gatewayDirection=\"Diverging\" ");
		endNode(xmlDump);
	}

}
