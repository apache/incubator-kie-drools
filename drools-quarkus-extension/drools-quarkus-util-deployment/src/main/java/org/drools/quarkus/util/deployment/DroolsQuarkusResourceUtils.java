/**
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
package org.drools.quarkus.util.deployment;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.maven.dependency.ResolvedDependency;
import io.quarkus.vertx.http.deployment.spi.AdditionalStaticResourceBuildItem;
import org.drools.base.util.Drools;
import org.drools.codegen.common.AppPaths;
import org.drools.codegen.common.DroolsModelBuildContext;
import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.codegen.common.GeneratedFileWriter;
import org.drools.codegen.common.context.QuarkusDroolsModelBuildContext;
import org.drools.wiring.api.ComponentsSupplier;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.kie.api.internal.utils.KieService;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.memorycompiler.KieMemoryCompiler.compileNoLoad;

/**
 * Utility class to aggregate and share resource handling in Drools/Kogito extensions
 */
public class DroolsQuarkusResourceUtils {

    static final String HOT_RELOAD_SUPPORT_PACKAGE = "org.kie.kogito.app";
    static final String HOT_RELOAD_SUPPORT_CLASS = "HotReloadSupportClass";
    static final String HOT_RELOAD_SUPPORT_FQN = HOT_RELOAD_SUPPORT_PACKAGE + "." + HOT_RELOAD_SUPPORT_CLASS;
    public static final String HOT_RELOAD_SUPPORT_PATH = HOT_RELOAD_SUPPORT_FQN.replace('.', '/');

    private DroolsQuarkusResourceUtils() {
        // utility class
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsQuarkusResourceUtils.class);

    static {
        if (Drools.isNativeImage() && !KieService.load(ComponentsSupplier.class).getClass().getSimpleName().equals("StaticComponentsSupplier")) {
            throw new IllegalStateException("Cannot run quarkus extension in native mode with module org.drools:drools-wiring-dynamic. Please remove it from your classpath.");
        }
    }

    // since quarkus-maven-plugin is later phase of maven-resources-plugin,
    // need to manually late-provide the resource in the expected location for quarkus:dev phase --so not: writeGeneratedFile( f, resourcePath );
    private static final GeneratedFileWriter.Builder generatedFileWriterBuilder = GeneratedFileWriter.builder("drools"
            , "drools.codegen.resources.directory", "drools.codegen.sources.directory");


    public static DroolsModelBuildContext createDroolsBuildContext(Iterable<Path> paths, IndexView index) {
        // scan and parse paths
        AppPaths appPaths = QuarkusAppPaths.from(paths);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        DroolsModelBuildContext context = QuarkusDroolsModelBuildContext.builder()
                .withClassLoader(classLoader)
                .withApplicationProperties(appPaths.getResourceFiles())
                .withClassAvailabilityResolver(className -> classAvailabilityResolver(index, className))
                .withAppPaths(appPaths)
                .build();

        return context;
    }

    /**
     * Verify if a class is available. First uses jandex indexes, then fallback on classLoader
     */
    private static boolean classAvailabilityResolver(IndexView index, String className) {
        if (index != null) {
            DotName classDotName = DotName.createSimple(className);
            boolean classFound = !index.getAnnotations(classDotName).isEmpty() ||
                    index.getClassByName(classDotName) != null;
            if (classFound) {
                return true;
            }
        }

        return QuarkusClassLoader.isClassPresentAtRuntime(className);
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

    public static Collection<GeneratedBeanBuildItem> compileGeneratedSources(DroolsModelBuildContext context, Collection<ResolvedDependency> dependencies,
            Collection<GeneratedFile> generatedFiles, boolean useDebugSymbols) {
        Map<String, String> sourcesMap = getSourceMap(generatedFiles);
        if (sourcesMap.isEmpty()) {
            LOGGER.info("No Java source to compile");
            return Collections.emptyList();
        }

        JavaCompilerSettings compilerSettings = createJavaCompilerSettings(context, dependencies, useDebugSymbols);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return makeBuildItems(compileNoLoad(sourcesMap, classLoader, compilerSettings));
    }

    private static JavaCompilerSettings createJavaCompilerSettings(DroolsModelBuildContext context, Collection<ResolvedDependency> dependencies, boolean useDebugSymbols) {
        JavaCompilerSettings compilerSettings = JavaCompiler.getCompiler().createDefaultSettings();
        compilerSettings.addOption("-proc:none"); // force disable annotation processing
        if (useDebugSymbols) {
            compilerSettings.addOption("-g");
            compilerSettings.addOption("-parameters");
        }
        for (Path classPath : context.getAppPaths().getClassesPaths()) {
            compilerSettings.addClasspath(classPath.toFile());
        }
        for (ResolvedDependency i : dependencies) {
            if (i.isResolved()) {
                compilerSettings.addClasspath(i.getResolvedPaths().getSinglePath().toFile());
            } else {
                LOGGER.info("Ignoring non-resolved dependency {}", i.getKey().toGacString());
            }
        }
        return compilerSettings;
    }

    private static Map<String, String> getSourceMap(Collection<GeneratedFile> generatedFiles) {
        Map<String, String> sourcesMap = new HashMap<>();
        for (GeneratedFile javaFile : generatedFiles) {
            if (javaFile.category() == GeneratedFileType.Category.SOURCE) {
                sourcesMap.put(toClassName(javaFile.relativePath()), new String(javaFile.contents(), StandardCharsets.UTF_8));
            }
        }
        return sourcesMap;
    }

    public static Collection<GeneratedBeanBuildItem> makeBuildItems(Map<String, byte[]> byteCodeMap) {
        Collection<GeneratedBeanBuildItem> buildItems = new ArrayList<>();
        for (Map.Entry<String, byte[]> byteCode : byteCodeMap.entrySet()) {
            buildItems.add(new GeneratedBeanBuildItem(byteCode.getKey(), byteCode.getValue()));
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

    public static String getHotReloadSupportSource() {
        return "package " + HOT_RELOAD_SUPPORT_PACKAGE + ";\n" +
                "@io.quarkus.runtime.Startup()\n" +
                "public class " + HOT_RELOAD_SUPPORT_CLASS + " {\n" +
                "private static final String ID = \"" + UUID.randomUUID() + "\";\n" +
                "}";
    }

    private static final DotName RULE_UNIT_DEF_INTERFACE = DotName.createSimple("org.drools.ruleunits.dsl.RuleUnitDefinition");

    public static List<GeneratedFile> getRuleUnitDefProducerSource(IndexView indexView) {
        return indexView.getAllKnownImplementors(RULE_UNIT_DEF_INTERFACE).stream()
                .map(ClassInfo::name)
                .map(DroolsQuarkusResourceUtils::generateRuleUnitDefProducerSource)
                .collect(Collectors.toList());
    }

    private static final String RULE_UNIT_DEF_PRODUCER =
            "import jakarta.enterprise.context.Dependent;\n" +
                    "import jakarta.enterprise.inject.Produces;\n" +
                    "\n" +
                    "import org.drools.ruleunits.api.RuleUnit;\n" +
                    "import org.drools.ruleunits.api.RuleUnitProvider;\n" +
                    "\n" +
                    "@Dependent\n" +
                    "public class $RULE_UNIT_NAME$Producer {\n" +
                    "\n" +
                    "    @Produces\n" +
                    "    public RuleUnit<$RULE_UNIT_NAME$> produceRuleUnit() {\n" +
                    "        return RuleUnitProvider.get().getRuleUnit(new $RULE_UNIT_NAME$());\n" +
                    "    }\n" +
                    "}\n";

    private static GeneratedFile generateRuleUnitDefProducerSource(DotName ruleUnitDefName) {
        String source = "package " + ruleUnitDefName.packagePrefix() + ";\n\n" +
                RULE_UNIT_DEF_PRODUCER.replaceAll("\\$RULE_UNIT_NAME\\$", ruleUnitDefName.withoutPackagePrefix());
        return new GeneratedFile(GeneratedFileType.SOURCE, ruleUnitDefName.toString().replace('.', '/') + "Producer.java", source);
    }
}
