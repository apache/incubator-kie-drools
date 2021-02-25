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
package org.kie.kogito.quarkus.common.deployment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.jandex.CompositeIndex;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.GeneratedFileType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.utils.AppPaths;
import org.kie.kogito.codegen.core.utils.GeneratedFileWriter;
import org.kie.memorycompiler.resources.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.bootstrap.model.AppDependency;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;

import static java.util.stream.Collectors.toList;

/**
 * Utility class to aggregate and share resource handling in Kogito extensions
 */
public class KogitoQuarkusResourceUtils {

    private KogitoQuarkusResourceUtils() {
        // utility class
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoQuarkusResourceUtils.class);

    // since quarkus-maven-plugin is later phase of maven-resources-plugin,
    // need to manually late-provide the resource in the expected location for quarkus:dev phase --so not: writeGeneratedFile( f, resourcePath );
    private static final GeneratedFileWriter.Builder generatedFileWriterBuilder =
            new GeneratedFileWriter.Builder(
                    "target/classes",
                    System.getProperty("kogito.codegen.sources.directory", "target/generated-sources/kogito/"),
                    System.getProperty("kogito.codegen.resources.directory", "target/generated-resources/kogito/"),
                    "target/generated-sources/kogito/");

    public static KogitoBuildContext kogitoBuildContext(Iterable<Path> paths, IndexView index) {
        // scan and parse paths
        AppPaths appPaths = AppPaths.fromQuarkus(paths);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return KogitoQuarkusContextProvider.context(appPaths, classLoader, className -> classAvailabilityResolver(classLoader, index, className));
    }

    /**
     * Verify if a class is available. First uses jandex indexes, then fallback on classLoader
     *
     * @param classLoader
     * @param className
     * @return
     */
    private static boolean classAvailabilityResolver(ClassLoader classLoader, IndexView index, String className) {
        DotName classDotName = DotName.createSimple(className);
        boolean classFound = !index.getAnnotations(classDotName).isEmpty() ||
                index.getClassByName(classDotName) != null;
        if (classFound) {
            return true;
        }
        try {
            classLoader.loadClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void dumpFilesToDisk(AppPaths appPaths, Collection<GeneratedFile> generatedFiles) {
        generatedFileWriterBuilder
                .build(appPaths.getFirstProjectPath())
                .writeAll(generatedFiles);
    }

    public static void registerResources(Collection<GeneratedFile> generatedFiles,
            BuildProducer<NativeImageResourceBuildItem> resource,
            BuildProducer<GeneratedResourceBuildItem> genResBI) {
        for (GeneratedFile f : generatedFiles) {
            if (f.category() == GeneratedFileType.Category.RESOURCE) {
                genResBI.produce(new GeneratedResourceBuildItem(f.relativePath(), f.contents()));
                resource.produce(new NativeImageResourceBuildItem(f.relativePath()));
            }
        }
    }

    public static Collection<GeneratedBeanBuildItem> compileGeneratedSources(KogitoBuildContext context, List<AppDependency> dependencies, Collection<GeneratedFile> generatedFiles)
            throws IOException {
        Collection<GeneratedFile> javaFiles =
                generatedFiles.stream()
                        .filter(f -> f.category() == GeneratedFileType.Category.SOURCE)
                        .collect(toList());

        if (javaFiles.isEmpty()) {
            LOGGER.info("No Java source to compile");
            return Collections.emptyList();
        }

        InMemoryCompiler inMemoryCompiler = new InMemoryCompiler(context.getAppPaths().getClassesPaths(),
                dependencies);
        inMemoryCompiler.compile(javaFiles);
        return makeBuildItems(
                context.getAppPaths(),
                inMemoryCompiler.getTargetFileSystem());
    }

    public static IndexView generateAggregatedIndex(IndexView baseIndex, List<KogitoGeneratedClassesBuildItem> generatedKogitoClasses) {
        List<IndexView> indexes = new ArrayList<>();
        indexes.add(baseIndex);

        indexes.addAll(generatedKogitoClasses.stream()
                .map(KogitoGeneratedClassesBuildItem::getIndexedClasses)
                .collect(Collectors.toList()));

        return CompositeIndex.create(indexes.toArray(new IndexView[0]));
    }

    public static Path getTargetClassesPath(AppPaths appPaths) {
        return generatedFileWriterBuilder.build(appPaths.getFirstProjectPath()).getClassesDir();
    }

    private static Collection<GeneratedBeanBuildItem> makeBuildItems(AppPaths appPaths, ResourceReader resources) throws IOException {

        Collection<GeneratedBeanBuildItem> buildItems = new ArrayList<>();
        Path location = generatedFileWriterBuilder.build(appPaths.getFirstProjectPath()).getClassesDir();
        ;
        for (String fileName : resources.getFileNames()) {
            byte[] data = resources.getBytes(fileName);
            String className = toClassName(fileName);
            buildItems.add(new GeneratedBeanBuildItem(className, data));

            Path path = pathOf(location.toString(), fileName);
            Files.write(path, data);

            String sourceFile = location.toString().replaceFirst("\\.class", ".java");
            if (sourceFile.contains("$")) {
                sourceFile = sourceFile.substring(0, sourceFile.indexOf("$")) + ".java";
            }
            KogitoCompilationProvider.classToSource.put(path, Paths.get(sourceFile));
        }

        return buildItems;
    }

    public static String toClassName(String sourceName) {
        if (sourceName.startsWith("./")) {
            sourceName = sourceName.substring(2);
        }
        if (sourceName.endsWith(".java")) {
            sourceName = sourceName.substring(0, sourceName.length() - 5);
        } else if (sourceName.endsWith(".class")) {
            sourceName = sourceName.substring(0, sourceName.length() - 6);
        }
        return sourceName.replace('/', '.');
    }

    private static Path pathOf(String location, String end) {
        Path path = Paths.get(location, end);
        path.getParent().toFile().mkdirs();
        return path;
    }
}
