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
package org.kie.kogito.serverless.workflow.parser.handlers;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.mvel.java.JavaDialect;
import org.jbpm.compiler.canonical.descriptors.AbstractServiceTaskDescriptor;
import org.jbpm.compiler.canonical.descriptors.TaskDescriptor;
import org.jbpm.process.core.datatype.DataTypeResolver;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.AbstractCompositeNodeFactory;
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.kie.kogito.internal.utils.ConversionUtils;
import org.kie.kogito.jackson.utils.JsonNodeVisitor;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;
import org.kie.kogito.process.expr.ExpressionWorkItemResolver;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;
import org.kie.kogito.serverless.workflow.suppliers.ConfigSuppliedWorkItemSupplier;
import org.kie.kogito.serverless.workflow.suppliers.ExpressionActionSupplier;
import org.kie.kogito.serverless.workflow.suppliers.ParamsRestBodyBuilderSupplier;
import org.kie.kogito.serverless.workflow.suppliers.SysoutActionSupplier;
import org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils;
import org.kie.kogito.serverless.workflow.workitemparams.ObjectResolver;
import org.kogito.workitem.rest.RestWorkItemHandler;
import org.kogito.workitem.rest.decorators.ApiKeyAuthDecorator;
import org.kogito.workitem.rest.decorators.BearerTokenAuthDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.UnknownType;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.actions.Action;
import io.serverlessworkflow.api.events.EventRef;
import io.serverlessworkflow.api.filters.ActionDataFilter;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionRef;
import io.serverlessworkflow.api.functions.SubFlowRef;
import io.serverlessworkflow.api.interfaces.State;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import static org.kie.kogito.internal.utils.ConversionUtils.concatPaths;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.OPENAPI_OPERATION_SEPARATOR;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.getServiceName;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.resolveFunctionMetadata;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.runtimeOpenApi;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.runtimeRestApi;

public abstract class CompositeContextNodeHandler<S extends State> extends StateHandler<S> {

    private static Logger logger = LoggerFactory.getLogger(CompositeContextNodeHandler.class);

    private static final String SCRIPT_TYPE_PARAM = "script";
    private static final String SYSOUT_TYPE_PARAM = "message";
    private static final String SERVICE_TASK_TYPE = "Service Task";
    private static final String WORKITEM_INTERFACE = "Interface";
    private static final String WORKITEM_OPERATION = "Operation";
    private static final String WORKITEM_INTERFACE_IMPL = "interfaceImplementationRef";
    private static final String WORKITEM_OPERATION_IMPL = "operationImplementationRef";
    private static final String WORKITEM_PARAM_TYPE = "ParameterType";
    private static final String WORKITEM_PARAM = "Parameter";
    private static final String SERVICE_INTERFACE_KEY = "interface";
    private static final String SERVICE_OPERATION_KEY = "operation";
    private static final String SERVICE_IMPL_KEY = "implementation";
    private static final String LANG_SEPARATOR = ":";
    private static final String METHOD_SEPARATOR = ":";
    private static final String INTFC_SEPARATOR = "::";

    protected CompositeContextNodeHandler(S state, Workflow workflow, ParserContext parserContext) {
        super(state, workflow, parserContext);
    }

    protected final CompositeContextNodeFactory<?> makeCompositeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        return factory.compositeContextNode(parserContext.newId()).name(state.getName()).autoComplete(true);
    }

    protected final <T extends AbstractCompositeNodeFactory<?, ?>> T handleActions(T embeddedSubProcess, List<Action> actions) {
        return handleActions(embeddedSubProcess, actions, null);
    }

    protected final <T extends AbstractCompositeNodeFactory<?, ?>> T handleActions(T embeddedSubProcess, List<Action> actions, String outputVar, String... extraVariables) {
        if (actions != null && !actions.isEmpty()) {
            NodeFactory<?, ?> startNode = embeddedSubProcess.startNode(parserContext.newId()).name("EmbeddedStart");
            NodeFactory<?, ?> currentNode = startNode;
            for (Action action : actions) {
                currentNode = connect(currentNode, getActionNode(embeddedSubProcess, action, outputVar, extraVariables));
            }
            connect(currentNode, embeddedSubProcess.endNode(parserContext.newId()).name("EmbeddedEnd").terminate(true)).done();
        } else {
            connect(embeddedSubProcess.startNode(parserContext.newId()).name("EmbeddedStart"), embeddedSubProcess.endNode(parserContext.newId()).name("EmbeddedEnd").terminate(true)).done();
        }
        return embeddedSubProcess;
    }

    protected final MakeNodeResult getActionNode(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            Action action) {
        return getActionNode(embeddedSubProcess, action, null);
    }

    public MakeNodeResult getActionNode(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            Action action, String collectVar, String... extraVariables) {
        ActionDataFilter actionFilter = action.getActionDataFilter();
        String fromExpr = null;
        String resultExpr = null;
        String toExpr = null;
        boolean useData = true;
        if (actionFilter != null) {
            fromExpr = actionFilter.getFromStateData();
            resultExpr = actionFilter.getResults();
            toExpr = actionFilter.getToStateData();
            useData = actionFilter.isUseResults();
        }
        if (action.getFunctionRef() != null) {
            return filterAndMergeNode(embeddedSubProcess, fromExpr, resultExpr, toExpr, useData,
                    (factory, inputVar, outputVar) -> getActionNode(factory, action.getFunctionRef(), inputVar, outputVar, collectVar, extraVariables));
        } else if (action.getEventRef() != null) {
            return filterAndMergeNode(embeddedSubProcess, fromExpr, resultExpr, toExpr, useData,
                    (factory, inputVar, outputVar) -> getActionNode(factory, action.getEventRef(), inputVar));
        } else if (action.getSubFlowRef() != null) {
            return filterAndMergeNode(embeddedSubProcess, fromExpr, resultExpr, toExpr, useData,
                    (factory, inputVar, outputVar) -> getActionNode(factory, action.getSubFlowRef(), inputVar, outputVar));
        } else {
            throw new IllegalArgumentException("Action node " + action.getName() + " of state " + state.getName() + " does not have function or event defined");
        }
    }

    private NodeFactory<?, ?> getActionNode(RuleFlowNodeContainerFactory<?, ?> factory,
            SubFlowRef subFlowRef,
            String inputVar,
            String outputVar) {
        return ServerlessWorkflowParser.subprocessNode(
                factory.subProcessNode(parserContext.newId()).name(subFlowRef.getWorkflowId()).processId(subFlowRef.getWorkflowId()).waitForCompletion(true),
                inputVar,
                outputVar);
    }

    private NodeFactory<?, ?> getActionNode(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            EventRef eventRef, String inputVar) {
        return sendEventNode(embeddedSubProcess.actionNode(parserContext.newId()), eventDefinition(eventRef.getTriggerEventRef()), eventRef.getData(), inputVar);
    }

    private NodeFactory<?, ?> getActionNode(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            FunctionRef functionRef, String inputVar, String outputVar, String collectVar, String... extraVariables) {
        String actionName = functionRef.getRefName();
        FunctionDefinition actionFunction = workflow.getFunctions().getFunctionDefs()
                .stream()
                .filter(wf -> wf.getName().equals(actionName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("cannot find function " + actionName));

        ActionType actionType = ActionType.from(actionFunction);
        String operation = actionType.getOperation(actionFunction);
        switch (actionType) {
            case SCRIPT:
                return embeddedSubProcess
                        .actionNode(parserContext.newId())
                        .name(actionName)
                        .action(JavaDialect.ID,
                                functionRef
                                        .getArguments().get(SCRIPT_TYPE_PARAM).asText());
            case EXPRESSION:
                return embeddedSubProcess
                        .actionNode(parserContext.newId())
                        .name(actionName)
                        .action(ExpressionActionSupplier.of(workflow, operation).withVarNames(inputVar, outputVar).withCollectVar(collectVar)
                                .withAddInputVars(extraVariables).build());
            case SYSOUT:
                return embeddedSubProcess
                        .actionNode(parserContext.newId())
                        .name(actionName)
                        .action(new SysoutActionSupplier(workflow.getExpressionLang(), functionRef.getArguments().get(SYSOUT_TYPE_PARAM).asText(), inputVar, extraVariables));
            case SERVICE:
                return addServiceParameters(embeddedSubProcess
                        .workItemNode(parserContext.newId())
                        .name(actionName)
                        .metaData(TaskDescriptor.KEY_WORKITEM_TYPE, SERVICE_TASK_TYPE)
                        .workName(SERVICE_TASK_TYPE)
                        .inMapping(inputVar, WORKITEM_PARAM)
                        .outMapping(WORKITEM_PARAM, outputVar), actionFunction, operation, functionRef.getArguments());
            case REST:
                return addRestParameters(buildRestWorkItem(embeddedSubProcess, actionFunction, inputVar, outputVar, functionRef.getArguments()), actionFunction, operation);
            case OPENAPI:
                return addOpenApiParameters(buildRestWorkItem(embeddedSubProcess, actionFunction, inputVar, outputVar, functionRef.getArguments()), actionFunction, operation);
            default:
                return emptyNode(embeddedSubProcess, actionName);
        }
    }

    private WorkItemNodeFactory<?> buildRestWorkItem(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            FunctionDefinition actionFunction,
            String inputVar,
            String outputVar,
            JsonNode functionArgs) {
        WorkItemNodeFactory<?> workItemFactory = embeddedSubProcess
                .workItemNode(parserContext.newId())
                .name(actionFunction.getName())
                .metaData(TaskDescriptor.KEY_WORKITEM_TYPE, RestWorkItemHandler.REST_TASK_TYPE)
                .workName(RestWorkItemHandler.REST_TASK_TYPE)
                .inMapping(inputVar, SWFConstants.MODEL_WORKFLOW_VAR)
                .outMapping(RestWorkItemHandler.RESULT, outputVar)
                .workParameter(RestWorkItemHandler.BODY_BUILDER, new ParamsRestBodyBuilderSupplier());
        if (functionArgs != null) {
            processArgs(workItemFactory, functionArgs, SWFConstants.MODEL_WORKFLOW_VAR, ObjectResolver.class);
        }
        return workItemFactory;
    }

    private <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> addServiceParameters(WorkItemNodeFactory<T> node,
            FunctionDefinition actionFunction,
            String operation, JsonNode functionArgs) {
        String intfc = null;
        String method = null;
        String lang = null;
        // try extracting from operation (format language:interface::method)
        if (operation != null) {
            int indexOf = operation.indexOf(INTFC_SEPARATOR);
            if (indexOf != -1) {
                method = operation.substring(indexOf + INTFC_SEPARATOR.length());
                operation = operation.substring(0, indexOf);
                indexOf = operation.indexOf(LANG_SEPARATOR);
                if (indexOf != -1) {
                    intfc = operation.substring(indexOf + LANG_SEPARATOR.length());
                    lang = operation.substring(0, indexOf);
                } else {
                    intfc = operation;
                }
            }
        }
        if (lang == null) {
            lang = resolveFunctionMetadata(
                    actionFunction, SERVICE_IMPL_KEY, parserContext.getContext(), String.class, "Java");
        }
        // fallback to metadata for backward compatibility
        if (intfc == null) {
            intfc = resolveFunctionMetadata(
                    actionFunction, SERVICE_INTERFACE_KEY, parserContext.getContext());
        }
        if (method == null) {
            method = resolveFunctionMetadata(
                    actionFunction, SERVICE_OPERATION_KEY, parserContext.getContext());
        }

        if (functionArgs == null || functionArgs.isEmpty()) {
            node.workParameter(WORKITEM_PARAM_TYPE, ServerlessWorkflowParser.JSON_NODE);
        } else {
            processArgs(node, functionArgs, WORKITEM_PARAM, ObjectResolver.class);
        }

        return node.workParameter(WORKITEM_INTERFACE, intfc)
                .workParameter(WORKITEM_OPERATION, method)
                .workParameter(WORKITEM_INTERFACE_IMPL, intfc)
                .workParameter(WORKITEM_OPERATION_IMPL, method)
                .workParameter(SERVICE_IMPL_KEY, lang);
    }

    private <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> addRestParameters(WorkItemNodeFactory<T> node,
            FunctionDefinition actionFunction,
            String operation) {
        String url = null;
        String method = null;
        // try extracting from operation (format method:url)
        if (operation != null) {
            int indexOf = operation.indexOf(METHOD_SEPARATOR);
            if (indexOf != -1) {
                method = operation.substring(0, indexOf);
                url = operation.substring(indexOf + METHOD_SEPARATOR.length());
            } else {
                url = operation;
            }
        }
        if (method == null) {
            method = resolveFunctionMetadata(actionFunction, "method", parserContext.getContext());
        }

        return node.workParameter(RestWorkItemHandler.URL, url)
                .workParameter(RestWorkItemHandler.METHOD, method)
                .workParameter(RestWorkItemHandler.USER, runtimeRestApi(actionFunction, "user", parserContext.getContext()))
                .workParameter(RestWorkItemHandler.PASSWORD, runtimeRestApi(actionFunction, "password", parserContext.getContext()))
                .workParameter(RestWorkItemHandler.HOST, runtimeRestApi(actionFunction, "host", parserContext.getContext()))
                .workParameter(RestWorkItemHandler.PORT, runtimeRestApi(actionFunction, "port", parserContext.getContext(), Integer.class, 8080))
                .workParameter(RestWorkItemHandler.BODY_BUILDER, new ParamsRestBodyBuilderSupplier())
                .workParameter(BearerTokenAuthDecorator.BEARER_TOKEN, runtimeRestApi(actionFunction, "access_token", parserContext.getContext()))
                .workParameter(ApiKeyAuthDecorator.KEY_PREFIX, runtimeRestApi(actionFunction, "api_key_prefix", parserContext.getContext()))
                .workParameter(ApiKeyAuthDecorator.KEY, runtimeRestApi(actionFunction, "api_key", parserContext.getContext()));
    }

    private NodeFactory<?, ?> addOpenApiParameters(WorkItemNodeFactory<?> node,
            FunctionDefinition function,
            String operation) {
        int indexOf = function.getOperation().indexOf(OPENAPI_OPERATION_SEPARATOR);
        String uri = operation.substring(0, indexOf);
        String serviceName = getServiceName(uri);
        String operationId = operation.substring(indexOf + OPENAPI_OPERATION_SEPARATOR.length());
        try {
            // although OpenAPIParser has built built in support to load uri, it messes up when using contextclassloader, so using our retrieval apis to get the content
            SwaggerParseResult result =
                    new OpenAPIParser().readContents(new String(URIContentLoaderFactory.buildLoader(URI.create(uri), parserContext.getContext().getClassLoader()).toBytes()), null, null);
            OpenAPI openAPI = result.getOpenAPI();
            if (openAPI == null) {
                throw new IllegalArgumentException("Problem parsing uri " + uri);
            }
            logger.debug("OpenAPI parser messages {}", result.getMessages());
            OpenAPIDescriptor openAPIDescriptor = OpenAPIDescriptor.of(openAPI, operationId);
            addSecurity(node, openAPI, serviceName);
            return node.workParameter(RestWorkItemHandler.URL,
                    runtimeOpenApi(serviceName, "base_path", String.class, OpenAPIDescriptor.getDefaultURL(openAPI, "http://localhost:8080"),
                            (key, clazz, defaultValue) -> new ConfigSuppliedWorkItemSupplier<String>(key, clazz, defaultValue, calculatedKey -> concatPaths(calculatedKey, openAPIDescriptor.getPath()),
                                    new LambdaExpr(new Parameter(new UnknownType(), "calculatedKey"),
                                            new MethodCallExpr(ConversionUtils.class.getCanonicalName() + ".concatPaths")
                                                    .addArgument(new NameExpr("calculatedKey")).addArgument(new StringLiteralExpr(openAPIDescriptor.getPath()))))))
                    .workParameter(RestWorkItemHandler.METHOD, openAPIDescriptor.getMethod());
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem retrieving uri " + uri);
        }
    }

    private void addSecurity(WorkItemNodeFactory<?> node, OpenAPI openAPI, String serviceName) {
        if (openAPI.getComponents() != null) {
            Map<String, SecurityScheme> schemes = openAPI.getComponents().getSecuritySchemes();
            if (schemes != null) {
                for (SecurityScheme scheme : schemes.values()) {
                    switch (scheme.getType()) {

                        case APIKEY:
                            node.workParameter(ApiKeyAuthDecorator.KEY_PREFIX, runtimeOpenApi(serviceName, "api_key_prefix", parserContext.getContext()))
                                    .workParameter(ApiKeyAuthDecorator.KEY, runtimeOpenApi(serviceName, "api_key", parserContext.getContext()))
                                    .workParameter(ApiKeyAuthDecorator.LOCATION, scheme.getIn())
                                    .workParameter(ApiKeyAuthDecorator.PARAMETER, scheme.getName());
                            break;
                        case HTTP:
                            // TODO http security scheme is not properly parsed for some reason (need to investigate it, in the mean time, trust user properties definition) 
                            break;
                        default:
                            logger.warn("Unsupported scheme type {}", scheme.getType());
                    }
                }
            }
            // add properties for htpp based authentication (both user and token), if specified
            node.workParameter(RestWorkItemHandler.USER, runtimeOpenApi(serviceName, "username", parserContext.getContext()))
                    .workParameter(RestWorkItemHandler.PASSWORD, runtimeOpenApi(serviceName, "password", parserContext.getContext()))
                    .workParameter(BearerTokenAuthDecorator.BEARER_TOKEN, runtimeOpenApi(serviceName, "access_token", parserContext.getContext()));
        }
    }

    private Map<String, Object> functionsToMap(JsonNode jsonNode) {
        Map<String, Object> map = new HashMap<>();
        if (jsonNode != null) {
            Iterator<Entry<String, JsonNode>> iter = jsonNode.fields();
            while (iter.hasNext()) {
                Entry<String, JsonNode> entry = iter.next();
                map.put(entry.getKey(), functionReference(JsonObjectUtils.simpleToJavaValue(entry.getValue())));
            }
        }
        return map;
    }

    private Object functionReference(Object object) {
        if (object instanceof JsonNode) {
            return JsonNodeVisitor.transformTextNode((JsonNode) object, node -> JsonObjectUtils.fromValue(ExpressionHandlerUtils.replaceExpr(workflow, node.asText())));
        } else if (object instanceof CharSequence) {
            return ExpressionHandlerUtils.replaceExpr(workflow, object.toString());
        } else {
            return object;
        }
    }

    private void processArgs(WorkItemNodeFactory<?> workItemFactory,
            JsonNode functionArgs, String paramName, Class<? extends ExpressionWorkItemResolver> clazz) {
        if (functionArgs.isObject()) {
            functionsToMap(functionArgs).entrySet().forEach(entry -> processArg(entry.getKey(), entry.getValue(), workItemFactory, paramName, clazz));
        } else {
            processArg(RestWorkItemHandler.CONTENT_DATA, functionReference(JsonObjectUtils.simpleToJavaValue(functionArgs)), workItemFactory, paramName, clazz);
        }
    }

    private void processArg(String key, Object value, WorkItemNodeFactory<?> workItemFactory, String paramName, Class<? extends ExpressionWorkItemResolver> clazz) {
        boolean isExpr = isExpression(value);
        workItemFactory
                .workParameter(key,
                        AbstractServiceTaskDescriptor.processWorkItemValue(workflow.getExpressionLang(), value, paramName, clazz, isExpr))
                .workParameterDefinition(key,
                        DataTypeResolver.fromObject(value, isExpr));
    }

    private boolean isExpression(Object expr) {
        return expr instanceof CharSequence && ExpressionHandlerFactory.get(workflow.getExpressionLang(), expr.toString()).isValid() || expr instanceof JsonNode;
    }

    private NodeFactory<?, ?> emptyNode(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess, String actionName) {
        return embeddedSubProcess
                .actionNode(parserContext.newId())
                .name(actionName)
                .action(JavaDialect.ID, "");
    }

}
