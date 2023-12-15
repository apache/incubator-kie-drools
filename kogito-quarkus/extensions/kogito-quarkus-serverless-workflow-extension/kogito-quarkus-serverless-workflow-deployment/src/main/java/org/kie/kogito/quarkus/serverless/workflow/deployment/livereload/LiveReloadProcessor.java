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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.quarkus.util.deployment.DroolsQuarkusResourceUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.quarkus.common.deployment.KogitoAddonsPreGeneratedSourcesBuildItem;
import org.kie.kogito.quarkus.common.deployment.KogitoBuildContextBuildItem;
import org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils;
import org.kie.kogito.quarkus.common.deployment.LiveReloadExecutionBuildItem;
import org.kie.kogito.quarkus.serverless.workflow.config.LiveReloadConfigBuilder;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.bootstrap.classloading.MemoryClassPathElement;
import io.quarkus.bootstrap.classloading.PathTreeClassPathElement;
import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.bootstrap.model.ApplicationModel;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigBuilderBuildItem;
import io.quarkus.deployment.index.IndexingUtil;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.deployment.pkg.builditem.OutputTargetBuildItem;
import io.quarkus.paths.PathTree;
import io.quarkus.runtime.configuration.ConfigUtils;
import io.quarkus.runtime.configuration.QuarkusConfigFactory;

import jakarta.inject.Inject;

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

    private final QuarkusClassLoader.Builder classLoader;

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
        classLoader = QuarkusClassLoader.builder("liveReload", kogitoBuildContext.getClassLoader(), false);
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    public LiveReloadExecutionBuildItem liveReload(BuildProducer<KogitoAddonsPreGeneratedSourcesBuildItem> sourcesProducer, BuildProducer<GeneratedResourceBuildItem> genResBI,
            BuildProducer<RunTimeConfigBuilderBuildItem> configBuilder) {
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
        configBuilder.produce(new RunTimeConfigBuilderBuildItem(LiveReloadConfigBuilder.class.getCanonicalName()));
        if (!generatedFiles.isEmpty()) {
            sourcesProducer.produce(new KogitoAddonsPreGeneratedSourcesBuildItem(generatedFiles));
            skipNextLiveReload();
            ClassLoader reloadedClassLoader = classLoader.build();
            QuarkusConfigFactory.setConfig(ConfigUtils.emptyConfigBuilder().addDefaultSources().addDiscoveredSources().forClassLoader(reloadedClassLoader).build());
            return new LiveReloadExecutionBuildItem(KogitoQuarkusResourceUtils.generateAggregatedIndexNew(computingIndex, indexViews), reloadedClassLoader);
        } else {
            dontSkipNextLiveReload();
            return new LiveReloadExecutionBuildItem(computingIndex);
        }
    }

    private CodeGenerationResult generateCode(LiveReloadableCodeGenProvider codeGenProvider) {
        try {
            Collection<GeneratedFile> generatedFiles = new ArrayList<>(generateSources(codeGenProvider));
            Collection<GeneratedBeanBuildItem> generatedBeans = compileGeneratedSources(generatedFiles);
            if (!generatedBeans.isEmpty()) {
                classLoader.addElement(new MemoryClassPathElement(
                        generatedBeans.stream().collect(Collectors.toMap(x -> x.getName().replace('.', '/').concat(".class"), GeneratedBeanBuildItem::getData)), true));
            }
            return !generatedFiles.isEmpty() ? new CodeGenerationResult(generatedFiles, indexCompiledSources(generatedBeans))
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
                    sources.filter(Files::isRegularFile).forEach(p -> processSource(p, outDir, generatedFiles));
                }
                Path classPath = outDir.getParent().getParent().resolve("classes");
                Path serviceLoaderPath = classPath.resolve("META-INF/services");
                if (Files.isDirectory(classPath) && Files.isDirectory(serviceLoaderPath)) {
                    classLoader.addElement(new PathTreeClassPathElement(PathTree.ofDirectoryOrFile(classPath), true));
                }
            }
        }
        return generatedFiles;
    }

    private void processSource(Path path, Path outDir, Collection<GeneratedFile> generatedFiles) {
        if (path.toString().endsWith(".java")) {
            try {
                generatedFiles.add(new GeneratedFile(GeneratedFileType.SOURCE, outDir.relativize(path), Files.readAllBytes(path)));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
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
