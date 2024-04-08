/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.serverless.workflow.parser.handlers;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.EventNodeFactory;
import org.jbpm.ruleflow.core.factory.JoinFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.ruleflow.core.factory.StartNodeFactory;
import org.jbpm.ruleflow.core.factory.SubProcessNodeFactory;
import org.jbpm.ruleflow.core.factory.TimerNodeFactory;
import org.jbpm.workflow.core.impl.DataAssociation;
import org.jbpm.workflow.core.impl.DataDefinition;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;
import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.correlation.SimpleCorrelation;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;

import io.serverlessworkflow.api.events.EventDefinition;

import static org.jbpm.process.core.timer.Timer.TIME_DURATION;
import static org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser.DEFAULT_WORKFLOW_VAR;
import static org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser.JSON_NODE;

public class NodeFactoryUtils {

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> SubProcessNodeFactory<T> subprocessNode(SubProcessNodeFactory<T> nodeFactory, String inputVar, String outputVar) {
        Map<String, String> types = Collections.singletonMap(DEFAULT_WORKFLOW_VAR, JSON_NODE);
        DataAssociation inputDa = new DataAssociation(
                new DataDefinition(inputVar, inputVar, JSON_NODE),
                new DataDefinition(DEFAULT_WORKFLOW_VAR, DEFAULT_WORKFLOW_VAR, JSON_NODE), null, null);
        DataAssociation outputDa = new DataAssociation(
                new DataDefinition(DEFAULT_WORKFLOW_VAR, DEFAULT_WORKFLOW_VAR, JSON_NODE),
                new DataDefinition(outputVar, outputVar, JSON_NODE), null, null);

        VariableScope variableScope = new VariableScope();
        return nodeFactory
                .independent(true)
                .metaData("BPMN.InputTypes", types)
                .metaData("BPMN.OutputTypes", types)
                .mapDataInputAssociation(inputDa)
                .mapDataOutputAssociation(outputDa)
                .context(variableScope)
                .defaultContext(variableScope);
    }

    public static <T extends NodeFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> T sendEventNode(NodeFactory<T, P> actionNode,
            EventDefinition eventDefinition, String inputVar) {
        return sendEventNode(actionNode, eventDefinition.getName(), eventDefinition.getType(), inputVar);
    }

    public static <T extends NodeFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> T sendEventNode(NodeFactory<T, P> actionNode, String name,
            String type, String inputVar) {
        return actionNode
                .name(name)
                .metaData(Metadata.EVENT_TYPE, "message")
                .metaData(Metadata.MAPPING_VARIABLE, inputVar)
                .metaData(Metadata.TRIGGER_REF, type)
                .metaData(Metadata.MESSAGE_TYPE, JSON_NODE)
                .metaData(Metadata.TRIGGER_TYPE, "ProduceMessage");
    }

    public static <T extends StartNodeFactory<P>, P extends RuleFlowNodeContainerFactory<P, ?>> T startMessageNode(T nodeFactory, EventDefinition eventDefinition, String inputVar, String outputVar) {
        return (T) addEventDefinition(messageNode(nodeFactory, eventDefinition.getName(), eventDefinition.getType(), inputVar), eventDefinition).trigger(ServerlessWorkflowParser.JSON_NODE, outputVar);
    }

    public static <T extends EventNodeFactory<P>, P extends RuleFlowNodeContainerFactory<P, ?>> T consumeMessageNode(T nodeFactory, EventDefinition eventDefinition, String inputVar,
            String outputVar) {
        return (T) addEventDefinition(consumeMessageNode(nodeFactory, eventDefinition.getName(), eventDefinition.getType(), inputVar, outputVar), eventDefinition);
    }

    public static <T extends EventNodeFactory<P>, P extends RuleFlowNodeContainerFactory<P, ?>> T consumeMessageNode(T eventNode, String name, String type,
            String inputVar, String outputVar) {
        return (T) messageNode(eventNode, name, type, inputVar)
                .inputVariableName(inputVar)
                .variableName(outputVar)
                .outMapping(inputVar, outputVar)
                .metaData(Metadata.MAPPING_VARIABLE, DEFAULT_WORKFLOW_VAR)
                .eventType("Message-" + type);
    }

    private static <T extends NodeFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> T messageNode(T nodeFactory, String name, String type, String inputVar) {
        return nodeFactory
                .name(name)
                .metaData(Metadata.EVENT_TYPE, "message")
                .metaData(Metadata.TRIGGER_MAPPING, inputVar)
                .metaData(Metadata.TRIGGER_REF, type)
                .metaData(Metadata.MESSAGE_TYPE, JSON_NODE)
                .metaData(Metadata.TRIGGER_TYPE, "ConsumeMessage");
    }

    private static <T extends NodeFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> T addEventDefinition(T nodeFactory, EventDefinition eventDefinition) {
        return nodeFactory.metaData(Metadata.DATA_ONLY, eventDefinition.isDataOnly())
                .metaData(Metadata.CORRELATION_ATTRIBUTES, getCorrelationAttributes(eventDefinition));
    }

    private static CompositeCorrelation getCorrelationAttributes(EventDefinition eventDefinition) {
        return new CompositeCorrelation(eventDefinition.getCorrelation().stream()
                .map(c -> new SimpleCorrelation<>(c.getContextAttributeName(), c.getContextAttributeValue()))
                .collect(Collectors.toSet()));
    }

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> SplitFactory<T> eventBasedExclusiveSplitNode(SplitFactory<T> nodeFactory) {
        return eventBasedSplitNode(nodeFactory, Split.TYPE_XAND);
    }

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> SplitFactory<T> eventBasedSplitNode(SplitFactory<T> nodeFactory, int type) {
        return nodeFactory.name("EventSplit_" + nodeFactory.getNode().getId().toExternalFormat())
                .type(type)
                .metaData("EventBased", "true");
    }

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> SplitFactory<T> exclusiveSplitNode(SplitFactory<T> nodeFactory) {
        return nodeFactory.name("ExclusiveSplit_" + nodeFactory.getNode().getId().toExternalFormat())
                .type(Split.TYPE_XOR);
    }

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> JoinFactory<T> joinExclusiveNode(JoinFactory<T> nodeFactory) {
        return nodeFactory.name("ExclusiveJoin_" + nodeFactory.getNode().getId().toExternalFormat())
                .type(Join.TYPE_XOR);
    }

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> TimerNodeFactory<T> timerNode(TimerNodeFactory<T> nodeFactory, String duration) {
        return nodeFactory.name("TimerNode_" + nodeFactory.getNode().getId().toExternalFormat())
                .type(TIME_DURATION)
                .delay(duration)
                .metaData("EventType", "Timer");
    }

    private NodeFactoryUtils() {
    }

}
