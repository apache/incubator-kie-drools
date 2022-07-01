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
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.tags.Tag;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.kie.kogito.codegen.api.GeneratedInfo;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.operationid.WorkflowOperationIdFactoryProvider;
import org.kie.kogito.serverless.workflow.parser.handlers.StateHandler;
import org.kie.kogito.serverless.workflow.parser.handlers.StateHandlerFactory;
import org.kie.kogito.serverless.workflow.parser.handlers.validation.WorkflowValidator;
import org.kie.kogito.serverless.workflow.parser.schema.OpenApiModelSchemaGenerator;
import org.kie.kogito.serverless.workflow.parser.schema.WorkflowModelSchemaRef;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.workflow.Constants;

import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.processResourceFile;

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
    private GeneratedInfo<KogitoWorkflowProcess> processInfo;
    private KogitoBuildContext context;

    public static ServerlessWorkflowParser of(Reader workflowFile, String workflowFormat, KogitoBuildContext context) throws IOException {
        return of(ServerlessWorkflowUtils.getWorkflow(workflowFile, workflowFormat), context);
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
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess(workflow.getId(), !workflow.isKeepActive())
                .name(workflow.getName() == null ? DEFAULT_NAME : workflow.getName())
                .version(workflow.getVersion() == null ? DEFAULT_VERSION : workflow.getVersion())
                .packageName(workflow.getMetadata() != null ? workflow.getMetadata().getOrDefault("package",
                        DEFAULT_PACKAGE) : DEFAULT_PACKAGE)
                .visibility("Public")
                .variable(DEFAULT_WORKFLOW_VAR, new ObjectDataType(JsonNode.class), ObjectMapperFactory.get().createObjectNode())
                .type(KogitoWorkflowProcess.SW_TYPE);
        ParserContext parserContext =
                new ParserContext(idGenerator, factory, context, WorkflowOperationIdFactoryProvider.getFactory(context.getApplicationProperty(WorkflowOperationIdFactoryProvider.PROPERTY_NAME)));
        loadConstants(workflow, factory, parserContext);
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

    private static void loadConstants(Workflow workflow, RuleFlowProcessFactory factory, ParserContext parserContext) {
        Constants constants = workflow.getConstants();
        if (constants != null) {
            if (constants.getRefValue() != null) {
                processResourceFile(workflow, parserContext, constants.getRefValue()).ifPresent(bytes -> {
                    try {
                        constants.setConstantsDef(ObjectMapperFactory.get().readValue(bytes, JsonNode.class));
                    } catch (IOException e) {
                        throw new IllegalArgumentException("Invalid file " + constants.getRefValue(), e);
                    }
                });
            }
            factory.metaData(Metadata.CONSTANTS, constants.getConstantsDef());
        }
    }
}
