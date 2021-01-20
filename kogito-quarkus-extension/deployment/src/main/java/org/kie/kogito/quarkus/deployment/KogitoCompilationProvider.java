/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import org.kie.kogito.codegen.GeneratedFileType;
import org.kie.kogito.codegen.Generator;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.utils.AppPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class KogitoCompilationProvider extends JavaCompilationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoCompilationProvider.class);
    protected static Map<Path, Path> classToSource = new HashMap<>();

    @Override
    public Set<String> handledSourcePaths() {
        return Collections.singleton("src" + File.separator + "main" + File.separator + "resources");
    }

    @Override
    public final void compile(Set<File> filesToCompile, Context quarkusContext) {
        // This classloader reads from the file system all the project dependencies, plus the quarkus output directory
        // containing all the latest class definitions of user's pojos, eventually recompiled also during the latest
        // hot reload round. It is also necessary to use a null as a parent classloader otherwise this classloader
        // could load the old definition of a class from the parent instead of getting the latest one from the output directory
        final URLClassLoader cl = new URLClassLoader( getClasspathUrls( quarkusContext ), null );

        File outputDirectory = quarkusContext.getOutputDirectory();
        try {
            AppPaths appPaths = AppPaths.fromProjectDir(quarkusContext.getProjectDirectory().toPath());
            KogitoBuildContext context = KogitoQuarkusContextProvider.context(appPaths, cl);

            ApplicationGenerator appGen = new ApplicationGenerator(context);

            appGen.registerGeneratorIfEnabled(getGenerator(context, filesToCompile, quarkusContext));

            Collection<GeneratedFile> generatedFiles = appGen.generate();

            Set<File> generatedSourceFiles = new HashSet<>();
            for (GeneratedFile file : generatedFiles) {
                Path path = pathOf(outputDirectory.getPath(), file.relativePath());
                if (file.type().canHotReload()) {
                    Files.write(path, file.contents());
                    if (file.category().equals(GeneratedFileType.Category.SOURCE)) {
                        generatedSourceFiles.add(path.toFile());
                    }
                }
                else {
                    if(LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Skipping file because cannot hot reload: " + file);
                    }
                }
            }
            super.compile(generatedSourceFiles, quarkusContext);
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

    protected abstract Generator getGenerator(KogitoBuildContext context,
                                              Set<File> filesToCompile,
                                              Context quarkusContext);

    static Path pathOf(String path, String relativePath) {
        Path p = Paths.get(path, relativePath);
        p.getParent().toFile().mkdirs();
        return p;
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
