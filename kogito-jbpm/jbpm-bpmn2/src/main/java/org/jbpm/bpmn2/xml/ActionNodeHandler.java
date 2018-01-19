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
import org.drools.compiler.rule.builder.dialect.java.JavaDialect;

import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;

import org.xml.sax.Attributes;

public class ActionNodeHandler extends AbstractNodeHandler {

    protected Node createNode(Attributes attrs) {
        throw new IllegalArgumentException("Reading in should be handled by specific handlers");
    }

    @SuppressWarnings("unchecked")
    public Class generateNodeFor() {
        return ActionNode.class;
    }

    public void writeNode(Node node,
                          StringBuilder xmlDump,
                          int metaDataType) {
        ActionNode actionNode = (ActionNode) node;
        DroolsConsequenceAction action = null;
        if (actionNode.getAction() instanceof DroolsConsequenceAction) {
            action = (DroolsConsequenceAction) actionNode.getAction();
        } else {
            logger.warn("Cannot serialize custom implementation of the Action interface to XML");
        }

        String eventType = (String) actionNode.getMetaData("EventType");
        String ref = (String) actionNode.getMetaData("Ref");
        String variableRef = (String) actionNode.getMetaData("Variable");

        if (action != null) {
            String s = action.getConsequence();
            if (s.startsWith("org.drools.core.process.instance.impl.WorkItemImpl workItem = new org.drools.core.process.instance.impl.WorkItemImpl();")) {
                writeNode("intermediateThrowEvent",
                          actionNode,
                          xmlDump,
                          metaDataType);
                xmlDump.append(">" + EOL);
                writeExtensionElements(actionNode,
                                       xmlDump);
                String variable = (String) actionNode.getMetaData("MappingVariable");
                if (variable != null) {
                    xmlDump.append(
                            "      <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(actionNode) + "_Input\" />" + EOL +
                                    "      <dataInputAssociation>" + EOL +
                                    "        <sourceRef>" + XmlDumper.replaceIllegalChars(variable) + "</sourceRef>" + EOL +
                                    "        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(actionNode) + "_Input</targetRef>" + EOL +
                                    "      </dataInputAssociation>" + EOL +
                                    "      <inputSet>" + EOL +
                                    "        <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(actionNode) + "_Input</dataInputRefs>" + EOL +
                                    "      </inputSet>" + EOL);
                }
                xmlDump.append("      <messageEventDefinition messageRef=\"" + XmlBPMNProcessDumper.getUniqueNodeId(actionNode) + "_Message\"/>" + EOL);
                endNode("intermediateThrowEvent",
                        xmlDump);
            } else if ("signal".equals(eventType)) {
                writeNode("intermediateThrowEvent",
                          actionNode,
                          xmlDump,
                          metaDataType);
                xmlDump.append(">" + EOL);
                writeExtensionElements(actionNode,
                                       xmlDump);

                if (!s.startsWith("null")) {

                    xmlDump.append(
                            "      <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(actionNode) + "_Input\" />" + EOL +
                                    "      <dataInputAssociation>" + EOL +
                                    "        <sourceRef>" + XmlDumper.replaceIllegalChars(variableRef) + "</sourceRef>" + EOL +
                                    "        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(actionNode) + "_Input</targetRef>" + EOL +
                                    "      </dataInputAssociation>" + EOL +
                                    "      <inputSet>" + EOL +
                                    "        <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(actionNode) + "_Input</dataInputRefs>" + EOL +
                                    "      </inputSet>" + EOL);
                }
                xmlDump.append("      <signalEventDefinition signalRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(ref) + "\"/>" + EOL);
                endNode("intermediateThrowEvent",
                        xmlDump);
            } else if (s.startsWith(RUNTIME_SIGNAL_EVENT)) {
                writeNode("intermediateThrowEvent",
                          actionNode,
                          xmlDump,
                          metaDataType);
                xmlDump.append(">" + EOL);
                writeExtensionElements(actionNode,
                                       xmlDump);
                s = s.substring(44);
                String type = s.substring(0,
                                          s.indexOf("\""));
                s = s.substring(s.indexOf(",") + 2);
                String variable = null;
                if (!s.startsWith("null")) {
                    variable = s.substring(0,
                                           s.indexOf(")"));
                    xmlDump.append(
                            "      <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(actionNode) + "_Input\" />" + EOL +
                                    "      <dataInputAssociation>" + EOL +
                                    "        <sourceRef>" + XmlDumper.replaceIllegalChars(variable) + "</sourceRef>" + EOL +
                                    "        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(actionNode) + "_Input</targetRef>" + EOL +
                                    "      </dataInputAssociation>" + EOL +
                                    "      <inputSet>" + EOL +
                                    "        <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(actionNode) + "_Input</dataInputRefs>" + EOL +
                                    "      </inputSet>" + EOL);
                }
                xmlDump.append("      <signalEventDefinition signalRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(type) + "\"/>" + EOL);
                endNode("intermediateThrowEvent",
                        xmlDump);
            } else if (s.startsWith(PROCESS_INSTANCE_SIGNAL_EVENT)) {
                writeNode("intermediateThrowEvent",
                          actionNode,
                          xmlDump,
                          metaDataType);
                xmlDump.append(">" + EOL);
                writeExtensionElements(actionNode,
                                       xmlDump);
                s = s.substring(43);
                assert "Compensation".equals(s.substring(0,
                                                         s.indexOf("\"")))
                        : "Type is not \"Compensation\" but \"" + s.substring(0,
                                                                              s.indexOf("\"")) + "\"";

                String activityRef = "";
                int begin = 12; // : Compensation
                int end = s.length() - 3; // ");
                String compensationEvent = s.substring(begin,
                                                       end);
                if (!compensationEvent.startsWith(CompensationScope.IMPLICIT_COMPENSATION_PREFIX)) {
                    // specific
                    activityRef = "activityRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(activityRef) + "\" ";
                } // else general: activityRef = "" (above) 
                xmlDump.append("      <compensateEventDefinition " + activityRef + "/>" + EOL);
                endNode("intermediateThrowEvent",
                        xmlDump);
            } else if (s.startsWith("org.drools.core.process.instance.context.exception.ExceptionScopeInstance scopeInstance = (org.drools.core.process.instance.context.exception.ExceptionScopeInstance) ((org.drools.workflow.instance.NodeInstance) kcontext.getNodeInstance()).resolveContextInstance(org.drools.core.process.core.context.exception.ExceptionScope.EXCEPTION_SCOPE, \"")) {
                writeNode("intermediateThrowEvent",
                          actionNode,
                          xmlDump,
                          metaDataType);
                xmlDump.append(">" + EOL);
                writeExtensionElements(actionNode,
                                       xmlDump);
                s = s.substring(327);
                String type = s.substring(0,
                                          s.indexOf("\""));
                xmlDump.append("      <escalationEventDefinition escalationRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(type) + "\"/>" + EOL);
                endNode("intermediateThrowEvent",
                        xmlDump);
            } else if ("IntermediateThrowEvent-None".equals(actionNode.getMetaData("NodeType"))) {
                writeNode("intermediateThrowEvent",
                          actionNode,
                          xmlDump,
                          metaDataType);
                xmlDump.append(">" + EOL);
                writeExtensionElements(actionNode,
                                       xmlDump);
                endNode("intermediateThrowEvent",
                        xmlDump);
            } else {
                writeNode("scriptTask",
                          actionNode,
                          xmlDump,
                          metaDataType);
                if (JavaDialect.ID.equals(action.getDialect())) {
                    xmlDump.append("scriptFormat=\"" + XmlBPMNProcessDumper.JAVA_LANGUAGE + "\" ");
                } else if ("JavaScript".equals(action.getDialect())) {
                    xmlDump.append("scriptFormat=\"" + XmlBPMNProcessDumper.JAVASCRIPT_LANGUAGE + "\" ");
                }
                Object isForCompensationObj = actionNode.getMetaData("isForCompensation");
                if (isForCompensationObj != null && (Boolean) isForCompensationObj) {
                    xmlDump.append("isForCompensation=\"true\" ");
                }
                xmlDump.append(">" + EOL);
                writeExtensionElements(actionNode,
                                       xmlDump);
                if (action.getConsequence() != null) {
                    xmlDump.append("      <script>" + XmlDumper.replaceIllegalChars(action.getConsequence()) + "</script>" + EOL);
                }
                endNode("scriptTask",
                        xmlDump);
            }
        } else {
            writeNode("scriptTask",
                      actionNode,
                      xmlDump,
                      metaDataType);
            xmlDump.append(">" + EOL);
            writeExtensionElements(actionNode,
                                   xmlDump);
            endNode("scriptTask",
                    xmlDump);
        }
    }
}
