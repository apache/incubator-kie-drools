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
import java.util.stream.Stream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.GeneratorContext;
import org.kie.kogito.codegen.decision.DecisionCodegen;
import org.kie.kogito.codegen.process.ProcessCodegen;
import org.kie.kogito.codegen.rules.IncrementalRuleCodegen;
import org.kie.kogito.maven.plugin.util.MojoUtil;

@Mojo(name = "generateModel",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
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

    /**
     * Partial generation can be used when reprocessing a pre-compiled project
     * for faster code-generation. It only generates code for rules and processes,
     * and does not generate extra meta-classes (etc. Application).
     * Use only when doing recompilation and for development purposes
     */
    @Parameter(property = "kogito.codegen.partial", defaultValue = "false")
    private boolean generatePartial;

    @Parameter(property = "kogito.sources.keep", defaultValue = "false")
    private boolean keepSources;

    @Parameter(property = "kogito.persistence.enabled", defaultValue = "false")
    private boolean persistence;

    @Parameter(required = true, defaultValue = "${project.basedir}/src/main/resources")
    private File kieSourcesDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            generateModel();
        } catch (IOException e) {
            throw new MojoExecutionException("An I/O error occurred", e);
        }
    }

    private void generateModel() throws MojoExecutionException, IOException {
        project.addCompileSourceRoot(customizableSources.getPath());
        project.addCompileSourceRoot(generatedSources.getPath());

        setSystemProperties(properties);

        ApplicationGenerator appGen = createApplicationGenerator();

        Collection<GeneratedFile> generatedFiles;
        if (generatePartial) {
            generatedFiles = appGen.generateComponents();
        } else {
            generatedFiles = appGen.generate();
        }

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
                        int dot = p.lastIndexOf( '.' );
                        return dot > 0 ? p.substring( dot ) : "";
                    })
                    .anyMatch( DROOLS_EXTENSIONS::contains );
        }
    }

    private ApplicationGenerator createApplicationGenerator() throws IOException, MojoExecutionException {
        String appPackageName = project.getGroupId();

        // safe guard to not generate application classes that would clash with interfaces
        if (appPackageName.equals(ApplicationGenerator.DEFAULT_GROUP_ID)) {
            appPackageName = ApplicationGenerator.DEFAULT_PACKAGE_NAME;
        }

        boolean usePersistence = persistence || hasClassOnClasspath(project, "org.kie.kogito.persistence.KogitoProcessInstancesFactory");
        boolean useMonitoring = hasClassOnClasspath(project, "org.kie.kogito.monitoring.rest.MetricsResource");
        boolean useTracing = hasClassOnClasspath(project, "org.kie.kogito.tracing.decision.DecisionTracingListener");

        ClassLoader projectClassLoader = MojoUtil.createProjectClassLoader(this.getClass().getClassLoader(),
                                                                           project,
                                                                           outputDirectory,
                                                                           null);

        GeneratorContext context = GeneratorContext.ofResourcePath(kieSourcesDirectory);
        context.withBuildContext(discoverKogitoRuntimeContext(project));

        ApplicationGenerator appGen =
                new ApplicationGenerator(appPackageName, targetDirectory)
                        .withDependencyInjection(discoverDependencyInjectionAnnotator(project))
                        .withPersistence(usePersistence)
                        .withMonitoring(useMonitoring)
                        .withClassLoader(projectClassLoader)
                        .withGeneratorContext(context);

        // if unspecified, then default to checking for file type existence
        // if not null, the property has been overridden, and we should use the specified value

        if (generateProcesses()) {
            appGen.withGenerator(ProcessCodegen.ofPath(kieSourcesDirectory.toPath()))
                    .withPersistence(usePersistence)
                    .withClassLoader(projectClassLoader);
        }

        if (generateRules()) {
            boolean useRestServices = hasClassOnClasspath(project, "javax.ws.rs.Path");
            appGen.withGenerator(IncrementalRuleCodegen.ofPath(kieSourcesDirectory.toPath()))
                    .withKModule(getKModuleModel())
                    .withClassLoader(projectClassLoader)
                    .withMonitoring(useMonitoring)
                    .withRestServices(useRestServices);
        }

        if (generateDecisions()) {
            appGen.withGenerator(DecisionCodegen.ofPath(kieSourcesDirectory.toPath()))
                    .withTracing(useTracing)
                    .withMonitoring(useMonitoring);
        }

        return appGen;
    }

    private KieModuleModel getKModuleModel() throws IOException {
        if (!project.getResources().isEmpty()) {
            Path moduleXmlPath = Paths.get(project.getResources().get(0).getDirectory()).resolve(KieModuleModelImpl.KMODULE_JAR_PATH);
            try {
                return KieModuleModelImpl.fromXML(
                        new ByteArrayInputStream(
                                Files.readAllBytes(moduleXmlPath)));
            } catch (NoSuchFileException e) {
                getLog().debug("kmodule.xml is missing. Returned the default value.", e);
                return new KieModuleModelImpl();
            }
        } else {
            getLog().debug("kmodule.xml is missing. Returned the default value.");
            return new KieModuleModelImpl();
        }
    }

    private void writeGeneratedFile(GeneratedFile f) throws IOException {
        Files.write( pathOf(f), f.contents() );
    }

    private Path pathOf(GeneratedFile f) {
        File sourceFolder = f.getType().isCustomizable() ? customizableSources : generatedSources;
        Path path = Paths.get(sourceFolder.getPath(), f.relativePath());
        path.getParent().toFile().mkdirs();
        return path;
    }

    private void deleteDrlFiles() throws MojoExecutionException {
        // Remove drl files
        try (final Stream<Path> drlFiles = Files.find(outputDirectory.toPath(), Integer.MAX_VALUE, (p, f) -> drlFileMatcher.matches(p))) {
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
