package org.kie.kogito.quarkus.deployment;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.quarkus.deployment.dev.JavaCompilationProvider;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.Generator;
import org.kie.kogito.codegen.GeneratorContext;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;

public abstract class KogitoCompilationProvider extends JavaCompilationProvider {

    protected static Map<Path, Path> classToSource = new HashMap<>();    

    private String appPackageName = System.getProperty("kogito.codegen.packageName", "org.kie.kogito.app");

    @Override
    public Set<String> handledSourcePaths() {
        return Collections.singleton("src" + File.separator + "main" + File.separator + "resources");
    }

    @Override
    public final void compile(Set<File> filesToCompile, Context context) {
        // This classloader reads from the file system all the project dependencies, plus the quarkus output directory
        // containing all the latest class definitions of user's pojos, eventually recompiled also during the latest
        // hot reload round. It is also necessary to use a null as a parent classloader otherwise this classloader
        // could load the old definition of a class from the parent instead of getting the latest one from the output directory
        final URLClassLoader cl = new URLClassLoader( getClasspathUrls( context ), null );

        File outputDirectory = context.getOutputDirectory();
        try {
            GeneratorContext generationContext = GeneratorContext
                    .ofResourcePath(context.getProjectDirectory().toPath().resolve("src/main/resources").toFile());
            generationContext
                    .withBuildContext(new QuarkusKogitoBuildContext(className -> hasClassOnClasspath(cl, className)));

            ApplicationGenerator appGen = new ApplicationGenerator(appPackageName, outputDirectory)
                    .withDependencyInjection(new CDIDependencyInjectionAnnotator())
                    .withGeneratorContext(generationContext);

            addGenerator(appGen, filesToCompile, context, cl);

            Collection<GeneratedFile> generatedFiles = appGen.generate();

            Set<File> generatedSourceFiles = new HashSet<>();
            for (GeneratedFile file : generatedFiles) {
                Path path = pathOf(outputDirectory.getPath(), file.relativePath());
                if (file.getType() != GeneratedFile.Type.APPLICATION && file.getType() != GeneratedFile.Type.APPLICATION_CONFIG) {
                    Files.write(path, file.contents());
                    generatedSourceFiles.add(path.toFile());
                }
            }
            super.compile(generatedSourceFiles, context);
        } catch (Exception e) {
            throw new KogitoCompilerException(e);
        } finally {
            try {
                cl.close();
            } catch (IOException e) {
                throw new RuntimeException( e );
            }
        }
    }

    @Override
    public Path getSourcePath(Path classFilePath, Set<String> sourcePaths, String classesPath) {
        if (classToSource.containsKey(classFilePath)) {
            return classToSource.get(classFilePath);
        }

        return null;
    }

    protected abstract Generator addGenerator(ApplicationGenerator appGen, Set<File> filesToCompile, Context context, ClassLoader cl)
            throws IOException;

    static Path pathOf(String path, String relativePath) {
        Path p = Paths.get(path, relativePath);
        p.getParent().toFile().mkdirs();
        return p;
    }
    
    private boolean hasClassOnClasspath(ClassLoader cl, String className) {
        try {
            cl.loadClass(className);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private URL[] getClasspathUrls( Context context ) {
        Set<File> elements = context.getClasspath();
        URL[] urls = new URL[elements.size()+1];

        try {
            urls[0] = context.getOutputDirectory().toURI().toURL();
            int i = 1;
            for (File artifact : elements) {
                urls[i++] = artifact.toURI().toURL();
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException( e );
        }

        return urls;
    }
}
