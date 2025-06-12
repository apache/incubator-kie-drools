/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.codegen.common;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import static org.drools.util.Config.getConfig;

/**
 * Writes {@link GeneratedFile} to the right directory, depending on its
 * {@link GeneratedFileType.Category}
 */
public class GeneratedFileWriter {

    /**
     *
     * @param finalPath e.g. "drools" or "kogito"
     * @param resourcesDirectoryProperty e.g. "drools.codegen.resources.directory" or "kogito.codegen.resources.directory"
     * @param sourcesDirectoryProperty e.g. "drools.codegen.sources.directory" or "kogito.codegen.sources.directory"
     * @return
     */
    public static Builder builder(String finalPath, String resourcesDirectoryProperty, String sourcesDirectoryProperty ) {
        return builder(finalPath,
                       resourcesDirectoryProperty,
        sourcesDirectoryProperty,
                       AppPaths.BT);
    }

    /**
     * Default-access for testing purpose
     * @param finalPath
     * @param resourcesDirectoryProperty
     * @param sourcesDirectoryProperty
     * @param buildTool
     * @return
     */
    static Builder builder(String finalPath, String resourcesDirectoryProperty, String sourcesDirectoryProperty, AppPaths.BuildTool buildTool) {
        // using runtime BT instead to allow usage of
        // Springboot from GRADLE
        String targetClasses = buildTool.CLASSES_PATH.toString();

        String generatedResourcesSourcesKogito = Path.of(buildTool.GENERATED_RESOURCES_PATH.toString(), finalPath).toString();
        String generatedSourcesKogito = Path.of(buildTool.GENERATED_SOURCES_PATH.toString(), finalPath).toString();
        return new Builder(targetClasses,
                           getConfig(resourcesDirectoryProperty, generatedResourcesSourcesKogito),
                           getConfig(sourcesDirectoryProperty, generatedSourcesKogito));
    }

    public static class Builder {
        //Default-access for testing purpose
        final String classesDir;
        final String resourcesDir;
        final String sourcesDir;

        /**
         *
         * @param classesDir usually target/classes/
         * @param resourcesDir usually target/generated-resources/kogito/
         * @param sourcesDir usually target/generated-sources/kogito/
         */
        private Builder(String classesDir, String resourcesDir, String sourcesDir) {
            this.classesDir = classesDir;
            this.resourcesDir = resourcesDir;
            this.sourcesDir = sourcesDir;
        }

        /**
         * @param basePath the path to which the given subdirectories will be written
         *        e.g. ${basePath}/${classesDir}/myfile.ext
         *
         */
        public GeneratedFileWriter build(Path basePath) {
            return new GeneratedFileWriter(
                    basePath.resolve(classesDir),
                    basePath.resolve(resourcesDir),
                    basePath.resolve(sourcesDir));
        }
    }

    private final Path classesDir;
    private final Path resourcesDir;
    private final Path sourcesDir;
    /**
     *
     * @param classesDir usually target/classes/
     * @param resourcesDir usually target/generated-resources/kogito/
     * @param sourcesDir usually target/generated-sources/kogito/
     */
    //Default-access for testing purpose
    GeneratedFileWriter(Path classesDir, Path resourcesDir, Path sourcesDir) {
        this.classesDir = classesDir;
        this.resourcesDir = resourcesDir;
        this.sourcesDir = sourcesDir;
    }

    public void writeAll(Collection<GeneratedFile> generatedFiles) {
        generatedFiles.forEach(this::write);
    }

    public void write(GeneratedFile f) throws UncheckedIOException {
        try {
            GeneratedFileType.Category category = f.category();
            switch (category) {
                case INTERNAL_RESOURCE, // since codegen happens after maven-resource-plugin (both in Quarkus and SB), need to manually place in the correct (CP) location
                     STATIC_HTTP_RESOURCE,
                     COMPILED_CLASS:
                    writeGeneratedFile(f, classesDir);
                    break;
                case SOURCE:
                    writeGeneratedFile(f, sourcesDir);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Category " + category.name());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Path getClassesDir() {
        return classesDir;
    }

    public Path getResourcesDir() {
        return resourcesDir;
    }

    public Path getSourcesDir() {
        return sourcesDir;
    }

    void writeGeneratedFile(GeneratedFile f, Path location) throws IOException {
        if (location == null) {
            return;
        }
        Files.write(
                pathOf(location, f.path()),
                f.contents());
    }

    private Path pathOf(Path location, Path end) throws IOException {
        Path path = location.resolve(end);
        Files.createDirectories(path.getParent());
        return path;
    }
}
