/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.JsonSchemaGenerator;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerFactory;
import org.kie.memorycompiler.JavaCompilerSettings;
import org.kie.memorycompiler.JavaConfiguration;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.kie.kogito.Model;
import org.kie.kogito.UserTask;
import org.kie.kogito.codegen.process.persistence.PersistenceGenerator;
import org.kie.kogito.codegen.process.persistence.proto.ReflectionProtoGenerator;
import org.kie.kogito.process.ProcessInstancesFactory;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import static java.util.Arrays.asList;
import static org.kie.kogito.codegen.GeneratedFile.Type.CLASS;
import static org.kie.kogito.codegen.GeneratedFile.Type.GENERATED_CP_RESOURCE;
import static org.kie.kogito.codegen.utils.GeneratedFileValidation.validateGeneratedFileTypes;

@Mojo(name = "process-model-classes",
      requiresDependencyResolution = ResolutionScope.RUNTIME,
      requiresProject = true,
      defaultPhase = LifecyclePhase.PROCESS_CLASSES,
      threadSafe = true)
public class ProcessClassesMojo extends AbstractKieMojo {
        
    private static final JavaCompiler JAVA_COMPILER = JavaCompilerFactory.loadCompiler( JavaConfiguration.CompilerType.NATIVE, "1.8");

    @Parameter(property = "kogito.jsonSchema.version", required=false)
    private String schemaVersion;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            JavaCompilerSettings settings = new JavaCompilerSettings();
            List<URL> pathUrls = new ArrayList<>();
            for(String path: project.getRuntimeClasspathElements()) {
                pathUrls.add(new File(path).toURI().toURL());
                settings.addClasspath(path);
            }

            URL[] urlsForClassLoader = pathUrls.toArray(new URL[pathUrls.size()]);

            // need to define parent classloader which knows all dependencies of the plugin
            try (URLClassLoader cl = new URLClassLoader(urlsForClassLoader, Thread.currentThread().getContextClassLoader())) {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.addUrls(cl.getURLs());
                builder.addClassLoader(cl);

                Reflections reflections = new Reflections(builder);
                Set<Class<? extends Model>> modelClasses = reflections.getSubTypesOf(Model.class);

                String appPackageName = project.getGroupId();

                // safe guard to not generate application classes that would clash with interfaces
                if (appPackageName.equals(ApplicationGenerator.DEFAULT_GROUP_ID)) {
                    appPackageName = KogitoBuildContext.DEFAULT_PACKAGE_NAME;
                }
                // collect constructor parameters so the generated class can create constructor with injection
                List<String> parameters = new ArrayList<>();
                Set<Class<? extends ProcessInstancesFactory>> classes = reflections.getSubTypesOf(ProcessInstancesFactory.class);
                if (!classes.isEmpty()) {

                    Class<? extends ProcessInstancesFactory> c = classes.iterator().next();
                    for (Type t : c.getConstructors()[0].getGenericParameterTypes()) {
                        parameters.add(t.getTypeName());
                    }
                }

                AddonsConfig addonsConfig = loadAddonsConfig(false, project);

                KogitoBuildContext.Builder contextBuilder = discoverKogitoRuntimeContext(project)
                        .withApplicationProperties(kieSourcesDirectory)
                        .withPackageName(appPackageName)
                        .withAddonsConfig(addonsConfig)
                        .withTargetDirectory(targetDirectory);

                KogitoBuildContext context = contextBuilder.build();

                String persistenceType = context.getApplicationProperty("kogito.persistence.type").orElse(PersistenceGenerator.DEFAULT_PERSISTENCE_TYPE);

                // Generate persistence files
                PersistenceGenerator persistenceGenerator = new PersistenceGenerator(context, modelClasses, new ReflectionProtoGenerator(), cl, parameters, persistenceType);
                Collection<GeneratedFile> persistenceFiles = persistenceGenerator.generate();

                validateGeneratedFileTypes(persistenceFiles, asList(CLASS, GENERATED_CP_RESOURCE));

                Collection<GeneratedFile> generatedClasses = persistenceFiles.stream().filter(x -> x.getType().equals(CLASS)).collect(Collectors.toList());
                Collection<GeneratedFile> generatedResources = persistenceFiles.stream().filter(x -> x.getType().equals(GENERATED_CP_RESOURCE)).collect(Collectors.toList());

                // Compile and write persistence files
                compileAndWriteClasses(generatedClasses, cl, settings);

                // Dump resources
                generatedResources.forEach(this::writeGeneratedFile);

                // Json schema generation
                generateJsonSchema(reflections)
                        // FIXME workaround until KOGITO-2901 is solved: all generated resource that needs to be available
                        //  at runtime has to be GENERATED_CP_RESOURCE (so that are saved in the proper folder)
                        .stream()
                        .map(gf -> new GeneratedFile(GENERATED_CP_RESOURCE, gf.relativePath(), gf.contents()))
                        .forEach(this::writeGeneratedFile);

            }
        } catch (Exception e) {
            throw new MojoExecutionException("Error during processing model classes", e);
        }
    }

    private void compileAndWriteClasses(Collection<GeneratedFile> generatedClasses, ClassLoader cl, JavaCompilerSettings settings) throws MojoFailureException, IOException {
        MemoryFileSystem srcMfs = new MemoryFileSystem();
        MemoryFileSystem trgMfs = new MemoryFileSystem();

        String[] sources = new String[generatedClasses.size()];
        int index = 0;
        for (GeneratedFile entry : generatedClasses) {
            String fileName = entry.relativePath();
            sources[index++] = fileName;
            srcMfs.write(fileName, entry.contents());
        }

        Path path = Paths.get(project.getBuild().getOutputDirectory());

        if (sources.length > 0) {

            CompilationResult result = JAVA_COMPILER.compile(sources, srcMfs, trgMfs, cl, settings);
            if (result.getErrors().length > 0) {
                throw new MojoFailureException(Arrays.toString(result.getErrors()));
            }

            for (String fileName : trgMfs.getFileNames()) {
                byte[] data = trgMfs.getBytes(fileName);
                writeCompiledFile(path, fileName, data);
            }
        }
    }

    private Collection<GeneratedFile> generateJsonSchema(Reflections reflections) throws IOException {
        return new JsonSchemaGenerator.ClassBuilder(reflections.getTypesAnnotatedWith(UserTask.class).stream())
                .withGenSchemaPredicate(x -> true)
                .withSchemaVersion(schemaVersion).build()
                .generate();
    }

    private Path writeCompiledFile(Path parentPath, String fileName, byte[] data) throws IOException {
        Path path = parentPath.resolve(fileName);
        if (!path.getParent().toFile().exists()) {
            Files.createDirectories(path.getParent());
        }
        Files.write(path, data);
        return path;
    }

}