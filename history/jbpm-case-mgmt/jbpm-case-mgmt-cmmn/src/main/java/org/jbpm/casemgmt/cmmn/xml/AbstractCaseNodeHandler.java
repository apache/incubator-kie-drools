/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.cmmn.xml;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.casemgmt.cmmn.core.PlanItem;
import org.jbpm.casemgmt.cmmn.core.Sentry;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class AbstractCaseNodeHandler extends BaseAbstractHandler implements Handler {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractCaseNodeHandler.class);

    public AbstractCaseNodeHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = true;
    }

    protected void initValidParents() {
        this.validParents = new HashSet<Class<?>>();
        this.validParents.add(NodeContainer.class);
    }

    protected void initValidPeers() {
        this.validPeers = new HashSet<Class<?>>();
        this.validPeers.add(null);
        this.validPeers.add(PlanItem.class);
        this.validPeers.add(Sentry.class);
        this.validPeers.add(HumanTaskNode.class);
        this.validPeers.add(MilestoneNode.class);
        this.validPeers.add(DynamicNode.class);
        this.validPeers.add(SubProcessNode.class);
        this.validPeers.add(WorkItemNode.class);
        this.validPeers.add(RuleSetNode.class);

    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        final Node node = createNode(attrs);
        String id = attrs.getValue("id");
        node.setMetaData("UniqueId", id);
        final String name = attrs.getValue("name");
        node.setName(name);

        AtomicInteger idGen = (AtomicInteger) parser.getMetaData().get("idGen");
        node.setId(idGen.getAndIncrement());

        return node;
    }

    protected abstract Node createNode(Attributes attrs);

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        Node node = (Node) parser.getCurrent();

        ProcessBuildData buildData = (ProcessBuildData) parser.getData();
        Map<String, PlanItem> planItems = (Map<String, PlanItem>) buildData.getMetaData("PlanItems");

        PlanItem planItem = planItems.get(node.getMetaData().get("UniqueId"));
        if (planItem != null && planItem.getEntryCriterion() != null) {
            if ("autostart".equalsIgnoreCase(planItem.getEntryCriterion().getExpression())) {
                node.setMetaData("customAutoStart", "true");
            } else {
                node.setMetaData("customActivationExpression", planItem.getEntryCriterion().getExpression());
                node.setMetaData("customActivationFragmentName", node.getName());
            }
        }

        handleNode(node, element, uri, localName, parser);
        NodeContainer nodeContainer = (NodeContainer) parser.getParent();
        nodeContainer.addNode(node);
        ((ProcessBuildData) parser.getData()).addNode(node);
        return node;
    }

    protected void handleNode(final Node node,
                              final Element element,
                              final String uri,
                              final String localName,
                              final ExtensibleXmlParser parser) throws SAXException {

    }

    protected void loadDataInputsAndOutputs(final Element element,
                                            Map<String, String> inputs,
                                            Map<String, String> outputs,
                                            Map<String, String> inputTypes,
                                            Map<String, String> outputTypes,
                                            final ExtensibleXmlParser parser) {
        ProcessBuildData buildData = (ProcessBuildData) parser.getData();
        Map<String, String> fileItems = (Map<String, String>) buildData.getMetaData("FileItems");

        if (fileItems == null) {
            // no file items then return directly
            return;
        }

        Object parent = parser.getParent();
        if (parent instanceof ContextContainer) {
            ContextContainer contextContainer = (ContextContainer) parent;
            VariableScope variableScope = (VariableScope) contextContainer.getDefaultContext(VariableScope.VARIABLE_SCOPE);

            // handle entry and exit criteria
            org.w3c.dom.Node xmlNode = element.getFirstChild();
            while (xmlNode != null) {
                String nodeName = xmlNode.getNodeName();
                if ("input".equals(nodeName)) {
                    String inputName = ((Element) xmlNode).getAttribute("name");
                    String bindingRef = ((Element) xmlNode).getAttribute("bindingRef");
                    String varName = VariableScope.CASE_FILE_PREFIX + fileItems.get(bindingRef);
                    inputs.put(inputName, varName);

                    Variable var = variableScope.findVariable(varName);
                    inputTypes.put(inputName, var.getType().getStringType());
                } else if ("output".equals(nodeName)) {
                    String outputName = ((Element) xmlNode).getAttribute("name");
                    String bindingRef = ((Element) xmlNode).getAttribute("bindingRef");
                    String varName = VariableScope.CASE_FILE_PREFIX + fileItems.get(bindingRef);
                    outputs.put(outputName, varName);

                    Variable var = variableScope.findVariable(varName);
                    inputTypes.put(outputName, var.getType().getStringType());
                }
                xmlNode = xmlNode.getNextSibling();
            }
        }
    }

}
