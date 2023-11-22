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

    private final Set<Path> projectPaths = new LinkedHashSet<>();
    private final Collection<Path> classesPaths = new ArrayList<>();

    private final boolean isJar;
    private final BuildTool bt;
    private final Path resourcesPath;
    private final Path outputTarget;

    public static AppPaths fromProjectDir(Path projectDir, Path outputTarget) {
        return new AppPaths(Collections.singleton(projectDir), Collections.emptyList(), false, BuildTool.findBuildTool(), "main", outputTarget);
    }

    /**
     * Builder to be used only for tests, where <b>all</b> resources must be present in "src/test/resources" directory
     *
     * @param projectDir
     * @return
     */
    public static AppPaths fromTestDir(Path projectDir) {
        return new AppPaths(Collections.singleton(projectDir), Collections.emptyList(), false, BuildTool.findBuildTool(), "test", Paths.get(projectDir.toString(), TARGET_DIR));
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
        this.bt = bt;
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
        return bt == BuildTool.MAVEN
               ? projectPaths.iterator().next()
               : outputTarget;
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
