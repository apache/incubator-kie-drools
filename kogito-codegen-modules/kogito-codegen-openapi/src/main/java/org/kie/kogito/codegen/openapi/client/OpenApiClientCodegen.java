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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.drools.core.util.StringUtils;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.context.ContextAttributesConstants;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.AbstractGenerator;
import org.kie.kogito.codegen.openapi.client.generator.OpenApiClientGeneratorWrapper;
import org.kie.kogito.codegen.openapi.client.io.PathResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.serverlessworkflow.api.workflow.BaseWorkflow;

import static java.util.stream.Collectors.toList;

/**
 * Entry point for the OpenAPIClient generator code.
 **/
public class OpenApiClientCodegen extends AbstractGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiClientCodegen.class);
    private static final String BASE_PACKAGE = "openapi.client";
    private static final String GEN_BASE_PATH = "openapi";
    private static final int PRIORITY = 1;

    private final List<OpenApiSpecDescriptor> openApiSpecDescriptors;
    private final GeneratedFileBuilder fileBuilder;

    private OpenApiClientCodegen(KogitoBuildContext context, String name, List<OpenApiSpecDescriptor> openApiSpecDescriptors) {
        super(context, name);
        this.fileBuilder = new GeneratedFileBuilder(context);
        if (openApiSpecDescriptors == null) {
            this.openApiSpecDescriptors = Collections.emptyList();
        } else {
            this.openApiSpecDescriptors = openApiSpecDescriptors;
        }
    }

    public static OpenApiClientCodegen ofCollectedResources(KogitoBuildContext context, Collection<CollectedResource> resources) {
        // In the future, we can create a provider for these resources to facilitate implementations for DRL, DMN, BPMN..
        final List<OpenApiSpecDescriptor> openApiSpecDescriptors =
                resources.stream().map(CollectedResource::resource)
                        .flatMap(resource -> ServerlessWorkflowCodegenUtils.SUPPORTED_SW_EXTENSIONS
                                .stream()
                                .filter(ext -> resource.getSourcePath().endsWith(ext))
                                .map(e -> {
                                    try (final Reader reader = resource.getReader()) {
                                        return BaseWorkflow.fromSource(StringUtils.readFileAsString(reader));
                                    } catch (IOException ex) {
                                        throw new OpenApiClientParsingException("Failed to parse Serverless Workflow file " + resource.getSourcePath(), ex);
                                    }
                                }))
                        .filter(ServerlessWorkflowCodegenUtils::acceptOnlyWithOpenAPIOperation)
                        .map(workflow -> ServerlessWorkflowCodegenUtils.fromSWFunctions(workflow.getFunctions()))
                        .flatMap(Collection::stream)
                        .collect(toList());
        return new OpenApiClientCodegen(context, "openapispecs", openApiSpecDescriptors);
    }

    @Override
    public int priority() {
        return PRIORITY;
    }

    public List<OpenApiSpecDescriptor> getOpenAPISpecResources() {
        return openApiSpecDescriptors;
    }

    @Override
    public Optional<ApplicationSection> section() {
        return Optional.empty();
    }

    @Override
    protected Collection<GeneratedFile> internalGenerate() {
        final List<GeneratedFile> generatedFiles = new ArrayList<>();
        this.openApiSpecDescriptors.forEach(descriptor -> {
            LOGGER.debug("Generating OpenApi classes based on {}", descriptor.getResourceName());
            final String openApiGeneratorOutputDir = this.getOutputDirForOpenAPIGen(descriptor);
            try {
                // save the descriptor file in a temporary location
                final String resolvedPath =
                        PathResolverFactory.newResolver(descriptor, this.context()).resolve(descriptor);
                // generate the openapi client files
                final List<GeneratedFile> files =
                        OpenApiClientGeneratorWrapper.newInstance(resolvedPath, openApiGeneratorOutputDir, this.context())
                                .withPackage(this.getBasePackageFor(descriptor))
                                .generate(descriptor)
                                .stream()
                                .filter(f -> f.toPath().toString().toLowerCase().endsWith(".java"))
                                .map(f -> this.fileBuilder.build(f, descriptor))
                                .collect(toList());
                generatedFiles.addAll(files);
            } finally {
                this.cleanOpenApiGeneratorCode(openApiGeneratorOutputDir);
            }
        });
        this.context().addContextAttribute(ContextAttributesConstants.OPENAPI_DESCRIPTORS, this.openApiSpecDescriptors);
        return generatedFiles;
    }

    @Override
    public boolean isEmpty() {
        return openApiSpecDescriptors.isEmpty();
    }

    /**
     * Deletes every file generated by the OpenApi Generator tool
     *
     * @param outputDir the target dir
     */
    private void cleanOpenApiGeneratorCode(final String outputDir) {
        try (Stream<Path> files = Files.walk(Paths.get(outputDir))) {
            files.sorted(Comparator.reverseOrder()).map(Path::toFile).filter(File::exists).forEach(File::delete);
        } catch (IOException e) {
            LOGGER.warn("Impossible to clean up OpenApi generator output dir", e);
        }
    }

    /**
     * Base package for the generated OpenAPI Java Client files
     *
     * @param resource the processed resource
     * @return the base package for the Java classes
     */
    private String getBasePackageFor(final OpenApiSpecDescriptor resource) {
        return this.context().getPackageName() + "." + BASE_PACKAGE + "." + resource.getId();
    }

    /**
     * Gets the temporary directory were the OpenAPI Generator tool will generate the client project.
     *
     * @param resource the given OpenAPI spec resource as defined in the input file
     * @return the final path where to generate the OpenAPI Client project
     */
    private String getOutputDirForOpenAPIGen(final OpenApiSpecDescriptor resource) {
        return String.join("/",
                OpenApiUtils.getEndUserTargetDir(this.context()),
                GEN_BASE_PATH,
                resource.getId());
    }
}
