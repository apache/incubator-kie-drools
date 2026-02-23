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
 *
 */
package org.kie.kogito.codegen.manager;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class responsible for managing and writing generated files to specific locations.
 * This class provides methods to handle the dumping of generated files while
 * leveraging an internally configured {@code GeneratedFileWriter} instance.
 * The {@code GeneratedFileManager} is designed to be used as a static utility and
 * therefore cannot be instantiated.
 */
public class GeneratedFileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratedFileManager.class);

    private static final GeneratedFileWriter.Builder GENERATED_FILE_WRITER_BUILDER =
            GeneratedFileWriter.builder("kogito", "kogito.codegen.resources.directory", "kogito.codegen.sources.directory");

    private GeneratedFileManager() {
        // Not instantiable - static methods only.
    }

    /**
     * Dumps a collection of generated files to the specified base path using a configured {@code GeneratedFileWriter}.
     * If the provided collection of generated files is null or empty, no action is taken.
     *
     * @param generatedFiles the collection of {@code GeneratedFile} objects to be written; may be null or empty.
     * @param basePath the base directory {@code Path} where the files will be written; must not be null.
     * @throws NullPointerException if {@code basePath} is null.
     */
    public static void dumpGeneratedFiles(Collection<GeneratedFile> generatedFiles, Path basePath) {
        Objects.requireNonNull(basePath, "basePath must not be null");

        if (generatedFiles == null || generatedFiles.isEmpty()) {
            LOGGER.debug("No generated files to write (0 items).");
            return;
        }

        GeneratedFileWriter writer = GENERATED_FILE_WRITER_BUILDER.build(basePath);
        generatedFiles.forEach(generatedFile -> writeGeneratedFile(generatedFile, writer));
    }

    /**
     * Writes a single {@code GeneratedFile} to its target destination using the specified {@code GeneratedFileWriter}.
     * The method logs the file path if logging at the INFO level is enabled.
     *
     * @param generatedFile the {@code GeneratedFile} to be written; must not be null
     * @param writer the {@code GeneratedFileWriter} responsible for handling the file write operation; must not be null
     */
    static void writeGeneratedFile(GeneratedFile generatedFile, GeneratedFileWriter writer) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Writing file: {}", generatedFile.path());
        }
        writer.write(generatedFile);
    }

    /**
     * Deletes all files with the specified extension in a given directory
     *
     * @param directory the directory to search for files
     * @param extension the file extension to match (e.g., "drl", "dmn", "java")
     */
    public static void deleteFilesByExtension(Path directory, String extension) {
        Objects.requireNonNull(directory, "directory must not be null");
        Objects.requireNonNull(extension, "extension must not be null");

        if (extension.isBlank()) {
            throw new IllegalArgumentException("extension must not be blank");
        }

        if (!Files.exists(directory)) {
            throw new IllegalArgumentException("directory does not exist: " + directory);
        }

        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("path is not a directory: " + directory);
        }

        final String normalizedExtension = extension.startsWith(".") ? extension : "." + extension;

        LOGGER.debug("Deleting files with extension '{}' in directory: {}", normalizedExtension, directory);

        try (final Stream<Path> files = Files.find(directory,
                Integer.MAX_VALUE,
                (path, attributes) -> attributes.isRegularFile() &&
                        path.getFileName().toString().toLowerCase().endsWith(normalizedExtension.toLowerCase()))) {
            files.forEach(path -> {
                try {
                    Files.delete(path);
                    LOGGER.debug("Deleted file: {}", path);
                } catch (IOException e) {
                    throw new UncheckedIOException("Failed to delete: " + path, e);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Error during " + normalizedExtension + " files deletion in: " + directory, e);
        }
    }

}
