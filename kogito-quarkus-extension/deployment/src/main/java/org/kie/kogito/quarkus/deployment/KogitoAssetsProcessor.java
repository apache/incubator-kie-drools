/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.quarkus.deployment;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Inject;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ArchiveRootBuildItem;
import io.quarkus.deployment.builditem.CapabilityBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyIgnoreWarningBuildItem;
import io.quarkus.deployment.index.IndexingUtil;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.CompositeIndex;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;
import org.kie.api.pmml.PMML4Result;
import org.kie.internal.kogito.codegen.Generated;
import org.kie.internal.kogito.codegen.VariableInfo;
import org.kie.kogito.Model;
import org.kie.kogito.UserTask;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.GeneratedFileType;
import org.kie.kogito.codegen.JsonSchemaGenerator;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.process.persistence.PersistenceGenerator;
import org.kie.kogito.codegen.utils.AppPaths;
import org.kie.kogito.codegen.utils.ApplicationGeneratorDiscovery;
import org.kie.kogito.codegen.utils.GeneratedFileWriter;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluatorFinder;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluatorFinderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.kie.kogito.codegen.utils.GeneratedFileValidation.validateGeneratedFileTypes;

/**
 * Main class of the Kogito extension
 */
public class KogitoAssetsProcessor {

    private static final Logger logger = LoggerFactory.getLogger(KogitoAssetsProcessor.class);

    public static final String targetClassesDir = "target/classes";

    // since quarkus-maven-plugin is later phase of maven-resources-plugin,
    // need to manually late-provide the resource in the expected location for quarkus:dev phase --so not: writeGeneratedFile( f, resourcePath );
    private static final GeneratedFileWriter.Builder generatedFileWriterBuilder =
            new GeneratedFileWriter.Builder(
                    targetClassesDir,
                    System.getProperty("kogito.codegen.sources.directory", "target/generated-sources/kogito/"),
                    System.getProperty("kogito.codegen.resources.directory", "target/generated-resources/kogito/"),
                    "target/generated-sources/kogito/");

    private static final DotName persistenceFactoryClass = DotName.createSimple("org.kie.kogito.persistence.KogitoProcessInstancesFactory");
    private static final DotName quarkusSVGService = DotName.createSimple("org.kie.kogito.svg.service.QuarkusProcessSvgService");

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
        return new CapabilityBuildItem("kogito");
    }

    @BuildStep
    FeatureBuildItem featureBuildItem() {
        return new FeatureBuildItem("kogito");
    }

    /**
     * Main entry point of the Quarkus extension
     */
    @BuildStep
    public void generateModel(
            BuildProducer<GeneratedBeanBuildItem> generatedBeans,
            BuildProducer<NativeImageResourceBuildItem> resource,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            BuildProducer<GeneratedResourceBuildItem> genResBI
    ) throws IOException {

        if (liveReload.isLiveReload()) {
            return;
        }


        boolean useProcessSVG = combinedIndexBuildItem.getIndex().getClassByName(quarkusSVGService) != null;

        // configure the application generator
        KogitoBuildContext context = kogitoBuildContext();

        Collection<GeneratedFile> generatedFiles = ApplicationGeneratorDiscovery
                .discover(context)
                .generate();

        // dump files to disk
        dumpFilesToDisk(context.getAppPaths(), generatedFiles);

        // build Java source code and register the generated beans
        Index index = processGeneratedJavaSourceCode(
                context,
                generatedFiles,
                generatedBeans);

        // no java source code has been generated. Stop.
        if (index == null) {
            return;
        }

        // Persistence files
        generatedFiles.addAll(generatePersistenceInfo(
                context,
                index,
                generatedBeans,
                resource,
                reflectiveClass));

        // Json schema files
        generatedFiles.addAll(generateJsonSchema(context, index));

        // Write files to disk
        dumpFilesToDisk(context.getAppPaths(), generatedFiles);

        // register resources to the Quarkus environment
        registerResources(generatedFiles, resource, genResBI);

        registerDataEventsForReflection(index, context, reflectiveClass);

        if (useProcessSVG) {
            registerProcessSVG(context.getAppPaths(), resource);
        }
    }

    private KogitoBuildContext kogitoBuildContext() {
        // scan and parse paths
        AppPaths appPaths = AppPaths.fromQuarkus(root.getPaths());
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return KogitoQuarkusContextProvider.context(appPaths, classLoader, className -> classAvailabilityResolver(classLoader, className));
    }

    /**
     * Verify if a class is available. First uses jandex indexes, then fallback on classLoader
     * @param classLoader
     * @param className
     * @return
     */
    private boolean classAvailabilityResolver(ClassLoader classLoader, String className) {
        IndexView index = combinedIndexBuildItem.getIndex();
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

    private void dumpFilesToDisk(AppPaths appPaths, Collection<GeneratedFile> generatedFiles){
        generatedFileWriterBuilder
                .build(appPaths.getFirstProjectPath())
                .writeAll(generatedFiles);
    }

    private void registerResources(Collection<GeneratedFile> generatedFiles,
                                   BuildProducer<NativeImageResourceBuildItem> resource,
                                   BuildProducer<GeneratedResourceBuildItem> genResBI) {
        for (GeneratedFile f : generatedFiles) {
            if (f.category() == GeneratedFileType.Category.RESOURCE) {
                genResBI.produce(new GeneratedResourceBuildItem(f.relativePath(), f.contents()));
                resource.produce(new NativeImageResourceBuildItem(f.relativePath()));
            }
        }
    }

    private Index processGeneratedJavaSourceCode(
            KogitoBuildContext context,
            Collection<GeneratedFile> generatedFiles,
            BuildProducer<GeneratedBeanBuildItem> generatedBeans) throws IOException {

        Collection<GeneratedFile> javaFiles =
                generatedFiles.stream()
                        .filter(f -> f.category() == GeneratedFileType.Category.SOURCE)
                        .collect(toList());

        if (javaFiles.isEmpty()) {
            return null;
        }

        InMemoryCompiler inMemoryCompiler = new InMemoryCompiler(
                context.getAppPaths().getClassesPaths(),
                curateOutcomeBuildItem.getEffectiveModel().getUserDependencies());
        inMemoryCompiler.compile(javaFiles);

        MemoryFileSystem trgMfs = inMemoryCompiler.getTargetFileSystem();
        Collection<GeneratedBeanBuildItem> generatedBeanBuildItems = makeBuildItems(context.getAppPaths(), trgMfs);
        generatedBeanBuildItems.forEach(generatedBeans::produce);
        return indexBuildItems(context, generatedBeanBuildItems);
    }

    private Collection<GeneratedFile> generatePersistenceInfo(
            KogitoBuildContext context,
            IndexView inputIndex,
            BuildProducer<GeneratedBeanBuildItem> generatedBeans,
            BuildProducer<NativeImageResourceBuildItem> resource,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass) throws IOException {

        CompositeIndex index = CompositeIndex.create(combinedIndexBuildItem.getIndex(), inputIndex);

        Collection<GeneratedFile> persistenceGeneratedFiles = getGeneratedPersistenceFiles(index, context, reflectiveClass);

        validateGeneratedFileTypes(persistenceGeneratedFiles, asList(GeneratedFileType.Category.SOURCE, GeneratedFileType.Category.RESOURCE));

        Collection<GeneratedFile> persistenceClasses = persistenceGeneratedFiles.stream().filter(x -> x.category().equals(GeneratedFileType.Category.SOURCE)).collect(toList());
        Collection<GeneratedFile> persistenceProtoFiles = persistenceGeneratedFiles.stream().filter(x -> x.category().equals(GeneratedFileType.Category.RESOURCE)).collect(toList());

        if (!persistenceClasses.isEmpty()) {
            InMemoryCompiler inMemoryCompiler = new InMemoryCompiler(context.getAppPaths().getClassesPaths(),
                                                                     curateOutcomeBuildItem.getEffectiveModel().getUserDependencies());
            inMemoryCompiler.compile(persistenceClasses);
            Collection<GeneratedBeanBuildItem> generatedBeanBuildItems = makeBuildItems(context.getAppPaths(), inMemoryCompiler.getTargetFileSystem());
            generatedBeanBuildItems.forEach(generatedBeans::produce);
        }

        if (context.getAddonsConfig().usePersistence()) {
            resource.produce(new NativeImageResourceBuildItem("kogito-types.proto"));
        }

        return persistenceProtoFiles;
    }

    private Collection<GeneratedFile> getGeneratedPersistenceFiles(IndexView index,
                                                                   KogitoBuildContext context,
                                                                   BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
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

        if(persistenceGenerator.persistenceType().equals(PersistenceGenerator.MONGODB_PERSISTENCE_TYPE)) {
            addInnerClasses(org.jbpm.marshalling.impl.JBPMMessages.class, reflectiveClass);
            reflectiveClass.produce(new ReflectiveClassBuildItem(true, true, "java.lang.String"));
        }

        return persistenceGenerator.generate();
    }

    private void addInnerClasses(Class<?> superClass, BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
        asList(superClass.getDeclaredClasses()).forEach(c -> {
            reflectiveClass.produce(new ReflectiveClassBuildItem(true, true, c.getName()));
            addInnerClasses(c, reflectiveClass);
        });
    }

    @BuildStep
    public List<ReflectiveHierarchyIgnoreWarningBuildItem> reflectiveDMNREST() {
        List<ReflectiveHierarchyIgnoreWarningBuildItem> result = new ArrayList<>();
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.api.builder.Message$Level")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.core.DMNContext")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.core.DMNDecisionResult")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(
                DotName.createSimple("org.kie.dmn.api.core.DMNDecisionResult$DecisionEvaluationStatus")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.core.DMNMessage")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.core.DMNMessage$Severity")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.core.DMNMessageType")));
        result.add(
                new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.feel.runtime.events.FEELEvent")));
        return result;
    }

    @SuppressWarnings("rawtypes")
    @BuildStep
    public List<ReflectiveClassBuildItem> reflectivePredictions() {
        logger.debug("reflectivePredictions()");
        PMMLModelEvaluatorFinder pmmlModelEvaluatorFinder = new PMMLModelEvaluatorFinderImpl();
        final List<PMMLModelEvaluator> pmmlEvaluators = pmmlModelEvaluatorFinder.getImplementations(false);
        logger.debug("pmmlEvaluators {}", pmmlEvaluators.size());
        final List<ReflectiveClassBuildItem> toReturn = new ArrayList<>();
        toReturn.add(new ReflectiveClassBuildItem(true, true, PMML4Result.class));
        pmmlEvaluators.
                forEach(pmmlModelEvaluator -> toReturn.add(new ReflectiveClassBuildItem(true, true, pmmlModelEvaluator.getClass())));
        logger.debug("toReturn {}", toReturn.size());
        return toReturn;
    }

    @BuildStep
    public NativeImageResourceBuildItem predictionSPI() {
        logger.debug("predictionSPI()");
        return new NativeImageResourceBuildItem("META-INF/services/org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator");
    }

    @BuildStep
    public ReflectiveClassBuildItem reflectionJobsManagement() {
        return new ReflectiveClassBuildItem(true, true, "org.kie.kogito.jobs.api.Job");
    }

    private void registerProcessSVG(AppPaths appPaths, BuildProducer<NativeImageResourceBuildItem> resource) throws IOException {
        Path relativePath = Paths.get("META-INF", "processSVG");
        Path targetClasses = appPaths.getFirstProjectPath().resolve(targetClassesDir);

        //batik
        resource.produce(new NativeImageResourceBuildItem("org/apache/batik/util/resources/XMLResourceDescriptor.properties"));

        Path resolvedPath = targetClasses.resolve(relativePath);
        try (Stream<Path> filePathFound = Files.find(resolvedPath, Integer.MAX_VALUE, (filePath, attrs) -> svgFileMatcher.matches(filePath))) {
            List<String> svgs = filePathFound.map(svgPath -> targetClasses.relativize(svgPath).toString()).collect(toList());
            resource.produce(new NativeImageResourceBuildItem(svgs));
        }
    }

    private Collection<GeneratedFile> generateJsonSchema(KogitoBuildContext context, Index index) throws IOException {
        Path targetClasses = context.getAppPaths().getFirstProjectPath().resolve(targetClassesDir);
        URL[] urls = {targetClasses.toUri().toURL()};

        try (URLClassLoader cl = new URLClassLoader(urls, context.getClassLoader())) {

            List<AnnotationInstance> annotations =
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
    }

    private Optional<Class<?>> loadClassFromAnnotation(AnnotationInstance annotationInstance, ClassLoader classLoader) {
        try {
            return Optional.of(classLoader.loadClass(annotationInstance.target().asClass().name().toString()));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    private void registerDataEventsForReflection(Index index, KogitoBuildContext context, BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.event.AbstractDataEvent"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.AbstractProcessDataEvent"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.ProcessInstanceDataEvent"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.VariableInstanceDataEvent"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.impl.ProcessInstanceEventBody"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.impl.NodeInstanceEventBody"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.impl.ProcessErrorEventBody"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.impl.VariableInstanceEventBody"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.UserTaskInstanceDataEvent"));
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, true, "org.kie.kogito.services.event.impl.UserTaskInstanceEventBody"));
        if (context.getAddonsConfig().useMonitoring()){
            reflectiveClass.produce(
                    new ReflectiveClassBuildItem(true, true, "org.HdrHistogram.Histogram"));
            reflectiveClass.produce(
                    new ReflectiveClassBuildItem(true, true, "org.HdrHistogram.ConcurrentHistogram"));
        }

        // not sure there is any generated class directly inheriting from AbstractDataEvent, keeping just in case
        addChildrenClasses(index, org.kie.kogito.event.AbstractDataEvent.class, reflectiveClass);
        addChildrenClasses(index, org.kie.kogito.services.event.AbstractProcessDataEvent.class, reflectiveClass);
    }

    private void addChildrenClasses(Index index,
                                    Class<?> superClass,
                                    BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
        index.getAllKnownSubclasses(DotName.createSimple(superClass.getCanonicalName()))
             .forEach(c -> reflectiveClass.produce(
                      new ReflectiveClassBuildItem(true, true, c.name().toString())));
    }

    private Index indexBuildItems(KogitoBuildContext context, Collection<GeneratedBeanBuildItem> buildItems) {
        Indexer kogitoIndexer = new Indexer();
        Set<DotName> kogitoIndex = new HashSet<>();

        for (GeneratedBeanBuildItem generatedBeanBuildItem : buildItems) {
            IndexingUtil.indexClass(
                    generatedBeanBuildItem.getName(),
                    kogitoIndexer,
                    combinedIndexBuildItem.getIndex(),
                    kogitoIndex,
                    context.getClassLoader(),
                    generatedBeanBuildItem.getData());
        }

        return kogitoIndexer.complete();
    }

    private Collection<GeneratedBeanBuildItem> makeBuildItems(AppPaths appPaths, MemoryFileSystem trgMfs) throws IOException {

        ArrayList<GeneratedBeanBuildItem> buildItems = new ArrayList<>();
        Path location = appPaths.getFirstProjectPath().resolve(targetClassesDir);
        for (String fileName : trgMfs.getFileNames()) {
            byte[] data = trgMfs.getBytes(fileName);
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

    private String toClassName(String sourceName) {
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

    private Path pathOf(String location, String end) {
        Path path = Paths.get(location, end);
        path.getParent().toFile().mkdirs();
        return path;
    }
}
