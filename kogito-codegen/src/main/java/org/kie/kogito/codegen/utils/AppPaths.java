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

package org.kie.kogito.codegen.utils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public class AppPaths {

    private final Set<Path> projectPaths = new LinkedHashSet<>();
    private final Collection<Path> classesPaths = new ArrayList<>();

    private final boolean isJar;

    public static AppPaths fromProjectDir(Path projectDir) {
        return new AppPaths(Collections.singleton(projectDir), Collections.emptyList(), false);
    }

    public static AppPaths fromQuarkus(Iterable<Path> paths) {
        Set<Path> projectPaths = new LinkedHashSet<>();
        Collection<Path> classesPaths = new ArrayList<>();
        boolean isJar = false;
        for (Path path : paths) {
            PathType pathType = getPathType(path);
            switch (pathType) {
                case CLASSES:
                    classesPaths.add(path);
                    projectPaths.add(path.getParent().getParent());
                    break;
                case TEST_CLASSES:
                    projectPaths.add(path.getParent().getParent());
                    break;
                case JAR:
                    isJar = true;
                    classesPaths.add(path);
                    projectPaths.add(path.getParent().getParent());
                    break;
                case UNKNOWN:
                    classesPaths.add(path);
                    projectPaths.add(path);
                    break;
            }
        }
        return new AppPaths(projectPaths, classesPaths, isJar);
    }

    private static PathType getPathType(Path archiveLocation) {
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

    private AppPaths(Set<Path> projectPaths, Collection<Path> classesPaths, boolean isJar) {
        this.isJar = isJar;
        this.projectPaths.addAll(projectPaths);
        this.classesPaths.addAll(classesPaths);
    }

    public Path[] getPaths() {
        if (isJar) {
            return getJarPaths();
        } else {
            return getResourcePaths();
        }
    }

    public Path getFirstProjectPath() {
        return projectPaths.iterator().next();
    }

    public Path getFirstClassesPath() {
        return classesPaths.iterator().next();
    }

    private Path[] getJarPaths() {
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

    public Collection<Path> getProjectPaths() {
        return Collections.unmodifiableCollection(projectPaths);
    }

    public Collection<Path> getClassesPaths() {
        return Collections.unmodifiableCollection(classesPaths);
    }

    private Path[] transformPaths(Collection<Path> paths, UnaryOperator<Path> f) {
        return paths.stream().map(f).toArray(Path[]::new);
    }
}