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
package org.kie.kogito.quarkus.common.deployment;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.drools.codegen.common.AppPaths;
import org.drools.codegen.common.DroolsModelBuildContext;
import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.codegen.common.GeneratedFileWriter;
import org.drools.quarkus.util.deployment.QuarkusAppPaths;
import org.drools.util.PortablePath;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.jandex.CompositeIndex;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.codegen.api.SourceFileCodegenBindNotifier;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;
import org.kie.memorycompiler.resources.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.maven.dependency.Dependency;
import io.quarkus.vertx.http.deployment.spi.AdditionalStaticResourceBuildItem;

/**
 * Utility class to aggregate and share resource handling in Kogito extensions
 */
public class KogitoQuarkusResourceUtils {

    static final String HOT_RELOAD_SUPPORT_PACKAGE = "org.kie.kogito.app";
    static final String HOT_RELOAD_SUPPORT_CLASS = "HotReloadSupportClass";
    static final String HOT_RELOAD_SUPPORT_FQN = HOT_RELOAD_SUPPORT_PACKAGE + "." + HOT_RELOAD_SUPPORT_CLASS;
    static final String HOT_RELOAD_SUPPORT_PATH = HOT_RELOAD_SUPPORT_FQN.replace('.', '/');

    private static boolean shouldDumpFiles = ConfigProvider.getConfig().getOptionalValue("kogito.quarkus.codegen.dumpFiles", Boolean.class).orElse(true);

    private KogitoQuarkusResourceUtils() {
        // utility class
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoQuarkusResourceUtils.class);

    // since quarkus-maven-plugin is later phase of maven-resources-plugin,
    // need to manually late-provide the resource in the expected location for quarkus:dev phase --so not: writeGeneratedFile( f, resourcePath )
    private static final GeneratedFileWriter.Builder generatedFileWriterBuilder = GeneratedFileWriter.builder("kogito", "kogito.codegen.resources.directory", "kogito.codegen.sources.directory");

    public static KogitoBuildContext kogitoBuildContext(Iterable<Path> paths, IndexView index, Dependency appArtifact) {
        // scan and parse paths
        AppPaths appPaths = QuarkusAppPaths.from(paths);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        KogitoBuildContext context = QuarkusKogitoBuildContext.builder()
                .withApplicationPropertyProvider(new KogitoQuarkusApplicationPropertiesProvider())
                .withClassLoader(classLoader)
                .withClassAvailabilityResolver(className -> classAvailabilityResolver(classLoader, index, className))
                .withClassSubTypeAvailabilityResolver(classSubTypeAvailabilityResolver(index))
                .withAppPaths(appPaths)
                .withGAV(new KogitoGAV(appArtifact.getGroupId(), appArtifact.getArtifactId(), appArtifact.getVersion()))
                .withSourceFileProcessBindNotifier(new SourceFileCodegenBindNotifier())
                .build();

        if (!context.hasClassAvailable(QuarkusKogitoBuildContext.QUARKUS_REST)) {
            LOGGER.info("Disabling REST generation because class '" + QuarkusKogitoBuildContext.QUARKUS_REST + "' is not available");
            context.setApplicationProperty(DroolsModelBuildContext.KOGITO_GENERATE_REST, "false");
        }
        if (!context.hasClassAvailable(QuarkusKogitoBuildContext.QUARKUS_DI)) {
            LOGGER.info("Disabling dependency injection generation because class '" + QuarkusKogitoBuildContext.QUARKUS_DI + "' is not available");
            context.setApplicationProperty(DroolsModelBuildContext.KOGITO_GENERATE_DI, "false");
        }
        return context;
    }

    private static Predicate<Class<?>> classSubTypeAvailabilityResolver(IndexView index) {
        return clazz -> index.getAllKnownImplementors(DotName.createSimple(clazz.getCanonicalName()))
                .stream()
                .anyMatch(c -> !Modifier.isInterface(c.flags()) && !Modifier.isAbstract(c.flags()));
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
        if (shouldDumpFiles) {
            generatedFileWriterBuilder
                    .build(appPaths.getFirstProjectPath())
                    .writeAll(generatedFiles);
        }
    }

    public static void registerResources(Collection<GeneratedFile> generatedFiles,
            BuildProducer<AdditionalStaticResourceBuildItem> staticResProducer,
            BuildProducer<NativeImageResourceBuildItem> resource,
            BuildProducer<GeneratedResourceBuildItem> genResBI) {
        for (GeneratedFile f : generatedFiles) {
            if (f.category() == GeneratedFileType.Category.INTERNAL_RESOURCE || f.category() == GeneratedFileType.Category.STATIC_HTTP_RESOURCE) {
                genResBI.produce(new GeneratedResourceBuildItem(f.relativePath(), f.contents(), shouldDumpFiles));
                resource.produce(new NativeImageResourceBuildItem(f.relativePath()));
            }
            if (f.category() == GeneratedFileType.Category.STATIC_HTTP_RESOURCE) {
                String resoucePath = f.relativePath().substring(GeneratedFile.META_INF_RESOURCES.length() - 1); // keep '/' at the beginning
                staticResProducer.produce(new AdditionalStaticResourceBuildItem(resoucePath, false));
            }
        }
    }

    public static IndexView generateAggregatedIndex(IndexView baseIndex, List<KogitoGeneratedClassesBuildItem> generatedKogitoClasses) {
        return generateAggregatedIndexNew(baseIndex, generatedKogitoClasses.stream()
                .map(KogitoGeneratedClassesBuildItem::getIndexedClasses)
                .collect(Collectors.toList()));
    }

    public static IndexView generateAggregatedIndexNew(IndexView baseIndex, List<IndexView> newIndexViews) {
        List<IndexView> indexes = new ArrayList<>();
        indexes.add(baseIndex);
        indexes.addAll(newIndexViews);
        return CompositeIndex.create(indexes);
    }

    public static Path getTargetClassesPath(AppPaths appPaths) {
        return generatedFileWriterBuilder.build(appPaths.getFirstProjectPath()).getClassesDir();
    }

    private static Collection<GeneratedBeanBuildItem> makeBuildItems(AppPaths appPaths, ResourceReader resources) throws IOException {

        Collection<GeneratedBeanBuildItem> buildItems = new ArrayList<>();
        for (PortablePath path : resources.getFilePaths()) {
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
