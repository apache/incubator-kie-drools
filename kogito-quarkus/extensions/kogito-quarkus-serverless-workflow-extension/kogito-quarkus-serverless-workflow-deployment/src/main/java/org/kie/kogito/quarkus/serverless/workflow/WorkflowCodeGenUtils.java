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
package org.kie.kogito.quarkus.serverless.workflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.internal.SupportedExtensions;
import org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory;
import org.kie.kogito.serverless.workflow.operationid.WorkflowOperationId;
import org.kie.kogito.serverless.workflow.operationid.WorkflowOperationIdFactory;
import org.kie.kogito.serverless.workflow.operationid.WorkflowOperationIdFactoryProvider;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.CompilationUnit;

import io.quarkus.deployment.CodeGenContext;
import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;

public class WorkflowCodeGenUtils {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowCodeGenUtils.class);
    private static Map<Path, Optional<Workflow>> workflowCache = new WeakHashMap<>();

    private WorkflowCodeGenUtils() {
    }

    private static WorkflowOperationIdFactory operationIdFactory(CodeGenContext context) {
        return WorkflowOperationIdFactoryProvider.getFactory(context.config().getOptionalValue(WorkflowOperationIdFactoryProvider.PROPERTY_NAME, String.class));
    }

    public static Stream<WorkflowOperationResource> operationResources(Stream<Path> files, Predicate<FunctionDefinition> predicate, CodeGenContext context) {
        return getWorkflows(files).flatMap(w -> processFunction(w, predicate, operationIdFactory(context)));
    }

    public static Stream<Workflow> getWorkflows(Stream<Path> files) {
        return files
                .filter(Files::isRegularFile)
                .map(WorkflowCodeGenUtils::getWorkflow)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public static GeneratedFile fromCompilationUnit(KogitoBuildContext context, CompilationUnit unit, String className) {
        return new GeneratedFile(GeneratedFileType.SOURCE, Path.of("", context.getPackageName().split("\\.")).resolve(className + ".java"),
                unit.toString());
    }

    public static WorkflowHandlerGeneratedFile fromCompilationUnit(String workItemHandlerName, KogitoBuildContext context, CompilationUnit unit, String className) {
        return new WorkflowHandlerGeneratedFile(workItemHandlerName, GeneratedFileType.SOURCE, Path.of("", context.getPackageName().split("\\.")).resolve(className + ".java"),
                unit.toString());
    }

    private static Stream<WorkflowOperationResource> processFunction(Workflow workflow, Predicate<FunctionDefinition> predicate, WorkflowOperationIdFactory factory) {
        if (workflow.getFunctions() == null || workflow.getFunctions().getFunctionDefs() == null) {
            return Stream.empty();
        }
        return workflow.getFunctions().getFunctionDefs().stream().filter(predicate).map(f -> getResource(workflow, f, factory));
    }

    private static WorkflowOperationResource getResource(Workflow workflow, FunctionDefinition function, WorkflowOperationIdFactory factory) {
        WorkflowOperationId operationId = factory.from(workflow, function, Optional.empty());
        return new WorkflowOperationResource(operationId,
                URIContentLoaderFactory.buildLoader(operationId.getUri(), workflow, Optional.empty(), function.getAuthRef()));
    }

    private static Optional<Workflow> getWorkflow(Path path) {
        if (SupportedExtensions.getSWFExtensions().stream().anyMatch(ext -> path.toString().endsWith(ext))) {
            return workflowCache.computeIfAbsent(path, p -> {
                try {
                    return Optional.of(ServerlessWorkflowUtils.getWorkflow(p));
                } catch (IOException ex) {
                    logger.info("Error reading workflow file {}. Ignoring exception {}", p, ex);
                    return Optional.<Workflow> empty();
                }
            });
        }
        return Optional.empty();
    }
}
