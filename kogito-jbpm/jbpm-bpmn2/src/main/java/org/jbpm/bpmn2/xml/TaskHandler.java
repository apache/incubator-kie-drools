/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import java.util.Collection;
import java.util.HashMap;

import org.jbpm.compiler.xml.Parser;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.datatype.DataTypeResolver;
import org.jbpm.process.core.impl.ParameterDefinitionImpl;
import org.jbpm.process.core.impl.WorkImpl;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.DataAssociation;
import org.jbpm.workflow.core.impl.DataDefinition;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static org.jbpm.ruleflow.core.Metadata.CONDITION;

public class TaskHandler extends AbstractNodeHandler {

    protected Node createNode(Attributes attrs) {
        return new WorkItemNode();
    }

    public Class<?> generateNodeFor() {
        return Node.class;
    }

    protected Node handleNode(final Node node, final Element element, final String uri,
            final String localName, final Parser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);

        WorkItemNode workItemNode = (WorkItemNode) node;
        String name = getTaskName(element);
        Work work = new WorkImpl();
        work.setName(name);
        workItemNode.setWork(work);

        Node currentNode = workItemNode;
        workItemNode.setIoSpecification(readIOEspecification(parser, element));
        workItemNode.setMultiInstanceSpecification(readMultiInstanceSpecification(parser, element, workItemNode.getIoSpecification()));
        if (workItemNode.getMultiInstanceSpecification().hasMultiInstanceInput()) {
            currentNode = decorateMultiInstanceSpecificationActivity(workItemNode, workItemNode.getMultiInstanceSpecification());
        }

        // this is a hack as most of the examples in kogito depends on this evaluation
        work.setParameter("NodeName", workItemNode.getName());
        setParameter(work, "TaskName", workItemNode.getIoSpecification().getDataInputAssociation());
        workItemNode.setMetaData("DataInputs", new HashMap<String, String>());
        workItemNode.setMetaData("DataOutputs", new HashMap<String, String>());

        handleScript(workItemNode, element, "onEntry");
        handleScript(workItemNode, element, "onExit");

        String compensation = element.getAttribute("isForCompensation");
        if (compensation != null) {
            boolean isForCompensation = Boolean.parseBoolean(compensation);
            if (isForCompensation) {
                workItemNode.setMetaData("isForCompensation", isForCompensation);
            }
        }

        for (DataDefinition dd : workItemNode.getIoSpecification().getDataInputs()) {
            workItemNode.getWork().addParameterDefinition(new ParameterDefinitionImpl(dd.getLabel(),
                    DataTypeResolver.fromType(dd.getType(), Thread.currentThread().getContextClassLoader())));
        }
        return currentNode;
    }

    protected void setParameter(Work work, String label, Collection<DataAssociation> dataAssociations) {
        for (DataAssociation dataAssociation : dataAssociations) {
            if (!dataAssociation.getAssignments().isEmpty()) {
                if (label.equals(dataAssociation.getAssignments().get(0).getTo().getLabel())) {
                    DataDefinition from = dataAssociation.getAssignments().get(0).getFrom();
                    work.setParameter(label, from.hasExpression() ? from.getExpression() : from.getLabel());
                }
            }
        }
    }

    protected String getTaskName(final Element element) {
        return element.getAttribute("taskName");
    }

    @Override
    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        throw new IllegalArgumentException("Writing out should be handled by the WorkItemNodeHandler");
    }

    public Object end(final String uri, final String localName,
            final Parser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        Node node = (Node) parser.getCurrent();
        // determine type of event definition, so the correct type of node can be generated

        node = handleNode(node, element, uri, localName, parser);

        // replace node in case it's milestone
        if (node instanceof WorkItemNode && ((WorkItemNode) node).getWork().getName().equals("Milestone")) {
            WorkItemNode workItemNode = (WorkItemNode) node;
            setParameter(((WorkItemNode) node).getWork(), CONDITION, ((NodeImpl) node).getIoSpecification().getDataInputAssociation());
            String milestoneCondition = (String) ((WorkItemNode) node).getWork().getParameter(CONDITION);
            MilestoneNode milestoneNode = new MilestoneNode();
            milestoneNode.setId(workItemNode.getId());
            milestoneNode.setMetaData(workItemNode.getMetaData());
            milestoneNode.setCondition(milestoneCondition);
            milestoneNode.setName(workItemNode.getName());
            milestoneNode.setParentContainer(workItemNode.getParentContainer());
            Arrays.stream(workItemNode.getActionTypes()).forEach(action -> milestoneNode.setActions(action, workItemNode.getActions(action)));
            node = milestoneNode;
        }

        NodeContainer nodeContainer = (NodeContainer) parser.getParent();
        nodeContainer.addNode(node);
        ((ProcessBuildData) parser.getData()).addNode(node);

        return node;
    }

}
