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
package org.drools.codegen.common;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public class AppPaths {

    public enum BuildTool {
        MAVEN,
        GRADLE;

        public static AppPaths.BuildTool findBuildTool() {
            return System.getProperty("org.gradle.appname") == null ? MAVEN : GRADLE;
        }
    }

    public static final String TARGET_DIR = "target";

    public static final String SRC_DIR = "src";

    public static final String RESOURCES_DIR = "resources";

    public static final String GENERATED_RESOURCES_DIR = "generated-resources";

    public static final String MAIN_DIR = "main";
    public static final String TEST_DIR = "test";

    private final Set<Path> projectPaths = new LinkedHashSet<>();
    private final Collection<Path> classesPaths = new ArrayList<>();

    private final boolean isJar;
    private final Path outputTarget;

    private final Path[] paths;

    private final Path firstProjectPath;

    private final Path[] resourcePaths;
    private final File[] resourceFiles;

    private final Path[] sourcePaths;

    public static AppPaths fromProjectDir(Path projectDir, Path outputTarget) {
        return new AppPaths(Collections.singleton(projectDir), Collections.emptyList(), false, BuildTool.findBuildTool(), MAIN_DIR, outputTarget);
    }

    /**
     * Builder to be used only for tests, where <b>all</b> resources must be present in "src/test/resources" directory
     *
     * @param projectDir
     * @return
     */
    public static AppPaths fromTestDir(Path projectDir) {
        return new AppPaths(Collections.singleton(projectDir), Collections.emptyList(), false, BuildTool.findBuildTool(), TEST_DIR, Paths.get(projectDir.toString(), TARGET_DIR));
    }

    /**
     * @param projectPaths
     * @param classesPaths
     * @param isJar
     * @param bt
     * @param resourcesBasePath "main" or "test"
     */
    protected AppPaths(Set<Path> projectPaths, Collection<Path> classesPaths, boolean isJar, BuildTool bt,
            String resourcesBasePath, Path outputTarget) {
        this.isJar = isJar;
        this.projectPaths.addAll(projectPaths);
        this.classesPaths.addAll(classesPaths);
        this.outputTarget = outputTarget;
        firstProjectPath = getFirstProjectPath(this.projectPaths, outputTarget, bt);
        resourcePaths = getResourcePaths(this.projectPaths, resourcesBasePath, bt);
        paths = isJar ? getJarPaths(isJar, this.classesPaths) : resourcePaths;
        resourceFiles = getResourceFiles(resourcePaths);
        sourcePaths = getSourcePaths(this.projectPaths);
    }

    public Path[] getPaths() {
        return paths;
    }

    public Path getFirstProjectPath() {
        return firstProjectPath;
    }

    public File[] getResourceFiles() {
        return resourceFiles;
    }

    public Path[] getResourcePaths() {
        return resourcePaths;
    }

    public Path[] getSourcePaths() {
        return sourcePaths;
    }

    public Collection<Path> getClassesPaths() {
        return Collections.unmodifiableCollection(classesPaths);
    }

    public Path getOutputTarget() {
        return outputTarget;
    }

    @Override
    public String toString() {
        return "AppPaths{" +
                "projectPaths=" + projectPaths +
                ", classesPaths=" + classesPaths +
                ", isJar=" + isJar +
                '}';
    }

    static Path getFirstProjectPath(Set<Path> innerProjectPaths, Path innerOutputTarget, BuildTool innerBt) {
        return innerBt == BuildTool.MAVEN
                ? innerProjectPaths.iterator().next()
                : innerOutputTarget;
    }

    static Path[] getJarPaths(boolean isInnerJar, Collection<Path> innerClassesPaths) {
        if (!isInnerJar) {
            throw new IllegalStateException("Not a jar");
        } else {
            return innerClassesPaths.toArray(new Path[0]);
        }
    }

    static Path[] getResourcePaths(Set<Path> innerProjectPaths, String resourcesBasePath, BuildTool innerBt) {
        Path[] toReturn;
        if (innerBt == BuildTool.GRADLE) {
            toReturn = transformPaths(innerProjectPaths, p -> p.resolve(Paths.get("")));
        } else {
            toReturn = transformPaths(innerProjectPaths, p -> p.resolve(Paths.get(SRC_DIR, resourcesBasePath,
                                                                                  RESOURCES_DIR)));
            Path[] generatedResourcesPaths = transformPaths(innerProjectPaths, p -> p.resolve(Paths.get(TARGET_DIR,
                                                                                                        GENERATED_RESOURCES_DIR)));
            Path[] newToReturn = new Path[toReturn.length + generatedResourcesPaths.length];
            System.arraycopy(toReturn, 0, newToReturn, 0, toReturn.length);
            System.arraycopy(generatedResourcesPaths, 0, newToReturn, toReturn.length, generatedResourcesPaths.length);
            toReturn = newToReturn;
        }
        return toReturn;
    }

    static File[] getResourceFiles(Path[] innerResourcePaths) {
        return Arrays.stream(innerResourcePaths).map(Path::toFile).toArray(File[]::new);
    }

    static Path[] getSourcePaths(Set<Path> innerProjectPaths) {
        return transformPaths(innerProjectPaths, p -> p.resolve(SRC_DIR));
    }

    static Path[] transformPaths(Collection<Path> paths, UnaryOperator<Path> f) {
        return paths.stream().map(f).toArray(Path[]::new);
    }

}
