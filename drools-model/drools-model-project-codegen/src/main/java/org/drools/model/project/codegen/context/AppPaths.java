/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.model.project.codegen.context;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public class AppPaths {

    public enum BuildTool {
        MAVEN,
        GRADLE
    }

    public static final String TARGET_DIR = "target";

    private final Set<Path> projectPaths = new LinkedHashSet<>();
    private final Collection<Path> classesPaths = new ArrayList<>();

    private final boolean isJar;
    private final Path resourcesPath;
    private final Path outputTarget;

    public static AppPaths fromProjectDir(Path projectDir, Path outputTarget) {
        return new AppPaths(Collections.singleton(projectDir), Collections.emptyList(), false, BuildTool.MAVEN, "main", outputTarget);
    }

    public static AppPaths fromQuarkus(Path outputTarget, Iterable<Path> paths, BuildTool bt) {
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
        return new AppPaths(projectPaths, classesPaths, isJar, bt, "main", outputTarget);
    }

    /**
     * Builder to be used only for tests, where <b>all</b> resources must be present in "src/test/resources" directory
     *
     * @param projectDir
     * @return
     */
    public static AppPaths fromTestDir(Path projectDir) {
        return new AppPaths(Collections.singleton(projectDir), Collections.emptyList(), false, BuildTool.MAVEN, "test", Paths.get(projectDir.toString(), TARGET_DIR));
    }

    private static PathType getPathType(Path archiveLocation) {
        String path = archiveLocation.toString();
        if (path.endsWith(TARGET_DIR + File.separator + "classes")) {
            return PathType.CLASSES;
        }
        if (path.endsWith(TARGET_DIR + File.separator + "test-classes")) {
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

    /**
     * @param projectPaths
     * @param classesPaths
     * @param isJar
     * @param bt
     * @param resourcesBasePath "main" or "test"
     */
    private AppPaths(Set<Path> projectPaths, Collection<Path> classesPaths, boolean isJar, BuildTool bt,
                     String resourcesBasePath, Path outputTarget) {
        this.isJar = isJar;
        this.projectPaths.addAll(projectPaths);
        this.classesPaths.addAll(classesPaths);
        this.outputTarget = outputTarget;
        if (bt == BuildTool.GRADLE) {
            resourcesPath = Paths.get(""); // no prefix required
        } else {
            resourcesPath = Paths.get("src", resourcesBasePath, "resources");
        }
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

    private Path[] getJarPaths() {
        if (!isJar) {
            throw new IllegalStateException("Not a jar");
        }
        return classesPaths.toArray(new Path[classesPaths.size()]);
    }

    public File[] getResourceFiles() {
        return projectPaths.stream().map(p -> p.resolve(resourcesPath).toFile()).toArray(File[]::new);
    }

    public Path[] getResourcePaths() {
        return transformPaths(projectPaths, p -> p.resolve(resourcesPath));
    }

    public Path[] getSourcePaths() {
        return transformPaths(projectPaths, p -> p.resolve("src"));
    }

    public Collection<Path> getClassesPaths() {
        return Collections.unmodifiableCollection(classesPaths);
    }

    public Path getOutputTarget() {
        return outputTarget;
    }

    private Path[] transformPaths(Collection<Path> paths, UnaryOperator<Path> f) {
        return paths.stream().map(f).toArray(Path[]::new);
    }

    @Override
    public String toString() {
        return "AppPaths{" +
                "projectPaths=" + projectPaths +
                ", classesPaths=" + classesPaths +
                ", isJar=" + isJar +
                '}';
    }
}
