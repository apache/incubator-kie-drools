package org.kie.kogito.quarkus.deployment;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.bootstrap.BootstrapDependencyProcessingException;
import io.quarkus.bootstrap.model.AppDependency;
import io.quarkus.bootstrap.model.AppModel;
import io.quarkus.bootstrap.model.PathsCollection;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ArchiveRootBuildItem;
import io.quarkus.deployment.builditem.CapabilityBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyIgnoreWarningBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.deployment.index.IndexingUtil;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.runtime.LaunchMode;
import org.drools.compiler.builder.impl.KogitoKieModuleModelImpl;
import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompilerSettings;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.core.base.ClassFieldAccessorFactory;
import org.drools.modelcompiler.builder.JavaParserCompiler;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.CompositeIndex;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.MethodInfo;
import org.jbpm.util.JsonSchemaUtil;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.internal.jci.CompilationProblem;
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
import org.kie.kogito.codegen.prediction.PredictionCodegen;
import org.kie.kogito.codegen.process.ProcessCodegen;
import org.kie.kogito.codegen.process.persistence.PersistenceGenerator;
import org.kie.kogito.codegen.rules.IncrementalRuleCodegen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class KogitoAssetsProcessor {

    private static final String generatedResourcesDir = System.getProperty("kogito.codegen.resources.directory", "target/generated-resources/kogito/");
    private static final String generatedSourcesDir = "target/generated-sources/kogito/";
    private static final String generatedCustomizableSourcesDir = System.getProperty("kogito.codegen.sources.directory", "target/generated-sources/kogito/");
    private static final Logger logger = LoggerFactory.getLogger(KogitoAssetsProcessor.class);
    private final transient String generatedClassesDir = System.getProperty("quarkus.debug.generated-classes-dir");
    private final transient String appPackageName = "org.kie.kogito.app";
    private final transient String persistenceFactoryClass = "org.kie.kogito.persistence.KogitoProcessInstancesFactory";
    private final transient String metricsClass = "org.kie.kogito.monitoring.rest.MetricsResource";
    private final transient String tracingClass = "org.kie.kogito.tracing.decision.DecisionTracingListener";
    private final transient String knativeEventingClass = "org.kie.kogito.events.knative.ce.http.HttpRequestConverter";

    @BuildStep
    CapabilityBuildItem capability() {
        return new CapabilityBuildItem("kogito");
    }

    @BuildStep
    FeatureBuildItem featureBuildItem() {
        return new FeatureBuildItem("kogito");
    }

    private void generatePersistenceInfo(AppPaths appPaths,
                                         BuildProducer<GeneratedBeanBuildItem> generatedBeans,
                                         IndexView index,
                                         LaunchModeBuildItem launchMode,
                                         BuildProducer<NativeImageResourceBuildItem> resource,
                                         CurateOutcomeBuildItem curateOutcomeBuildItem) throws IOException, BootstrapDependencyProcessingException {

        ClassInfo persistenceClass = index
                .getClassByName(createDotName(persistenceFactoryClass));
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

        Collection<GeneratedFile> generatedFiles = getGeneratedPersistenceFiles(appPaths, index, usePersistence, parameters);

        if (!generatedFiles.isEmpty()) {
            MemoryFileSystem trgMfs = new MemoryFileSystem();
            CompilationResult result = compile(appPaths, trgMfs, curateOutcomeBuildItem.getEffectiveModel(), generatedFiles, launchMode.getLaunchMode());
            register(appPaths, trgMfs, generatedBeans, GeneratedBeanBuildItem::new, launchMode.getLaunchMode(), result);
        }

        if (usePersistence) {
            resource.produce(new NativeImageResourceBuildItem("kogito-types.proto"));
        }
    }

    private Collection<GeneratedFile> getGeneratedPersistenceFiles(AppPaths appPaths, IndexView index, boolean usePersistence, List<String> parameters) {
        GeneratorContext context = buildContext(appPaths, index);

        Collection<ClassInfo> modelClasses = index
                .getAllKnownImplementors(createDotName(Model.class.getCanonicalName()));

        Collection<GeneratedFile> generatedFiles = new ArrayList<>();

        for (Path projectPath : appPaths.projectPaths) {
            PersistenceGenerator persistenceGenerator = new PersistenceGenerator( new File( projectPath.toFile(), "target" ),
                                                                                  modelClasses, usePersistence,
                                                                                  new JandexProtoGenerator( index, createDotName( Generated.class.getCanonicalName() ),
                                                                                                            createDotName( VariableInfo.class.getCanonicalName() ) ),
                                                                                  parameters );
            persistenceGenerator.setDependencyInjection( new CDIDependencyInjectionAnnotator() );
            persistenceGenerator.setPackageName( appPackageName );
            persistenceGenerator.setContext( context );

            generatedFiles.addAll(persistenceGenerator.generate());
        }
        return generatedFiles;
    }


    private Collection<GeneratedFile> getJsonSchemaFiles(Index index, MemoryFileSystem trgMfs) throws IOException {
        MemoryClassLoader cl = new MemoryClassLoader(trgMfs, Thread.currentThread().getContextClassLoader());
        return new JsonSchemaGenerator.Builder(index.getAnnotations(createDotName(UserTask.class.getCanonicalName())).stream().map(instance -> {
            try {
                return cl.loadClass(instance.target().toString());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        })).withGenSchemaPredicate(x -> true).withSchemaVersion(System.getProperty("kogito.jsonSchema.version")).build().generate();

    }

    @BuildStep
    public List<ReflectiveHierarchyIgnoreWarningBuildItem> reflectiveDMNREST() {
        List<ReflectiveHierarchyIgnoreWarningBuildItem> result = new ArrayList<>();
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(createDotName("org.kie.api.builder.Message$Level")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(createDotName("org.kie.dmn.api.core.DMNContext")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(createDotName("org.kie.dmn.api.core.DMNDecisionResult")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(
                createDotName("org.kie.dmn.api.core.DMNDecisionResult$DecisionEvaluationStatus")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(createDotName("org.kie.dmn.api.core.DMNMessage")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(createDotName("org.kie.dmn.api.core.DMNMessage$Severity")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(createDotName("org.kie.dmn.api.core.DMNMessageType")));
        result.add(
                new ReflectiveHierarchyIgnoreWarningBuildItem(createDotName("org.kie.dmn.api.feel.runtime.events.FEELEvent")));
        return result;
    }

    @BuildStep
    public RuntimeInitializedClassBuildItem runtimeInitializedClass() {
        return new RuntimeInitializedClassBuildItem(ClassFieldAccessorFactory.class.getName());
    }

    @BuildStep
    public void generateModel(ArchiveRootBuildItem root,
                              BuildProducer<GeneratedBeanBuildItem> generatedBeans,
                              CombinedIndexBuildItem combinedIndexBuildItem,
                              LaunchModeBuildItem launchMode,
                              LiveReloadBuildItem liveReload,
                              BuildProducer<NativeImageResourceBuildItem> resource,
                              BuildProducer<GeneratedResourceBuildItem> genResBI,
                              BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
                              CurateOutcomeBuildItem curateOutcomeBuildItem) throws IOException, BootstrapDependencyProcessingException {

        if (liveReload.isLiveReload()) {
            return;
        }

        AppPaths appPaths = new AppPaths(root.getPaths());

        ApplicationGenerator appGen = createApplicationGenerator(appPaths, combinedIndexBuildItem);
        Collection<GeneratedFile> generatedFiles = appGen.generate();

        Collection<GeneratedFile> javaFiles = generatedFiles.stream().filter( f -> f.relativePath().endsWith( ".java" ) ).collect( Collectors.toCollection( ArrayList::new ));
        writeGeneratedFiles(appPaths, generatedFiles, resource, genResBI);

        if (!javaFiles.isEmpty()) {

            Indexer kogitoIndexer = new Indexer();
            Set<DotName> kogitoIndex = new HashSet<>();

            MemoryFileSystem trgMfs = new MemoryFileSystem();
            CompilationResult result = compile(appPaths, trgMfs, curateOutcomeBuildItem.getEffectiveModel(), javaFiles, launchMode.getLaunchMode());
            register(appPaths, trgMfs, generatedBeans,
                     (className, data) -> generateBeanBuildItem( combinedIndexBuildItem, kogitoIndexer, kogitoIndex, className, data ),
                     launchMode.getLaunchMode(), result);


            Index index = kogitoIndexer.complete();

            generatePersistenceInfo(appPaths, generatedBeans, CompositeIndex.create(combinedIndexBuildItem.getIndex(), index),
                                    launchMode, resource, curateOutcomeBuildItem);


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

            Collection<ClassInfo> dataEvents = index
                    .getAllKnownSubclasses(createDotName("org.kie.kogito.event.AbstractDataEvent"));

            dataEvents.forEach(c -> reflectiveClass.produce(
                    new ReflectiveClassBuildItem(true, true, c.name().toString())));

            Collection<GeneratedFile> jsonFiles = getJsonSchemaFiles(index, trgMfs);
            Path relativePath = JsonSchemaUtil.getJsonDir();
            Path jsonSchemaPath = appPaths.getFirstProjectPath().resolve("target").resolve("classes").resolve(relativePath);
            Files.createDirectories(jsonSchemaPath);

            for (GeneratedFile jsonFile : jsonFiles) {
                Files.write(jsonSchemaPath.resolve(jsonFile.relativePath()), jsonFile.contents());
                resource.produce(new NativeImageResourceBuildItem(relativePath.resolve(jsonFile.relativePath()).toString()));
            }
        }
    }

    private void writeGeneratedFiles(AppPaths appPaths, Collection<GeneratedFile> resourceFiles, BuildProducer<NativeImageResourceBuildItem> niResBI, BuildProducer<GeneratedResourceBuildItem> genResBI) {
        for (Path projectPath : appPaths.projectPaths) {
            String restResourcePath = projectPath.resolve( generatedCustomizableSourcesDir ).toString();
            String resourcePath = projectPath.resolve( generatedResourcesDir ).toString();
            String sourcePath = projectPath.resolve( generatedSourcesDir ).toString(); 

            for (GeneratedFile f : resourceFiles) {
                try {
                    if ( f.getType() == GeneratedFile.Type.RESOURCE ) {
                        writeGeneratedFile( f, resourcePath );
                    } else if (f.getType() == GeneratedFile.Type.GENERATED_CP_RESOURCE) { // since quarkus-maven-plugin is later phase of maven-resources-plugin, need to manually late-provide the resource in the expected location for quarkus:dev phase --so not: writeGeneratedFile( f, resourcePath );
                        Path resolve = appPaths.getFirstProjectPath().resolve("target").resolve("classes").resolve(f.relativePath());
                        resolve.getParent().toFile().mkdirs();
                        Files.write(resolve, f.contents());
                        genResBI.produce(new GeneratedResourceBuildItem(f.relativePath(), f.contents()));
                        niResBI.produce(new NativeImageResourceBuildItem(f.relativePath()));
                    } else if (f.getType().isCustomizable()) {
                        writeGeneratedFile(f, restResourcePath);
                    } else {
                        writeGeneratedFile(f, sourcePath);
                    }
                } catch (IOException e) {
                    logger.warn(String.format("Could not write file '%s'", f.toString()), e);
                }
            }
        }
    }

    private GeneratedBeanBuildItem generateBeanBuildItem(CombinedIndexBuildItem combinedIndexBuildItem, Indexer kogitoIndexer, Set<DotName> kogitoIndex, String className, byte[] data) {
        IndexingUtil.indexClass(className, kogitoIndexer, combinedIndexBuildItem.getIndex(), kogitoIndex,
                                Thread.currentThread().getContextClassLoader(), data);
        return new GeneratedBeanBuildItem(className, data);
    }

    private CompilationResult compile(AppPaths appPaths, MemoryFileSystem trgMfs,
                                      AppModel appModel,
                                      Collection<GeneratedFile> generatedFiles,
                                      LaunchMode launchMode) throws IOException {

        JavaCompiler javaCompiler = JavaParserCompiler.getCompiler();
        JavaCompilerSettings compilerSettings = javaCompiler.createDefaultSettings();
        compilerSettings.addOption("-proc:none"); // force disable annotation processing
        for (Path classPath : appPaths.classesPaths) {
            compilerSettings.addClasspath(classPath.toString());
        }
        if (appModel != null) {
            for (AppDependency i : appModel.getUserDependencies()) {
                compilerSettings.addClasspath(i.getArtifact().getPaths().getSinglePath().toAbsolutePath().toString());
            }
        }

        MemoryFileSystem srcMfs = new MemoryFileSystem();

        String[] sources = new String[generatedFiles.size()];
        int index = 0;
        for (GeneratedFile entry : generatedFiles) {
            String generatedClassFile = entry.relativePath().replace("src/main/java/", "");
            String fileName = toRuntimeSource(toClassName(generatedClassFile));
            sources[index++] = fileName;

            srcMfs.write(fileName, entry.contents());

            String location = generatedClassesDir;
            if (launchMode == LaunchMode.DEVELOPMENT) {
                location = Paths.get(appPaths.getFirstClassesPath().toString()).toString();
            }

            writeGeneratedFile(entry, location);
        }

        return javaCompiler.compile(sources, srcMfs, trgMfs,
                                    Thread.currentThread().getContextClassLoader(), compilerSettings);
    }

    private void register(AppPaths appPaths, MemoryFileSystem trgMfs,
                          BuildProducer<GeneratedBeanBuildItem> generatedBeans,
                          BiFunction<String, byte[], GeneratedBeanBuildItem> bif,
                          LaunchMode launchMode,
                          CompilationResult result) throws IOException {
        if (result.getErrors().length > 0) {
            StringBuilder errorInfo = new StringBuilder();
            for (CompilationProblem compilationProblem : result.getErrors()) {
                errorInfo.append(compilationProblem.toString());
                errorInfo.append("\n");
                logger.error(compilationProblem.toString());
            }
            Arrays.stream(result.getErrors()).forEach(cp -> errorInfo.append(cp.toString()));
            throw new IllegalStateException(errorInfo.toString());
        }

        for (String fileName : trgMfs.getFileNames()) {
            byte[] data = trgMfs.getBytes(fileName);
            String className = toClassName(fileName);
            generatedBeans.produce(bif.apply(className, data));

            if (launchMode == LaunchMode.DEVELOPMENT) {
                Path path = writeFile(Paths.get(appPaths.getFirstClassesPath().toString(), fileName).toString(), data);

                String sourceFile = path.toString().replaceFirst("\\.class", ".java");
                if (sourceFile.contains("$")) {
                    sourceFile = sourceFile.substring(0, sourceFile.indexOf("$")) + ".java";
                }
                KogitoCompilationProvider.classToSource.put(path, Paths.get(sourceFile));
            }
        }
    }

    private Path writeFile(String fileName, byte[] data) throws IOException {
        Path path = Paths.get(fileName);
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        Files.write(path, data);

        return path;
    }

    private ApplicationGenerator createApplicationGenerator(AppPaths appPaths, CombinedIndexBuildItem combinedIndexBuildItem) throws IOException {

        boolean usePersistence = combinedIndexBuildItem.getIndex()
                .getClassByName(createDotName(persistenceFactoryClass)) != null;
        boolean useMonitoring = combinedIndexBuildItem.getIndex()
                .getClassByName(createDotName(metricsClass)) != null;
        boolean useTracing = !combinedIndexBuildItem.getIndex()
                .getAllKnownSubclasses(createDotName(tracingClass)).isEmpty();
        boolean useKnativeEventing = combinedIndexBuildItem.getIndex()
                .getClassByName(createDotName(knativeEventingClass)) != null;

        AddonsConfig addonsConfig = new AddonsConfig()
                .withPersistence(usePersistence)
                .withMonitoring(useMonitoring)
                .withTracing(useTracing)
                .withKnativeEventing(useKnativeEventing);

        GeneratorContext context = buildContext(appPaths, combinedIndexBuildItem.getIndex());

        ApplicationGenerator appGen = new ApplicationGenerator(appPackageName, new File(appPaths.getFirstProjectPath().toFile(), "target"))
                .withDependencyInjection(new CDIDependencyInjectionAnnotator())
                .withAddons(addonsConfig)
                .withGeneratorContext(context);

        addProcessGenerator(appPaths, addonsConfig, appGen);
        addRuleGenerator(appPaths, appGen, addonsConfig);
        addPredictionGenerator(appPaths, appGen, addonsConfig);
        addDecisionGenerator(appPaths, appGen, addonsConfig);

        return appGen;
    }

    private void addRuleGenerator(AppPaths appPaths, ApplicationGenerator appGen, AddonsConfig addonsConfig) throws IOException {
        IncrementalRuleCodegen generator = appPaths.isJar ?
                IncrementalRuleCodegen.ofJar(appPaths.getJarPath()) :
                IncrementalRuleCodegen.ofPath(appPaths.getSourcePaths());

        appGen.withGenerator(generator)
                .withKModule(findKieModuleModel(appPaths))
                .withAddons(addonsConfig)
                .withClassLoader(Thread.currentThread().getContextClassLoader());
    }

    private KieModuleModel findKieModuleModel( AppPaths appPaths ) throws IOException {
        for ( Path resourcePath : appPaths.getResourcePaths() ) {
            Path moduleXmlPath = resourcePath.resolve( KogitoKieModuleModelImpl.KMODULE_JAR_PATH );
            if ( Files.exists( moduleXmlPath ) ) {
                return KogitoKieModuleModelImpl.fromXML(
                        new ByteArrayInputStream(
                                Files.readAllBytes(moduleXmlPath)));
            }
        }

        return KogitoKieModuleModelImpl.fromXML(
                "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>");
    }

    private void addProcessGenerator(AppPaths appPaths, AddonsConfig addonsConfig, ApplicationGenerator appGen) throws IOException {
        ProcessCodegen generator = appPaths.isJar ?
                ProcessCodegen.ofJar(appPaths.getJarPath()) :
                ProcessCodegen.ofPath(appPaths.getProjectPaths());

        appGen.withGenerator(generator)
                .withAddons(addonsConfig)
                .withClassLoader(Thread.currentThread().getContextClassLoader());
    }

    private void addPredictionGenerator(AppPaths appPaths, ApplicationGenerator appGen, AddonsConfig addonsConfig) throws IOException {
        PredictionCodegen generator = appPaths.isJar ?
                PredictionCodegen.ofJar(appPaths.getJarPath()) :
                PredictionCodegen.ofPath(appPaths.getResourcePaths());

        appGen.withGenerator(generator.withAddons(addonsConfig));
    }

    private void addDecisionGenerator( AppPaths appPaths, ApplicationGenerator appGen, AddonsConfig addonsConfig ) throws IOException {
        DecisionCodegen generator = appPaths.isJar ?
                DecisionCodegen.ofJar(appPaths.getJarPath()) :
                DecisionCodegen.ofPath(appPaths.getResourcePaths());

        appGen.withGenerator(generator.withAddons(addonsConfig));
    }

    private String toRuntimeSource(String className) {
        return "src/main/java/" + className.replace('.', '/') + ".java";
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

    private void writeGeneratedFile(GeneratedFile f, String location) throws IOException {
        if (location == null) {
            return;
        }

        String generatedClassFile = f.relativePath().replace("src/main/java", "");
        Files.write(
                pathOf(location, generatedClassFile),
                f.contents());
    }

    private Path pathOf(String location, String end) {
        Path path = Paths.get(location, end);
        path.getParent().toFile().mkdirs();
        return path;
    }

    private DotName createDotName(String name) {
        int lastDot = name.indexOf('.');
        if (lastDot < 0) {
            return DotName.createComponentized(null, name);
        }

        DotName lastDotName = null;
        while (lastDot > 0) {
            String local = name.substring(0, lastDot);
            name = name.substring(lastDot + 1);
            lastDot = name.indexOf('.');
            lastDotName = DotName.createComponentized(lastDotName, local);
        }

        int lastDollar = name.indexOf('$');
        if (lastDollar < 0) {
            return DotName.createComponentized(lastDotName, name);
        }
        DotName lastDollarName = null;
        while (lastDollar > 0) {
            String local = name.substring(0, lastDollar);
            name = name.substring(lastDollar + 1);
            lastDollar = name.indexOf('$');
            if (lastDollarName == null) {
                lastDollarName = DotName.createComponentized(lastDotName, local);
            } else {
                lastDollarName = DotName.createComponentized(lastDollarName, local, true);
            }
        }
        return DotName.createComponentized(lastDollarName, name, true);
    }

    private GeneratorContext buildContext(AppPaths appPaths, IndexView index) {
        GeneratorContext context = GeneratorContext.ofResourcePath(appPaths.getResourceFiles());
        context.withBuildContext(new QuarkusKogitoBuildContext(className -> {
            DotName classDotName = createDotName(className);
            return !index.getAnnotations(classDotName).isEmpty() || index.getClassByName(classDotName) != null;

        }));

        return context;
    }

    private static class AppPaths {

        private final Set<Path> projectPaths = new LinkedHashSet<>();
        private final List<Path> classesPaths = new ArrayList<>();

        private boolean isJar = false;

        private AppPaths(PathsCollection paths) {
            for (Path path : paths) {
                PathType pathType = getPathType(path);
                switch (pathType) {
                    case CLASSES: {
                        classesPaths.add(path);
                        projectPaths.add(path.getParent().getParent());
                        break;
                    }
                    case TEST_CLASSES: {
                        projectPaths.add(path.getParent().getParent());
                        break;
                    }
                    case JAR: {
                        isJar = true;
                        classesPaths.add(path);
                        projectPaths.add(path.getParent().getParent());
                        break;
                    }
                    case UNKNOWN: {
                        classesPaths.add(path);
                        projectPaths.add(path);
                        break;
                    }
                }
            }
        }

        public Path getFirstProjectPath() {
            return projectPaths.iterator().next();
        }

        public Path getFirstClassesPath() {
            return classesPaths.get(0);
        }

        public Path[] getJarPath() {
            if (!isJar) {
                throw new IllegalStateException("Not a jar");
            }
            return classesPaths.toArray(new Path[classesPaths.size()]);
        }

        public File[] getResourceFiles() {
            return projectPaths.stream().map(p -> p.resolve("src/main/resources").toFile()).toArray(File[]::new);
        }

        public Path[] getResourcePaths() {
            return transformPaths(projectPaths, p -> p.resolve("src/main/resources"));
        }

        public Path[] getSourcePaths() {
            return transformPaths(projectPaths, p -> p.resolve("src"));
        }

        public Path[] getProjectPaths() {
            return transformPaths(projectPaths, Function.identity());
        }

        private Path[] transformPaths(Collection<Path> paths, Function<Path, Path> f) {
            return paths.stream().map(f).toArray(Path[]::new);
        }

        private PathType getPathType(Path archiveLocation) {
            String path = archiveLocation.toString();
            if (path.endsWith("target" + File.separator + "classes")) {
                return PathType.CLASSES;
            }
            if (path.endsWith("target" + File.separator + "test-classes")) {
                return PathType.TEST_CLASSES;
            }
            // Quarkus generates a file with extension .jar.original when doing a native compilation of a uberjar
            // TODO replace ".jar.original" with constant JarResultBuildStep.RENAMED_JAR_EXTENSION when it will be avialable in Quakrus 1.7
            if (path.endsWith(".jar") || path.endsWith(".jar.original")) {
                return PathType.JAR;
            }
            return PathType.UNKNOWN;
        }

        private enum PathType {
            CLASSES, TEST_CLASSES, JAR, UNKNOWN
        }
    }
}
