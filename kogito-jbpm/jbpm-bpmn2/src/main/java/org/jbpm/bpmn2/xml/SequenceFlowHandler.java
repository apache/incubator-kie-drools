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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.bpmn2.core.Association;
import org.jbpm.bpmn2.core.IntermediateLink;
import org.jbpm.bpmn2.core.Lane;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.CompositeNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SequenceFlowHandler extends BaseAbstractHandler implements Handler {

	public SequenceFlowHandler() {
		initValidParents();
		initValidPeers();
		this.allowNesting = false;
	}

	protected void initValidParents() {
		this.validParents = new HashSet<Class<?>>();
		this.validParents.add(NodeContainer.class);
	}

	protected void initValidPeers() {
		this.validPeers = new HashSet<Class<?>>();
		this.validPeers.add(null);
		this.validPeers.add(Lane.class);
		this.validPeers.add(Variable.class);
		this.validPeers.add(Node.class);
		this.validPeers.add(SequenceFlow.class);
		this.validPeers.add(Lane.class);
		this.validPeers.add(Association.class);
		// TODO: this is right?
		this.validPeers.add(IntermediateLink.class);

	}

	@SuppressWarnings("unchecked")
	public Object start(final String uri, final String localName,
			final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);

		final String id = attrs.getValue("id");
		final String sourceRef = attrs.getValue("sourceRef");
		final String targetRef = attrs.getValue("targetRef");
		final String bendpoints = attrs.getValue("g:bendpoints");
		final String name = attrs.getValue("name");
		final String priority = attrs.getValue("http://www.jboss.org/drools",
				"priority");

		NodeContainer nodeContainer = (NodeContainer) parser.getParent();

		List<SequenceFlow> connections = null;
		if (nodeContainer instanceof RuleFlowProcess) {
			RuleFlowProcess process = (RuleFlowProcess) nodeContainer;
			connections = (List<SequenceFlow>) process
					.getMetaData(ProcessHandler.CONNECTIONS);
			if (connections == null) {
				connections = new ArrayList<SequenceFlow>();
				process.setMetaData(ProcessHandler.CONNECTIONS, connections);
			}
		} 

		else if (nodeContainer instanceof CompositeNode) {

			CompositeNode compositeNode = (CompositeNode) nodeContainer;
			connections = (List<SequenceFlow>) compositeNode
					.getMetaData(ProcessHandler.CONNECTIONS);
			if (connections == null) {
				connections = new ArrayList<SequenceFlow>();
				compositeNode.setMetaData(ProcessHandler.CONNECTIONS,
						connections);
			}
		}

		SequenceFlow connection = new SequenceFlow(id, sourceRef, targetRef);
		connection.setBendpoints(bendpoints);
		connection.setName(name);
		if (priority != null) {
			connection.setPriority(Integer.parseInt(priority));
		}

		connections.add(connection);

		return connection;
	}

	public Object end(final String uri, final String localName,
			final ExtensibleXmlParser parser) throws SAXException {
		final Element element = parser.endElementBuilder();
		SequenceFlow sequenceFlow = (SequenceFlow) parser.getCurrent();

		org.w3c.dom.Node xmlNode = element.getFirstChild();
		while (xmlNode != null) {
			String nodeName = xmlNode.getNodeName();
			if ("conditionExpression".equals(nodeName)) {
				String expression = xmlNode.getTextContent();
				org.w3c.dom.Node languageNode = xmlNode.getAttributes()
						.getNamedItem("language");
                if (languageNode != null) {
                    String language = languageNode.getNodeValue();
                    if (XmlBPMNProcessDumper.JAVA_LANGUAGE.equals(language)) {
                        sequenceFlow.setLanguage("java");
                    } else if (XmlBPMNProcessDumper.MVEL_LANGUAGE.equals(language)) {
                        sequenceFlow.setLanguage("mvel");
                    } else if (XmlBPMNProcessDumper.RULE_LANGUAGE.equals(language)) {
                        sequenceFlow.setType("rule");
                    } else if (XmlBPMNProcessDumper.XPATH_LANGUAGE.equals(language)) {
                        sequenceFlow.setLanguage("XPath");
                    } else if (XmlBPMNProcessDumper.JAVASCRIPT_LANGUAGE.equals(language)) {
                        sequenceFlow.setLanguage("JavaScript");
                    } else if (XmlBPMNProcessDumper.FEEL_LANGUAGE.equals(language)) {
                        sequenceFlow.setLanguage("FEEL");
                    } else {
                        throw new IllegalArgumentException("Unknown language " + language);
                    }
                }
				sequenceFlow.setExpression(expression);
			}
			xmlNode = xmlNode.getNextSibling();
		}
		return sequenceFlow;
	}

	public Class<?> generateNodeFor() {
		return SequenceFlow.class;
	}

}
