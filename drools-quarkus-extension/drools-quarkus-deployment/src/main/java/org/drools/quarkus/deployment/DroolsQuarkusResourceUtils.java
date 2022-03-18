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
package org.drools.quarkus.deployment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.maven.dependency.Dependency;
import io.quarkus.maven.dependency.ResolvedDependency;
import io.quarkus.vertx.http.deployment.spi.AdditionalStaticResourceBuildItem;
import org.drools.model.project.codegen.GeneratedFile;
import org.drools.model.project.codegen.GeneratedFileType;
import org.drools.model.project.codegen.context.AppPaths;
import org.drools.model.project.codegen.context.DroolsModelBuildContext;
import org.drools.model.project.codegen.context.impl.QuarkusDroolsModelBuildContext;
import org.drools.model.project.codegen.io.GeneratedFileWriter;
import org.jboss.jandex.CompositeIndex;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.kie.memorycompiler.resources.KiePath;
import org.kie.memorycompiler.resources.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;

/**
 * Utility class to aggregate and share resource handling in Kogito extensions
 */
public class DroolsQuarkusResourceUtils {

    static final String HOT_RELOAD_SUPPORT_PACKAGE = "org.kie.kogito.app";
    static final String HOT_RELOAD_SUPPORT_CLASS = "HotReloadSupportClass";
    static final String HOT_RELOAD_SUPPORT_FQN = HOT_RELOAD_SUPPORT_PACKAGE + "." + HOT_RELOAD_SUPPORT_CLASS;
    static final String HOT_RELOAD_SUPPORT_PATH = HOT_RELOAD_SUPPORT_FQN.replace('.', '/');

    private DroolsQuarkusResourceUtils() {
        // utility class
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsQuarkusResourceUtils.class);

    // since quarkus-maven-plugin is later phase of maven-resources-plugin,
    // need to manually late-provide the resource in the expected location for quarkus:dev phase --so not: writeGeneratedFile( f, resourcePath );
    private static final GeneratedFileWriter.Builder generatedFileWriterBuilder =
            new GeneratedFileWriter.Builder(
                    "target/classes",
                    System.getProperty("kogito.codegen.sources.directory", "target/generated-sources/kogito/"),
                    System.getProperty("kogito.codegen.resources.directory", "target/generated-resources/kogito/"),
                    "target/generated-sources/kogito/");

    public static DroolsModelBuildContext createBuildContext(Path outputTarget, Iterable<Path> paths, IndexView index, Dependency appArtifact) {
        // scan and parse paths
        AppPaths.BuildTool buildTool;
        if (System.getProperty("org.gradle.appname") == null) {
            buildTool = AppPaths.BuildTool.MAVEN;
        } else {
            buildTool = AppPaths.BuildTool.GRADLE;
        }
        AppPaths appPaths = AppPaths.fromQuarkus(outputTarget, paths, buildTool);
//        ClassLoader classLoader = DroolsAssetsProcessor.class.getClassLoader(); // Thread.currentThread().getContextClassLoader();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        DroolsModelBuildContext context = QuarkusDroolsModelBuildContext.builder()
//                .withApplicationPropertyProvider(new KogitoQuarkusApplicationPropertiesProvider())
                .withClassLoader(classLoader)
                .withClassAvailabilityResolver(className -> classAvailabilityResolver(classLoader, index, className))
                .withAppPaths(appPaths)
                .build();
/*
        if (!context.hasClassAvailable(QuarkusKogitoBuildContext.QUARKUS_REST)) {
            LOGGER.info("Disabling REST generation because class '" + QuarkusKogitoBuildContext.QUARKUS_REST + "' is not available");
            context.setApplicationProperty(KogitoBuildContext.KOGITO_GENERATE_REST, "false");
        }
        if (!context.hasClassAvailable(QuarkusKogitoBuildContext.QUARKUS_DI)) {
            LOGGER.info("Disabling dependency injection generation because class '" + QuarkusKogitoBuildContext.QUARKUS_DI + "' is not available");
            context.setApplicationProperty(KogitoBuildContext.KOGITO_GENERATE_DI, "false");
        }
*/
        return context;
    }

    /**
     * Verify if a class is available. First uses jandex indexes, then fallback on classLoader
     *
     * @param classLoader
     * @param className
     * @return
     */
    private static boolean classAvailabilityResolver(ClassLoader classLoader, IndexView index, String className) {
        if (index != null) {
            DotName classDotName = DotName.createSimple(className);
            boolean classFound = !index.getAnnotations(classDotName).isEmpty() ||
                    index.getClassByName(classDotName) != null;
            if (classFound) {
                return true;
            }
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
            BuildProducer<AdditionalStaticResourceBuildItem> staticResProducer,
            BuildProducer<NativeImageResourceBuildItem> resource,
            BuildProducer<GeneratedResourceBuildItem> genResBI) {
        for (GeneratedFile f : generatedFiles) {
            if (f.category() == GeneratedFileType.Category.INTERNAL_RESOURCE || f.category() == GeneratedFileType.Category.STATIC_HTTP_RESOURCE) {
                genResBI.produce(new GeneratedResourceBuildItem(f.relativePath(), f.contents(), true));
                resource.produce(new NativeImageResourceBuildItem(f.relativePath()));
            }
            if (f.category() == GeneratedFileType.Category.STATIC_HTTP_RESOURCE) {
                String resoucePath = f.relativePath().substring(GeneratedFile.META_INF_RESOURCES.length() - 1); // keep '/' at the beginning
                staticResProducer.produce(new AdditionalStaticResourceBuildItem(resoucePath, false));
            }
        }
    }

    public static Collection<GeneratedBeanBuildItem> compileGeneratedSources(
            DroolsModelBuildContext context,
            Collection<ResolvedDependency> dependencies,
            Collection<GeneratedFile> generatedFiles,
            boolean useDebugSymbols) throws IOException {
        Collection<GeneratedFile> javaFiles =
                generatedFiles.stream()
                        .filter(f -> f.category() == GeneratedFileType.Category.SOURCE)
                        .collect(toList());

        if (javaFiles.isEmpty()) {
            LOGGER.info("No Java source to compile");
            return Collections.emptyList();
        }

        InMemoryCompiler inMemoryCompiler =
                new InMemoryCompiler(
                        context.getAppPaths().getClassesPaths(),
                        dependencies,
                        useDebugSymbols);
        inMemoryCompiler.compile(javaFiles);
        return makeBuildItems(
                context.getAppPaths(),
                inMemoryCompiler.getTargetFileSystem());
    }

    public static IndexView generateAggregatedIndex(IndexView baseIndex, List<DroolsGeneratedClassesBuildItem> generatedKogitoClasses) {
        List<IndexView> indexes = new ArrayList<>();
        indexes.add(baseIndex);

        indexes.addAll(generatedKogitoClasses.stream()
                .map(DroolsGeneratedClassesBuildItem::getIndexedClasses)
                .collect(Collectors.toList()));
        return CompositeIndex.create(indexes.toArray(new IndexView[0]));
    }

    public static Path getTargetClassesPath(AppPaths appPaths) {
        return generatedFileWriterBuilder.build(appPaths.getFirstProjectPath()).getClassesDir();
    }

    private static Collection<GeneratedBeanBuildItem> makeBuildItems(AppPaths appPaths, ResourceReader resources) throws IOException {

        Collection<GeneratedBeanBuildItem> buildItems = new ArrayList<>();
        for (KiePath path : resources.getFilePaths()) {
            byte[] data = resources.getBytes(path);
            String className = toClassName(path.asString());

            // Write the bytecode of the class retriggering the hot reload in the file system
            // This is necessary to workaround the problem fixed by https://github.com/quarkusio/quarkus/pull/15726 and
            // TODO this can be removed when we will use a version of quarkus having that fix
            if (className.equals(HOT_RELOAD_SUPPORT_FQN)) {
                for (Path classPath : appPaths.getClassesPaths()) {
                    // Write the class bytecode in the first available directory class path if any
                    if (classPath.toFile().isDirectory()) {
                        Files.write(pathOf(classPath.toString(), HOT_RELOAD_SUPPORT_PATH + ".class"), data);
                        break;
                    }
                }
            }

            buildItems.add(new GeneratedBeanBuildItem(className, data));
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
        return sourceName.replace('/', '.').replace('\\', '.');
    }

    private static Path pathOf(String location, String end) {
        Path path = Paths.get(location, end);
        path.getParent().toFile().mkdirs();
        return path;
    }

    static String getHotReloadSupportSource() {
        return "package " + HOT_RELOAD_SUPPORT_PACKAGE + ";\n" +
                "@io.quarkus.runtime.Startup()\n" +
                "public class " + HOT_RELOAD_SUPPORT_CLASS + " {\n" +
                "private static final String ID = \"" + UUID.randomUUID().toString() + "\";\n" +
                "}";
    }
}
