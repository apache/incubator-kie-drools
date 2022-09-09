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
package org.kie.kogito.quarkus.serverless.workflow.deployment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.codegen.common.GeneratedFile;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.jboss.jandex.IndexView;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;
import org.kie.kogito.process.expr.ExpressionHandler;
import org.kie.kogito.quarkus.common.deployment.KogitoAddonsPreGeneratedSourcesBuildItem;
import org.kie.kogito.quarkus.common.deployment.KogitoBuildContextBuildItem;
import org.kie.kogito.quarkus.serverless.workflow.WorkflowCodeGenUtils;
import org.kie.kogito.quarkus.serverless.workflow.WorkflowHandlerGenerator;
import org.kie.kogito.quarkus.serverless.workflow.openapi.WorkflowOpenApiHandlerGenerator;
import org.kie.kogito.quarkus.serverless.workflow.rpc.WorkflowRPCHandlerGenerator;
import org.kie.kogito.serverless.workflow.openapi.ServerlessWorkflowOASFilter;
import org.kie.kogito.serverless.workflow.parser.FunctionNamespace;
import org.kie.kogito.serverless.workflow.parser.FunctionTypeHandler;
import org.kie.kogito.serverless.workflow.parser.schema.OpenApiModelSchemaGenerator;
import org.kie.kogito.serverless.workflow.rpc.FileDescriptorHolder;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;
import io.quarkus.deployment.pkg.steps.NativeOrNativeSourcesBuild;
import io.quarkus.smallrye.openapi.deployment.spi.AddToOpenAPIDefinitionBuildItem;
import io.serverlessworkflow.api.Workflow;

/**
 * Main class of the Kogito Serverless Workflow extension
 */
public class ServerlessWorkflowAssetsProcessor {

    // Injecting Instance<WorkflowOpenApiHandlerGenerator> does not work here
    private static WorkflowHandlerGenerator[] generators = { WorkflowOpenApiHandlerGenerator.instance, WorkflowRPCHandlerGenerator.instance };

    @BuildStep
    FeatureBuildItem featureBuildItem() {
        return new FeatureBuildItem("kogito-serverless-workflow");
    }

    @BuildStep(onlyIf = NativeOrNativeSourcesBuild.class)
    NativeImageResourceBuildItem addExpressionHandlers(BuildProducer<ServiceProviderBuildItem> serviceProvider) {
        serviceProvider.produce(ServiceProviderBuildItem.allProvidersFromClassPath(ExpressionHandler.class.getCanonicalName()));
        serviceProvider.produce(ServiceProviderBuildItem.allProvidersFromClassPath(FunctionNamespace.class.getCanonicalName()));
        serviceProvider.produce(ServiceProviderBuildItem.allProvidersFromClassPath(FunctionTypeHandler.class.getCanonicalName()));
        return new NativeImageResourceBuildItem(FileDescriptorHolder.DESCRIPTOR_PATH);
    }

    @BuildStep
    void addWorkItemHandlers(KogitoBuildContextBuildItem contextBI, CombinedIndexBuildItem indexBuildItem, BuildProducer<KogitoAddonsPreGeneratedSourcesBuildItem> sources) {
        KogitoBuildContext context = contextBI.getKogitoBuildContext();
        IndexView index = indexBuildItem.getIndex();
        Collection<GeneratedFile> generatedFiles = new ArrayList<>();
        for (WorkflowHandlerGenerator generator : generators) {
            for (GeneratedFile generated : generator.generateHandlerClasses(context, index)) {
                generatedFiles.add(generated);
                context.addGeneratedHandler(WorkflowCodeGenUtils.getRefHandler(generated));
            }
        }
        sources.produce(new KogitoAddonsPreGeneratedSourcesBuildItem(generatedFiles));
    }

    @BuildStep
    void addOpenAPIModelSchema(KogitoBuildContextBuildItem contextBuildItem, BuildProducer<AddToOpenAPIDefinitionBuildItem> openAPIProducer) {
        Collection<OpenAPI> inputModelSchemas = getWorkflows(contextBuildItem.getKogitoBuildContext())
                .map(OpenApiModelSchemaGenerator::new)
                .map(OpenApiModelSchemaGenerator::generateOpenAPIModelSchema)
                .collect(Collectors.toList());

        openAPIProducer.produce(new AddToOpenAPIDefinitionBuildItem(new ServerlessWorkflowOASFilter(inputModelSchemas)));
    }

    private static Stream<Workflow> getWorkflows(KogitoBuildContext kogitoBuildContext) {
        Path[] paths = kogitoBuildContext.getAppPaths().getPaths();

        Stream<Path> workflowFiles = CollectedResourceProducer.fromPaths(paths).stream()
                .map(collectedResource -> collectedResource.resource().getSourcePath())
                .map(Paths::get);

        return WorkflowCodeGenUtils.getWorkflows(workflowFiles);
    }
}
