package org.kie.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.resolver.filter.CumulativeScopeArtifactFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.submarine.codegen.ApplicationGenerator;
import org.kie.submarine.codegen.GeneratedFile;
import org.kie.submarine.codegen.process.ProcessCodegen;
import org.kie.submarine.codegen.rules.RuleCodegen;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;

@Mojo(name = "generateModel",
        requiresDependencyResolution = ResolutionScope.NONE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class GenerateModelMojo extends AbstractKieMojo {

    public static PathMatcher drlFileMatcher = FileSystems.getDefault().getPathMatcher("glob:**.drl");

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

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/submarine")
    private File generatedSources;

    @Parameter(property = "generateModel", defaultValue = "no")
    private String generateModel;

    @Parameter(property = "generateProcessModel", defaultValue = "yes")
    private String generateProcessModel;

    @Parameter(property = "dependencyInjection", defaultValue = "true")
    private boolean dependencyInjection;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            generateModel();
        } catch (IOException e) {
            throw new MojoExecutionException("An I/O error occurred", e);
        }
    }

    private void generateModel() throws MojoExecutionException, IOException {
        // these should be probably substituted by boolean params
        boolean generateRuleUnits =
                ExecModelMode.shouldGenerateModel(generateModel);
        boolean generateProcesses =
                BPMNModelMode.shouldGenerateBPMNModel(generateProcessModel);

        project.addCompileSourceRoot(generatedSources.getPath());

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            setSystemProperties(properties);

            ClassLoader projectClassLoader = createProjectClassLoader();
            Thread.currentThread().setContextClassLoader(projectClassLoader);

            ApplicationGenerator appGen = createApplicationGenerator(
                    generateRuleUnits, generateProcesses);

            for (GeneratedFile generatedFile : appGen.generate()) {
                writeGeneratedFile(generatedFile);
            }

            if (ExecModelMode.shouldDeleteFile(generateModel)) {
                deleteDrlFiles();
            }
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    private ApplicationGenerator createApplicationGenerator(boolean generateRuleUnits, boolean generateProcesses) throws IOException {
        String appPackageName = project.getGroupId();
        Path projectPath = projectDir.toPath();

        ApplicationGenerator appGen =
                new ApplicationGenerator(appPackageName, targetDirectory)
                        .withDependencyInjection(dependencyInjection);

        if (generateRuleUnits) {
            appGen.withGenerator(RuleCodegen.ofPath(projectPath, false))
                .withRuleEventListenersConfig(customRuleEventListenerConfigExists(appPackageName));
        }

        if (generateProcesses) {
            appGen.withGenerator(ProcessCodegen.ofPath(projectPath))
                    .withWorkItemHandlerConfig(
                            customWorkItemConfigExists(appPackageName))
                    .withProcessEventListenerConfig(
                            customProcessListenerConfigExists(appPackageName));
        }

        return appGen;
    }

    private String customWorkItemConfigExists(String appPackageName) {
        String sourceDir = Paths.get(projectDir.getPath(), "src").toString();
        String workItemHandlerConfigClass = ProcessCodegen.defaultWorkItemHandlerConfigClass(appPackageName);
        Path p = Paths.get(sourceDir,
                           "main/java",
                           workItemHandlerConfigClass.replace('.', '/') + ".java");
        return Files.exists(p) ? workItemHandlerConfigClass : null;
    }
    private String customProcessListenerConfigExists(String appPackageName) {
        String sourceDir = Paths.get(projectDir.getPath(), "src").toString();
        String processEventListenerClass = ProcessCodegen.defaultProcessListenerConfigClass(appPackageName);
        Path p = Paths.get(sourceDir,
                           "main/java",
                           processEventListenerClass.replace('.', '/') + ".java");
        return Files.exists(p) ? processEventListenerClass : null;
    }
    private String customRuleEventListenerConfigExists(String appPackageName) {
        String sourceDir = Paths.get(projectDir.getPath(), "src").toString();
        String ruleEventListenerConfiglass = RuleCodegen.defaultRuleEventListenerConfigClass(appPackageName);
        Path p = Paths.get(sourceDir,
                           "main/java",
                           ruleEventListenerConfiglass.replace('.', '/') + ".java");
        return Files.exists(p) ? ruleEventListenerConfiglass : null;
    }


    private void writeAll(List<GeneratedFile> generatedFiles) throws IOException {
        for (GeneratedFile f : generatedFiles) {
            writeGeneratedFile(f);
        }
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

    private ClassLoader createProjectClassLoader() throws MojoExecutionException {
        try {
            List<InternalKieModule> kmoduleDeps = new ArrayList<>();

            Set<URL> urls = new HashSet<>();
            for (String element : project.getCompileClasspathElements()) {
                urls.add(new File(element).toURI().toURL());
            }

            project.setArtifactFilter(new CumulativeScopeArtifactFilter(Arrays.asList("compile",
                                                                                      "runtime")));
            for (Artifact artifact : project.getArtifacts()) {
                File file = artifact.getFile();
                if (file != null) {
                    urls.add(file.toURI().toURL());
                    KieModuleModel depModel = getDependencyKieModel(file);
                    if (depModel != null) {
                        ReleaseId releaseId = new ReleaseIdImpl(artifact.getGroupId(),
                                                                artifact.getArtifactId(),
                                                                artifact.getVersion());
                        kmoduleDeps.add(new ZipKieModule(releaseId,
                                                         depModel,
                                                         file));
                    }
                }
            }
            urls.add(outputDirectory.toURI().toURL());

            return URLClassLoader.newInstance(urls.toArray(new URL[0]),
                                              getClass().getClassLoader());
        } catch (DependencyResolutionRequiredException | MalformedURLException e) {
            throw new MojoExecutionException("Error setting up Kie ClassLoader", e);
        }
    }

    private void deleteDrlFiles() throws MojoExecutionException {
        // Remove drl files
        try {
            final Stream<Path> drlFiles = Files.find(outputDirectory.toPath(), Integer.MAX_VALUE, (p, f) -> drlFileMatcher.matches(p));
            drlFiles.forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Unable to delete file " + p);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            throw new MojoExecutionException("Unable to find .drl files");
        }
    }

    private KieModuleModel getDependencyKieModel(File jar) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(jar);
            ZipEntry zipEntry = zipFile.getEntry(KieModuleModelImpl.KMODULE_JAR_PATH);
            if (zipEntry != null) {
                KieModuleModel kieModuleModel = KieModuleModelImpl.fromXML(zipFile.getInputStream(zipEntry));
                setDefaultsforEmptyKieModule(kieModuleModel);
                return kieModuleModel;
            }
        } catch (Exception e) {
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
