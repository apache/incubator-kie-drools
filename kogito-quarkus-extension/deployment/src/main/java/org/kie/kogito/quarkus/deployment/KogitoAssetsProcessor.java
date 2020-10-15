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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import javax.inject.Inject;
import org.drools.compiler.builder.impl.KogitoKieModuleModelImpl;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.CompositeIndex;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.MethodInfo;
import org.jbpm.util.JsonSchemaUtil;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.pmml.PMML4Result;
import org.kie.internal.kogito.codegen.Generated;
import org.kie.internal.kogito.codegen.VariableInfo;
import org.kie.kogito.Model;
import org.kie.kogito.UserTask;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.GeneratorContext;
import org.kie.kogito.codegen.JsonSchemaGenerator;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.decision.DecisionCodegen;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.kie.kogito.codegen.io.CollectedResource;
import org.kie.kogito.codegen.prediction.PredictionCodegen;
import org.kie.kogito.codegen.process.ProcessCodegen;
import org.kie.kogito.codegen.process.persistence.PersistenceGenerator;
import org.kie.kogito.codegen.rules.IncrementalRuleCodegen;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluatorFinder;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluatorFinderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final String appPackageName = "org.kie.kogito.app";
    private static final DotName persistenceFactoryClass = DotName.createSimple("org.kie.kogito.persistence.KogitoProcessInstancesFactory");
    private static final DotName metricsClass = DotName.createSimple("org.kie.kogito.monitoring.rest.MetricsResource");
    private static final DotName tracingClass = DotName.createSimple("org.kie.kogito.tracing.decision.DecisionTracingListener");
    private static final DotName knativeEventingClass = DotName.createSimple("org.kie.kogito.events.knative.ce.extensions.KogitoProcessExtension");
    private static final DotName dmnJpmmlClass = DotName.createSimple( "org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator");

    @Inject
    ArchiveRootBuildItem root;
    @Inject
    LiveReloadBuildItem liveReload;
    @Inject
    CurateOutcomeBuildItem curateOutcomeBuildItem;
    @Inject
    CombinedIndexBuildItem combinedIndexBuildItem;
    @Inject
    BuildProducer<GeneratedBeanBuildItem> generatedBeans;
    @Inject
    BuildProducer<NativeImageResourceBuildItem> resource;
    @Inject
    BuildProducer<ReflectiveClassBuildItem> reflectiveClass;
    @Inject
    BuildProducer<GeneratedResourceBuildItem> genResBI;

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
    public void generateModel() throws IOException {

        if (liveReload.isLiveReload()) {
            return;
        }


        // scan and parse paths
        AppPaths appPaths = new AppPaths(root.getPaths());

        // enable addons looking at available classes
        boolean usePersistence = combinedIndexBuildItem.getIndex()
                .getClassByName(persistenceFactoryClass) != null;
        boolean useMonitoring = combinedIndexBuildItem.getIndex()
                .getClassByName(metricsClass) != null;
        boolean useTracing = !combinedIndexBuildItem.getIndex()
                .getAllKnownSubclasses(tracingClass).isEmpty();
        boolean useKnativeEventing = combinedIndexBuildItem.getIndex()
                .getClassByName(knativeEventingClass) != null;
        boolean isJPMMLAvailable =combinedIndexBuildItem.getIndex()
                .getClassByName(dmnJpmmlClass) != null;

        AddonsConfig addonsConfig = new AddonsConfig()
                .withPersistence(usePersistence)
                .withMonitoring(useMonitoring)
                .withTracing(useTracing)
                .withKnativeEventing(useKnativeEventing);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        GeneratorContext context = buildContext(appPaths, combinedIndexBuildItem.getIndex());

        Path[] paths = appPaths.getPath();

        // configure the application generator

        ApplicationGenerator appGen =
                new ApplicationGenerator(
                        appPackageName,
                        new File(appPaths.getFirstProjectPath().toFile(), "target"))
                        .withDependencyInjection(new CDIDependencyInjectionAnnotator())
                        .withAddons(addonsConfig)
                        .withGeneratorContext(context);

        // configure each individual generator. Ordering is relevant.

        appGen.withGenerator(ProcessCodegen.ofCollectedResources(CollectedResource.fromPaths(paths)))
                .withAddons(addonsConfig)
                .withClassLoader(classLoader);

        appGen.withGenerator(IncrementalRuleCodegen.ofCollectedResources(CollectedResource.fromPaths(paths)))
                .withKModule(findKieModuleModel(appPaths))
                .withAddons(addonsConfig)
                .withClassLoader(classLoader);

        appGen.withGenerator(PredictionCodegen.ofCollectedResources(isJPMMLAvailable, CollectedResource.fromPaths(paths)))
                .withAddons(addonsConfig);

        appGen.withGenerator(DecisionCodegen.ofCollectedResources(CollectedResource.fromPaths(paths)))
              .withAddons(addonsConfig)
              .withClassLoader(classLoader);

        // real work occurs here: invoke the code-generation procedure
        Collection<GeneratedFile> generatedFiles = appGen.generate();

        // dump files to disk
        for (Path projectPath : appPaths.projectPaths) {
            generatedFileWriterBuilder
                    .build(projectPath)
                    .writeAll(generatedFiles);
        }

        // register resources to the Quarkus environment
        registerResources(generatedFiles);

        // build Java source code and register the generated beans
        Index index = processGeneratedJavaSourceCode(
                appPaths,
                generatedFiles);

        // no java source code has been generated. Stop.
        if (index == null) {
            return;
        }

        // further processing
        generatePersistenceInfo(appPaths, index);

        registerDataEventsForReflection(index);

        writeJsonSchema(appPaths, index);
    }

    private void registerResources(Collection<GeneratedFile> generatedFiles) {
        for (GeneratedFile f : generatedFiles) {
            if (f.getType() == GeneratedFile.Type.GENERATED_CP_RESOURCE) {
                genResBI.produce(new GeneratedResourceBuildItem(f.relativePath(), f.contents()));
                resource.produce(new NativeImageResourceBuildItem(f.relativePath()));
            }
        }
    }

    private Index processGeneratedJavaSourceCode(
            AppPaths appPaths,
            Collection<GeneratedFile> generatedFiles) throws IOException {

        // we are currently filtering on file extension,
        // but we should tag GeneratedFile properly
        Collection<GeneratedFile> javaFiles =
                generatedFiles.stream()
                        .filter(f -> f.relativePath().endsWith(".java"))
                        .collect(Collectors.toList());

        if (javaFiles.isEmpty()) {
            return null;
        }

        InMemoryCompiler inMemoryCompiler = new InMemoryCompiler(
                appPaths.classesPaths,
                curateOutcomeBuildItem.getEffectiveModel().getUserDependencies());
        inMemoryCompiler.compile(javaFiles);

        MemoryFileSystem trgMfs = inMemoryCompiler.getTargetFileSystem();
        Collection<GeneratedBeanBuildItem> generatedBeanBuildItems = makeBuildItems(appPaths, trgMfs);
        generatedBeanBuildItems.forEach(generatedBeans::produce);
        return indexBuildItems(generatedBeanBuildItems);
    }

    private void generatePersistenceInfo(AppPaths appPaths, IndexView inputIndex) throws IOException {

        CompositeIndex index = CompositeIndex.create(combinedIndexBuildItem.getIndex(), inputIndex);

        ClassInfo persistenceClass = index
                .getClassByName(persistenceFactoryClass);
        boolean usePersistence = persistenceClass != null;

        List<String> parameters = new ArrayList<>();
        if (usePersistence) {
            for (MethodInfo mi : persistenceClass.methods()) {
                if (mi.name().equals("<init>") && !mi.parameters().isEmpty()) {
                    parameters = mi.parameters().stream().map(p -> p.name().toString()).collect(Collectors.toList());
                    break;
                }
            }
        }
        GeneratorContext context = buildContext(appPaths, index);
        String persistenceType = context.getApplicationProperty("kogito.persistence.type").orElse(PersistenceGenerator.DEFAULT_PERSISTENCE_TYPE);
        Collection<GeneratedFile> generatedFiles = getGeneratedPersistenceFiles(appPaths, index, usePersistence, parameters, context, persistenceType);

        if (!generatedFiles.isEmpty()) {
            InMemoryCompiler inMemoryCompiler = new InMemoryCompiler(appPaths.classesPaths,
                                                                     curateOutcomeBuildItem.getEffectiveModel().getUserDependencies());
            inMemoryCompiler.compile(generatedFiles);
            Collection<GeneratedBeanBuildItem> generatedBeanBuildItems = makeBuildItems(appPaths, inMemoryCompiler.getTargetFileSystem());
            generatedBeanBuildItems.forEach(generatedBeans::produce);
        }

        if (usePersistence) {
            resource.produce(new NativeImageResourceBuildItem("kogito-types.proto"));
        }
       
        if(persistenceType.equals(PersistenceGenerator.MONGODB_PERSISTENCE_TYPE)) {
            addInnerClasses(org.jbpm.marshalling.impl.JBPMMessages.class, reflectiveClass);
            reflectiveClass.produce(new ReflectiveClassBuildItem(true, true, "java.lang.String"));
        }
    }
    
    private void addInnerClasses(Class<?> superClass, BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
        Arrays.asList(superClass.getDeclaredClasses()).forEach(c -> {
            reflectiveClass.produce(new ReflectiveClassBuildItem(true, true, c.getName()));
            addInnerClasses(c, reflectiveClass);
        });
    }

    private Collection<GeneratedFile> getGeneratedPersistenceFiles(AppPaths appPaths,
                                                                   IndexView index,
                                                                   boolean usePersistence,
                                                                   List<String> parameters,
                                                                   GeneratorContext context,
                                                                   String persistenceType) {
        JandexProtoGenerator jandexProtoGenerator =
                new JandexProtoGenerator(
                        index,
                        DotName.createSimple(Generated.class.getCanonicalName()),
                        DotName.createSimple(VariableInfo.class.getCanonicalName()));
        Collection<ClassInfo> modelClasses = index
                .getAllKnownImplementors(DotName.createSimple(Model.class.getCanonicalName()));

        Collection<GeneratedFile> generatedFiles = new ArrayList<>();

        for (Path projectPath : appPaths.projectPaths) {
            PersistenceGenerator persistenceGenerator =
                    makePersistenceGenerator(
                            usePersistence,
                            parameters,
                            context,
                            modelClasses,
                            jandexProtoGenerator,
                            projectPath,
                            persistenceType);

            generatedFiles.addAll(persistenceGenerator.generate());
        }
        return generatedFiles;
    }

    private PersistenceGenerator makePersistenceGenerator(
            boolean usePersistence,
            List<String> parameters,
            GeneratorContext context,
            Collection<ClassInfo> modelClasses,
            JandexProtoGenerator jandexProtoGenerator,
            Path projectPath,
            String persistenceType) {
        PersistenceGenerator persistenceGenerator =
                new PersistenceGenerator(
                        new File(projectPath.toFile(), "target"),
                        modelClasses,
                        usePersistence,
                        jandexProtoGenerator,
                        parameters,
                        persistenceType);
        persistenceGenerator.setDependencyInjection(new CDIDependencyInjectionAnnotator());
        persistenceGenerator.setPackageName(appPackageName);
        persistenceGenerator.setContext(context);
        return persistenceGenerator;
    }

    private Collection<GeneratedFile> getJsonSchemaFiles(Path path, Index index) throws IOException {
        URL[] urls = {path.toUri().toURL()};
        URLClassLoader cl = new URLClassLoader(
                urls,
                Thread.currentThread().getContextClassLoader());
        List<AnnotationInstance> annotations =
                index.getAnnotations(DotName.createSimple(UserTask.class.getCanonicalName()));

        JsonSchemaGenerator.SimpleBuilder simpleBuilder =
                new JsonSchemaGenerator.SimpleBuilder(cl)
                        .withSchemaVersion(System.getProperty("kogito.jsonSchema.version"));

        for (AnnotationInstance ann : annotations) {
            String processName = ann.value("processName").asString();
            String taskName = ann.value("taskName").asString();
            simpleBuilder.addSchemaName(ann.target().asClass().name().toString(), processName, taskName);
        }

        return simpleBuilder.build().generate();
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

    private void writeJsonSchema(AppPaths appPaths, Index index) throws IOException {
        Path relativePath = JsonSchemaUtil.getJsonDir();
        Path targetClasses = appPaths.getFirstProjectPath().resolve(targetClassesDir);
        Collection<GeneratedFile> jsonFiles = getJsonSchemaFiles(targetClasses, index);
        Path jsonSchemaPath = targetClasses.resolve(relativePath);
        Files.createDirectories(jsonSchemaPath);

        for (GeneratedFile jsonFile : jsonFiles) {
            Files.write(jsonSchemaPath.resolve(jsonFile.relativePath()), jsonFile.contents());
            resource.produce(new NativeImageResourceBuildItem(relativePath.resolve(jsonFile.relativePath()).toString()));
        }
    }

    private void registerDataEventsForReflection(Index index) {
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

    private Index indexBuildItems(Collection<GeneratedBeanBuildItem> buildItems) {
        Indexer kogitoIndexer = new Indexer();
        Set<DotName> kogitoIndex = new HashSet<>();

        for (GeneratedBeanBuildItem generatedBeanBuildItem : buildItems) {
            IndexingUtil.indexClass(
                    generatedBeanBuildItem.getName(),
                    kogitoIndexer,
                    combinedIndexBuildItem.getIndex(),
                    kogitoIndex,
                    Thread.currentThread().getContextClassLoader(),
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

    private KieModuleModel findKieModuleModel(AppPaths appPaths) throws IOException {
        for (Path resourcePath : appPaths.getResourcePaths()) {
            Path moduleXmlPath = resourcePath.resolve(KogitoKieModuleModelImpl.KMODULE_JAR_PATH);
            if (Files.exists(moduleXmlPath)) {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(Files.readAllBytes(moduleXmlPath))) {
                    return KogitoKieModuleModelImpl.fromXML(bais);
                }
            }
        }

        return KogitoKieModuleModelImpl.fromXML(
                "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>");
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

    private GeneratorContext buildContext(AppPaths appPaths, IndexView index) {
        GeneratorContext context = GeneratorContext.ofResourcePath(appPaths.getResourceFiles());
        context.withBuildContext(new QuarkusKogitoBuildContext(className -> {
            DotName classDotName = DotName.createSimple(className);
            return !index.getAnnotations(classDotName).isEmpty() || index.getClassByName(classDotName) != null;
        }));

        return context;
    }
}
