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
package org.kie.kogito.quarkus.serverless.workflow.asyncapi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.kogito.quarkus.serverless.workflow.WorkflowCodeGenUtils;

import io.quarkiverse.asyncapi.generator.input.AsyncAPISpecInput;
import io.quarkiverse.asyncapi.generator.input.AsyncApiSpecInputProvider;
import io.quarkus.deployment.CodeGenContext;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;

public class WorkflowAsyncApiSpecInputProvider implements AsyncApiSpecInputProvider {

    private static final String KOGITO_PACKAGE_PREFIX = "org.kie.kogito.asyncAPI";

    @Override
    public AsyncAPISpecInput read(CodeGenContext context) {
        Path inputDir = context.inputDir();
        while (!Files.exists(inputDir)) {
            inputDir = inputDir.getParent();
        }
        try (Stream<Path> workflowFiles = Files.walk(inputDir)) {
            return new AsyncAPISpecInput(WorkflowCodeGenUtils.operationResources(workflowFiles, f -> f.getType() == Type.ASYNCAPI, context)
                    .collect(Collectors.toMap(resource -> resource.getOperationId().getFileName(), AsyncInputStreamSupplier::new, (key1, key2) -> key1)), KOGITO_PACKAGE_PREFIX);
        } catch (IOException io) {
            throw new IllegalStateException(io);
        }
    }
}
