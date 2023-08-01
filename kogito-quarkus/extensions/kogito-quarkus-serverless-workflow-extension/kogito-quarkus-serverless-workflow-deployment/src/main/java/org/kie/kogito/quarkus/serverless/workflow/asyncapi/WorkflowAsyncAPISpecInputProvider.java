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
package org.kie.kogito.quarkus.serverless.workflow.asyncapi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.kogito.quarkus.serverless.workflow.WorkflowCodeGenUtils;
import org.kie.kogito.serverless.workflow.operationid.WorkflowOperationIdFactoryProvider;

import io.quarkiverse.asyncapi.config.AsyncAPISpecInput;
import io.quarkiverse.asyncapi.config.AsyncAPISpecInputProvider;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;
import io.smallrye.config.ConfigSourceContext;

public class WorkflowAsyncAPISpecInputProvider implements AsyncAPISpecInputProvider {

    @Override
    public AsyncAPISpecInput read(ConfigSourceContext context) throws IOException {
        try (Stream<Path> workflowFiles = Files.walk(Path.of(System.getProperty("user.dir")))) {
            return new AsyncAPISpecInput(WorkflowCodeGenUtils
                    .operationResources(workflowFiles, f -> f.getType() == Type.ASYNCAPI,
                            Optional.ofNullable(context.getValue(WorkflowOperationIdFactoryProvider.PROPERTY_NAME).getValue()))
                    .collect(Collectors.toMap(resource -> resource.getOperationId().getFileName(), AsyncAPIInputStreamSupplier::new, (key1, key2) -> key1)));
        }
    }
}
