/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.scenariosimulation.backend.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.drools.scenariosimulation.backend.runner.ScenarioException;

/**
 * Utility that provide classPath scan to retrieve resources
 */
public class ResourceHelper {

    private ResourceHelper() {
    }

    static String[] getClassPathElements() {
        return System.getProperty("java.class.path", ".").split(System.getProperty("path.separator"));
    }

    /**
     * Scan into classpath folders to find resources with the required extension
     * @param extension to find
     * @return stream of matching resources
     */
    public static Stream<String> getResourcesByExtension(String extension) {
        return Arrays.stream(getClassPathElements())
                .flatMap(elem -> internalGetResources(elem, Pattern.compile(".*\\." + extension + "$")));
    }

    /**
     * This method is internal because it works only with folder to explore (classPath folder) and not with exact paths
     * @param path to folder or jar
     * @param pattern to find
     * @return stream of matching resources
     */
    static Stream<String> internalGetResources(String path, Pattern pattern) {
        final File file = new File(path);
        if (!file.isDirectory()) {
            return Stream.empty();
        }
        return getResourcesFromDirectory(file, pattern);
    }

    /**
     * Scan folder to find resources that match with pattern
     * @param directory where to start the search
     * @param pattern to find
     * @return stream of matching resources
     */
    public static Stream<String> getResourcesFromDirectory(File directory, Pattern pattern) {
        if (directory == null || directory.listFiles() == null) {
            return Stream.empty();
        }
        return Arrays.stream(directory.listFiles()).flatMap(elem -> {
            if (elem.isDirectory()) {
                return getResourcesFromDirectory(elem, pattern);
            } else {
                try {
                    String fileName = elem.getCanonicalPath();
                    if (pattern.matcher(fileName).matches()) {
                        return Stream.of(fileName);
                    }
                } catch (final IOException e) {
                    throw new ScenarioException("Impossible to access to resources", e);
                }
            }
            return Stream.empty();
        });
    }
}
