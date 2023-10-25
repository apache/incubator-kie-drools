/**
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
package org.drools.quarkus.util.deployment;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;

public class GeneratedFileWriter {

    public static class Builder {

        private final String classesDir;
        private final String sourcesDir;
        private final String resourcePath;
        private final String scaffoldedSourcesDir;

        /**
         *
         * @param classesDir usually target/classes/
         * @param sourcesDir usually target/generated-sources/kogito/
         * @param resourcesDir usually target/generated-resources/kogito/
         * @param scaffoldedSourcesDir usually src/main/java/
         */
        public Builder(String classesDir, String sourcesDir, String resourcesDir, String scaffoldedSourcesDir) {
            this.classesDir = classesDir;
            this.sourcesDir = sourcesDir;
            this.resourcePath = resourcesDir;
            this.scaffoldedSourcesDir = scaffoldedSourcesDir;
        }

        /**
         * @param basePath the path to which the given subdirectories will be written
         *        e.g. ${basePath}/${classesDir}/myfile.ext
         *
         */
        public GeneratedFileWriter build(Path basePath) {
            return new GeneratedFileWriter(
                    basePath.resolve(classesDir),
                    basePath.resolve(sourcesDir),
                    basePath.resolve(resourcePath),
                    basePath.resolve(scaffoldedSourcesDir));
        }
    }

    private final Path classesDir;
    private final Path sourcesDir;
    private final Path resourcePath;
    private final Path scaffoldedSourcesDir;

    public static final String DEFAULT_SOURCES_DIR = "generated-sources/kogito/";
    public static final String DEFAULT_RESOURCE_PATH = "generated-resources/kogito/";
    public static final String DEFAULT_SCAFFOLDED_SOURCES_DIR = "src/main/java/";
    public static final String DEFAULT_CLASSES_DIR = "target/classes";

    /**
     *
     * @param classesDir usually {@link #DEFAULT_CLASSES_DIR}
     * @param sourcesDir usually target/generated-sources/kogito/. See {@link #DEFAULT_SOURCES_DIR}
     * @param resourcePath usually target/generated-resources/kogito/ {@link #DEFAULT_RESOURCE_PATH}
     * @param scaffoldedSourcesDir usually {@link #DEFAULT_SCAFFOLDED_SOURCES_DIR}
     */
    public GeneratedFileWriter(Path classesDir, Path sourcesDir, Path resourcePath, Path scaffoldedSourcesDir) {
        this.classesDir = classesDir;
        this.sourcesDir = sourcesDir;
        this.resourcePath = resourcePath;
        this.scaffoldedSourcesDir = scaffoldedSourcesDir;
    }

    public void writeAll(Collection<GeneratedFile> generatedFiles) {
        generatedFiles.forEach(this::write);
    }

    public void write(GeneratedFile f) throws UncheckedIOException {
        try {
            GeneratedFileType.Category category = f.category();
            switch (category) {
                case INTERNAL_RESOURCE: // since codegen happens after maven-resource-plugin (both in Quarkus and SB), need to manually place in the correct (CP) location
                case STATIC_HTTP_RESOURCE:
                case COMPILED_CLASS:
                    writeGeneratedFile(f, classesDir);
                    break;
                case SOURCE:
                    if (f.type().isCustomizable()) {
                        writeGeneratedFile(f, scaffoldedSourcesDir);
                    } else {
                        writeGeneratedFile(f, sourcesDir);
                    }
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

    public Path getSourcesDir() {
        return sourcesDir;
    }

    public Path getResourcePath() {
        return resourcePath;
    }

    public Path getScaffoldedSourcesDir() {
        return scaffoldedSourcesDir;
    }

    private void writeGeneratedFile(GeneratedFile f, Path location) throws IOException {
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
