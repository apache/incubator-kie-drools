/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.kie.kogito.Model;
import org.kie.kogito.UserTask;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.GeneratedFileType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.json.JsonSchemaGenerator;
import org.kie.kogito.codegen.process.persistence.PersistenceGenerator;
import org.kie.kogito.codegen.process.persistence.proto.ReflectionProtoGenerator;
import org.kie.kogito.process.ProcessInstancesFactory;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerFactory;
import org.kie.memorycompiler.JavaCompilerSettings;
import org.kie.memorycompiler.JavaConfiguration;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import static java.util.Arrays.asList;
import static org.kie.kogito.codegen.core.utils.GeneratedFileValidation.validateGeneratedFileTypes;

@Mojo(name = "process-model-classes",
        requiresDependencyResolution = ResolutionScope.RUNTIME,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        threadSafe = true)
public class ProcessClassesMojo extends AbstractKieMojo {

    private static final JavaCompiler JAVA_COMPILER = JavaCompilerFactory.loadCompiler(JavaConfiguration.CompilerType.NATIVE, "1.8");

    @Parameter(property = "kogito.jsonSchema.version", required = false)
    private String schemaVersion;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            JavaCompilerSettings settings = new JavaCompilerSettings();
            List<URL> pathUrls = new ArrayList<>();
            for (String path : project.getRuntimeClasspathElements()) {
                File pathFile = new File(path);
                pathUrls.add(pathFile.toURI().toURL());
                settings.addClasspath(pathFile);
            }

            URL[] urlsForClassLoader = pathUrls.toArray(new URL[pathUrls.size()]);

            // need to define parent classloader which knows all dependencies of the plugin
            try (URLClassLoader cl = new URLClassLoader(urlsForClassLoader, Thread.currentThread().getContextClassLoader())) {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.addUrls(cl.getURLs());
                builder.addClassLoader(cl);

                Reflections reflections = new Reflections(builder);
                @SuppressWarnings({ "rawtype", "unchecked" })
                Set<Class<?>> modelClasses = (Set) reflections.getSubTypesOf(Model.class);

                // collect constructor parameters so the generated class can create constructor with injection
                Class<?> persistenceClass = reflections.getSubTypesOf(ProcessInstancesFactory.class)
                        .stream()
                        .filter(c -> !c.isInterface())
                        .findFirst()
                        .orElse(null);

                ReflectionProtoGenerator protoGenerator = ReflectionProtoGenerator.builder()
                        .withPersistenceClass(persistenceClass)
                        .build(modelClasses);

                KogitoBuildContext context = discoverKogitoRuntimeContext(cl);

                // Generate persistence files
                PersistenceGenerator persistenceGenerator = new PersistenceGenerator(context, protoGenerator);
                Collection<GeneratedFile> persistenceFiles = persistenceGenerator.generate();

                validateGeneratedFileTypes(persistenceFiles, asList(GeneratedFileType.Category.SOURCE, GeneratedFileType.Category.RESOURCE));

                Collection<GeneratedFile> generatedClasses = persistenceFiles.stream().filter(x -> x.category().equals(GeneratedFileType.Category.SOURCE)).collect(Collectors.toList());
                Collection<GeneratedFile> generatedResources = persistenceFiles.stream().filter(x -> x.category().equals(GeneratedFileType.Category.RESOURCE)).collect(Collectors.toList());

                // Compile and write persistence files
                compileAndWriteClasses(generatedClasses, cl, settings);

                // Dump resources
                generatedResources.forEach(this::writeGeneratedFile);

                // Json schema generation
                Stream<Class<?>> classStream = reflections.getTypesAnnotatedWith(UserTask.class).stream();
                generateJsonSchema(classStream).forEach(this::writeGeneratedFile);
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Error during processing model classes", e);
        }
    }

    private void compileAndWriteClasses(Collection<GeneratedFile> generatedClasses, ClassLoader cl, JavaCompilerSettings settings) throws MojoFailureException {
        MemoryFileSystem srcMfs = new MemoryFileSystem();
        MemoryFileSystem trgMfs = new MemoryFileSystem();

        String[] sources = new String[generatedClasses.size()];
        int index = 0;
        for (GeneratedFile entry : generatedClasses) {
            String fileName = entry.relativePath();
            sources[index++] = fileName;
            srcMfs.write(fileName, entry.contents());
        }

        if (sources.length > 0) {

            CompilationResult result = JAVA_COMPILER.compile(sources, srcMfs, trgMfs, cl, settings);
            if (result.getErrors().length > 0) {
                throw new MojoFailureException(Arrays.toString(result.getErrors()));
            }

            for (String fileName : trgMfs.getFileNames()) {
                byte[] data = trgMfs.getBytes(fileName);
                writeGeneratedFile(new GeneratedFile(GeneratedFileType.COMPILED_CLASS, fileName, data));
            }
        }
    }

    private Collection<GeneratedFile> generateJsonSchema(Stream<Class<?>> classes) throws IOException {
        return new JsonSchemaGenerator.ClassBuilder(classes)
                .withGenSchemaPredicate(x -> true)
                .withSchemaVersion(schemaVersion).build()
                .generate();
    }
}
