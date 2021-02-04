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
import java.util.List;
import java.util.Map;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.Escalation;
import org.jbpm.bpmn2.core.IntermediateLink;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.process.instance.impl.actions.HandleEscalationAction;
import org.jbpm.process.instance.impl.actions.HandleMessageAction;
import org.jbpm.process.instance.impl.actions.SignalProcessInstanceAction;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.ThrowLinkNode;
import org.jbpm.workflow.core.node.Transformation;
import org.kie.api.runtime.process.DataTransformer;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static org.jbpm.bpmn2.xml.ProcessHandler.createJavaAction;

public class IntermediateThrowEventHandler extends AbstractNodeHandler {

	private DataTransformerRegistry transformerRegistry = DataTransformerRegistry.get();

	public static final String LINK_NAME = "linkName";
	public static final String LINK_SOURCE = "source";
	public static final String LINK_TARGET = "target";
	
	private static final String MAPPING_VARIABLE_KEY = "MappingVariable";
	private static final String TRANSFORMATION_KEY = "Transformation";

	@Override
	protected Node createNode( Attributes attrs) {
		return new ActionNode();
	}

	@Override
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
		return Node.class;
	}

	@Override
    public Object end(final String uri, final String localName,
			final ExtensibleXmlParser parser) throws SAXException {
		final Element element = parser.endElementBuilder();
		ActionNode node = (ActionNode) parser.getCurrent();
		// determine type of event definition, so the correct type of node
		// can be generated
		org.w3c.dom.Node xmlNode = element.getFirstChild();
		while (xmlNode != null) {
			String nodeName = xmlNode.getNodeName();
			if ("signalEventDefinition".equals(nodeName)) {
				// reuse already created ActionNode
				handleSignalNode(node, element, uri, localName, parser);
				break;
			} else if ("messageEventDefinition".equals(nodeName)) {
				// reuse already created ActionNode
				handleMessageNode(node, element, uri, localName, parser);
				break;
			} else if ("escalationEventDefinition".equals(nodeName)) {
				// reuse already created ActionNode
				handleEscalationNode(node, element, uri, localName, parser);
				break;
			} else if ("compensateEventDefinition".equals(nodeName)) {
				// reuse already created ActionNode
				handleThrowCompensationEventNode(node, element, uri, localName, parser);
				break;
			} else if ("linkEventDefinition".equals(nodeName)) {
				ThrowLinkNode linkNode = new ThrowLinkNode();
				linkNode.setId(node.getId());
				handleLinkNode(element, linkNode, xmlNode, parser);
				NodeContainer nodeContainer = (NodeContainer) parser
						.getParent();
				nodeContainer.addNode(linkNode);
				((ProcessBuildData) parser.getData()).addNode(node);
				// we break the while and stop the execution of this method.
				return linkNode;
			}
			xmlNode = xmlNode.getNextSibling();
		}
		// none event definition
		if (node.getAction() == null) {
			node.setAction(new DroolsConsequenceAction("mvel", ""));
			node.setMetaData("NodeType", "IntermediateThrowEvent-None");
		}
		NodeContainer nodeContainer = (NodeContainer) parser.getParent();
		nodeContainer.addNode(node);
		return node;
	}

	protected void handleLinkNode(Element element, Node node,
			org.w3c.dom.Node xmlLinkNode, ExtensibleXmlParser parser) {

		node.setName(element.getAttribute("name"));

		NamedNodeMap linkAttr = xmlLinkNode.getAttributes();
		String name = linkAttr.getNamedItem("name").getNodeValue();


		String id = element.getAttribute("id");
		node.setMetaData("UniqueId", id);
		node.setMetaData(LINK_NAME, name);

		org.w3c.dom.Node xmlNode = xmlLinkNode.getFirstChild();

		NodeContainer nodeContainer = (NodeContainer) parser.getParent();

		IntermediateLink aLink = new IntermediateLink();
		aLink.setName(name);
		aLink.setUniqueId(id);
		while (null != xmlNode) {
			String nodeName = xmlNode.getNodeName();

			if (LINK_TARGET.equals(nodeName)) {
				String target = xmlNode.getTextContent();
				node.setMetaData(LINK_TARGET, target);
			}

			if (LINK_SOURCE.equals(nodeName)) {
				String source = xmlNode.getTextContent();
				ArrayList<String> sources = (ArrayList<String>) node
						.getMetaData().get(LINK_SOURCE);

				// if there is no list, create one
				if (null == sources) {
					sources = new ArrayList<String>();
				}

				// to connect nodes.
				aLink.addSource(source);

				// to do the xml dump
				sources.add(source);
				node.setMetaData(LINK_SOURCE, sources);
			}
			xmlNode = xmlNode.getNextSibling();
		}
		aLink.configureThrow();

		if (nodeContainer instanceof RuleFlowProcess) {
			RuleFlowProcess process = (RuleFlowProcess) nodeContainer;
			List<IntermediateLink> links = (List<IntermediateLink>) process
					.getMetaData().get(ProcessHandler.LINKS);
			if (null == links) {
				links = new ArrayList<IntermediateLink>();
			}
			links.add(aLink);
			process.setMetaData(ProcessHandler.LINKS, links);
		} else if (nodeContainer instanceof CompositeNode) {
			CompositeNode subprocess = (CompositeNode) nodeContainer;
			List<IntermediateLink> links = (List<IntermediateLink>) subprocess
					.getMetaData().get(ProcessHandler.LINKS);
			if (null == links) {
				links = new ArrayList<IntermediateLink>();
			}
			links.add(aLink);
			subprocess.setMetaData(ProcessHandler.LINKS, links);
		}

	}

	public void handleSignalNode( final Node node, final Element element,
                                  final String uri, final String localName,
                                  final ExtensibleXmlParser parser) throws SAXException {
		ActionNode actionNode = (ActionNode) node;
		org.w3c.dom.Node xmlNode = element.getFirstChild();
		while (xmlNode != null) {
			String nodeName = xmlNode.getNodeName();
			if ("dataInput".equals(nodeName)) {
                String id = ((Element) xmlNode).getAttribute("id");
                String inputName = ((Element) xmlNode).getAttribute("name");
                dataInputs.put(id, inputName);
            } else if ("dataInputAssociation".equals(nodeName)) {
				readDataInputAssociation(xmlNode, actionNode, parser);
			} else if ("signalEventDefinition".equals(nodeName)) {
				String signalName = ((Element) xmlNode).getAttribute("signalRef");
				String variable = findVariable((String) actionNode.getMetaData(MAPPING_VARIABLE_KEY), parser);

				signalName = checkSignalAndConvertToRealSignalNam(parser, signalName);

                actionNode.setMetaData(Metadata.EVENT_TYPE, "signal");
                actionNode.setMetaData(Metadata.REF, signalName);
                actionNode.setMetaData(Metadata.VARIABLE, variable);

				// check if signal should be send async
                if (dataInputs.containsValue("async")) {
                    signalName = "ASYNC-" + signalName;
                }

                DroolsConsequenceAction action = createJavaAction(new SignalProcessInstanceAction(signalName, variable, (String) actionNode.getMetaData("customScope"), (Transformation) actionNode.getMetaData().get(TRANSFORMATION_KEY)));
                actionNode.setAction(action);
            }
			xmlNode = xmlNode.getNextSibling();
		}
	}

    @SuppressWarnings("unchecked")
	public void handleMessageNode( final Node node, final Element element,
                                   final String uri, final String localName,
                                   final ExtensibleXmlParser parser) throws SAXException {
		ActionNode actionNode = (ActionNode) node;
		org.w3c.dom.Node xmlNode = element.getFirstChild();
		while (xmlNode != null) {
			String nodeName = xmlNode.getNodeName();
			if ("dataInput".equals(nodeName)) {
                String id = ((Element) xmlNode).getAttribute("id");
                String inputName = ((Element) xmlNode).getAttribute("name");
                dataInputs.put(id, inputName);
            } else if ("dataInputAssociation".equals(nodeName)) {
				readDataInputAssociation(xmlNode, actionNode, parser);
			} else if ("messageEventDefinition".equals(nodeName)) {
				String messageRef = ((Element) xmlNode)
						.getAttribute("messageRef");
				Map<String, Message> messages = (Map<String, Message>) ((ProcessBuildData) parser
						.getData()).getMetaData("Messages");
				if (messages == null) {
					throw new IllegalArgumentException("No messages found");
				}
				Message message = messages.get(messageRef);
				if (message == null) {
					throw new IllegalArgumentException(
							"Could not find message " + messageRef);
				}
                String variable = (String) actionNode.getMetaData(MAPPING_VARIABLE_KEY);
                Variable v = (Variable) ((ProcessBuildData) parser.getData()).getMetaData("Variable");
                if (v != null) {
                    variable = (String) v.getMetaData(variable);
                }
                actionNode.setMetaData(Metadata.MESSAGE_TYPE, message.getType());
                actionNode.setMetaData(Metadata.TRIGGER_TYPE, "ProduceMessage");
                actionNode.setMetaData(Metadata.TRIGGER_REF, message.getName());

                DroolsConsequenceAction action = createJavaAction(new HandleMessageAction(message.getType(), variable, (Transformation) actionNode.getMetaData().get(TRANSFORMATION_KEY)));
                actionNode.setAction(action);			
			}
			xmlNode = xmlNode.getNextSibling();
		}
	}

	@SuppressWarnings("unchecked")
	public void handleEscalationNode( final Node node, final Element element,
                                      final String uri, final String localName,
                                      final ExtensibleXmlParser parser) throws SAXException {
		ActionNode actionNode = (ActionNode) node;
		org.w3c.dom.Node xmlNode = element.getFirstChild();
		while (xmlNode != null) {
			String nodeName = xmlNode.getNodeName();
			if ("dataInputAssociation".equals(nodeName)) {
				readDataInputAssociation(xmlNode, actionNode, parser);
			} else if ("escalationEventDefinition".equals(nodeName)) {
				String escalationRef = ((Element) xmlNode)
						.getAttribute("escalationRef");
				if (escalationRef != null && escalationRef.trim().length() > 0) {
					Map<String, Escalation> escalations = (Map<String, Escalation>) ((ProcessBuildData) parser
							.getData()).getMetaData(ProcessHandler.ESCALATIONS);
					if (escalations == null) {
						throw new IllegalArgumentException(
								"No escalations found");
					}
                    Escalation escalation = escalations.get(escalationRef);
                    if (escalation == null) {
                        throw new IllegalArgumentException(
                                                           "Could not find escalation " + escalationRef);
                    }
                    String faultName = escalation.getEscalationCode();
                    String variable = (String) actionNode.getMetaData(MAPPING_VARIABLE_KEY);

                    DroolsConsequenceAction action = createJavaAction(new HandleEscalationAction(faultName, variable));
                    actionNode.setAction(action);
				} else {
				    throw new IllegalArgumentException("General escalation is not yet supported");
				}
			}
			xmlNode = xmlNode.getNextSibling();
		}
	}

	protected void readDataInputAssociation(org.w3c.dom.Node xmlNode,
			ActionNode actionNode, final ExtensibleXmlParser parser) {
		
		
		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        if ("sourceRef".equals(subNode.getNodeName())) {            
            // sourceRef
            String eventVariable = subNode.getTextContent();
            // targetRef
            subNode = subNode.getNextSibling();
            String target = subNode.getTextContent();
            // transformation
            Transformation transformation = null;
            subNode = subNode.getNextSibling();
            if (subNode != null && "transformation".equals(subNode.getNodeName())) {
                String lang = subNode.getAttributes().getNamedItem("language").getNodeValue();
                String expression = subNode.getTextContent();
    
                DataTransformer transformer = transformerRegistry.find(lang);
                if (transformer == null) {
                    throw new IllegalArgumentException("No transformer registered for language " + lang);
                }
                transformation = new Transformation(lang, expression, dataInputs.get(target));
                actionNode.setMetaData(TRANSFORMATION_KEY, transformation);
            }
    
            if (eventVariable != null && eventVariable.trim().length() > 0) {            
                if (dataInputs.containsKey(eventVariable)) {
                    eventVariable = dataInputs.get(eventVariable);
                }
                
                actionNode.setMetaData(MAPPING_VARIABLE_KEY, findVariable(eventVariable, parser));
            }
        } else {
            // targetRef
            // assignment
            subNode = subNode.getNextSibling();
            if (subNode != null) {
                org.w3c.dom.Node subSubNode = subNode.getFirstChild();
                NodeList nl = subSubNode.getChildNodes();
                if (nl.getLength() > 1) {
                    actionNode.setMetaData(MAPPING_VARIABLE_KEY, subSubNode.getTextContent());
                    return;
                } else if (nl.getLength() == 0) {
                    return;
                }
                Object result = null;
                Object from = nl.item(0);
                if (from instanceof Text) {
                    String text = ((Text) from).getTextContent();
                    if (text.startsWith("\"") && text.endsWith("\"")) {
                        result = text.substring(1, text.length() -1);
                    } else {
                        result = text;
                    }
                } else {
                    result = nl.item(0);
                }
                actionNode.setMetaData(MAPPING_VARIABLE_KEY, "\"" + findVariable(result.toString(), parser) + "\"");
            }
        }
	}

	@Override
	public void writeNode( Node node, StringBuilder xmlDump, int metaDataType) {
		throw new IllegalArgumentException(
				"Writing out should be handled by action node handler");
	}


}
