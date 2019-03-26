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

package org.jbpm.compiler.xml.processes;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class RuleSetNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new RuleSetNode();
    }

    public void handleNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        RuleSetNode ruleSetNode = (RuleSetNode) node;
        String ruleFlowGroup = element.getAttribute("ruleFlowGroup");
        if (ruleFlowGroup != null && ruleFlowGroup.length() > 0) {
        	ruleSetNode.setRuleFlowGroup(ruleFlowGroup);
        }
    }

    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return RuleSetNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		RuleSetNode ruleSetNode = (RuleSetNode) node;
		writeNode("ruleSet", ruleSetNode, xmlDump, includeMeta);
        String ruleFlowGroup = ruleSetNode.getRuleFlowGroup();
        if (ruleFlowGroup != null) {
            xmlDump.append("ruleFlowGroup=\"" + ruleFlowGroup + "\" ");
        }
        if (ruleSetNode.getTimers() != null || (includeMeta && containsMetaData(ruleSetNode))) {
            xmlDump.append(">\n");
            if (ruleSetNode.getTimers() != null) {
            	writeTimers(ruleSetNode.getTimers(), xmlDump);
            }
            if (includeMeta) {
            	writeMetaData(ruleSetNode, xmlDump);
            }
            endNode("ruleSet", xmlDump);
        } else {
            endNode(xmlDump);
        }
	}

}
