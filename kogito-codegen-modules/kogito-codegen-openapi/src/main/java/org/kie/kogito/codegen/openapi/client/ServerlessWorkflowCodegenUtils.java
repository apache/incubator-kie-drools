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
package org.kie.kogito.codegen.openapi.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.workflow.Functions;

public final class ServerlessWorkflowCodegenUtils {

    public static final Set<String> SUPPORTED_SW_EXTENSIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(".sw.json", ".sw.yaml", ".sw.yml")));

    private ServerlessWorkflowCodegenUtils() {
    }

    /**
     * Creates a reference of the {@link OpenApiSpecDescriptor} from a given Serverless Workflow function definition
     *
     * @param function the given function definition
     * @return a reference of {@link OpenApiSpecDescriptor} based on the defined operation
     */
    public static OpenApiSpecDescriptor fromSWFunctionDefinition(final FunctionDefinition function) {
        Objects.requireNonNull(function.getOperation(), "Operation attribute in the Workflow Function definition can't be null");
        if (function.getOperation().isEmpty()) {
            throw new IllegalArgumentException("Resource can't be empty when creating a new OpenAPISpecResource");
        }
        if (ServerlessWorkflowUtils.isOpenApiOperation(function)) {
            final String resource = ServerlessWorkflowUtils.getOpenApiURI(function);
            final String operationId = ServerlessWorkflowUtils.getOpenApiOperationId(function);
            return new OpenApiSpecDescriptor(resource, operationId);
        }
        throw new IllegalArgumentException("Resource does not contain operation id");
    }

    /**
     * Creates a reference of the {@link OpenApiSpecDescriptor} from a given Serverless Workflow function collection
     *
     * @param functions the given function collection
     * @return a list of {@link OpenApiSpecDescriptor} for the given function collection
     */
    public static List<OpenApiSpecDescriptor> fromSWFunctions(final Functions functions) {
        final List<OpenApiSpecDescriptor> resources = new ArrayList<>();
        if (functions == null || functions.getFunctionDefs() == null) {
            return resources;
        }
        functions.getFunctionDefs().stream()
                .filter(ServerlessWorkflowUtils::isOpenApiOperation)
                .map(ServerlessWorkflowCodegenUtils::fromSWFunctionDefinition)
                .forEach(o -> {
                    final int idx = resources.indexOf(o);
                    if (idx >= 0) {
                        resources.get(idx).addRequiredOperations(o.getRequiredOperations());
                    } else {
                        resources.add(o);
                    }
                });
        return resources;
    }

    public static boolean acceptOnlyWithOpenAPIOperation(final Workflow workflow) {
        return workflow.getFunctions() != null &&
                workflow.getFunctions().getFunctionDefs() != null &&
                workflow.getFunctions().getFunctionDefs().stream().anyMatch(ServerlessWorkflowUtils::isOpenApiOperation);
    }
}
