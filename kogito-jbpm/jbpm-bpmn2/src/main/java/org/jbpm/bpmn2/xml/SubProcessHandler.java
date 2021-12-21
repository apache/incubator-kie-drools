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

import java.util.List;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.Association;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.core.IntermediateLink;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.StartNode;
import org.kie.api.definition.process.Connection;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SubProcessHandler extends AbstractNodeHandler {

    protected Node createNode(Attributes attrs) {
        CompositeContextNode subProcessNode = new CompositeContextNode();
        String eventSubprocessAttribute = attrs.getValue("triggeredByEvent");
        if (eventSubprocessAttribute != null && Boolean.parseBoolean(eventSubprocessAttribute)) {
            subProcessNode = new EventSubProcessNode();
        }
        VariableScope variableScope = new VariableScope();
        subProcessNode.addContext(variableScope);
        subProcessNode.setDefaultContext(variableScope);

        String compensation = attrs.getValue("isForCompensation");
        if (compensation != null) {
            boolean isForCompensation = Boolean.parseBoolean(compensation);
            if (isForCompensation) {
                subProcessNode.setMetaData("isForCompensation", isForCompensation);
            }
        }
        subProcessNode.setAutoComplete(true);
        return subProcessNode;
    }

    public Class<CompositeContextNode> generateNodeFor() {
        return CompositeContextNode.class;
    }

    @Override
    protected Node handleNode(Node node, Element element, String uri, String localName, ExtensibleXmlParser parser) throws SAXException {
        CompositeContextNode compositeNode = (CompositeContextNode) node;
        super.handleNode(node, element, uri, localName, parser);

        handleScript(compositeNode, element, "onEntry");
        handleScript(compositeNode, element, "onExit");
        compositeNode.setIoSpecification(readIOEspecification(parser, element));
        compositeNode.setMultiInstanceSpecification(readMultiInstanceSpecification(parser, element, compositeNode.getIoSpecification()));

        Node outcome = compositeNode;
        if (compositeNode.getMultiInstanceSpecification().hasMultiInstanceInput()) {
            ForEachNode forEachNode = (ForEachNode) decorateMultiInstanceSpecificationSubProcess(compositeNode, compositeNode.getMultiInstanceSpecification());

            handleScript(forEachNode, element, "onEntry");
            handleScript(forEachNode, element, "onExit");

            List<SequenceFlow> connections = (List<SequenceFlow>) forEachNode.getMetaData(ProcessHandler.CONNECTIONS);
            ProcessHandler.linkConnections(forEachNode, connections);
            ProcessHandler.linkBoundaryEvents(forEachNode);

            // This must be done *after* linkConnections(process, connections)
            //  because it adds hidden connections for compensations
            List<Association> associations = (List<Association>) forEachNode.getMetaData(ProcessHandler.ASSOCIATIONS);
            ProcessHandler.linkAssociations((Definitions) forEachNode.getMetaData("Definitions"), forEachNode, associations);
            applyAsync(node, Boolean.parseBoolean((String) compositeNode.getMetaData().get("customAsync")));
            outcome = forEachNode;
        } else {
            handleCompositeContextNode(compositeNode);
        }
        NodeContainer nodeContainer = (NodeContainer) parser.getParent();
        nodeContainer.addNode(outcome);

        return outcome;
    }

    protected void handleCompositeContextNode(CompositeContextNode compositeNode) throws SAXException {
        List<SequenceFlow> connections = (List<SequenceFlow>) compositeNode.getMetaData(ProcessHandler.CONNECTIONS);

        List<IntermediateLink> throwLinks = (List<IntermediateLink>) compositeNode.getMetaData(ProcessHandler.LINKS);
        ProcessHandler.linkIntermediateLinks(compositeNode, throwLinks);

        ProcessHandler.linkConnections(compositeNode, connections);
        ProcessHandler.linkBoundaryEvents(compositeNode);

        // This must be done *after* linkConnections(process, connections)
        //  because it adds hidden connections for compensations
        List<Association> associations = (List<Association>) compositeNode.getMetaData(ProcessHandler.ASSOCIATIONS);
        ProcessHandler.linkAssociations((Definitions) compositeNode.getMetaData("Definitions"), compositeNode, associations);

    }

    protected void applyAsync(Node node, boolean isAsync) {
        for (org.kie.api.definition.process.Node subNode : ((CompositeContextNode) node).getNodes()) {
            if (isAsync) {
                List<Connection> incoming = subNode.getIncomingConnections(NodeImpl.CONNECTION_DEFAULT_TYPE);
                if (incoming != null) {
                    for (Connection con : incoming) {
                        if (con.getFrom() instanceof StartNode) {
                            ((Node) subNode).setMetaData("customAsync", Boolean.toString(isAsync));
                            return;
                        }
                    }
                }

            }
        }
    }

    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        throw new IllegalArgumentException("Writing out should be handled by specific handlers");
    }

}
