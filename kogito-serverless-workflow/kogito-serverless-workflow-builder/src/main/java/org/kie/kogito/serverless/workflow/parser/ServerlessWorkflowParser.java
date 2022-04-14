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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.tags.Tag;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.ruleflow.core.factory.JoinFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.ruleflow.core.factory.SubProcessNodeFactory;
import org.jbpm.ruleflow.core.factory.TimerNodeFactory;
import org.jbpm.workflow.core.impl.DataAssociation;
import org.jbpm.workflow.core.impl.DataDefinition;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;
import org.kie.kogito.codegen.api.GeneratedInfo;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.parser.handlers.StateHandler;
import org.kie.kogito.serverless.workflow.parser.handlers.StateHandlerFactory;
import org.kie.kogito.serverless.workflow.parser.handlers.validation.WorkflowValidator;
import org.kie.kogito.serverless.workflow.parser.schema.OpenApiModelSchemaGenerator;
import org.kie.kogito.serverless.workflow.parser.schema.WorkflowModelSchemaRef;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.events.EventDefinition;
import io.serverlessworkflow.api.workflow.Constants;

import static org.jbpm.process.core.timer.Timer.TIME_DURATION;
import static org.jbpm.ruleflow.core.Metadata.UNIQUE_ID;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.processResourceFile;

public class ServerlessWorkflowParser {

    public static final String NODE_START_NAME = "Start";
    public static final String NODE_END_NAME = "End";
    public static final String DEFAULT_NAME = "workflow";
    public static final String DEFAULT_PACKAGE = "org.kie.kogito.serverless";
    public static final String DEFAULT_VERSION = "1.0";

    public static final String JSON_NODE = "com.fasterxml.jackson.databind.JsonNode";
    public static final String DEFAULT_WORKFLOW_VAR = SWFConstants.DEFAULT_WORKFLOW_VAR;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerlessWorkflowParser.class);

    private NodeIdGenerator idGenerator = DefaultNodeIdGenerator.get();
    private Workflow workflow;
    private GeneratedInfo<KogitoWorkflowProcess> processInfo;
    private KogitoBuildContext context;

    public static ServerlessWorkflowParser of(Reader workflowFile, String workflowFormat, KogitoBuildContext context) throws IOException {
        return of(ServerlessWorkflowUtils.getObjectMapper(workflowFormat).readValue(workflowFile, Workflow.class), context);
    }

    public static ServerlessWorkflowParser of(Workflow workflow, KogitoBuildContext context) {
        return new ServerlessWorkflowParser(workflow, context);
    }

    public ServerlessWorkflowParser withIdGenerator(NodeIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        return this;
    }

    private ServerlessWorkflowParser(Workflow workflow, KogitoBuildContext context) {
        this.workflow = workflow;
        this.context = context;
    }

    private GeneratedInfo<KogitoWorkflowProcess> parseProcess() {
        WorkflowValidator.validateStart(workflow);
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess(workflow.getId())
                .name(workflow.getName() == null ? DEFAULT_NAME : workflow.getName())
                .version(workflow.getVersion() == null ? DEFAULT_VERSION : workflow.getVersion())
                .packageName(workflow.getMetadata() != null ? workflow.getMetadata().getOrDefault("package",
                        DEFAULT_PACKAGE) : DEFAULT_PACKAGE)
                .visibility("Public")
                .variable(DEFAULT_WORKFLOW_VAR, new ObjectDataType(JsonNode.class), ObjectMapperFactory.get().createObjectNode());
        ParserContext parserContext = new ParserContext(idGenerator, factory, context);
        loadConstants(workflow, parserContext);
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

        Collection<Tag> tags = getTags(workflow);
        if (!tags.isEmpty()) {
            factory.metaData(Metadata.TAGS, tags);
        }

        WorkflowModelSchemaRef schemaRef = new OpenApiModelSchemaGenerator(this.workflow, parserContext).generateModelSchema();
        if (schemaRef.hasInputModel()) {
            factory.metaData(Metadata.DATA_INPUT_SCHEMA_REF, schemaRef.getInputModelRef());
        }

        return new GeneratedInfo<>(factory.validate().getProcess(), parserContext.generatedFiles());
    }

    private static Collection<Tag> getTags(Workflow workflow) {
        Collection<Tag> tags = new ArrayList<>();
        if (workflow.getAnnotations() != null && !workflow.getAnnotations().isEmpty()) {
            for (String annotation : workflow.getAnnotations()) {
                tags.add(OASFactory.createObject(Tag.class).name(annotation));
            }
        }
        if (workflow.getDescription() != null) {
            tags.add(OASFactory.createObject(Tag.class)
                    .name(workflow.getId())
                    .description(workflow.getDescription()));
        }
        return Collections.unmodifiableCollection(tags);
    }

    public GeneratedInfo<KogitoWorkflowProcess> getProcessInfo() {
        if (processInfo == null) {
            processInfo = parseProcess();
        }
        return processInfo;
    }

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
        return actionNode
                .name(eventDefinition.getName())
                .metaData(Metadata.EVENT_TYPE, "message")
                .metaData(Metadata.MAPPING_VARIABLE, inputVar)
                .metaData(Metadata.TRIGGER_REF, eventDefinition.getType())
                .metaData(Metadata.MESSAGE_TYPE, JSON_NODE)
                .metaData(Metadata.TRIGGER_TYPE, "ProduceMessage");
    }

    public static <T extends NodeFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> T messageNode(T nodeFactory, EventDefinition eventDefinition, String inputVar) {
        return nodeFactory
                .name(eventDefinition.getName())
                .metaData(Metadata.EVENT_TYPE, "message")
                .metaData(Metadata.TRIGGER_MAPPING, inputVar)
                .metaData(Metadata.TRIGGER_REF, eventDefinition.getType())
                .metaData(Metadata.MESSAGE_TYPE, JSON_NODE)
                .metaData(Metadata.TRIGGER_TYPE, "ConsumeMessage")
                .metaData(Metadata.DATA_ONLY, isDataOnly(eventDefinition));
    }

    // TODO remove when SDK is updated to include dataOnly in EventDefinition, see https://github.com/serverlessworkflow/sdk-java/issues/183
    private static Boolean isDataOnly(EventDefinition eventDefinition) {
        Boolean result = Boolean.TRUE;
        Map<String, String> metadata = eventDefinition.getMetadata();
        final String dataOnlyKey = "dataOnly";
        if (metadata != null && metadata.containsKey(dataOnlyKey)) {
            result = Boolean.parseBoolean(metadata.get(dataOnlyKey));
        }
        return result;
    }

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> SplitFactory<T> eventBasedExclusiveSplitNode(SplitFactory<T> nodeFactory) {
        return nodeFactory.name("ExclusiveSplit_" + nodeFactory.getNode().getId())
                .type(Split.TYPE_XAND)
                .metaData(UNIQUE_ID, Long.toString(nodeFactory.getNode().getId()))
                .metaData("EventBased", "true");
    }

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> SplitFactory<T> exclusiveSplitNode(SplitFactory<T> nodeFactory) {
        return nodeFactory.name("ExclusiveSplit_" + nodeFactory.getNode().getId())
                .type(Split.TYPE_XOR)
                .metaData(UNIQUE_ID, Long.toString(nodeFactory.getNode().getId()));
    }

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> JoinFactory<T> joinExclusiveNode(JoinFactory<T> nodeFactory) {
        return nodeFactory.name("ExclusiveJoin_" + nodeFactory.getNode().getId())
                .type(Join.TYPE_XOR)
                .metaData(UNIQUE_ID, Long.toString(nodeFactory.getNode().getId()));
    }

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> TimerNodeFactory<T> timerNode(TimerNodeFactory<T> nodeFactory, String duration) {
        return nodeFactory.name("TimerNode_" + nodeFactory.getNode().getId())
                .type(TIME_DURATION)
                .delay(duration)
                .metaData(UNIQUE_ID, Long.toString(nodeFactory.getNode().getId()))
                .metaData("EventType", "Timer");
    }

    private static void loadConstants(Workflow workflow, ParserContext parserContext) {
        Constants constants = workflow.getConstants();
        if (constants != null && constants.getRefValue() != null) {
            processResourceFile(workflow, parserContext, constants.getRefValue()).ifPresent(bytes -> {
                try {
                    constants.setConstantsDef(ObjectMapperFactory.get().readValue(bytes, JsonNode.class));
                } catch (IOException e) {
                    throw new IllegalArgumentException("Invalid file " + constants.getRefValue(), e);
                }
            });
        }
    }
}
