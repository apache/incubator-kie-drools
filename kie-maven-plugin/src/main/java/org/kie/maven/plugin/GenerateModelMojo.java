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
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.modelcompiler.CanonicalKieModule;
import org.drools.modelcompiler.builder.CanonicalModelKieProject;
import org.drools.modelcompiler.builder.KieModuleModelMethod;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.drools.modelcompiler.builder.ModelWriter;
import org.drools.modelcompiler.builder.ProjectSourceClass;
import org.drools.modelcompiler.builder.generator.ModuleSourceClass;
import org.drools.modelcompiler.builder.generator.RuleUnitInstanceSourceClass;
import org.drools.modelcompiler.builder.generator.RuleUnitSourceClass;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

import static java.util.Collections.singletonList;
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

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/drools-model-compiler")
    private File modelCompilerOutputDirectory;

    @Parameter(defaultValue = "${project.source.directory}")
    private File projectSourceDirectory;

    @Parameter(property = "generateModel", defaultValue = "no")
    private String generateModel;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (ExecModelMode.shouldGenerateModel(generateModel)) {
            generateModel();
        }
    }

    private void generateModel() throws MojoExecutionException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        KieServices ks = KieServices.Factory.get();

        try {
            ClassLoader projectClassLoader = createProjectClassLoader();

            Thread.currentThread().setContextClassLoader(projectClassLoader);

            setSystemProperties(properties);

            final KieBuilderImpl kieBuilder = (KieBuilderImpl) ks.newKieBuilder(projectDir);

            getLog().info("Begin code generation");

            kieBuilder.buildAll(ExecutableModelMavenPluginKieProject::new, s -> {
                return !s.contains("src/test/java");
            });

            InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModule();
            getLog().info("kieBuilder is type: "+kieBuilder.getClass());
            getLog().info("kieModule is type: "+kieModule.getClass());
            MemoryFileSystem mfs = getMemoryFileSystem(kieModule);

            String modelCompilerOutputPath =
                    modelCompilerOutputDirectory.getPath();

            new CanonicalModelWriter(
                    mfs,
                    kieModule.getFileNames(),
                    modelCompilerOutputPath,
                    getLog()).write();

            project.addCompileSourceRoot(modelCompilerOutputPath);

            new ResourceFileWriter(
                    mfs,
                    targetDirectory.getPath()).write();

            if (ExecModelMode.shouldDeleteFile(generateModel)) {
                deleteDrlFiles();
            }
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }

        getLog().info("DSL successfully generated");
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

    private MemoryFileSystem getMemoryFileSystem(InternalKieModule kieModule) {
        return kieModule instanceof CanonicalKieModule ?
                ((MemoryKieModule) ((CanonicalKieModule) kieModule).getInternalKieModule()).getMemoryFileSystem() :
                ((MemoryKieModule) kieModule).getMemoryFileSystem();
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

    private class ExecutableModelMavenPluginKieProject extends CanonicalModelKieProject {

        public ExecutableModelMavenPluginKieProject(InternalKieModule kieModule, ClassLoader classLoader) {
            super(true, kieModule, classLoader);
        }

        @Override
        public void writeProjectOutput(MemoryFileSystem trgMfs, ResultsImpl messages) {
            MemoryFileSystem srcMfs = new MemoryFileSystem();
            List<String> generatedSourceFiles = new ArrayList<>();
            ModelWriter modelWriter = new ModelWriter();
            for (ModelBuilderImpl modelBuilder : modelBuilders) {
                ModelWriter.Result result = modelWriter.writeModel(srcMfs, modelBuilder.getPackageModels());
                generatedSourceFiles.addAll(result.getModelFiles());
            }

            KieModuleModelMethod modelMethod = new KieModuleModelMethod(kBaseModels);
            new org.drools.modelcompiler.builder.ModelSourceClass(
                    getInternalKieModule().getReleaseId(), modelMethod, generatedSourceFiles)
                    .write(srcMfs);
            new ProjectSourceClass(modelMethod)
                    .write(srcMfs);

            srcMfs.copyFolder(srcMfs.getFolder("src/main/java"), trgMfs, trgMfs.getFolder("."));
            writeModelFile(generatedSourceFiles, trgMfs);
        }
    }
}
