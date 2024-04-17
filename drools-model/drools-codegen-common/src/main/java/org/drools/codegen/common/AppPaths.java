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
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

public class AppPaths {

    public enum BuildTool {
        MAVEN("target",
              Path.of("target","generated-sources"),
              Path.of("target","generated-resources"),
              Path.of("target","generated-test-resources"),
              Path.of("target","classes"),
              Path.of("target","test-classes")),
        GRADLE("build",
               Path.of("build", "generated", "sources"),
               Path.of("build","generated", "resources"),
               Path.of("build","generated", "test", "resources"),
               Path.of("build","classes", "java", "main"),
               Path.of("build","classes", "java", "test"));

        public final String OUTPUT_DIRECTORY;
        public final Path GENERATED_SOURCES_PATH;
        public final Path GENERATED_RESOURCES_PATH;
        public final Path GENERATED_TEST_RESOURCES_PATH;
        public final Path CLASSES_PATH;
        public final Path TEST_CLASSES_PATH;

        BuildTool(String outputDirectory, Path generatedSourcesPath, Path generatedResourcesPath, Path generatedTestResourcesPath, Path classesPath, Path testClassesPath) {
            this.OUTPUT_DIRECTORY = outputDirectory;
            this.GENERATED_SOURCES_PATH = generatedSourcesPath;
            this.GENERATED_RESOURCES_PATH = generatedResourcesPath;
            this.GENERATED_TEST_RESOURCES_PATH = generatedTestResourcesPath;
            this.CLASSES_PATH = classesPath;
            this.TEST_CLASSES_PATH = testClassesPath;
        }

        public static AppPaths.BuildTool findBuildTool() {
            return System.getProperty("org.gradle.appname") == null ? MAVEN : GRADLE;
        }
    }

    public static final String TARGET_DIR;
    public static final String GENERATED_SOURCES_DIR;
    public static final String GENERATED_RESOURCES_DIR;
    public static final BuildTool BT;

    static {
        BT = BuildTool.findBuildTool();
        TARGET_DIR = BT.OUTPUT_DIRECTORY;
        GENERATED_SOURCES_DIR = BT.GENERATED_SOURCES_PATH.toString();
        GENERATED_RESOURCES_DIR = BT.GENERATED_RESOURCES_PATH.toString();
    }

    public static final String SRC_DIR = "src";

    public static final String RESOURCES_DIR = "resources";

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

    public static AppPaths fromProjectDir(Path projectDir) {
        return fromProjectDir(projectDir, BT);
    }

    /**
     * Builder to be used only for tests, where <b>all</b> resources must be present in "src/test/resources" directory
     *
     * @param projectDir
     * @return
     */
    public static AppPaths fromTestDir(Path projectDir) {
        return fromTestDir(projectDir, BT);
    }

    /**
     * Default-access method for testing purpose
     * @param projectDir
     * @param bt
     * @return
     */
    static AppPaths fromProjectDir(Path projectDir, BuildTool bt) {
        return new AppPaths(Collections.singletonList(projectDir), Collections.emptyList(), false, bt, MAIN_DIR, false);
    }

    /**
     * Default-access method for testing purpose
     * @param projectDir
     * @param bt
     * @return
     */
    static AppPaths fromTestDir(Path projectDir, BuildTool bt) {
        return new AppPaths(Collections.singletonList(projectDir), Collections.emptyList(), false, bt, TEST_DIR, true);
    }

    /**
     * @param projectPaths
     * @param classesPaths
     * @param isJar
     * @param bt
     * @param resourcesBasePath "main" or "test"
     */
    protected AppPaths(List<Path> projectPaths, Collection<Path> classesPaths, boolean isJar, BuildTool bt,
                       String resourcesBasePath, boolean isTest) {
        this.isJar = isJar;
        this.projectPaths.addAll(projectPaths);
        this.classesPaths.addAll(classesPaths);
        this.outputTarget = Paths.get(".", bt.OUTPUT_DIRECTORY);
        firstProjectPath = projectPaths.get(0);
        resourcePaths = getResourcePaths(this.projectPaths, resourcesBasePath, bt, isTest);
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

    static Path[] getJarPaths(boolean isInnerJar, Collection<Path> innerClassesPaths) {
        if (!isInnerJar) {
            throw new IllegalStateException("Not a jar");
        } else {
            return innerClassesPaths.toArray(new Path[0]);
        }
    }

    static Path[] getResourcePaths(Set<Path> innerProjectPaths, String resourcesBasePath, BuildTool innerBt, boolean isTest) {
        Path[] resourcesPaths = transformPaths(innerProjectPaths, p -> p.resolve(Paths.get(SRC_DIR, resourcesBasePath,
                                                                              RESOURCES_DIR)));
        Path generatedResourcesPath = isTest ? innerBt.GENERATED_TEST_RESOURCES_PATH : innerBt.GENERATED_RESOURCES_PATH;
        Path[] generatedResourcesPaths = transformPaths(innerProjectPaths, p -> p.resolve(generatedResourcesPath));
        Path[] toReturn = new Path[resourcesPaths.length + generatedResourcesPaths.length];
        System.arraycopy(resourcesPaths, 0, toReturn, 0, resourcesPaths.length);
        System.arraycopy(generatedResourcesPaths, 0, toReturn, resourcesPaths.length, generatedResourcesPaths.length);
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
