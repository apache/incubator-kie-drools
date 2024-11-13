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
package org.kie.kogito.serverless.workflow.parser;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.process.core.validation.impl.ProcessValidationErrorImpl;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.ruleflow.core.validation.RuleFlowProcessValidator;
import org.jbpm.workflow.core.WorkflowModelValidator;
import org.kie.kogito.codegen.api.GeneratedInfo;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.internal.utils.ConversionUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.process.validation.ValidationException;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.extensions.OutputSchema;
import org.kie.kogito.serverless.workflow.operationid.WorkflowOperationIdFactoryProvider;
import org.kie.kogito.serverless.workflow.parser.handlers.StateHandler;
import org.kie.kogito.serverless.workflow.parser.handlers.StateHandlerFactory;
import org.kie.kogito.serverless.workflow.parser.handlers.validation.WorkflowValidator;
import org.kie.kogito.serverless.workflow.suppliers.JsonSchemaValidatorSupplier;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;
import org.kie.kogito.serverless.workflow.utils.WorkflowFormat;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.datainputschema.DataInputSchema;
import io.serverlessworkflow.api.timeouts.TimeoutsDefinition;
import io.serverlessworkflow.api.timeouts.WorkflowExecTimeout;
import io.serverlessworkflow.api.workflow.Constants;

import static org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.compoundURI;
import static org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.readBytes;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.getBaseURI;

public class ServerlessWorkflowParser {

    public static final String NODE_START_NAME = "Start";
    public static final String NODE_END_NAME = "End";
    public static final String DEFAULT_PACKAGE = "org.kie.kogito.serverless";
    public static final String DEFAULT_VERSION = "1.0";

    public static final String JSON_NODE = "com.fasterxml.jackson.databind.JsonNode";
    public static final String DEFAULT_WORKFLOW_VAR = SWFConstants.DEFAULT_WORKFLOW_VAR;

    private NodeIdGenerator idGenerator = DefaultNodeIdGenerator.get();
    private Workflow workflow;

    private GeneratedInfo<KogitoWorkflowProcess> processInfo;
    private KogitoBuildContext context;

    public static ServerlessWorkflowParser of(Reader workflowFile, WorkflowFormat workflowFormat, KogitoBuildContext context) throws IOException {
        return of(ServerlessWorkflowUtils.getWorkflow(workflowFile, workflowFormat), context);
    }

    /**
     * @deprecated use method that accepts WorkflowFormat enumeration
     */
    @Deprecated
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

    public ServerlessWorkflowParser withBaseURI(Path baseURI) {
        return withBaseURI(baseURI.toUri());
    }

    public ServerlessWorkflowParser withBaseURI(URI baseURI) {
        return withBaseURI(baseURI.toString());
    }

    public ServerlessWorkflowParser withBaseURI(URL baseURI) {
        return withBaseURI(baseURI.toString());
    }

    public ServerlessWorkflowParser withBaseURI(String baseURI) {
        ServerlessWorkflowUtils.withBaseURI(workflow, baseURI);
        return this;
    }

    private ServerlessWorkflowParser(Workflow workflow, KogitoBuildContext context) {
        this.workflow = workflow;
        this.context = context;
    }

    private GeneratedInfo<KogitoWorkflowProcess> parseProcess() {

        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess(workflow.getId(), !workflow.isKeepActive())
                .name(workflow.getName() == null ? workflow.getId() : workflow.getName())
                .version(workflow.getVersion() == null ? DEFAULT_VERSION : workflow.getVersion())
                .packageName(workflow.getMetadata() != null ? workflow.getMetadata().getOrDefault("package",
                        DEFAULT_PACKAGE) : DEFAULT_PACKAGE)
                .visibility("Public")
                .expressionLanguage(workflow.getExpressionLang())
                .metaData(Metadata.VARIABLE, DEFAULT_WORKFLOW_VAR)
                .variable(DEFAULT_WORKFLOW_VAR, new ObjectDataType(JsonNode.class), ObjectMapperFactory.listenerAware().createObjectNode())
                .type(KogitoWorkflowProcess.SW_TYPE);
        ParserContext parserContext =
                new ParserContext(idGenerator, factory, context, WorkflowOperationIdFactoryProvider.getFactory(context.getApplicationProperty(WorkflowOperationIdFactoryProvider.PROPERTY_NAME)));
        WorkflowValidator.validateStart(workflow, parserContext);
        modelValidator(parserContext, Optional.ofNullable(workflow.getDataInputSchema())).ifPresent(factory::inputValidator);
        modelValidator(parserContext, ServerlessWorkflowUtils.getExtension(workflow, OutputSchema.class).map(OutputSchema::getOutputSchema)).ifPresent(factory::outputValidator);
        loadConstants(factory, parserContext);
        Collection<StateHandler<?>> handlers =
                workflow.getStates().stream().map(state -> StateHandlerFactory.getStateHandler(state, workflow, parserContext))
                        .filter(Optional::isPresent).map(Optional::get).filter(state -> !state.usedForCompensation()).collect(Collectors.toList());
        handlers.forEach(StateHandler::handleStart);
        handlers.forEach(StateHandler::handleEnd);
        handlers.forEach(StateHandler::handleState);
        handlers.forEach(StateHandler::handleTransitions);
        handlers.forEach(StateHandler::handleConnections);

        if (parserContext.isCompensation()) {
            factory.metaData(Metadata.COMPENSATION, true);
            factory.metaData(Metadata.COMPENSATE_WHEN_ABORTED, true);
            factory.addCompensationContext(workflow.getId());
        }
        TimeoutsDefinition timeouts = workflow.getTimeouts();
        if (timeouts != null) {
            WorkflowExecTimeout workflowTimeout = timeouts.getWorkflowExecTimeout();
            if (workflowTimeout != null) {
                factory.metaData(Metadata.PROCESS_DURATION, workflowTimeout.getDuration());
            }
        }

        Collection<String> tags = workflow.getAnnotations();
        if (tags != null && !tags.isEmpty()) {
            factory.metaData(Metadata.TAGS, tags);
        }
        String description = workflow.getDescription();
        if (!ConversionUtils.isEmpty(description)) {
            factory.metaData(Metadata.DESCRIPTION, description);
        }
        List<String> annotations = workflow.getAnnotations();
        if (!annotations.isEmpty()) {
            factory.metaData(Metadata.ANNOTATIONS, annotations);
        }
        factory.link();
        List<ProcessValidationError> errors = RuleFlowProcessValidator.getInstance().validateProcess(factory.getProcess(), new ArrayList<>());
        parserContext.validationErrors().forEach(m -> errors.add(new ProcessValidationErrorImpl(factory.getProcess(), m)));
        if (!errors.isEmpty()) {
            throw new ValidationException(factory.getProcess().getId(), errors);
        }
        return new GeneratedInfo<>(factory.getProcess(), parserContext.generatedFiles());
    }

    private Optional<WorkflowModelValidator> modelValidator(ParserContext parserContext, Optional<DataInputSchema> schema) {
        return schema.map(s -> new JsonSchemaValidatorSupplier(JsonSchemaReader.read(
                getBaseURI(workflow).map(u -> compoundURI(u, s.getSchema())).orElseGet(() -> s.getSchema()),
                readBytes(s.getSchema(), workflow, parserContext)), s.isFailOnValidationErrors()));
    }

    public GeneratedInfo<KogitoWorkflowProcess> getProcessInfo() {
        if (processInfo == null) {
            processInfo = parseProcess();
        }
        return processInfo;
    }

    private void loadConstants(RuleFlowProcessFactory factory, ParserContext parserContext) {
        Constants constants = workflow.getConstants();
        if (constants != null) {
            if (constants.getRefValue() != null) {
                try {
                    constants.setConstantsDef(ObjectMapperFactory.get().readValue(readBytes(constants.getRefValue(), workflow, parserContext), JsonNode.class));
                } catch (IOException e) {
                    parserContext.addValidationError("Invalid file " + constants.getRefValue() + e);
                    return;
                }
            }
            factory.metaData(Metadata.CONSTANTS, constants.getConstantsDef());
        }
    }
}
