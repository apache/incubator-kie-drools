/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.compiler.xml.processes;

import org.jbpm.compiler.xml.Parser;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.instance.rule.RuleType;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import static org.jbpm.workflow.instance.rule.RuleType.DRL_LANG;

public class RuleSetNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new RuleSetNode();
    }

    @Override
    public void handleNode(final Node node, final Element element, final String uri,
            final String localName, final Parser parser)
            throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        RuleSetNode ruleSetNode = (RuleSetNode) node;
        String ruleFlowGroup = element.getAttribute("ruleFlowGroup");
        String language = element.getAttribute("implementation");
        if (language == null || language.equalsIgnoreCase("##unspecified") || language.isEmpty()) {
            language = DRL_LANG;
        }
        if (ruleFlowGroup != null && ruleFlowGroup.length() > 0) {
            ruleSetNode.setRuleType(RuleType.of(ruleFlowGroup, language));
        }
    }

    @SuppressWarnings("unchecked")
    public Class generateNodeFor() {
        return RuleSetNode.class;
    }

    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
        RuleSetNode ruleSetNode = (RuleSetNode) node;
        writeNode("ruleSet", ruleSetNode, xmlDump, includeMeta);
        RuleType ruleType = ruleSetNode.getRuleType();
        if (ruleType != null) {
            if (!ruleType.isDecision()) {
                xmlDump.append("ruleFlowGroup=\"" + ruleType.getName() + "\" ");
            }
        }
        xmlDump.append(" implementation=\"" + ruleSetNode.getLanguage() + "\" ");
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
