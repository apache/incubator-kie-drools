/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.maven.plugin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.builder.impl.KogitoKieModuleModelImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.GeneratedFile.Type;
import org.kie.kogito.codegen.GeneratorContext;
import org.kie.kogito.codegen.DashboardGeneratedFileUtils;
import org.kie.kogito.codegen.decision.DecisionCodegen;
import org.kie.kogito.codegen.io.CollectedResource;
import org.kie.kogito.codegen.prediction.PredictionCodegen;
import org.kie.kogito.codegen.process.ProcessCodegen;
import org.kie.kogito.codegen.rules.IncrementalRuleCodegen;
import org.kie.kogito.maven.plugin.util.MojoUtil;

@Mojo(name = "generateModel",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE,
        threadSafe = true)
public class GenerateModelMojo extends AbstractKieMojo {

    public static final List<String> DROOLS_EXTENSIONS = Arrays.asList(".drl", ".xls", ".xlsx", ".csv");

    public static final PathMatcher drlFileMatcher = FileSystems.getDefault().getPathMatcher("glob:**.drl");

    @Parameter(required = true, defaultValue = "${project.build.directory}")
    private File targetDirectory;

    @Parameter(required = true, defaultValue = "${project.basedir}")
    private File projectDir;

    @Parameter(required = true, defaultValue = "${project.build.testSourceDirectory}")
    private File testDir;

    @Parameter
    private Map<String, String> properties;

    @Parameter(required = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(required = true, defaultValue = "${project.build.outputDirectory}")
    private File outputDirectory;

    @Parameter(property = "kogito.codegen.sources.directory", defaultValue = "${project.build.directory}/generated-sources/kogito")
    private File customizableSources;

    @Parameter(readonly = true, defaultValue = "${project.build.directory}/generated-sources/kogito")
    private File generatedSources;

    // due to a limitation of the injector, the following 2 params have to be Strings
    // otherwise we cannot get the default value to null
    // when the value is null, the semantics is to enable the corresponding
    // codegen backend only if at least one file of the given type exist

    @Parameter(property = "kogito.codegen.rules", defaultValue = "")
    private String generateRules; // defaults to true iff there exist DRL files

    @Parameter(property = "kogito.codegen.processes", defaultValue = "")
    private String generateProcesses; // defaults to true iff there exist BPMN files

    @Parameter(property = "kogito.codegen.decisions", defaultValue = "")
    private String generateDecisions; // defaults to true iff there exist DMN files

    @Parameter(property = "kogito.codegen.predictions", defaultValue = "")
    private String generatePredictions; // defaults to true iff there exist PMML files

    /**
     * Partial generation can be used when reprocessing a pre-compiled project
     * for faster code-generation. It only generates code for rules and processes,
     * and does not generate extra meta-classes (etc. Application).
     * Use only when doing recompilation and for development purposes
     */
    @Parameter(property = "kogito.codegen.partial", defaultValue = "false")
    private boolean generatePartial;

    @Parameter(property = "kogito.codegen.ondemand", defaultValue = "false")
    private boolean onDemand;

    @Parameter(property = "kogito.sources.keep", defaultValue = "false")
    private boolean keepSources;

    @Parameter(property = "kogito.persistence.enabled", defaultValue = "false")
    private boolean persistence;

    @Parameter(required = true, defaultValue = "${project.basedir}/src/main/resources")
    private File kieSourcesDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            addCompileSourceRoots();
            if (isOnDemand()) {
                getLog().info("On-Demand Mode is On. Use mvn compile kogito:scaffold");
            } else {
                generateModel();
            }
        } catch (IOException e) {
            throw new MojoExecutionException("An I/O error occurred", e);
        }
    }

    protected boolean isOnDemand() {
        return onDemand;
    }

    protected File getCustomizableSources() {
        return customizableSources;
    }

    protected void addCompileSourceRoots() {
        project.addCompileSourceRoot(getCustomizableSources().getPath());
        project.addCompileSourceRoot(generatedSources.getPath());
    }

    protected void generateModel() throws MojoExecutionException, IOException {

        setSystemProperties(properties);

        ApplicationGenerator appGen = createApplicationGenerator();

        Collection<GeneratedFile> generatedFiles;
        if (generatePartial) {
            generatedFiles = appGen.generateComponents();
        } else {
            generatedFiles = appGen.generate();
        }

        Optional<GeneratedFile> dashboardsListFile = DashboardGeneratedFileUtils.list(generatedFiles);
        dashboardsListFile.ifPresent(generatedFiles::add);

        for (GeneratedFile generatedFile : generatedFiles) {
            writeGeneratedFile(generatedFile);
        }

        if (!keepSources) {
            deleteDrlFiles();
        }
    }

    private boolean generateDecisions() throws IOException {
        return generateDecisions == null ? decisionsExist() : Boolean.parseBoolean(generateDecisions);
    }

    private boolean generatePredictions() throws IOException {
        return generatePredictions == null ? predictionsExist() : Boolean.parseBoolean(generatePredictions);
    }

    private boolean generateRules() throws IOException {
        return generateRules == null ? rulesExist() : Boolean.parseBoolean(generateRules);
    }

    private boolean generateProcesses() throws IOException {
        return generateProcesses == null ? processesExist() : Boolean.parseBoolean(generateProcesses);
    }

    private boolean decisionsExist() throws IOException {
        try (final Stream<Path> paths = Files.walk(projectDir.toPath())) {
            return paths.map(p -> p.toString().toLowerCase()).anyMatch(p -> p.endsWith(".dmn"));
        }
    }

    private boolean predictionsExist() throws IOException {
        try (final Stream<Path> paths = Files.walk(projectDir.toPath())) {
            return paths.map(p -> p.toString().toLowerCase()).anyMatch(p -> p.endsWith(".pmml"));
        }
    }

    private boolean processesExist() throws IOException {
        try (final Stream<Path> paths = Files.walk(projectDir.toPath())) {
            return paths.map(p -> p.toString().toLowerCase())
                    .anyMatch(p -> p.endsWith(".bpmn") || p.endsWith(".bpmn2") || p.endsWith(".sw.json") || p.endsWith(".sw.yml"));
        }
    }

    private boolean rulesExist() throws IOException {
        try (final Stream<Path> paths = Files.walk(projectDir.toPath())) {
            return paths.map(p -> p.toString().toLowerCase())
                    .map(p -> {
                        int dot = p.lastIndexOf('.');
                        return dot > 0 ? p.substring(dot) : "";
                    })
                    .anyMatch(DROOLS_EXTENSIONS::contains);
        }
    }

    private ApplicationGenerator createApplicationGenerator() throws IOException, MojoExecutionException {
        String appPackageName = project.getGroupId();

        // safe guard to not generate application classes that would clash with interfaces
        if (appPackageName.equals(ApplicationGenerator.DEFAULT_GROUP_ID)) {
            appPackageName = ApplicationGenerator.DEFAULT_PACKAGE_NAME;
        }

        boolean usePersistence = persistence || hasClassOnClasspath(project, "org.kie.kogito.persistence.KogitoProcessInstancesFactory");
        boolean usePrometheusMonitoring = hasClassOnClasspath(project, "org.kie.kogito.monitoring.prometheus.rest.MetricsResource");
        boolean useMonitoring = usePrometheusMonitoring || hasClassOnClasspath(project, "org.kie.kogito.monitoring.core.MonitoringRegistry");
        boolean useTracing = hasClassOnClasspath(project, "org.kie.kogito.tracing.decision.DecisionTracingListener");
        boolean useKnativeEventing = hasClassOnClasspath(project, "org.kie.kogito.events.knative.ce.extensions.KogitoProcessExtension");
        boolean useCloudEvents = hasClassOnClasspath(project, "org.kie.kogito.addon.cloudevents.AbstractTopicDiscovery");

        AddonsConfig addonsConfig = new AddonsConfig()
                .withPersistence(usePersistence)
                .withMonitoring(useMonitoring)
                .withPrometheusMonitoring(usePrometheusMonitoring)
                .withTracing(useTracing)
                .withKnativeEventing(useKnativeEventing)
                .withCloudEvents(useCloudEvents);

        ClassLoader projectClassLoader = MojoUtil.createProjectClassLoader(this.getClass().getClassLoader(),
                                                                           project,
                                                                           outputDirectory,
                                                                           null);

        GeneratorContext context = GeneratorContext.ofResourcePath(kieSourcesDirectory);
        context.withBuildContext(discoverKogitoRuntimeContext(project));

        ApplicationGenerator appGen =
                new ApplicationGenerator(appPackageName, targetDirectory)
                        .withDependencyInjection(discoverDependencyInjectionAnnotator(project))
                        .withAddons(addonsConfig)
                        .withClassLoader(projectClassLoader)
                        .withGeneratorContext(context);

        // if unspecified, then default to checking for file type existence
        // if not null, the property has been overridden, and we should use the specified value

        if (generateProcesses()) {
            appGen.withGenerator(ProcessCodegen.ofCollectedResources(CollectedResource.fromDirectory(kieSourcesDirectory.toPath())))
                    .withAddons(addonsConfig)
                    .withClassLoader(projectClassLoader);
        }

        if (generateRules()) {
            boolean useRestServices = hasClassOnClasspath(project, "javax.ws.rs.Path")
                    || hasClassOnClasspath(project, "org.springframework.web.bind.annotation.RestController");
            appGen.withGenerator(IncrementalRuleCodegen.ofCollectedResources(CollectedResource.fromDirectory(kieSourcesDirectory.toPath())))
                    .withKModule(getKModuleModel())
                    .withClassLoader(projectClassLoader)
                    .withAddons(addonsConfig)
                    .withRestServices(useRestServices);
        }

        boolean isJPMMLAvailable = hasClassOnClasspath(project, "org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator");
        appGen.withGenerator(PredictionCodegen.ofCollectedResources(isJPMMLAvailable, CollectedResource.fromDirectory(kieSourcesDirectory.toPath())))
                .withAddons(addonsConfig);

        if (generateDecisions()) {
            appGen.withGenerator(DecisionCodegen.ofCollectedResources(CollectedResource.fromDirectory(kieSourcesDirectory.toPath())))
                  .withAddons(addonsConfig)
                  .withClassLoader(projectClassLoader)
                  .withPCLResolverFn(x -> hasClassOnClasspath(project, x));
        }

        return appGen;
    }

    private KieModuleModel getKModuleModel() throws IOException {
        if (!project.getResources().isEmpty()) {
            Path moduleXmlPath = Paths.get(project.getResources().get(0).getDirectory()).resolve(KieModuleModelImpl.KMODULE_JAR_PATH);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(Files.readAllBytes(moduleXmlPath))) {
                return KogitoKieModuleModelImpl.fromXML(bais);
            } catch (NoSuchFileException e) {
                getLog().debug("kmodule.xml is missing. Returned the default value.", e);
                return new KogitoKieModuleModelImpl();
            }
        } else {
            getLog().debug("kmodule.xml is missing. Returned the default value.");
            return new KogitoKieModuleModelImpl();
        }
    }

    private void writeGeneratedFile(GeneratedFile f) throws IOException {
        Files.write(pathOf(f), f.contents());
    }

    private Path pathOf(GeneratedFile f) {
        File sourceFolder;
        Path path;
        if (f.getType() == Type.GENERATED_CP_RESOURCE) { // since kogito-maven-plugin is after maven-resource-plugin, need to manually place in the correct (CP) location
            sourceFolder = outputDirectory;
            path = Paths.get(sourceFolder.getPath(), f.relativePath());
            getLog().info("Generating: " + path);
        } else if (f.getType().isCustomizable()) {
            sourceFolder = getCustomizableSources();
            path = Paths.get(sourceFolder.getPath(), f.relativePath());
            getLog().info("Generating: " + path);
        } else {
            sourceFolder = generatedSources;
            path = Paths.get(sourceFolder.getPath(), f.relativePath());
        }

        path.getParent().toFile().mkdirs();
        return path;
    }

    private void deleteDrlFiles() throws MojoExecutionException {
        // Remove drl files
        try (final Stream<Path> drlFiles = Files.find(outputDirectory.toPath(), Integer.MAX_VALUE,
                                                      (p, f) -> drlFileMatcher.matches(p))) {
            drlFiles.forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to find .drl files");
        }
    }
}
