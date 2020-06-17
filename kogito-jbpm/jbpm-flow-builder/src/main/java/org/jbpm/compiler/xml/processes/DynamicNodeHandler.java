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
import org.jbpm.workflow.core.node.DynamicNode;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import static org.jbpm.ruleflow.core.Metadata.COMPLETION_CONDITION;

public class DynamicNodeHandler extends CompositeNodeHandler {

    public static final String AUTOCOMPLETE_COMPLETION_CONDITION = "autocomplete";

    protected Node createNode() {
        return new DynamicNode();
    }

    public Class<?> generateNodeFor() {
        return DynamicNode.class;
    }

    protected String getNodeName() {
        return "dynamic";
    }

    @Override
    protected void handleNode(Node node, Element element, String uri, String localName, ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        DynamicNode dynamicNode = (DynamicNode) node;
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node n = element.getChildNodes().item(i);
            if (COMPLETION_CONDITION.equals(n.getNodeName())) {
                if (AUTOCOMPLETE_COMPLETION_CONDITION.equals(n.getTextContent())) {
                    dynamicNode.setAutoComplete(true);
                } else {
                    dynamicNode.setCompletionCondition(n.getTextContent());
                }
            }
        }
    }
}
