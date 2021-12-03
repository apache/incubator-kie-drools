/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.ruleflow.core.factory.EventNodeFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.StartNodeFactory;
import org.jbpm.ruleflow.core.factory.SubProcessNodeFactory;
import org.kie.api.definition.process.Process;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.parser.handlers.StateHandler;
import org.kie.kogito.serverless.workflow.parser.handlers.StateHandlerFactory;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.events.EventDefinition;

public class ServerlessWorkflowParser {

    public static final String NODE_START_NAME = "Start";
    public static final String NODE_END_NAME = "End";
    public static final String DEFAULT_NAME = "workflow";
    public static final String DEFAULT_PACKAGE = "org.kie.kogito.serverless";
    public static final String DEFAULT_VERSION = "1.0";

    public static final String JSON_NODE = "com.fasterxml.jackson.databind.JsonNode";
    public static final String DEFAULT_WORKFLOW_VAR = SWFConstants.DEFAULT_WORKFLOW_VAR;

    private NodeIdGenerator idGenerator = DefaultNodeIdGenerator.get();
    private Workflow workflow;
    private Process process;

    public static ServerlessWorkflowParser of(Reader workflowFile,
            String workflowFormat) throws IOException {
        return of(ServerlessWorkflowUtils.getObjectMapper(workflowFormat).readValue(workflowFile, Workflow.class));
    }

    public static ServerlessWorkflowParser of(Workflow workflow) {
        return new ServerlessWorkflowParser(workflow);
    }

    public ServerlessWorkflowParser withIdGenerator(NodeIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        return this;
    }

    private ServerlessWorkflowParser(Workflow workflow) {
        this.workflow = workflow;
    }

    private Process parseProcess() {
        String workflowStartStateName = workflow.getStart().getStateName();
        if (workflowStartStateName == null || workflowStartStateName.trim().isEmpty()) {
            throw new IllegalArgumentException("workflow does not define a starting state");
        }
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess(workflow.getId())
                .name(workflow.getName() == null ? DEFAULT_NAME : workflow.getName())
                .version(workflow.getVersion() == null ? DEFAULT_VERSION : workflow.getVersion())
                .packageName(workflow.getMetadata() != null ? workflow.getMetadata().getOrDefault("package",
                        DEFAULT_PACKAGE) : DEFAULT_PACKAGE)
                .visibility("Public")
                .variable(DEFAULT_WORKFLOW_VAR, JsonNode.class);
        ParserContext parserContext = new ParserContext(idGenerator, factory);
        Collection<StateHandler<?>> handlers =
                workflow.getStates().stream().map(state -> StateHandlerFactory.getStateHandler(state, workflow, parserContext))
                        .filter(Optional::isPresent).map(Optional::get).filter(state -> !state.usedForCompensation()).collect(Collectors.toList());
        handlers.forEach(StateHandler::handleStart);
        handlers.forEach(StateHandler::handleEnd);
        handlers.forEach(StateHandler::handleState);
        handlers.forEach(StateHandler::handleTransitions);
        handlers.forEach(StateHandler::handleErrors);
        handlers.forEach(StateHandler::handleConnections);
        if (parserContext.isCompensation()) {
            factory.metaData(Metadata.COMPENSATION, true);
            factory.addCompensationContext(workflow.getId());
        }
        return factory.validate().getProcess();
    }

    public Process getProcess() {
        if (process == null) {
            process = parseProcess();
        }
        return process;
    }

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> StartNodeFactory<T> messageStartNode(StartNodeFactory<T> nodeFactory, EventDefinition eventDefinition) {
        return messageNode(nodeFactory, eventDefinition).trigger(JSON_NODE, DEFAULT_WORKFLOW_VAR);
    }

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> EventNodeFactory<T> messageEventNode(EventNodeFactory<T> nodeFactory, EventDefinition eventDefinition) {
        return messageNode(nodeFactory, eventDefinition).eventType("Message-" + eventDefinition.getType()).variableName(DEFAULT_WORKFLOW_VAR);
    }

    public static <T extends NodeFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> T messageNode(T nodeFactory, EventDefinition eventDefinition) {
        return nodeFactory.name(eventDefinition.getName())
                .metaData(Metadata.EVENT_TYPE, "message")
                .metaData(Metadata.TRIGGER_MAPPING, DEFAULT_WORKFLOW_VAR)
                .metaData(Metadata.TRIGGER_TYPE, "ConsumeMessage")
                .metaData(Metadata.TRIGGER_REF, eventDefinition.getType())
                .metaData(Metadata.MESSAGE_TYPE, JSON_NODE);
    }

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> SubProcessNodeFactory<T> subprocessNode(SubProcessNodeFactory<T> nodeFactory) {
        Map<String, String> types = Collections.singletonMap(DEFAULT_WORKFLOW_VAR, JSON_NODE);
        VariableScope variableScope = new VariableScope();
        return nodeFactory
                .independent(true)
                .metaData("BPMN.InputTypes", types)
                .metaData("BPMN.OutputTypes", types)
                .inMapping(DEFAULT_WORKFLOW_VAR, DEFAULT_WORKFLOW_VAR)
                .outMapping(DEFAULT_WORKFLOW_VAR, DEFAULT_WORKFLOW_VAR)
                .context(variableScope)
                .defaultContext(variableScope);
    }

    public static <T extends NodeFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> NodeFactory<T, P> sendEventNode(NodeFactory<T, P> actionNode,
            EventDefinition eventDefinition) {
        return actionNode
                .name(eventDefinition.getName())
                .metaData(Metadata.TRIGGER_TYPE, "ProduceMessage")
                .metaData(Metadata.MAPPING_VARIABLE, DEFAULT_WORKFLOW_VAR)
                .metaData(Metadata.TRIGGER_REF, eventDefinition.getType())
                .metaData(Metadata.MESSAGE_TYPE, JSON_NODE);
    }

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> EventNodeFactory<T> consumeEventNode(EventNodeFactory<T> eventNode,
            EventDefinition eventDefinition) {
        return eventNode
                .name(eventDefinition.getName())
                .variableName(DEFAULT_WORKFLOW_VAR)
                .metaData(Metadata.EVENT_TYPE, "message")
                .metaData(Metadata.TRIGGER_REF, eventDefinition.getType())
                .metaData(Metadata.MESSAGE_TYPE, JSON_NODE)
                .metaData(Metadata.TRIGGER_TYPE, "ConsumeMessage")
                .eventType("Message-" + eventDefinition.getType());
    }
}
