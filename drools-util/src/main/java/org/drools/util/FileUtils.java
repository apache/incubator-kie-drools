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
package org.drools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Utility to access files
 */
public class FileUtils {

    private FileUtils() {
        // Avoid instantiating class
    }

    /**
     * Retrieve the <code>File</code> of the given <b>file</b>
     * This method does not guarantee the returned file if multiple files, with same name, are present in different directories
     * @param fileName
     * @return
     */
    public static File getFile(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        File toReturn = ResourceHelper.getFileResourcesByExtension(extension)
                .stream()
                .filter(file -> file.getName().equals(fileName))
                .findFirst()
                .orElse(null);
        if (toReturn == null) {
            throw new IllegalArgumentException("Failed to find file " + fileName);
        }
        return toReturn;
    }

    /**
     * Retrieve the <code>File</code> of the given <b>file</b>
     * @param fileName
     * @param parentDir
     * @return
     */
    public static File getFile(String fileName, String parentDir) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        File parentDirectory = new File(parentDir);
        if (!parentDirectory.exists() || !parentDirectory.canRead() || !parentDirectory.isDirectory()) {
            throw new IllegalArgumentException("Failed to find parent directory " + parentDir);
        }
        File toReturn = ResourceHelper.getFileResourcesByExtension(extension)
                .stream()
                .filter(file -> file.getName().equals(fileName) &&
                        file.getParentFile() != null &&
                        file.getParentFile().getAbsolutePath().equals(parentDirectory.getAbsolutePath()))
                .findFirst()
                .orElse(null);
        if (toReturn == null) {
            throw new IllegalArgumentException("Failed to find file " + fileName);
        }
        return toReturn;
    }

    /**
     * Retrieve the <code>FileInputStream</code> of the given <b>file</b>
     * @param fileName
     * @return
     * @throws IOException
     */
    public static FileInputStream getFileInputStream(String fileName) throws IOException {
        File sourceFile = getFile(fileName);
        return new FileInputStream(sourceFile);
    }

    /**
     * Retrieve the <code>FileInputStream</code> of the given <b>file</b>
     * @param fileName
     * @param parentDir
     * @return
     * @throws IOException
     */
    public static FileInputStream getFileInputStream(String fileName, String parentDir) throws IOException {
        File sourceFile = getFile(fileName, parentDir);
        return new FileInputStream(sourceFile);
    }

    /**
     * Retrieve the <b>content</b> of the given <b>file</b>
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String getFileContent(String fileName) throws IOException {
        File file = getFile(fileName);
        Path path = file.toPath();
        Stream<String> lines = Files.lines(path);
        String toReturn = lines.collect(Collectors.joining("\n"));
        lines.close();
        return toReturn;
    }

    /**
     * Retrieve the <b>content</b> of the given <b>file</b>
     * @param fileName
     * @param parentDir
     * @return
     * @throws IOException
     */
    public static String getFileContent(String fileName, String parentDir) throws IOException {
        File file = getFile(fileName, parentDir);
        Path path = file.toPath();
        Stream<String> lines = Files.lines(path);
        String toReturn = lines.collect(Collectors.joining("\n"));
        lines.close();
        return toReturn;
    }

    /**
     * @param fileName
     * @param classLoader
     * @return
     *
     */
    public static Optional<InputStream> getInputStreamFromFileNameAndClassLoader(String fileName, ClassLoader classLoader) {
        return Optional.ofNullable(classLoader.getResourceAsStream(fileName));
    }

    /**
     * delete a directory and all its content
     * @param path path to the directory to delete
     */
    public static void deleteDirectory(Path path) {
        try {
            if (Files.exists(path)) {
                try (Stream<Path> walk = Files.walk(path)) {
                    walk.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
