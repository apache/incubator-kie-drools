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

import org.drools.compiler.compiler.xml.XmlDumper;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.core.Error;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.FaultNode;
import org.kie.api.definition.process.NodeContainer;
import org.xml.sax.Attributes;

public class FaultNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        throw new IllegalArgumentException("Reading in should be handled by end event handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return FaultNode.class;
    }

    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        FaultNode faultNode = (FaultNode) node;
		writeNode("endEvent", faultNode, xmlDump, metaDataType);
		xmlDump.append(">" + EOL);
        writeExtensionElements(node, xmlDump);
        if (faultNode.getFaultVariable() != null) {
            xmlDump.append("      <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(faultNode) + "_Input\" name=\"error\" />" + EOL);
            xmlDump.append("      <dataInputAssociation>" + EOL);
            xmlDump.append(
                "        <sourceRef>" + XmlDumper.replaceIllegalChars(faultNode.getFaultVariable()) + "</sourceRef>" + EOL +
                "        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(faultNode) + "_Input</targetRef>" + EOL);
            xmlDump.append("      </dataInputAssociation>" + EOL);
            xmlDump.append("      <inputSet>" + EOL);
            xmlDump.append("        <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(faultNode) + "_Input</dataInputRefs>" + EOL);
            xmlDump.append("      </inputSet>" + EOL);
        }
        if (faultNode.isTerminateParent()) {
            String errorCode = faultNode.getFaultName();
            String errorId = getErrorIdForErrorCode(errorCode, faultNode);
            xmlDump.append("      <errorEventDefinition errorRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(errorId) + "\" />" + EOL);
        } else {
            xmlDump.append("      <escalationEventDefinition escalationRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(faultNode.getFaultName()) + "\" />" + EOL);
        }
		endNode("endEvent", xmlDump);
	}

}
