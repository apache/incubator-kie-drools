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
package org.kie.kogito.codegen.sample.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.kie.api.io.Resource;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.ConfigGenerator;
import org.kie.kogito.codegen.api.Generator;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.sample.generator.config.SampleConfigGenerator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import static java.util.stream.Collectors.toList;
import static org.drools.codegen.common.GeneratedFileType.REST;

public class SampleCodegen implements Generator {

    public static String GENERATOR_NAME = "sample";
    public static Set<String> SUPPORTED_EXTENSIONS = Collections.singleton("txt");
    private final KogitoBuildContext context;
    private final Collection<SampleResource> sampleResources;

    public static SampleCodegen ofCollectedResources(KogitoBuildContext context, Collection<CollectedResource> resources) {
        Collection<CollectedResource> rawSampleResource = resources.stream()
                .filter(resource -> SUPPORTED_EXTENSIONS.stream().anyMatch(resource.resource().getSourcePath()::endsWith))
                .collect(toList());
        return new SampleCodegen(context, rawSampleResource);
    }

    private SampleCodegen(KogitoBuildContext context, Collection<CollectedResource> rawSampleResource) {
        this.context = context;
        this.sampleResources = parseResources(rawSampleResource);
    }

    @Override
    public Optional<ApplicationSection> section() {
        return Optional.of(new SampleContainerGenerator(context(), sampleResources));
    }

    @Override
    public boolean isEmpty() {
        return sampleResources.isEmpty();
    }

    @Override
    public Collection<GeneratedFile> generate() {
        if (isEmpty()) {
            return Collections.emptyList();
        }

        TemplatedGenerator generator = TemplatedGenerator.builder()
                .withFallbackContext(QuarkusKogitoBuildContext.CONTEXT_NAME)
                .build(context(), "SampleRestResource");

        CompilationUnit compilationUnit = generator.compilationUnitOrThrow();

        if (context.hasDI()) {
            compilationUnit.findAll(FieldDeclaration.class,
                    SampleCodegen::isSampleRuntimeField).forEach(fd -> context.getDependencyInjectionAnnotator().withInjection(fd));
        } else {
            compilationUnit.findAll(FieldDeclaration.class,
                    SampleCodegen::isSampleRuntimeField).forEach(SampleCodegen::initializeSampleRuntimeField);
        }

        return context.hasRESTForGenerator(this) ? Collections.singleton(new GeneratedFile(REST, generator.generatedFilePath(), compilationUnit.toString())) : Collections.emptyList();
    }

    @Override
    public Optional<ConfigGenerator> configGenerator() {
        return Optional.of(new SampleConfigGenerator(context()));
    }

    @Override
    public KogitoBuildContext context() {
        return context;
    }

    @Override
    public String name() {
        return GENERATOR_NAME;
    }

    public static boolean isSampleRuntimeField(FieldDeclaration fieldDeclaration) {
        return fieldDeclaration.getElementType().asClassOrInterfaceType().getNameAsString().equals("SampleRuntime");
    }

    private static Collection<SampleResource> parseResources(Collection<CollectedResource> rawSampleResource) {
        return rawSampleResource.stream()
                .map(cr -> new SampleResource(removeExtension(cr.basePath().getFileName().toString()), getContent(cr.resource())))
                .collect(toList());
    }

    private static String removeExtension(String rawPath) {
        return rawPath.substring(0, rawPath.lastIndexOf('.'));
    }

    private static String getContent(Resource resource) {
        try {
            return new BufferedReader(new InputStreamReader(
                    resource.getInputStream(), StandardCharsets.UTF_8))
                            .lines()
                            .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible to read resource " + resource.getSourcePath(), e);
        }
    }

    private static void initializeSampleRuntimeField(FieldDeclaration fd) {
        fd.getVariable(0).setInitializer(new ObjectCreationExpr().setType(SampleContainerGenerator.SAMPLE_RUNTIME_CLASSNAME)
                .addArgument(new ObjectCreationExpr().setType("Application")));
    }
}
