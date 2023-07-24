/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.quarkus.serverless.workflow.deployment.livereload;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.drl.quarkus.util.deployment.DroolsQuarkusResourceUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.quarkus.common.deployment.KogitoAddonsPreGeneratedSourcesBuildItem;
import org.kie.kogito.quarkus.common.deployment.KogitoBuildContextBuildItem;
import org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils;
import org.kie.kogito.quarkus.common.deployment.LiveReloadExecutionBuildItem;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.bootstrap.model.ApplicationModel;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.index.IndexingUtil;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.deployment.pkg.builditem.OutputTargetBuildItem;

/**
 * This class adds live reload support for {@link io.quarkus.deployment.CodeGenProvider} objects.
 */
public class LiveReloadProcessor {

    private final LiveReloadBuildItem liveReloadBuildItem;

    private final ApplicationModel applicationModel;

    private final Path workDir;

    private final IndexView computingIndex;

    private final IndexView index;

    private final KogitoBuildContext kogitoBuildContext;

    @Inject
    public LiveReloadProcessor(
            CombinedIndexBuildItem combinedIndexBuildItem,
            LiveReloadBuildItem liveReloadBuildItem,
            CurateOutcomeBuildItem curateOutcomeBuildItem,
            OutputTargetBuildItem outputTargetBuildItem,
            KogitoBuildContextBuildItem contextBuildItem) {
        this.liveReloadBuildItem = liveReloadBuildItem;
        applicationModel = curateOutcomeBuildItem.getApplicationModel();
        workDir = outputTargetBuildItem.getOutputDirectory();
        computingIndex = combinedIndexBuildItem.getComputingIndex();
        index = combinedIndexBuildItem.getIndex();
        kogitoBuildContext = contextBuildItem.getKogitoBuildContext();
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    public LiveReloadExecutionBuildItem liveReload(BuildProducer<KogitoAddonsPreGeneratedSourcesBuildItem> sourcesProducer) {
        Collection<GeneratedFile> generatedFiles = new ArrayList<>();
        List<IndexView> indexViews = new ArrayList<>();
        if (liveReloadBuildItem.isLiveReload()) {
            if (shouldSkipLiveReload()) {
                dontSkipNextLiveReload();
            } else {
                ServiceLoader.load(LiveReloadableCodeGenProvider.class).stream()
                        .map(ServiceLoader.Provider::get)
                        .map(this::generateCode)
                        .forEach(codeGenerationResult -> {
                            generatedFiles.addAll(codeGenerationResult.getGeneratedFiles());
                            indexViews.add(codeGenerationResult.getIndexView());
                        });
            }
        }
        if (!generatedFiles.isEmpty()) {
            sourcesProducer.produce(new KogitoAddonsPreGeneratedSourcesBuildItem(generatedFiles));
            skipNextLiveReload();
            return new LiveReloadExecutionBuildItem(KogitoQuarkusResourceUtils.generateAggregatedIndexNew(computingIndex, indexViews));
        } else {
            dontSkipNextLiveReload();
            return new LiveReloadExecutionBuildItem(computingIndex);
        }
    }

    private CodeGenerationResult generateCode(LiveReloadableCodeGenProvider codeGenProvider) {
        try {
            Collection<GeneratedFile> generatedFiles = new ArrayList<>(generateSources(codeGenProvider));
            return !generatedFiles.isEmpty() ? new CodeGenerationResult(generatedFiles, indexCompiledSources(compileGeneratedSources(generatedFiles)))
                    : new CodeGenerationResult(List.of(), computingIndex);
        } catch (CodeGenException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private IndexView indexCompiledSources(Collection<GeneratedBeanBuildItem> generatedBeanBuildItems) {
        Indexer kogitoIndexer = new Indexer();

        for (GeneratedBeanBuildItem generatedBeanBuildItem : generatedBeanBuildItems) {
            IndexingUtil.indexClass(
                    generatedBeanBuildItem.getName(),
                    kogitoIndexer,
                    index,
                    new HashSet<>(),
                    kogitoBuildContext.getClassLoader(),
                    generatedBeanBuildItem.getData());
        }

        return kogitoIndexer.complete();
    }

    private Collection<GeneratedBeanBuildItem> compileGeneratedSources(Collection<GeneratedFile> sources) {
        return DroolsQuarkusResourceUtils.compileGeneratedSources(
                kogitoBuildContext,
                applicationModel.getRuntimeDependencies(),
                sources,
                true);
    }

    private Collection<GeneratedFile> generateSources(LiveReloadableCodeGenProvider codeGenProvider)
            throws CodeGenException, IOException {
        Path outDir = workDir.resolve("generated-sources").resolve(codeGenProvider.providerId());
        Collection<GeneratedFile> generatedFiles = new ArrayList<>();
        Config config = ConfigProvider.getConfig();
        for (Path sourcePath : kogitoBuildContext.getAppPaths().getSourcePaths()) {
            Path inputDir = sourcePath.resolve("main").resolve(codeGenProvider.inputDirectory());
            CodeGenContext codeGenContext = new CodeGenContext(applicationModel, outDir, workDir, inputDir, false, config, false);
            if (codeGenProvider.shouldRun(inputDir, config) && codeGenProvider.trigger(codeGenContext)) {
                try (Stream<Path> sources = Files.walk(outDir)) {
                    sources.filter(Files::isRegularFile)
                            .filter(path -> path.toString().endsWith(".java"))
                            .map(path -> {
                                try {
                                    return new GeneratedFile(GeneratedFileType.SOURCE, outDir.relativize(path), Files.readAllBytes(path));
                                } catch (IOException e) {
                                    throw new UncheckedIOException(e);
                                }
                            })
                            .forEach(generatedFiles::add);
                }
            }
        }

        return generatedFiles;
    }

    private void skipNextLiveReload() {
        liveReloadBuildItem.setContextObject(SkipLiveReload.class, SkipLiveReload.TRUE);
    }

    private void dontSkipNextLiveReload() {
        liveReloadBuildItem.setContextObject(SkipLiveReload.class, SkipLiveReload.FALSE);
    }

    private boolean shouldSkipLiveReload() {
        if (liveReloadBuildItem.getContextObject(SkipLiveReload.class) != null) {
            return liveReloadBuildItem.getContextObject(SkipLiveReload.class) == SkipLiveReload.TRUE;
        }
        return false;
    }

    @BuildStep(onlyIfNot = IsDevelopment.class)
    public LiveReloadExecutionBuildItem executeWhenNotDevelopment() {
        return new LiveReloadExecutionBuildItem(computingIndex);
    }

    private static class CodeGenerationResult {

        private final Collection<GeneratedFile> generatedFiles;

        private final IndexView indexView;

        CodeGenerationResult(Collection<GeneratedFile> generatedFiles, IndexView indexView) {
            this.generatedFiles = generatedFiles;
            this.indexView = indexView;
        }

        Collection<GeneratedFile> getGeneratedFiles() {
            return generatedFiles;
        }

        IndexView getIndexView() {
            return indexView;
        }
    }
}
