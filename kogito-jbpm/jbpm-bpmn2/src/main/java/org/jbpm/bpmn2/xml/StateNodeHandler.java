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

import org.drools.compiler.compiler.xml.XmlDumper;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.StateNode;
import org.xml.sax.Attributes;

public class StateNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        throw new IllegalArgumentException("Reading in should be handled by intermediate catch event handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return StateNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
	    StateNode stateNode = (StateNode) node;
		String condition = (String) stateNode.getMetaData("Condition");
		writeNode("intermediateCatchEvent", stateNode, xmlDump, metaDataType);
		xmlDump.append(">" + EOL);
		writeExtensionElements(node, xmlDump);
		xmlDump.append("      <conditionalEventDefinition>" + EOL);
        xmlDump.append("        <condition xsi:type=\"tFormalExpression\" language=\"" + XmlBPMNProcessDumper.RULE_LANGUAGE + "\">" + XmlDumper.replaceIllegalChars(condition) + "</condition>" + EOL);
        xmlDump.append("      </conditionalEventDefinition>" + EOL);
		endNode("intermediateCatchEvent", xmlDump);
	}

}
