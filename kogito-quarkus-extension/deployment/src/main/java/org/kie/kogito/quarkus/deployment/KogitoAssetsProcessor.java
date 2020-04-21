package org.kie.kogito.quarkus.deployment;

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
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.bootstrap.BootstrapDependencyProcessingException;
import io.quarkus.bootstrap.model.AppDependency;
import io.quarkus.bootstrap.model.AppModel;
import io.quarkus.builder.item.BuildItem;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ArchiveRootBuildItem;
import io.quarkus.deployment.builditem.CapabilityBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyIgnoreWarningBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.deployment.index.IndexingUtil;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.runtime.LaunchMode;
import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompilerSettings;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.base.ClassFieldAccessorFactory;
import org.drools.modelcompiler.builder.JavaParserCompiler;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.CompositeIndex;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.MethodInfo;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.internal.jci.CompilationProblem;
import org.kie.internal.kogito.codegen.Generated;
import org.kie.internal.kogito.codegen.VariableInfo;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.GeneratorContext;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.decision.DecisionCodegen;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.kie.kogito.codegen.process.ProcessCodegen;
import org.kie.kogito.codegen.process.persistence.PersistenceGenerator;
import org.kie.kogito.codegen.rules.IncrementalRuleCodegen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KogitoAssetsProcessor {

    private static final String generatedDashboardsDir = "/target/resources/";
    private static final Logger logger = LoggerFactory.getLogger(KogitoAssetsProcessor.class);
    private final transient String generatedClassesDir = System.getProperty("quarkus.debug.generated-classes-dir");
    private final transient String appPackageName = "org.kie.kogito.app";
    private final transient String persistenceFactoryClass = "org.kie.kogito.persistence.KogitoProcessInstancesFactory";
    private final transient String metricsClass = "org.kie.kogito.monitoring.rest.MetricsResource";


    @BuildStep
    CapabilityBuildItem capability() {
        return new CapabilityBuildItem("kogito");
    }

    @BuildStep
    FeatureBuildItem featureBuildItem() {
        return new FeatureBuildItem("kogito");
    }

    public void generatePersistenceInfo(ArchiveRootBuildItem root,
            BuildProducer<GeneratedBeanBuildItem> generatedBeans,
            IndexView index,
            LaunchModeBuildItem launchMode,
            BuildProducer<NativeImageResourceBuildItem> resource,
            CurateOutcomeBuildItem curateOutcomeBuildItem) throws IOException, BootstrapDependencyProcessingException {

        Path projectPath = getProjectPath(root.getArchiveLocation());
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

        GeneratorContext context = buildContext(projectPath, index);

        Collection<ClassInfo> modelClasses = index
                .getAllKnownImplementors(createDotName(Model.class.getCanonicalName()));

        PersistenceGenerator persistenceGenerator = new PersistenceGenerator(new File(projectPath.toFile(), "target"),
                modelClasses, usePersistence,
                new JandexProtoGenerator(index, createDotName(Generated.class.getCanonicalName()),
                        createDotName(VariableInfo.class.getCanonicalName())),
                parameters);
        persistenceGenerator.setDependencyInjection(new CDIDependencyInjectionAnnotator());
        persistenceGenerator.setPackageName(appPackageName);
        persistenceGenerator.setContext(context);

        Collection<GeneratedFile> generatedFiles = persistenceGenerator.generate();

        if (!generatedFiles.isEmpty()) {
            MemoryFileSystem trgMfs = new MemoryFileSystem();
            CompilationResult result = compile(root, trgMfs, curateOutcomeBuildItem.getEffectiveModel(), generatedFiles,
                    launchMode.getLaunchMode(),
                    root.getArchiveLocation());
            register(trgMfs, generatedBeans, (className, data) -> new GeneratedBeanBuildItem(className, data),
                    launchMode.getLaunchMode(), result, root.getArchiveLocation());
        }

        if (usePersistence) {
            resource.produce(new NativeImageResourceBuildItem("kogito-types.proto"));
        }
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

    @BuildStep(loadsApplicationClasses = true)
    public void generateModel(ArchiveRootBuildItem root,
            BuildProducer<GeneratedBeanBuildItem> generatedBeans,
            CombinedIndexBuildItem combinedIndexBuildItem,
            LaunchModeBuildItem launchMode,
            LiveReloadBuildItem liveReload,
            BuildProducer<NativeImageResourceBuildItem> resource,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            CurateOutcomeBuildItem curateOutcomeBuildItem) throws IOException, BootstrapDependencyProcessingException {

        if (liveReload.isLiveReload()) {
            return;
        }

        Path targetClassesPath = root.getArchiveLocation();
        Path projectPath = getProjectPath(targetClassesPath);

        ApplicationGenerator appGen = createApplicationGenerator(projectPath, launchMode.getLaunchMode(), combinedIndexBuildItem);
        Collection<GeneratedFile> generatedFiles = appGen.generate();

        Collection<GeneratedFile> javaFiles = generatedFiles.stream().filter( f -> f.relativePath().endsWith( ".java" ) ).collect( Collectors.toCollection( ArrayList::new ));
        Collection<GeneratedFile> resourceFiles = generatedFiles.stream().filter( f -> f.getType() == GeneratedFile.Type.RESOURCE ).collect( Collectors.toCollection( ArrayList::new ));

        String resourcePath = Paths.get(projectPath.toString()).toString() + generatedDashboardsDir;
        writeResourceFiles(resourcePath, resourceFiles);

        if (!javaFiles.isEmpty()) {

            Indexer kogitoIndexer = new Indexer();
            Set<DotName> kogitoIndex = new HashSet<>();

            MemoryFileSystem trgMfs = new MemoryFileSystem();
            CompilationResult result = compile(root, trgMfs, curateOutcomeBuildItem.getEffectiveModel(), javaFiles,
                    launchMode.getLaunchMode(),
                    targetClassesPath);
            register(trgMfs, generatedBeans, (className, data) -> {

                IndexingUtil.indexClass(className, kogitoIndexer, combinedIndexBuildItem.getIndex(), kogitoIndex,
                        Thread.currentThread().getContextClassLoader(),
                        data);
                return new GeneratedBeanBuildItem(className, data);
            },
                    launchMode.getLaunchMode(), result, targetClassesPath);

            Index index = kogitoIndexer.complete();

            generatePersistenceInfo(root, generatedBeans, CompositeIndex.create(combinedIndexBuildItem.getIndex(), index),
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

        }

    }

    private void writeResourceFiles(String projectPath, Collection<GeneratedFile> resourceFiles){
        resourceFiles.forEach(f -> {
            try {
                writeGeneratedFile(f, projectPath);
            } catch (IOException e) {
                logger.warn(String.format("Could not write resource file %s", f.toString()), e);
            }
        });
    }

    private Path getProjectPath(Path archiveLocation) {
        //TODO: revisit this, we should not be depending on a project, it breaks the upgrade use case
        String path = archiveLocation.toString();
        if (path.endsWith("target" + File.separator + "classes")) {
            return archiveLocation.getParent().getParent();
        } else if (path.endsWith(".jar") && archiveLocation.getParent().getFileName().toString().equals("target")) {
            return archiveLocation.getParent().getParent();
        }
        return archiveLocation;
    }

    private CompilationResult compile(ArchiveRootBuildItem root, MemoryFileSystem trgMfs,
            AppModel appModel,
            Collection<GeneratedFile> generatedFiles,
            LaunchMode launchMode, Path projectPath)
            throws IOException, BootstrapDependencyProcessingException {

        JavaCompiler javaCompiler = JavaParserCompiler.getCompiler();
        JavaCompilerSettings compilerSettings = javaCompiler.createDefaultSettings();
        compilerSettings.addOption("-proc:none"); // force disable annotation processing
        compilerSettings.addClasspath(root.getArchiveLocation().toString());
        if (appModel != null) {
            for (AppDependency i : appModel.getUserDependencies()) {
                compilerSettings.addClasspath( i.getArtifact().getPath().toAbsolutePath().toString() );
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
                location = Paths.get(projectPath.toString()).toString();
            }

            writeGeneratedFile(entry, location);

        }

        return javaCompiler.compile(sources, srcMfs, trgMfs,
                Thread.currentThread().getContextClassLoader(), compilerSettings);
    }

    private void register(MemoryFileSystem trgMfs, BuildProducer generatedBeans,
            BiFunction<String, byte[], ? extends BuildItem> bif, LaunchMode launchMode,
            CompilationResult result, Path projectPath) throws IOException {
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
                Path path = writeFile(Paths.get(projectPath.toString(), fileName).toString(), data);

                String sourceFile = path.toString().replaceFirst("\\.class", ".java");
                if (sourceFile.contains("$")) {
                    sourceFile = sourceFile.substring(0, sourceFile.indexOf("$")) + ".java";
                }
                KogitoCompilationProvider.classToSource.put(path,
                        Paths.get(sourceFile));
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

    private ApplicationGenerator createApplicationGenerator(Path projectPath, LaunchMode launchMode, CombinedIndexBuildItem combinedIndexBuildItem)
            throws IOException {

        boolean usePersistence = combinedIndexBuildItem.getIndex()
                .getClassByName(createDotName(persistenceFactoryClass)) != null;
        boolean useMonitoring = combinedIndexBuildItem.getIndex()
                .getClassByName(createDotName(metricsClass)) != null;

        GeneratorContext context = buildContext(projectPath, combinedIndexBuildItem.getIndex());

        ApplicationGenerator appGen = new ApplicationGenerator(appPackageName, new File(projectPath.toFile(), "target"))
                .withDependencyInjection(new CDIDependencyInjectionAnnotator())
                .withPersistence(usePersistence)
                .withMonitoring(useMonitoring)
                .withGeneratorContext(context);

        boolean isDirectory = projectPath.toFile().isDirectory();
        addProcessGenerator( projectPath, usePersistence, appGen, isDirectory);
        addRuleGenerator( projectPath, appGen, isDirectory, useMonitoring );
        addDecisionGenerator( projectPath, appGen, isDirectory, useMonitoring );

        return appGen;
    }

    private void addRuleGenerator( Path projectPath, ApplicationGenerator appGen, boolean isDirectory, boolean useMonitoring ) throws IOException {
        Path moduleXmlPath = projectPath.resolve("src/main/resources").resolve( KieModuleModelImpl.KMODULE_JAR_PATH);
        KieModuleModel kieModuleModel = null;
        if ( Files.exists(moduleXmlPath)) {
            kieModuleModel = KieModuleModelImpl.fromXML(
                    new ByteArrayInputStream(
                            Files.readAllBytes(moduleXmlPath)));
        } else {
            kieModuleModel = KieModuleModelImpl.fromXML(
                    "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>");
        }

        IncrementalRuleCodegen generator = isDirectory ?
                IncrementalRuleCodegen.ofPath(projectPath.resolve("src")) :
                IncrementalRuleCodegen.ofJar(projectPath);

        appGen.withGenerator(generator)
                .withKModule(kieModuleModel)
                .withMonitoring(useMonitoring)
                .withClassLoader(Thread.currentThread().getContextClassLoader());
    }

    private void addProcessGenerator( Path projectPath, boolean usePersistence, ApplicationGenerator appGen, boolean isDirectory ) throws IOException {
        ProcessCodegen generator = isDirectory ?
                ProcessCodegen.ofPath(projectPath) :
                ProcessCodegen.ofJar(projectPath);

        appGen.withGenerator( generator )
                .withPersistence(usePersistence)
                .withClassLoader(Thread.currentThread().getContextClassLoader());
    }

    private void addDecisionGenerator( Path projectPath, ApplicationGenerator appGen, boolean isDirectory, boolean useMonitoring ) throws IOException {
        DecisionCodegen generator = isDirectory ?
                DecisionCodegen.ofPath(projectPath.resolve("src/main/resources")) :
                DecisionCodegen.ofJar(projectPath);

        appGen.withGenerator( generator )
                .withMonitoring(useMonitoring);
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

    private GeneratorContext buildContext(Path projectPath, IndexView index) {
    	GeneratorContext context = GeneratorContext.ofResourcePath(projectPath.resolve("src/main/resources").toFile());
        context.withBuildContext(new QuarkusKogitoBuildContext(className -> {
                DotName classDotName = createDotName(className);
                return !index.getAnnotations(classDotName).isEmpty() || index.getClassByName(classDotName) != null;

        }));

        return context;
    }

}
