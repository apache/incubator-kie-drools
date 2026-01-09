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
package org.kie.kogito.quarkus.serverless.workflow.rpc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.kogito.quarkus.serverless.workflow.WorkflowCodeGenUtils;
import org.kie.kogito.quarkus.serverless.workflow.WorkflowOperationResource;
import org.kie.kogito.serverless.workflow.io.URIContentLoader;
import org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;

public class WorkflowRPCCodeGenProvider implements CodeGenProvider {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowRPCCodeGenProvider.class);

    @Override
    public String providerId() {
        return "serverless-workflow-grpc";
    }

    @Override
    public String inputExtension() {
        return "json";
    }

    @Override
    public String inputDirectory() {
        return "resources";
    }

    @Override
    public boolean trigger(CodeGenContext context) throws CodeGenException {
        try (Stream<Path> rpcFilePaths = Files.walk(context.inputDir())) {
            Path outputPath = context.workDir().resolve("proto_temp");
            Files.createDirectories(outputPath);
            Collection<Path> protoFiles =
                    WorkflowCodeGenUtils.operationResources(rpcFilePaths, this::isRPC, context).map(r -> getPath(r, outputPath)).filter(Optional::isPresent).map(Optional::get)
                            .collect(Collectors.toList());
            logger.debug("Collected proto paths are {}", protoFiles);
            if (protoFiles.isEmpty()) {
                return false;
            }
            ProtocUtils.generateDescriptor(protoFiles, context);
            return true;
        } catch (Exception io) {
            logger.error("Exception generating RPC code", io);
            throw new CodeGenException(io);
        }
    }

    public Optional<Path> getPath(WorkflowOperationResource resource, Path outputPath) {
        logger.debug("Checking if resource {} should be written to {}", resource, outputPath);
        URIContentLoader contentLoader = resource.getContentLoader();
        Optional<Path> path = contentLoader.getPath();
        if (path.isPresent()) {
            return path;
        }
        try {
            Path tempPath = outputPath.resolve(resource.getOperationId().getFileName());
            Files.write(tempPath, URIContentLoaderFactory.readAllBytes(contentLoader));
            return Optional.of(tempPath);
        } catch (IOException io) {
            throw new IllegalStateException(io);
        }
    }

    private boolean isRPC(FunctionDefinition function) {
        return function.getType() == Type.RPC;
    }
}
