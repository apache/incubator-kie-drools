package org.kie.kogito.maven.plugin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.maven.model.Resource;
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
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.SpringDependencyInjectionAnnotator;
import org.kie.kogito.codegen.process.ProcessCodegen;
import org.kie.kogito.codegen.rules.IncrementalRuleCodegen;
import org.kie.kogito.codegen.rules.RuleCodegen;
import org.kie.kogito.maven.plugin.util.MojoUtil;

@Mojo(name = "generateModel",
        requiresDependencyResolution = ResolutionScope.NONE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class GenerateModelMojo extends AbstractKieMojo {

    private static final String MAIN_JAVA_DIRECTORY = "main/java";
    private static final String JAVA_FILE_SUFFIX = ".java";

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

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/kogito")
    private File generatedSources;

    // due to a limitation of the injector, the following 2 params have to be Strings
    // otherwise we cannot get the default value to null
    // when the value is null, the semantics is to enable the corresponding
    // codegen backend only if at least one file of the given type exist

    @Parameter(property = "kogito.codegen.rules", defaultValue = "")
    private String generateRules; // defaults to true iff there exist DRL files

    @Parameter(property = "kogito.codegen.processes", defaultValue = "")
    private String generateProcesses; // defaults to true iff there exist BPMN files

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

    @Parameter(property = "kogito.di.enabled", defaultValue = "true")
    private boolean dependencyInjection;

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
        // if unspecified, then default to checking for file type existence
        // if not null, the property has been overridden, and we should use the specified value
        boolean genRules = generateRules == null ? rulesExist() : Boolean.parseBoolean(generateRules);
        boolean genProcesses = generateProcesses == null ? processesExist() : Boolean.parseBoolean(generateProcesses);

        project.addCompileSourceRoot(generatedSources.getPath());

        setSystemProperties(properties);

        ApplicationGenerator appGen = createApplicationGenerator(
                genRules, genProcesses);

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

    private boolean processesExist() throws IOException {
        try (final Stream<Path> paths = Files.walk(projectDir.toPath())) {
            return paths.map(p -> p.toString().toLowerCase())
                    .anyMatch(p -> p.endsWith(".bpmn") || p.endsWith(".bpmn2"));
        }
    }

    private boolean rulesExist() throws IOException {
        try (final Stream<Path> paths = Files.walk(projectDir.toPath())) {
            return paths.anyMatch(p -> p.toString().toLowerCase().endsWith(".drl"));
        }
    }

    private ApplicationGenerator createApplicationGenerator(boolean generateRuleUnits, boolean generateProcesses) throws IOException, MojoExecutionException {
        String appPackageName = project.getGroupId();
        Path projectPath = projectDir.toPath();
        // safe guard to not generate application classes that would clash with interfaces
        if (appPackageName.equals(ApplicationGenerator.DEFAULT_GROUP_ID)) {
            appPackageName = ApplicationGenerator.DEFAULT_PACKAGE_NAME;
        }

        ApplicationGenerator appGen =
                new ApplicationGenerator(appPackageName, targetDirectory)
                        .withDependencyInjection(discoverDependencyInjectionAnnotator());

        if (generateRuleUnits) {
            appGen.withGenerator(IncrementalRuleCodegen.ofPath(kieSourcesDirectory.toPath()))
                    .withKModule(getKModuleModel())
                    .withClassLoader(MojoUtil.createProjectClassLoader(this.getClass().getClassLoader(),
                                                                       project,
                                                                       outputDirectory,
                                                                       null))
                    .withRuleEventListenersConfig(customRuleEventListenerConfigExists(appPackageName));
        }

        if (generateProcesses) {
            appGen.withGenerator(ProcessCodegen.ofPath(kieSourcesDirectory.toPath()))
                    .withWorkItemHandlerConfig(
                            customWorkItemConfigExists(appPackageName))
                    .withProcessEventListenerConfig(
                            customProcessListenerConfigExists(appPackageName));
        }

        return appGen;
    }

    private KieModuleModel getKModuleModel() throws IOException {
        for (Resource resource : project.getResources()) {
            Path moduleXmlPath = Paths.get(resource.getDirectory()).resolve(KieModuleModelImpl.KMODULE_JAR_PATH);
            return KieModuleModelImpl.fromXML(
                    new ByteArrayInputStream(
                            Files.readAllBytes(moduleXmlPath)));
        }
        return new KieModuleModelImpl();
    }

    private String customWorkItemConfigExists(String appPackageName) {
        String sourceDir = Paths.get(projectDir.getPath(), "src").toString();
        String workItemHandlerConfigClass = ProcessCodegen.defaultWorkItemHandlerConfigClass(appPackageName);
        Path p = Paths.get(sourceDir,
                           MAIN_JAVA_DIRECTORY,
                           workItemHandlerConfigClass.replace('.', '/') + JAVA_FILE_SUFFIX);
        return p.toFile().exists() ? workItemHandlerConfigClass : null;
    }

    private String customProcessListenerConfigExists(String appPackageName) {
        String sourceDir = Paths.get(projectDir.getPath(), "src").toString();
        String processEventListenerClass = ProcessCodegen.defaultProcessListenerConfigClass(appPackageName);
        Path p = Paths.get(sourceDir,
                           MAIN_JAVA_DIRECTORY,
                           processEventListenerClass.replace('.', '/') + JAVA_FILE_SUFFIX);
        return p.toFile().exists() ? processEventListenerClass : null;
    }

    private String customRuleEventListenerConfigExists(String appPackageName) {
        String sourceDir = Paths.get(projectDir.getPath(), "src").toString();
        String ruleEventListenerConfiglass = RuleCodegen.defaultRuleEventListenerConfigClass(appPackageName);
        Path p = Paths.get(sourceDir,
                           MAIN_JAVA_DIRECTORY,
                           ruleEventListenerConfiglass.replace('.', '/') + JAVA_FILE_SUFFIX);
        return p.toFile().exists() ? ruleEventListenerConfiglass : null;
    }

    private void writeGeneratedFile(GeneratedFile f) throws IOException {
        Files.write(
                pathOf(f.relativePath()),
                f.contents());
    }

    private Path pathOf(String end) {
        Path path = Paths.get(generatedSources.getPath(), end);
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

    protected DependencyInjectionAnnotator discoverDependencyInjectionAnnotator() {
        if (!dependencyInjection) {
            return null;
        }

        boolean hasSpring = project.getDependencies().stream().anyMatch(d -> d.getArtifactId().contains("spring"));
        if (hasSpring) {
            return new SpringDependencyInjectionAnnotator();
        }

        boolean hasQuarkus = project.getDependencies().stream().anyMatch(d -> d.getArtifactId().contains("quarkus"));
        if (hasQuarkus) {
            return new CDIDependencyInjectionAnnotator();
        }

        throw new IllegalStateException("Unable to find dependency injection annotator");
    }
}
