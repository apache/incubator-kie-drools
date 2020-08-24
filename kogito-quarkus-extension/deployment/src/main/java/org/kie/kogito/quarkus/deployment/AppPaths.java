/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import io.quarkus.bootstrap.model.PathsCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AppPaths {

    private static final Logger logger = LoggerFactory.getLogger(AppPaths.class);

    final Set<Path> projectPaths = new LinkedHashSet<>();
    final List<Path> classesPaths = new ArrayList<>();

    boolean isJar = false;

    AppPaths(PathsCollection paths) {
        for (Path path : paths) {
            PathType pathType = getPathType(path);
            switch (pathType) {
                case CLASSES: {
                    classesPaths.add(path);
                    projectPaths.add(path.getParent().getParent());
                    break;
                }
                case TEST_CLASSES: {
                    projectPaths.add(path.getParent().getParent());
                    break;
                }
                case JAR: {
                    isJar = true;
                    classesPaths.add(path);
                    projectPaths.add(path.getParent().getParent());
                    break;
                }
                case UNKNOWN: {
                    classesPaths.add(path);
                    projectPaths.add(path);
                    break;
                }
            }
        }

    }

    public Path[] getPath() {
        if (isJar) {
            return getJarPath();
        } else {
            return getResourcePaths();
        }
    }

    public Path getFirstProjectPath() {
        return projectPaths.iterator().next();
    }

    public Path getFirstClassesPath() {
        return classesPaths.get(0);
    }

    public Path[] getJarPath() {
        if (!isJar) {
            throw new IllegalStateException("Not a jar");
        }
        return classesPaths.toArray(new Path[classesPaths.size()]);
    }

    public File[] getResourceFiles() {
        return projectPaths.stream().map(p -> p.resolve("src/main/resources").toFile()).toArray(File[]::new);
    }

    public Path[] getResourcePaths() {
        return transformPaths(projectPaths, p -> p.resolve("src/main/resources"));
    }

    public Path[] getSourcePaths() {
        return transformPaths(projectPaths, p -> p.resolve("src"));
    }

    public Path[] getProjectPaths() {
        return transformPaths(projectPaths, Function.identity());
    }

    private Path[] transformPaths(Collection<Path> paths, Function<Path, Path> f) {
        return paths.stream().map(f).toArray(Path[]::new);
    }

    private PathType getPathType(Path archiveLocation) {
        String path = archiveLocation.toString();
        if (path.endsWith("target" + File.separator + "classes")) {
            return PathType.CLASSES;
        }
        if (path.endsWith("target" + File.separator + "test-classes")) {
            return PathType.TEST_CLASSES;
        }
        // Quarkus generates a file with extension .jar.original when doing a native compilation of a uberjar
        // TODO replace ".jar.original" with constant JarResultBuildStep.RENAMED_JAR_EXTENSION when it will be avialable in Quakrus 1.7
        if (path.endsWith(".jar") || path.endsWith(".jar.original")) {
            return PathType.JAR;
        }
        return PathType.UNKNOWN;
    }

    private enum PathType {
        CLASSES,
        TEST_CLASSES,
        JAR,
        UNKNOWN
    }
}