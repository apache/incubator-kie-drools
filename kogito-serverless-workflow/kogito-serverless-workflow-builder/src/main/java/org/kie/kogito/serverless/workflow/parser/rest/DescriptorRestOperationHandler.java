/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.parser.rest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.kie.kogito.internal.utils.ConversionUtils;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.handlers.openapi.OpenAPIDescriptor;
import org.kie.kogito.serverless.workflow.parser.handlers.openapi.OpenAPIDescriptorFactory;
import org.kie.kogito.serverless.workflow.suppliers.ApiKeyAuthDecoratorSupplier;
import org.kie.kogito.serverless.workflow.suppliers.BasicAuthDecoratorSupplier;
import org.kie.kogito.serverless.workflow.suppliers.BearerTokenAuthDecoratorSupplier;
import org.kie.kogito.serverless.workflow.suppliers.ClientOAuth2AuthDecoratorSupplier;
import org.kie.kogito.serverless.workflow.suppliers.CollectionParamsDecoratorSupplier;
import org.kie.kogito.serverless.workflow.suppliers.ConfigSuppliedWorkItemSupplier;
import org.kie.kogito.serverless.workflow.suppliers.PasswordOAuth2AuthDecoratorSupplier;
import org.kie.kogito.serverless.workflow.utils.WorkflowOperationId;
import org.kogito.workitem.rest.RestWorkItemHandler;
import org.kogito.workitem.rest.auth.ApiKeyAuthDecorator;
import org.kogito.workitem.rest.auth.ApiKeyAuthDecorator.Location;
import org.kogito.workitem.rest.auth.BearerTokenAuthDecorator;
import org.kogito.workitem.rest.auth.ClientOAuth2AuthDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.UnknownType;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import static org.kie.kogito.internal.utils.ConversionUtils.concatPaths;
import static org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.buildLoader;
import static org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.readAllBytes;
import static org.kie.kogito.serverless.workflow.parser.handlers.NodeFactoryUtils.fillRest;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.ACCESS_TOKEN;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.API_KEY;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.API_KEY_PREFIX;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.PASSWORD_PROP;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.USER_PROP;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.runtimeOpenApi;

public class DescriptorRestOperationHandler implements RestOperationHandler {

    private static final Logger logger = LoggerFactory.getLogger(DescriptorRestOperationHandler.class);

    private final ParserContext parserContext;
    private final WorkflowOperationId operationId;

    public DescriptorRestOperationHandler(ParserContext parserContext, WorkflowOperationId operationId) {
        this.parserContext = parserContext;
        this.operationId = operationId;
    }

    @Override
    public <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> fillWorkItemHandler(WorkItemNodeFactory<T> node,
            Workflow workflow,
            FunctionDefinition actionFunction) {
        return fillRest(addOpenApiParameters(node, workflow, actionFunction));
    }

    private <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> addOpenApiParameters(WorkItemNodeFactory<T> node,
            Workflow workflow,
            FunctionDefinition function) {
        URI uri = operationId.getUri();
        String serviceName = operationId.getPackageName();
        try {
            // although OpenAPIParser has built in support to load uri, it messes up when using contextclassloader, so using our retrieval apis to get the content
            SwaggerParseResult result =
                    new OpenAPIParser().readContents(new String(readAllBytes(buildLoader(uri, parserContext.getContext().getClassLoader(), workflow, function.getAuthRef()))), null, null);
            OpenAPI openAPI = result.getOpenAPI();
            if (openAPI == null) {
                throw new IllegalArgumentException("Problem parsing uri " + uri);
            }
            logger.debug("OpenAPI parser messages {}", result.getMessages());
            OpenAPIDescriptor openAPIDescriptor = OpenAPIDescriptorFactory.of(openAPI, operationId.getOperation());
            addSecurity(node, openAPIDescriptor, serviceName);
            return node.workParameter(RestWorkItemHandler.URL,
                    runtimeOpenApi(serviceName, "base_path", String.class, OpenAPIDescriptorFactory.getDefaultURL(openAPI, "http://localhost:8080"),
                            (key, clazz, defaultValue) -> new ConfigSuppliedWorkItemSupplier<>(key, clazz, defaultValue, calculatedKey -> concatPaths(calculatedKey, openAPIDescriptor.getPath()),
                                    new LambdaExpr(new Parameter(new UnknownType(), "calculatedKey"),
                                            new MethodCallExpr(ConversionUtils.class.getCanonicalName() + ".concatPaths")
                                                    .addArgument(new NameExpr("calculatedKey")).addArgument(new StringLiteralExpr(openAPIDescriptor.getPath()))))))
                    .workParameter(RestWorkItemHandler.METHOD, openAPIDescriptor.getMethod())
                    .workParameter(RestWorkItemHandler.PARAMS_DECORATOR, new CollectionParamsDecoratorSupplier(openAPIDescriptor.getHeaderParams(), openAPIDescriptor.getQueryParams()));
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem retrieving uri " + uri);
        }
    }

    private void addSecurity(WorkItemNodeFactory<?> node, OpenAPIDescriptor openAPI, String serviceName) {
        Collection<Supplier<Expression>> authDecorators = new ArrayList<>();
        for (SecurityScheme scheme : openAPI.getSchemes()) {
            switch (scheme.getType()) {
                case APIKEY:
                    authDecorators.add(new ApiKeyAuthDecoratorSupplier(scheme.getName(), from(scheme.getIn())));
                    node.workParameter(ApiKeyAuthDecorator.KEY_PREFIX, runtimeOpenApi(serviceName, API_KEY_PREFIX, parserContext.getContext()))
                            .workParameter(ApiKeyAuthDecorator.KEY, runtimeOpenApi(serviceName, API_KEY, parserContext.getContext()));
                    break;
                case HTTP:
                    if (scheme.getScheme().equals("bearer")) {
                        authDecorators.add(new BearerTokenAuthDecoratorSupplier());
                        node.workParameter(RestWorkItemHandler.AUTH_METHOD, new BearerTokenAuthDecorator()).workParameter(BearerTokenAuthDecorator.BEARER_TOKEN,
                                runtimeOpenApi(serviceName, ACCESS_TOKEN, parserContext.getContext()));
                    } else if (scheme.getScheme().equals("basic")) {
                        authDecorators.add(new BasicAuthDecoratorSupplier());
                        node.workParameter(RestWorkItemHandler.USER, runtimeOpenApi(serviceName, USER_PROP, parserContext.getContext()))
                                .workParameter(RestWorkItemHandler.PASSWORD, runtimeOpenApi(serviceName, PASSWORD_PROP, parserContext.getContext()));
                    }
                    break;
                case OAUTH2:
                    // only support client and password credentials
                    if (scheme.getFlows().getClientCredentials() != null) {
                        authDecorators.add(new ClientOAuth2AuthDecoratorSupplier(scheme.getFlows().getClientCredentials().getTokenUrl(), scheme.getFlows().getClientCredentials().getRefreshUrl()));
                        node.workParameter(ClientOAuth2AuthDecorator.CLIENT_ID, runtimeOpenApi(serviceName, "client_id", parserContext.getContext()))
                                .workParameter(ClientOAuth2AuthDecorator.CLIENT_SECRET, runtimeOpenApi(serviceName, "client_secret", parserContext.getContext()));
                    } else if (scheme.getFlows().getPassword() != null) {
                        authDecorators.add(new PasswordOAuth2AuthDecoratorSupplier(scheme.getFlows().getPassword().getTokenUrl(), scheme.getFlows().getPassword().getRefreshUrl()));
                        node.workParameter(RestWorkItemHandler.USER, runtimeOpenApi(serviceName, USER_PROP, parserContext.getContext()))
                                .workParameter(RestWorkItemHandler.PASSWORD, runtimeOpenApi(serviceName, PASSWORD_PROP, parserContext.getContext()));
                    } else if (scheme.getFlows().getAuthorizationCode() != null) {
                        logger.warn("Unsupported scheme type {} for authorization code flow {}", scheme.getType(), scheme.getFlows().getAuthorizationCode());
                    } else if (scheme.getFlows().getImplicit() != null) {
                        logger.warn("Unsupported scheme type {} for implicit flow {}", scheme.getType(), scheme.getFlows().getImplicit());
                    }
                    break;
                default:
                    logger.warn("Unsupported scheme type {}", scheme.getType());
            }
        }
        if (!authDecorators.isEmpty()) {
            node.workParameter(RestWorkItemHandler.AUTH_METHOD, authDecorators);
        }
    }

    private static ApiKeyAuthDecorator.Location from(In in) {
        switch (in) {
            case COOKIE:
                return Location.COOKIE;
            case HEADER:
                return Location.HEADER;
            case QUERY:
            default:
                return Location.QUERY;
        }
    }

}
