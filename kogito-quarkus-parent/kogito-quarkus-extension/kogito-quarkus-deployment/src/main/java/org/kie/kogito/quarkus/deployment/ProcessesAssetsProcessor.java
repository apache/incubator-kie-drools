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
package org.kie.kogito.quarkus.deployment;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.kie.kogito.Model;
import org.kie.kogito.UserTask;
import org.kie.kogito.codegen.Generated;
import org.kie.kogito.codegen.VariableInfo;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.GeneratedFileType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.json.JsonSchemaGenerator;
import org.kie.kogito.codegen.process.persistence.PersistenceGenerator;
import org.kie.kogito.quarkus.common.deployment.InMemoryClassLoader;
import org.kie.kogito.quarkus.common.deployment.KogitoGeneratedClassesBuildItem;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf;
import org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf;
import org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf;
import org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.bootstrap.model.AppDependency;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ArchiveRootBuildItem;
import io.quarkus.deployment.builditem.CapabilityBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigurationDefaultBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.kie.kogito.codegen.core.utils.GeneratedFileValidation.validateGeneratedFileTypes;
import static org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils.compileGeneratedSources;
import static org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils.dumpFilesToDisk;
import static org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils.generateAggregatedIndex;
import static org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils.getTargetClassesPath;
import static org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils.kogitoBuildContext;
import static org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils.registerResources;

/**
 * Main class of the Kogito processes extension
 */
public class ProcessesAssetsProcessor {

    private static final DotName persistenceFactoryClass = DotName.createSimple("org.kie.kogito.persistence.KogitoProcessInstancesFactory");
    private static final String PROCESS_SVG_SERVICE = "org.kie.kogito.svg.service.QuarkusProcessSvgService";

    private static final PathMatcher svgFileMatcher = FileSystems.getDefault().getPathMatcher("glob:**.svg");

    @Inject
    ArchiveRootBuildItem root;
    @Inject
    LiveReloadBuildItem liveReload;
    @Inject
    CurateOutcomeBuildItem curateOutcomeBuildItem;
    @Inject
    CombinedIndexBuildItem combinedIndexBuildItem;

    @BuildStep
    CapabilityBuildItem capability() {
        return new CapabilityBuildItem("kogito-processes");
    }

    @BuildStep
    FeatureBuildItem featureBuildItem() {
        return new FeatureBuildItem("kogito-processes");
    }

    /**
     * Main entry point of the Quarkus extension
     */
    @BuildStep
    public void postGenerationProcessing(
            List<KogitoGeneratedClassesBuildItem> generatedKogitoClasses,
            BuildProducer<GeneratedBeanBuildItem> generatedBeans,
            BuildProducer<NativeImageResourceBuildItem> resource,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            BuildProducer<GeneratedResourceBuildItem> genResBI,
            BuildProducer<RunTimeConfigurationDefaultBuildItem> runTimeConfiguration) throws IOException {

        // merge project index with classes generated by Kogito
        IndexView aggregatedIndex = generateAggregatedIndex(combinedIndexBuildItem.getIndex(), generatedKogitoClasses);

        // configure the application generator
        KogitoBuildContext context = kogitoBuildContext(root.getPaths(), aggregatedIndex, curateOutcomeBuildItem.getEffectiveModel().getAppArtifact());

        Collection<GeneratedFile> generatedFiles = generatePersistenceInfo(
                context,
                aggregatedIndex,
                generatedBeans,
                resource,
                reflectiveClass,
                runTimeConfiguration,
                liveReload.isLiveReload());

        Map<String, byte[]> classes = new HashMap<>();
        for (KogitoGeneratedClassesBuildItem generatedKogitoClass : generatedKogitoClasses) {
            classes.putAll(generatedKogitoClass.getGeneratedClasses());
        }

        // Json schema files
        generatedFiles.addAll(generateJsonSchema(context, aggregatedIndex, classes));

        // Write files to disk
        dumpFilesToDisk(context.getAppPaths(), generatedFiles);

        // register resources to the Quarkus environment
        registerResources(generatedFiles, resource, genResBI);

        registerProcessSVG(context, resource);
    }

    private Collection<GeneratedFile> generatePersistenceInfo(
            KogitoBuildContext context,
            IndexView index,
            BuildProducer<GeneratedBeanBuildItem> generatedBeans,
            BuildProducer<NativeImageResourceBuildItem> resource,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            BuildProducer<RunTimeConfigurationDefaultBuildItem> runTimeConfiguration,
            boolean useDebugSymbols) throws IOException {

        if (context.getAddonsConfig().usePersistence()) {
            resource.produce(new NativeImageResourceBuildItem("kogito-types.proto"));
        }

        Collection<GeneratedFile> persistenceGeneratedFiles = getGeneratedPersistenceFiles(index, context, reflectiveClass, runTimeConfiguration);

        validateGeneratedFileTypes(persistenceGeneratedFiles, asList(GeneratedFileType.Category.SOURCE, GeneratedFileType.Category.RESOURCE));

        List<AppDependency> dependencies = curateOutcomeBuildItem.getEffectiveModel().getUserDependencies();
        compileGeneratedSources(context, dependencies, persistenceGeneratedFiles, useDebugSymbols)
                .forEach(generatedBeans::produce);

        return persistenceGeneratedFiles.stream()
                .filter(x -> x.category().equals(GeneratedFileType.Category.RESOURCE))
                .collect(toList());
    }

    private Collection<GeneratedFile> getGeneratedPersistenceFiles(IndexView index,
            KogitoBuildContext context,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            BuildProducer<RunTimeConfigurationDefaultBuildItem> runTimeConfiguration) {
        ClassInfo persistenceClass = index
                .getClassByName(persistenceFactoryClass);

        Collection<ClassInfo> modelClasses = index
                .getAllKnownImplementors(DotName.createSimple(Model.class.getCanonicalName()));
        JandexProtoGenerator protoGenerator = JandexProtoGenerator.builder(
                index,
                DotName.createSimple(Generated.class.getCanonicalName()),
                DotName.createSimple(VariableInfo.class.getCanonicalName()))
                .withPersistenceClass(persistenceClass)
                .build(modelClasses);

        PersistenceGenerator persistenceGenerator = new PersistenceGenerator(
                context,
                protoGenerator);

        if (persistenceGenerator.persistenceType().equals(PersistenceGenerator.MONGODB_PERSISTENCE_TYPE)) {
            addInnerClasses(KogitoProcessInstanceProtobuf.class, reflectiveClass);
            addInnerClasses(KogitoTypesProtobuf.class, reflectiveClass);
            addInnerClasses(KogitoNodeInstanceContentsProtobuf.class, reflectiveClass);
            addInnerClasses(KogitoWorkItemsProtobuf.class, reflectiveClass);
            reflectiveClass.produce(new ReflectiveClassBuildItem(true, true, "java.lang.String"));
        } else if (persistenceGenerator.persistenceType().equals(PersistenceGenerator.KAFKA_PERSISTENCE_TYPE)) {
            String processIds = protoGenerator.getProcessIds().stream().map(s -> "kogito.process." + s).collect(joining(","));
            runTimeConfiguration.produce(new RunTimeConfigurationDefaultBuildItem(PersistenceGenerator.QUARKUS_KAFKA_STREAMS_TOPICS_PROP, processIds));
        }

        return persistenceGenerator.generate();
    }

    private void addInnerClasses(Class<?> superClass, BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
        asList(superClass.getDeclaredClasses()).forEach(c -> {
            reflectiveClass.produce(new ReflectiveClassBuildItem(true, true, c.getName()));
            addInnerClasses(c, reflectiveClass);
        });
    }

    private void registerProcessSVG(KogitoBuildContext context, BuildProducer<NativeImageResourceBuildItem> resource) throws IOException {
        if (!context.hasClassAvailable(PROCESS_SVG_SERVICE)) {
            return;
        }

        Path relativePath = Paths.get("META-INF", "processSVG");
        Path targetClasses = getTargetClassesPath(context.getAppPaths());

        //batik
        resource.produce(new NativeImageResourceBuildItem("org/apache/batik/util/resources/XMLResourceDescriptor.properties"));

        Path resolvedPath = targetClasses.resolve(relativePath);
        try (Stream<Path> filePathFound = Files.find(resolvedPath, Integer.MAX_VALUE, (filePath, attrs) -> svgFileMatcher.matches(filePath))) {
            List<String> svgs = filePathFound.map(svgPath -> targetClasses.relativize(svgPath).toString()).collect(toList());
            resource.produce(new NativeImageResourceBuildItem(svgs));
        }
    }

    private Collection<GeneratedFile> generateJsonSchema(KogitoBuildContext context, IndexView index, Map<String, byte[]> generatedClasses) throws IOException {
        ClassLoader cl = new InMemoryClassLoader(context.getClassLoader(), generatedClasses);

        Collection<AnnotationInstance> annotations =
                index.getAnnotations(DotName.createSimple(UserTask.class.getCanonicalName()));

        Stream<Class<?>> stream = annotations.stream()
                .map(ann -> loadClassFromAnnotation(ann, cl))
                .filter(Optional::isPresent)
                .map(Optional::get);

        JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator.ClassBuilder(stream)
                .withGenSchemaPredicate(x -> true)
                .withSchemaVersion(System.getProperty("kogito.jsonSchema.version")).build();

        return jsonSchemaGenerator.generate();
    }

    private Optional<Class<?>> loadClassFromAnnotation(AnnotationInstance annotationInstance, ClassLoader classLoader) {
        try {
            return Optional.of(classLoader.loadClass(annotationInstance.target().asClass().name().toString()));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
