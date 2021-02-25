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
package org.jbpm.bpmn2.xml;

import java.util.Arrays;
import java.util.List;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.DynamicNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static org.jbpm.compiler.xml.processes.DynamicNodeHandler.AUTOCOMPLETE_COMPLETION_CONDITION;
import static org.jbpm.ruleflow.core.Metadata.COMPLETION_CONDITION;
import static org.jbpm.ruleflow.core.Metadata.CUSTOM_ACTIVATION_CONDITION;

public class AdHocSubProcessHandler extends CompositeContextNodeHandler {

    protected static final List<String> AUTOCOMPLETE_EXPRESSIONS = Arrays.asList(
            "getActivityInstanceAttribute(\"numberOfActiveInstances\") == 0", AUTOCOMPLETE_COMPLETION_CONDITION);

    @Override
    protected Node createNode(Attributes attrs) {
        DynamicNode result = new DynamicNode();
        VariableScope variableScope = new VariableScope();
        result.addContext(variableScope);
        result.setDefaultContext(variableScope);
        return result;
    }

    @Override
    public Class<?> generateNodeFor() {
        return DynamicNode.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void handleNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        DynamicNode dynamicNode = (DynamicNode) node;
        String cancelRemainingInstances = element.getAttribute("cancelRemainingInstances");
        if ("false".equals(cancelRemainingInstances)) {
            dynamicNode.setCancelRemainingInstances(false);
        }
        // by default it should not autocomplete as it's adhoc
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        dynamicNode.setActivationCondition((String) node.getMetaData().get(CUSTOM_ACTIVATION_CONDITION));
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if (COMPLETION_CONDITION.equals(nodeName)) {
                String expression = xmlNode.getTextContent();
                if (AUTOCOMPLETE_EXPRESSIONS.contains(expression)) {
                    dynamicNode.setAutoComplete(true);
                } else {
                    dynamicNode.setCompletionCondition(expression);
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
        List<SequenceFlow> connections = (List<SequenceFlow>) dynamicNode.getMetaData(ProcessHandler.CONNECTIONS);
        ProcessHandler.linkConnections(dynamicNode, connections);
        ProcessHandler.linkBoundaryEvents(dynamicNode);

        handleScript(dynamicNode, element, "onEntry");
        handleScript(dynamicNode, element, "onExit");
    }

    @Override
    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        DynamicNode dynamicNode = (DynamicNode) node;
        writeNode("adHocSubProcess", dynamicNode, xmlDump, metaDataType);
        if (!dynamicNode.isCancelRemainingInstances()) {
            xmlDump.append(" cancelRemainingInstances=\"false\"");
        }
        xmlDump.append(" ordering=\"Parallel\" >" + EOL);
        writeExtensionElements(dynamicNode, xmlDump);
        // nodes
        List<Node> subNodes = getSubNodes(dynamicNode);
        XmlBPMNProcessDumper.INSTANCE.visitNodes(subNodes, xmlDump, metaDataType);

        // connections
        visitConnectionsAndAssociations(dynamicNode, xmlDump, metaDataType);

        if (dynamicNode.isAutoComplete()) {
            xmlDump.append("    <completionCondition xsi:type=\"tFormalExpression\">" + AUTOCOMPLETE_COMPLETION_CONDITION + "</completionCondition>" + EOL);
        }
        endNode("adHocSubProcess", xmlDump);
    }

}
