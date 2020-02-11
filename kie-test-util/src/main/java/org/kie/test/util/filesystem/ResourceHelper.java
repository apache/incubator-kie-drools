/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.test.util.filesystem;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility that provide classPath scan to retrieve resources
 */
public class ResourceHelper {

    private static final Logger logger = LoggerFactory.getLogger(ResourceHelper.class);

    /**
     * Scan into classpath folders to find resources with the required extension
     * @param extension to find
     * @return stream of matching resources
     */
    public static Stream<File> getResourcesByExtension(String extension) {
        return Arrays.stream(getClassPathElements())
                .flatMap(elem -> internalGetResources(elem, Pattern.compile(".*\\." + extension + "$")));
    }

    /**
     * Scan folder to find resources that match with pattern
     * @param directory where to start the search
     * @param pattern to find
     * @return stream of matching resources
     * @throws IOException
     */
    public static Stream<File> getResourcesFromDirectory(File directory, Pattern pattern) {
        if (directory == null || directory.listFiles() == null) {
            return Stream.empty();
        }
        return Arrays.stream(Objects.requireNonNull(directory.listFiles())).flatMap(
                elem -> {
                    if (elem.isDirectory()) {
                        return getResourcesFromDirectory(elem, pattern);
                    } else {
                        try {
                            if (pattern.matcher(elem.getCanonicalPath()).matches()) {
                                return Stream.of(elem);
                            }
                        } catch (final IOException e) {
                            logger.error("Failed top retrieve resources from directory " + directory.getAbsolutePath() + " with pattern " + pattern.pattern(), e);
                        }
                    }
                    return Stream.empty();
                });
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
    static Stream<File> internalGetResources(String path, Pattern pattern) {
        final File file = new File(path);
        if (!file.isDirectory()) {
            return Stream.empty();
        }
        return getResourcesFromDirectory(file, pattern);
    }

    private ResourceHelper() {
        // Avoid instantiating class
    }
}
