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
package org.kie.kogito.quarkus.serverless.openapi;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.kogito.codegen.process.ProcessCodegen;
import org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory;
import org.kie.kogito.serverless.workflow.utils.OpenAPIOperationId;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;

import io.quarkiverse.openapi.generator.deployment.codegen.OpenApiSpecInputProvider;
import io.quarkiverse.openapi.generator.deployment.codegen.SpecInputModel;
import io.quarkus.deployment.CodeGenContext;
import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;

public class WorkflowOpenApiSpecInputProvider implements OpenApiSpecInputProvider {

    @Override
    public List<SpecInputModel> read(CodeGenContext context) {
        Path inputDir = context.inputDir();
        while (!Files.exists(inputDir)) {
            inputDir = inputDir.getParent();
        }
        try (Stream<Path> openApiFilesPaths = Files.walk(inputDir)) {
            return openApiFilesPaths
                    .filter(Files::isRegularFile)
                    .map(this::getWorkflow)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(this::getSpecInput)
                    .flatMap(x -> x)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Optional<Workflow> getWorkflow(Path p) {
        return ProcessCodegen.SUPPORTED_SW_EXTENSIONS.entrySet()
                .stream()
                .filter(e -> p.getFileName().toString().endsWith(e.getKey()))
                .map(e -> {
                    try (Reader r = Files.newBufferedReader(p)) {
                        return ServerlessWorkflowUtils.getWorkflow(r, e.getValue());
                    } catch (IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }).findFirst();
    }

    private Stream<SpecInputModel> getSpecInput(Workflow workflow) {
        return workflow.getFunctions().getFunctionDefs().stream().filter(ServerlessWorkflowUtils::isOpenApiOperation).map(f -> getSpecInput(f, workflow));
    }

    private SpecInputModel getSpecInput(FunctionDefinition function, Workflow workflow) {
        try {
            OpenAPIOperationId operationId = OpenAPIOperationId.fromOperation(function.getOperation());
            URI uri = operationId.getUri();
            return new SpecInputModel(operationId.getFileName(),
                    URIContentLoaderFactory.buildLoader(uri, Thread.currentThread().getContextClassLoader(), workflow, function.getAuthRef()).getInputStream());
        } catch (IOException io) {
            throw new IllegalStateException(io);
        }
    }
}
