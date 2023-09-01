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
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility that provide classPath scan to retrieve resources
 */
public class ResourceHelper {

    /**
     * Scan into classpath folders to find resources with the required extension
     * @param extension to find
     * @return stream of matching resources
     */
    public static Collection<File> getFileResourcesByExtension(String extension) {
        return Arrays.stream(getClassPathElements())
                .flatMap(elem -> internalGetFileResources(elem, Pattern.compile(".*\\." + extension + "$"))
                        .stream())
                .collect(Collectors.toSet());
    }

    /**
     * Scan into classpath folders to find resources with the required extension
     * @param extension to find
     * @return stream of matching resources
     */
    public static Collection<String> getResourcesByExtension(String extension) {
        return Arrays.stream(getClassPathElements())
                .flatMap(elem -> internalGetResources(elem, Pattern.compile(".*\\." + extension + "$"))
                        .stream())
                .collect(Collectors.toSet());
    }

    /**
     * Scan folder to find resources that match with pattern
     * @param directory where to start the search
     * @param pattern to find
     * @return stream of matching resources
     */
    public static Collection<File> getFileResourcesFromDirectory(File directory, Pattern pattern) {
        if (directory == null || directory.listFiles() == null) {
            return Collections.emptySet();
        }
        return Arrays.stream(Objects.requireNonNull(directory.listFiles()))
                .flatMap(
                        elem -> {
                            if (elem.isDirectory()) {
                                return getFileResourcesFromDirectory(elem, pattern).stream();
                            } else {
                                try {
                                    if (pattern.matcher(elem.getCanonicalPath()).matches()) {
                                        return Stream.of(elem);
                                    }
                                } catch (final IOException e) {
                                    throw new RuntimeException("Failed to retrieve resources from directory " + directory.getAbsolutePath() + " with pattern " + pattern.pattern(), e);
                                }
                            }
                            return Stream.empty();
                        })
                .collect(Collectors.toSet());
    }

    /**
     * Scan folder to find resources that match with pattern
     * @param directory where to start the search
     * @param pattern to find
     * @return stream of matching resources
     */
    public static Collection<String> getResourcesFromDirectory(File directory, Pattern pattern) {
        if (directory == null || directory.listFiles() == null) {
            return Collections.emptySet();
        }
        return Arrays.stream(Objects.requireNonNull(directory.listFiles()))
                .flatMap(elem -> {
                    if (elem.isDirectory()) {
                        return getResourcesFromDirectory(elem, pattern)
                                .stream();
                    } else {
                        try {
                            String fileName = elem.getCanonicalPath();
                            if (pattern.matcher(fileName).matches()) {
                                return Stream.of(fileName);
                            } else {
                                return Stream.empty();
                            }
                        } catch (final IOException e) {
                            throw new RuntimeException("Impossible to access to resources", e);
                        }
                    }
                })
                .collect(Collectors.toSet());
    }

    static String[] getClassPathElements() {
        return System.getProperty("java.class.path", ".").split(System.getProperty("path.separator"));
    }

    /**
     * This method is internal because it works only with folder to explore (classPath folder) and not with exact paths
     * @param path to folder or jar
     * @param pattern to find
     * @return stream of matching resources
     */
    static Collection<File> internalGetFileResources(String path, Pattern pattern) {
        final File file = new File(path);
        if (!file.isDirectory()) {
            return Collections.emptySet();
        }
        return getFileResourcesFromDirectory(file, pattern);
    }

    /**
     * This method is internal because it works only with folder to explore (classPath folder) and not with exact paths
     * @param path to folder or jar
     * @param pattern to find
     * @return stream of matching resources
     */
    static Collection<String> internalGetResources(String path, Pattern pattern) {
        final File file = new File(path);
        if (!file.isDirectory()) {
            return Collections.emptySet();
        }
        return getResourcesFromDirectory(file, pattern);
    }

    private ResourceHelper() {
        // Avoid instantiating class
    }
}
