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
package org.kie.kogito.addon.source.files.deployment;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;

import org.kie.kogito.addon.source.files.SourceFile;
import org.kie.kogito.addon.source.files.SourceFilesProviderProducer;
import org.kie.kogito.addon.source.files.SourceFilesRecorder;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;
import org.kie.kogito.quarkus.addons.common.deployment.KogitoCapability;
import org.kie.kogito.quarkus.addons.common.deployment.RequireCapabilityKogitoAddOnProcessor;
import org.kie.kogito.quarkus.common.deployment.KogitoBuildContextBuildItem;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.vertx.http.deployment.spi.AdditionalStaticResourceBuildItem;

class KogitoAddOnSourceFilesProcessor extends RequireCapabilityKogitoAddOnProcessor {

    private static final String FEATURE = "kogito-addon-source-files-extension";

    private static final Collection<String> SOURCE_FILE_EXTENSIONS = List.of(".bpmn", ".bpmn2", ".json", ".yaml", ".yml");

    KogitoAddOnSourceFilesProcessor() {
        super(KogitoCapability.PROCESSES);
    }

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem sourceFilesProviderProducer() {
        return new AdditionalBeanBuildItem(SourceFilesProviderProducer.class);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void addSourceFileProcessBindListener(KogitoBuildContextBuildItem ctxBuildItem,
            SourceFilesRecorder sourceFilesRecorder) {
        KogitoBuildContext kogitoBuildContext = ctxBuildItem.getKogitoBuildContext();

        SourceFileProcessBindListenerImpl processListener = new SourceFileProcessBindListenerImpl(
                kogitoBuildContext.getAppPaths().getResourceFiles(),
                sourceFilesRecorder);

        SourceFileServerlessWorkflowBindListenerImpl serverlessWorkflowListener = new SourceFileServerlessWorkflowBindListenerImpl(
                kogitoBuildContext.getAppPaths().getResourceFiles(),
                sourceFilesRecorder);

        kogitoBuildContext.getSourceFileCodegenBindNotifier()
                .ifPresent(notifier -> notifier.addListeners(processListener, serverlessWorkflowListener));
    }

    @BuildStep
    void produceSourceFiles(
            KogitoBuildContextBuildItem ctxBuildItem,
            BuildProducer<GeneratedResourceBuildItem> generatedResourceProducer,
            BuildProducer<AdditionalStaticResourceBuildItem> additionalStaticResourceProducer,
            BuildProducer<NativeImageResourceBuildItem> nativeImageResourceProducer) {
        Path sourcesDestinationPath = ctxBuildItem.getKogitoBuildContext().getAppPaths().getOutputTarget().resolve(
                Path.of("classes/META-INF/resources" + SourceFile.SOURCES_HTTP_PATH));

        Collection<CollectedResource> collectedResources = CollectedResourceProducer.fromPaths(
                ctxBuildItem.getKogitoBuildContext().getAppPaths().getPaths());

        collectedResources.stream()
                .filter(this::isSourceFile)
                .forEach(resource -> generateStaticResource(
                        sourcesDestinationPath,
                        generatedResourceProducer,
                        additionalStaticResourceProducer,
                        nativeImageResourceProducer,
                        resource));
    }

    private static void generateStaticResource(
            Path sourcesDestinationPath,
            BuildProducer<GeneratedResourceBuildItem> generatedResourceProducer,
            BuildProducer<AdditionalStaticResourceBuildItem> additionalStaticResourceProducer,
            BuildProducer<NativeImageResourceBuildItem> nativeImageResourceProducer,
            CollectedResource resource) {
        Path sourceFile = Paths.get(resource.resource().getSourcePath());

        Path relativeDestinationFilePath = sourceFile.startsWith(resource.basePath())
                ? sourceFile.subpath(resource.basePath().getNameCount(), sourceFile.getNameCount())
                : sourceFile;

        byte[] contents;

        try {
            contents = resource.resource().getInputStream().readAllBytes();
            Path absoluteDestinationFilePath = sourcesDestinationPath.resolve(relativeDestinationFilePath);
            createDirectories(absoluteDestinationFilePath.getParent());
            Files.write(absoluteDestinationFilePath, contents, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to move source file to static resources directory.", e);
        }

        String sourcesDestinationDirectory = "META-INF/resources" + SourceFile.SOURCES_HTTP_PATH;

        generatedResourceProducer.produce(new GeneratedResourceBuildItem(
                sourcesDestinationDirectory + relativeDestinationFilePath, contents, true));

        nativeImageResourceProducer.produce(new NativeImageResourceBuildItem(
                sourcesDestinationDirectory + relativeDestinationFilePath));

        additionalStaticResourceProducer.produce(new AdditionalStaticResourceBuildItem(
                SourceFile.SOURCES_HTTP_PATH + relativeDestinationFilePath, false));
    }

    private static void createDirectories(Path path) throws IOException {
        if (!Files.exists(path.getParent())) {
            createDirectories(path.getParent());
        }
        Files.createDirectories(path);
    }

    private boolean isSourceFile(CollectedResource resource) {
        return SOURCE_FILE_EXTENSIONS.stream().anyMatch(ext -> resource.resource().getSourcePath().endsWith(ext));
    }
}
